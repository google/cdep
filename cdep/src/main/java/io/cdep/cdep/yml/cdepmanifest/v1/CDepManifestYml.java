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

import io.cdep.annotations.NotNull;
import io.cdep.annotations.Nullable;
import io.cdep.cdep.Coordinate;
import io.cdep.cdep.yml.cdepmanifest.Archive;
import io.cdep.cdep.yml.cdepmanifest.HardNameDependency;
import io.cdep.cdep.yml.cdepmanifest.v3.Linux;
import io.cdep.cdep.yml.cdepmanifest.v3.iOS;

@SuppressWarnings("WeakerAccess")
public class CDepManifestYml {
  @Nullable
  final public Coordinate coordinate;
  @Nullable
  final public HardNameDependency dependencies[];
  @Nullable
  final public Archive archive;
  @Nullable
  final public Android android;
  @Nullable
  final public Linux linux;
  @Nullable
  final public iOS iOS;
  @Nullable
  final public String example;

  public CDepManifestYml() {
    this.coordinate = null;
    this.dependencies = null;
    this.archive = null;
    this.android = null;
    this.linux = null;
    this.iOS = null;
    this.example = null;
  }

  public CDepManifestYml(@NotNull Coordinate coordinate) {
    this.coordinate = coordinate;
    this.dependencies = null;
    this.archive = null;
    this.android = null;
    this.linux = null;
    this.iOS = null;
    this.example = null;
  }

  public CDepManifestYml(@NotNull Coordinate coordinate,
      @Nullable HardNameDependency[] dependencies,
      @Nullable Archive archive,
      @Nullable Android android,
      @Nullable iOS ios,
      @Nullable Linux linux,
      @NotNull String example) {
    this.coordinate = coordinate;
    this.dependencies = dependencies;
    this.archive = archive;
    this.android = android;
    this.iOS = ios;
    this.linux = linux;
    this.example = example;
  }
}
