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

public class Version {
  public static final Version EMPTY_VERSION = new Version();

  @NotNull
  public final String value;

  public Version(@NotNull String version) {
    this.value = version;
  }

  private Version() {
    this.value = "";
  }

  @Override
  public boolean equals(@Nullable Object obj) {
    return obj != null && obj instanceof Version && Objects.equals(value, obj.toString());
  }

  @Override
  public int hashCode() {
    return toString().hashCode();
  }

  @NotNull
  @Override
  public String toString() {
    return value;
  }
}
