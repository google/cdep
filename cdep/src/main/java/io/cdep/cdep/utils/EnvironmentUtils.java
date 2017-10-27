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
package io.cdep.cdep.utils;

import io.cdep.annotations.NotNull;
import io.cdep.cdep.generator.GeneratorEnvironment;
import io.cdep.cdep.resolver.ResolvedManifest;
import io.cdep.cdep.resolver.Resolver;
import io.cdep.cdep.yml.cdep.SoftNameDependency;
import io.cdep.cdep.yml.cdepmanifest.Archive;
import io.cdep.cdep.yml.cdepmanifest.CDepManifestYml;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;

import static io.cdep.cdep.utils.Invariant.require;

public class EnvironmentUtils {

  /**
   * Returns the package level archive's include folder. Will throw an exception if there was no package level
   * archive.
   */
  @NotNull
  public static File getPackageLevelIncludeFolder(@NotNull GeneratorEnvironment environment, @NotNull String coordinate)
      throws IOException, NoSuchAlgorithmException, URISyntaxException {
    ResolvedManifest resolved = resolveManifest(environment, coordinate);
    return getPackageLevelIncludeFolder(environment, coordinate, resolved);
  }

  /**
   * Returns the package level archive's include folder. Will throw an exception if there was no package level
   * archive.
   */
  @NotNull
  static File getPackageLevelIncludeFolder(
      @NotNull GeneratorEnvironment environment,
      @NotNull String coordinate,
      @NotNull ResolvedManifest resolved) throws URISyntaxException, MalformedURLException {
    CDepManifestYml manifest = resolved.cdepManifestYml;
    require(manifest.interfaces != null, "'%s' does not have archive", coordinate);
    assert manifest.interfaces != null;
    Archive archive = manifest.interfaces.headers;
    require(archive != null, "'%s' does not have archive", coordinate);
    assert archive != null;
    require(!archive.include.isEmpty(), "'%s' does not have archive.include", coordinate);
    require(!archive.file.isEmpty(), "'%s' does not have archive.include.file", coordinate);
    return new File(environment.getLocalUnzipFolder(manifest.coordinate,
        resolved.remote.toURI().resolve(".").resolve(archive.file).toURL()), archive.include);
  }

  /**
   * Return the resolved manifest or throw an exception.
   */
  @NotNull
  private static ResolvedManifest resolveManifest(@NotNull GeneratorEnvironment environment, @NotNull String coordinate)
      throws IOException, NoSuchAlgorithmException {
    SoftNameDependency name = new SoftNameDependency(coordinate);
    ResolvedManifest resolved = new Resolver(environment).resolveAny(name);
    require(resolved != null, "Could not resolve '%s'", coordinate);
    assert resolved != null;
    return resolved;
  }
}
