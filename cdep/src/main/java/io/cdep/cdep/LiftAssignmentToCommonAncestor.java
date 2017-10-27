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
import io.cdep.cdep.ast.finder.*;

import java.util.*;

import static io.cdep.cdep.ast.finder.ExpressionBuilder.assignmentBlock;
import static io.cdep.cdep.utils.Invariant.require;

public class LiftAssignmentToCommonAncestor extends RewritingVisitor {
  @NotNull
  private final Set<AssignmentExpression> captured = new HashSet<>();
  @NotNull
  private Map<AssignmentExpression, Integer> functionCounts = new HashMap<>();

  public LiftAssignmentToCommonAncestor() {
  }

  @NotNull
  @Override
  protected Expression visitFindModuleExpression(@NotNull FindModuleExpression expr) {
    List<AssignmentExpression> order = new ArrayList<>();
    Map<AssignmentExpression, Integer> counts = new HashMap<>();
    assignments(expr, order, counts);
    this.functionCounts = counts;
    StatementExpression body = (StatementExpression) visit(expr.body);
    List<AssignmentExpression> block = extractBlocks(body);
    if (block.size() > 0) {
      body = assignmentBlock(block, body);
    }

    StatementExpression result = new FindModuleExpression(
        expr.globals,
        expr.coordinate,
        expr.headerArchive,
        expr.include,
        body);
    block = extractBlocks(result);
    if (block.size() > 0) {
      result = assignmentBlock(block, result);
    }
    return result;
  }

  @NotNull
  @Override
  protected Expression visitIfSwitchExpression(@NotNull IfSwitchExpression expr) {
    Expression result = super.visitIfSwitchExpression(expr);
    List<AssignmentExpression> block = extractBlocks(result);

    if (block.size() > 0) {
      return assignmentBlock(block, (StatementExpression) result);
    }
    return result;
  }

  @Override
  protected Expression visitModuleExpression(@NotNull ModuleExpression expr) {
    Expression result = super.visitModuleExpression(expr);
    List<AssignmentExpression> block = extractBlocks(result);

    if (block.size() > 0) {
      return assignmentBlock(block, (StatementExpression) result);
    }
    return result;
  }

  @NotNull
  private List<AssignmentExpression> extractBlocks(Expression result) {
    List<AssignmentExpression> order = new ArrayList<>();
    Map<AssignmentExpression, Integer> count = new HashMap<>();
    assignments(result, order, count);
    List<AssignmentExpression> block = new ArrayList<>();

    for (AssignmentExpression assignment : order) {
      if (captured.contains(assignment)) {
        continue;
      }
      long functionCount = functionCounts.get(assignment);
      long currentCount = count.get(assignment);
      require(currentCount <= functionCount);
      if (currentCount == functionCount) {
        // Current scope covers all references in the function so
        // we can lift the assignments to this level.
        captured.add(assignment);
        block.add(assignment);
      }
    }
    return block;
  }

  private void assignments(Expression expr,
      @NotNull List<AssignmentExpression> order,
      @NotNull Map<AssignmentExpression, Integer> counts) {
    require(order.size() == 0);
    require(counts.size() == 0);
    List<AssignmentExpression> assignments = new GetContainedReferences(expr).list;
    for (AssignmentExpression assignment : assignments) {
      Integer n = counts.get(assignment);
      if (n == null) {
        n = 0;
      }
      order.add(assignment);
      counts.put(assignment, ++n);
    }
  }
}
