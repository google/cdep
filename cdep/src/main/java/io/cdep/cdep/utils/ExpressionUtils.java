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
package io.cdep.cdep.utils;

import io.cdep.annotations.NotNull;
import io.cdep.annotations.Nullable;
import io.cdep.cdep.Coordinate;
import io.cdep.cdep.ReadonlyVisitor;
import io.cdep.cdep.ast.finder.Expression;
import io.cdep.cdep.ast.finder.FindModuleExpression;
import io.cdep.cdep.ast.finder.ModuleArchiveExpression;
import io.cdep.cdep.ast.finder.ModuleExpression;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Methods for dealing with FinderExpressions.
 */
abstract public class ExpressionUtils {
  /**
   * Traverse the given expression and locate all of the FoundModuleExpressions.
   * These expressions contain the local module location as well as the resolved coordinate
   * and other information
   */
  @NotNull
  public static Map<Coordinate, List<Expression>> getAllFoundModuleExpressions(@NotNull Expression expression) {
    return new Finder(expression).foundModules;
  }

  /**
   * Traverse the given expression and locate all of the FoundModuleExpressions.
   * These expressions contain the local module location as well as the resolved coordinate
   * and other information
   */
  @NotNull
  public static Set<String> findReferencedLibraryNames(@NotNull Expression expression) {
    return new Finder(expression).foundLibraryNames;
  }

  private static class Finder extends ReadonlyVisitor {
    @NotNull
    final private Map<Coordinate, List<Expression>> foundModules = new HashMap<>();
    @NotNull
    final private Set<String> foundLibraryNames = new HashSet<>();
    @Nullable
    private Coordinate coordinate;

    Finder(@NotNull Expression expression) {
      visit(expression);
    }

    @Override
    protected void visitModuleArchiveExpression(@NotNull ModuleArchiveExpression expr) {
      for (String lib : expr.libs) {
        foundLibraryNames.add(new File(lib).getName());
      }
      super.visitModuleArchiveExpression(expr);
    }

    @Override
    protected void visitModuleExpression(@NotNull ModuleExpression expr) {
      addModule(expr);
      super.visitModuleExpression(expr);
    }

    @Override
    protected void visitFindModuleExpression(@NotNull FindModuleExpression expr) {
      coordinate = expr.coordinate;
      super.visitFindModuleExpression(expr);
    }

    private void addModule(@NotNull Expression expression) {
      List<Expression> modules = foundModules.get(coordinate);
      if (modules == null) {
        modules = new ArrayList<>();
        foundModules.put(coordinate, modules);
        addModule(expression);
        return;
      }
      modules.add(expression);
    }
  }
}
