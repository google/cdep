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
package io.cdep.cdep.resolver;

import static io.cdep.cdep.resolver.ResolutionScope.Unresolvable.DIDNT_EXIST;
import static io.cdep.cdep.resolver.ResolutionScope.Unresolvable.UNPARSEABLE;
import static io.cdep.cdep.utils.Invariant.require;

import io.cdep.annotations.NotNull;
import io.cdep.annotations.Nullable;
import io.cdep.cdep.Coordinate;
import io.cdep.cdep.Version;
import io.cdep.cdep.utils.CoordinateUtils;
import io.cdep.cdep.utils.VersionUtils;
import io.cdep.cdep.yml.cdep.SoftNameDependency;
import io.cdep.cdep.yml.cdepmanifest.HardNameDependency;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Records the current state of resolving top-level and transitive dependencies.
 */
@SuppressWarnings({"Convert2Diamond", "unused"})
public class ResolutionScope {

  // Map of dependency edges. Key is dependant and value is dependees.
  @NotNull
  final public Map<Coordinate, List<Coordinate>> forwardEdges = new HashMap<>();
  // Map of dependency edges. Key is dependee and value is dependants.
  @NotNull
  final public Map<Coordinate, List<Coordinate>> backwardEdges = new HashMap<>();
  // Map of dependency edges. Key is dependant and value is dependees.
  @NotNull
  private final Map<Coordinate, List<Coordinate>> unificationWinnersToLosers = new HashMap<>();
  // Map of dependency edges. Key is dependee and value is dependants.
  @NotNull
  private final Map<Coordinate, List<Coordinate>> unificationLosersToWinners = new HashMap<>();
  // Dependencies that are not yet resolved but where resolution is possible
  @NotNull
  final private Map<String, SoftNameDependency> unresolved = new HashMap<>();
  // Dependencies that have been resolved (successfully or unsuccessfully)
  @NotNull
  final private Set<String> resolved = new HashSet<>();
  @NotNull
  final private Map<String, Unresolvable> unresolveable = new HashMap<>();
  @NotNull
  final private Map<String, ResolvedManifest> versionlessKeyedManifests = new HashMap<>();

  /**
   * Construct a fresh resolution scope.
   *
   * @param roots are the top level dependencies from cdep.yml.
   */
  public ResolutionScope(@NotNull SoftNameDependency[] roots) {
    for (SoftNameDependency root : roots) {
      addUnresolved(root);
    }
  }

  /**
   * Construct a fresh resolution scope.
   */
  public ResolutionScope() {
  }

  /**
   * Utility function to add a new edge to an edge map.
   */
  private static <T> void addEdge(@NotNull Map<T, List<T>> edges, T from, T to) {
    List<T> tos = edges.get(from);
    if (tos == null) {
      edges.put(from, new ArrayList<T>());
      addEdge(edges, from, to);
      return;
    }
    tos.add(to);
  }

  /**
   * Add an unresolved dependency to be resolved later.
   *
   * @param softname the name of the unresolved dependency.
   */
  public void addUnresolved(@NotNull SoftNameDependency softname) {
    if (!resolved.contains(softname.compile)) {
      unresolved.put(softname.compile, softname);
    }
  }

  /**
   * Return true if there are no more references to resolve.
   */
  public boolean isResolutionComplete() {
    return unresolved.isEmpty();
  }

  /**
   * Return all remaining unresolved references.
   */
  @NotNull
  public Collection<SoftNameDependency> getUnresolvedReferences() {
    return new ArrayList<>(unresolved.values());
  }

  /**
   * Return all remaining unresolved references.
   */
  @NotNull
  public Collection<String> getUnresolvableReferences() {
    return new ArrayList<>(unresolveable.keySet());
  }

  /**
   * Return all remaining unresolved references.
   */
  @NotNull
  public Unresolvable getUnresolveableReason(@NotNull String softname) {
    return unresolveable.get(softname);
  }

  /**
   * Whether the given dependency is resolved or not regardless of whether the resolution was
   * successful.
   *
   * @param name the name of the dependency.
   * @return true if the dependency has already been resolved.
   */
  private boolean isResolved(String name) {
    return resolved.contains(name);
  }

