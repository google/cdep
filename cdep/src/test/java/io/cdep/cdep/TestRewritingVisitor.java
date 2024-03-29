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
package io.cdep.cdep;

import io.cdep.cdep.ast.finder.Expression;
import io.cdep.cdep.yml.cdepmanifest.CxxLanguageFeatures;
import org.junit.Test;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static com.google.common.truth.Truth.assertThat;
import static io.cdep.cdep.ast.finder.ExpressionBuilder.archive;
import static org.junit.Assert.fail;

public class TestRewritingVisitor {
  @Test
  public void testNullInclude() throws Exception {
    new RewritingVisitor().visit(archive(new URL("https://google.com"), "sha256", 192L, null,
        null, new String[0], new Expression[0], null, new CxxLanguageFeatures[0]));
  }

  @Test
  public void testAllResolvedManifests() throws Exception {
    Map<String, String> expected = new LinkedHashMap<>();
    expected.put("admob", "Reference com.github.jomof:firebase/app:2.1.3-rev8 was not found");
    expected.put("fuzz1", "Could not parse main manifest coordinate []");
    for (ResolvedManifests.NamedManifest manifest : ResolvedManifests.all()) {
      BuildFindModuleFunctionTable builder = new BuildFindModuleFunctionTable();
      if(Objects.equals(manifest.name, "curlAndroid"))
      {
        builder.addManifest(ResolvedManifests.zlibAndroid().manifest);
        builder.addManifest(ResolvedManifests.boringSSLAndroid().manifest);
      }
      builder.addManifest(manifest.resolved);
      String expectedFailure = expected.get(manifest.name);
      try {
        new RewritingVisitor().visit(builder.build());
        if (expectedFailure != null) {
          fail("Expected failure");
        }
      } catch (RuntimeException e) {
        if (expectedFailure == null) {
          throw e;
        }
        assertThat(e.getMessage()).contains(expectedFailure);
      }
    }
  }
}
