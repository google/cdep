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
package io.cdep.cdep.ast.finder;

import io.cdep.annotations.NotNull;
import io.cdep.annotations.Nullable;
import io.cdep.cdep.Coordinate;

import java.util.List;
import java.util.Map;

public class FunctionTableExpression extends Expression {
  @NotNull final public GlobalBuildEnvironmentExpression globals;
  @NotNull
  final public List<Coordinate> orderOfReferences;
  @NotNull
  final private Map<Coordinate, StatementExpression> findFunctions;
  @NotNull
  final private Map<Coordinate, ExampleExpression> examples;

//  public FunctionTableExpression(@NotNull List<Coordinate> orderOfReferences,
//                                 @NotNull Map<Coordinate, StatementExpression> findFunctions,
//                                 @NotNull Map<Coordinate, ExampleExpression> examples) {
//    this.orderOfReferences = orderOfReferences;
//    this.globals = new GlobalBuildEnvironmentExpression();
//    this.findFunctions = findFunctions;
//    this.examples = examples;
//  }

  public FunctionTableExpression(@NotNull GlobalBuildEnvironmentExpression globals,
                                 @NotNull List<Coordinate> orderOfReferences,
                                 @NotNull Map<Coordinate, StatementExpression> findFunctions,
                                 @NotNull Map<Coordinate, ExampleExpression> examples) {
    assert orderOfReferences.size() == findFunctions.size();
    assert orderOfReferences.size() >= examples.size();

    this.globals = globals;
    this.orderOfReferences = orderOfReferences;
    this.findFunctions = findFunctions;
    this.examples = examples;

  }
//
//  public void addReference(Coordinate coordinate, StatementExpression statement) {
//    this.findFunctions.put(coordinate, statement);
//  }
//
//  public void addExample(Coordinate coordinate, ExampleExpression example) {
//    this.examples.put(coordinate, example);
//  }

  @NotNull
  public StatementExpression getFindFunction(@NotNull Coordinate coordinate) {

    return this.findFunctions.get(coordinate);
  }

  @Nullable
  public ExampleExpression getExample(@NotNull Coordinate coordinate) {
    return this.examples.get(coordinate);
  }
}
