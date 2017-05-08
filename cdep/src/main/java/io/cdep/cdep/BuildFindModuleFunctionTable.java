/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package io.cdep.cdep;

import static io.cdep.cdep.ast.finder.ExpressionBuilder.abort;
import static io.cdep.cdep.ast.finder.ExpressionBuilder.archive;
import static io.cdep.cdep.ast.finder.ExpressionBuilder.arrayHasOnlyElement;
import static io.cdep.cdep.ast.finder.ExpressionBuilder.assign;
import static io.cdep.cdep.ast.finder.ExpressionBuilder.constant;
import static io.cdep.cdep.ast.finder.ExpressionBuilder.eq;
import static io.cdep.cdep.ast.finder.ExpressionBuilder.getFileName;
import static io.cdep.cdep.ast.finder.ExpressionBuilder.gte;
import static io.cdep.cdep.ast.finder.ExpressionBuilder.ifSwitch;
import static io.cdep.cdep.ast.finder.ExpressionBuilder.joinFileSegments;
import static io.cdep.cdep.ast.finder.ExpressionBuilder.lastIndexOfString;
import static io.cdep.cdep.ast.finder.ExpressionBuilder.module;
import static io.cdep.cdep.ast.finder.ExpressionBuilder.multi;
import static io.cdep.cdep.ast.finder.ExpressionBuilder.nop;
import static io.cdep.cdep.ast.finder.ExpressionBuilder.stringStartsWith;
import static io.cdep.cdep.ast.finder.ExpressionBuilder.substring;
import static io.cdep.cdep.utils.Invariant.fail;
import static io.cdep.cdep.utils.Invariant.failIf;
import static io.cdep.cdep.utils.Invariant.require;
import static io.cdep.cdep.utils.StringUtils.safeFormat;

import io.cdep.annotations.NotNull;
import io.cdep.annotations.Nullable;
import io.cdep.cdep.ast.finder.AbortExpression;
import io.cdep.cdep.ast.finder.AssignmentExpression;
import io.cdep.cdep.ast.finder.ExampleExpression;
import io.cdep.cdep.ast.finder.Expression;
import io.cdep.cdep.ast.finder.FindModuleExpression;
import io.cdep.cdep.ast.finder.FunctionTableExpression;
import io.cdep.cdep.ast.finder.GlobalBuildEnvironmentExpression;
import io.cdep.cdep.ast.finder.ModuleArchiveExpression;
import io.cdep.cdep.ast.finder.StatementExpression;
import io.cdep.cdep.resolver.ResolvedManifest;
import io.cdep.cdep.utils.ArrayUtils;
import io.cdep.cdep.utils.CoordinateUtils;
import io.cdep.cdep.utils.StringUtils;
import io.cdep.cdep.yml.cdepmanifest.AndroidABI;
import io.cdep.cdep.yml.cdepmanifest.AndroidArchive;
import io.cdep.cdep.yml.cdepmanifest.Archive;
import io.cdep.cdep.yml.cdepmanifest.CDepManifestYml;
import io.cdep.cdep.yml.cdepmanifest.CxxLanguageFeatures;
import io.cdep.cdep.yml.cdepmanifest.HardNameDependency;
import io.cdep.cdep.yml.cdepmanifest.Interfaces;
import io.cdep.cdep.yml.cdepmanifest.LinuxArchive;
import io.cdep.cdep.yml.cdepmanifest.iOSArchitecture;
import io.cdep.cdep.yml.cdepmanifest.iOSArchive;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("Java8ReplaceMapGet")
public class BuildFindModuleFunctionTable {

  @NotNull
  private final Map<Coordinate, ResolvedManifest> manifests = new HashMap<>();

  public void addManifest(@NotNull ResolvedManifest resolved) {
    manifests.put(resolved.cdepManifestYml.coordinate, resolved);
  }

