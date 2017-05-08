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
package io.cdep;

import io.cdep.annotations.NotNull;
import io.cdep.cdep.generator.GeneratorEnvironment;
import io.cdep.cdep.utils.PlatformUtils;
import io.cdep.cdep.utils.ReflectionUtils;
import org.fusesource.jansi.AnsiConsole;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.cdep.cdep.utils.Invariant.require;

/**
 * Methods meant to be used for calling back from CMake or ndk-build into CDep.
 */
public class API {

  /**
   * Get the location of java.exe that started this process.
   */
  static String getJvmLocation() {
    String java = System.getProperties().getProperty("java.home")
        + File.separator + "bin" + File.separator + "java";
    if (PlatformUtils.isWindows()) {
      java += ".exe";
      java = java.replace("\\", "/");
    }
    File result = new File(java);
    require(result.isFile(), "Expected to find java at %s but didn't", result);
    return java;
  }

  /**
   * Get a java command-line to call back into CDep.
   */
  @NotNull
  private static List<String> callCDep(@NotNull GeneratorEnvironment environment) throws MalformedURLException {
    List<String> result = new ArrayList<>();
    if (PlatformUtils.isWindows()) {
      result.add("\"" + getJvmLocation() + "\"");
    } else {
      result.add(platformQuote(getJvmLocation()));
    }
    result.add("-classpath");
    File cdepLocation = ReflectionUtils.getLocation(API.class);
    String classPath = cdepLocation.getAbsolutePath().replace("\\", "/");
    if (!classPath.endsWith(".jar")) {
      String separator = PlatformUtils.isWindows() ? ";" : ":";
      // In a test environment need to include SnakeYAML since it isn't part of the unit test
      File yamlLocation = ReflectionUtils.getLocation(YAMLException.class);
      classPath = yamlLocation.getAbsolutePath().replace("\\", "/")
          + separator + classPath;
      File jansiLocation = ReflectionUtils.getLocation(AnsiConsole.class);
      classPath = jansiLocation.getAbsolutePath().replace("\\", "/")
          + separator + classPath;
    }
    result.add(platformQuote(classPath));
    result.add("io.cdep.CDep");
    result.add("--working-folder");
    result.add(platformQuote(environment.workingFolder.getAbsolutePath().replace("\\", "/")));

    return result;
  }

  private static String platformQuote(String file) {
    if (PlatformUtils.isWindows()) {
      return "\"" + file + "\"";
    }
    return file;
  }

  /**
   * Generate a call back to CDep.
   */
  @NotNull
  public static List<String> generateCDepCall(
      @NotNull GeneratorEnvironment environment,
      String ... args) throws MalformedURLException {
    List<String> result = new ArrayList<>();
    result.addAll(callCDep(environment));
    Collections.addAll(result, args);
    return result;
  }
}
