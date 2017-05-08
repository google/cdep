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

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

import io.cdep.annotations.NotNull;
import io.cdep.cdep.ResolvedManifests;
import io.cdep.cdep.yml.cdepmanifest.CDepManifestYml;
import io.cdep.cdep.yml.cdepmanifest.MergeCDepManifestYmls;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Test;

public class TestCDepManifestYmlUtils {

  @Test
  public void coverConstructor() {
    // Call constructor of tested class to cover that code.
    new CoverConstructor();
  }

  @Test
  public void empty() {
    try {
      check("");
      fail("Expected an exception");
    } catch (Exception e) {
      assertThat(e).hasMessage("Manifest was empty");
    }
  }

  @Test
  public void missingCoordinate() {
    try {
      check("coordinate:\n");
      fail("Expected an exception");
    } catch (Exception e) {
      assertThat(e).hasMessage("Manifest was missing coordinate");
    }
  }

  @Test
  public void noArtifactId() {
    try {
      check("coordinate:\n" + "  groupId: com.github.jomof\n");
      fail("Expected an exception");
    } catch (Exception e) {
      assertThat(e).hasMessage("Manifest was missing coordinate.artifactId");
    }
  }

  @Test
  public void noVersion() {
    try {
      check("coordinate:\n" + "  groupId: com.github.jomof\n" + "  artifactId: boost\n");
      fail("Expected an exception");
    } catch (Exception e) {
      assertThat(e).hasMessage("Manifest was missing coordinate.version");
    }
  }

  @Test
  public void noGroupId() {
    try {
      check("coordinate:\n" + "  artifactId: boost\n" + "  version: 1.0.63-rev10");
      fail("Expected an exception");
    } catch (Exception e) {
      assertThat(e).hasMessage("Manifest was missing coordinate.groupId");
    }
  }

  @Test
  public void noTargets() {
    try {
      check("coordinate:\n" + "  groupId: com.github.jomof\n" + "  artifactId: boost\n" + "  version: 1.0.63-rev10");
      fail("Expected an exception");
    } catch (Exception e) {
      assertThat(e).hasMessage("Package 'com.github.jomof:boost:1.0.63-rev10' does not contain any files");
    }
  }

  @Test
  public void malformedVersion() {
    try {
      check("coordinate:\n" + "  groupId: com.github.jomof\n" + "  artifactId: boost\n" + "  version: 1.0");
      fail("Expected an exception");
    } catch (Exception e) {
      assertThat(e).hasMessage("Package 'com.github.jomof:boost:1.0' has malformed version, " + "expected major.minor" +
          ".point[-tweak] but there was only one " + "dot");
    }
  }

  @Test
  public void duplicateAndroidZips() {
    //    try {
    //      check("coordinate:\n" + "  groupId: com.github.jomof\n" + "  artifactId: boost\n" + "  version: 1.0.63-rev10\n" +
    //          "android:\n" + "  archives:\n" + "  -" + " file: bob.zip\n" + "    size: 99\n" + "    sha256: " +
    //          "97ce6635df1f44653a597343cd5757bb8b6b992beb3720f5fc761e3644bcbe7b\n" + "  - file: bob.zip\n" + "    size: 99\n"
    // + "  "  + "  sha256: 97ce6635df1f44653a597343cd5757bb8b6b992beb3720f5fc761e3644bcbe7b\n");
    //      fail("Expected an exception");
    //    } catch (Exception e) {
    //      assertThat(e).hasMessage("Package 'com.github.jomof:boost:1.0.63-rev10' contains multiple references " + "to the
    // same " +
    //          "archive file 'bob.zip'");
    //    }
  }

  @Test
  public void duplicateiOSZips() {
    //    try {
    //      check("coordinate:\n" + "  groupId: com.github.jomof\n" + "  artifactId: boost\n" + "  version: 1.0.63-rev10\n" +
    //          "iOS:\n" + "  archives:\n" + "  - " + "file: bob.zip\n" + "    size: 99\n" + "    platform: iPhoneSimulator\n"
    // + "   " +
    //          "" + " sdk: 10.2\n" + "    architecture: i386\n" + "    sha256: " +
    //          "97ce6635df1f44653a597343cd5757bb8b6b992beb3720f5fc761e3644bcbe7b\n" + "  - file: bob.zip\n" + "    size: 99\n"
    // + "  " +
    //          "" + "  platform: iPhoneSimulator\n" + "    sdk: 10.2\n" + "    architecture: i386\n" + "    sha256: " +
    //          "97ce6635df1f44653a597343cd5757bb8b6b992beb3720f5fc761e3644bcbe7b\n");
    //      fail("Expected an exception");
    //    } catch (Exception e) {
    //      assertThat(e).hasMessage("Package 'com.github.jomof:boost:1.0.63-rev10' contains multiple references " + "to the
    // same " +
    //          "archive file 'bob.zip'");
    //    }
  }

