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

import io.cdep.cdep.Version;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

public class CoordinateUtils {
  final private static Pattern pattern = compile("^(.*):(.*):(.*)$");

  @Nullable
  public static Coordinate tryParse(@NotNull String value) {
    Matcher match = pattern.matcher(value);
    if (!match.find()) {
      return null;
    }
    String groupId = match.group(1);
    String artifactId = match.group(2);
    String version = match.group(3);
    return new Coordinate(groupId, artifactId, new Version(version));
  }
}