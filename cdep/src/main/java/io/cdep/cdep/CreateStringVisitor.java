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
import io.cdep.cdep.ast.finder.AbortExpression;
import io.cdep.cdep.ast.finder.AssignmentExpression;
import io.cdep.cdep.ast.finder.AssignmentReferenceExpression;
import io.cdep.cdep.ast.finder.ConstantExpression;
import io.cdep.cdep.ast.finder.Expression;
import io.cdep.cdep.ast.finder.FindModuleExpression;
import io.cdep.cdep.ast.finder.GlobalBuildEnvironmentExpression;
import io.cdep.cdep.ast.finder.IfSwitchExpression;
import io.cdep.cdep.ast.finder.InvokeFunctionExpression;
import io.cdep.cdep.ast.finder.ModuleArchiveExpression;
import io.cdep.cdep.ast.finder.ModuleExpression;
import io.cdep.cdep.ast.finder.ParameterExpression;
import io.cdep.cdep.utils.StringUtils;

public class CreateStringVisitor extends ReadonlyVisitor {
  private StringBuilder sb = new StringBuilder();
  private int indent = 0;

  private CreateStringVisitor(Expression expr) {
    visit(expr);
  }

  @NotNull
  public static String convert(Expression expr) {
    String result = new CreateStringVisitor(expr).sb.toString();
    while (result.startsWith("\n") || result.startsWith("\r")) {
      result = result.substring(1);
    }
    while (result.endsWith("\n") || result.endsWith("\r")) {
      result = result.substring(0, result.length() - 1);
    }
    return result;
  }

  @Override
  protected void visitFindModuleExpression(@NotNull FindModuleExpression expr) {
    append("\r\n");
    appendIndented("find(%s)", expr.coordinate);
    ++indent;
    visit(expr.body);
    --indent;
    append("\r\n");
    appendIndented("end_find");
    append("\r\n");
  }

  @Override
  protected void visitIfSwitchExpression(@NotNull IfSwitchExpression expr) {
    append("\r\n");
    appendIndented("");
    for (int i = 0; i < expr.conditions.length; ++i) {
      append("if(");
      visit(expr.conditions[i]);
      append(") ");
      ++indent;
      visit(expr.expressions[i]);
      --indent;
      append("\r\n");
      appendIndented("else ");
    }
    ++indent;
    visit(expr.elseExpression);
    --indent;
    append("\r\n");
    if (expr.conditions.length > 0) {
      appendIndented("end_if");
    }
  }

  @Override
  protected void visitAbortExpression(@NotNull AbortExpression expr) {
    Object parms[] = new String[expr.parameters.length];
    for (int i = 0; i < parms.length; ++i) {
      StringBuilder old = sb;
      sb = new StringBuilder();
      visit(expr.parameters[i]);
      parms[i] = sb.toString();
      sb = old;
    }

    append("\r\n");
    appendIndented("abort " + expr.message, parms);
  }

  @Override
  protected void visitAssignmentExpression(@NotNull AssignmentExpression expr) {
    append("\r\n");
    appendIndented("var %s = ", expr.name);
    visit(expr.expression);
  }

  @Override
  protected void visitInvokeFunctionExpression(@NotNull InvokeFunctionExpression expr) {
    append("%s(", expr.function.method.getName());
    for (int i = 0; i < expr.parameters.length; ++i) {
      if (i > 0) {
        append(", ");
      }
      visit(expr.parameters[i]);
    }
    append(")");
  }

  @Override
  protected void visitArray(@NotNull Expression[] array) {
    append("[");
    for (int i = 0; i < array.length; ++i) {
      if (i > 0) {
        append(", ");
      }
      visit(array[i]);
    }
    append("]");
  }

  @Override
  protected void visitParameterExpression(@NotNull ParameterExpression expr) {
    append(expr.name);
  }

  @Override
  protected void visitModuleExpression(@NotNull ModuleExpression expr) {
    append("\r\n");
    appendIndented("module");
    append("\r\n");
    ++indent;
    visit(expr.archive);
    --indent;
    appendIndented("end_module");
  }

  @Override
  protected void visitModuleArchiveExpression(@NotNull ModuleArchiveExpression expr) {
    if (expr.includePath != null) {
      appendIndent();
      append("include: ");
      visit(expr.includePath);
      append("\r\n");
    }
    if (expr.libraryPaths.length > 0) {
      appendIndent();
      append("libraries: [");
      visitArray(expr.libraryPaths);
      append("]\r\n");
    }
    appendIndent();
    append("requires: " + StringUtils.joinOn(", ", expr.requires));
    append("\r\n");
  }

  @Override
  protected void visitGlobalBuildEnvironmentExpression(@NotNull GlobalBuildEnvironmentExpression expr) {
    append("import %s\r\n", expr.cdepExplodedRoot.name);
    append("import %s\r\n", expr.buildSystemTargetSystem.name);
    append("import %s\r\n", expr.buildSystemTargetPlatform.name);
    append("import %s\r\n", expr.cdepDeterminedAndroidAbi.name);
    append("import %s\r\n", expr.cdepDeterminedAndroidRuntime.name);
    append("import %s\r\n", expr.cmakeOsxSysroot.name);
    append("import %s\r\n", expr.cmakeOsxArchitectures.name);
  }

  @Override
  public void visitAssignmentReferenceExpression(@NotNull AssignmentReferenceExpression expr) {
    append("*" + expr.assignment.name);
  }

  @Override
  protected void visitConstantExpression(@NotNull ConstantExpression expr) {
    append("'%s'", expr.value);
  }

  private void append(@NotNull String format, Object... parms) {
    sb.append(String.format(format, parms));
  }

  private void appendIndent() {
    String prefix = new String(new char[indent * 2]).replace('\0', ' ');
    sb.append(prefix);
  }

  private void appendIndented(@NotNull String format, Object... parms) {
    appendIndent();
    sb.append(String.format(format, parms));
  }
}