  @NotNull
  public FunctionTableExpression build() {
    FunctionTableExpression functionTable = new FunctionTableExpression();

    // Build module lookup findFunctions
    for (ResolvedManifest resolved : manifests.values()) {
      functionTable.findFunctions.put(resolved.cdepManifestYml.coordinate, buildFindModule(
          functionTable.globals, resolved));
    }

    // Build examples
    for (ResolvedManifest resolved : manifests.values()) {
      if (resolved.cdepManifestYml.example.isEmpty()) {
        continue;
      }
      functionTable.examples.put(resolved.cdepManifestYml.coordinate, new ExampleExpression(resolved.cdepManifestYml.example));
    }

    // Lift assignments up to the highest correct scope
    functionTable = (FunctionTableExpression) new ReplaceAssignmentWithReference().visit(functionTable);
    functionTable = (FunctionTableExpression) new LiftAssignmentToCommonAncestor().visit(functionTable);

    // Check sanity of the function system
    new CheckReferenceAndDependencyConsistency().visit(functionTable);

    return functionTable;
  }

  @NotNull
  private FindModuleExpression buildFindModule(
      @NotNull GlobalBuildEnvironmentExpression globals,
      @NotNull ResolvedManifest resolved) {

    Map<Expression, Expression> cases = new HashMap<>();
    Set<Coordinate> dependencies = new HashSet<>();
    CDepManifestYml manifest = resolved.cdepManifestYml;
    for (HardNameDependency dependency : manifest.dependencies) {
      Coordinate coordinate = CoordinateUtils.tryParse(dependency.compile);
      if (coordinate != null) {
        dependencies.add(coordinate);
      } else {
        fail("Could not parse main manifest coordinate [%s]", dependency.compile);
      }
    }

    Coordinate coordinate = manifest.coordinate;
    AssignmentExpression coordinateGroupId = assign("coordinate_group_id", constant(coordinate.groupId));
    AssignmentExpression coordinateArtifactId = assign("coordinate_artifact_id", constant(coordinate.artifactId));
    AssignmentExpression coordinateVersion = assign("coordinate_version", constant(coordinate.version.value));
    AssignmentExpression explodedArchiveTail = assign("exploded_archive_tail",
        joinFileSegments(coordinateGroupId, coordinateArtifactId, coordinateVersion));

    // Like, {root}/com.github.jomof/vectorial/1.0.0
    AssignmentExpression explodedArchiveFolder = assign("exploded_archive_folder",
        joinFileSegments(globals.cdepExplodedRoot, explodedArchiveTail));

    List<String> supported = new ArrayList<>();
    boolean headerOnly = true;
    if (manifest.android != null && manifest.android.archives != null) {
      headerOnly = false;
      supported.add("Android");
      cases.put(constant("Android"), buildAndroidStlTypeCase(globals, resolved, explodedArchiveFolder, dependencies));
    }
    if (manifest.iOS != null && manifest.iOS.archives != null) {
      headerOnly = false;
      supported.add("Darwin");
      cases.put(constant("Darwin"), buildDarwinPlatformCase(globals, resolved, explodedArchiveFolder, dependencies));
    }
    if (manifest.linux != null && manifest.linux.archives.length > 0) {
      headerOnly = false;
      supported.add("Linux");
      cases.put(constant("Linux"),
          buildSingleArchiveResolution(resolved, manifest.linux.archives[0], explodedArchiveFolder, dependencies));
    }
    Interfaces interfaces = manifest.interfaces;
    if (headerOnly && interfaces != null && interfaces.headers != null) {
      supported.add("Android");
      supported.add("Darwin");
      supported.add("Linux");
      cases.put(constant("Android"), nop());
      cases.put(constant("Darwin"), nop());
      cases.put(constant("Linux"), nop());
    }

    Expression bool[] = new Expression[cases.size()];
    Expression expressions[] = new Expression[cases.size()];
    int i = 0;
    for (Map.Entry<Expression, Expression> entry : cases.entrySet()) {
      bool[i] = eq(globals.buildSystemTargetSystem, entry.getKey());
      expressions[i] = entry.getValue();
      ++i;
    }

    AbortExpression abort;
    if (supported.size() == 0) {
      abort = abort(String.format("Module '%s' doesn't support any platforms.", coordinate));
    } else {
      abort = abort(String.format("Target platform %%s is not supported by %s. " + "Supported: %s", coordinate,
          StringUtils.joinOn(" ", supported)), globals.buildSystemTargetSystem);
    }
    StatementExpression expression = ifSwitch(bool, expressions, abort);

    if (interfaces != null && interfaces.headers != null) {
      Archive archive = interfaces.headers;
      expression = multi(buildSingleArchiveResolution(resolved, archive, explodedArchiveFolder, dependencies), expression);
    }

    if (interfaces != null && interfaces.headers != null && interfaces.headers.file.length() > 0) {
      return new FindModuleExpression(
          globals,
          coordinate,
          interfaces.headers.file,
          interfaces.headers.include,
          expression);
    }
    return new FindModuleExpression(
        globals,
        coordinate,
        null,
        null,
        expression);
  }

