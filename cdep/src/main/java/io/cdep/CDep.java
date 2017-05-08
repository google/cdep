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
package io.cdep;

import io.cdep.annotations.NotNull;
import io.cdep.annotations.Nullable;
import io.cdep.cdep.CheckLocalFileSystemIntegrity;
import io.cdep.cdep.Coordinate;
import io.cdep.cdep.ast.finder.FunctionTableExpression;
import io.cdep.cdep.fullfill.Fullfill;
import io.cdep.cdep.generator.*;
import io.cdep.cdep.io.IO;
import io.cdep.cdep.resolver.ResolvedManifest;
import io.cdep.cdep.resolver.Resolver;
import io.cdep.cdep.utils.*;
import io.cdep.cdep.yml.cdep.BuildSystem;
import io.cdep.cdep.yml.cdep.CDepYml;
import io.cdep.cdep.yml.cdep.SoftNameDependency;
import io.cdep.cdep.yml.cdepmanifest.CDepManifestYml;
import io.cdep.cdep.yml.cdepmanifest.CxxLanguageFeatures;
import io.cdep.cdep.yml.cdepmanifest.Interfaces;
import io.cdep.cdep.yml.cdepmanifest.MergeCDepManifestYmls;
import org.fusesource.jansi.AnsiConsole;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.cdep.cdep.io.IO.*;
import static io.cdep.cdep.utils.Invariant.*;
import static io.cdep.cdep.yml.cdepmanifest.CDepManifestBuilder.archive;

@SuppressWarnings("unused")
public class CDep {

  final private static String EXAMPLE_COORDINATE = "com.github.jomof:boost:1.0.63-rev24";
  @Nullable
  private final File downloadFolder = null;
  @NotNull
  private File workingFolder = new File(".");
  @Nullable
  private CDepYml config = null;
  @Nullable
  private File configFile = null;

  CDep(@NotNull PrintStream out, @NotNull PrintStream err, boolean ansi) {
    IO.setOut(out);
    IO.setErr(err);
    IO.setAnsi(ansi);
  }

  public static void main(@NotNull String[] args) {
    try {
      new CDep(AnsiConsole.out, AnsiConsole.err, true).go(args, false);
    } catch (Throwable e) {
      e.printStackTrace();
      System.exit(Integer.MIN_VALUE);
    } finally {
      AnsiConsole.out.close();
      AnsiConsole.err.close();
    }
  }

  /**
   * Return the first constant after matching one of the arguments. Argument and strign are removed
   * from the list.
   */

  @NotNull
  static private List<String> eatStringArgument(@NotNull String shortArgument,
      @NotNull String longArgument,
      @NotNull List<String> args) {

    boolean takeNext = false;
    List<String> result = new ArrayList<>();
    for (int i = 0; i < args.size(); ++i) {
      if (takeNext) {
        result.add(args.get(i));
        takeNext = false;
        args.set(i, null);
      } else if (args.get(i).equals(longArgument) || args.get(i).equals(shortArgument)) {
        takeNext = true;
        args.set(i, null);
      }
    }
    args.removeAll(Collections.<String>singleton(null));
    return result;
  }

  void go(@NotNull String[] argArray, boolean showFirstExceptionStack)
      throws IOException, URISyntaxException, NoSuchAlgorithmException {
    Invariant.pushScope();
    List<RuntimeException> errors;

    try {
      goNoScope(argArray, showFirstExceptionStack);
    } finally {
      errors = Invariant.popScope();
    }

    if (errors != null && errors.size() > 0) {
      if (showFirstExceptionStack) {
        // All errors will have been printed. Throw the first exception
        throw errors.get(0);
      }
      infoln("%s errors, exiting with code -1.", errors.size());
      System.exit(-1);
    }
  }

