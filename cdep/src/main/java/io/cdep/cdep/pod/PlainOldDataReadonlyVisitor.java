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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;

import static io.cdep.cdep.utils.Invariant.require;
import static io.cdep.cdep.utils.ReflectionUtils.*;

/**
 * Read-only visitor over a plain object. Uses reflection to find public fields to walk over.
 */
@SuppressWarnings("unused")
abstract public class PlainOldDataReadonlyVisitor {

  protected void visitPlainOldDataObject(String name, @NotNull Object value) {
    visitFields(value);
  }

  abstract public void visitString(String name, String node);

  public void visitStringArray(String name, @NotNull String array[]) {
    visitArray(name, array, String.class);
  }

  public void visitLong(String name, Long value) {
  }

  protected void visitArray(String name, @NotNull Object[] array, @NotNull Class<?> elementType) {
    for (Object value : array) {
      visit(value, elementType);
    }
  }

  public void visit(Object element, @NotNull Class<?> elementClass) {
    String methodName = getVisitorName(elementClass);
    Method method = getMethod(getClass(), methodName, String.class, elementClass);
    invoke(method, this, null, element);
  }

  private void visitFields(@NotNull Object node) {
    if (node.getClass().isEnum()) {
      return;
    }
    for (Field field : node.getClass().getFields()) {
      if (Modifier.isStatic(field.getModifiers())) {
        continue;
      }
      require(!Objects.equals(field.getDeclaringClass(), Object.class));
      require(!Objects.equals(field.getDeclaringClass(), String.class));
      String methodName = getVisitorName(field.getType());
      Method method = getMethod(getClass(), methodName, String.class, field.getType());
      Object fieldValue = getFieldValue(field, node);
      if (fieldValue != null) {
        invoke(method, this, field.getName(), fieldValue);
      }
    }
  }

  private String getVisitorName(@NotNull Class<?> type) {
    String name = type.getName();
    name = name.substring(name.lastIndexOf(".") + 1);
    name = "visit" + name;
    if (type.isArray()) {
      name = name.substring(0, name.length() - 1);
      name += "Array";
    }
    return name;
  }
}
