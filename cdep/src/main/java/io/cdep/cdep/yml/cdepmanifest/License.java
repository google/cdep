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

public class License {
  @NotNull
  final public String name;
  @NotNull
  final public String url;

  public License() {
    this.name = "";
    this.url = "";
  }

  public License(@NotNull String name,
      @NotNull String url) {
    this.name = name;
    this.url = url;
  }
}
