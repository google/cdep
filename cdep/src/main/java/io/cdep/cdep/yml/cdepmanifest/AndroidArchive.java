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
public class AndroidArchive {
  @NotNull
  final public String file;
  @NotNull
  final public String sha256;
  @NotNull
  final public Long size;
  @NotNull
  final public String ndk;
  @NotNull
  final public String compiler;
  @NotNull
  final public String runtime;
  @NotNull
  final public String platform;
  @NotNull
  final public String builder;
  @NotNull
  final public AndroidABI abi;
  @NotNull
  final public String include;
  @NotNull
  final public String libs[];
  @NotNull
  final public String flavor;

  private AndroidArchive() {
    this.file = "";
    this.sha256 = "";
    this.size = 0L;
    this.ndk = "";
    this.compiler = "";
    this.runtime = "";
    this.platform = "";
    this.builder = "";
    this.abi = new AndroidABI("");
    this.include = "";
    this.libs = new String[0];
    this.flavor = "";
  }

  public AndroidArchive(@NotNull String file,
      @NotNull String sha256,
      long size,
      @NotNull String ndk,
      @NotNull String compiler,
      @NotNull String runtime,
      @NotNull String platform,
      @NotNull String builder,
      @NotNull AndroidABI abi,
      @NotNull String include,
      @NotNull String libs[],
      @NotNull String flavor) {
    this.file = file;
    this.sha256 = sha256;
    this.size = size;
    this.ndk = ndk;
    this.compiler = compiler;
    this.runtime = runtime;
    this.platform = platform;
    this.builder = builder;
    this.abi = abi;
    this.include = include;
    this.libs = libs;
    this.flavor = flavor;
  }
}
