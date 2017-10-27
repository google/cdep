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
package io.cdep.cdep.generator;

import io.cdep.annotations.NotNull;
import io.cdep.annotations.Nullable;
import io.cdep.cdep.Coordinate;
import io.cdep.cdep.ast.finder.ExampleExpression;
import io.cdep.cdep.ast.finder.FunctionTableExpression;
import io.cdep.cdep.utils.FileUtils;

import java.io.File;
import java.io.IOException;

import static io.cdep.cdep.io.IO.info;

public class NdkBuildExamplesGenerator {
  final private GeneratorEnvironment environment;

  public NdkBuildExamplesGenerator(GeneratorEnvironment environment) {
    this.environment = environment;
  }

  public void generate(@NotNull FunctionTableExpression table) throws IOException {
    StringBuilder root = new StringBuilder();
    for (Coordinate coordinate : table.orderOfReferences) {
      File exampleFolder = getExampleFolder(coordinate);
      ExampleExpression example = table.getExample(coordinate);
      assert exampleFolder != null;
      //noinspection ResultOfMethodCallIgnored
      exampleFolder.mkdirs();
      String artifact = coordinate.artifactId.replace("/", "-");
      String sourceName = artifact + ".cpp";
      File exampleSourceFile = new File(exampleFolder, sourceName);
      info("Generating %s\n", exampleSourceFile);
      FileUtils.writeTextToFile(exampleSourceFile, example.sourceCode);
      File exampleAndroidMkFile = new File(exampleFolder, "Android.mk");
      String androidMk =
          "LOCAL_PATH := $(call my-dir)\r\n" +
          "include $(CLEAR_VARS)\r\n" +
          "LOCAL_MODULE := hello-{ARTIFACTID}\r\n" +
          "LOCAL_SRC_FILES += {SOURCE}\r\n" +
          "LOCAL_STATIC_LIBRARIES += {ARTIFACTID}\r\n" +
          "LOCAL_LDLIBS += -llog -ldl -lz -lm -latomic -lGLESv1_CM -lGLESv2 -landroid\r\n" +
          "include $(BUILD_SHARED_LIBRARY)\r\n" +
          "$(call import-module, cdep-dependencies)\r\n";
      androidMk = androidMk
          .replace("{ARTIFACTID}", artifact)
          .replace("{SOURCE}", sourceName);
      info("Generating %s\n", exampleAndroidMkFile);
      FileUtils.writeTextToFile(exampleAndroidMkFile, androidMk);
      root.append(String.format("include %s\r\n", exampleAndroidMkFile.getAbsoluteFile()));
    }
    // Generate root Android.mk
    File rootAndroidMkFile = new File(getExampleRootFolder(), "jni");
    rootAndroidMkFile = new File(rootAndroidMkFile, "Android.mk");
    info("Generating %s\n", rootAndroidMkFile);
    FileUtils.writeTextToFile(rootAndroidMkFile, root.toString());

    // Generate root Application.mk
    File moduleFolder = new File(environment.modulesFolder, "ndk-build");
    moduleFolder = new File(moduleFolder, "cdep-dependencies");
    moduleFolder.mkdirs();
    File rootApplicationMkFile = new File(getExampleRootFolder(), "jni");
    rootApplicationMkFile = new File(rootApplicationMkFile, "Application.mk");
    info("Generating %s\n", rootApplicationMkFile);
    String applicationMk =
        "NDK_ALL_ABIS=x86_64 x86 armeabi-v7a armeabi arm64-v8a\r\n" +
        "APP_PLATFORM=android-21\r\n" +
        "NDK_MODULE_PATH={MODULESFOLDER}/ndk-build\r\n" +
        "APP_STL=c++_static\r\n" +
        "APP_CPPFLAGS := -frtti -fexceptions\r\n";
    applicationMk = applicationMk
        .replace("{MODULESFOLDER}", environment.modulesFolder.getAbsolutePath());
    FileUtils.writeTextToFile(rootApplicationMkFile, applicationMk);
  }

  @NotNull
  private File getExampleRootFolder() {
    File file = environment.examplesFolder;
    file = new File(file, "ndk-build");
    return file;
  }

  @Nullable
  private File getExampleFolder(@NotNull Coordinate coordinate) {
    File file = getExampleRootFolder();
    file = new File(file, coordinate.groupId);
    file = new File(file, coordinate.artifactId);
    file = new File(file, coordinate.version.value);
    return file;
  }

}
