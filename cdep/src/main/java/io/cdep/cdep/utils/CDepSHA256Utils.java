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
import io.cdep.cdep.yml.cdepsha25.CDepSHA256;
import io.cdep.cdep.yml.cdepsha25.HashEntry;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class CDepSHA256Utils {

  @NotNull
  public static CDepSHA256 convertStringToCDepSHA256(@NotNull String content) {
    Yaml yaml = new Yaml(new Constructor(HashEntry[].class));
    HashEntry[] result = (HashEntry[]) yaml.load(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
    if (result == null) {
      result = new HashEntry[0];
    }
    return new CDepSHA256(result);
  }
}
