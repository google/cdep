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
import io.cdep.cdep.yml.cdepmanifest.LinuxArchive;
import net.java.quickcheck.Generator;

import static net.java.quickcheck.generator.CombinedGenerators.arrays;
import static net.java.quickcheck.generator.PrimitiveGenerators.*;

public class LinuxArchiveGenerator implements Generator<LinuxArchive> {
  @NotNull
  final public Generator<Kind> kind = enumValues(Kind.class);
  @NotNull
  final public Generator<String> file = new ShortFilenameGenerator();
  @NotNull
  final public Generator<String> sha256 = strings();
  @NotNull
  final public Generator<Long> size = longs();
  @NotNull
  final public Generator<String> include = strings();
  @NotNull
  final public Generator<String[]> libs = arrays(new ShortFilenameGenerator(), String.class);

  @Override
  public LinuxArchive next() {
    if (kind.next() == Kind.missing) {
      return null;
    }
    return new LinuxArchive(
        file.next(),
        sha256.next(),
        size.next(),
        libs.next(),
        include.next());
  }

  enum Kind {
    missing, random
  }
}
