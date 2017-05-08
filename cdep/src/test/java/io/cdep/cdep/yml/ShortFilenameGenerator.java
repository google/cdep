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
package io.cdep.cdep.yml;

import io.cdep.annotations.NotNull;
import net.java.quickcheck.Generator;

import static net.java.quickcheck.generator.PrimitiveGenerators.enumValues;
import static net.java.quickcheck.generator.PrimitiveGenerators.strings;

public class ShortFilenameGenerator implements Generator<String> {
  @NotNull
  final public Generator<String> string = strings();
  @NotNull
  final public Generator<Kind> kind = enumValues(Kind.class);

  @Override
  public String next() {
    switch (kind.next()) {
      case zip:
        return "archive.zip";
      case dota:
        return "lib.a";
      case dotso:
        return "lib.so";
      case doth:
        return "header.h";
    }

    return string.next();
  }

  enum Kind {
    random, zip, dota, dotso, doth
  }
}
