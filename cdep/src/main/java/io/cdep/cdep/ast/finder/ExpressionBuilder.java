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

import static io.cdep.cdep.utils.Invariant.require;

import io.cdep.annotations.NotNull;
import io.cdep.annotations.Nullable;
import io.cdep.cdep.Coordinate;
import io.cdep.cdep.yml.cdepmanifest.CxxLanguageFeatures;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Methods for creating expression trees
 */
@SuppressWarnings("unused")
public class ExpressionBuilder {
  @NotNull
  public static AssignmentBlockExpression assignmentBlock(@NotNull List<AssignmentExpression> assignments,
      @NotNull StatementExpression statement) {
    return new AssignmentBlockExpression(assignments, statement);
  }

  @NotNull
  public static AssignmentBlockExpression assignmentBlock(@NotNull AssignmentExpression assignment,
      @NotNull StatementExpression statement) {
    List<AssignmentExpression> assignments = new ArrayList<>();
    assignments.add(assignment);
    return new AssignmentBlockExpression(assignments, statement);
  }

  @NotNull
  public static ModuleArchiveExpression archive(@NotNull URL file,
      @NotNull String sha256,
      @NotNull Long size,
      @Nullable String include,
      @Nullable Expression includePath,
      @NotNull String libs[],
      @NotNull Expression libraryPaths[],
      @NotNull CxxLanguageFeatures requires[]) {
    return new ModuleArchiveExpression(file, sha256, size, include, includePath, libs, libraryPaths, requires);
  }

  @NotNull
  public static ModuleExpression module(@NotNull ModuleArchiveExpression archive, @Nullable Set<Coordinate> dependencies) {
    if (dependencies == null) {
      dependencies = new HashSet<>();
    }
    return new ModuleExpression(archive, dependencies);
  }

  @NotNull
  public static AssignmentReferenceExpression reference(@NotNull AssignmentExpression assignment) {
    return new AssignmentReferenceExpression(assignment);
  }

  @NotNull
  public static IfSwitchExpression ifSwitch(@NotNull List<Expression> conditionList,
      @NotNull List<Expression> expressionList,
      @NotNull Expression elseExpression) {
    require(conditionList.size() == expressionList.size());
    int size = conditionList.size();
    Expression conditions[] = new Expression[size];
    Expression expressions[] = new Expression[size];
    for (int i = 0; i < size; ++i) {
      conditions[i] = conditionList.get(i);
      expressions[i] = expressionList.get(i);
    }
    return ifSwitch(conditions, expressions, elseExpression);
  }

  @NotNull
  public static IfSwitchExpression ifSwitch(@NotNull Expression condition,
      @NotNull Expression trueExpression,
      @NotNull Expression falseExpression) {
    Expression conditions[] = new Expression[]{condition};
    Expression expressions[] = new Expression[]{trueExpression};
    return ifSwitch(conditions, expressions, falseExpression);
  }

  @NotNull
  public static IfSwitchExpression ifSwitch(@NotNull Expression conditions[],
      @NotNull Expression expressions[],
      @NotNull Expression elseExpression) {
    return new IfSwitchExpression(conditions, expressions, elseExpression);
  }

  @NotNull
  public static InvokeFunctionExpression invoke(
      @NotNull ExternalFunctionExpression function,
      @NotNull Expression... parameters) {
    return new InvokeFunctionExpression(function, parameters);
  }

  /**
   * Returns true if expression left is greater than or equal to integer right.
   */
  @NotNull
  public static InvokeFunctionExpression gte(Expression left, int right) {
    return invoke(ExternalFunctionExpression.INTEGER_GTE, left, constant(right));
  }

  /**
   * Logical not
   */
  @NotNull
  public static InvokeFunctionExpression not(Expression value) {
    return invoke(ExternalFunctionExpression.NOT, value);
  }

  /**
   * Logical or
   */
  @NotNull
  public static InvokeFunctionExpression or(Expression left, Expression right) {
    return invoke(ExternalFunctionExpression.OR, left, right);
  }

