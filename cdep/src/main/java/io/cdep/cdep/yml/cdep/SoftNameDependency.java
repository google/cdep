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
package io.cdep.cdep.yml.cdep;

import io.cdep.annotations.NotNull;
import io.cdep.annotations.Nullable;

@SuppressWarnings("unused")
public class SoftNameDependency {
  @Nullable
  final public String compile;

  public SoftNameDependency() {
    compile = null;
  }

  public SoftNameDependency(@NotNull String compile) {
    this.compile = compile;
  }

  @NotNull
  public String toYaml(int indent) {
    String firstPrefix = new String(new char[(indent - 1) * 2]).replace('\0', ' ');
    String nextPrefix = new String(new char[indent * 2]).replace('\0', ' ');
    StringBuilder sb = new StringBuilder();
    if (compile != null && compile.length() > 0) {
      sb.append(String.format("%scompile: %s\n", firstPrefix, compile));
    }
    return sb.toString();
  }

  @NotNull
  @Override
  public String toString() {
    return toYaml(1);
  }
}