  @NotNull
  private StatementExpression buildSingleArchiveResolution(
      @NotNull ResolvedManifest resolved,
      @NotNull Archive archive,
      @NotNull AssignmentExpression explodedArchiveFolder,
      Set<Coordinate> dependencies) {
    if (archive.file.isEmpty() || archive.sha256.isEmpty() || archive.size == 0L) {
      return abort(String.format("Archive in %s was malformed", resolved.remote));
    }
    StatementExpression moduleArchive = buildArchive(
        resolved.remote,
        archive.file,
        archive.sha256,
        archive.size,
        archive.include,
        ArrayUtils.nullToEmpty(archive.requires, CxxLanguageFeatures.class),
        new String[0],
        explodedArchiveFolder);
    if (moduleArchive instanceof ModuleArchiveExpression) {
      return module((ModuleArchiveExpression) moduleArchive, dependencies);
    }
    return moduleArchive;
  }

  @NotNull
  private Expression buildSingleArchiveResolution(@NotNull ResolvedManifest resolved,
      @NotNull LinuxArchive archive, @NotNull AssignmentExpression explodedArchiveFolder,
      Set<Coordinate> dependencies) {
    if (archive.file.isEmpty() || archive.sha256.isEmpty() || archive.size == 0) {
      return abort(String.format("Archive in %s was malformed", resolved.remote));
    }
    Expression moduleArchive = buildArchive(
        resolved.remote,
        archive.file,
        archive.sha256,
        archive.size,
        archive.include,
        new CxxLanguageFeatures[0],
        ArrayUtils.nullToEmpty(archive.libs, String.class),
        explodedArchiveFolder);
    if (moduleArchive instanceof ModuleArchiveExpression) {
      return module((ModuleArchiveExpression) moduleArchive, dependencies);
    }
    return moduleArchive;
  }

  @NotNull
  private Expression buildSingleArchiveResolution(@NotNull ResolvedManifest resolved,
      @NotNull iOSArchive archive, @NotNull AssignmentExpression explodedArchiveFolder,
      Set<Coordinate> dependencies) {
    if (archive.file.isEmpty() || archive.sha256.isEmpty() || archive.size == 0L) {
      return abort(String.format("Archive in %s was malformed", resolved.remote));
    }
    Expression moduleArchive = buildArchive(
        resolved.remote,
        archive.file,
        archive.sha256,
        archive.size,
        archive.include,
        new CxxLanguageFeatures[0],
        ArrayUtils.nullToEmpty(archive.libs, String.class),
        explodedArchiveFolder);
    if (moduleArchive instanceof ModuleArchiveExpression) {
      return module((ModuleArchiveExpression) moduleArchive, dependencies);
    }
    return moduleArchive;
  }

