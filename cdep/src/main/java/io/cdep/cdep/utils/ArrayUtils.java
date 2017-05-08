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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ArrayUtils {
  /**
   * Return an empty array if the array is null.
   */
  @NotNull
  public static <T> T[] nullToEmpty(@Nullable T[] array, Class<T> clazz) {
    if (array == null) {
      //noinspection unchecked
      return (T[]) Array.newInstance(clazz, 0);
    }
    return array;
  }

  @NotNull
  public static <T> T[] removeNullElements(@NotNull T[] array, Class<T> clazz) {
    @SuppressWarnings("Convert2Diamond") List<T> list = new ArrayList<T>();
    for (T anArray : array) {
      if (anArray == null) {
        continue;
      }
      list.add(anArray);
    }
    //noinspection unchecked
    return list.toArray((T[])Array.newInstance(clazz, list.size()));
  }
}
