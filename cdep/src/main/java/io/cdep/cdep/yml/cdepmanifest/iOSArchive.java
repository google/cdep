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
import io.cdep.annotations.Nullable;

@SuppressWarnings({"WeakerAccess", "unused"})
public class iOSArchive {

  @NotNull
  final public String file;
  @NotNull
  final public String sha256;
  @NotNull
  final public Long size;
  @Nullable
  final public iOSPlatform platform;
  @Nullable
  final public iOSArchitecture architecture;
  @NotNull
  final public String sdk;
  @NotNull
  final public String include;
  @NotNull
  final public String libs[];
  @NotNull
  final public String flavor;

  private iOSArchive() {
    this.file = "";
    this.sha256 = "";
    this.size = 0L;
    this.platform = null;
    this.architecture = null;
    this.sdk = "";
    this.libs = new String[0];
    this.flavor = "";
    this.include = "";
  }

  public iOSArchive(
      @NotNull String file,
      @NotNull String sha256,
      @NotNull Long size,
      @Nullable iOSPlatform platform,
      @Nullable iOSArchitecture architecture,
      @NotNull String sdk,
      @NotNull String include,
      @NotNull String libs[],
      @NotNull String flavor) {
    this.file = file;
    this.sha256 = sha256;
    this.size = size;
    this.platform = platform;
    this.architecture = architecture;
    this.sdk = sdk;
    this.include = include;
    this.libs = libs;
    this.flavor = flavor;
  }
}
