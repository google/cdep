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

import static io.cdep.cdep.utils.Invariant.fail;
import static io.cdep.cdep.utils.Invariant.require;
import static io.cdep.cdep.utils.ReflectionUtils.invoke;
import static io.cdep.cdep.utils.StringUtils.safeFormat;

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
import io.cdep.cdep.yml.cdepmanifest.CxxLanguageFeatures;
import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Walks the expression tree and interprets the constant for the supplied state.
 */
@SuppressWarnings("unused")
public class InterpretingVisitor {

  @Nullable
  private Frame stack = null;

  @Nullable
  private static Object coerce(@Nullable Object o, @NotNull Class<?> clazz) {
    if (o == null) {
      return null;
    }
    if (clazz.isInstance(o)) {
      return o;
    }
    if (clazz.equals(File.class)) {
      if (o instanceof String) {
        return new File((String) o);
      }
    }
    if (clazz.equals(int.class)) {
      if (o instanceof Integer) {
        return o;
      }
      if (o instanceof String) {
        return Integer.parseInt((String) o);
      }
    }
    if (clazz.equals(String[].class)) {
      if (o instanceof Object[]) {
        Object objarr[] = (Object[]) o;
        String result[] = new String[objarr.length];
        for (int i = 0; i < result.length; ++i) {
          result[i] = (String) coerce(objarr[i], String.class);
        }
        return result;
      }
    }
    if (clazz.equals(String.class)) {
      if (o instanceof Integer) {
        return o.toString();
      }
      if (o instanceof File) {
        return o.toString();
      }
      return o.toString();
    }
    if (clazz.equals(CxxLanguageFeatures[].class) && o instanceof Object[]) {
      Object specific[] = (Object[]) o;
      CxxLanguageFeatures requires[] = new CxxLanguageFeatures[specific.length];
      for (int i = 0; i < requires.length; ++i) {
        requires[i] = (CxxLanguageFeatures) specific[i];
      }
      return requires;
    }
    fail("Did not coerce %s to %s", o.getClass(), clazz);
    return null;
  }

