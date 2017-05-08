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
package io.cdep.cdep;

import static com.google.common.truth.Truth.assertThat;

import io.cdep.annotations.NotNull;
import io.cdep.cdep.resolver.ResolvedManifest;
import io.cdep.cdep.utils.CDepManifestYmlUtils;
import io.cdep.cdep.utils.ReflectionUtils;
import io.cdep.cdep.yml.cdepmanifest.CDepManifestYml;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

@SuppressWarnings("unused")
public class ResolvedManifests {
  @NotNull
  public static TestManifest openssl() throws MalformedURLException {
    return getResolvedManifest("coordinate:\n"
        + "  groupId: com.github.jomof\n"
        + "  artifactId: openssl\n"
        + "  version: 1.0.1-e-rev6\n"
        + "interfaces:\n"
        + "  headers:\n"
        + "    file: openssl-headers.zip\n"
        + "    sha256: fb18d3b17d7841e43b87429ca5fad3d6b4029bc1bfd09fd69f1468ef4aae5e2f\n"
        + "    size: 378330\n"
        + "    include: include\n"
        + "android:\n"
        + "  archives:\n"
        + "  - file: openssl-android-stlport-21-armeabi.zip\n"
        + "    sha256: dc7d0fe098a4d483cb8e4b92980f5ec5add7a8923c39adcd07f30b23de9a4692\n"
        + "    size: 5006726\n"
        + "    runtime: stlport\n"
        + "    platform: 21\n"
        + "    abi: armeabi\n"
        + "    libs: [libssl.a, libcrypto.a]\n"
        + "  - file: openssl-android-stlport-21-armeabi-v7a.zip\n"
        + "    sha256: 90c2b1834967b6f325679df10f3632a3ab925b761701c15e80303f37a17898fb\n"
        + "    size: 4959317\n"
        + "    runtime: stlport\n"
        + "    platform: 21\n"
        + "    abi: armeabi-v7a\n"
        + "    libs: [libssl.a, libcrypto.a]\n"
        + "  - file: openssl-android-stlport-21-x86_64.zip\n"
        + "    sha256: e7d2230290b1d51b68689b90fd1c4e5e45f1c5fcdb6479c44213ef0d0f6c5e9d\n"
        + "    size: 4754330\n"
        + "    runtime: stlport\n"
        + "    platform: 21\n"
        + "    abi: x86_64\n"
        + "    libs: [libcrypto.a, libssl.a]\n"
        + "  - file: openssl-android-stlport-21-arm64-v8a.zip\n"
        + "    sha256: de4c6de09eda3e97621455ee27785563ca46ce5dcf45bfdb7ea879cc3dd478cd\n"
        + "    size: 4824256\n"
        + "    runtime: stlport\n"
        + "    platform: 21\n"
        + "    abi: arm64-v8a\n"
        + "    libs: [libssl.a, libcrypto.a]\n"
        + "  - file: openssl-android-stlport-21-x86.zip\n"
        + "    sha256: a61db2f3ec8f5d03a8b1a7512aae433d1197ff03c72ea6f4c3d0c072283209d3\n"
        + "    size: 4986803\n"
        + "    runtime: stlport\n"
        + "    platform: 21\n"
        + "    abi: x86\n"
        + "    libs: [libssl.a, libcrypto.a]\n"
        + "  - file: openssl-android-stlport-12-armeabi.zip\n"
        + "    sha256: 0d44ea854dafd90da3d32b092e465e75daa7a12aefe895fc8387575a39f36a71\n"
        + "    size: 5002347\n"
        + "    runtime: stlport\n"
        + "    platform: 12\n"
        + "    abi: armeabi\n"
        + "    libs: [libcrypto.a, libssl.a]\n"
        + "  - file: openssl-android-stlport-12-armeabi-v7a.zip\n"
        + "    sha256: 8b4b4dfbae00ac6d46230dec55e7ade3d969e557f01ea43494d1d08bd66d558e\n"
        + "    size: 4955003\n"
        + "    runtime: stlport\n"
        + "    platform: 12\n"
        + "    abi: armeabi-v7a\n"
        + "    libs: [libssl.a, libcrypto.a]\n"
        + "  - file: openssl-android-stlport-12-x86.zip\n"
        + "    sha256: 09b626b21de90597c2776d4f210db6a254400eda1220dcaf188d2b6edae5dab0\n"
        + "    size: 4982008\n"
        + "    runtime: stlport\n"
        + "    platform: 12\n"
        + "    abi: x86\n"
        + "    libs: [libcrypto.a, libssl.a]\n"
        + "  - file: openssl-android-cpp-21-armeabi.zip\n"
        + "    sha256: f50e0adc09aae689f8ea469bb5f96f274a9974682ed8aa3c26aa9517a9a7e052\n"
        + "    size: 5006390\n"
        + "    runtime: c++\n"
        + "    platform: 21\n"
        + "    abi: armeabi\n"
        + "    libs: [libssl.a, libcrypto.a]\n"
        + "  - file: openssl-android-cpp-21-armeabi-v7a.zip\n"
        + "    sha256: 362817ff58ea6e367e13e2af1aaf88a7bde0fb88a7a730c43c17ea385d008b51\n"
        + "    size: 4958593\n"
        + "    runtime: c++\n"
        + "    platform: 21\n"
        + "    abi: armeabi-v7a\n"
        + "    libs: [libssl.a, libcrypto.a]\n"
        + "  - file: openssl-android-cpp-21-x86_64.zip\n"
        + "    sha256: 218ac4f0aabb08d5e7eea5efaec321e09eb82bd5dc76e67c98de7775291212cb\n"
        + "    size: 4753942\n"
        + "    runtime: c++\n"
        + "    platform: 21\n"
        + "    abi: x86_64\n"
        + "    libs: [libcrypto.a, libssl.a]\n"
        + "  - file: openssl-android-cpp-21-arm64-v8a.zip\n"
        + "    sha256: 66141d96c9d99caf5dcb99d7812f6cbc19f3b71f4187012001e99e50309dd370\n"
        + "    size: 4823609\n"
        + "    runtime: c++\n"
        + "    platform: 21\n"
        + "    abi: arm64-v8a\n"
        + "    libs: [libssl.a, libcrypto.a]\n"
        + "  - file: openssl-android-cpp-21-x86.zip\n"
        + "    sha256: 1b5b59b18a02b5f0323e59ba147ad56361cb6f51a1b538883d0110eddd16a837\n"
        + "    size: 4986158\n"
        + "    runtime: c++\n"
        + "    platform: 21\n"
        + "    abi: x86\n"
        + "    libs: [libssl.a, libcrypto.a]\n"
        + "  - file: openssl-android-cpp-12-armeabi.zip\n"
        + "    sha256: 1945b9945864fd895f36382eaf4eef9027db21c447e9820fd08f20ccb193bbcd\n"
        + "    size: 5001663\n"
        + "    runtime: c++\n"
        + "    platform: 12\n"
        + "    abi: armeabi\n"
        + "    libs: [libcrypto.a, libssl.a]\n"
        + "  - file: openssl-android-cpp-12-armeabi-v7a.zip\n"
        + "    sha256: 6b025b79b43309fc76deebfe1b4c48e41eabbf69096ba75f4f9de16707d24158\n"
        + "    size: 4954275\n"
        + "    runtime: c++\n"
        + "    platform: 12\n"
        + "    abi: armeabi-v7a\n"
        + "    libs: [libssl.a, libcrypto.a]\n"
        + "  - file: openssl-android-cpp-12-x86.zip\n"
        + "    sha256: e21ffbb003c5a8660736577e0853e686ea5e31e70662e87b32d2139dc53951ca\n"
        + "    size: 4981521\n"
        + "    runtime: c++\n"
        + "    platform: 12\n"
        + "    abi: x86\n"
        + "    libs: [libcrypto.a, libssl.a]\n"
        + "  - file: openssl-android-gnustl-21-armeabi.zip\n"
        + "    sha256: 1a46fbdb72102743a31ada1fed19c17726548797d978f8e4eadf041e0b6acf41\n"
        + "    size: 5006611\n"
        + "    runtime: gnustl\n"
        + "    platform: 21\n"
        + "    abi: armeabi\n"
        + "    libs: [libcrypto.a, libssl.a]\n"
        + "  - file: openssl-android-gnustl-21-armeabi-v7a.zip\n"
        + "    sha256: 036b3c7e284d30a6fb51c7865cf14a650bfec7173d2c3e9604cd814f1011e520\n"
        + "    size: 4959263\n"
        + "    runtime: gnustl\n"
        + "    platform: 21\n"
        + "    abi: armeabi-v7a\n"
        + "    libs: [libssl.a, libcrypto.a]\n"
        + "  - file: openssl-android-gnustl-21-x86_64.zip\n"
        + "    sha256: 70d025b0c44ac0b60f5b7398e006ff270ef731081eec8a014eac341a5626cbb1\n"
        + "    size: 4754465\n"
        + "    runtime: gnustl\n"
        + "    platform: 21\n"
        + "    abi: x86_64\n"
        + "    libs: [libcrypto.a, libssl.a]\n"
        + "  - file: openssl-android-gnustl-21-arm64-v8a.zip\n"
        + "    sha256: b4ac7771036cc278334b060f793510124889a48d36871117ea54c2b3049b2608\n"
        + "    size: 4823863\n"
        + "    runtime: gnustl\n"
        + "    platform: 21\n"
        + "    abi: arm64-v8a\n"
        + "    libs: [libssl.a, libcrypto.a]\n"
        + "  - file: openssl-android-gnustl-21-x86.zip\n"
        + "    sha256: 590f1a8de674076aad266815ddf7b27301939d82f77855112cf4ef224fb6cc83\n"
        + "    size: 4986506\n"
        + "    runtime: gnustl\n"
        + "    platform: 21\n"
        + "    abi: x86\n"
        + "    libs: [libssl.a, libcrypto.a]\n"
        + "  - file: openssl-android-gnustl-12-armeabi.zip\n"
        + "    sha256: fd3d77e78dcf9c21d2f3069f9e22619b00c849baf459a6ec764c1afcad42df08\n"
        + "    size: 5002373\n"
        + "    runtime: gnustl\n"
        + "    platform: 12\n"
        + "    abi: armeabi\n"
        + "    libs: [libssl.a, libcrypto.a]\n"
        + "  - file: openssl-android-gnustl-12-armeabi-v7a.zip\n"
        + "    sha256: 35f5fd5133d1aa78afa254040bce3199bc22cb1ece54c062d1186928e7345215\n"
        + "    size: 4954985\n"
        + "    runtime: gnustl\n"
        + "    platform: 12\n"
        + "    abi: armeabi-v7a\n"
        + "    libs: [libssl.a, libcrypto.a]\n"
        + "  - file: openssl-android-gnustl-12-x86.zip\n"
        + "    sha256: 35d8a30367494a2df5f7c97094d144a433c91105ad53d689a412ec1f65214945\n"
        + "    size: 4981869\n"
        + "    runtime: gnustl\n"
        + "    platform: 12\n"
        + "    abi: x86\n"
        + "    libs: [libssl.a, libcrypto.a]\n"
        + "example: |\n"
        + "  #include \"openssl/bio.h\"\n"
        + "  #include \"openssl/ssl.h\"\n"
        + "  #include \"openssl/err.h\"\n"
        + "  \n"
        + "  void example() {\n"
        + "    SSL_load_error_strings();\n"
        + "    ERR_load_BIO_strings();\n"
        + "    OpenSSL_add_all_algorithms();\n"
        + "  }");
  }

