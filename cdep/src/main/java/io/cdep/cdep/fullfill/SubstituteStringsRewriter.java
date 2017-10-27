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
package io.cdep.cdep.fullfill;

import io.cdep.annotations.NotNull;
import io.cdep.annotations.Nullable;
import io.cdep.cdep.yml.cdepmanifest.CDepManifestYmlRewriter;

import java.util.HashMap;
import java.util.Map;

public class SubstituteStringsRewriter extends CDepManifestYmlRewriter {
  final private Map<String, String> variables = new HashMap<>();

  @NotNull
  SubstituteStringsRewriter replace(String key, String value) {
    variables.put(key, value);
    return this;
  }

  @Nullable
  @Override
  protected String visitString(@Nullable String value) {
    if (value == null) {
      return null;
    }
    String result = value;
    for (String key : variables.keySet()) {
      result = result.replace(key, variables.get(key));
    }
    return result;
  }
}