  @Test
  public void duplicateZipsBetweenAndroidAndiOS() {
    //    try {
    //      check("coordinate:\n" +
    //          "  groupId: com.github.jomof\n" + "  artifactId: boost\n" + "  version: 1.0.63-rev10\n" +
    //          "android:\n" + "  archives:\n" + "  -" + " file: bob.zip\n" + "    size: 99\n" + "    sha256: " +
    // "97ce6635df1f44653a597343cd5757bb8b6b992beb3720f5fc761e3644bcbe7b\n" + "iOS:\n" + "  " + "archives:\n" + "  - file: " +
    // "bob.zip\n" + "    size: 99\n" + "    platform: iPhoneSimulator\n" + "    sdk: 10.2\n" + "    architecture: i386\n" + "
    // " + "  sha256: 97ce6635df1f44653a597343cd5757bb8b6b992beb3720f5fc761e3644bcbe7b\n");
    //      fail("Expected an exception");
    //    } catch (Exception e) {
    //      assertThat(e).hasMessage("Package 'com.github.jomof:boost:1.0.63-rev10' contains multiple references " + "to the
    // same " +
    //          "archive file 'bob.zip'");
    //    }
  }

  @Test
  public void emptyiOSArchive() {
    check("coordinate:\n" + "  groupId: com.github.jomof\n" + "  artifactId: boost\n" + "  version: 1.0.63-rev10\n" +
        "android:\n" + "  archives:\n" + "  - " + "file: bob.zip\n" + "    size: 99\n" + "    sha256: " +
        "97ce6635df1f44653a597343cd5757bb8b6b992beb3720f5fc761e3644bcbe7b\n" + "iOS:\n" + "  archives:\n");
  }

  @Test
  public void emptyAndroidArchive() {
    check("coordinate:\n" + "  groupId: com.github.jomof\n" + "  artifactId: boost\n" + "  version: 1.0.63-rev10\n" + "iOS:\n"
        + "  archives:\n" + "  - file:" + " bob.zip\n" + "    size: 99\n" + "    sha256: " +
        "97ce6635df1f44653a597343cd5757bb8b6b992beb3720f5fc761e3644bcbe7b\n" + "    platform: " + "iPhoneSimulator\n" + "    "
        + "sdk: 10.2\n" + "    architecture: i386\n" + "android:\n" + "  archives:\n");
  }

  @Test
  public void missingAndroidSha() {
    try {
      check("coordinate:\n" + "  groupId: com.github.jomof\n" + "  artifactId: boost\n" + "  version: 1.0.63-rev10\n" +
          "android:\n" + "  archives:\n" + "   " + " - file: bob.zip\n");
      fail("Expected an exception");
    } catch (Exception e) {
      assertThat(e).hasMessage("Package 'com.github.jomof:boost:1.0.63-rev10' has missing android.archive.sha256 for 'bob.zip'");
    }
  }

  @Test
  public void missingiOSSha() {
    try {
      check("coordinate:\n" + "  groupId: com.github.jomof\n" + "  artifactId: boost\n" + "  version: 1.0.63-rev10\n" +
          "iOS:\n" + "  archives:\n" + "    - " + "file: bob.zip\n");
      fail("Expected an exception");
    } catch (Exception e) {
      assertThat(e).hasMessage("Package 'com.github.jomof:boost:1.0.63-rev10' has missing ios.archive.sha256 for 'bob.zip'");
    }
  }

  @Test
  public void missingAndroidFile() {
    try {
      check("coordinate:\n" + "  groupId: com.github.jomof\n" + "  artifactId: boost\n" + "  version: 1.0.63-rev10\n" +
          "android:\n" + "  archives:\n" + "   " + " - sha256: " +
          "97ce6635df1f44653a597343cd5757bb8b6b992beb3720f5fc761e3644bcbe7b" + "\n");
      fail("Expected an exception");
    } catch (Exception e) {
      assertThat(e).hasMessage("Package 'com.github.jomof:boost:1.0.63-rev10' has missing android.archive.file");
    }
  }

