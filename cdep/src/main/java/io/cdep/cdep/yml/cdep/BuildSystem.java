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
package io.cdep.cdep.yml.cdep;

import io.cdep.annotations.NotNull;

@SuppressWarnings("WeakerAccess")
public class BuildSystem {
  final static public String CMAKE = "cmake";
  final static public String CMAKE_EXAMPLES = "cmakeExamples";
  final static public String NDK_BUILD = "ndk-build";

  @NotNull
  final public String name;
  public BuildSystem(@NotNull String name) {
    this.name = name;
  }

  public static BuildSystem[] values() {
    return new BuildSystem[] {
        new BuildSystem(CMAKE),
        new BuildSystem(CMAKE_EXAMPLES),
        new BuildSystem(NDK_BUILD),
    };
  }

  @Override
  public boolean equals(Object obj) {
    return obj != null && obj instanceof BuildSystem && ((BuildSystem) obj).name.equals(name);
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public String toString() {
    return name;
  }
}