  @NotNull
  public static TestManifest fuzz2() throws MalformedURLException {
    return getResolvedManifest("coordinate:\n" +
        "  groupId: \n" +
        "  artifactId: \n" +
        "  version: \n" +
        "interfaces:\n" +
        "  headers:\n" +
        "    file: \",S`&|[x0q;J.$9D#P6FUDG>+&69ZXG\"\n" +
        "    sha256: 1234\n" +
        "    size: 100 \n" +
        "    include: Y81Z-%q{iyu9rVk)4w7VHr9T~-8}\n" +
        "    requires: [cxx_attributes, cxx_attributes, cxx_delegating_constructors, cxx_digit_separators, " +
        "cxx_nonstatic_member_init, cxx_raw_string_literals, cxx_raw_string_literals]\n");
  }

  @NotNull
  public static TestManifest fuzz1() throws MalformedURLException {
    return getResolvedManifest("coordinate:\n" +
        "  groupId: \n" +
        "  artifactId: \n" +
        "  version: \n" +
        "dependencies:\n" +
        "  - compile: \n");
  }

  @NotNull
  public static TestManifest re2() throws MalformedURLException {
    return getResolvedManifest("coordinate:\n" +
        "  groupId: com.github.jomof\n" +
        "  artifactId: re2\n" +
        "  version: 17.3.1-rev13\n" +
        "interfaces:\n" +
        "  headers:\n" +
        "    file: re2-header.zip\n" +
        "    sha256: 4bce31b6e319021a1cc2838573c4855b94a222e2004fef5fb6d067f8f84ad7d4\n" +
        "    size: 43964\n" +
        "    include: include\n" +
        "    requires: [cxx_deleted_functions, cxx_variadic_templates]\n" +
        "android:\n" +
        "  dependencies:\n" +
        "  archives:\n" +
        "    - file: re2-21-armeabi.zip\n" +
        "      sha256: 0f84538e027c63874175b80cc9b69845d2b6fe825446459188743f02ba881eba\n" +
        "      size: 3063079\n" +
        "      platform: 21\n" +
        "      abi: armeabi\n" +
        "      lib: libre2.a\n" +
        "    - file: re2-21-armeabi-v7a.zip\n" +
        "      sha256: 2a6abd472b20b0543a782b638ae6a56ea76c884b685c33b2d701ecf6c19d2a06\n" +
        "      size: 3027838\n" +
        "      platform: 21\n" +
        "      abi: armeabi-v7a\n" +
        "      lib: libre2.a\n" +
        "    - file: re2-21-x86_64.zip\n" +
        "      sha256: b5b601063674cd1f9c72dc865ce0fe2d9e293e4acac47e78a1dd95046d015d9c\n" +
        "      size: 3052728\n" +
        "      platform: 21\n" +
        "      abi: x86_64\n" +
        "      lib: libre2.a\n" +
        "    - file: re2-21-arm64-v8a.zip\n" +
        "      sha256: c103d5164bbd213cffc60f54b1f404e6af9988e05b36922a011c600624779d6a\n" +
        "      size: 3059374\n" +
        "      platform: 21\n" +
        "      abi: arm64-v8a\n" +
        "      lib: libre2.a\n" +
        "    - file: re2-21-x86.zip\n" +
        "      sha256: ef017da852091374cbed55f6a99749146ec9bff55032e940998e8e285a8fc7f9\n" +
        "      size: 3089600\n" +
        "      platform: 21\n" +
        "      abi: x86\n" +
        "      lib: libre2.a\n" +
        "    - file: re2-12-armeabi.zip\n" +
        "      sha256: d7f2a60586160b1429e421fbdfcb765fbe9a5ccfdfb63c40955b9178c79f2572\n" +
        "      size: 3062546\n" +
        "      platform: 12\n" +
        "      abi: armeabi\n" +
        "      lib: libre2.a\n" +
        "    - file: re2-12-armeabi-v7a.zip\n" +
        "      sha256: 28f653d1d2e28ca32d66fd22462eb50b9502351b3102934b15d5a020c215d2e7\n" +
        "      size: 3027537\n" +
        "      platform: 12\n" +
        "      abi: armeabi-v7a\n" +
        "      lib: libre2.a\n" +
        "    - file: re2-12-x86.zip\n" +
        "      sha256: c9c5c26c8e4a2c214ee2d1491bbd35063ca828f5593164683cfea30948916087\n" +
        "      size: 3089626\n" +
        "      platform: 12\n" +
        "      abi: x86\n" +
        "      lib: libre2.a\n" +
        "example: |\n" +
        "  #include <re2/re2.h>\n" +
        "  void test() {\n" +
        "    RE2::FullMatch(\"hello\", \"h.*o\");\n" +
        "  }\n" +
        "  \n" +
        "   \n");
  }

  @NotNull
  public static TestManifest simpleRequires() throws MalformedURLException {
    return getResolvedManifest("coordinate:\n" +
        "  groupId: com.github.jomof\n" +
        "  artifactId: firebase/app\n" +
        "  version: 2.1.3-rev22\n" +
        "interfaces:\n" +
        "  headers:\n" +
        "    file: firebase-app-header.zip\n" +
        "    sha256: 18c2f18957a0cb2a53bca193afa978f2a8d94d33b7052d3c4383fa8e8d579535\n" +
        "    size: 91607\n" +
        "    include: include\n" +
        "    requires: [cxx_auto_type]\n");
  }

