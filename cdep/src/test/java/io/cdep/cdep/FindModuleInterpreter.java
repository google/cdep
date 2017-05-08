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

import static io.cdep.cdep.ast.finder.ExpressionBuilder.constant;
import static io.cdep.cdep.utils.Invariant.require;

import io.cdep.annotations.NotNull;
import io.cdep.annotations.Nullable;
import io.cdep.cdep.InterpretingVisitor.ModuleArchive;
import io.cdep.cdep.ast.finder.AssignmentBlockExpression;
import io.cdep.cdep.ast.finder.FindModuleExpression;
import io.cdep.cdep.ast.finder.FunctionTableExpression;
import io.cdep.cdep.ast.finder.NopExpression;
import io.cdep.cdep.ast.finder.ParameterExpression;
import io.cdep.cdep.ast.finder.StatementExpression;

class FindModuleInterpreter {
  private static FindModuleExpression getFindFunction(StatementExpression statement) {
    if (statement instanceof FindModuleExpression) {
      return (FindModuleExpression) statement;
    }
    if (statement instanceof AssignmentBlockExpression) {
      return getFindFunction(((AssignmentBlockExpression) statement).statement);
    }
    throw new RuntimeException(statement.getClass().toString());
  }
  @Nullable
  @SuppressWarnings("SameParameterValue")
  static ModuleArchive findAndroid(@NotNull final FunctionTableExpression table, Coordinate functionName, final String
      cdepExplodedRoot, final String targetPlatform, final String systemVersion, // On android, platform like 21
      final String androidStlType, final String androidTargetAbi) {
    final FindModuleExpression function = getFindFunction(table.findFunctions.get(functionName));
    return toModuleArchive(new InterpretingVisitor() {
      @Override
      protected Object visitParameterExpression(@NotNull ParameterExpression expr) {
        if (expr == table.globals.buildSystemTargetSystem) {
          return targetPlatform;
        }
        if (expr == table.globals.buildSystemTargetPlatform) {
          return systemVersion;
        }
        if (expr == table.globals.cdepDeterminedAndroidRuntime) {
          return androidStlType;
        }
        if (expr == table.globals.cdepDeterminedAndroidAbi) {
          return androidTargetAbi;
        }
        if (expr == table.globals.cdepExplodedRoot) {
          return cdepExplodedRoot;
        }
        if (expr == table.globals.buildSystemNoneRuntime) {
          return constant("none");
        }
        return super.visitParameterExpression(expr);
      }
    }.visit(function));
  }

  @Nullable
  static ModuleArchive findiOS(@NotNull final FunctionTableExpression table, Coordinate functionName, final String cdepExplodedRoot,
      final String osxArchitectures[], final String osxSysroot) {
    final FindModuleExpression function = getFindFunction(table.findFunctions.get(functionName));
    return toModuleArchive(new InterpretingVisitor() {
      @Override
      protected Object visitParameterExpression(@NotNull ParameterExpression expr) {
        if (expr == table.globals.buildSystemTargetSystem) {
          return "Darwin";
        }
        if (expr == table.globals.cmakeOsxSysroot) {
          return osxSysroot;
        }
        if (expr == table.globals.cdepExplodedRoot) {
          return cdepExplodedRoot;
        }
        if (expr == table.globals.cmakeOsxArchitectures) {
          return osxArchitectures;
        }
        return super.visitParameterExpression(expr);
      }
    }.visit(function));
  }

  @Nullable
  private static ModuleArchive toModuleArchive(Object value) {
    if (value instanceof ModuleArchive) {
      return (ModuleArchive) value;
    }
    if (value instanceof Object[]) {
      ModuleArchive found = null;
      for (Object object : (Object[]) value) {
        if (object instanceof ModuleArchive) {
          require(found == null);
          found = (ModuleArchive) object;
          continue;
        }
        require(object instanceof NopExpression);
      }
      require(found != null);
      return found;
    }
    throw new RuntimeException(value.getClass().toString());
  }

  @Nullable
  static ModuleArchive findLinux(@NotNull final FunctionTableExpression table, Coordinate functionName, final String cdepExplodedRoot) {
    final FindModuleExpression function = getFindFunction(table.findFunctions.get(functionName));
    return toModuleArchive(new InterpretingVisitor() {
      @Override
      protected Object visitParameterExpression(@NotNull ParameterExpression expr) {
        if (expr == table.globals.buildSystemTargetSystem) {
          return "Linux";
        }
        if (expr == table.globals.cdepExplodedRoot) {
          return cdepExplodedRoot;
        }
        return super.visitParameterExpression(expr);
      }
    }.visit(function));
  }
}
