package io.cdep.cdep.resolver;

import io.cdep.annotations.NotNull;
import io.cdep.annotations.Nullable;
import io.cdep.cdep.yml.cdep.SoftNameDependency;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * This resolver locates a package by using a coordinate that is transformed into a github URL. It allows a single
 * Github repo to host multple packages. For example,
 *
 *   - compile: com.github.google.cdep:sqlite:1.2.3
 *
 * Is decomposed into:
 *
 *   host = github.com
 *   domain = google
 *   project = cdep
 *   artifact = sqlite
 *   version = 1.2.3
 *
 * then these fields are recomposed into:
 *
 *   http://[host]/[domain]/releases/download/[artifact]-[version]/cdep-manifest.yml
 *
 *      -- to --
 *
 *   http://github.com/google/releases/download/sqlite-1.2.3/cdep-manifest.yml
 */
public class GithubMultipackageCoordinateResolver extends CoordinateResolver {

  final private Pattern pattern = compile("^com\\.github\\.(.*)\\.(.*):(.*):(.*)$");
  final private CoordinateResolver urlResolver;

  GithubMultipackageCoordinateResolver() {
    this.urlResolver = new GithubStyleMultipackageUrlCoordinateResolver();
  }

  @Nullable
  @Override
  public ResolvedManifest resolve(@NotNull ManifestProvider environment, @NotNull SoftNameDependency dependency)
      throws IOException, NoSuchAlgorithmException {
    String coordinate = dependency.compile;
    assert coordinate != null;
    Matcher match = pattern.matcher(coordinate);
    if (match.find()) {
      String domain = match.group(1);
      String project = match.group(2);
      String artifact = match.group(3);
      String version = match.group(4);
      String subArtifact = "";
      if (artifact.contains("/")) {
        int pos = artifact.indexOf("/");
        subArtifact = "-" + artifact.substring(pos + 1);
        artifact = artifact.substring(0, pos);
      }
      String manifest = String.format("https://github.com/%s/%s/releases/download/%s@%s/cdep-manifest%s.yml",
          domain,
          project,
          artifact,
          version,
          subArtifact);
      return urlResolver.resolve(environment, new SoftNameDependency(manifest));
    }
    return null;
  }
}
