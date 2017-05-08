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
package io.cdep.cdep.fullfill;

import static io.cdep.cdep.io.IO.infoln;
import static io.cdep.cdep.utils.Invariant.errorsInScope;

import io.cdep.annotations.NotNull;
import io.cdep.cdep.BuildFindModuleFunctionTable;
import io.cdep.cdep.Coordinate;
import io.cdep.cdep.generator.GeneratorEnvironment;
import io.cdep.cdep.generator.GeneratorEnvironmentUtils;
import io.cdep.cdep.resolver.ResolutionScope;
import io.cdep.cdep.resolver.ResolvedManifest;
import io.cdep.cdep.resolver.Resolver;
import io.cdep.cdep.utils.CDepManifestYmlUtils;
import io.cdep.cdep.utils.FileUtils;
import io.cdep.cdep.yml.cdep.SoftNameDependency;
import io.cdep.cdep.yml.cdepmanifest.CDepManifestYml;
import io.cdep.cdep.yml.cdepmanifest.CDepManifestYmlEquality;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Fullfill {

  /**
   * Returns a list of manifest files and zip files,
   */
  @NotNull
  public static List<File> multiple(
      GeneratorEnvironment environment,
      @NotNull File templates[],
      File outputFolder,
      @NotNull File sourceFolder,
      String version) throws IOException, NoSuchAlgorithmException {
    List<File> result = new ArrayList<>();
    CDepManifestYml manifests[] = new CDepManifestYml[templates.length];

    File layout = new File(outputFolder, "layout");
    if (!layout.isDirectory()) {
      //noinspection ResultOfMethodCallIgnored
      layout.mkdirs();
    }

    File staging = new File(outputFolder, "staging");
    if (!staging.isDirectory()) {
      //noinspection ResultOfMethodCallIgnored
      staging.mkdirs();
    }

    // Read all manifest files
    for (int i = 0; i < manifests.length; ++i) {
      String body = FileUtils.readAllText(templates[i]);
      manifests[i] = CDepManifestYmlUtils.convertStringToManifest(body);
    }

    // Replace variables
    SubstituteStringsRewriter substitutor = new SubstituteStringsRewriter()
        .replace("${source}", sourceFolder.getAbsolutePath())
        .replace("${layout}", layout.getAbsolutePath())
        .replace("${version}", version);
    for (int i = 0; i < manifests.length; ++i) {
      manifests[i] = substitutor.visitCDepManifestYml(manifests[i]);
    }

    // Build function table along the way to prove function table can be built from the resulting
    // manifests.
    Resolver resolver = new Resolver(environment);
    ResolutionScope scope = new ResolutionScope();

    infoln("Fullfilling %s manifests", templates.length);
    for (int i = 0; i < manifests.length; ++i) {
      Coordinate coordinate = manifests[i].coordinate;

      FillMissingFieldsBasedOnFilepathRewriter filler = new FillMissingFieldsBasedOnFilepathRewriter();
      infoln("  guessing archive details from path names in %s", coordinate);
      manifests[i] = filler.visitCDepManifestYml(manifests[i]);
      if (errorsInScope() > 0) {
        // Exit early if there were problems
        return result;
      }

      ZipFilesRewriter zipper = new ZipFilesRewriter(layout, staging);
      infoln("  zipping files referenced in %s", coordinate);
      manifests[i] = zipper.visitCDepManifestYml(manifests[i]);
      result.addAll(zipper.getZips());
      if (errorsInScope() > 0) {
        // Exit early if there were problems
        return result;
      }

      FileHashAndSizeRewriter hasher = new FileHashAndSizeRewriter(layout);
      infoln("  computing hashes and file sizes of archives in %s", coordinate);
      manifests[i] = hasher.visitCDepManifestYml(manifests[i]);
      if (errorsInScope() > 0) {
        // Exit early if there were problems
        return result;
      }

      DependencyHashRewriter dependencyHasher =
          new DependencyHashRewriter(environment);
      infoln("  hashing dependencies in %s", coordinate);
      manifests[i] = dependencyHasher.visitCDepManifestYml(manifests[i]);
      if (errorsInScope() > 0) {
        // Exit early if there were problems
        return result;
      }

      File output = new File(layout, templates[i].getName());
      infoln("  writing manifest file %s", new File(".")
          .toURI().relativize(output.toURI()).getPath());
      String body = CDepManifestYmlUtils.convertManifestToString(manifests[i]);
      FileUtils.writeTextToFile(output, body);
      result.add(output);
      if (errorsInScope() > 0) {
        // Exit early if there were problems
        return result;
      }

      infoln("  checking sanity of result %s", coordinate);
      // Attempt to read the manifest that was written to disk.
      CDepManifestYml readFromDisk = CDepManifestYmlUtils.convertStringToManifest(
          FileUtils.readAllText(output));
      CDepManifestYmlEquality.areDeeplyIdentical(manifests[i], readFromDisk);
      if (errorsInScope() > 0) {
        // Exit early if there were problems
        return result;
      }
      CDepManifestYmlUtils.checkManifestSanity(manifests[i]);

      // Find any transitive dependencies that we may need to build the function table.
      SoftNameDependency softname = new SoftNameDependency(coordinate.toString());
      scope.addUnresolved(softname);
      scope.recordResolved(
          softname,
          new ResolvedManifest(output.toURI().toURL(), manifests[i]),
          CDepManifestYmlUtils.getTransitiveDependencies(manifests[i]));
      if (errorsInScope() > 0) {
        // Exit early if there were problems
        return result;
      }
    }

    infoln("  checking consistency of all manifests");
    // Resolve all remaining dependencies. This happens if the fullfilled manifests have
    // their own dependencies.
    resolver.resolveAll(scope);

    // Attempt to build a function table for the combination of manifests.
    BuildFindModuleFunctionTable table = new BuildFindModuleFunctionTable();
    GeneratorEnvironmentUtils.addAllResolvedToTable(table, scope);
    table.build();
    if (errorsInScope() > 0) {
      // Exit early if there were problems
      return result;
    }



    return result;
  }
}
