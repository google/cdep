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
package io.cdep.cdep.yml.cdepmanifest.v1;

import io.cdep.annotations.Nullable;

@SuppressWarnings("unused")
public class AndroidArchive {
  @Nullable
  final public String file;
  @Nullable
  final public String sha256;
  @Nullable
  final public Long size;
  @Nullable
  final public String ndk;
  @Nullable
  final public String compiler;
  @Nullable
  final public String runtime;
  @Nullable
  final public String platform;
  @Nullable
  final public String builder;
  @Nullable
  final public String abis[];
  @Nullable
  final public String include;
  @Nullable
  final public String lib;
  @Nullable
  final public String flavor;

  private AndroidArchive() {
    this.file = null;
    this.sha256 = null;
    this.size = null;
    this.ndk = null;
    this.compiler = null;
    this.runtime = null;
    this.platform = null;
    this.builder = null;
    this.abis = null;
    this.include = "include";
    this.lib = null;
    this.flavor = null;
  }
}
