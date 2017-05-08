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
import io.cdep.cdep.Coordinate;
import io.cdep.cdep.ResolvedManifests;
import io.cdep.cdep.Version;
import net.java.quickcheck.Generator;
import net.java.quickcheck.generator.PrimitiveGenerators;

import java.util.List;

import static net.java.quickcheck.generator.PrimitiveGenerators.enumValues;
import static net.java.quickcheck.generator.PrimitiveGenerators.strings;

public class CoordinateGenerator implements Generator<Coordinate> {
  private static List<ResolvedManifests.TestManifest> allManifests = ResolvedManifests.allTestManifest();

  @NotNull
  final public Generator<Kind> kind = enumValues(Kind.class);
  @NotNull
  final public Generator<String> strings = strings();
  @NotNull
  final public Generator<Integer> integers = PrimitiveGenerators.integers();
  @NotNull
  final public Generator<Version> versions = new VersionGenerator();

  @Override
  public Coordinate next() {
    switch (kind.next()) {
      case missing:
        return Coordinate.EMPTY_COORDINATE;
      case random:
        return new Coordinate(strings.next(), strings.next(), versions.next());
    }
    return allManifests.get((Math.abs(integers.next()) % allManifests.size())).manifest.cdepManifestYml.coordinate;
  }

  enum Kind {
    missing, random, specific,
  }
}
