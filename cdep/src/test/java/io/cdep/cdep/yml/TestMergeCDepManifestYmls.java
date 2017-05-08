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
import io.cdep.cdep.utils.CDepManifestYmlUtils;
import io.cdep.cdep.yml.cdepmanifest.*;
import org.junit.Test;

import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.truth.Truth.assertThat;
import static junit.framework.TestCase.fail;

public class TestMergeCDepManifestYmls {

  @Test
  public void testJustCoordinate() throws MalformedURLException {
    try {
      MergeCDepManifestYmls.merge(ResolvedManifests.sqlite().manifest.cdepManifestYml,
          ResolvedManifests.admob().manifest.cdepManifestYml);
      fail("Expected exception");
    } catch (RuntimeException e) {
      assertThat(e).hasMessage("Manifests were different at artifactId.coordinate.[constant]");
    }
  }

  @Test
  public void testMergeAndroidiOS() throws MalformedURLException {
    CDepManifestYml iOSManifest = ResolvedManifests.sqliteiOS().manifest.cdepManifestYml;
    CDepManifestYml androidManifest = ResolvedManifests.sqliteAndroid().manifest.cdepManifestYml;
    CDepManifestYml result = MergeCDepManifestYmls.merge(androidManifest, iOSManifest);
    iOS iOS = result.iOS;
    assert iOSManifest.iOS != null;
    assert iOSManifest.iOS.archives != null;
    assertThat(iOS.archives).hasLength(iOSManifest.iOS.archives.length);
    Android android = result.android;
    assert androidManifest.android != null;
    assert androidManifest.android.archives != null;
    assertThat(android.archives).hasLength(androidManifest.android.archives.length);
  }

  @Test
  public void testTwoWayMerges() throws Exception {
    Set<String> commonDifferences = new HashSet<>();
    commonDifferences.add("Manifests were different at requires.requires.headers.interfaces.[constant]");
    commonDifferences.add("Manifests were different at groupId.coordinate.[constant]");
    commonDifferences.add("Manifests were different at artifactId.coordinate.[constant]");
    commonDifferences.add("Manifests were different at file.headers.interfaces.[constant]");
    commonDifferences.add("Manifests were different at include.headers.interfaces.[constant]");
    commonDifferences.add("Manifests were different at size.headers.interfaces.[constant]");
    commonDifferences.add("Manifests were different at sha256.archive.[constant]");
    commonDifferences.add("Manifests were different at value.version.coordinate.[constant]");
    commonDifferences.add("Manifests were different at compile.dependencies.[constant]");

    // Some manifest strings don't round trip because we don't have concept of null scalars
    // only empty
    Set<String> stringsDontRoundTrip = new HashSet<>();
    stringsDontRoundTrip.add("fuzz1-fuzz1");
    boolean somethingUnexpected = false;
    for (ResolvedManifests.NamedManifest manifest1 : ResolvedManifests.all()) {
      for (ResolvedManifests.NamedManifest manifest2 : ResolvedManifests.all()) {
        String key = manifest1.name + "-" + manifest2.name;
        try {
          CDepManifestYml merged1 = MergeCDepManifestYmls.merge(
              manifest1.resolved.cdepManifestYml,
              manifest2.resolved.cdepManifestYml);
          String string = CDepManifestYmlUtils.convertManifestToString(merged1);
          CDepManifestYml merged2 = CDepManifestYmlUtils.convertStringToManifest(string);
          if (!stringsDontRoundTrip.contains(key)) {
            if (!CDepManifestYmlEquality.areDeeplyIdentical(merged1, merged2)) {
              CDepManifestYmlUtils.convertManifestToString(merged1);
              assertThat(string).isEqualTo(CDepManifestYmlUtils.convertManifestToString(merged2));
              CDepManifestYmlEquality.areDeeplyIdentical(merged1, merged2);
              fail("Converted string wasn't the same as original");
            }
          }
        } catch (RuntimeException e) {
          if (!e.getClass().equals(RuntimeException.class)) {
            throw e;
          }
          String actual = e.getMessage();
          if (!commonDifferences.contains(actual)) {
            // e.printStackTrace();
            System.out.printf("expected.put(\"%s\", \"%s\");\n", key, actual);
            somethingUnexpected = true;
          }
        }
      }
    }

    if (somethingUnexpected) {
      throw new RuntimeException("Saw unexpected results. See console.");
    }
  }

  @Test
  public void mergeAndroidiOSLinux() throws Exception {
    CDepManifestYml result = MergeCDepManifestYmls.merge(
        ResolvedManifests.sqlite().manifest.cdepManifestYml,
        ResolvedManifests.sqliteLinux().manifest.cdepManifestYml);
    assertThat(result.linux).isNotNull();
    assertThat(result.linux.archives).isNotEmpty();
    assertThat(result.linux.archives).hasLength(1);
  }
}
