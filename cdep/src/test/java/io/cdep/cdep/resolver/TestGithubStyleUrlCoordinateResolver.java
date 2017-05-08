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

import static com.google.common.truth.Truth.assertThat;

import io.cdep.cdep.generator.GeneratorEnvironment;
import io.cdep.cdep.yml.cdep.SoftNameDependency;
import java.io.File;
import org.junit.Test;

public class TestGithubStyleUrlCoordinateResolver {

  final private GeneratorEnvironment environment = new GeneratorEnvironment(new File(""  +
      "./test-files/TestFindModuleFunctionTableBuilder/working"), null, false, false);

  @Test
  public void testSimple() throws Exception {
    ResolvedManifest resolved = new GithubStyleUrlCoordinateResolver().resolve(environment, new SoftNameDependency
        ("https://github" + ".com/jomof/cmakeify/releases/download/0.0.81/cdep-manifest.yml"));
    assert resolved != null;
    assert resolved.cdepManifestYml.coordinate != null;
    assertThat(resolved.cdepManifestYml.coordinate.groupId).isEqualTo("com.github.jomof");
    assertThat(resolved.cdepManifestYml.coordinate.artifactId).isEqualTo("cmakeify");
    assertThat(resolved.cdepManifestYml.coordinate.version.value).isEqualTo("0.0.81");
    assert resolved.cdepManifestYml.android != null;
    assert resolved.cdepManifestYml.android.archives != null;
    assertThat(resolved.cdepManifestYml.android.archives.length).isEqualTo(8);
  }

  @Test
  public void testCompound() throws Exception {
    ResolvedManifest resolved = new GithubStyleUrlCoordinateResolver().resolve(environment, new SoftNameDependency
        ("https://github" + ".com/jomof/firebase/releases/download/2.1.3-rev5/cdep-manifest-database.yml"));
    assert resolved != null;
    assert resolved.cdepManifestYml.coordinate != null;
    assertThat(resolved.cdepManifestYml.coordinate.groupId).isEqualTo("com.github.jomof");
    assertThat(resolved.cdepManifestYml.coordinate.artifactId).isEqualTo("firebase/database");
    assertThat(resolved.cdepManifestYml.coordinate.version.value).isEqualTo("2.1.3-rev5");
    assert resolved.cdepManifestYml.android != null;
    assert resolved.cdepManifestYml.android.archives != null;
    assertThat(resolved.cdepManifestYml.android.archives.length).isEqualTo(21);
  }

  @Test
  public void testMissing() throws Exception {
    // Missing URL should return null because the coordinate may be resolvable in other ways.
    ResolvedManifest resolved = new GithubStyleUrlCoordinateResolver().resolve(environment, new SoftNameDependency
        ("https://github" + ".com/jomof/firebase/releases/download/0.0.0/cdep-manifest-appx.yml"));
    assertThat(resolved).isNull();
  }
}
