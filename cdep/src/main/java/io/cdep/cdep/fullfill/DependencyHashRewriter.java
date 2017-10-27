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

import io.cdep.annotations.NotNull;
import io.cdep.annotations.Nullable;
import io.cdep.cdep.generator.GeneratorEnvironment;
import io.cdep.cdep.resolver.ResolvedManifest;
import io.cdep.cdep.resolver.Resolver;
import io.cdep.cdep.utils.HashUtils;
import io.cdep.cdep.yml.cdep.SoftNameDependency;
import io.cdep.cdep.yml.cdepmanifest.CDepManifestYmlRewriter;
import io.cdep.cdep.yml.cdepmanifest.HardNameDependency;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static io.cdep.cdep.utils.Invariant.fail;
import static io.cdep.cdep.utils.Invariant.require;
import static io.cdep.cdep.yml.cdepmanifest.HardNameDependency.EMPTY_HARDNAME_DEPENDENCY;

/**
 * Fill in hash values for hard name dependencies.
 */
public class DependencyHashRewriter extends CDepManifestYmlRewriter {
  private final GeneratorEnvironment environment;

  DependencyHashRewriter(GeneratorEnvironment environment) {
    this.environment = environment;
  }

  @Nullable
  @Override
  protected HardNameDependency visitHardNameDependency(@NotNull HardNameDependency dependency) {
    require(!dependency.compile.isEmpty(), "Dependency had no compile field");
    if (dependency.sha256.isEmpty()) {
      try {
        ResolvedManifest resolved = new Resolver(environment).resolveAny(
            new SoftNameDependency(
                dependency.compile));
        if (resolved == null) {
          if (!dependency.compile.isEmpty()) {
            require(false, "Could not resolve dependency %s",
                dependency.compile);
          } else if (!dependency.sha256.isEmpty()) {
            require(false, "Could not resolve dependency [%s]",
                dependency.sha256.substring(0, 7));
          } else {
            fail("Could not resolve dependency because it had no name");
          }
          return EMPTY_HARDNAME_DEPENDENCY;
        }

        File manifest = environment.tryGetLocalDownloadedFile(
            resolved.cdepManifestYml.coordinate,
            resolved.remote);
        assert manifest != null;
        return new HardNameDependency(
            resolved.cdepManifestYml.coordinate.toString(),
            HashUtils.getSHA256OfFile(manifest));
      } catch (@NotNull IOException | NoSuchAlgorithmException e) {
        throw new RuntimeException(e);
      }
    }
    return super.visitHardNameDependency(dependency);
  }
}
