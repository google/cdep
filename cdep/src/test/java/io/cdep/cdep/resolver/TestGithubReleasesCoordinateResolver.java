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

import io.cdep.cdep.generator.GeneratorEnvironment;
import io.cdep.cdep.yml.cdep.SoftNameDependency;
import org.junit.Test;

import java.io.File;

import static com.google.common.truth.Truth.assertThat;

public class TestGithubReleasesCoordinateResolver {
  final private GeneratorEnvironment environment = new GeneratorEnvironment(new File(""  +
      "./test-files/TestGithubReleasesCoordinateResolver/working"), null, false, false);

  @Test
  public void testCompound() throws Exception {
    ResolvedManifest resolved = new GithubReleasesCoordinateResolver().resolve(environment, new SoftNameDependency("com.github"
        + ".jomof:firebase/database:2.1.3-rev5"));
    assert resolved != null;
    assert resolved.cdepManifestYml.coordinate != null;
    assertThat(resolved.cdepManifestYml.coordinate.groupId).isEqualTo("com.github.jomof");
    assertThat(resolved.cdepManifestYml.coordinate.artifactId).isEqualTo("firebase/database");
    assertThat(resolved.cdepManifestYml.coordinate.version.value).isEqualTo("2.1.3-rev5");
    assert resolved.cdepManifestYml.android != null;
    assert resolved.cdepManifestYml.android.archives != null;
    assertThat(resolved.cdepManifestYml.android.archives.length).isEqualTo(21);
  }
}
