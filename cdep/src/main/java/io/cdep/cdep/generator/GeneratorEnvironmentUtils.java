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
package io.cdep.cdep.generator;

import io.cdep.annotations.NotNull;
import io.cdep.annotations.Nullable;
import io.cdep.cdep.BuildFindModuleFunctionTable;
import io.cdep.cdep.Coordinate;
import io.cdep.cdep.ast.finder.Expression;
import io.cdep.cdep.ast.finder.FunctionTableExpression;
import io.cdep.cdep.ast.finder.ModuleArchiveExpression;
import io.cdep.cdep.ast.finder.ModuleExpression;
import io.cdep.cdep.resolver.ResolutionScope;
import io.cdep.cdep.resolver.ResolvedManifest;
import io.cdep.cdep.resolver.Resolver;
import io.cdep.cdep.utils.ArchiveUtils;
import io.cdep.cdep.utils.HashUtils;
import io.cdep.cdep.yml.cdep.SoftNameDependency;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.cdep.cdep.utils.Invariant.require;

/**
 * Methods for dealing with GeneratorEnvironment.
 */
public class GeneratorEnvironmentUtils {

  /**
   * Given a function table and generator environment, download all of the files referenced.
   */
  public static void downloadReferencedModules(
      @NotNull GeneratorEnvironment environment,
      @NotNull Map<Coordinate, List<Expression>> foundModules)
      throws IOException, NoSuchAlgorithmException {

    Set<File> alreadyExploded = new HashSet<>();

    // Download and unzip any modules.
    for (Coordinate coordinate : foundModules.keySet()) {
      List<Expression> foundModuleExpressions = foundModules.get(coordinate);
      for (Expression foundModule : foundModuleExpressions) {
        ModuleArchiveExpression archive = null;
        if (foundModule instanceof ModuleExpression) {
          ModuleExpression specific = (ModuleExpression) foundModule;
          archive = specific.archive;
        }
        assert archive != null;
        URL archiveURL = archive.file;
        Long size = archive.size;
        String sha256 = archive.sha256;

        File local = environment.getLocalDownloadFilename(coordinate, archiveURL);
        boolean forceUnzip = environment.forceRedownload && !alreadyExploded.contains(local);
        local = downloadSingleArchive(environment, coordinate, archiveURL, size, sha256, forceUnzip);
        alreadyExploded.add(local);
      }
    }
  }

  @Nullable
  public static File downloadSingleArchive(
      @NotNull GeneratorEnvironment environment,
      @NotNull Coordinate coordinate,
      @NotNull URL archiveURL,
      long size,
      @NotNull String sha256,
      boolean forceUnzip) throws IOException, NoSuchAlgorithmException {
    File local = environment.tryGetLocalDownloadedFile(coordinate, archiveURL);
    require(local != null, "Resolved archive '%s' didn't exist", archiveURL);
    assert local != null;
    if (size != local.length()) {
      // It may have been an interrupted download. Try again.
      if (!environment.forceRedownload) {
        forceUnzip = true;
        //noinspection ResultOfMethodCallIgnored
        local.delete();
        local = environment.tryGetLocalDownloadedFile(coordinate, archiveURL);
        require(local != null, "Resolved archive '%s' didn't exist", archiveURL);
      }
      assert local != null;
      require(size == local.length(),
          "File size for %s was %s which did not match constant %s from the manifest",
          archiveURL,
          local.length(),
          size);
    }

    String localSha256String = HashUtils.getSHA256OfFile(local);
    require(localSha256String.equals(sha256), "SHA256 for %s did not match constant from manifest", archiveURL);

    File unzipFolder = environment.getLocalUnzipFolder(coordinate, archiveURL);
    if (!unzipFolder.exists() || forceUnzip) {
      //noinspection ResultOfMethodCallIgnored
      unzipFolder.mkdirs();
      ArchiveUtils.unzip(local, unzipFolder);
    }
    return local;
  }

  /**
   * Resolve whatever unresolved references remain and add them to the table.
   */
  public static void addAllResolvedToTable(
      @NotNull BuildFindModuleFunctionTable builder,
      @NotNull ResolutionScope scope) {
    for (String name : scope.getResolutions()) {
      ResolvedManifest resolved = scope.getResolution(name);
      builder.addManifest(resolved);
    }
  }

  /**
   * Given a set of dependencies, build the corresponding compile function table. Will look up
   * transitive references if necessary.
   */
  @NotNull
  public static FunctionTableExpression getFunctionTableExpression(
      @NotNull GeneratorEnvironment environment,
      @NotNull SoftNameDependency dependencies[])
      throws IOException, NoSuchAlgorithmException {
    BuildFindModuleFunctionTable table = new BuildFindModuleFunctionTable();
    ResolutionScope scope = new Resolver(environment).resolveAll(dependencies);
    addAllResolvedToTable(table, scope);
    return table.build();
  }
}