  @NotNull
  public static TestManifest multipleRequires() throws MalformedURLException {
    return getResolvedManifest("coordinate:\n" +
        "  groupId: com.github.jomof\n" +
        "  artifactId: firebase/app\n" +
        "  version: 2.1.3-rev22\n" +
        "interfaces:\n" +
        "  headers:\n" +
        "    file: firebase-app-header.zip\n" +
        "    sha256: 18c2f18957a0cb2a53bca193afa978f2a8d94d33b7052d3c4383fa8e8d579535\n" +
        "    size: 91607\n" +
        "    include: include\n" +
        "    requires: [cxx_auto_type, cxx_decltype]\n");
  }

  @NotNull
  public static TestManifest indistinguishableAndroidArchives() throws MalformedURLException {
    return getResolvedManifest(
        "coordinate:\n"
            + "  groupId: com.github.jomof\n"
            + "  artifactId: firebase/app\n"
            + "  version: 0.0.0\n"
            + "interfaces:\n"
            + "  headers:\n"
            + "    file: archive0.zip\n"
            + "    sha256: ec747544670ffec2771af8a78d59200a440b5d511ed5c7b03fd5133665980e19\n"
            + "    size: 3504\n"
            + "    include: include\n"
            + "android:\n"
            + "  dependencies:\n"
            + "  archives:\n"
            + "    - file: archive1.zip\n"
            + "      sha256: b64fbfe3fb87b873eec07cfd9aefbe77244914457d98bd3a12f8dcc6f5179673\n"
            + "      size: 1093008\n"
            + "      runtime: c++\n"
            + "      abi: arm64-v8a\n"
            + "      lib: libapp.a\n"
            + "    - file: archive2.zip\n"
            + "      sha256: c10494931ddb45e9c47e9c64249b78b0ce58e9a0d28f5eb15155fd098861841a\n"
            + "      size: 968470\n"
            + "      runtime: c++\n"
            + "      abi: arm64-v8a\n"
            + "      lib: libapp.a\n");
  }

  @NotNull
  public static TestManifest templateWithOnlyFile() throws MalformedURLException {
    return getResolvedManifest("coordinate:\n"
        + "  groupId: com.github.jomof\n"
        + "  artifactId: firebase/app\n"
        + "  version: ${version}\n"
        + "interfaces:\n"
        + "  headers:\n"
        + "    file: ${source}/include/firebase/app.h -> firebase/app.h\n"
        + "\n"
        + "android:\n"
        + "  archives:\n"
        + "  - file: firebase_cpp_sdk/libs/android/arm64-v8a/c++/libapp.a\n");
  }

  @NotNull
  public static TestManifest templateWithNullArchives() throws MalformedURLException {
    return getResolvedManifest("coordinate:\n"
        + "  groupId: com.github.jomof\n"
        + "  artifactId: firebase/app\n"
        + "  version: ${version}\n"
        + "interfaces:\n"
        + "  headers:\n"
        + "    file: ${source}/include/firebase/app.h -> firebase/app.h\n"
        + "\n"
        + "android:\n"
        + "  archives:\n");
  }

