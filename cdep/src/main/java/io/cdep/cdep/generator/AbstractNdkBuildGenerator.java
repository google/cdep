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
package io.cdep.cdep.generator;

import static io.cdep.cdep.utils.StringUtils.safeFormat;

import io.cdep.annotations.NotNull;
import io.cdep.cdep.ReadonlyVisitor;
import io.cdep.cdep.ast.finder.ConstantExpression;

public class AbstractNdkBuildGenerator extends ReadonlyVisitor {
  @NotNull
  final GeneratorEnvironment environment;

  @NotNull
  protected StringBuilder sb = new StringBuilder();

  protected AbstractNdkBuildGenerator(@NotNull GeneratorEnvironment environment) {
    this.environment = environment;
  }

  @Override
  protected void visitConstantExpression(ConstantExpression expr) {
    append("%s", expr.value);
  }

  protected void append(String format, Object... args) {
    sb.append(safeFormat(format, args));
  }
}