  @NotNull
  private Expression buildSingleArchiveResolution(@NotNull ResolvedManifest resolved,
      @NotNull AndroidArchive archive,
      @NotNull AndroidABI abi,
      @NotNull AssignmentExpression explodedArchiveFolder,
      @NotNull Set<Coordinate> dependencies) {
    if (archive.file.isEmpty() || archive.sha256.isEmpty() || archive.size == 0L) {
      return abort(String.format("Archive in %s was malformed", resolved.remote));
    }
    require(abi.name.length() > 0);

    String abiLibs[] = new String[archive.libs.length];
    for (int i = 0; i < abiLibs.length; ++i) {
      abiLibs[i] = abi + "/" + archive.libs[i];
    }
    Expression moduleArchive = buildArchive(
        resolved.remote,
        archive.file,
        archive.sha256,
        archive.size,
        archive.include,
        new CxxLanguageFeatures[0],
        ArrayUtils.nullToEmpty(abiLibs, String.class),
        explodedArchiveFolder);
    if (moduleArchive instanceof ModuleArchiveExpression) {
      return module((ModuleArchiveExpression) moduleArchive, dependencies);
    }
    return moduleArchive;
  }

  @NotNull
  private StatementExpression buildArchive(
      @NotNull URL remote,
      @NotNull String file,
      @NotNull String sha256,
      @NotNull Long size,
      @Nullable String include,
      @NotNull CxxLanguageFeatures[] requires,
      @NotNull String libs[],
      @NotNull AssignmentExpression explodedArchiveFolder) {
    String libLibs[] = new String[libs.length];
    Expression libPaths[] = new Expression[libs.length];
    for (int i = 0; i < libs.length; ++i) {
      libLibs[i] = "lib/" + libs[i];
      libPaths[i] = joinFileSegments(explodedArchiveFolder, file, "lib", libs[i]);
    }

    try {
      return archive(
          remote.toURI().resolve(".").resolve(file).toURL(),
          sha256,
          size,
          include,
          include == null ? null : joinFileSegments(explodedArchiveFolder, file, include),
          libLibs,
          libPaths,
          requires);
    } catch (IllegalArgumentException e) {
      return abort("Archive file could not be converted to URL. It is likely an illegal path.");
    } catch (MalformedURLException e) {
      return abort("Archive file could not be converted to URL. It may have an unknown protocol.");
    } catch (URISyntaxException e) {
      return abort("Archive file could not be converted to URL. It may have an invalid syntax.");
    }
  }

  @NotNull
  private Expression buildDarwinPlatformCase(
      @NotNull GlobalBuildEnvironmentExpression globals,
      @NotNull ResolvedManifest resolved,
      @NotNull AssignmentExpression explodedArchiveFolder,
      @NotNull Set<Coordinate> dependencies) {

    // Something like iPhone10.2.sdk or iPhone.sdk
    AssignmentExpression osxSysrootSDKName = assign("osx_sysroot_sdk_name", getFileName(globals.cmakeOsxSysroot));

    // The position of the right-most dot
    AssignmentExpression lastDotPosition = assign("last_dot_position", lastIndexOfString(osxSysrootSDKName, "."));

    // Something like iPhone10.2 or iPhone
    AssignmentExpression combinedPlatformAndSDK = assign("combined_platform_and_sdk",
        substring(osxSysrootSDKName, constant(0), lastDotPosition));

    assert resolved.cdepManifestYml.iOS != null;
    iOSArchive[] archives = resolved.cdepManifestYml.iOS.archives;
    if (archives == null) {
      archives = new iOSArchive[0];
    }
    return buildiosArchitectureSwitch(globals, resolved, archives, explodedArchiveFolder, combinedPlatformAndSDK, dependencies);
  }

