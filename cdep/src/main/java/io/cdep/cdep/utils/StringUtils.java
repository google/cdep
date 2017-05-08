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
import java.util.Collection;
import java.util.IllegalFormatException;

public class StringUtils {

  public static boolean isNumeric(@NotNull String str) {
    for (char c : str.toCharArray()) {
      if (!Character.isDigit(c)) {
        return false;
      }
    }
    return true;
  }

  public static String joinOn(String delimiter, @NotNull Object array[]) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < array.length; ++i) {
      if (i != 0) {
        sb.append(delimiter);
      }
      sb.append(array[i]);
    }
    return sb.toString();
  }

  public static String joinOn(String delimiter, @NotNull Collection<String> strings) {
    StringBuilder sb = new StringBuilder();
    int i = 0;
    for (String string : strings) {
      if (i != 0) {
        sb.append(delimiter);
      }
      sb.append(string);
      ++i;
    }
    return sb.toString();
  }

  public static String joinOn(String delimiter, @NotNull String ... strings) {
    StringBuilder sb = new StringBuilder();
    int i = 0;
    for (String string : strings) {
      if (i != 0) {
        sb.append(delimiter);
      }
      sb.append(string);
      ++i;
    }
    return sb.toString();
  }

  public static String joinOnSkipNullOrEmpty(String delimiter, @NotNull String ... strings) {
    StringBuilder sb = new StringBuilder();
    int i = 0;
    for (String string : strings) {
      if (string == null || string.isEmpty()) {
        continue;
      }
      if (i != 0) {
        sb.append(delimiter);
      }
      sb.append(string);
      ++i;
    }
    return sb.toString();
  }

  @NotNull
  public static String nullToEmpty(@Nullable String value) {
    if (value == null) {
      return "";
    }
    return value;
  }

  @NotNull
  public static String[] singletonArrayOrEmpty(@Nullable String value) {
    if (value == null) {
      return new String[0];
    }
    return new String[] {value};
  }

  @NotNull
  public static String whitespace(int indent) {
    return new String(new char[indent ]).replace('\0', ' ');
  }

  @NotNull
  public static String safeFormat(@NotNull String format, Object ... args) {
    try {
      return String.format(format, args);
    } catch (IllegalFormatException e) {
      // It looks like format characters coming from the user in some way.
      // Provide a safe alternative that has all the same information.
      return format + " with parameters {" + joinOn(", ", args) + "}";
    }
  }

  public static boolean containsAny(@NotNull String value, @NotNull String chars) {
    for (Character c : chars.toCharArray()) {
      if (value.contains(c.toString())) {
        return true;
      }
    }
    return false;
  }

  @NotNull
  public static String firstAvailable(@NotNull String value, int n) {
    //noinspection ConstantConditions
    if (value == null) {
      //noinspection ReturnOfNull
      return null;
    }
    return value.substring(0, Math.min(n, value.length()));
  }

  public static boolean startsWithAny(@NotNull String value, @NotNull String chars) {
    for (Character c : chars.toCharArray()) {
      if (value.startsWith(c.toString())) {
        return true;
      }
    }
    return false;
  }
}