  private void goNoScope(@NotNull String[] argArray, boolean showFirstExceptionStack)
      throws IOException, URISyntaxException, NoSuchAlgorithmException {
    List<String> args = new ArrayList<>();
    Collections.addAll(args, argArray);

    if (!handleHelp(args)) {
      return;
    }
    if (!handleVersion(args)) {
      return;
    }
    handleWorkingFolder(args);
    handleDownloadFolder(args);
    if (handleWrapper(args)) {
      return;
    }
    if (handleStartupInfo(args)) {
      return;
    }
    if (handleShow(args)) {
      return;
    }
    if (handleLint(args)) {
      return;
    }
    if (handleMerge(args)) {
      return;
    }
    if (handleFetch(args)) {
      return;
    }
    if (handleFetchArchive(args)) {
      return;
    }
    if (handleFullfill(args)) {
      return;
    }
    if (!handleReadCDepYml()) {
      return;
    }
    if (handleCreate(args)) {
      return;
    }
    if (handleRedownload(args)) {
      return;
    }

    handleGenerateScript();
  }

  private void runBuilders(@NotNull GeneratorEnvironment environment,
      @NotNull FunctionTableExpression table) throws IOException {
    if (config == null || config.builders == null) {
      return;
    }
    for (BuildSystem buildSystem : config.builders) {
      switch (buildSystem.name) {
        case BuildSystem.CMAKE:
          new CMakeGenerator(environment, table).generate();
          break;
        case BuildSystem.CMAKE_EXAMPLES:
          new CMakeExamplesGenerator(environment).generate(table);
          break;
        case BuildSystem.NDK_BUILD:
          new NdkBuildGenerator(environment).generate(table);
          break;
        default:
          errorln("Unknown CDep builder '%s'", buildSystem);
          break;
      }
    }
  }

  private boolean handleRedownload(@NotNull List<String> args)
      throws IOException, NoSuchAlgorithmException {
    if (args.size() > 0 && "redownload".equals(args.get(0))) {
      GeneratorEnvironment environment = getGeneratorEnvironment(true, false);
      FunctionTableExpression table = getFunctionTableExpression(environment);

      // Download and unzip archives.
      GeneratorEnvironmentUtils.downloadReferencedModules(environment,
          ExpressionUtils.getAllFoundModuleExpressions(table));

      // Check that the expected files were downloaded
      new CheckLocalFileSystemIntegrity(environment.unzippedArchivesFolder).visit(table);

      runBuilders(environment, table);
      return true;
    }
    return false;
  }

  private boolean handleLint(@NotNull List<String> args)
      throws IOException, NoSuchAlgorithmException {
    if (args.size() > 0 && "lint".equals(args.get(0))) {
      if (args.size() > 1) {
        GeneratorEnvironment environment = getGeneratorEnvironment(false, false);

        SoftNameDependency dependencies[] = new SoftNameDependency[args.size() - 1];
        for (int i = 1; i < args.size(); ++i) {
          dependencies[i - 1] = new SoftNameDependency(args.get(i));
        }

        GeneratorEnvironmentUtils.getFunctionTableExpression(environment, dependencies);

        // Check that the expected files were downloaded
        //new StubCheckLocalFileSystemIntegrity(environment.unzippedArchivesFolder).visit(table);
        return true;
      } else {
        info("Usage: cdep lint (coordinate or path/to/cdep-manifest.yml)'\n");
        return true;
      }
    }
    return false;
  }

  private boolean handleCreate(@NotNull List<String> args)
      throws IOException, NoSuchAlgorithmException {
    if (args.size() > 0 && "create".equals(args.get(0))) {
      if (args.size() > 1 && "hashes".equals(args.get(1))) {
        GeneratorEnvironment environment = getGeneratorEnvironment(false, false);
        getFunctionTableExpression(environment);
        environment.writeCDepSHA256File();
        info("Created cdep.sha256\n");
        return true;
      }
      info("Usage: cdep create hashes'\n");
      return true;
    }
    return false;
  }