  @Nullable
  public Object visit(@Nullable Expression expr) {
    if (expr == null) {
      return null;
    }

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
      return visitValueExpression((ConstantExpression) expr);
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
    if (expr.getClass().equals(AssignmentBlockExpression.class)) {
      return visitAssignmentBlockExpression((AssignmentBlockExpression) expr);
    }
    if (expr.getClass().equals(AssignmentReferenceExpression.class)) {
      return visitAssignmentReferenceExpression((AssignmentReferenceExpression) expr);
    }
    if (expr.getClass().equals(ModuleArchiveExpression.class)) {
      return visitModuleArchiveExpression((ModuleArchiveExpression) expr);
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
    throw new RuntimeException("intr" + expr.getClass().toString());
  }

  @Nullable
  @SuppressWarnings("SameReturnValue")
  private Object visitGlobalBuildEnvironmentExpression(GlobalBuildEnvironmentExpression expr) {
    return null;
  }

  @NotNull
  ModuleArchive visitModuleArchiveExpression(@NotNull ModuleArchiveExpression expr) {
    Object fullIncludePath = visit(expr.includePath);
    File fullLibraryNames[] = visitArray(expr.libraryPaths, File.class);
    return new ModuleArchive(expr.file, (File) fullIncludePath, fullLibraryNames);
  }

  @NotNull
  private Object visitAssignmentReferenceExpression(@NotNull AssignmentReferenceExpression expr) {
    assert stack != null;
    AssignmentFuture future = stack.lookup(expr.assignment);
    if (future.value == null) {
      Frame oldStack = stack;
      stack = future.stack;
      future.value = visit(future.expr);
      stack = oldStack;
      return visitAssignmentReferenceExpression(expr);
    }
    return future.value;
  }

  @Nullable
  private Object visitAssignmentBlockExpression(@NotNull AssignmentBlockExpression expr) {
    stack = new Frame(stack);
    for (AssignmentExpression assignment : expr.assignments) {
      visitAssignmentExpression(assignment);
    }
    visit(expr.statement);
    Object result = visit(expr.statement);
    stack = stack.prior;
    return result;
  }

  @NotNull
  private Object visitMultiStatementExpression(@NotNull MultiStatementExpression expr) {
    return visitArray(expr.statements, Object.class);
  }

  @NotNull
  private Object visitArrayExpression(@NotNull ArrayExpression expr) {
    return visitArray(expr.elements, Object.class);
  }

  private Method visitExternalFunctionExpression(@NotNull ExternalFunctionExpression expr) {
    return expr.method;
  }

  @SuppressWarnings("SameReturnValue")
  @Nullable
  private Object visitExampleExpression(ExampleExpression expr) {
    return null;
  }

  @SuppressWarnings("SameReturnValue")
  @Nullable
  Object visitAbortExpression(@NotNull AbortExpression expr) {
    Object parameters[] = (Object[]) coerce(visitArray(expr.parameters, Object.class), String[].class);
    String message = safeFormat("Abort: " + expr.message, parameters);
    fail(message);
    return message;
  }

  @Nullable
  private ModuleArchive visitModuleExpression(@NotNull ModuleExpression expr) {
    return (ModuleArchive) visit(expr.archive);
  }

  private Object visitNopExpression(NopExpression expr) {
    return expr;
  }

  private Object visitInvokeFunctionExpression(@NotNull InvokeFunctionExpression expr) {
    Method method = visitExternalFunctionExpression(expr.function);
    Object parameters[] = visitArray(expr.parameters, Object.class);

    Object thiz = null;
    int firstParameter = 0;
    if (!Modifier.isStatic(method.getModifiers())) {
      thiz = coerce(parameters[0], method.getDeclaringClass());
      ++firstParameter;
    }
    Object parms[] = new Object[expr.parameters.length - firstParameter];
    for (int i = firstParameter; i < expr.parameters.length; ++i) {
      parms[i - firstParameter] = coerce(parameters[i], method.getParameterTypes()[i - firstParameter]);
    }
    return invoke(method, thiz, parms);
  }

  @NotNull
  private <T> T[] visitArray(@NotNull Expression[] array, Class<T> clazz) {
    @SuppressWarnings("unchecked") T result[] = (T[]) Array.newInstance(clazz, array.length);
    for (int i = 0; i < array.length; ++i) {
      //noinspection unchecked
      result[i] = (T) visit(array[i]);
    }
    return result;
  }

  @SuppressWarnings("SameReturnValue")
  @Nullable
  private Object visitAssignmentExpression(@NotNull AssignmentExpression expr) {
    assert stack != null;
    stack.assignments.put(expr, new AssignmentFuture(stack, expr.expression));
    return null;
  }

  @NotNull
  private Object visitValueExpression(@NotNull ConstantExpression expr) {
    return expr.value;
  }

  @Nullable
  Object visitIfSwitchExpression(@NotNull IfSwitchExpression expr) {
    for (int i = 0; i < expr.conditions.length; ++i) {
      Object condition = visit(expr.conditions[i]);
      assert condition != null;
      require(Boolean.class.isAssignableFrom(condition.getClass()),
          "Value of type '%s' was not assignable to boolean",
          condition.getClass());
      if ((boolean) condition) {
        Object result = visit(expr.expressions[i]);
        require(result != null, "Expected %s to not return null", expr.expressions[i]);
        return result;
      }
    }
    Object result = visit(expr.elseExpression);
    require(result != null, "Expected %s to not return null", expr.elseExpression);
    return result;
  }

  Object visitParameterExpression(@NotNull ParameterExpression expr) {
    throw new RuntimeException("Need to bind " + expr.name);
  }

  @Nullable
  Object visitFindModuleExpression(@NotNull FindModuleExpression expr) {
    stack = new Frame(stack);
    Object result = visit(expr.body);
    stack = stack.prior;
    return result;
  }

  @SuppressWarnings("SameReturnValue")
  @Nullable
  private Object visitFunctionTableExpression(@NotNull FunctionTableExpression expr) {
    visit(expr.globals);
    for (Coordinate coordinate : expr.findFunctions.keySet()) {
      stack = new Frame(stack);
      visit(expr.findFunctions.get(coordinate));
      stack = stack.prior;
    }
    for (Coordinate coordinate : expr.examples.keySet()) {
      visit(expr.examples.get(coordinate));
    }
    return null;
  }

  private static class AssignmentFuture {

    public Expression expr;
    @Nullable
    public Object value;
    public Frame stack;

    AssignmentFuture(Frame stack, Expression expr) {
      if (expr instanceof AssignmentExpression) {
        throw new RuntimeException();
      }
      this.expr = expr;
      this.value = null;
      this.stack = stack;
    }
  }

  private static class Frame {
    final public Frame prior;

    @NotNull
    final public Map<AssignmentExpression, AssignmentFuture> assignments;

    Frame(Frame prior) {
      this.prior = prior;
      this.assignments = new HashMap<>();
    }

    AssignmentFuture lookup(@NotNull AssignmentExpression assignment) {
      AssignmentFuture value = assignments.get(assignment);
      if (value == null) {
        require(prior != null, "Could not resolve '%s", assignment.name);
        assert prior != null;
        return prior.lookup(assignment);
      }
      return value;
    }
  }

  static class ModuleArchive {

    final public URL remote;
    final public File fullIncludePath;
    final public File fullLibraryNames[];

    ModuleArchive(URL remote, File fullIncludePath, File fullLibraryNames[]) {
      this.remote = remote;
      this.fullIncludePath = fullIncludePath;
      this.fullLibraryNames = fullLibraryNames;
    }
  }
}