  @Test
  public void missingiOSFile() {
    try {
      check("coordinate:\n" + "  groupId: com.github.jomof\n" + "  artifactId: boost\n" + "  version: 1.0.63-rev10\n" +
          "iOS:\n" + "  archives:\n" + "    - " + "sha256: 97ce6635df1f44653a597343cd5757bb8b6b992beb3720f5fc761e3644bcbe7b\n");
      fail("Expected an exception");
    } catch (Exception e) {
      assertThat(e).hasMessage("Package 'com.github.jomof:boost:1.0.63-rev10' has missing ios.archive.file");
    }
  }

  @Test
  public void missingAndroidSize() {
    try {
      check("coordinate:\n"
          + "  groupId: com.github.jomof\n"
          + "  artifactId: boost\n"
          + "  version: 1.0.63-rev10\n"
          + "android:\n"
          + "  archives:\n"
          + "   - file: bob.zip\n"
          + "     sha256: 97ce6635df1f44653a597343cd5757bb8b6b992beb3720f5fc761e3644bcbe7b\n");
      fail("Expected an exception");
    } catch (Exception e) {
      assertThat(e).hasMessage("Package 'com.github.jomof:boost:1.0.63-rev10' has missing or zero android.archive.size for 'bob.zip'");
    }
  }

  @Test
  public void missingiOSSize() {
    try {
      check("coordinate:\n" + "  groupId: com.github.jomof\n" + "  artifactId: boost\n" + "  version: 1.0.63-rev10\n" +
          "iOS:\n" + "  archives:\n" + "    - " + "file: bob.zip\n" + "      sha256: " +
          "97ce6635df1f44653a597343cd5757bb8b6b992beb3720f5fc761e3644bcbe7b\n");
      fail("Expected an exception");
    } catch (Exception e) {
      assertThat(e).hasMessage("Package 'com.github.jomof:boost:1.0.63-rev10' has missing ios.archive.size for 'bob.zip'");
    }
  }

  @Test
  public void checkAndroidSuccess() {
    check("coordinate:\n" + "  groupId: com.github.jomof\n" + "  artifactId: boost\n" + "  version: 1.0.63-rev10\n" +
        "android:\n" + "  archives:\n" + "    -" + " file: bob.zip\n" + "      sha256: " +
        "97ce6635df1f44653a597343cd5757bb8b6b992beb3720f5fc761e3644bcbe7b\n" + "      size: 192\n");
  }

  @Test
  public void checkiOSSuccess() {
    check("coordinate:\n" + "  groupId: com.github.jomof\n" + "  artifactId: boost\n" + "  version: 1.0.63-rev10\n" +
        "android:\n" + "  archives:\n" + "    -" + " file: bob.zip\n" + "      sha256: " +
        "97ce6635df1f44653a597343cd5757bb8b6b992beb3720f5fc761e3644bcbe7b\n" + "      size: 192\n");
  }

  private void check(@NotNull String content) {
    CDepManifestYml manifest = CDepManifestYmlUtils.convertStringToManifest(content);
    CDepManifestYmlUtils.checkManifestSanity(manifest);
  }