  private boolean handleStartupInfo(@NotNull List<String> args) {
    if (args.size() > 0 && "startup-info".equals(args.get(0))) {
      String jvmLocation = API.getJvmLocation();
      infoln("%s", jvmLocation);
      RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
      List<String> jvmArgs = bean.getInputArguments();

      for (String jvmArg : jvmArgs) {
        infoln(jvmArg);
      }
      infoln("-classpath " + System.getProperty("java.class.path"));
      // print the non-JVM command line arguments
      // print name of the main class with its arguments, like org.ClassName param1 param2
      infoln(System.getProperty("sun.java.command"));
      return true;
    }
    return false;
  }

  private boolean handleMerge(@NotNull List<String> args)
      throws IOException, NoSuchAlgorithmException {
    if (args.size() > 0 && "merge".equals(args.get(0))) {
      if (args.size() < 4) {
        info("Usage: cdep merge coordinate1 coordinate2 ... outputmanifest.yml");
        return true;
      }

      if ("headers".equals(args.get(1))) {
        handleMergeHeaders(args);
        return true;
      }
      File output = new File(args.get(args.size() - 1));
      if (output.exists()) {
        throw new RuntimeException(
            String.format("File %s already exists", output.getAbsolutePath()));
      }

      GeneratorEnvironment environment = getGeneratorEnvironment(false, true);

      CDepManifestYml merged = null;
      for (int i = 1; i < args.size() - 1; ++i) {
        SoftNameDependency name = new SoftNameDependency(args.get(i));
        ResolvedManifest resolved = new Resolver(environment).resolveAny(name);
        if (resolved == null) {
          info("Manifest for '%s' didn't exist. Aborting merge.\n", args.get(i));
          return true;
        } else if (merged == null) {
          merged = resolved.cdepManifestYml;
        } else {
          merged = MergeCDepManifestYmls.merge(merged, resolved.cdepManifestYml);
        }
      }
      require(merged != null, "No manifests to merge");

      // Check the merge for sanity
      assert merged != null;
      CDepManifestYmlUtils.checkManifestSanity(merged);

      // Write the merged manifest out
      String body = CDepManifestYmlUtils.convertManifestToString(merged);
      FileUtils.writeTextToFile(output, body);
      info("Merged %s manifests into %s.\n", args.size() - 2, output);
      return true;
    }
    return false;
  }

  private void handleMergeHeaders(@NotNull List<String> args)
      throws IOException, NoSuchAlgorithmException {
    if (args.size() != 6) {
      info("Usage: cdep merge headers coordinate headers.zip folder outputmanifest.yml");
      return;
    }
    String coordinate = args.get(2);
    File zip = new File(args.get(3));
    String include = args.get(4);
    if (!zip.isFile()) {
      throw new RuntimeException(
          String.format("File %s already doesn't exist or isn't a file", zip.getAbsolutePath()));
    }
    File output = new File(args.get(5));
    GeneratorEnvironment environment = getGeneratorEnvironment(false, true);
    SoftNameDependency name = new SoftNameDependency(coordinate);
    ResolvedManifest resolved = new Resolver(environment).resolveAny(name);

    String sha256 = HashUtils.getSHA256OfFile(zip);
    long size = zip.length();
    assert resolved != null;
    CDepManifestYml prior = resolved.cdepManifestYml;
    CDepManifestYml updated = new CDepManifestYml(
        prior.sourceVersion,
        prior.coordinate,
        prior.dependencies,
        prior.license,
        new Interfaces(archive(zip.getName(), sha256, size, include, new CxxLanguageFeatures[0])),
        prior.android,
        prior.iOS,
        prior.linux,
        prior.example);
    String body = CDepManifestYmlUtils.convertManifestToString(updated);
    FileUtils.writeTextToFile(output, body);
    info("Merged %s and %s into %s.\n", coordinate, zip, output);
  }

