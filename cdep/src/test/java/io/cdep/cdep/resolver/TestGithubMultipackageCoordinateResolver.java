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

import io.cdep.cdep.Coordinate;
import io.cdep.cdep.generator.GeneratorEnvironment;
import io.cdep.cdep.yml.cdep.SoftNameDependency;
import io.cdep.cdep.yml.cdepmanifest.CDepManifestYml;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

import static com.google.common.truth.Truth.assertThat;

public class TestGithubMultipackageCoordinateResolver {
  final private GeneratorEnvironment environment = new GeneratorEnvironment(
      new File("./test-files/TestGithubMultipackageCoordinateResolver/working"),
      null,
      null,
      null,
      false,
      false);

  @Test
  public void testCompound() throws Exception {
    final ManifestProvider manifestProvider = new ManifestProvider() {
      @Override
      public CDepManifestYml tryGetManifest(Coordinate coordinate, URL remoteArchive)
          throws IOException, NoSuchAlgorithmException {
        assertThat(remoteArchive.toString()).isEqualTo(
            "https://github.com/google/cdep/releases/download/firebase%402.1.3-rev5/cdep-manifest-database.yml");
        return new CDepManifestYml(coordinate);
      }
    };

    ResolvedManifest resolved = new GithubMultipackageCoordinateResolver()
        .resolve(
            manifestProvider,
            new SoftNameDependency("com.github.google.cdep:firebase/database:2.1.3-rev5"));
    assert resolved != null;
    assert resolved.cdepManifestYml.coordinate != null;
    assertThat(resolved.cdepManifestYml.coordinate.groupId).isEqualTo("com.github.google.cdep");
    assertThat(resolved.cdepManifestYml.coordinate.artifactId).isEqualTo("firebase/database");
    assertThat(resolved.cdepManifestYml.coordinate.version.value).isEqualTo("2.1.3-rev5");
  }
}