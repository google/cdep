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
package io.cdep.cdep;

import io.cdep.annotations.NotNull;
import io.cdep.annotations.Nullable;

import java.util.Objects;

@SuppressWarnings("unused")
public class Coordinate {
  final public static Coordinate EMPTY_COORDINATE = new Coordinate();

  @NotNull
  final public String groupId; // like com.github.jomof
  @NotNull
  final public String artifactId; // like cmakeify
  @NotNull
  final public Version version; // like alpha-0.0.27

  private Coordinate() {
    groupId = "";
    artifactId = "";
    version = Version.EMPTY_VERSION;
  }

  public Coordinate(@NotNull String groupId, @NotNull String artifactId, @NotNull Version version) {
    this.groupId = groupId;
    this.artifactId = artifactId;
    this.version = version;
  }

  public Coordinate(@NotNull String groupId, @NotNull String artifactId) {
    this.groupId = groupId;
    this.artifactId = artifactId;
    this.version = Version.EMPTY_VERSION;
  }

  @Override
  public boolean equals(@Nullable Object obj) {
    return obj != null && obj instanceof Coordinate && Objects.equals(toString(), obj.toString());
  }

  @Override
  public int hashCode() {
    return toString().hashCode();
  }

  @NotNull
  @Override
  public String toString() {
    if (version.value.length() > 0) {
      return groupId + ":" + artifactId + ":" + version;
    }
    return groupId + ":" + artifactId;
  }
}
