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
package io.cdep.cdep.fullfill;

import io.cdep.annotations.NotNull;
import io.cdep.cdep.utils.FileUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Converts path patterns in cdep-manifest.yml skeleton to a collection of files.
 *
 * Grammar:
 *
 *   Individual file:
 *     - file: path/to/libmylib.a
 *
 *   All subfiles from here:
 *     - file: path/to/files/...
 *
 *   Combine:
 *     - file: a.lib | b.lib
 *
 *   Remap:
 *     - file: a.lib -> somepath/a.lib
 *
 */
public class PathMapping {
  final public File from;
  final public File to;

  private PathMapping(File from, File to) {
    this.from = from;
    this.to = to;
  }

  /**
   * Returns a collection of from-to path mappings.
   */
  public static PathMapping[] parse(@NotNull String text) {
    List<PathMapping> result = new ArrayList<>();
    String[] mappings = text.split("\\|");
    for (String mapping : mappings) {
      String[] fromTo = mapping.split("->");
      if (fromTo.length == 1) {
        if (fromTo[0].endsWith("/...")) {
          // Have some like path/...
          File baseFolder = new File(fromTo[0].substring(0, fromTo[0].length() - 4));
          for (File from : FileUtils.listFileTree(baseFolder)) {
            File to = new File(from.getPath().substring(baseFolder.getPath().length() + 1));
            result.add(new PathMapping(from, to));
          }
        } else {
          result.add(new PathMapping(
              new File(fromTo[0].trim()),
              new File(new File(fromTo[0].trim()).getName())));
        }
      } else if (fromTo.length == 2) {
        result.add(new PathMapping(
            new File(fromTo[0].trim()), new File(
            fromTo[1].trim())));
      }
    }
    return result.toArray(new PathMapping[result.size()]);
  }
}
