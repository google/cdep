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
package io.cdep.cdep.yml.cdepmanifest.v3;

import io.cdep.annotations.NotNull;
import io.cdep.annotations.Nullable;

@SuppressWarnings("unused")
public class LinuxArchive {
  @Nullable
  final public String file;
  @Nullable
  final public String sha256;
  @Nullable
  final public Long size;
  @Nullable
  final public String lib;
  @Nullable
  final public String include;

  LinuxArchive() {
    this.file = null;
    this.sha256 = null;
    this.size = null;
    this.lib = null;
    this.include = null;
  }

  public LinuxArchive(@NotNull String file,
      @NotNull String sha256,
      @NotNull Long size,
      @NotNull String lib,
      @Nullable String include) {
    this.file = file;
    this.sha256 = sha256;
    this.size = size;
    this.lib = lib;
    this.include = include;
  }
}
