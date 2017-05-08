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
package io.cdep.cdep.pod;

import io.cdep.annotations.NotNull;
import io.cdep.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static io.cdep.cdep.utils.Invariant.require;
import static io.cdep.cdep.utils.ReflectionUtils.*;

/**
 * Visit two instances at the same time. Allows comparison and merging.
 */
@SuppressWarnings("unused")
public class PlainOldDataReadonlyCovisitor {

  @NotNull
  final List<String> namestack = new ArrayList<>();

  private void push(@Nullable String name) {
    if (name == null) {
      push("[constant]");
      return;
    }
    namestack.add(0, name);
  }

  private void pop() {
    namestack.remove(0);
  }

  public void covisitString(String name, String left, String right) {
  }

  @SuppressWarnings("EmptyMethod")
  public void covisitInteger(String name, Integer left, Integer right) {
  }

  public void covisitStringArray(String name, String left[], String right[]) {
    covisitArray(name, left, right, String.class);
  }

  public void covisitLongArray(String name, Long left[], Long right[]) {
    covisitArray(name, left, right, Long.class);
  }

  public void covisitLong(String name, Long left, Long right) {

  }

  public void covisitObject(String name, Object left, Object right) {
    covisitFields(left, right);
  }

  protected <T> void covisitArray(String name, @Nullable T[] left, @Nullable T[] right, @NotNull Class<T> elementType) {
    if (left == null && right == null) {
      return;
    }
    int max = 0;
    if (left != null) {
      max = Math.max(max, left.length);
    }
    if (right != null) {
      max = Math.max(max, right.length);
    }
    for (int i = 0; i < max; ++i) {
      Object leftElement = null;
      Object rightElement = null;
      if (left != null && left.length > i) {
        leftElement = left[i];
      }
      if (right != null && right.length > i) {
        rightElement = right[i];
      }
      covisit(name, leftElement, rightElement, elementType);
    }
  }

  public void covisit(@Nullable Object left, @Nullable Object right) {
    if (left == null && right == null) {
      return;
    }
    if (left == null) {
      covisit(null, null, right, right.getClass());
      return;
    }
    if (right == null) {
      covisit(null, left, null, left.getClass());
      return;
    }
    require(left.getClass().equals(right.getClass()));
    covisit(null, left, right, left.getClass());
  }

  protected void covisit(String name, @Nullable Object left, @Nullable Object right, @NotNull Class<?> type) {
    if (left == null && right == null) {
      return;
    }
    String methodName = getVisitorName(type);
    push(name);
    try {
      Method method = getMethod(getClass(), methodName, String.class, type, type);
      invoke(method, this, name, left, right);
    } finally {
      pop();
    }
  }

  protected void covisitFields(@Nullable Object left, @Nullable Object right) {
    require(left != null || right != null);
    Object representative = right;
    if (representative == null) {
      representative = left;
    }
    assert representative != null;
    require(!representative.getClass().isEnum(), "Don't visit enum field");
    for (Field field : representative.getClass().getFields()) {
      if (Modifier.isStatic(field.getModifiers())) {
        continue;
      }
      Object leftValue = left == null ? null : getFieldValue(field, left);
      Object rightValue = right == null ? null : getFieldValue(field, right);
      covisit(field.getName(), leftValue, rightValue, field.getType());
    }
  }

  private String getVisitorName(@NotNull Class<?> type) {
    String name;
    if (type.isArray()) {
      name = type.getComponentType().getName();
    } else {
      name = type.getName();
    }
    int dotPos = name.lastIndexOf(".");
    if (dotPos != -1) {
      // Trim the namespace
      name = name.substring(dotPos + 1);
    }
    name = "covisit" + name;
    if (type.isArray()) {
      name += "Array";
    }
    return name;
  }
}