  @NotNull
  private Expression buildiosArchitectureSwitch(
      @NotNull GlobalBuildEnvironmentExpression globals,
      @NotNull ResolvedManifest resolved,
      @NotNull iOSArchive archive[],
      @NotNull AssignmentExpression explodedArchiveFolder,
      @NotNull AssignmentExpression combinedPlatformAndSDK,
      Set<Coordinate> dependencies) {
    Map<iOSArchitecture, List<iOSArchive>> grouped = groupByArchitecture(archive);
    List<Expression> conditions = new ArrayList<>();
    List<Expression> expressions = new ArrayList<>();
    String supported = "";
    if (grouped.size() == 1) {
      return buildiOSPlatformSdkSwitch(resolved,
          grouped.values().iterator().next(),
          explodedArchiveFolder,
          combinedPlatformAndSDK,
          null,
          dependencies);
    }
    for (iOSArchitecture architecture : grouped.keySet()) {
      if (failIf(architecture == null, "iOS architecture in manifest was unknown or missing")) {
        conditions.add(constant(false));
      } else {
        assert architecture != null;
        conditions.add(arrayHasOnlyElement(globals.cmakeOsxArchitectures, constant(architecture.toString())));
        supported += " " + architecture.toString();
      }
      expressions.add(buildiOSPlatformSdkSwitch(resolved,
          grouped.get(architecture),
          explodedArchiveFolder,
          combinedPlatformAndSDK,
          architecture,
          dependencies));

    }
    return ifSwitch(conditions,
        expressions,
        abort(String.format("OSX architecture %%s is not supported by %s. Supported: %s",
            resolved.cdepManifestYml.coordinate,
            supported), globals.cmakeOsxArchitectures));
  }

  @NotNull
  private Expression buildiOSPlatformSdkSwitch(
      @NotNull ResolvedManifest resolved,
      @NotNull List<iOSArchive> archives,
      @NotNull AssignmentExpression explodedArchiveFolder,
      @NotNull AssignmentExpression combinedPlatformAndSDK,
      @Nullable iOSArchitecture architecture,
      @NotNull Set<Coordinate> dependencies) {
    List<Expression> conditionList = new ArrayList<>();
    List<Expression> expressionList = new ArrayList<>();
    String supported = "";

    // Exact matches. For example, path ends with exactly iPhoneOS10.2
    // TODO:  Linter should verify that there is not duplicate exact platforms (ie platform+sdk)
    for (iOSArchive archive : archives) {
      String platformSDK = archive.platform + archive.sdk;
      conditionList.add(eq(combinedPlatformAndSDK, constant(platformSDK)));
      expressionList.add(buildSingleArchiveResolution(resolved, archive, explodedArchiveFolder, dependencies));

      supported += platformSDK + " ";
    }

    assert resolved.cdepManifestYml.iOS != null;
    assert resolved.cdepManifestYml.iOS.archives != null;
    if (resolved.cdepManifestYml.iOS.archives.length == 1) {
      return buildSingleArchiveResolution(resolved, resolved.cdepManifestYml.iOS.archives[0],
          explodedArchiveFolder, dependencies);
    }

    // If there was no exact match then do a startsWith match like, starts  with iPhone*
    // TODO: Need to match on the highest SDK version. This matches the first seen.
    for (iOSArchive archive : resolved.cdepManifestYml.iOS.archives) {
      if (failIf(archive.platform == null,
          "iOS platform was missing in some packages and present in others. It needs to be consistent")) {
        continue;
      }
      assert archive.platform != null;
      conditionList.add(stringStartsWith(combinedPlatformAndSDK, constant(archive.platform.toString())));
      expressionList.add(buildSingleArchiveResolution(resolved, archive, explodedArchiveFolder, dependencies));
    }

    Expression notFound;
    if (architecture == null) {
      notFound = abort(String.format("OSX SDK %%s is not supported by %s. " + "Supported: %s",
        resolved.cdepManifestYml.coordinate,
        supported), combinedPlatformAndSDK);
    }  else {
      notFound = abort(String.format("OSX SDK %%s is not supported by %s and architecture %s. " + "Supported: %s",
        resolved.cdepManifestYml.coordinate,
        architecture,
        supported), combinedPlatformAndSDK);
    }

    return ifSwitch(conditionList, expressionList, notFound);
  }

  @NotNull
  private Map<iOSArchitecture, List<iOSArchive>> groupByArchitecture(@NotNull iOSArchive archives[]) {
    Map<iOSArchitecture, List<iOSArchive>> result = new HashMap<>();
    for (iOSArchive archive : archives) {
      List<iOSArchive> list = result.get(archive.architecture);
      if (list == null) {
        list = new ArrayList<>();
        result.put(archive.architecture, list);
      }
      list.add(archive);
    }
    return result;
  }

