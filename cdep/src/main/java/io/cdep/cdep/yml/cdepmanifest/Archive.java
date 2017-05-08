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
package io.cdep.cdep.yml.cdepmanifest;

import io.cdep.annotations.NotNull;

@SuppressWarnings("unused")
public class Archive {
  @NotNull
  final public String file;
  @NotNull
  final public String sha256;
  @NotNull
  final public Long size;
  @NotNull
  final public String include;
  @NotNull
  final public CxxLanguageFeatures requires[];

  private Archive() {
    this.file = "";
    this.sha256 = "";
    this.size = 0L;
    this.include = "";
    this.requires = new CxxLanguageFeatures[0];
  }

  public Archive(
      @NotNull String file,
      @NotNull String sha256,
      @NotNull Long size,
      @NotNull String include,
      @NotNull CxxLanguageFeatures requires[]) {
    this.file = file;
    this.sha256 = sha256;
    this.size = size;
    this.include = include;
    this.requires = requires;
  }
}
