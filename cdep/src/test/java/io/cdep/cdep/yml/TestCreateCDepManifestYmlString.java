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

import io.cdep.annotations.NotNull;
import io.cdep.cdep.Coordinate;
import io.cdep.cdep.ResolvedManifests;
import io.cdep.cdep.Version;
import io.cdep.cdep.utils.CDepManifestYmlUtils;
import io.cdep.cdep.utils.Invariant;
import io.cdep.cdep.yml.cdepmanifest.*;
import net.java.quickcheck.QuickCheck;
import net.java.quickcheck.characteristic.AbstractCharacteristic;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static io.cdep.cdep.yml.cdepmanifest.CDepManifestBuilder.archive;
import static io.cdep.cdep.yml.cdepmanifest.CDepManifestBuilder.hardname;
import static io.cdep.cdep.yml.cdepmanifest.CreateCDepManifestYmlString.serialize;
import static io.cdep.cdep.yml.cdepmanifest.CxxLanguageFeatures.cxx_alignas;
import static io.cdep.cdep.yml.cdepmanifest.CxxLanguageFeatures.cxx_auto_type;

public class TestCreateCDepManifestYmlString {

  private static void check(@NotNull CDepManifestYml manifest) {
    String result = null;
    CDepManifestYml manifest2 = null;
    try {
      // Convert to constant
      result = CDepManifestYmlUtils.convertManifestToString(manifest);

      // Convert from constant
      manifest2 = CDepManifestYmlUtils.convertStringToManifest(result);

      // Would like to compare equality here.
      CDepManifestYmlEquality.throwIfNotDeeplyIdentical(manifest, manifest2);
    } catch (Exception e) {
      throw e;
    }
  }

  private static void checkIgnoreErrors(@NotNull CDepManifestYml manifest) {
    try {
      Invariant.pushScope();
      check(manifest);
    } finally {
      Invariant.popScope();
    }
  }

  @Test
  public void testSimple1() {
    assertThat(serialize(hardname("nameval", "shaval"))).isEqualTo("compile: nameval\r\n" + "sha256: shaval\r\n");
  }

  @Test
  public void testSimple2() {

    assertThat(serialize(hardname("nameval2", "shaval2"))).isEqualTo("compile: nameval2\r\n" + "sha256: shaval2\r\n");
  }

  @Test
  public void fuzzTest() {
//    for (int i = 0; i < 1000; ++i)
    QuickCheck.forAll(new CDepManifestYmlGenerator(), new AbstractCharacteristic<CDepManifestYml>() {
      @Override
      protected void doSpecify(CDepManifestYml any) throws Throwable {
        checkIgnoreErrors(any);
      }
    });
  }

  @Test
  public void testCoordinateIsBlankStrings() {
    check(new CDepManifestYml(new Coordinate("", "", new Version(""))));
  }

  @Test
  public void testArchive() {
    assertThat(serialize(archive("fileval",
        "shaval",
        100,
        "hello",
        new CxxLanguageFeatures[0]))).isEqualTo("file: fileval\r\n" + "sha256: shaval\r\n" +
        "size: 100\r\n" + "include: " + "hello\r\n");
  }

  @Test
  public void testArchiveWithRequires() {
    assertThat(serialize(archive(
        "fileval",
        "shaval",
        100,
        "hello",
        new CxxLanguageFeatures[]{cxx_auto_type, cxx_alignas})))
        .isEqualTo(
            "file: fileval\r\n" +
                "sha256: shaval\r\n" +
                "size: 100\r\n" +
                "include: hello\r\n" +
                "requires: [cxx_auto_type, cxx_alignas]\r\n");
  }

  @Test
  public void testUnwantedQuotes() {
    assertThat(serialize(new CDepManifestYml(
        CDepManifestYmlVersion.v3,
        new Coordinate("com.github.jomof", "openssl", new Version("1.0.1-e-rev5")),
        new HardNameDependency[0],
        new License(),
        null,
        null,
        null,
        null,
        "")))
        .isEqualTo(
            "coordinate:\r\n"
                + "  groupId: com.github.jomof\r\n"
                + "  artifactId: openssl\r\n"
                + "  version: 1.0.1-e-rev5\r\n" +
                  "license:\r\n");
  }

  @Test
  public void testSqlite() throws Exception {
    check(ResolvedManifests.sqlite().manifest.cdepManifestYml);
  }

  @Test
  public void testAdmob() throws Exception {
    check(ResolvedManifests.admob().manifest.cdepManifestYml);
  }

  @Test
  public void testRequires() throws Exception {
    // Ensure that round-trip works
    String originalString = ResolvedManifests.simpleRequires().body;
    CDepManifestYml originalManifest = CDepManifestYmlUtils.convertStringToManifest(originalString);
    String convertedString = CDepManifestYmlUtils.convertManifestToString(originalManifest);
    CDepManifestYml convertedManifest = CDepManifestYmlUtils.convertStringToManifest(convertedString);
    assertThat(CDepManifestYmlEquality.areDeeplyIdentical(originalManifest, convertedManifest)).isTrue();
  }

  @Test
  public void testAllResolvedManifests() throws Exception {
    for (ResolvedManifests.TestManifest manifest : ResolvedManifests.allTestManifest()) {
      if (manifest.body.equals(ResolvedManifests.fuzz1().body)) {
        // This one doesn't round trip because of null<->blank non-distinction
        continue;
      }
      check(manifest.manifest.cdepManifestYml);
    }
  }
}