  @NotNull
  public static TestManifest singleABISqlite() throws MalformedURLException {
    return getResolvedManifest("coordinate:\n" +
        "  groupId: com.github.jomof\n" +
        "  artifactId: sqlite\n" +
        "  version: 3.16.2-rev45\n" +
        "android:\n" +
        "  dependencies:\n" +
        "  archives:\n" +
        "    - file: sqlite-android-cxx-platform-12-armeabi.zip\n" +
        "      sha256: 5f2d0311c6dfabcd6674b0e2c0e5c8f693d6b9f2590b5dfa0c5c26c3fa129de7\n" +
        "      size: 980275\n" +
        "      ndk: r13b\n" +
        "      runtime: c++\n" +
        "      platform: 12\n" +
        "      abi: armeabi\n" +
        "      include: include\n" +
        "      lib: libsqlite.a\n" +
        "    - file: sqlite-android-cxx-platform-12-armeabi-v7a.zip\n" +
        "      sha256: 8a66e88c571adf7cf025e7e3b350e67af4b3bf7b5e9327d78cb0c7621e882aa5\n" +
        "      size: 944437\n" +
        "      ndk: r13b\n" +
        "      runtime: c++\n" +
        "      platform: 12\n" +
        "      abi: armeabi-v7a\n" +
        "      include: include\n" +
        "      lib: libsqlite.a\n" +
        "    - file: sqlite-android-cxx-platform-12-x86.zip\n" +
        "      sha256: 1701d88c4a1fae2df73ba16e0696a6bcf66de35590580279c2c3acaefe864dd2\n" +
        "      size: 1010262\n" +
        "      ndk: r13b\n" +
        "      runtime: c++\n" +
        "      platform: 12\n" +
        "      abi: x86\n" +
        "      include: include\n" +
        "      lib: libsqlite.a\n" +
        "    - file: sqlite-android-gnustl-platform-12-armeabi.zip\n" +
        "      sha256: e348a8256d09b622264fde6248b2a128359f96683b2051d87607cd326d52f207\n" +
        "      size: 980305\n" +
        "      ndk: r13b\n" +
        "      runtime: gnustl\n" +
        "      platform: 12\n" +
        "      abi: armeabi\n" +
        "      include: include\n" +
        "      lib: libsqlite.a\n" +
        "    - file: sqlite-android-gnustl-platform-12-armeabi-v7a.zip\n" +
        "      sha256: a3a24ac0a72bc98f1469a6b741540d61e251418f1b84e022e7826561ee5ddb74\n" +
        "      size: 944458\n" +
        "      ndk: r13b\n" +
        "      runtime: gnustl\n" +
        "      platform: 12\n" +
        "      abi: armeabi-v7a\n" +
        "      include: include\n" +
        "      lib: libsqlite.a\n" +
        "    - file: sqlite-android-gnustl-platform-12-x86.zip\n" +
        "      sha256: fa75a9846ed1e13ae321a6b0c5aa5630e3c7d446bb4b2a8452b7909a375e2ce2\n" +
        "      size: 1010292\n" +
        "      ndk: r13b\n" +
        "      runtime: gnustl\n" +
        "      platform: 12\n" +
        "      abi: x86\n" +
        "      include: include\n" +
        "      lib: libsqlite.a\n" +
        "    - file: sqlite-android-stlport-platform-12-armeabi.zip\n" +
        "      sha256: 2fcd7d13946dc9460e9a982b23a3ec58f72c9c85e5419e6d2b2195b189530c93\n" +
        "      size: 980299\n" +
        "      ndk: r13b\n" +
        "      runtime: stlport\n" +
        "      platform: 12\n" +
        "      abi: armeabi\n" +
        "      include: include\n" +
        "      lib: libsqlite.a\n" +
        "    - file: sqlite-android-stlport-platform-12-armeabi-v7a.zip\n" +
        "      sha256: 94ef5df595f1d12fbf9a89d42ca400ec5029c593ac8ce896fb2ea5a0f689feb5\n" +
        "      size: 944463\n" +
        "      ndk: r13b\n" +
        "      runtime: stlport\n" +
        "      platform: 12\n" +
        "      abi: armeabi-v7a\n" +
        "      include: include\n" +
        "      lib: libsqlite.a\n" +
        "    - file: sqlite-android-stlport-platform-12-x86.zip\n" +
        "      sha256: f8a4ac89c2109716057155fdd03069928c4500b89e5d1df5a419c9a2c84513a4\n" +
        "      size: 1010275\n" +
        "      ndk: r13b\n" +
        "      runtime: stlport\n" +
        "      platform: 12\n" +
        "      abi: x86\n" +
        "      include: include\n" +
        "      lib: libsqlite.a\n" +
        "    - file: sqlite-android-cxx-platform-21-armeabi.zip\n" +
        "      sha256: a71ccf0b3e6e032aa639c40422c0acffb97f9db2ef98770b7c68d8b451877f12\n" +
        "      size: 980483\n" +
        "      ndk: r13b\n" +
        "      runtime: c++\n" +
        "      platform: 21\n" +
        "      abi: armeabi\n" +
        "      include: include\n" +
        "      lib: libsqlite.a\n" +
        "    - file: sqlite-android-cxx-platform-21-armeabi-v7a.zip\n" +
        "      sha256: d6e9e4a2d993fa513b06a98040fb8ed0d6d16da67a662c65faf6fa42977de3cd\n" +
        "      size: 944616\n" +
        "      ndk: r13b\n" +
        "      runtime: c++\n" +
        "      platform: 21\n" +
        "      abi: armeabi-v7a\n" +
        "      include: include\n" +
        "      lib: libsqlite.a\n" +
        "    - file: sqlite-android-cxx-platform-21-arm64-v8a.zip\n" +
        "      sha256: 1628df08f794311a80ba2baaf51f29b3a7c2dbf5348787ee20b8da5aa7f8daf1\n" +
        "      size: 954374\n" +
        "      ndk: r13b\n" +
        "      runtime: c++\n" +
        "      platform: 21\n" +
        "      abi: arm64-v8a\n" +
        "      include: include\n" +
        "      lib: libsqlite.a\n" +
        "    - file: sqlite-android-cxx-platform-21-x86.zip\n" +
        "      sha256: 60853a52145018fd15ce2578dd9c824475a60fd7ab8fb2b966558d41289de088\n" +
        "      size: 1010369\n" +
        "      ndk: r13b\n" +
        "      runtime: c++\n" +
        "      platform: 21\n" +
        "      abi: x86\n" +
        "      include: include\n" +
        "      lib: libsqlite.a\n" +
        "    - file: sqlite-android-cxx-platform-21-x86_64.zip\n" +
        "      sha256: aab9578c12e2e3f54f3be3ad44f7a5387512749f6f1b5125586bc57106b45bd3\n" +
        "      size: 974451\n" +
        "      ndk: r13b\n" +
        "      runtime: c++\n" +
        "      platform: 21\n" +
        "      abi: x86_64\n" +
        "      include: include\n" +
        "      lib: libsqlite.a\n" +
        "    - file: sqlite-android-gnustl-platform-21-armeabi.zip\n" +
        "      sha256: 56d5816ff4931bd467b34fac98dd4dab301f01d3ca3c91fbc61679f567710297\n" +
        "      size: 980475\n" +
        "      ndk: r13b\n" +
        "      runtime: gnustl\n" +
        "      platform: 21\n" +
        "      abi: armeabi\n" +
        "      include: include\n" +
        "      lib: libsqlite.a\n" +
        "    - file: sqlite-android-gnustl-platform-21-armeabi-v7a.zip\n" +
        "      sha256: 5d52d0db30c916190f4feba0449dd57ff1cb1081a2358f92828a356fb486c42b\n" +
        "      size: 944611\n" +
        "      ndk: r13b\n" +
        "      runtime: gnustl\n" +
        "      platform: 21\n" +
        "      abi: armeabi-v7a\n" +
        "      include: include\n" +
        "      lib: libsqlite.a\n" +
        "    - file: sqlite-android-gnustl-platform-21-arm64-v8a.zip\n" +
        "      sha256: 00dd1e16099392cb9edeec9f8d6e1a67fbe45a42fc696aa95e3b309100d5d50e\n" +
        "      size: 954355\n" +
        "      ndk: r13b\n" +
        "      runtime: gnustl\n" +
        "      platform: 21\n" +
        "      abi: arm64-v8a\n" +
        "      include: include\n" +
        "      lib: libsqlite.a\n" +
        "    - file: sqlite-android-gnustl-platform-21-x86.zip\n" +
        "      sha256: a964724e9443a69ce61692d2c356d6b9466eefab1d52689551970826636dab74\n" +
        "      size: 1010346\n" +
        "      ndk: r13b\n" +
        "      runtime: gnustl\n" +
        "      platform: 21\n" +
        "      abi: x86\n" +
        "      include: include\n" +
        "      lib: libsqlite.a\n" +
        "    - file: sqlite-android-gnustl-platform-21-x86_64.zip\n" +
        "      sha256: 871207a0b606279675ff4426937f6ef34ad5f39dc4a083f28be72993f92a75ce\n" +
        "      size: 974455\n" +
        "      ndk: r13b\n" +
        "      runtime: gnustl\n" +
        "      platform: 21\n" +
        "      abi: x86_64\n" +
        "      include: include\n" +
        "      lib: libsqlite.a\n" +
        "    - file: sqlite-android-stlport-platform-21-armeabi.zip\n" +
        "      sha256: 0d26f166c232fd5c0735bfaa14ae8350301b5258880d813dba2be6def4c2e862\n" +
        "      size: 980482\n" +
        "      ndk: r13b\n" +
        "      runtime: stlport\n" +
        "      platform: 21\n" +
        "      abi: armeabi\n" +
        "      include: include\n" +
        "      lib: libsqlite.a\n" +
        "    - file: sqlite-android-stlport-platform-21-armeabi-v7a.zip\n" +
        "      sha256: 49b6282523580466ab508ffd9d086c04a827d42918dae8c7daa440e3936fb439\n" +
        "      size: 944624\n" +
        "      ndk: r13b\n" +
        "      runtime: stlport\n" +
        "      platform: 21\n" +
        "      abi: armeabi-v7a\n" +
        "      include: include\n" +
        "      lib: libsqlite.a\n" +
        "    - file: sqlite-android-stlport-platform-21-arm64-v8a.zip\n" +
        "      sha256: ef3905ef9a484e5772380b9d2b5a3e2875e243b9201517f45ce8dd07497789a8\n" +
        "      size: 954359\n" +
        "      ndk: r13b\n" +
        "      runtime: stlport\n" +
        "      platform: 21\n" +
        "      abi: arm64-v8a\n" +
        "      include: include\n" +
        "      lib: libsqlite.a\n" +
        "    - file: sqlite-android-stlport-platform-21-x86.zip\n" +
        "      sha256: 5e3518cd7a2687afd05dd3d4e64837ab7b970c64620e08e079707b9e3dcbbb95\n" +
        "      size: 1010368\n" +
        "      ndk: r13b\n" +
        "      runtime: stlport\n" +
        "      platform: 21\n" +
        "      abi: x86\n" +
        "      include: include\n" +
        "      lib: libsqlite.a\n" +
        "    - file: sqlite-android-stlport-platform-21-x86_64.zip\n" +
        "      sha256: 3d794d8a9d9cd082b1bccc5d0ccc36704b5c9fa514c6c1512c850f764c981fda\n" +
        "      size: 974553\n" +
        "      ndk: r13b\n" +
        "      runtime: stlport\n" +
        "      platform: 21\n" +
        "      abi: x86_64\n" +
        "      include: include\n" +
        "      lib: libsqlite.a\n" +
        "iOS:\n" +
        "  archives:\n" +
        "    - file: sqlite-ios-platform-iPhoneOS-architecture-armv7-sdk-9.3.zip\n" +
        "      sha256: a50eed482b83c4cbe03d25b501b68b80a374b14aa36fb1cd19f21d03500dbe2e\n" +
        "      size: 514132\n" +
        "      platform: iPhoneOS\n" +
        "      architecture: armv7\n" +
        "      sdk: 9.3\n" +
        "      include: include\n" +
        "      lib: libsqlite.a\n" +
        "    - file: sqlite-ios-platform-iPhoneOS-architecture-armv7s-sdk-9.3.zip\n" +
        "      sha256: 4c9484799d62711c670469e0369e280311e5297a278994b84d3c746d555c3e25\n" +
        "      size: 514253\n" +
        "      platform: iPhoneOS\n" +
        "      architecture: armv7s\n" +
        "      sdk: 9.3\n" +
        "      include: include\n" +
        "      lib: libsqlite.a\n" +
        "    - file: sqlite-ios-platform-iPhoneOS-architecture-arm64-sdk-9.3.zip\n" +
        "      sha256: 5ec985dbfb3f988a35afb665a12d1e80deb26e1d5684a2e2b6c8bca9ce32273e\n" +
        "      size: 529340\n" +
        "      platform: iPhoneOS\n" +
        "      architecture: arm64\n" +
        "      sdk: 9.3\n" +
        "      include: include\n" +
        "      lib: libsqlite.a\n" +
        "    - file: sqlite-ios-platform-iPhoneSimulator-architecture-i386-sdk-9.3.zip\n" +
        "      sha256: fc0ff04fbfe7a8d866406168208008ae352a243111c64656e6294a1696b10446\n" +
        "      size: 555307\n" +
        "      platform: iPhoneSimulator\n" +
        "      architecture: i386\n" +
        "      sdk: 9.3\n" +
        "      include: include\n" +
        "      lib: libsqlite.a\n" +
        "    - file: sqlite-ios-platform-iPhoneSimulator-architecture-x86_64-sdk-9.3.zip\n" +
        "      sha256: 84e75eabff1e14387c13ce0b9fe939ecc152d8ee086056fce5df2cce41a395ba\n" +
        "      size: 547071\n" +
        "      platform: iPhoneSimulator\n" +
        "      architecture: x86_64\n" +
        "      sdk: 9.3\n" +
        "      include: include\n" +
        "      lib: libsqlite.a\n" +
        "example: |\n" +
        "  #include <sqlite3.h>\n" +
        "  void test() {\n" +
        "    sqlite3_initialize();\n" +
        "  }\n");
  }

