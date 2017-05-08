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

import io.cdep.annotations.NotNull;
import io.cdep.annotations.Nullable;
import io.cdep.cdep.Coordinate;
import io.cdep.cdep.Version;
import io.cdep.cdep.yml.cdep.SoftNameDependency;
import io.cdep.cdep.yml.cdepmanifest.CDepManifestYml;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.cdep.cdep.utils.Invariant.require;

public class GithubStyleUrlCoordinateResolver extends CoordinateResolver {
  final private Pattern pattern = Pattern.compile("^https://(.*)/(.*)/(.*)/releases/download/(.*)/cdep-manifest(" +
      ".*).yml$");

  @Nullable
  @Override
  public ResolvedManifest resolve(@NotNull ManifestProvider environment, @NotNull SoftNameDependency dependency)
      throws IOException, NoSuchAlgorithmException {
    String coordinate = dependency.compile;
    assert coordinate != null;
    Matcher match = pattern.matcher(coordinate);
    if (match.find()) {

      String baseUrl = match.group(1);
      String segments[] = baseUrl.split("\\.");
      String groupId = "";
      for (int i = 0; i < segments.length; ++i) {
        groupId += segments[segments.length - i - 1];
        groupId += ".";
      }
      String user = match.group(2);
      groupId += user;
      String artifactId = match.group(3);
      Version version = new Version(match.group(4));
      String subArtifact = match.group(5);
      if (subArtifact.length() > 0) {
        require(subArtifact.startsWith("-"), "Url is incorrectly formed at '%s': %s", subArtifact, coordinate);
        artifactId += "/" + subArtifact.substring(1);
      }

      Coordinate provisionalCoordinate = new Coordinate(groupId, artifactId, version);
      CDepManifestYml cdepManifestYml = environment.tryGetManifest(provisionalCoordinate, new URL(coordinate));
      if (cdepManifestYml == null) {
        // The URL didn't exist.
        return null;
      }

      // Ensure that the manifest coordinate agrees with the url provided
      require(groupId.equals(cdepManifestYml.coordinate.groupId),
          "groupId '%s' from manifest did not agree with github url %s",
          cdepManifestYml.coordinate.groupId,
          coordinate);
      require(artifactId.startsWith(cdepManifestYml.coordinate.artifactId),
          "artifactId '%s' from manifest did not agree with '%s' from github url %s",
          artifactId,
          cdepManifestYml.coordinate.artifactId,
          coordinate);
      require(version.equals(cdepManifestYml.coordinate.version),
          "version '%s' from manifest did not agree with version %s github url %s",
          cdepManifestYml.coordinate.version,
          version,
          coordinate);
      return new ResolvedManifest(new URL(coordinate), cdepManifestYml);
    }

    return null;
  }
}
