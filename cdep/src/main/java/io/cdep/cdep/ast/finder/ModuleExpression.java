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
package io.cdep.cdep.ast.finder;

import io.cdep.annotations.NotNull;
import io.cdep.cdep.Coordinate;

import java.util.Set;

public class ModuleExpression extends StatementExpression {
  @NotNull
  final public ModuleArchiveExpression archive;
  @NotNull
  final public Set<Coordinate> dependencies;

  public ModuleExpression(@NotNull ModuleArchiveExpression archive, @NotNull Set<Coordinate> dependencies) {
    for(Coordinate coordinate : dependencies) {
      assert coordinate != null;
    }
    this.archive = archive;
    this.dependencies = dependencies;
  }
}
