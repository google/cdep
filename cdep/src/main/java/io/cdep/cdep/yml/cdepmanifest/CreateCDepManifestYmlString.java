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
package io.cdep.cdep.yml.cdepmanifest;

import static io.cdep.cdep.utils.StringUtils.whitespace;

import io.cdep.annotations.NotNull;
import io.cdep.annotations.Nullable;
import io.cdep.cdep.Version;
import io.cdep.cdep.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;

public class CreateCDepManifestYmlString extends CDepManifestYmlReadonlyVisitor {

  @NotNull
  private final List<StringBuilder> sb = new ArrayList<>();
  private int indent = 0;

  public static String serialize(@NotNull Object node, int indent) {
    CreateCDepManifestYmlString thiz = new CreateCDepManifestYmlString();
    thiz.indent = indent;
    thiz.push();
    thiz.visitPlainOldDataObject(null, node);
    return thiz.pop();
  }

  public static String serialize(@NotNull Object node) {
    return serialize(node, 0);
  }

  private static boolean containsQuotableCharacters(String value) {
    return
        StringUtils.containsAny(value, ",\n{}[]\r\u007f=:? ")
      || StringUtils.startsWithAny(value, "-|*>!&#@<'%\"`\\");
  }

  private void push() {
    sb.add(0, new StringBuilder());
  }

  private String pop() {
    String result = sb.get(0).toString();
    sb.remove(0);
    return result;
  }

  @Override
  protected void visitPlainOldDataObject(@Nullable String name, @NotNull Object value) {
    if (name == null) {
      super.visitPlainOldDataObject(null, value);
      return;
    }
    appendIndented("%s:\r\n", name);
    ++indent;
    super.visitPlainOldDataObject(name, value);
    --indent;
  }

  @Override
  public void visitiOSPlatform(String name, @NotNull iOSPlatform value) {
    appendIndented("%s: %s\r\n", name, value);
  }

  @Override
  public void visitiOSArchitecture(String name, @NotNull iOSArchitecture value) {
    appendIndented("%s: %s\r\n", name, value);
  }

  @Override
  public void visitAndroidABI(@Nullable String name, @NotNull AndroidABI value) {
    appendIndented("%s: %s\r\n", name, quoteIfNecessary(value.name));
  }

  @Override
  public void visitCxxLanguageFeatures(@Nullable String name, @NotNull CxxLanguageFeatures value) {
    if (name == null) {
      append(value.toString() + "\r\n");
      return;
    }
    appendIndented("%s: %s\r\n", name, value);
  }

  @Override
  public void visitVersion(String name, @NotNull Version value) {
    appendIndented("%s: %s\r\n", name, quoteIfNecessary(value.value));
  }

  private String quoteIfNecessary(String value) {
    if (value.isEmpty()) {
      return "\"\"";
    }
    if (containsQuotableCharacters(value)) {
      return "\"" + yamlEscape(value) + "\"";
    }
    return value;
  }

  @Override
  public void visitString(String name, @NotNull String value) {
    if (value.isEmpty()) {
      return;
    }
    if (!containsQuotableCharacters(value)) {
      appendIndented("%s: %s\r\n", name, value);
      return;
    }

    if (!value.contains("\n")) {
      // If no line breaks then just quote it.
      appendIndented("%s: %s\r\n", name, quoteIfNecessary(value));
      return;
    }

    String lines[] = value.split("\\n"); // May contain \r as well, but that's okay
    appendIndented("%s: |\n", name);
    ++indent;
    for (int i = 0; i < lines.length; ++i) {
      appendIndented("%s", lines[i]);
      if (i != lines.length - 1) {
        append("\n");
      } else if (value.endsWith("\n")) {
        // Only want the trailing \n if the source had a trailing \n
        append("\n");
      }
    }
    --indent;
  }

  private String yamlEscape(String value) {
    return value
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\\n")
        .replace("\r", "\\\r")
        .replace('\u007F', '?');
  }

  @Override
  public void visitLong(String name, Long node) {
    appendIndented("%s: %s\r\n", name, node);
  }

  @Override
  public void visitStringArray(String name, @NotNull String[] array) {
    String strings[] = new String[array.length];
    for (int i = 0; i < array.length; ++i) {
      strings[i] = quoteIfNecessary(array[i]);
    }
    appendIndented("%s: [%s]\r\n", name, StringUtils.joinOn(", ", strings));
  }

  @Override
  public void visitArray(String name, @NotNull Object[] array, @NotNull Class<?> elementType) {
    if (array.length == 0) {
      return;
    }
    if (elementType.isEnum()) {
      appendIndented("%s: [%s]\r\n", name, StringUtils.joinOn(", ", array));
      return;
    }
    appendIndented("%s:\r\n", name);
    ++indent;
    for (Object obj : array) {
      if (obj == null) {
        continue;
      }
      push();
      visit(obj, elementType);
      String sub = pop();
      if (sub.length()==0) {
        continue;
      }
      sb.get(0).append(whitespace((indent - 1) * 2));
      sb.get(0).append("- ");
      sb.get(0).append(sub.substring(indent * 2));
    }
    --indent;
  }

  private void append(@NotNull String format, Object... parms) {
    sb.get(0).append(String.format(format, parms));
  }

  private void appendIndented(String format, Object... parms) {
    String prefix = whitespace(indent * 2);
    //noinspection StringConcatenationInFormatCall
    sb.get(0).append(String.format(prefix + format, parms));
  }

}