  // This is a cmakeify.yml that has "abi" instead of abis.
  @NotNull
  public static TestManifest singleABI() throws MalformedURLException {
    return getResolvedManifest("coordinate:\n" +
        "  groupId: com.github.jomof\n" +
        "  artifactId: sqlite\n" +
        "  version: 0.0.0\n" +
        "android:\n" +
        "  archives:\n" +
        "  - lib: libsqlite.a\n" +
        "    file: sqlite-android-cxx-platform-12-armeabi.zip\n" +
        "    sha256: 485db444abe7535b3cb745c159bbeddba8db9530caf7af8f62750b8cee8d6086\n" +
        "    size: 980275\n" +
        "    runtime: c++\n" +
        "    platform: 12\n" +
        "    ndk: r13b\n" +
        "    abi: armeabi\n" +
        "  - lib: libsqlite.a\n" +
        "    file: sqlite-android-cxx-platform-12-armeabi-v7a.zip\n" +
        "    sha256: 3efa3b51f23cdac7c55422423b845569b1522ba15fa46f1ba28f89dfeaba323e\n" +
        "    size: 944438\n" +
        "    runtime: c++\n" +
        "    platform: 12\n" +
        "    ndk: r13b\n" +
        "    abi: armeabi-v7a\n" +
        "  - lib: libsqlite.a\n" +
        "    file: sqlite-android-cxx-platform-12-x86.zip\n" +
        "    sha256: 50d9593ef6658f72f107c2bdf3f1dc03e292575081da645748f4f0286bde6d59\n" +
        "    size: 1010262\n" +
        "    runtime: c++\n" +
        "    platform: 12\n" +
        "    ndk: r13b\n" +
        "    abi: x86\n" +
        "  - lib: libsqlite.a\n" +
        "    file: sqlite-android-gnustl-platform-12-armeabi.zip\n" +
        "    sha256: 226e0074826878285f734a4fa0db2c1addf2a1b6558e2f6b1000601d9e0d52c0\n" +
        "    size: 980301\n" +
        "    runtime: gnustl\n" +
        "    platform: 12\n" +
        "    ndk: r13b\n" +
        "    abi: armeabi\n" +
        "  - lib: libsqlite.a\n" +
        "    file: sqlite-android-gnustl-platform-12-armeabi-v7a.zip\n" +
        "    sha256: db6cfc4c0428ad0cf103a2f35a45fcde1e7ed834ca984daabd8d1a1a2514e3cc\n" +
        "    size: 944460\n" +
        "    runtime: gnustl\n" +
        "    platform: 12\n" +
        "    ndk: r13b\n" +
        "    abi: armeabi-v7a\n" +
        "  - lib: libsqlite.a\n" +
        "    file: sqlite-android-gnustl-platform-12-x86.zip\n" +
        "    sha256: 52d85c384ed64081f5eac9af3615b32904e5dc25851d3606376e890a43c3bf69\n" +
        "    size: 1010292\n" +
        "    runtime: gnustl\n" +
        "    platform: 12\n" +
        "    ndk: r13b\n" +
        "    abi: x86\n" +
        "  - lib: libsqlite.a\n" +
        "    file: sqlite-android-stlport-platform-12-armeabi.zip\n" +
        "    sha256: 1c367b880ce3ed498db0d2844ebd71aa989c549af33410a42a65c901d33caf50\n" +
        "    size: 980298\n" +
        "    runtime: stlport\n" +
        "    platform: 12\n" +
        "    ndk: r13b\n" +
        "    abi: armeabi\n" +
        "  - lib: libsqlite.a\n" +
        "    file: sqlite-android-stlport-platform-12-armeabi-v7a.zip\n" +
        "    sha256: afca8597b8ec21729bad255f5978117dfe1d348bd947bdeba4a91fb0e8d120d3\n" +
        "    size: 944462\n" +
        "    runtime: stlport\n" +
        "    platform: 12\n" +
        "    ndk: r13b\n" +
        "    abi: armeabi-v7a\n" +
        "  - lib: libsqlite.a\n" +
        "    file: sqlite-android-stlport-platform-12-x86.zip\n" +
        "    sha256: c2f5f7fb2f4426c8ce2d2e827dd82626f33f50d751221a2003e3b39b45c11c78\n" +
        "    size: 1010270\n" +
        "    runtime: stlport\n" +
        "    platform: 12\n" +
        "    ndk: r13b\n" +
        "    abi: x86\n" +
        "  - lib: libsqlite.a\n" +
        "    file: sqlite-android-cxx-platform-21-armeabi.zip\n" +
        "    sha256: 3fcf7cf1921dff8bee24f33f2e1ea84e54610089fd612493c225da4c64a5ac21\n" +
        "    size: 980485\n" +
        "    runtime: c++\n" +
        "    platform: 21\n" +
        "    ndk: r13b\n" +
        "    abi: armeabi\n" +
        "  - lib: libsqlite.a\n" +
        "    file: sqlite-android-cxx-platform-21-armeabi-v7a.zip\n" +
        "    sha256: 669806ae1516cc3f11f4b7d72d6f97a033bda6da8a60e020b0d0c0aadef5db1c\n" +
        "    size: 944612\n" +
        "    runtime: c++\n" +
        "    platform: 21\n" +
        "    ndk: r13b\n" +
        "    abi: armeabi-v7a\n" +
        "  - lib: libsqlite.a\n" +
        "    file: sqlite-android-cxx-platform-21-arm64-v8a.zip\n" +
        "    sha256: 5f4dc6a1a81f1c694dcb51944080b33c7b9dec8021759c8891016d4b85d6f22f\n" +
        "    size: 954374\n" +
        "    runtime: c++\n" +
        "    platform: 21\n" +
        "    ndk: r13b\n" +
        "    abi: arm64-v8a\n" +
        "  - lib: libsqlite.a\n" +
        "    file: sqlite-android-cxx-platform-21-x86.zip\n" +
        "    sha256: 436db93e9a7124707b641c25247021d6f2af28ad8cb9c028a62217cb532764b4\n" +
        "    size: 1010367\n" +
        "    runtime: c++\n" +
        "    platform: 21\n" +
        "    ndk: r13b\n" +
        "    abi: x86\n" +
        "  - lib: libsqlite.a\n" +
        "    file: sqlite-android-cxx-platform-21-x86_64.zip\n" +
        "    sha256: 7a45cf3592d63a5f3ce673bbd1a87ea47c45d28e0441b51751d26f2f18dde46c\n" +
        "    size: 974454\n" +
        "    runtime: c++\n" +
        "    platform: 21\n" +
        "    ndk: r13b\n" +
        "    abi: x86_64\n" +
        "  - lib: libsqlite.a\n" +
        "    file: sqlite-android-gnustl-platform-21-armeabi.zip\n" +
        "    sha256: 06bd163a2a8dfae9a007446dd54e9d91361c762c95775b255a926301fbc265a9\n" +
        "    size: 980473\n" +
        "    runtime: gnustl\n" +
        "    platform: 21\n" +
        "    ndk: r13b\n" +
        "    abi: armeabi\n" +
        "  - lib: libsqlite.a\n" +
        "    file: sqlite-android-gnustl-platform-21-armeabi-v7a.zip\n" +
        "    sha256: 95caac16315a34464c3690d11ca08676182cf1519321a8ae912f64d2badd1119\n" +
        "    size: 944611\n" +
        "    runtime: gnustl\n" +
        "    platform: 21\n" +
        "    ndk: r13b\n" +
        "    abi: armeabi-v7a\n" +
        "  - lib: libsqlite.a\n" +
        "    file: sqlite-android-gnustl-platform-21-arm64-v8a.zip\n" +
        "    sha256: 1a50b8a3512438009cde20afc4d0db67377ea8521ec95204ac20648989541567\n" +
        "    size: 954354\n" +
        "    runtime: gnustl\n" +
        "    platform: 21\n" +
        "    ndk: r13b\n" +
        "    abi: arm64-v8a\n" +
        "  - lib: libsqlite.a\n" +
        "    file: sqlite-android-gnustl-platform-21-x86.zip\n" +
        "    sha256: 6632c8ecbbeb7de9e25ff7a5e6b79153198cc1eeaa0c874729513375a9ebec41\n" +
        "    size: 1010340\n" +
        "    runtime: gnustl\n" +
        "    platform: 21\n" +
        "    ndk: r13b\n" +
        "    abi: x86\n" +
        "  - lib: libsqlite.a\n" +
        "    file: sqlite-android-gnustl-platform-21-x86_64.zip\n" +
        "    sha256: 5c38449bb84b112f5361858edc1dcca5596b79e7df9a4707355324cc8b5314f1\n" +
        "    size: 974455\n" +
        "    runtime: gnustl\n" +
        "    platform: 21\n" +
        "    ndk: r13b\n" +
        "    abi: x86_64\n" +
        "  - lib: libsqlite.a\n" +
        "    file: sqlite-android-stlport-platform-21-armeabi.zip\n" +
        "    sha256: 652fdd5f95d3d3ddf11caf2e49193f93de9551d5e9b29fa4b25616622e8a20d2\n" +
        "    size: 980480\n" +
        "    runtime: stlport\n" +
        "    platform: 21\n" +
        "    ndk: r13b\n" +
        "    abi: armeabi\n" +
        "  - lib: libsqlite.a\n" +
        "    file: sqlite-android-stlport-platform-21-armeabi-v7a.zip\n" +
        "    sha256: 00c675c9788b7999695b9f2e9f5c40d9942c3051daeda812d17346bce3ba0035\n" +
        "    size: 944624\n" +
        "    runtime: stlport\n" +
        "    platform: 21\n" +
        "    ndk: r13b\n" +
        "    abi: armeabi-v7a\n" +
        "  - lib: libsqlite.a\n" +
        "    file: sqlite-android-stlport-platform-21-arm64-v8a.zip\n" +
        "    sha256: a73b37fbae458b7c1b80e6cec773f4551dfd1dc631a569c2ed4eeb7160602616\n" +
        "    size: 954358\n" +
        "    runtime: stlport\n" +
        "    platform: 21\n" +
        "    ndk: r13b\n" +
        "    abi: arm64-v8a\n" +
        "  - lib: libsqlite.a\n" +
        "    file: sqlite-android-stlport-platform-21-x86.zip\n" +
        "    sha256: 1d7ee2ed92e198bd5583091abe2c8fac734b46abc6867d6ef7630be18162ff75\n" +
        "    size: 1010370\n" +
        "    runtime: stlport\n" +
        "    platform: 21\n" +
        "    ndk: r13b\n" +
        "    abi: x86\n" +
        "  - lib: libsqlite.a\n" +
        "    file: sqlite-android-stlport-platform-21-x86_64.zip\n" +
        "    sha256: 4332466fe8ac9cf22d2a552fb996bde3da98df0647a3897d25cbdaa5360a7042\n" +
        "    size: 974553\n" +
        "    runtime: stlport\n" +
        "    platform: 21\n" +
        "    ndk: r13b\n" +
        "    abi: x86_64\n" +
        "example: |\n" +
        "  #include <sqlite3.h>\n" +
        "  void test() {\n" +
        "    sqlite3_initialize();\n" +
        "  }");
  }