  /**
   * Returns true if the given parameter is defined.
   */
  @NotNull
  public static InvokeFunctionExpression requireMinimumCxxCompilerStandard(Expression expr) {
    return invoke(ExternalFunctionExpression.REQUIRE_MINIMUM_CXX_COMPILER_STANDARD, expr);
  }

  /**
   * Returns true if the current build system supports compiler features.
   */
  @NotNull
  public static InvokeFunctionExpression supportsCompilerFeatures() {
    return invoke(ExternalFunctionExpression.SUPPORTS_COMPILER_FEATURES);
  }

  /**
   * Require the given Cxx compiler features.
   */
  @NotNull
  public static InvokeFunctionExpression requiresCompilerFeatures(ArrayExpression array) {
    return invoke(ExternalFunctionExpression.REQUIRES_COMPILER_FEATURES, array);
  }

  /**
   * Return true if constant starts with find.
   */
  @NotNull
  public static InvokeFunctionExpression stringStartsWith(@NotNull Expression string, @NotNull Expression find) {
    return invoke(ExternalFunctionExpression.STRING_STARTSWITH, string, find);
  }

  /**
   * Return true if the given array has just one element and it is the given value.
   */
  @NotNull
  public static InvokeFunctionExpression arrayHasOnlyElement(@NotNull Expression array, @NotNull Expression value) {
    return invoke(ExternalFunctionExpression.ARRAY_HAS_ONLY_ELEMENT, array, value);
  }

  /**
   * Extract a substring.
   */
  @NotNull
  public static InvokeFunctionExpression substring(@NotNull Expression string,
      @NotNull Expression start,
      @NotNull Expression end) {
    return invoke(ExternalFunctionExpression.STRING_SUBSTRING_BEGIN_END, string, start, end);
  }

  /**
   * Return the last index of constant inside of constant.
   */
  @SuppressWarnings("SameParameterValue")
  @NotNull
  public static InvokeFunctionExpression lastIndexOfString(@NotNull Expression string, @NotNull String value) {
    return invoke(ExternalFunctionExpression.STRING_LASTINDEXOF, string, constant(value));
  }

  /**
   * Given a file with path, return just the filename with extension.
   */
  @NotNull
  public static InvokeFunctionExpression getFileName(@NotNull Expression file) {
    return invoke(ExternalFunctionExpression.FILE_GETNAME, file);
  }

  @NotNull
  public static InvokeFunctionExpression eq(@NotNull Expression left, @NotNull Expression right) {
    return invoke(ExternalFunctionExpression.STRING_EQUALS, left, right);
  }

  @NotNull
  public static AssignmentExpression assign(@NotNull String name, @NotNull Expression expression) {
    return new AssignmentExpression(name, expression);
  }

  @NotNull
  public static AbortExpression abort(@NotNull String message, @NotNull Expression... parameters) {
    return new AbortExpression(message, parameters);
  }

  @NotNull
  public static ArrayExpression array(@NotNull Expression... expressions) {
    return new ArrayExpression(expressions);
  }

  @NotNull
  public static ConstantExpression constant(@Nullable Object value) {
    assert value != null;
    return new ConstantExpression(value);
  }

  @NotNull
  private static ArrayExpression array(@NotNull String... elements) {
    Expression array[] = new Expression[elements.length];
    for (int i = 0; i < elements.length; ++i) {
      array[i] = constant(elements[i]);
    }
    return array(array);
  }

  @NotNull
  public static Expression joinFileSegments(@NotNull Expression root, @NotNull String... segments) {
    return invoke(ExternalFunctionExpression.FILE_JOIN_SEGMENTS, root, array(segments));
  }

  @NotNull
  public static Expression joinFileSegments(@NotNull Expression root, @NotNull Expression... segments) {
    return invoke(ExternalFunctionExpression.FILE_JOIN_SEGMENTS, root, array(segments));
  }

  @NotNull
  public static MultiStatementExpression multi(@NotNull StatementExpression... statements) {
    return new MultiStatementExpression(statements);
  }

  @NotNull
  public static NopExpression nop() {
    return new NopExpression();
  }
}
