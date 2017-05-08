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

import io.cdep.cdep.Coordinate;
import io.cdep.cdep.yml.cdepmanifest.HardNameDependency;
import net.java.quickcheck.Generator;

import static net.java.quickcheck.generator.PrimitiveGenerators.strings;

public class HardnameGenerator implements Generator<HardNameDependency> {
  Generator<Coordinate> compileGenerator = new CoordinateGenerator();
  Generator<String> sha256Generator = strings();

  @Override
  public HardNameDependency next() {
    String compile = compileGenerator.next().toString();
    String sha256 = sha256Generator.next();
    return new HardNameDependency(compile, sha256);
  }
}
