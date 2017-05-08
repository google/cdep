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
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.cdep.annotations.NotNull;
import io.cdep.annotations.Nullable;
import io.cdep.cdep.Coordinate;
import io.cdep.cdep.utils.CDepManifestYmlUtils;
import io.cdep.cdep.utils.CoordinateUtils;
import io.cdep.cdep.yml.cdep.SoftNameDependency;
import io.cdep.cdep.yml.cdepmanifest.CDepManifestYml;
import java.net.URL;
import org.junit.Test;


public class TestResolver {

  @Nullable
  private static final Coordinate ADMOB_COORDINATE = CoordinateUtils.tryParse("com.github.jomof:firebase/admob:2.1.3-rev7");

  @NotNull
  private static final String ADMOB_URL = "https://github.com/jomof/firebase/releases/download/2.1.3-rev7/cdep-manifest-admob" +
      ".yml";

  @NotNull
  private static final CDepManifestYml ADMOB_MANIFEST = CDepManifestYmlUtils.convertStringToManifest("coordinate:\n  groupId: "
      + "com.github.jomof\n  artifactId: firebase/admob\n  version: 2.1.3-rev7\narchive:\n  file: " +
      "firebase-include.zip\n  sha256: 51827bab4c5b4f335058ab3c0a93f9fa39ba284d21bd686f27368829ee088815\n  " +
      "size: 93293\ndependencies:\n  - compile: com.github.jomof:firebase/app:2.1.3-rev7\n    sha256: " +
      ""  + "8292d143db85ec40ddf4d51133571607f4df3796e0477e8678993dcae4acfd03");

  @Nullable
  private static final Coordinate APP_COORDINATE = CoordinateUtils.tryParse("com.github.jomof:firebase/app:2.1.3-rev7");

  @NotNull
  private static final CDepManifestYml APP_MANIFEST = CDepManifestYmlUtils.convertStringToManifest("coordinate:\n  groupId: com"
      + ".github.jomof\n  artifactId: firebase/app\n  version: 2.1.3-rev7\narchive:\n  file: " +
      "firebase-include.zip\n  sha256: 51827bab4c5b4f335058ab3c0a93f9fa39ba284d21bd686f27368829ee088815\n  " +
      "size: 93293\n");


  @NotNull
  private static final CDepManifestYml ADMOB_MISSING_DEPENDENCY_MANIFEST = CDepManifestYmlUtils.convertStringToManifest
      ("coordinate:\n  groupId: com.github.jomof\n  artifactId: firebase/admob\n  version: 2.1.3-rev7\n" +
          "archive:\n  file: firebase-include.zip\n  sha256: " +
          "51827bab4c5b4f335058ab3c0a93f9fa39ba284d21bd686f27368829ee088815\n  size: 93293\ndependencies:\n  - "
          + "compile: com.github.jomof:firebase/app:2.1.3-rev8\n    sha256: " +
          "8292d143db85ec40ddf4d51133571607f4df3796e0477e8678993dcae4acfd03");

  @NotNull
  private static final CDepManifestYml ADMOB_BROKEN_DEPENDENCY_MANIFEST = CDepManifestYmlUtils.convertStringToManifest
      ("coordinate:\n  groupId: com.github.jomof\n  artifactId: firebase/admob\n  version: 2.1.3-rev7\n" +
          "archive:\n  file: firebase-include.zip\n  sha256: " +
          "51827bab4c5b4f335058ab3c0a93f9fa39ba284d21bd686f27368829ee088815\n  size: 93293\ndependencies:\n  - "
          + "compile: xxx\n    sha256: 8292d143db85ec40ddf4d51133571607f4df3796e0477e8678993dcae4acfd03");

  @Test
  public void twoResolversMatch() throws Exception {
    ManifestProvider provider = mock(ManifestProvider.class);
    when(provider.tryGetManifest(ADMOB_COORDINATE, new URL(ADMOB_URL))).thenReturn(ADMOB_MANIFEST);
    CoordinateResolver resolvers[] = new CoordinateResolver[]{new GithubReleasesCoordinateResolver(), new
        GithubReleasesCoordinateResolver()};
    Resolver resolver = new Resolver(provider, resolvers);
    try {
      assert ADMOB_COORDINATE != null;
      resolver.resolveAny(new SoftNameDependency(ADMOB_COORDINATE.toString()));
      fail("Expected exception");
    } catch (RuntimeException e) {
      assertThat(e).hasMessage("Multiple resolvers matched coordinate: com.github.jomof:firebase/admob:2.1.3-rev7");
    }
  }

  @Test
  public void testScopeUnresolvableDependencyResolve() throws Exception {
    ManifestProvider provider = mock(ManifestProvider.class);
    when(provider.tryGetManifest(ADMOB_COORDINATE, new URL(ADMOB_URL))).thenReturn(ADMOB_MISSING_DEPENDENCY_MANIFEST);
    Resolver resolver = new Resolver(provider);
    try {
      assert ADMOB_COORDINATE != null;
      resolver.resolveAll(new SoftNameDependency[]{new SoftNameDependency(ADMOB_COORDINATE.toString())});
      fail("Expected exception");
    } catch (RuntimeException e) {
      assertThat(e).hasMessage("Archive com.github.jomof:firebase/admob:2.1.3-rev7 is missing include");
    }
  }

  @Test
  public void testScopeMalformedDependencyResolve() throws Exception {
    ManifestProvider provider = mock(ManifestProvider.class);
    when(provider.tryGetManifest(ADMOB_COORDINATE, new URL(ADMOB_URL))).thenReturn(ADMOB_BROKEN_DEPENDENCY_MANIFEST);
    Resolver resolver = new Resolver(provider);
    try {
      assert ADMOB_COORDINATE != null;
      resolver.resolveAll(new SoftNameDependency[]{new SoftNameDependency(ADMOB_COORDINATE.toString())});
      fail("Expected exception");
    } catch (RuntimeException e) {
      assertThat(e).hasMessage("Archive com.github.jomof:firebase/admob:2.1.3-rev7 is missing include");
    }
  }

  @Test
  public void testEmptyScopeResolution() throws Exception {
    ManifestProvider provider = mock(ManifestProvider.class);
    when(provider.tryGetManifest(ADMOB_COORDINATE, new URL(ADMOB_URL))).thenReturn(ADMOB_MISSING_DEPENDENCY_MANIFEST);
    Resolver resolver = new Resolver(provider);
    ResolutionScope scope = resolver.resolveAll(new SoftNameDependency[]{});
    assertThat(scope.isResolutionComplete()).isTrue();
    assertThat(scope.getResolutions()).hasSize(0);
  }
}