  @NotNull
  private Expression buildAndroidStlTypeCase(
      @NotNull GlobalBuildEnvironmentExpression globals,
      @NotNull ResolvedManifest resolved,
      @NotNull AssignmentExpression explodedArchiveFolder,
      @NotNull Set<Coordinate> dependencies) {

    // Gather up the runtime names
    Map<String, List<AndroidArchive>> stlTypes = new HashMap<>();
    assert resolved.cdepManifestYml.android != null;
    assert resolved.cdepManifestYml.android.archives != null;
    for (AndroidArchive android : resolved.cdepManifestYml.android.archives) {
      List<AndroidArchive> androids = stlTypes.get(android.runtime);
      if (androids == null) {
        androids = new ArrayList<>();
        stlTypes.put(android.runtime, androids);
      }
      androids.add(android);
    }

    List<AndroidArchive> noRuntimeAndroids = stlTypes.get("");
    if (noRuntimeAndroids != null) {
      require(stlTypes.size() == 1,
          "Runtime is on some android submodules but not other in module '%s'",
          resolved.cdepManifestYml.coordinate);
      // If there are no runtimes, then skip the runtime check. This is likely a
      // header-only module.
      return buildAndroidPlatformExpression(globals, resolved, noRuntimeAndroids, explodedArchiveFolder, dependencies);
    }

    Map<Expression, Expression> cases = new HashMap<>();
    String runtimes = "";
    for (String stlType : stlTypes.keySet()) {
      runtimes += stlType + " ";
      cases.put(constant(stlType + "_shared"),
          buildAndroidPlatformExpression(globals, resolved, stlTypes.get(stlType), explodedArchiveFolder, dependencies));
      cases.put(constant(stlType + "_static"),
          buildAndroidPlatformExpression(globals, resolved, stlTypes.get(stlType), explodedArchiveFolder, dependencies));
    }

    // Pick a default runtime for the case where there final project uses 'none' or 'system'
    if (stlTypes.get("c++") != null) {
      cases.put(globals.buildSystemNoneRuntime, buildAndroidPlatformExpression(globals,
          resolved, stlTypes.get("c++"), explodedArchiveFolder, dependencies));
    } else if (stlTypes.get("gnustl") != null) {
      cases.put(globals.buildSystemNoneRuntime, buildAndroidPlatformExpression(globals,
          resolved, stlTypes.get("gnustl"), explodedArchiveFolder, dependencies));
    } else {
      cases.put(globals.buildSystemNoneRuntime, abort(
          safeFormat("No suitable runtime found to substitute for '%%s' in module %s",
              resolved.cdepManifestYml.coordinate,
              globals.buildSystemNoneRuntime)));
    }



    Expression bool[] = new Expression[cases.size()];
    Expression expressions[] = new Expression[cases.size()];
    int i = 0;
    for (Map.Entry<Expression, Expression> entry : cases.entrySet()) {
      bool[i] = eq(globals.cdepDeterminedAndroidRuntime, entry.getKey());
      expressions[i] = entry.getValue();
      ++i;
    }
    return ifSwitch(bool,
        expressions,
        abort(String.format("Android runtime '%%s' is not supported by %s. Supported: %s",
            resolved.cdepManifestYml.coordinate,
            runtimes), globals.cdepDeterminedAndroidRuntime));
  }

