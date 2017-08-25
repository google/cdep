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
package io.cdep.cdep.utils;

import io.cdep.annotations.NotNull;
import io.cdep.cdep.yml.cdep.BuildSystem;
import io.cdep.cdep.yml.cdep.CDepYml;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import static io.cdep.cdep.utils.Invariant.fail;
import static io.cdep.cdep.utils.Invariant.require;

abstract public class CDepYmlUtils {
  public static void checkSanity(@NotNull CDepYml cdepYml, File configFile) {
    Set<BuildSystem> builders = new HashSet<>();
    for (BuildSystem builder : cdepYml.builders) {
      require(!builders.contains(builder), "'builders' contains '%s' more than once", builder);
      builders.add(builder);
    }

    if (cdepYml.builders.length == 0) {
      String allowed = StringUtils.joinOn(" ", BuildSystem.values());
      fail("'builders' section is " + "missing or empty. Valid values are: %s.", allowed);
    }
  }

  @NotNull
  public static CDepYml fromString(@NotNull String url, @NotNull String content) {
    if (Invariant.errorsInScope() > 0) {
      return new CDepYml();
    }
    Invariant.registerYamlFile(url);
    Yaml yaml = new Yaml(new Constructor(CDepYml.class));
    byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
    CDepYml cdepYml = (CDepYml) yaml.load(new ByteArrayInputStream(bytes));
    require(cdepYml != null, "cdep.yml was empty");
    if (Invariant.errorsInScope() > 0) {
      return new CDepYml();
    }
    assert cdepYml != null;
    Node node = yaml.compose(new InputStreamReader(new ByteArrayInputStream(bytes)));
    SnakeYmlUtils.mapAndRegisterNodes(url, cdepYml, node);
    return cdepYml;
  }
}
