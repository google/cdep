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

import static com.google.common.truth.Truth.assertThat;

import io.cdep.cdep.Coordinate;
import io.cdep.cdep.resolver.ResolutionScope.Unresolvable;
import io.cdep.cdep.utils.CoordinateUtils;
import io.cdep.cdep.yml.cdep.SoftNameDependency;
import io.cdep.cdep.yml.cdepmanifest.CDepManifestYml;
import io.cdep.cdep.yml.cdepmanifest.HardNameDependency;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class TestResolutionScope {

  @Test
  public void testNoRoots() throws IOException {
    ResolutionScope scope = new ResolutionScope(new SoftNameDependency[0]);
    assertThat(scope.isResolutionComplete()).isTrue();
  }

  @Test
  public void testSimpleResolution() throws IOException {
    ResolutionScope scope = new ResolutionScope(new SoftNameDependency[]{new SoftNameDependency("com.github.jomof:firebase/admob:2.1.3-rev7")});
    assertThat(scope.isResolutionComplete()).isFalse();
    assertThat(scope.getUnresolvedReferences()).hasSize(1);
    SoftNameDependency unresolved = scope.getUnresolvedReferences().iterator().next();
    assertThat(unresolved.compile).isEqualTo("com.github.jomof:firebase/admob:2.1.3-rev7");
    Coordinate coordinate = CoordinateUtils.tryParse("com.github.jomof:firebase/admob:2.1.3-rev7");
    assert coordinate != null;
    CDepManifestYml manifest = new CDepManifestYml(coordinate);
    ResolvedManifest resolved = new ResolvedManifest(new URL("http://www.google.com"), manifest);
    List<HardNameDependency> transitiveDependencies = new ArrayList<>();
    scope.recordResolved(unresolved, resolved, transitiveDependencies);
    assertThat(scope.isResolutionComplete()).isTrue();
  }

  @Test
  public void testSimpleUnresolvable() throws IOException {
    ResolutionScope scope = new ResolutionScope(new SoftNameDependency[]{
        new SoftNameDependency("com.github.jomof:firebase/admob:2.1.3-rev7")});
    assertThat(scope.isResolutionComplete()).isFalse();
    assertThat(scope.getUnresolvedReferences()).hasSize(1);
    SoftNameDependency unresolved = scope.getUnresolvedReferences().iterator().next();
    assertThat(unresolved.compile).isEqualTo("com.github.jomof:firebase/admob:2.1.3-rev7");
    scope.recordUnresolvable(new SoftNameDependency("com.github.jomof:firebase/admob:2.1.3-rev7"));
    assertThat(scope.isResolutionComplete()).isTrue();
    assertThat(scope.getResolutions()).hasSize(0);
    assertThat(scope.getUnresolvableReferences()).hasSize(1);
    assertThat(scope.getUnresolveableReason(scope.getUnresolvableReferences().iterator().next())).
        isSameAs(Unresolvable.DIDNT_EXIST);
  }

  @Test
  public void testTransitiveResolution() throws IOException {
    ResolutionScope scope = new ResolutionScope(new SoftNameDependency[]{new SoftNameDependency("com.github.jomof:firebase/admob:2.1.3-rev7")});
    assertThat(scope.isResolutionComplete()).isFalse();
    assertThat(scope.getUnresolvedReferences()).hasSize(1);
    SoftNameDependency unresolved = scope.getUnresolvedReferences().iterator().next();
    assertThat(unresolved.compile).isEqualTo("com.github.jomof:firebase/admob:2.1.3-rev7");
    Coordinate coordinate = CoordinateUtils.tryParse("com.github.jomof:firebase/admob:2.1.3-rev7");
    assert coordinate != null;
    CDepManifestYml manifest = new CDepManifestYml(coordinate);
    ResolvedManifest resolved = new ResolvedManifest(new URL("http://www.google.com"), manifest);
    List<HardNameDependency> transitiveDependencies = new ArrayList<>();
    transitiveDependencies.add(new HardNameDependency("com.github.jomof:firebase/app:2.1.3-rev7", "shavalue"));
    scope.recordResolved(unresolved, resolved, transitiveDependencies);
    assertThat(scope.isResolutionComplete()).isFalse();
    assertThat(scope.getResolutions()).hasSize(1);
  }

  @Test
  public void testLocalPathResolution() throws IOException {
    ResolutionScope scope = new ResolutionScope(new SoftNameDependency[]{new SoftNameDependency("/tmp/cdep-manifest.yml")});
    assertThat(scope.isResolutionComplete()).isFalse();
    assertThat(scope.getUnresolvedReferences()).hasSize(1);
    SoftNameDependency unresolved = scope.getUnresolvedReferences().iterator().next();
    assertThat(unresolved.compile).isEqualTo("/tmp/cdep-manifest.yml");
    Coordinate coordinate = CoordinateUtils.tryParse("com.github.jomof:firebase/admob:2.1.3-rev7");
    assert coordinate != null;
    CDepManifestYml manifest = new CDepManifestYml(coordinate);
    ResolvedManifest resolved = new ResolvedManifest(new URL("http://www.google.com"), manifest);
    List<HardNameDependency> transitiveDependencies = new ArrayList<>();
    scope.recordResolved(unresolved, resolved, transitiveDependencies);
    assertThat(scope.isResolutionComplete()).isTrue();
    assertThat(scope.getResolutions()).hasSize(1);
  }

  @Test
  public void testTransitiveResolutionWithDependencyAlsoRoot() throws IOException {
    ResolutionScope scope = new ResolutionScope(new SoftNameDependency[]{new SoftNameDependency("com.github.jomof:firebase/admob:2.1.3-rev7"), new SoftNameDependency("com.github.jomof:firebase/app:2.1.3-rev7"),});
    assertThat(scope.isResolutionComplete()).isFalse();
    assertThat(scope.getUnresolvedReferences()).hasSize(2);
    SoftNameDependency unresolved = scope.getUnresolvedReferences().iterator().next();
    assertThat(unresolved.compile).isEqualTo("com.github.jomof:firebase/admob:2.1.3-rev7");
    Coordinate coordinate = CoordinateUtils.tryParse("com.github.jomof:firebase/admob:2.1.3-rev7");
    assert coordinate != null;
    CDepManifestYml manifest = new CDepManifestYml(coordinate);
    ResolvedManifest resolved = new ResolvedManifest(new URL("http://www.google.com"), manifest);
    List<HardNameDependency> transitiveDependencies = new ArrayList<>();
    transitiveDependencies.add(new HardNameDependency("com.github.jomof:firebase/app:2.1.3-rev7", "shavalue"));
    scope.recordResolved(unresolved, resolved, transitiveDependencies);
    assertThat(scope.isResolutionComplete()).isFalse();
    assertThat(scope.getResolutions()).hasSize(1);
  }

  @Test
  public void testTransitiveDependenciesLeadingToSameRoot() throws IOException {
    ResolutionScope scope = new ResolutionScope(new SoftNameDependency[]{
        new SoftNameDependency("com.github.jomof:firebase/admob:2.1.3-rev7"),
        new SoftNameDependency("com.github.jomof:firebase/database:2.1.3-rev7"),});
    assertThat(scope.isResolutionComplete()).isFalse();
    assertThat(scope.getUnresolvedReferences()).hasSize(2);

    List<HardNameDependency> transitiveDependencies = new ArrayList<>();
    transitiveDependencies.add(new HardNameDependency("com.github.jomof:firebase/app:2.1.3-rev7", "shavalue"));

    // Resolve the first level dependencies: admob and database respectively
    for (SoftNameDependency unresolved : scope.getUnresolvedReferences()) {
      assert unresolved.compile != null;
      Coordinate coordinate = CoordinateUtils.tryParse(unresolved.compile);
      assert coordinate != null;
      CDepManifestYml manifest = new CDepManifestYml(coordinate);
      ResolvedManifest resolved = new ResolvedManifest(new URL("http://www.google.com"), manifest);
      scope.recordResolved(unresolved, resolved, transitiveDependencies);
    }

    // Resolve the transitive dependency to app
    SoftNameDependency unresolved = scope.getUnresolvedReferences().iterator().next();
    assert unresolved.compile != null;
    Coordinate coordinate = CoordinateUtils.tryParse(unresolved.compile);
    assert coordinate != null;
    CDepManifestYml manifest = new CDepManifestYml(coordinate);
    ResolvedManifest resolved = new ResolvedManifest(new URL("http://www.google.com"), manifest);
    scope.recordResolved(unresolved, resolved, new ArrayList<HardNameDependency>());

    // Make sure resolution complete
    assertThat(scope.isResolutionComplete()).isTrue();
    assertThat(scope.getResolutions()).hasSize(3);

    // Check edges
    assertThat(scope.forwardEdges).hasSize(2);
    assertThat(scope.backwardEdges).hasSize(1);
    assertThat(scope.forwardEdges.get(CoordinateUtils.tryParse("com.github.jomof:firebase/admob:2.1.3-rev7"))).containsExactly(CoordinateUtils.tryParse("com" + ".github.jomof:firebase/app:2.1.3-rev7"));
    assertThat(scope.forwardEdges.get(CoordinateUtils.tryParse("com.github.jomof:firebase/database:2.1.3-rev7"))).containsExactly(CoordinateUtils.tryParse("com.github.jomof:firebase/app:2.1.3-rev7"));
  }

  @Test
  public void testTwoReferencesToDifferentVersion() throws IOException {
    ResolutionScope scope = new ResolutionScope(new SoftNameDependency[]{
        new SoftNameDependency("com.github.jomof:vectorial:1.0.0"),
        new SoftNameDependency("com.github.jomof:vectorial:1.0.1")
    });
    assertThat(scope.isResolutionComplete()).isFalse();
    assertThat(scope.getUnresolvedReferences()).hasSize(2);

    List<HardNameDependency> transitiveDependencies = new ArrayList<>();

    // Resolve the first level dependencies
    for (SoftNameDependency unresolved : scope.getUnresolvedReferences()) {
      assert unresolved.compile != null;
      Coordinate coordinate = CoordinateUtils.tryParse(unresolved.compile);
      assert coordinate != null;
      CDepManifestYml manifest = new CDepManifestYml(coordinate);
      ResolvedManifest resolved = new ResolvedManifest(new URL("http://www.google.com"), manifest);
      scope.recordResolved(unresolved, resolved, transitiveDependencies);
    }

    // At this point, the two versions should be unified up to version 1.0.1
    assertThat(scope.getUnresolvedReferences()).hasSize(0);
    assertThat(scope.getResolutions()).hasSize(1);

  }

  @Test
  public void testTransistiveReferencesToDifferentVersions1() throws IOException {
    ResolutionScope scope = new ResolutionScope(new SoftNameDependency[]{
        new SoftNameDependency("com.github.jomof:vectorial:1.0.1"),
        new SoftNameDependency("com.github.jomof:mathfu:1.0.0")
    });
    assertThat(scope.isResolutionComplete()).isFalse();
    assertThat(scope.getUnresolvedReferences()).hasSize(2);

    // Resolve the first level dependencies
    for (SoftNameDependency unresolved : scope.getUnresolvedReferences()) {
      Coordinate coordinate = CoordinateUtils.tryParse(unresolved.compile);
      switch(coordinate.artifactId) {
        case "mathfu": {
          CDepManifestYml manifest = new CDepManifestYml(coordinate);
          ResolvedManifest resolved = new ResolvedManifest(new URL("http://www.google.com"), manifest);
          List<HardNameDependency> transitiveDependencies = new ArrayList<>();
          transitiveDependencies.add(new HardNameDependency("com.github.jomof:vectorial:1.0.0", "sha"));
          scope.recordResolved(unresolved, resolved, transitiveDependencies);
          break; }
        case "vectorial": {
          CDepManifestYml manifest = new CDepManifestYml(coordinate);
          ResolvedManifest resolved = new ResolvedManifest(new URL("http://www.google.com"), manifest);
          List<HardNameDependency> transitiveDependencies = new ArrayList<>();
          scope.recordResolved(unresolved, resolved, transitiveDependencies);
          break; }
        default:
          throw new RuntimeException(coordinate.artifactId);
      }
    }

    // There should be one remaining dependency: com.github.jomof:vectorial:1.0.0
    assertThat(scope.getUnresolvedReferences()).hasSize(1);

    // Resolve the second level dependencies
    for (SoftNameDependency unresolved : scope.getUnresolvedReferences()) {
      Coordinate coordinate = CoordinateUtils.tryParse(unresolved.compile);
      switch(coordinate.artifactId) {
        case "vectorial": {
          CDepManifestYml manifest = new CDepManifestYml(coordinate);
          ResolvedManifest resolved = new ResolvedManifest(new URL("http://www.google.com"), manifest);
          List<HardNameDependency> transitiveDependencies = new ArrayList<>();
          scope.recordResolved(unresolved, resolved, transitiveDependencies);
          break; }
        default:
          throw new RuntimeException(coordinate.artifactId);
      }
    }

    // At this point, the two versions of vectorial should be unified up to version 1.0.1
    assertThat(scope.getUnresolvedReferences()).hasSize(0);
    assertThat(scope.getResolutions()).hasSize(2);

    // Check unification winners and losers
    assertThat(scope.getUnificationWinners())
        .containsExactly(CoordinateUtils.tryParse("com.github.jomof:vectorial:1.0.1"));
    assertThat(scope.getUnificationLosers())
        .containsExactly(CoordinateUtils.tryParse("com.github.jomof:vectorial:1.0.0"));
  }

  @Test
  public void testTransistiveReferencesToDifferentVersions2() throws IOException {
    ResolutionScope scope = new ResolutionScope(new SoftNameDependency[]{
        new SoftNameDependency("com.github.jomof:mathfu:1.0.0"),
        new SoftNameDependency("com.github.jomof:vectorial:1.0.1")
    });
    assertThat(scope.isResolutionComplete()).isFalse();
    assertThat(scope.getUnresolvedReferences()).hasSize(2);

    // Resolve the first level dependencies
    for (SoftNameDependency unresolved : scope.getUnresolvedReferences()) {
      Coordinate coordinate = CoordinateUtils.tryParse(unresolved.compile);
      switch(coordinate.artifactId) {
        case "mathfu": {
          CDepManifestYml manifest = new CDepManifestYml(coordinate);
          ResolvedManifest resolved = new ResolvedManifest(new URL("http://www.google.com"), manifest);
          List<HardNameDependency> transitiveDependencies = new ArrayList<>();
          transitiveDependencies.add(new HardNameDependency("com.github.jomof:vectorial:1.0.0", "sha"));
          scope.recordResolved(unresolved, resolved, transitiveDependencies);
          break; }
        case "vectorial": {
          CDepManifestYml manifest = new CDepManifestYml(coordinate);
          ResolvedManifest resolved = new ResolvedManifest(new URL("http://www.google.com"), manifest);
          List<HardNameDependency> transitiveDependencies = new ArrayList<>();
          scope.recordResolved(unresolved, resolved, transitiveDependencies);
          break; }
        default:
          throw new RuntimeException(coordinate.artifactId);
      }
    }

    // There should be one remaining dependency: com.github.jomof:vectorial:1.0.0
    assertThat(scope.getUnresolvedReferences()).hasSize(1);

    // Resolve the second level dependencies
    for (SoftNameDependency unresolved : scope.getUnresolvedReferences()) {
      Coordinate coordinate = CoordinateUtils.tryParse(unresolved.compile);
      switch(coordinate.artifactId) {
        case "vectorial": {
          CDepManifestYml manifest = new CDepManifestYml(coordinate);
          ResolvedManifest resolved = new ResolvedManifest(new URL("http://www.google.com"), manifest);
          List<HardNameDependency> transitiveDependencies = new ArrayList<>();
          scope.recordResolved(unresolved, resolved, transitiveDependencies);
          break; }
        default:
          throw new RuntimeException(coordinate.artifactId);
      }
    }

    // At this point, the two versions of vectorial should be unified up to version 1.0.1
    assertThat(scope.getUnresolvedReferences()).hasSize(0);
    assertThat(scope.getResolutions()).hasSize(2);

    // Check unification winners and losers
    assertThat(scope.getUnificationWinners())
        .containsExactly(CoordinateUtils.tryParse("com.github.jomof:vectorial:1.0.1"));
    assertThat(scope.getUnificationLosers())
        .containsExactly(CoordinateUtils.tryParse("com.github.jomof:vectorial:1.0.0"));
  }

  @Test
  public void testTwoTransitiveReferencesToSameDependency() throws IOException {
    ResolutionScope scope = new ResolutionScope(new SoftNameDependency[]{new SoftNameDependency("com.github.jomof:firebase/admob:2.1.3-rev7"), new SoftNameDependency("com.github.jomof:firebase/database:2.1.3-rev7"),});
    assertThat(scope.isResolutionComplete()).isFalse();
    assertThat(scope.getUnresolvedReferences()).hasSize(2);
    SoftNameDependency unresolved = scope.getUnresolvedReferences().iterator().next();

    Coordinate coordinate = CoordinateUtils.tryParse("com.github.jomof:firebase/admob:2.1.3-rev7");
    assert coordinate != null;
    CDepManifestYml manifest = new CDepManifestYml(coordinate);
    ResolvedManifest resolved = new ResolvedManifest(new URL("http://www.google.com"), manifest);
    List<HardNameDependency> transitiveDependencies = new ArrayList<>();
    transitiveDependencies.add(new HardNameDependency("com.github.jomof:firebase/app:2.1.3-rev7", "shavalue"));
    scope.recordResolved(unresolved, resolved, transitiveDependencies);

    coordinate = CoordinateUtils.tryParse("com.github.jomof:firebase/app:2.1.3-rev7");
    assert coordinate != null;
    manifest = new CDepManifestYml(coordinate);
    resolved = new ResolvedManifest(new URL("http://www.google.com"), manifest);
    transitiveDependencies = new ArrayList<>();
    scope.recordResolved(unresolved, resolved, transitiveDependencies);

    coordinate = CoordinateUtils.tryParse("com.github.jomof:firebase/database:2.1.3-rev7");
    assert coordinate != null;
    manifest = new CDepManifestYml(coordinate);
    resolved = new ResolvedManifest(new URL("http://www.google.com"), manifest);
    transitiveDependencies = new ArrayList<>();
    transitiveDependencies.add(new HardNameDependency("com.github.jomof:firebase/app:2.1.3-rev7", "shavalue"));
    scope.recordResolved(unresolved, resolved, transitiveDependencies);

    assertThat(scope.isResolutionComplete()).isTrue();
    assertThat(scope.getResolutions()).hasSize(3);
  }

  @Test
  public void testUnparsable() throws IOException {
    ResolutionScope scope = new ResolutionScope(new SoftNameDependency[]{
        new SoftNameDependency("com.github.jomof:firebase/admob:2.1.3-rev7")});
    assertThat(scope.isResolutionComplete()).isFalse();
    assertThat(scope.getUnresolvedReferences()).hasSize(1);
    SoftNameDependency unresolved = scope.getUnresolvedReferences().iterator().next();
    assertThat(unresolved.compile).isEqualTo("com.github.jomof:firebase/admob:2.1.3-rev7");
    Coordinate coordinate = CoordinateUtils.tryParse("com.github.jomof:firebase/admob:2.1.3-rev7");
    assert coordinate != null;
    CDepManifestYml manifest = new CDepManifestYml(coordinate);
    ResolvedManifest resolved = new ResolvedManifest(new URL("http://www.google.com"), manifest);
    List<HardNameDependency> transitiveDependencies = new ArrayList<>();
    transitiveDependencies.add(new HardNameDependency("com.github.jomof:firebase/app", "shavalue"));
    scope.recordResolved(unresolved, resolved, transitiveDependencies);
    assertThat(scope.isResolutionComplete()).isTrue();
    assertThat(scope.getResolutions()).hasSize(1);
    assertThat(scope.getUnresolvableReferences()).hasSize(1);
    assertThat(scope.getUnresolveableReason(scope.getUnresolvableReferences().iterator().next())).
        isSameAs(Unresolvable.UNPARSEABLE);
  }
}
