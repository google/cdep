package io.cdep.cdep;
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

import static io.cdep.cdep.utils.Invariant.require;

import io.cdep.annotations.NotNull;
import io.cdep.annotations.Nullable;
import io.cdep.cdep.ast.finder.FindModuleExpression;
import io.cdep.cdep.ast.finder.FunctionTableExpression;
import io.cdep.cdep.ast.finder.ModuleArchiveExpression;
import io.cdep.cdep.ast.finder.ModuleExpression;
import io.cdep.cdep.utils.StringUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This checker looks at the SHA256 of files along each dependency chain and ensures that each file
 * is only present at a single level.
 * <p>
 * Also check whether references were unresolved.
 */
@SuppressWarnings("Convert2Diamond")
public class CheckReferenceAndDependencyConsistency extends ReadonlyVisitor {

  // Map of dependency edges. Key is dependant and constant is dependees.
  private final Map<Coordinate, List<Coordinate>> forwardEdges = new HashMap<>();
  // Map of dependency edges. Key is dependee and constant is dependants.
  private final Map<Coordinate, List<Coordinate>> backwardEdges = new HashMap<>();
  // Map from module coordinate to the archives that it references
  private final Map<Coordinate, List<ModuleArchiveExpression>> moduleArchives = new HashMap<>();
  @Nullable
  private Coordinate currentFindModule = null;

  /**
   * Utility function to add a new edge to an edge map.
   */
  private static void addEdge(
      @NotNull Map<Coordinate, List<Coordinate>> edges,
      @NotNull Coordinate from,
      @NotNull Coordinate to) {
    List<Coordinate> tos = edges.get(from);
    if (tos == null) {
      edges.put(from, new ArrayList<Coordinate>());
      addEdge(edges, from, to);
      return;
    }
    tos.add(to);
  }

  /**
   * Utility function to add a new edge to an edge map.
   */
  private void addModuleArchive(ModuleArchiveExpression archive) {
    require(currentFindModule != null);
    List<ModuleArchiveExpression> tos = moduleArchives.get(currentFindModule);
    if (tos == null) {
      moduleArchives.put(currentFindModule, new ArrayList<ModuleArchiveExpression>());
      addModuleArchive(archive);
      return;
    }
    tos.add(archive);
  }

  @Override
  protected void visitFunctionTableExpression(@NotNull FunctionTableExpression expr) {
    super.visitFunctionTableExpression(expr);
    for (Coordinate coordinate : forwardEdges.keySet()) {
      Map<String, Coordinate> shaToPrior = copyArchivesInto(coordinate);
      validateForward(coordinate, shaToPrior);
    }
  }

  private void validateForward(Coordinate dependant, @NotNull Map<String, Coordinate> shaToPrior) {
    for (Coordinate dependee : forwardEdges.get(dependant)) {
      List<ModuleArchiveExpression> dependeeArchives = moduleArchives.get(dependee);
      require(dependeeArchives != null, "Reference %s was not found, needed by %s", dependee, dependant);
      if (dependeeArchives == null) {
        continue;
      }
      // Have any of the dependee archives been seen before?
      for (ModuleArchiveExpression dependeeArchive : dependeeArchives) {
        Coordinate prior = shaToPrior.get(dependeeArchive.sha256);
        String digest = StringUtils.firstAvailable(dependeeArchive.sha256, 7);
        require(prior == null,
            "Package '%s' depends on '%s' but both packages contain a file '%s' "
                + "with the same SHA256. The file should only be in the lowest level package '%s' "
                + "(sha256:%s)",
            dependant,
            dependee,
            dependeeArchive.file,
            dependee,
            digest);
      }
    }
  }

  /**
   * Produce a map from SHA256 of each archive to the coordinate that references that archive
   * as a dependency.
   */
  @NotNull
  private Map<String, Coordinate> copyArchivesInto(Coordinate coordinate) {
    Map<String, Coordinate> copy = new HashMap<>();
    for (ModuleArchiveExpression archive : moduleArchives.get(coordinate)) {
      copy.put(archive.sha256, coordinate);
    }
    return copy;
  }

  @Override
  protected void visitFindModuleExpression(@NotNull FindModuleExpression expr) {
    this.currentFindModule = expr.coordinate;
    super.visitFindModuleExpression(expr);
    this.currentFindModule = null;
  }

  @Override
  protected void visitModuleExpression(@NotNull ModuleExpression expr) {
    require(currentFindModule != null);
    for (Coordinate coordinate : expr.dependencies) {
      addEdge(forwardEdges, currentFindModule, coordinate);
      addEdge(backwardEdges, coordinate, currentFindModule);
    }
    super.visitModuleExpression(expr);
  }

  @Override
  protected void visitModuleArchiveExpression(@NotNull ModuleArchiveExpression expr) {
    addModuleArchive(expr);
    super.visitModuleArchiveExpression(expr);
  }
}