  @NotNull
  private Expression buildAndroidPlatformExpression(
      @NotNull GlobalBuildEnvironmentExpression globals,
      @NotNull ResolvedManifest resolved,
      @NotNull List<AndroidArchive> androids,
      @NotNull AssignmentExpression explodedArchiveFolder,
      //
      // Parent of all .zip folders for this coordinate
      @NotNull Set<Coordinate> dependencies) {

    // If there's only one android left and it doesn't have a platform then this is
    // a header-only module.
    if (androids.size() == 1 && androids.get(0).platform.isEmpty()) {
      return buildAndroidAbiExpression(globals, resolved, androids, explodedArchiveFolder, dependencies);
    }

    Map<Integer, List<AndroidArchive>> grouped = new HashMap<>();
    for (AndroidArchive android : androids) {
      Integer platform;
      try {
        platform = android.platform.isEmpty() ? 0 : Integer.parseInt(android.platform);
      } catch (NumberFormatException e) {
        return abort(String.format(
            "Android platform string in %s manifest could not be converted to an integer",
            resolved.cdepManifestYml.coordinate));

      }
      List<AndroidArchive> group = grouped.get(platform);
      if (group == null) {
        group = new ArrayList<>();
        grouped.put(platform, group);
      }
      group.add(android);
    }

    List<Integer> platforms = new ArrayList<>();
    platforms.addAll(grouped.keySet());
    Collections.sort(platforms);

    List<Expression> conditions = new ArrayList<>();
    List<Expression> expressions = new ArrayList<>();

    for (Integer platform : platforms) {
      if (platform == null) {
        // This is an error condition. We still want to generate a viable table with the right
        // modules. This should probably be a boolean-typed abort.
        conditions.add(0, gte(globals.buildSystemTargetPlatform, 0));
      } else {
        conditions.add(0, gte(globals.buildSystemTargetPlatform, platform));
      }
      expressions.add(0,
          buildAndroidAbiExpression(globals, resolved, grouped.get(platform), explodedArchiveFolder, dependencies));
    }
    return ifSwitch(conditions,
        expressions,
        abort(String.format("Android API level %%s is not supported by %s", resolved.cdepManifestYml.coordinate),
            globals.buildSystemTargetPlatform));
  }

  @NotNull
  private Expression buildAndroidAbiExpression(
      @NotNull GlobalBuildEnvironmentExpression globals,
      @NotNull ResolvedManifest resolved,
      @NotNull List<AndroidArchive> androids,
      @NotNull AssignmentExpression explodedArchiveFolder,
      @NotNull Set<Coordinate> dependencies) {
    CDepManifestYml manifest = resolved.cdepManifestYml;
    Map<Expression, Expression> cases = new HashMap<>();
    String supported = "";

    // Group ABI (ABI may be empty for header-only)
    Map<AndroidABI, List<AndroidArchive>> grouped = new HashMap<>();
    for (AndroidArchive android : androids) {
      AndroidABI abi = android.abi;
      List<AndroidArchive> group = grouped.get(abi);
      if (group == null) {
        group = new ArrayList<>();
        grouped.put(abi, group);
      }
      group.add(android);
    }

    if (grouped.size() == 1 && grouped.containsKey(AndroidABI.EMPTY_ABI)) {
      // Header only case.
      AndroidArchive archive = androids.iterator().next();
      Expression moduleArchive = buildArchive(
          resolved.remote,
          archive.file,
          archive.sha256,
          archive.size,
          archive.include,
          new CxxLanguageFeatures[0],
          new String[0],
          explodedArchiveFolder);
      if (moduleArchive instanceof ModuleArchiveExpression) {
        return module((ModuleArchiveExpression) moduleArchive, dependencies);
      }
      return moduleArchive;
    }

    for (AndroidABI abi : grouped.keySet()) {
      AndroidArchive archive = grouped.get(abi).iterator().next();
      supported += abi + " ";
      cases.put(constant(abi), buildSingleArchiveResolution(resolved, archive, abi,
          explodedArchiveFolder, dependencies));
    }

    Expression prior = abort(String.format("Android ABI %%s is not supported by %s for platform %%s. Supported: %s",
        manifest.coordinate,
        supported),
        globals.cdepDeterminedAndroidAbi,
        globals.buildSystemTargetPlatform);

    Expression bool[] = new Expression[cases.size()];
    Expression expressions[] = new Expression[cases.size()];
    int i = 0;
    for (Map.Entry<Expression, Expression> entry : cases.entrySet()) {
      bool[i] = eq(globals.cdepDeterminedAndroidAbi, entry.getKey());
      expressions[i] = entry.getValue();
      ++i;
    }
    return ifSwitch(bool, expressions, prior);
  }
}