  @Test
  public void testAllResolvedManifests() throws Exception {
    Map<String, String> expected = new HashMap<>();
    expected.put("admob", "Archive com.github.jomof:firebase/admob:2.1.3-rev8 is missing include");
    expected.put("archiveMissingSha256", "Archive com.github.jomof:vectorial:0.0.0 is missing sha256");
    expected.put("sqliteLinuxMultiple",
        "Package 'com.github.jomof:sqlite:0.0.0' has multiple linux archives. Only one is allowed.");
    expected.put("archiveMissingSize", "Archive com.github.jomof:vectorial:0.0.0 is missing size or it is zero");
    expected.put("archiveMissingFile", "Archive com.github.jomof:vectorial:0.0.0 is missing file");
    expected.put("templateWithNullArchives", "Package 'com.github.jomof:firebase/app:${version}' "
        + "has malformed version, expected major.minor.point[-tweak] but there were no dots");
    expected.put("templateWithOnlyFile", "Package 'com.github.jomof:firebase/app:${version}' has "
        + "malformed version, expected major.minor.point[-tweak] but there were no dots");
    expected.put("indistinguishableAndroidArchives", "Android archive com.github.jomof:firebase/app:0.0.0 file archive2.zip is indistinguishable at build time from archive1.zip given the information in the manifest");
    expected.put("fuzz1", "Manifest was missing coordinate");
    expected.put("fuzz2", "Manifest was missing coordinate");
    boolean unexpectedFailure = false;
    for (ResolvedManifests.NamedManifest manifest : ResolvedManifests.all()) {
      String key = manifest.name;
      String expectedFailure = expected.get(key);
      try {
        CDepManifestYmlUtils.checkManifestSanity(manifest.resolved.cdepManifestYml);
        if (expectedFailure != null) {
          fail("Expected failure");
        }
      } catch (RuntimeException e) {
        if (!e.getMessage().equals(expectedFailure)) {
          //e.printStackTrace();
          System.out.printf("expected.put(\"%s\", \"%s\");\n", key, e.getMessage());
          unexpectedFailure = true;
        }
      }
    }
    if (unexpectedFailure) {
      throw new RuntimeException("Unexpected failures. See console.");
    }
  }

