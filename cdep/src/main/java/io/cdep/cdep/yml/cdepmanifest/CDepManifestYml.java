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
import io.cdep.cdep.Coordinate;

public class CDepManifestYml {
  @NotNull
  final public Coordinate coordinate;
  @NotNull
  final public HardNameDependency dependencies[];
  @NotNull
  final public License license;
  @Nullable
  final public Interfaces interfaces;
  @Nullable
  final public Android android;
  @Nullable
  final public Linux linux;
  @Nullable
  final public iOS iOS;
  @NotNull
  final public String example;
  @NotNull
  public CDepManifestYmlVersion sourceVersion;

  public CDepManifestYml() {
    this.sourceVersion = CDepManifestYmlVersion.vlatest;
    this.coordinate = Coordinate.EMPTY_COORDINATE;
    this.dependencies = new HardNameDependency[0];
    this.license = new License();
    this.interfaces = null;
    this.android = null;
    this.linux = null;
    this.iOS = null;
    this.example = "";
  }

  public CDepManifestYml(@NotNull Coordinate coordinate) {
    this.sourceVersion = CDepManifestYmlVersion.vlatest;
    this.coordinate = coordinate;
    this.dependencies = new HardNameDependency[0];
    this.license = new License();
    this.interfaces = null;
    this.android = null;
    this.linux = null;
    this.iOS = null;
    this.example = "";
  }

  public CDepManifestYml(
      @NotNull CDepManifestYmlVersion sourceVersion,
      @NotNull Coordinate coordinate,
      @NotNull HardNameDependency[] dependencies,
      @NotNull License license,
      @Nullable Interfaces interfaces,
      @Nullable Android android,
      @Nullable iOS ios,
      @Nullable Linux linux,
      @NotNull String example) {
    for (HardNameDependency dependency : dependencies) {
      assert dependency != null;
    }
    this.sourceVersion = sourceVersion;
    this.coordinate = coordinate;
    this.dependencies = dependencies;
    this.license = license;
    this.interfaces = interfaces;
    this.android = android;
    this.iOS = ios;
    this.linux = linux;
    this.example = example;
  }
}