  private boolean handleShow(@NotNull List<String> args)
      throws IOException, NoSuchAlgorithmException, URISyntaxException {
    if (args.size() > 0 && "show".equals(args.get(0))) {
      if (args.size() > 1 && "folders".equals(args.get(1))) {
        GeneratorEnvironment environment = getGeneratorEnvironment(false, false);
        info("Downloads: %s\n", environment.downloadFolder.getAbsolutePath());
        info("Exploded: %s\n", environment.unzippedArchivesFolder.getAbsolutePath());
        info("Modules: %s\n", environment.modulesFolder.getAbsolutePath());
        return true;
      }
      if (args.size() > 1 && "local".equals(args.get(1))) {
        GeneratorEnvironment environment = getGeneratorEnvironment(false, false);
        if (args.size() == 2) {
          info("Usage: cdep show local %s\n", EXAMPLE_COORDINATE);
          return true;
        }
        SoftNameDependency dependency = new SoftNameDependency(args.get(2));
        Resolver resolver = new Resolver(environment);
        ResolvedManifest resolved = resolver.resolveAny(dependency);
        if (resolved == null) {
          info("Could not resolve manifest coordinate %s\n", args.get(2));
          return true;
        }

        File local = environment
            .getLocalDownloadFilename(resolved.cdepManifestYml.coordinate, resolved.remote);
        infoln(local.getCanonicalFile());
        return true;
      }
      if (args.size() > 1 && "manifest".equals(args.get(1))) {
        handleReadCDepYml();
        assert config != null;
        info(config.toString());
        return true;
      }
      if (args.size() > 1 && "include".equals(args.get(1))) {
        if (args.size() == 2) {
          info("Usage: show include {coordinate}/n");
          return true;
        }
        // Redirect output so that only the include folder is printed (so that it can be redirected in shells)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream alternOut = new PrintStream(baos);
        PrintStream original = IO.setOut(alternOut);
        for (int i = 2; i < args.size(); ++i) {
          GeneratorEnvironment environment = getGeneratorEnvironment(false, true);
          File include = EnvironmentUtils.getPackageLevelIncludeFolder(environment, args.get(i));
          IO.setOut(original);
          info("%s\n", include);
        }
        return true;
      }
      info("Usage: cdep show [folders|local|manifest|include]'\n");
      return true;
    }
    return false;
  }

  private boolean handleWrapper(@NotNull List<String> args) throws IOException {
    if (args.size() > 0 && "wrapper".equals(args.get(0))) {
      String appname = System.getProperty("io.cdep.appname");
      if (appname == null) {
        throw new RuntimeException(
            "Must set java system proeperty io.cdep.appname to the path of cdep.bat");
      }
      File applicationBase = new File(appname).getParentFile();
      if (applicationBase == null || !applicationBase.isDirectory()) {
        fail("Could not find folder for io.cdep.appname='%s'", appname);
      }
      info("Installing cdep wrapper from %s\n", applicationBase);
      File cdepBatFrom = new File(applicationBase, "cdep.bat");
      File cdepBatTo = new File(workingFolder, "cdep.bat");
      File cdepFrom = new File(applicationBase, "cdep");
      File cdepTo = new File(workingFolder, "cdep");
      File cdepYmlFrom = new File(applicationBase, "cdep.yml");
      File cdepYmlTo = new File(workingFolder, "cdep.yml");
      File bootstrapFrom = new File(applicationBase, "bootstrap/wrapper/bootstrap.jar");
      File bootstrapTo = new File(workingFolder, "bootstrap/wrapper/bootstrap.jar");
      //noinspection ResultOfMethodCallIgnored
      bootstrapTo.getParentFile().mkdirs();
      info("Installing %s\n", cdepBatTo);
      FileUtils.copyFile(cdepBatFrom, cdepBatTo);
      info("Installing %s\n", cdepTo);
      FileUtils.copyFile(cdepFrom, cdepTo);
      if (!cdepTo.setExecutable(true)) {
        throw new RuntimeException("User did not have permission to make cdep executable");
      }
      info("Installing %s\n", bootstrapTo);
      FileUtils.copyFile(bootstrapFrom, bootstrapTo);
      if (cdepYmlTo.isFile()) {
        info("Not overwriting %s\n", cdepYmlTo);
      } else {
        info("Installing %s\n", cdepYmlTo);
        FileUtils.copyFile(cdepYmlFrom, cdepYmlTo);
      }
      return true;
    }
    return false;
  }