  @NotNull
  public static TestManifest boost() throws MalformedURLException {
    return getResolvedManifest("coordinate:\n"
        + "  groupId: com.github.jomof\n"
        + "  artifactId: boost\n"
        + "  version: 1.0.63-rev21\n"
        + "interfaces:\n"
        + "  headers:\n"
        + "    file: boost_1_63_0.zip\n"
        + "    sha256: 97ce6635df1f44653a597343cd5757bb8b6b992beb3720f5fc761e3644bcbe7b\n"
        + "    size: 142660118\n"
        + "    include: boost_1_63_0\n\n"
        + "example: |\n  #include <boost/numeric/ublas/matrix.hpp>\n  void test() {\n     using namespace " +
        "boost::numeric::ublas;\n     matrix<double> m (3, 3);\n     for (unsigned i = 0; i < m.size1 (); ++ i) {\n       for " +
        "(unsigned j = 0; j < m.size2 (); ++ j) {\n         m (i, j) = 3 * i + j;\n       }\n     }\n  }\n");
  }

  @NotNull
  public static TestManifest sqliteLinux() throws MalformedURLException {
    return getResolvedManifest(
        "coordinate:\n  groupId: com.github.jomof\n  artifactId: sqlite\n  version: 0.0.0\nlinux:\n  archives:\n  - lib: " +
            "libsqlite.a\n    file: sqlite-linux.zip\n    sha256: " +
            "a4fcb715b3b22a29ee774f30795516e46ccc4712351d13030f0f58b36c5b3d9b\n    size: 480895\nexample: |\n  #include " +
            "<sqlite3.h>\n  void test() {\n    sqlite3_initialize();\n  }");
  }

  @NotNull
  public static TestManifest sqliteLinuxMultiple() throws MalformedURLException {
    return getResolvedManifest(
        "coordinate:\n  groupId: com.github.jomof\n  artifactId: sqlite\n  version: 0.0.0\nlinux:\n  archives:\n  - lib: " +
            "libsqlite.a\n    file: sqlite-linux-1.zip\n    sha256: " +
            "a4fcb715b3b22a29ee774f30795516e46ccc4712351d13030f0f58b36c5b3d9b\n    size: 480895\n  - lib: libsqlite.a\n    " +
            "file: sqlite-linux-2.zip\n    sha256: a4fcb715b3b22a29ee774f30795516e46ccc4712351d13030f0f58b36c5b3d9b\n    size: " +
            "480895\nexample: |\n  #include <sqlite3.h>\n  void test() {\n    sqlite3_initialize();\n  }");
  }

  @NotNull
  public static TestManifest archiveMissingSize() throws MalformedURLException {
    return getResolvedManifest(
        "coordinate:\n  groupId: com.github.jomof\n  artifactId: vectorial\n  version: 0.0.0\narchive:\n  file: vectorial.zip\n" +
            "  sha256: 47e72f9898a78024a96e7adc5b29d6ec02313a02087646d69d7797f13840121c\n  size: \n  include: " +
            "vectorial-master/include\nexample: |\n  #include <vectorial/simd4f.h>\n  void test() {\n    float z = " +
            "simd4f_get_z(simd4f_add(simd4f_create(1,2,3,4), \n      simd4f_create(1,2,3,4)));\n  }");
  }

  @NotNull
  public static TestManifest archiveMissingFile() throws MalformedURLException {
    return getResolvedManifest(
        "coordinate:\n  groupId: com.github.jomof\n  artifactId: vectorial\n  version: 0.0.0\narchive:\n  file:\n  sha256: " +
            "47e72f9898a78024a96e7adc5b29d6ec02313a02087646d69d7797f13840121c\n  size: 92\n  include: " +
            "vectorial-master/include\nexample: |\n  #include <vectorial/simd4f.h>\n  void test() {\n    float z = " +
            "simd4f_get_z(simd4f_add(simd4f_create(1,2,3,4), \n      simd4f_create(1,2,3,4)));\n  }");
  }

  @NotNull
  public static TestManifest archiveMissingSha256() throws MalformedURLException {
    return getResolvedManifest(
        "coordinate:\n  groupId: com.github.jomof\n  artifactId: vectorial\n  version: 0.0.0\narchive:\n  file: bob.zip\n  " +
            "sha256: \n  size: 92\nexample: |\n  #include <vectorial/simd4f.h>\n  void test() {\n    float z = " +
            "simd4f_get_z(simd4f_add(simd4f_create(1,2,3,4), \n      simd4f_create(1,2,3,4)));\n  }");
  }

