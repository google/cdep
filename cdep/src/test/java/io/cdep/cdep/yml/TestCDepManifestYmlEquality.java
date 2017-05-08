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
package io.cdep.cdep.yml;

import io.cdep.cdep.ResolvedManifests;
import io.cdep.cdep.yml.cdepmanifest.CDepManifestYml;
import io.cdep.cdep.yml.cdepmanifest.CDepManifestYmlEquality;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static io.cdep.cdep.utils.Invariant.require;

public class TestCDepManifestYmlEquality {

  private static void checkEquals(CDepManifestYml left, CDepManifestYml right) {
    assertThat(CDepManifestYmlEquality.areDeeplyIdentical(left, right)).isTrue();
  }

  private static void checkNotEquals(CDepManifestYml left, CDepManifestYml right) {
    assertThat(CDepManifestYmlEquality.areDeeplyIdentical(left, right)).isFalse();
  }

  @Test
  public void testSqlite() throws Exception {
    checkEquals(ResolvedManifests.sqlite().manifest.cdepManifestYml, ResolvedManifests.sqlite().manifest.cdepManifestYml);
  }

  @Test
  public void testAdmob() throws Exception {
    checkEquals(ResolvedManifests.admob().manifest.cdepManifestYml, ResolvedManifests.admob().manifest.cdepManifestYml);
  }

  @Test
  public void testSqliteAdmob() throws Exception {
    checkNotEquals(ResolvedManifests.sqlite().manifest.cdepManifestYml, ResolvedManifests.admob().manifest.cdepManifestYml);
  }

  @Test
  public void testAdmobSqlite() throws Exception {
    checkNotEquals(ResolvedManifests.admob().manifest.cdepManifestYml, ResolvedManifests.sqlite().manifest.cdepManifestYml);
  }

  @Test
  public void testEqualityPairs() throws Exception {
    for (ResolvedManifests.NamedManifest manifest1 : ResolvedManifests.all()) {
      for (ResolvedManifests.NamedManifest manifest2 : ResolvedManifests.all()) {
        boolean expected = manifest1.name.equals(manifest2.name);
        if (CDepManifestYmlEquality.areDeeplyIdentical(
            manifest1.resolved.cdepManifestYml,
            manifest2.resolved.cdepManifestYml) != expected) {
          if (expected) {
            CDepManifestYmlEquality.throwIfNotDeeplyIdentical(
                manifest1.resolved.cdepManifestYml,
                manifest2.resolved.cdepManifestYml);
          } else {
            CDepManifestYmlEquality.areDeeplyIdentical(
                manifest1.resolved.cdepManifestYml,
                manifest2.resolved.cdepManifestYml);
            require(false, "Expected %s to not be equal to %s", manifest1.name, manifest2.name);
          }
        }
      }
    }
  }
}

