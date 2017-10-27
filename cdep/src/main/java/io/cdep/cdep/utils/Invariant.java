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
import org.yaml.snakeyaml.nodes.Node;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static io.cdep.cdep.io.IO.errorln;
import static io.cdep.cdep.utils.StringUtils.safeFormat;

/*
 * Methods for ensuring state at runtime
 */
abstract public class Invariant {
  private static final LinkedList<List<CDepRuntimeException>> requirementFailures = new LinkedList<>();
  private static final LinkedList<Boolean> showOutputs = new LinkedList<>();
  private static final LinkedList<LinkedList<Node>> yamlExplictNode = new LinkedList<>();
  private static final LinkedList<LinkedList<String>> yamlFiles = new LinkedList<>();
  private static final LinkedList<LinkedList<Map<Object, Node>>> yamlNodes = new LinkedList<>();

  public static void registerYamlFile(String file) {
    registerYamlNodes(file, new HashMap<Object, Node>());
  }

  public static void registerYamlNodes(String file, Map<Object, Node> yamlNodes) {
    if (Invariant.yamlFiles.size() == 0) {
      return;
    }
    Invariant.yamlFiles.get(0).push(file);
    Invariant.yamlNodes.get(0).push(yamlNodes);
  }

  public static void pushErrorCollectionScope(boolean showOutput) {
    yamlExplictNode.push(new LinkedList<Node>());
    yamlFiles.push(new LinkedList<String>());
    yamlNodes.push(new LinkedList<Map<Object, Node>>());
    requirementFailures.push(new ArrayList<CDepRuntimeException>());
    showOutputs.push(showOutput);
  }

  public static List<CDepRuntimeException> popErrorCollectionScope() {
    List<CDepRuntimeException> errors = requirementFailures.get(0);
    yamlExplictNode.pop();
    yamlFiles.pop();
    yamlNodes.pop();
    requirementFailures.pop();
    showOutputs.pop();
    return errors;
  }

  public static int errorsInScope() {
    if (requirementFailures.size() == 0) {
      return 0;
    }
    return requirementFailures.get(0).size();
  }

  private static void report(@NotNull CDepRuntimeException e) {
    if (requirementFailures.size() == 0) {
      throw e;
    }
    if (showOutputs.get(0)) {
      errorln(e.errorInfo, e.getMessage());
    }
    requirementFailures.get(0).add(e);
  }

  public static void fail(@NotNull String format) {
    report(new CDepRuntimeException(format, getBestErrorInfo(format, new Object[0])));
  }

  public static void fail(@NotNull String format, Object... parameters) {
    ErrorInfo errorInfo = getBestErrorInfo(format, parameters);
    report(new CDepRuntimeException(safeFormat(format, parameters), errorInfo));
  }

  public static void require(boolean check, @NotNull String format, Object... parameters) {
    if (check) {
      return;
    }
    ErrorInfo errorInfo = getBestErrorInfo(format, parameters);
    report(new CDepRuntimeException(safeFormat(format, parameters), errorInfo));
  }

  public static boolean failIf(boolean check, @NotNull String format, Object... parameters) {
    if (!check) {
      return false;
    }
    ErrorInfo errorInfo = getBestErrorInfo(format, parameters);
    report(new CDepRuntimeException(safeFormat(format, parameters), errorInfo));
    return true;
  }

  public static void require(boolean check) {
    if (!check) {
      report(new CDepRuntimeException("Invariant violation", getBestErrorInfo("Invariant violation", new Object[0])));
    }
  }

  @NotNull
  private static ErrorInfo getBestErrorInfo(@NotNull String format, @Nullable Object... parameters) {
    String code;
    try {
      code = HashUtils.getSHA256OfString(format).substring(0, 7);
    } catch (NoSuchAlgorithmException e) {
      code = "no-algo";
    } catch (IOException e) {
      code = "io-failed";
    }


    if (yamlFiles.size() == 0 || yamlFiles.get(0).size() == 0) {
      return new ErrorInfo(null, null, code);
    }
    if (parameters != null) {
      for (int i = 0; i < yamlFiles.get(0).size(); ++i) {
        Map<Object, Node> map = yamlNodes.get(0).get(i);
        for (int j = 0; j < parameters.length; ++j) {
          Node node = map.get(parameters[j]);
          if (node != null) {
            return new ErrorInfo(yamlFiles.get(0).get(i), node.getStartMark().getLine() + 1, code);
          }
        }
      }
    }

    // If no objects matched then just return a file name
    return new ErrorInfo(yamlFiles.get(0).get(0), null, code);
  }
}
