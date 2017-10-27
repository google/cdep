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
import io.cdep.cdep.Version;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class VersionUtils {
  final public static Comparator<Version> ASCENDING_COMPARATOR = new VersionComparator(true);
  final public static Comparator<Version> DESCENDING_COMPARATOR = new VersionComparator(false);

  /**
   * Check the validity of a version
   * @param version the version to check
   * @return null if valid, otherwise a message.
   */
  static String checkVersion(@NotNull Version version) {
    String[] pointSections = version.value.split("\\.");
    String EXPECTED = "major.minor.point[-tweak]";
    if (pointSections.length == 1) {
      return String.format("expected %s but there were no dots", EXPECTED);
    }
    if (pointSections.length == 2) {
      return String.format("expected %s but there was only one dot", EXPECTED);
    }
    if (pointSections.length > 3) {
      return String.format("expected %s but there were %s dots", EXPECTED, pointSections.length - 1);
    }
    if (pointSections.length == 0) {
       return String.format("expected %s but there was only a dot", EXPECTED);
    }
    if (!StringUtils.isNumeric(pointSections[0])) {
      return String.format("expected %s but major version '%s' wasn't a number", EXPECTED, pointSections[0]);
    }
    if (!StringUtils.isNumeric(pointSections[1])) {
      return String.format("expected %s but minor version '%s' wasn't a number", EXPECTED, pointSections[1]);
    }
    if (pointSections[2].contains("-")) {
      int dashPosition = pointSections[2].indexOf('-');
      String pointVersion = pointSections[2].substring(0, dashPosition);
      if (!StringUtils.isNumeric(pointVersion)) {
        return String.format("expected %s but point version '%s' wasn't a number", EXPECTED, pointVersion);
      }
    } else {
      if (!StringUtils.isNumeric(pointSections[2])) {
        return String.format("expected %s but point version '%s' wasn't a number", EXPECTED, pointSections[2]);
      }
    }
    return null;
  }

  /**
   * Compare versions which are islands of strings and integers separated by . or - (or nothign).
   */
  private static class VersionComparator implements Comparator<Version> {
    final int order;
    VersionComparator(boolean forward) {
      order = forward ? 1 : -1;
    }

    /**
     * Break the given version into separate islands of String or Integer. Separators like . and -
     * are discorded.
     */
    @NotNull
    private static List<Object> segment(@NotNull Version version) {
      List<Object> segments = new ArrayList<>();
      String value = version.value;
      String segment = "";
      Boolean inString = null; // null means don't know
      for (int i = 0; i < value.length(); ++i) {
        char c = value.charAt(i);
        if (c == '.' || c == '-') {
          // Separator char
          //noinspection ConstantConditions
          if (inString) {
            segments.add(segment);
          } else {
            segments.add(Integer.parseInt(segment));
          }
          segment = "";
          inString = null;
          continue;
        }
        boolean isString = c < '0' || c > '9';
        if (inString == null) {
          segment = "" + c;
          inString = isString;
          continue;
        } else if (inString) {
          if (isString) {
            segment += c;
            continue;
          }
          // Segment contains a string but we're now on a number. Add the segment to the list
          segments.add(segment);
          inString = false;
          segment = "" + c;
          continue;
        }
        if (!isString) {
          segment += c;
          continue;
        }
        // Segment contains a number but we're now on a non-number. Add the segment to the list.
        segments.add(Integer.parseInt(segment));
        inString = true;
        segment = "" + c;
      }

      // If there's anything left over then add it to the list.
      if (segment.length() > 0) {
        //noinspection ConstantConditions
        if (inString) {
          segments.add(segment);
        } else {
          segments.add(Integer.parseInt(segment));
        }
      }

      return segments;
    }

    private int rawCompare(@NotNull Version o1, @NotNull Version o2) {
      List<Object> s1 = segment(o1);
      List<Object> s2 = segment(o2);

      int groups = Math.min(s1.size(), s2.size());
      for (int i = 0; i < groups; ++i) {
        Object v1 = s1.get(i);
        Object v2 = s2.get(i);
        if (v1 instanceof Integer) {
          if (v2 instanceof Integer) {
            int compare = Integer.compare((int) v1, (int) v2);
            if (compare != 0) {
              return compare;
            }
            continue;
          }
          // Compare String to int, int is lower
          return -1;
        }
        if (v1 instanceof String) {
          if (v2 instanceof String) {
            int compare = ((String) v1).compareTo((String) v2);
            if (compare != 0) {
              return compare;
            }
            continue;
          }
          // Compare String to int, int is lower
          return 1;
        }
      }

      // If we made it this far then there still could be some straggler segments if
      // one version is longer than the other. More version segments wins.
      return Integer.compare(s1.size(), s2.size());
    }




    @Override
    public int compare(@NotNull Version o1, @NotNull Version o2) {
      return order * rawCompare(o1, o2);

    }
  }
}