  /**
   * Record the fact that the given dependency has been resolved.
   *
   * @param softname the name that started the resolution.
   * @param resolved the resolved manifest and hard name.
   * @param transitiveDependencies any new dependencies that were discovered during resolution
   */
  public void recordResolved(@NotNull SoftNameDependency softname,
      @NotNull ResolvedManifest resolved,
      @NotNull List<HardNameDependency> transitiveDependencies) {
    require(!isResolved(resolved.cdepManifestYml.coordinate.toString()),
        "%s was already resolved",
        resolved.cdepManifestYml.coordinate);

    determineUnificationWinner(resolved);
    this.resolved.add(resolved.cdepManifestYml.coordinate.toString());

    unresolved.remove(resolved.cdepManifestYml.coordinate.toString());
    unresolved.remove(softname.compile);

    for (HardNameDependency hardname : transitiveDependencies) {
      Coordinate coordinate = CoordinateUtils.tryParse(hardname.compile);
      if (coordinate == null) {
        this.resolved.add(hardname.compile);
        this.unresolveable.put(hardname.compile, UNPARSEABLE);
        continue;
      }
      addEdge(forwardEdges, resolved.cdepManifestYml.coordinate, coordinate);
      addEdge(backwardEdges, coordinate, resolved.cdepManifestYml.coordinate);
      addUnresolved(new SoftNameDependency(coordinate.toString()));
    }
  }

  /**
   * Unifies manifest version to most recent. Returns a versionless coordinate that is the
   * key to the manifest even if it changes later during resolution.
   */
  @SuppressWarnings("Java8ListSort")
  @Nullable
  private Coordinate determineUnificationWinner(@NotNull ResolvedManifest resolved) {
    Coordinate versionless = new Coordinate(
        resolved.cdepManifestYml.coordinate.groupId,
        resolved.cdepManifestYml.coordinate.artifactId);

    ResolvedManifest preexisting = versionlessKeyedManifests.get(versionless.toString());
    if (preexisting != null) {
      Map<Version, ResolvedManifest> manifests = new HashMap<>();
      manifests.put(resolved.cdepManifestYml.coordinate.version, resolved);
      manifests.put(preexisting.cdepManifestYml.coordinate.version, preexisting);
      List<Version> versions = new ArrayList<>();
      versions.addAll(manifests.keySet());
      assert versions.size() == 2;
      Collections.sort(versions, VersionUtils.DESCENDING_COMPARATOR);
      Version winningVersion = versions.get(0);

      versionlessKeyedManifests.put(versionless.toString(), manifests.get(winningVersion));
      Coordinate unificationWinner =
          new Coordinate(versionless.groupId, versionless.artifactId, versions.get(0));
      Coordinate unificationLoser =
          new Coordinate(versionless.groupId, versionless.artifactId, versions.get(1));
      addEdge(unificationWinnersToLosers, unificationWinner, unificationLoser);
      addEdge(unificationLosersToWinners, unificationLoser, unificationWinner);
      return versionless;
    }
    versionlessKeyedManifests.put(versionless.toString(), resolved);
    return versionless;
  }

  /**
   * Record fact that a given dependency could not be resolved.
   *
   * @param softname the name of the unresolvable dependency.
   */
  public void recordUnresolvable(@NotNull SoftNameDependency softname) {
    this.unresolved.remove(softname.compile);
    this.resolved.add(softname.compile);
    this.unresolveable.put(softname.compile, DIDNT_EXIST);
  }

  /**
   * Return the set of resolved names (coordinates or soft names).
   */
  @NotNull
  public ResolvedManifest getResolution(@NotNull String name) {
    return versionlessKeyedManifests.get(name);
  }

  /**
   * Return the set of resolved names (coordinates or soft names).
   */
  @NotNull
  public Collection<String> getResolutions() {
    return versionlessKeyedManifests.keySet();
  }

  /**
   * Return the set of unification winners.
   */
  @NotNull
  public Collection<Coordinate> getUnificationWinners() {
    return unificationWinnersToLosers.keySet();
  }

  /**
   * Return the set of unification losers.
   */
  @NotNull
  public Collection<Coordinate> getUnificationLosers() {
    return unificationLosersToWinners.keySet();
  }

  public enum Unresolvable {
    UNPARSEABLE,
    DIDNT_EXIST
  }
}
