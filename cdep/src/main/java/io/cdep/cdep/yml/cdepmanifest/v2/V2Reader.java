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
package io.cdep.cdep.yml.cdepmanifest.v2;

import io.cdep.annotations.NotNull;
import io.cdep.annotations.Nullable;
import io.cdep.cdep.yml.cdepmanifest.CDepManifestYmlVersion;
import io.cdep.cdep.yml.cdepmanifest.Interfaces;
import io.cdep.cdep.yml.cdepmanifest.v1.V1Reader;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static io.cdep.cdep.utils.Invariant.require;

public class V2Reader {

  @NotNull
  public static io.cdep.cdep.yml.cdepmanifest.v3.CDepManifestYml convertStringToManifest(@NotNull String content) {
    Yaml yaml = new Yaml(new Constructor(CDepManifestYml.class));
    io.cdep.cdep.yml.cdepmanifest.v3.CDepManifestYml manifest;
    try {
      CDepManifestYml prior = (CDepManifestYml) yaml.load(
          new ByteArrayInputStream(content.getBytes(StandardCharsets
              .UTF_8)));
      prior.sourceVersion = CDepManifestYmlVersion.v2;
      manifest = convert(prior);
      assert manifest != null;
      require(manifest.sourceVersion == CDepManifestYmlVersion.v2);
    } catch (YAMLException e) {
      manifest = convert(V1Reader.convertStringToManifest(content));
      assert manifest != null;
      require(manifest.sourceVersion == CDepManifestYmlVersion.v1);
    }
    return manifest;
  }

  @Nullable
  private static io.cdep.cdep.yml.cdepmanifest.v3.CDepManifestYml convert(@NotNull CDepManifestYml manifest) {
    assert manifest.sourceVersion != null;
    assert manifest.coordinate != null;
    return new io.cdep.cdep.yml.cdepmanifest.v3.CDepManifestYml(
        manifest.sourceVersion,
        manifest.coordinate,
        manifest.dependencies,
        new Interfaces(manifest.archive),
        manifest.android,
        manifest.iOS,
        manifest.linux,
        manifest.example);
  }
}