  @Test
  public void testTwoWayMergeSanity() throws Exception {
    Map<String, String> expected = new HashMap<>();
    expected.put("archiveMissingFile-archiveMissingFile", "Archive com.github.jomof:vectorial:0.0.0 is missing file");
    expected.put("archiveMissingSha256-archiveMissingSha256", "Archive com.github.jomof:vectorial:0.0.0 is missing sha256");
    expected.put("sqliteLinuxMultiple-sqliteLinuxMultiple",
        "Package 'com.github.jomof:sqlite:0.0.0' has multiple linux archives. Only one is allowed.");
    expected.put("sqliteLinuxMultiple-sqlite",
        "Package 'com.github.jomof:sqlite:0.0.0' has multiple linux archives. Only one is allowed.");
    expected.put("sqliteLinuxMultiple-singleABI",
        "Package 'com.github.jomof:sqlite:0.0.0' has multiple linux archives. Only one is allowed.");
    expected.put("sqliteLinuxMultiple-sqliteLinux",
        "Package 'com.github.jomof:sqlite:0.0.0' has multiple linux archives. Only one is allowed.");
    expected.put("archiveMissingSize-archiveMissingSize",
        "Archive com.github.jomof:vectorial:0.0.0 is missing size or it is zero");
    expected.put("admob-admob", "Archive com.github.jomof:firebase/admob:2.1.3-rev8 is missing include");
    expected.put("sqlite-sqliteLinuxMultiple",
        "Package 'com.github.jomof:sqlite:0.0.0' has multiple linux archives. Only one is allowed.");
    expected.put("singleABISqlite-singleABISqlite",
        "Package 'com.github.jomof:sqlite:3.16.2-rev45' contains multiple references to the same archive file " +
            "'sqlite-android-cxx-platform-12-armeabi.zip'");
    expected.put("singleABI-sqliteLinuxMultiple",
        "Package 'com.github.jomof:sqlite:0.0.0' has multiple linux archives. Only one is allowed.");
    expected.put("singleABI-singleABI",
        "Package 'com.github.jomof:sqlite:0.0.0' contains multiple references to the same archive file " +
            "'sqlite-android-cxx-platform-12-armeabi.zip'");
    expected.put("sqliteLinux-sqliteLinuxMultiple",
        "Package 'com.github.jomof:sqlite:0.0.0' has multiple linux archives. Only one is allowed.");
    expected.put("sqliteLinux-sqliteLinux",
        "Package 'com.github.jomof:sqlite:0.0.0' has multiple linux archives. Only one is allowed.");
    expected.put("sqliteiOS-sqliteiOS",
        "Package 'com.github.jomof:sqlite:3.16.2-rev33' contains multiple references to the same archive file " +
            "'sqlite-ios-platform-iPhoneOS-architecture-armv7-sdk-9.3.zip'");
    expected.put("templateWithNullArchives-templateWithNullArchives", "Package 'com.github.jomof:firebase/"
        + "app:${version}' has malformed version, expected major.minor.point[-tweak] but there were no dots");
    expected.put("templateWithNullArchives-templateWithOnlyFile", "Package 'com.github.jomof:"
        + "firebase/app:${version}' has malformed version, expected major.minor.point[-tweak] but there were no dots");
    expected.put("templateWithOnlyFile-templateWithNullArchives", "Package 'com.github.jomof:firebase/"
        + "app:${version}' has malformed version, expected major.minor.point[-tweak] but there were no dots");
    expected.put("templateWithOnlyFile-templateWithOnlyFile", "Package 'com.github.jomof:firebase/"
        + "app:${version}' has malformed version, expected major.minor.point[-tweak] but there were no dots");
    expected.put("sqlite-sqlite", "Android archive com.github.jomof:sqlite:0.0.0 file sqlite-android-cxx-platform-12.zip is indistinguishable at build time from sqlite-android-cxx-platform-12.zip given the information in the manifest");
    expected.put("sqlite-singleABI", "Android archive com.github.jomof:sqlite:0.0.0 file sqlite-android-cxx-platform-12-armeabi.zip is indistinguishable at build time from sqlite-android-cxx-platform-12.zip given the information in the manifest");
    expected.put("sqliteAndroid-sqliteAndroid", "Android archive com.github.jomof:sqlite:3.16.2-rev33 file sqlite-android-cxx-platform-12.zip is indistinguishable at build time from sqlite-android-cxx-platform-12.zip given the information in the manifest");
    expected.put("singleABI-sqlite", "Android archive com.github.jomof:sqlite:0.0.0 file sqlite-android-cxx-platform-12.zip is indistinguishable at build time from sqlite-android-cxx-platform-12-armeabi.zip given the information in the manifest");
    expected.put("singleABI-singleABI", "Android archive com.github.jomof:sqlite:0.0.0 file sqlite-android-cxx-platform-12-armeabi.zip is indistinguishable at build time from sqlite-android-cxx-platform-12-armeabi.zip given the information in the manifest");
    expected.put("indistinguishableAndroidArchives-indistinguishableAndroidArchives", "Android archive com.github.jomof:firebase/app:0.0.0 file archive2.zip is indistinguishable at build time from archive1.zip given the information in the manifest");
    expected.put("singleABISqlite-singleABISqlite", "Android archive com.github.jomof:sqlite:3.16.2-rev45 file sqlite-android-cxx-platform-12-armeabi.zip is indistinguishable at build time from sqlite-android-cxx-platform-12-armeabi.zip given the information in the manifest");
    expected.put("re2-re2", "Android archive com.github.jomof:re2:17.3.1-rev13 file re2-21-armeabi.zip is indistinguishable at build time from re2-21-armeabi.zip given the information in the manifest");
    expected.put("fuzz1-fuzz1", "Manifest was missing coordinate");
    expected.put("fuzz2-fuzz2", "Manifest was missing coordinate");
    expected.put("openssl-openssl", "Android archive com.github.jomof:openssl:1.0.1-e-rev6 file openssl-android-stlport-21-armeabi.zip is indistinguishable at build time from openssl-android-stlport-21-armeabi.zip given the information in the manifest");
    boolean somethingUnexpected = false;
    for (ResolvedManifests.NamedManifest manifest1 : ResolvedManifests.all()) {
      for (ResolvedManifests.NamedManifest manifest2 : ResolvedManifests.all()) {
        String key = manifest1.name + "-" + manifest2.name;
        String expectedFailure = expected.get(key);
        CDepManifestYml manifest;

        try {
          manifest = MergeCDepManifestYmls.merge(manifest1.resolved.cdepManifestYml, manifest2.resolved.cdepManifestYml);
        } catch (RuntimeException e) {
          continue;
        }

        try {
          CDepManifestYmlUtils.checkManifestSanity(manifest);
          if (expectedFailure != null) {
            TestCase.fail("Expected a failure.");
          }
        } catch (RuntimeException e) {
          String actual = e.getMessage();
          if (!actual.equals(expectedFailure)) {
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
  public void readPartial() throws IOException {
    // Make sure we can read an incomplete specification for 'fullfill' scenario
    CDepManifestYml partial = CDepManifestYmlUtils.convertStringToManifest(
        FileUtils.readAllText(new File("../third_party/stb/cdep/cdep-manifest-divide.yml")));
  }

  private static class CoverConstructor extends CDepManifestYmlUtils {

  }
}