  @NotNull
  public static TestManifest archiveMissingInclude() throws MalformedURLException {
    return getResolvedManifest(
        "coordinate:\n  groupId: com.github.jomof\n  artifactId: vectorial\n  version: 0.0.0\narchive:\n  file: bob.zip\n  " +
            "sha256: 47e72f9898a78024a96e7adc5b29d6ec02313a02087646d69d7797f13840121c\n  size: 92\n  include: " +
            "vectorial-master/include\nexample: |\n  #include <vectorial/simd4f.h>\n  void test() {\n    float z = simd4f_get_z" +
            "(simd4f_add(simd4f_create(1,2,3,4), \n      simd4f_create(1,2,3,4)));\n  }");
  }

  @NotNull
  static TestManifest getResolvedManifest(@NotNull String manifest) throws MalformedURLException {
    CDepManifestYml yml = CDepManifestYmlUtils.convertStringToManifest(manifest);
    return new TestManifest(manifest, new ResolvedManifest(new URL("http://google.com/cdep-manifest.yml"), yml));
  }

  @NotNull
  public static TestManifest archiveOnly() throws MalformedURLException {
    return getResolvedManifest(
        "coordinate:\n  groupId: com.github.jomof\n  artifactId: vectorial\n  version: 0.0.0\narchive:\n  file: vectorial.zip\n" +
            "  sha256: 47e72f9898a78024a96e7adc5b29d6ec02313a02087646d69d7797f13840121c\n  size: 52863\n  include: " +
            "vectorial-master/include\nexample: |\n  #include <vectorial/simd4f.h>\n  void test() {\n    float z = simd4f_get_z" +
            "(simd4f_add(simd4f_create(1,2,3,4), \n      simd4f_create(1,2,3,4)));\n  }");
  }

  @NotNull
  public static TestManifest admob() throws MalformedURLException {
    return getResolvedManifest(
        "coordinate:\n  groupId: com.github.jomof\n  artifactId: firebase/admob\n  version: 2.1.3-rev8\ndependencies:\n  - " +
            "compile: com.github.jomof:firebase/app:2.1.3-rev8\n    sha256: " +
            "41ce110b24d2cfa26144b9df1241a4941c57e892f07eb91600103e53650ef0a8\narchive:\n  file: firebase-include.zip\n  " +
            "sha256: 26e3889c07ad841c5c9ff8b1ad86a575833bec1bb6f15719a527d52ced07a57f\n  size: 93293\nandroid:\n  " +
            "archives:\n    - file: firebase-android-admob-cpp.zip\n      sha256: " +
            "34c3cd109199cbccf7ebb1652a5dd66080c27d1448cfa3e6dd5c811aa30e283a\n      size: 4990064\n      ndk: r10d\n     " +
            " runtime: c++\n      platform: 12\n      abis: [arm64-v8a, armeabi, armeabi-v7a, mips, mips64, x86, x86_64]\n     " +
            " include: include\n      lib: libadmob.a\n    - file: firebase-android-admob-gnustl.zip\n      sha256: " +
            "44cd6682f5f82735d4bf6103139e4179ed6c2b4ceec297fd04b129766e2a2a02\n      size: 5294900\n      ndk: r10d\n      " +
            "runtime: gnustl\n      platform: 12\n      abis: [arm64-v8a, armeabi, armeabi-v7a, mips, mips64, x86, " +
            "x86_64]\n      include: include\n      lib: libadmob.a\n    - file: firebase-android-admob-stlport.zip\n      " +
            "sha256: 0606618e53a06e00cd4c6775f49bd5474bccec68370691b35b12be2f1c89d755\n      size: 5154330\n      ndk: r10d\n  " +
            "    runtime: stlport\n      platform: 12\n      abis: [arm64-v8a, armeabi, armeabi-v7a, mips, mips64, " +
            "x86, x86_64]\n      include: include\n      lib: libadmob.a\nexample: |\n  #include \"firebase/admob.h\"\n  " +
            "#include \"firebase/admob/types.h\"\n  #include \"firebase/app.h\"\n  #include \"firebase/future.h\"\n  \n  " +
            "void test() {\n    const char* kAdMobAppID = \"ca-app-pub-XXXXXXXXXXXXXXXX~NNNNNNNNNN\";\n    " +
            "firebase::admob::Initialize(\n      *::firebase::App::Create(::firebase::AppOptions(), NULL /* jni_env */ , NULL " +
            "/* activity */ ), \n      kAdMobAppID);\n  }\n");
  }

  @NotNull
  public static TestManifest sqlite() throws MalformedURLException {
    return getResolvedManifest(
        "coordinate:\n  groupId: com.github.jomof\n  artifactId: sqlite\n  version: 0.0.0\nandroid:\n  archives:\n  - lib: " +
            "libsqlite.a\n    file: sqlite-android-cxx-platform-12.zip\n    sha256: " +
            "45a104d61786eaf163b3006aa989922c5c04b8e787073e1cbd60c7895943162c\n    size: 2676245\n    runtime: c++\n    " +
            "platform: 12\n    ndk: r13b\n    abis: [ armeabi, armeabi-v7a, x86 ]\n  - lib: libsqlite.a\n    file: " +
            "sqlite-android-gnustl-platform-12.zip\n    sha256: " +
            "5975eff815bd516b5da803f4921774ee38ec7d37fcb046bf2b3e078d920bd775\n    size: 2676242\n    runtime: gnustl\n    " +
            "platform: 12\n    ndk: r13b\n    abis: [ armeabi, armeabi-v7a, x86 ]\n  - lib: libsqlite.a\n    file: " +
            "sqlite-android-stlport-platform-12.zip\n    sha256: " +
            "b562331de4d7110349ec6ca2c7c888579f2bb56be095afce4671531809b2a894\n    size: 2676242\n    runtime: stlport\n    " +
            "platform: 12\n    ndk: r13b\n    abis: [ armeabi, armeabi-v7a, x86 ]\n  - lib: libsqlite.a\n    file: " +
            "sqlite-android-cxx-platform-21.zip\n    sha256: 54ee95133dbddd4e2d76c572c0f4591aa4d7820f96f52566906f244c05d8bd9c\n" +
            "    size: 4346280\n    runtime: c++\n    platform: 21\n    ndk: r13b\n    abis: [ armeabi, armeabi-v7a, " +
            "arm64-v8a, x86, x86_64 ]\n  - lib: libsqlite.a\n    file: sqlite-android-gnustl-platform-21.zip\n    sha256: " +
            "da9600b63f03dc9c11ac5b7c234212e16686e0b6874206626bc65f60f230f1af\n    size: 4346366\n    runtime: gnustl\n    " +
            "platform: 21\n    ndk: r13b\n    abis: [ armeabi, armeabi-v7a, arm64-v8a, x86, x86_64 ]\n  - lib: libsqlite.a\n   " +
            " file: sqlite-android-stlport-platform-21.zip\n    sha256: " +
            "f2876bf59b2624b9adc44fd7758bee15fd0d782ddb6049e9e384e0a2b7a7c03f\n    size: 4346303\n    runtime: stlport\n    " +
            "platform: 21\n    ndk: r13b\n    abis: [ armeabi, armeabi-v7a, arm64-v8a, x86, x86_64 ]\niOS:\n  archives:\n " +
            " - lib: libsqlite.a\n    file: sqlite-ios-platform-iPhone.zip\n    sha256: " +
            "7126dfb6282a53c16cd648fcfca3bd8c3ac306def1b5bc8cefb3b82b459fca80\n    size: 1293737\n    platform: iPhoneOS\n    " +
            "sdk: 10.2\n    architecture: armv7s\n  - lib: libsqlite.a\n    file: sqlite-ios-platform-simulator.zip\n    " +
            "sha256: 266f16031afd5aef8adf19394fdcf946cb6a28d19a41b7db1ff87487733b91df\n    size: 546921\n    platform: " +
            "iPhoneSimulator\n    sdk: 10.2\n    architecture: i386\nexample: |\n  #include <sqlite3.h>\n  void test() {\n" +
            "    sqlite3_initialize();\n  }");
  }

