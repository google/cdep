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
import io.cdep.cdep.Coordinate;
import io.cdep.cdep.yml.cdepmanifest.CDepManifestYmlVersion;
import io.cdep.cdep.yml.cdepmanifest.HardNameDependency;
import io.cdep.cdep.yml.cdepmanifest.Interfaces;

public class CDepManifestYml {
  @Nullable
  final public Coordinate coordinate;
  @Nullable
  final public HardNameDependency dependencies[];
  @Nullable
  final public Interfaces interfaces;
  @Nullable
  final public io.cdep.cdep.yml.cdepmanifest.v3.Android android;
  @Nullable
  final public io.cdep.cdep.yml.cdepmanifest.v3.Linux linux;
  @Nullable
  final public io.cdep.cdep.yml.cdepmanifest.v3.iOS iOS;
  @Nullable
  final public String example;
  @Nullable
  public CDepManifestYmlVersion sourceVersion;

  public CDepManifestYml() {
    this.sourceVersion = null;
    this.coordinate = null;
    this.dependencies = null;
    this.interfaces = null;
    this.android = null;
    this.linux = null;
    this.iOS = null;
    this.example = null;
  }

  public CDepManifestYml(@NotNull Coordinate coordinate) {
    this.sourceVersion = null;
    this.coordinate = coordinate;
    this.dependencies = null;
    this.interfaces = null;
    this.android = null;
    this.linux = null;
    this.iOS = null;
    this.example = null;
  }

  public CDepManifestYml(
      @NotNull CDepManifestYmlVersion sourceVersion,
      @NotNull Coordinate coordinate,
      @Nullable HardNameDependency[] dependencies,
      @Nullable Interfaces interfaces,
      @Nullable Android android,
      @Nullable iOS ios,
      @Nullable Linux linux,
      @Nullable String example) {
    this.sourceVersion = sourceVersion;
    this.coordinate = coordinate;
    this.dependencies = dependencies;
    this.interfaces = interfaces;
    this.android = android;
    this.iOS = ios;
    this.linux = linux;
    this.example = example;
  }
}
