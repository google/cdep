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
package io.cdep.cdep.yml.status;

import io.cdep.annotations.NotNull;

public class FileType {
  final static public String CMAKE_GLUE_FILE = "cmake-glue-file";
  final static public String NDKBUILD_GLUE_FILE = "ndkbuild-glue-file";

  @NotNull
  final public String name;

  public FileType(@NotNull String name) {
    this.name = name;
  }

  public static FileType[] values() {
    return new FileType[]{
        new FileType(CMAKE_GLUE_FILE),
        new FileType(NDKBUILD_GLUE_FILE),
    };
  }

  @Override
  public boolean equals(Object obj) {
    return obj != null && obj instanceof FileType && ((FileType) obj).name.equals(name);
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
