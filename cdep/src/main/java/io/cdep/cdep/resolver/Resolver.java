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
package io.cdep.cdep.resolver;

import io.cdep.annotations.NotNull;
import io.cdep.annotations.Nullable;
import io.cdep.cdep.resolver.ResolutionScope.Unresolvable;
import io.cdep.cdep.utils.CDepManifestYmlUtils;
import io.cdep.cdep.yml.cdep.SoftNameDependency;
import io.cdep.cdep.yml.cdepmanifest.HardNameDependency;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static io.cdep.cdep.utils.Invariant.fail;
import static io.cdep.cdep.utils.Invariant.require;

/**
 * Resolve references and groups of references (ResolutionScope)
 */
public class Resolver {

  final private static CoordinateResolver RESOLVERS[] = new CoordinateResolver[] {
      new GithubStyleUrlCoordinateResolver(),
      new GithubReleasesCoordinateResolver(),
      new LocalFilePathCoordinateResolver() };

  final private ManifestProvider manifestProvider;
  final private CoordinateResolver resolvers[];

  public Resolver(ManifestProvider manifestProvider) {
    this(manifestProvider, RESOLVERS);
  }

  Resolver(ManifestProvider manifestProvider, CoordinateResolver resolvers[]) {
    this.manifestProvider = manifestProvider;
    this.resolvers = resolvers;
  }

  /**
   * Resolve all of the references contained in the given ResolutionScope roots
   */
  @NotNull
  public ResolutionScope resolveAll(@NotNull SoftNameDependency[] roots)
      throws IOException, NoSuchAlgorithmException {
    ResolutionScope scope = new ResolutionScope(roots);
    resolveAll(scope);
    return scope;
  }

  /**
   * Resolve all of the references contained in the given ResolutionScope
   */
  public void resolveAll(@NotNull ResolutionScope scope)
      throws IOException, NoSuchAlgorithmException {
    // Progressively resolve dependencies
    while (!scope.isResolutionComplete()) {
      for (SoftNameDependency softname : scope.getUnresolvedReferences()) {
        ResolvedManifest resolved = resolveAny(softname);
        if (resolved == null) {
          scope.recordUnresolvable(softname);
        } else {
          List<HardNameDependency> transitive =
              CDepManifestYmlUtils.getTransitiveDependencies(resolved.cdepManifestYml);
          scope.recordResolved(softname, resolved, transitive);
        }
      }
    }

    // Throw some exceptions if we didn't resolve something.
    for (String softname : scope.getUnresolvableReferences()) {
      Unresolvable reason = scope.getUnresolveableReason(softname);
      switch(reason) {
        case DIDNT_EXIST:
          fail("Could not resolve '%s'. It doesn't exist.", softname);
          break;
        case UNPARSEABLE:
          fail("Could not resolve '%s'. It didn't look like a coordinate.", softname);
          break;
        default:
          fail(reason.toString());
          break;
      }
    }
  }

  /**
   * Resolve a single reference. Don't look at transitive references.
   */
  @Nullable
  public ResolvedManifest resolveAny(@NotNull SoftNameDependency dependency)
      throws IOException, NoSuchAlgorithmException {
    ResolvedManifest resolved = null;
    for (CoordinateResolver resolver : resolvers) {
      ResolvedManifest attempt = resolver.resolve(manifestProvider, dependency);
      if (attempt != null) {
        require(resolved == null, "Multiple resolvers matched coordinate: " + dependency.compile);
        resolved = attempt;
      }
    }
    if (resolved != null) {
      CDepManifestYmlUtils.checkManifestSanity(resolved.cdepManifestYml);
    }
    return resolved;
  }
}
