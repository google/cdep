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

@SuppressWarnings({"WeakerAccess", "unused"})
public class ReadonlyVisitor {

  public void visit(@Nullable Expression expr) {
    if (expr == null) {
      return;
    }

    if (expr.getClass().equals(FunctionTableExpression.class)) {
      visitFunctionTableExpression((FunctionTableExpression) expr);
      return;
    }
    if (expr.getClass().equals(FindModuleExpression.class)) {
      visitFindModuleExpression((FindModuleExpression) expr);
      return;
    }
    if (expr.getClass().equals(ParameterExpression.class)) {
      visitParameterExpression((ParameterExpression) expr);
      return;
    }
    if (expr.getClass().equals(IfSwitchExpression.class)) {
      visitIfSwitchExpression((IfSwitchExpression) expr);
      return;
    }
    if (expr.getClass().equals(ConstantExpression.class)) {
      visitConstantExpression((ConstantExpression) expr);
      return;
    }
    if (expr.getClass().equals(AssignmentExpression.class)) {
      visitAssignmentExpression((AssignmentExpression) expr);
      return;
    }
    if (expr.getClass().equals(InvokeFunctionExpression.class)) {
      visitInvokeFunctionExpression((InvokeFunctionExpression) expr);
      return;
    }
    if (expr.getClass().equals(ModuleExpression.class)) {
      visitModuleExpression((ModuleExpression) expr);
      return;
    }
    if (expr.getClass().equals(AbortExpression.class)) {
      visitAbortExpression((AbortExpression) expr);
      return;
    }
    if (expr.getClass().equals(ExampleExpression.class)) {
      visitExampleExpression((ExampleExpression) expr);
      return;
    }
    if (expr.getClass().equals(ExternalFunctionExpression.class)) {
      visitExternalFunctionExpression((ExternalFunctionExpression) expr);
      return;
    }
    if (expr.getClass().equals(ArrayExpression.class)) {
      visitArrayExpression((ArrayExpression) expr);
      return;
    }
    if (expr.getClass().equals(AssignmentBlockExpression.class)) {
      visitAssignmentBlockExpression((AssignmentBlockExpression) expr);
      return;
    }
    if (expr.getClass().equals(AssignmentReferenceExpression.class)) {
      visitAssignmentReferenceExpression((AssignmentReferenceExpression) expr);
      return;
    }
    if (expr.getClass().equals(ModuleArchiveExpression.class)) {
      visitModuleArchiveExpression((ModuleArchiveExpression) expr);
      return;
    }
    if (expr.getClass().equals(MultiStatementExpression.class)) {
      visitMultiStatementExpression((MultiStatementExpression) expr);
      return;
    }
    if (expr.getClass().equals(NopExpression.class)) {
      visitNopExpression((NopExpression) expr);
      return;
    }
    if (expr.getClass().equals(GlobalBuildEnvironmentExpression.class)) {
      visitGlobalBuildEnvironmentExpression((GlobalBuildEnvironmentExpression) expr);
      return;
    }

    throw new RuntimeException("ro" + expr.getClass().toString());
  }

  protected void visitGlobalBuildEnvironmentExpression(@NotNull GlobalBuildEnvironmentExpression expr) {
    visit(expr.cdepDeterminedAndroidRuntime);
    visit(expr.cdepDeterminedAndroidAbi);
    visit(expr.cdepExplodedRoot);
    visit(expr.cmakeOsxArchitectures);
    visit(expr.cmakeOsxSysroot);
    visit(expr.buildSystemTargetPlatform);
    visit(expr.buildSystemTargetSystem);
    visit(expr.buildSystemCxxCompilerStandard);
  }

  protected void visitModuleArchiveExpression(@NotNull ModuleArchiveExpression expr) {
    visit(expr.includePath);
    visitArray(expr.libraryPaths);
  }

  protected void visitAssignmentReferenceExpression(AssignmentReferenceExpression expr) {
  }

  protected void visitAssignmentBlockExpression(@NotNull AssignmentBlockExpression expr) {
    for (AssignmentExpression assignment : expr.assignments) {
      visit(assignment);
    }
    visit(expr.statement);
  }

  protected void visitArrayExpression(@NotNull ArrayExpression expr) {
    visitArray(expr.elements);
  }

  @SuppressWarnings("EmptyMethod")
  protected void visitExternalFunctionExpression(ExternalFunctionExpression expr) {
  }

  @SuppressWarnings("EmptyMethod")
  protected void visitExampleExpression(ExampleExpression expr) {
  }

  protected void visitAbortExpression(@NotNull AbortExpression expr) {
    visitArray(expr.parameters);
  }

  protected void visitModuleExpression(@NotNull ModuleExpression expr) {
    visit(expr.archive);
  }

  protected void visitInvokeFunctionExpression(@NotNull InvokeFunctionExpression expr) {
    visit(expr.function);
    visitArray(expr.parameters);
  }

  protected void visitArray(@NotNull Expression[] array) {
    for (Expression anArray : array) {
      visit(anArray);
    }
  }

  protected void visitAssignmentExpression(@NotNull AssignmentExpression expr) {
    visit(expr.expression);
  }

  protected void visitConstantExpression(ConstantExpression expr) {
  }

  protected void visitIfSwitchExpression(@NotNull IfSwitchExpression expr) {
    visitArray(expr.conditions);
    visitArray(expr.expressions);
    visit(expr.elseExpression);
  }

  protected void visitParameterExpression(ParameterExpression expr) {
  }

  protected void visitFindModuleExpression(@NotNull FindModuleExpression expr) {
    visit(expr.body);
  }

  protected void visitMultiStatementExpression(@NotNull MultiStatementExpression expr) {
    visitArray(expr.statements);
  }

  @SuppressWarnings("EmptyMethod")
  protected void visitNopExpression(NopExpression expr) {
  }

  protected void visitFunctionTableExpression(@NotNull FunctionTableExpression expr) {
    visit(expr.globals);
    for (Coordinate coordinate : expr.findFunctions.keySet()) {
      visit(expr.findFunctions.get(coordinate));
    }
    for (Coordinate coordinate : expr.examples.keySet()) {
      visit(expr.examples.get(coordinate));
    }
  }
}
