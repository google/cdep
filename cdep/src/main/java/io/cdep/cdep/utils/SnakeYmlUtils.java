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
import org.yaml.snakeyaml.nodes.*;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;

import static io.cdep.cdep.utils.Invariant.fail;

final class SnakeYmlUtils {
  /**
   * Given a deserialized object and the corresponding SnakeYML node. Construct a map of object field value to node.
   * The purpose is to map data to line number for error message purposes.
   */
  static <T> void mapAndRegisterNodes(@NotNull String url, @NotNull T object, @NotNull Node node) {
    LinkedHashMap<Object, Node> nodeMap = new LinkedHashMap<>();
    mapAndRegisterNodes(object, node, nodeMap);
    Invariant.registerYamlNodes(url, nodeMap);
  }

  private static void mapAndRegisterNodes(@NotNull Object object, @NotNull Node node, @NotNull LinkedHashMap<Object, Node> nodes) {
    nodes.put(object, node);
    if (node.getClass().isAssignableFrom(MappingNode.class)) {
      MappingNode concrete = (MappingNode) node;
      for (NodeTuple tuple : concrete.getValue()) {
        ScalarNode scalarKey = (ScalarNode) tuple.getKeyNode();
        String name = scalarKey.getValue();
        for (Field field : object.getClass().getFields()) {
          if (field.getName().equals(name)) {
            try {
              mapAndRegisterNodes(field.get(object), tuple.getValueNode(), nodes);
            } catch (IllegalAccessException e) {
              e.printStackTrace();
            }
          }
        }
      }
    } else if (node.getClass().isAssignableFrom(SequenceNode.class)) {
      SequenceNode concrete = (SequenceNode) node;
      List<Node> sequence = concrete.getValue();
      Object[] values = (Object[]) object;
      for (int i = 0; i < Math.min(values.length, sequence.size()); ++i) {
        mapAndRegisterNodes(values[i], sequence.get(0), nodes);
      }
    } else if (node.getClass().isAssignableFrom(ScalarNode.class)) {
      // Node has already been put in the map.
    } else {
      fail("unexpected snakeyml node type %s", node.getClass());
    }
  }

}
