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

import static io.cdep.cdep.ast.finder.ExpressionBuilder.abort;
import static io.cdep.cdep.ast.finder.ExpressionBuilder.archive;
import static io.cdep.cdep.ast.finder.ExpressionBuilder.array;
import static io.cdep.cdep.ast.finder.ExpressionBuilder.assign;
import static io.cdep.cdep.ast.finder.ExpressionBuilder.assignmentBlock;
import static io.cdep.cdep.ast.finder.ExpressionBuilder.constant;
import static io.cdep.cdep.ast.finder.ExpressionBuilder.ifSwitch;
import static io.cdep.cdep.ast.finder.ExpressionBuilder.invoke;
import static io.cdep.cdep.ast.finder.ExpressionBuilder.module;
import static io.cdep.cdep.ast.finder.ExpressionBuilder.multi;
import static io.cdep.cdep.ast.finder.ExpressionBuilder.nop;

import io.cdep.annotations.NotNull;
import io.cdep.annotations.Nullable;
import io.cdep.cdep.ast.finder.AbortExpression;
import io.cdep.cdep.ast.finder.ArrayExpression;
import io.cdep.cdep.ast.finder.AssignmentBlockExpression;
import io.cdep.cdep.ast.finder.AssignmentExpression;
import io.cdep.cdep.ast.finder.AssignmentReferenceExpression;
import io.cdep.cdep.ast.finder.ConstantExpression;
import io.cdep.cdep.ast.finder.ExampleExpression;
import io.cdep.cdep.ast.finder.Expression;
import io.cdep.cdep.ast.finder.ExternalFunctionExpression;
import io.cdep.cdep.ast.finder.FindModuleExpression;
import io.cdep.cdep.ast.finder.FunctionTableExpression;
import io.cdep.cdep.ast.finder.GlobalBuildEnvironmentExpression;
import io.cdep.cdep.ast.finder.IfSwitchExpression;
import io.cdep.cdep.ast.finder.InvokeFunctionExpression;
import io.cdep.cdep.ast.finder.ModuleArchiveExpression;
import io.cdep.cdep.ast.finder.ModuleExpression;
import io.cdep.cdep.ast.finder.MultiStatementExpression;
import io.cdep.cdep.ast.finder.NopExpression;
import io.cdep.cdep.ast.finder.ParameterExpression;
import io.cdep.cdep.ast.finder.StatementExpression;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class RewritingVisitor {
  private final Map<Expression, Expression> identity = new HashMap<>();

  @NotNull
  public Expression visit(@NotNull Expression expr) {
    Expression prior = identity.get(expr);
    if (prior != null) {
      return prior;
    }
    identity.put(expr, visitNoIdentity(expr));
    return visit(expr);
  }

  @Nullable
  private Expression visitMaybeNull(@Nullable Expression expr) {
    if (expr == null) {
      return null;
    }
    return this.visit(expr);
  }

  @Nullable
  private Expression visitNoIdentity(@NotNull Expression expr) {

    if (expr.getClass().equals(FunctionTableExpression.class)) {
      return visitFunctionTableExpression((FunctionTableExpression) expr);
    }
    if (expr.getClass().equals(FindModuleExpression.class)) {
      return visitFindModuleExpression((FindModuleExpression) expr);
    }
    if (expr.getClass().equals(ParameterExpression.class)) {
      return visitParameterExpression((ParameterExpression) expr);
    }
    if (expr.getClass().equals(IfSwitchExpression.class)) {
      return visitIfSwitchExpression((IfSwitchExpression) expr);
    }
    if (expr.getClass().equals(ConstantExpression.class)) {
      return visitConstantExpression((ConstantExpression) expr);
    }
    if (expr.getClass().equals(AssignmentExpression.class)) {
      return visitAssignmentExpression((AssignmentExpression) expr);
    }
    if (expr.getClass().equals(InvokeFunctionExpression.class)) {
      return visitInvokeFunctionExpression((InvokeFunctionExpression) expr);
    }
    if (expr.getClass().equals(ModuleExpression.class)) {
      return visitModuleExpression((ModuleExpression) expr);
    }
    if (expr.getClass().equals(AbortExpression.class)) {
      return visitAbortExpression((AbortExpression) expr);
    }
    if (expr.getClass().equals(ExampleExpression.class)) {
      return visitExampleExpression((ExampleExpression) expr);
    }
    if (expr.getClass().equals(ExternalFunctionExpression.class)) {
      return visitExternalFunctionExpression((ExternalFunctionExpression) expr);
    }
    if (expr.getClass().equals(ArrayExpression.class)) {
      return visitArrayExpression((ArrayExpression) expr);
    }
    if (expr.getClass().equals(ModuleArchiveExpression.class)) {
      return visitModuleArchiveExpression((ModuleArchiveExpression) expr);
    }
    if (expr.getClass().equals(AssignmentBlockExpression.class)) {
      return visitAssignmentBlockExpression((AssignmentBlockExpression) expr);
    }
    if (expr.getClass().equals(AssignmentReferenceExpression.class)) {
      return visitAssignmentReferenceExpression((AssignmentReferenceExpression) expr);
    }
    if (expr.getClass().equals(MultiStatementExpression.class)) {
      return visitMultiStatementExpression((MultiStatementExpression) expr);
    }
    if (expr.getClass().equals(NopExpression.class)) {
      return visitNopExpression((NopExpression) expr);
    }
    if (expr.getClass().equals(GlobalBuildEnvironmentExpression.class)) {
      return visitGlobalBuildEnvironmentExpression((GlobalBuildEnvironmentExpression) expr);
    }
    throw new RuntimeException("rw" + expr.getClass().toString());
  }

  protected Expression visitGlobalBuildEnvironmentExpression(GlobalBuildEnvironmentExpression expr) {
    return expr;
  }

  private Expression visitAssignmentReferenceExpression(AssignmentReferenceExpression expr) {
    return expr;
  }

  @NotNull
  private Expression visitAssignmentBlockExpression(@NotNull AssignmentBlockExpression expr) {
    return assignmentBlock(visitList(expr.assignments), (StatementExpression) visit(expr.statement));
  }

  @NotNull
  private List<AssignmentExpression> visitList(@NotNull List<AssignmentExpression> assignments) {
    List<AssignmentExpression> result = new ArrayList<>();
    for (AssignmentExpression assignment : assignments) {
      result.add((AssignmentExpression) visit(assignment));
    }
    return result;
  }

  @NotNull
  private Expression visitArrayExpression(@NotNull ArrayExpression expr) {
    return array(visitArray(expr.elements));
  }

  private Expression visitExternalFunctionExpression(ExternalFunctionExpression expr) {
    // Don't rewrite since identity is used for lookup.
    return expr;
  }

  @NotNull
  private Expression visitExampleExpression(@NotNull ExampleExpression expr) {
    return new ExampleExpression(expr.sourceCode);
  }

  @NotNull
  private Expression visitAbortExpression(@NotNull AbortExpression expr) {
    return abort(expr.message, visitArray(expr.parameters));
  }

  protected Expression visitModuleExpression(@NotNull ModuleExpression expr) {
    return module((ModuleArchiveExpression) visit(expr.archive), expr.dependencies);
  }

  @NotNull
  private Expression visitModuleArchiveExpression(@NotNull ModuleArchiveExpression expr) {
    return archive(
        expr.file,
        expr.sha256,
        expr.size,
        expr.include,
        visitMaybeNull(expr.includePath),
        expr.libs,
        visitExpressionArray(expr.libraryPaths),
        expr.requires);
  }

  @NotNull
  private Expression[] visitExpressionArray(@NotNull Expression[] libraryPaths) {
    Expression result[] = new Expression[libraryPaths.length];
    for (int i = 0; i < libraryPaths.length; ++i) {
      result[i] = visit(libraryPaths[i]);
    }
    return result;
  }

  @NotNull
  protected Expression visitInvokeFunctionExpression(@NotNull InvokeFunctionExpression expr) {
    return invoke((ExternalFunctionExpression) visit(expr.function), visitArray(expr.parameters));
  }

  @NotNull
  private Expression[] visitArray(@NotNull Expression[] array) {
    Expression result[] = new Expression[array.length];
    for (int i = 0; i < array.length; ++i) {
      result[i] = visit(array[i]);
    }
    return result;
  }

  @NotNull
  Expression visitAssignmentExpression(@NotNull AssignmentExpression expr) {
    return assign(expr.name, visit(expr.expression));
  }

  @NotNull
  private Expression visitConstantExpression(@NotNull ConstantExpression expr) {
    return constant(expr.value);
  }

  @NotNull
  Expression visitIfSwitchExpression(@NotNull IfSwitchExpression expr) {
    return ifSwitch(visitArray(expr.conditions), visitArray(expr.expressions), visit(expr.elseExpression));
  }

  @NotNull
  private Expression visitParameterExpression(@NotNull ParameterExpression expr) {
    return expr;
  }

  @NotNull
  Expression visitFindModuleExpression(@NotNull FindModuleExpression expr) {
    return new FindModuleExpression(
        (GlobalBuildEnvironmentExpression) visit(expr.globals),
        expr.coordinate,
        expr.headerArchive,
        expr.include,
        (StatementExpression) visit(expr.body));
  }

  @NotNull
  public Expression visitFunctionTableExpression(@NotNull FunctionTableExpression expr) {
    FunctionTableExpression newExpr = new FunctionTableExpression(expr.globals);

    for (Coordinate coordinate : expr.findFunctions.keySet()) {
      newExpr.findFunctions.put(coordinate, (StatementExpression) visit(expr.findFunctions.get(coordinate)));
    }
    for (Coordinate coordinate : expr.examples.keySet()) {
      newExpr.examples.put(coordinate, (ExampleExpression) visit(expr.examples.get(coordinate)));
    }
    return newExpr;
  }

  @NotNull
  private StatementExpression[] visitStatementExpressionArray(@NotNull StatementExpression[] array) {
    StatementExpression result[] = new StatementExpression[array.length];
    for (int i = 0; i < array.length; ++i) {
      result[i] = (StatementExpression) visit(array[i]);
    }
    return result;
  }

  @NotNull
  private Expression visitMultiStatementExpression(@NotNull MultiStatementExpression expr) {
    return multi(visitStatementExpressionArray(expr.statements));
  }

  @NotNull
  private Expression visitNopExpression(@NotNull NopExpression expr) {
    return nop();
  }
}
