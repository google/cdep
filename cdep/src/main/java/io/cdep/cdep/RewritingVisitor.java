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
import io.cdep.cdep.ast.finder.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.cdep.cdep.ast.finder.ExpressionBuilder.*;

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
        visitMaybeNull(expr.completionSentinel),
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
    Map<Coordinate, StatementExpression> findFunctions = new HashMap<>();
    Map<Coordinate, ExampleExpression> examples = new HashMap<>();

    for (Coordinate coordinate : expr.orderOfReferences) {
      findFunctions.put(coordinate, (StatementExpression) visit(expr.getFindFunction(coordinate)));
    }
    for (Coordinate coordinate : expr.orderOfReferences) {
      ExampleExpression example = expr.getExample(coordinate);
      if (example == null) {
        continue;
      }
      examples.put(coordinate, (ExampleExpression) visit(example));
    }
    return new FunctionTableExpression(expr.globals, expr.orderOfReferences, findFunctions, examples);
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
