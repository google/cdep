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
import io.cdep.cdep.ast.finder.AssignmentBlockExpression;
import io.cdep.cdep.ast.finder.AssignmentExpression;
import io.cdep.cdep.ast.finder.AssignmentReferenceExpression;
import io.cdep.cdep.ast.finder.Expression;

import java.util.ArrayList;
import java.util.List;

public class GetContainedReferences extends ReadonlyVisitor {
  final public List<AssignmentExpression> list = new ArrayList<>();

  public GetContainedReferences(Expression expression) {
    visit(expression);
  }

  @Override
  public void visitAssignmentReferenceExpression(@NotNull AssignmentReferenceExpression expr) {
    super.visit(expr.assignment);
    list.add(expr.assignment);
  }

  @Override
  protected void visitAssignmentBlockExpression(@NotNull AssignmentBlockExpression expr) {
    // Don't count assign block
    visit(expr.statement);
  }
}
