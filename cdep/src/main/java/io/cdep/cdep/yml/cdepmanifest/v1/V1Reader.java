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
package io.cdep.cdep.yml.cdepmanifest.v1;

import io.cdep.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static io.cdep.cdep.utils.Invariant.require;

public class V1Reader {

  @NotNull
  public static io.cdep.cdep.yml.cdepmanifest.v2.CDepManifestYml convertStringToManifest(@NotNull String content) {
    Yaml yaml = new Yaml(new Constructor(CDepManifestYml.class));
    CDepManifestYml manifest = (CDepManifestYml) yaml.load(
        new ByteArrayInputStream(content.getBytes(StandardCharsets
            .UTF_8)));
    require(manifest != null, "Manifest was empty");
    assert manifest != null;
    return convert(manifest);
  }

  @NotNull
  private static io.cdep.cdep.yml.cdepmanifest.v2.CDepManifestYml convert(@NotNull CDepManifestYml manifest) {
    assert manifest.coordinate != null;
    assert manifest.android != null;
    return new io.cdep.cdep.yml.cdepmanifest.v2.CDepManifestYml(
        manifest.coordinate,
        manifest.dependencies,
        manifest.archive,
        convert(manifest.android),
        manifest.iOS,
        manifest.linux,
        manifest.example);
  }

  @NotNull
  private static io.cdep.cdep.yml.cdepmanifest.v3.Android convert(@NotNull Android android) {
    assert android.archives != null;
    return new io.cdep.cdep.yml.cdepmanifest.v3.Android(android.dependencies, convert(android.archives));
  }

  private static io.cdep.cdep.yml.cdepmanifest.v3.AndroidArchive[] convert(@NotNull AndroidArchive[] archives) {
    List<io.cdep.cdep.yml.cdepmanifest.v3.AndroidArchive> singleAbiArchives = new ArrayList<>();
    for (AndroidArchive archive : archives) {
      if (archive.abis == null) {
        continue;
      }
      for (String abi : archive.abis) {
        singleAbiArchives.add(new io.cdep.cdep.yml.cdepmanifest.v3.AndroidArchive(
            archive.file,
            archive.sha256,
            archive.size,
            archive.ndk,
            archive.compiler,
            archive.runtime,
            archive.platform,
            archive.builder,
            abi,
            archive.include,
            archive.lib,
            archive.flavor));

      }
    }

    return singleAbiArchives.toArray(new io.cdep.cdep.yml.cdepmanifest.v3.AndroidArchive[singleAbiArchives.size()]);
  }
}
