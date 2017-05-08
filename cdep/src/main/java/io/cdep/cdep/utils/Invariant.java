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

import java.util.ArrayList;
import java.util.List;

import static io.cdep.cdep.io.IO.errorln;
import static io.cdep.cdep.utils.StringUtils.safeFormat;

/**
 * Methods for ensuring state at runtime
 */
abstract public class Invariant {
  private static final List<List<RuntimeException>> requirementFailures = new ArrayList<>();
  private static final List<Boolean> showOutputs = new ArrayList<>();

  @SuppressWarnings("Convert2Diamond")
  public static void pushScope() {
    pushScope(true);
  }

  public static void pushScope(boolean showOutput) {
    //noinspection Convert2Diamond
    requirementFailures.add(new ArrayList<RuntimeException>());
    showOutputs.add(showOutput);
  }

  public static List<RuntimeException> popScope() {
    List<RuntimeException> errors = requirementFailures.get(0);
    requirementFailures.remove(0);
    showOutputs.remove(0);
    return errors;
  }

  public static int errorsInScope() {
    if (requirementFailures.size() == 0) {
      return 0;
    }
    return requirementFailures.get(0).size();
  }

  private static void report(@NotNull RuntimeException e) {
    if (requirementFailures.size() == 0) {
      throw e;
    }
    if (showOutputs.get(0)) {
      errorln(e.getMessage());
    }
    requirementFailures.get(0).add(e);
  }

  public static void fail(@NotNull String format) {
    report(new RuntimeException(format));
  }

  public static void fail(@NotNull String format, Object... parameters) {
    report(new RuntimeException(safeFormat(format, parameters)));
  }

  public static void require(boolean check, @NotNull String format, Object... parameters) {
    if (check) {
      return;
    }
    report(new RuntimeException(safeFormat(format, parameters)));
  }

  public static boolean failIf(boolean check, @NotNull String format, Object... parameters) {
    if (!check) {
      return false;
    }
    report(new RuntimeException(safeFormat(format, parameters)));
    return true;
  }

  public static void require(boolean check) {
    if (!check) {
      report(new RuntimeException("Invariant violation"));
    }
  }
}
