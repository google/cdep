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

public class CDepYml {

  final public BuildSystem builders[] = new BuildSystem[0];
  final public SoftNameDependency dependencies[] = new SoftNameDependency[0];

  public CDepYml() {
  }

  private String toYaml(@SuppressWarnings("SameParameterValue") int indent) {
    String prefix = new String(new char[indent * 2]).replace('\0', ' ');
    StringBuilder sb = new StringBuilder();
    sb.append(String.format("%sbuilders: [", prefix));
    for (int j = 0; j < builders.length; ++j) {
      if (j != 0) {
        sb.append(", ");
      }
      sb.append(builders[j]);
    }
    sb.append("]\n");

    if (dependencies.length > 0) {
      sb.append(String.format("%sdependencies:\n", prefix));
      for (SoftNameDependency dependency : dependencies) {
        sb.append("- ");
        sb.append(dependency.toYaml(indent + 1));
      }
    }
    return sb.toString();
  }

  @NotNull
  @Override
  public String toString() {
    return toYaml(0);
  }
}
