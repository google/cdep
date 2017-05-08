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

import io.cdep.cdep.utils.CDepManifestYmlUtils;
import io.cdep.cdep.yml.cdepmanifest.AndroidABI;
import io.cdep.cdep.yml.cdepmanifest.CDepManifestYml;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class TestFillMissingFieldsBasedOnFilepath {

  @Test
  public void testABI() {
    String body = "coordinate:\n"
        + "  groupId: com.github.jomof\n"
        + "  artifactId: firebase/app\n"
        + "  version: ${version}\n"
        + "interfaces:\n"
        + "  headers:\n"
        + "    file: ${source}/include/firebase/app.h -> firebase/app.h\n"
        + "\n"
        + "android:\n"
        + "  archives:\n"
        + "  - file: firebase_cpp_sdk/libs/android/arm64-v8a/stlport/libapp.a\n"
        + "  - file: firebase_cpp_sdk/libs/android/armeabi-v7a/gnustl/libapp.a\n"
        + "  - file: firebase_cpp_sdk/libs/android/armeabi/c++/libapp.a\n"
        + "  - file: firebase_cpp_sdk/libs/android/mips64/cxx/libapp.a\n"
        + "  - file: firebase_cpp_sdk/libs/android/mips/c++/libapp.a\n"
        + "  - file: firebase_cpp_sdk/libs/android/x86_64/c++/libapp.a\n"
        + "  - file: firebase_cpp_sdk/libs/android/x86/c++/libapp.a\n";

    CDepManifestYml manifest = CDepManifestYmlUtils.convertStringToManifest(body);
    manifest = new FillMissingFieldsBasedOnFilepathRewriter().visitCDepManifestYml(manifest);
    assertThat(manifest.android.archives[0].abi).isEqualTo(AndroidABI.ARM64_V8A);
    assertThat(manifest.android.archives[0].runtime).isEqualTo("stlport");
    assertThat(manifest.android.archives[0].libs[0]).isEqualTo("libapp.a");
    assertThat(manifest.android.archives[1].abi).isEqualTo(AndroidABI.ARMEABI_V7A);
    assertThat(manifest.android.archives[1].runtime).isEqualTo("gnustl");
    assertThat(manifest.android.archives[2].abi).isEqualTo(AndroidABI.ARMEABI);
    assertThat(manifest.android.archives[3].runtime).isEqualTo("c++");
    assertThat(manifest.android.archives[3].abi).isEqualTo(AndroidABI.MIPS64);
    assertThat(manifest.android.archives[4].runtime).isEqualTo("c++");
    assertThat(manifest.android.archives[4].abi).isEqualTo(AndroidABI.MIPS);
    assertThat(manifest.android.archives[5].abi).isEqualTo(AndroidABI.X86_64);
    assertThat(manifest.android.archives[6].abi).isEqualTo(AndroidABI.X86);
  }

  @Test
  public void testPlatform() {
    String body = "coordinate:\n"
        + "  groupId: com.github.jomof\n"
        + "  artifactId: firebase/app\n"
        + "  version: ${version}\n"
        + "interfaces:\n"
        + "  headers:\n"
        + "    file: ${source}/include/firebase/app.h -> firebase/app.h\n"
        + "\n"
        + "android:\n"
        + "  archives:\n"
        + "  - file: firebase_cpp_sdk/libs/android/arm64-v8a/c++/libapp.a\n"
        + "  - file: firebase_cpp_sdk/libs/android-21/arm64-v8a/c++/libapp.a\n"
        + "  - file: firebase_cpp_sdk/libs/android-9/arm64-v8a/c++/libapp.a\n";

    CDepManifestYml manifest = CDepManifestYmlUtils.convertStringToManifest(body);
    manifest = new FillMissingFieldsBasedOnFilepathRewriter().visitCDepManifestYml(manifest);
    assertThat(manifest.android.archives[0].platform).isEqualTo("12");
    assertThat(manifest.android.archives[1].platform).isEqualTo("21");
    assertThat(manifest.android.archives[2].platform).isEqualTo("9");
  }
}