  private boolean handleFetch(@NotNull List<String> args)
      throws IOException, NoSuchAlgorithmException {
    if (args.size() > 0 && "fetch".equals(args.get(0))) {
      if (args.size() < 2) {
        info("Usage: cdep fetch {coordinate1} {coordinate2} ...\n");
        return true;
      }

      for (int i = 1; i < args.size(); ++i) {
        GeneratorEnvironment environment = getGeneratorEnvironment(false, true);
        SoftNameDependency dependencies[] = new SoftNameDependency[]{
            new SoftNameDependency(args.get(i))};
        FunctionTableExpression table = GeneratorEnvironmentUtils
            .getFunctionTableExpression(environment, dependencies);
        // Download and unzip archives.
        GeneratorEnvironmentUtils.downloadReferencedModules(environment,
            ExpressionUtils.getAllFoundModuleExpressions(table));
        // Check that the expected files were downloaded
        new CheckLocalFileSystemIntegrity(environment.unzippedArchivesFolder).visit(table);
      }

      info("Fetch complete\n");
      return true;
    }
    return false;
  }

  private boolean handleFetchArchive(@NotNull List<String> args)
      throws IOException, NoSuchAlgorithmException {
    if (args.size() > 0 && "fetch-archive".equals(args.get(0))) {
      if (args.size() != 5) {
        info("Usage: cdep fetch-archive {coordinate} archive.zip {size}{sha256}\n");
        return true;
      }
      Coordinate coordinate = CoordinateUtils.tryParse(args.get(1));
      String archive = args.get(2);
      Long size = Long.parseLong(args.get(3));
      String sha256 = args.get(4);

      GeneratorEnvironment environment = getGeneratorEnvironment(false, true);
      URL remoteArchive = new URL(archive);
      File localFile = environment.getLocalDownloadFilename(coordinate, remoteArchive);
      assert coordinate != null;
      GeneratorEnvironmentUtils.downloadSingleArchive(
          environment, coordinate, remoteArchive, size, sha256, false);
      require(localFile.isFile(), "Failed to download %s", localFile);
      return true;
    }
    return false;
  }

  private boolean handleFullfill(@NotNull List<String> args)
      throws IOException, NoSuchAlgorithmException {
    if (args.size() > 0 && "fullfill".equals(args.get(0))) {
      if (args.size() < 4) {
        info("Usage: cdep fullfill source-folder version manifest1.yml manifest2.yml ...\n");
        return true;
      }

      File manifests[] = new File[args.size() - 3];
      for (int i = 0; i < manifests.length; ++i) {
        manifests[i] = new File(args.get(i + 3));
        require(manifests[i].isFile(), "Manifest %s did not exist", manifests[i]);
      }

      File sourceFolder = new File(args.get(1));
      require(sourceFolder.isDirectory(), "Source folder %s did not exist", sourceFolder);

      File outputFolder = new File(".cdep/fullfill/output");

      String version = args.get(2);

      List<File> layoutFiles = Fullfill.multiple(getGeneratorEnvironment(false, false),
          manifests, outputFolder, sourceFolder, version);

      if (errorsInScope() == 0) {
        for (File file : layoutFiles) {
          info("%s\n", file);
        }
      }
      return true;
    }
    return false;
  }

