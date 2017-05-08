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

public class CMakeExamplesGenerator {

  final private GeneratorEnvironment environment;

  public CMakeExamplesGenerator(GeneratorEnvironment environment) {
    this.environment = environment;
  }

  public void generate(@NotNull FunctionTableExpression table) throws IOException {
    StringBuilder root = new StringBuilder();
    CMakeGenerator cmake = new CMakeGenerator(environment, table);
    root.append("cmake_minimum_required(VERSION 3.0.2)\n");
    for (Coordinate coordinate : table.examples.keySet()) {
      File exampleFolder = getExampleFolder(coordinate);
      ExampleExpression example = table.examples.get(coordinate);
      assert exampleFolder != null;
      //noinspection ResultOfMethodCallIgnored
      exampleFolder.mkdirs();
      String artifact = coordinate.artifactId.replace("/", "_");
      String sourceName = artifact + ".cpp";
      File exampleSourceFile = new File(exampleFolder, sourceName);
      info("Generating %s\n", exampleSourceFile);
      FileUtils.writeTextToFile(exampleSourceFile, example.sourceCode);
      File exampleCMakeListsFile = new File(exampleFolder, "CMakeLists.txt");
      String cmakeLists = "cmake_minimum_required(VERSION 3.0.2)\n"
          + "project({ARTIFACTID}_example_project)\n"
          + "include(\"{MODULE}\")\n"
          + "add_library({ARTIFACTID}_target SHARED {SOURCE})\n"
          + "{ADDFUNCTION}({ARTIFACTID}_target)\n";
      cmakeLists = cmakeLists.replace("{MODULE}", cmake.getCMakeConfigurationFile().getAbsolutePath()).replace("{ARTIFACTID}",
          artifact).replace("{SOURCE}", sourceName).replace("{ADDFUNCTION}", cmake.getAddDependencyFunctionName(coordinate));
      info("Generating %s\n", exampleCMakeListsFile);
      FileUtils.writeTextToFile(exampleCMakeListsFile, cmakeLists);
      root.append(String.format("add_subdirectory(\"%s\")\r\n", exampleCMakeListsFile.getParentFile().getAbsolutePath()));
    }
    File rootFile = new File(getExampleRootFolder(), "CMakeLists.txt");
    info("Generating %s\n", rootFile);
    FileUtils.writeTextToFile(rootFile, root.toString());

  }

  @NotNull
  private File getExampleRootFolder() {
    File file = environment.examplesFolder;
    file = new File(file, "cmake");
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
