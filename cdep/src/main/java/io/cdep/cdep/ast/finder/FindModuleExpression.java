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
import io.cdep.annotations.Nullable;
import io.cdep.cdep.Coordinate;

public class FindModuleExpression extends StatementExpression {
  @NotNull final public GlobalBuildEnvironmentExpression globals;
  @NotNull final public Coordinate coordinate;
  @Nullable final public String headerArchive;
  @Nullable final public String include;
  @NotNull final public StatementExpression body;

  public FindModuleExpression(
      @NotNull GlobalBuildEnvironmentExpression globals,
      @NotNull Coordinate coordinate,
      @Nullable String headerArchive,
      @Nullable String include,
      @NotNull StatementExpression body) {
    this.globals = globals;
    this.coordinate = coordinate;
    this.headerArchive = headerArchive;
    this.include = include;
    this.body = body;
  }
}