  private void handleGenerateScript() throws IOException, NoSuchAlgorithmException {
    //noinspection ConstantConditions
    if (config.dependencies == null || config.dependencies.length == 0) {
      info("Nothing to do. Add dependencies to %s\n", configFile);
      return;
    }
    GeneratorEnvironment environment = getGeneratorEnvironment(false, false);
    environment.readCDepSHA256File();
    FunctionTableExpression table = getFunctionTableExpression(environment);

    // Download and unzip archives.
    //GeneratorEnvironmentUtils.downloadReferencedModules(environment, ExpressionUtils.getAllFoundModuleExpressions(table));

    // Check that the expected files were downloaded
    //new CheckLocalFileSystemIntegrity(environment.unzippedArchivesFolder).visit(table);

    runBuilders(environment, table);
    environment.writeCDepSHA256File();
  }

  @NotNull
  private FunctionTableExpression getFunctionTableExpression(
      @NotNull GeneratorEnvironment environment)
      throws IOException, NoSuchAlgorithmException {
    assert config != null;
    return GeneratorEnvironmentUtils.getFunctionTableExpression(environment, config.dependencies);
  }

  @NotNull
  private GeneratorEnvironment getGeneratorEnvironment(boolean forceRedownload,
      boolean ignoreManifestHashes) {
    return new GeneratorEnvironment(workingFolder, downloadFolder, forceRedownload,
        ignoreManifestHashes);
  }

  private boolean handleReadCDepYml() throws IOException {
    configFile = new File(workingFolder, "cdep.yml");
    if (!configFile.exists()) {
      info("Expected a configuration file at %s\n", configFile.getCanonicalFile());
      return false;
    }

    this.config = CDepYmlUtils.fromString(FileUtils.readAllText(configFile));
    CDepYmlUtils.checkSanity(config, configFile);
    return true;
  }

  private boolean handleHelp(@NotNull List<String> args) {
    if (args.size() != 1 || !args.get(0).equals("--help")) {
      return true;
    }
    info("cdep %s\n", BuildInfo.PROJECT_VERSION);
    info(" cdep: download dependencies and generate build modules for current cdep.yml\n");
    info(
        " cdep fullfill source-folder version manifest1.yml manifest2.yml: fill in template cdep-manifest.yml with hashes, "
            +
            "sizes, zips, etc\n");
    info(" cdep show folders: show local download and file folders\n");
    info(" cdep show manifest: show cdep interpretation of cdep.yml\n");
    info(" cdep show include {coordinate}: show local include path for the given coordinate\n");
    info(" cdep redownload: redownload dependencies for current cdep.yml\n");
    info(" cdep create hashes: create or recreate cdep.sha256 file\n");
    info(
        " cdep merge {coordinate} {coordinate2} ... outputmanifest.yml: merge manifests into outputmanifest.yml\n");
    info(
        " cdep merge headers {coordinate} {headers.zip} outputmanifest.yml: merge header and manifest into "
            +
            "outputmanifest.yml\n");
    info(" cdep fetch {coordinate} {coordinate2} ... : download multiple packages\n");
    info(
        " cdep fetch-archive {coordinate} archive.zip {size}{sha256} : download a single archive from within a package\n");
    info(" cdep wrapper: copy cdep to the current folder\n");
    info(" cdep --version: show version information\n");
    return false;
  }

  private void handleWorkingFolder(@NotNull List<String> args) {
    for (String workingFolder : eatStringArgument("-wf", "--working-folder", args)) {
      this.workingFolder = new File(workingFolder);
    }
  }

  private void handleDownloadFolder(@NotNull List<String> args) {
    for (String workingFolder : eatStringArgument("-df", "--download-folder", args)) {
      this.workingFolder = new File(workingFolder);
    }
  }

  private boolean handleVersion(@NotNull List<String> args) {
    if (args.size() != 1 || !args.get(0).equals("--version")) {
      return true;
    }
    info("cdep %s\n", BuildInfo.PROJECT_VERSION);
    return false;
  }

}