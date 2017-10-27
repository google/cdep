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

import static io.cdep.cdep.ast.finder.ExpressionBuilder.constant;

import io.cdep.annotations.NotNull;
import io.cdep.annotations.Nullable;
import io.cdep.cdep.RewritingVisitor;
import io.cdep.cdep.ast.finder.ArrayExpression;
import io.cdep.cdep.ast.finder.AssignmentReferenceExpression;
import io.cdep.cdep.ast.finder.ConstantExpression;
import io.cdep.cdep.ast.finder.Expression;
import io.cdep.cdep.ast.finder.ExternalFunctionExpression;
import io.cdep.cdep.ast.finder.InvokeFunctionExpression;
import io.cdep.cdep.ast.finder.ModuleExpression;
import io.cdep.cdep.ast.finder.ParameterExpression;
import java.util.Objects;

/**
 * Locate File.join statements and join them into strings.
 */
public class JoinedFileToStringRewriter extends RewritingVisitor {
  private final String left;
  private final String right;

  public JoinedFileToStringRewriter(String left, String right) {
    this.left = left;
    this.right = right;
  }

  @NotNull
  @Override
  protected Expression visitInvokeFunctionExpression(@NotNull InvokeFunctionExpression expr) {
    if (Objects.equals(expr.function, ExternalFunctionExpression.FILE_JOIN_SEGMENTS)) {
      String value = getUnquotedConcatenation(expr.parameters[0], "/");
      value += "/";
      value += getUnquotedConcatenation(expr.parameters[1], "/");
      return constant(value);

    }
    return super.visitInvokeFunctionExpression(expr);
  }

  @Override
  protected Expression visitModuleExpression(@NotNull ModuleExpression expr) {
    return super.visitModuleExpression(expr);
  }

  /**
   * If a constant the return xyz without quotes.
   * If an assignment reference then return ${xyz}.
   */
  @Nullable
  private String getUnquotedConcatenation(Expression expr, String joinOn) {
    if (expr instanceof ConstantExpression) {
      return ((ConstantExpression) expr).value.toString();
    }
    if (expr instanceof AssignmentReferenceExpression) {
      return String.format("$%s%s%s", left, ((AssignmentReferenceExpression) expr).assignment.name, right);
    }
    if (expr instanceof ParameterExpression) {
      return String.format("$%s%s%s", left, ((ParameterExpression) expr).name, right);
    }
    if (expr instanceof ArrayExpression) {
      ArrayExpression specific = (ArrayExpression) expr;
      String result = "";
      for (int i = 0; i < specific.elements.length; ++i) {
        if (i > 0) {
          result += joinOn;
        }
        result += getUnquotedConcatenation(specific.elements[i], joinOn);
      }
      return result;
    }
    throw new RuntimeException(expr.getClass().toString());
  }
}