  @NotNull
  public static TestManifest sqliteiOS() throws MalformedURLException {
    return getResolvedManifest(
        "coordinate:\n  groupId: com.github.jomof\n  artifactId: sqlite\n  version: 3.16.2-rev33\niOS:\n  archives:\n  - lib: " +
            "libsqlite.a\n    file: sqlite-ios-platform-iPhoneOS-architecture-armv7-sdk-9.3.zip\n    sha256: " +
            "c28410af1bcc42e177a141082325efe0a0fa35a4c42fee786229c8f793009253\n    size: 514133\n    platform: iPhoneOS\n    " +
            "architecture: armv7\n    sdk: 9.3\n  - lib: libsqlite.a\n    file: " +
            "sqlite-ios-platform-iPhoneOS-architecture-armv7s-sdk-9.3.zip\n    sha256: " +
            "4c213577d7f2a77942b5459b5169e64c6ee2d38c806fb1f44c29fb6b6c7d535d\n    size: 514254\n    platform: iPhoneOS\n    " +
            "architecture: armv7s\n    sdk: 9.3\n  - lib: libsqlite.a\n    file: " +
            "sqlite-ios-platform-iPhoneOS-architecture-arm64-sdk-9.3.zip\n    sha256: " +
            "f7a2b0c1b8e532615e1e6e1151a4e182bad08cd9ae10a7b9aaa03d55a42f7bab\n    size: 529339\n    platform: iPhoneOS\n    " +
            "architecture: arm64\n    sdk: 9.3\n  - lib: libsqlite.a\n    file: " +
            "sqlite-ios-platform-iPhoneSimulator-architecture-i386-sdk-9.3.zip\n    sha256: " +
            "767cfc5379304f67aa27af8dc1b16d2372b65ba2829e22305d8d81858caa05a0\n    size: 555306\n    platform: " +
            "iPhoneSimulator\n    architecture: i386\n    sdk: 9.3\n  - lib: libsqlite.a\n    file: " +
            "sqlite-ios-platform-iPhoneSimulator-architecture-x86_64-sdk-9.3.zip\n    sha256: " +
            "7a76243c4ddd006f0105002ea5f6dd1784fb2f3231f793a00a7905661806c1ff\n    size: 547071\n    platform: " +
            "iPhoneSimulator\n    architecture: x86_64\n    sdk: 9.3\nexample: |\n  #include <sqlite3.h>\n  void test() {\n    " +
            "sqlite3_initialize();\n  }\n");
  }

  @NotNull
  public static TestManifest sqliteAndroid() throws MalformedURLException {
    return getResolvedManifest(
        "coordinate:\n  groupId: com.github.jomof\n  artifactId: sqlite\n  version: 3.16.2-rev33\nandroid:\n  archives:\n  - " +
            "lib: libsqlite.a\n    file: sqlite-android-cxx-platform-12.zip\n    sha256: " +
            "9604fa0c7fb7635075b31f9231455469c5498c95279840bddf476f98598f7fc9\n    size: 2675756\n    runtime: c++\n    " +
            "platform: 12\n    ndk: r13b\n    abis: [ armeabi, armeabi-v7a, x86 ]\n  - lib: libsqlite.a\n    file: " +
            "sqlite-android-gnustl-platform-12.zip\n    sha256: " +
            "794945bb7f1e9e62ba7484e4f889d7d965d583a7a7c3c749a7f953f5ac966ec1\n    size: 2675779\n    runtime: gnustl\n    " +
            "platform: 12\n    ndk: r13b\n    abis: [ armeabi, armeabi-v7a, x86 ]\n  - lib: libsqlite.a\n    file: " +
            "sqlite-android-stlport-platform-12.zip\n    sha256: " +
            "3df5b250e8d9429e0e0ee1e8fec571ace23678c05af1cc3ee09b4c94b08e453e\n    size: 2675775\n    runtime: stlport\n    " +
            "platform: 12\n    ndk: r13b\n    abis: [ armeabi, armeabi-v7a, x86 ]\n  - lib: libsqlite.a\n    file: " +
            "sqlite-android-cxx-platform-21.zip\n    sha256: fcc699217930c5bfd0a5fb7240b0f3b24f459034dd1ed43d31245d18c428f0f4\n" +
            "    size: 4345764\n    runtime: c++\n    platform: 21\n    ndk: r13b\n    abis: [ armeabi, armeabi-v7a, " +
            "arm64-v8a, x86, x86_64 ]\n  - lib: libsqlite.a\n    file: sqlite-android-gnustl-platform-21.zip\n    sha256: " +
            "5e80c6fe462398f89e7f421e95237e1426996b956fd4b089c32173999065d15e\n    size: 4345771\n    runtime: gnustl\n    " +
            "platform: 21\n    ndk: r13b\n    abis: [ armeabi, armeabi-v7a, arm64-v8a, x86, x86_64 ]\n  - lib: libsqlite.a\n   " +
            " file: sqlite-android-stlport-platform-21.zip\n    sha256: " +
            "fd6dfe67a3dcf32d3989498a539eb16eb11fd992f1a9459a8c258dfca8279b0a\n    size: 4345886\n    runtime: stlport\n    " +
            "platform: 21\n    ndk: r13b\n    abis: [ armeabi, armeabi-v7a, arm64-v8a, x86, x86_64 ]\nexample: |\n  #include " +
            "<sqlite3.h>\n  void test() {\n    sqlite3_initialize();\n  }\n");
  }

  @NotNull
  static TestManifest emptyiOSArchive() throws MalformedURLException {
    return getResolvedManifest(
        "coordinate:\n  groupId: com.github.jomof\n  artifactId: sqlite\n  version: 0.0.0\nandroid:\n  archives:\n  - lib: " +
            "libsqlite.a\n    file: sqlite-android-cxx-platform-12.zip\n    sha256: " +
            "45a104d61786eaf163b3006aa989922c5c04b8e787073e1cbd60c7895943162c\n    size: 2676245\n    runtime: c++\n    " +
            "platform: 12\n    ndk: r13b\n    abis: [ armeabi, armeabi-v7a, x86 ]\niOS:\n  archives:\nexample: |\n  #include " +
            "<sqlite3.h>\n  void test() {\n    sqlite3_initialize();\n  }");
  }

  @NotNull
  static TestManifest emptyAndroidArchive() throws MalformedURLException {
    return getResolvedManifest(
        "coordinate:\n  groupId: com.github.jomof\n  artifactId: sqlite\n  version: 0.0.0\niOS:\n  archives:\n  - lib: " +
            "libsqlite.a\n    file: sqlite-ios-platform-iPhone.zip\n    sha256: " +
            "45a104d61786eaf163b3006aa989922c5c04b8e787073e1cbd60c7895943162c\n    platform: iPhoneOS\n    size: 2676245\n    " +
            "sdk: 10.2\n    architecture: armv7\nandroid:\n  archives:\nexample: |\n  #include <sqlite3.h>\n  void test() {\n  " +
            "  sqlite3_initialize();\n  }");
  }

  @NotNull
  static public List<TestManifest> allTestManifest() {
    List<TestManifest> result = new ArrayList<>();
    for (Method method : ResolvedManifests.class.getMethods()) {
      if (!Modifier.isStatic(method.getModifiers())) {
        continue;
      }
      if (method.getReturnType() != TestManifest.class) {
        continue;
      }
      if (method.getParameterTypes().length != 0) {
        continue;
      }
      result.add((TestManifest) ReflectionUtils.invoke(method, null));
    }
    return result;
  }

  @NotNull
  static public List<NamedManifest> all() {
    List<NamedManifest> result = new ArrayList<>();
    for (Method method : ResolvedManifests.class.getMethods()) {
      if (!Modifier.isStatic(method.getModifiers())) {
        continue;
      }
      if (method.getReturnType() != TestManifest.class) {
        continue;
      }
      if (method.getParameterTypes().length != 0) {
        continue;
      }
      TestManifest resolved = (TestManifest) ReflectionUtils.invoke(method, null);
      String body = resolved.body;
      result.add(new NamedManifest(method.getName(), body, resolved.manifest));
    }
    return result;
  }

  @Test
  public void checkResolvedIntegrity() {
    assertThat(all()).isNotEmpty();
    assertThat(allTestManifest()).isNotEmpty();
  }

  public static class TestManifest {
    final public String body;
    final public ResolvedManifest manifest;

    TestManifest(String body, ResolvedManifest manifest) {
      this.body = body;
      this.manifest = manifest;
    }
  }

  public static class NamedManifest {
    final public String name;
    final public String body;
    final public ResolvedManifest resolved;

    NamedManifest(String name, String body, ResolvedManifest resolved) {
      this.name = name;
      this.body = body;
      this.resolved = resolved;
    }
  }
}
