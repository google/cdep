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

import io.cdep.cdep.ResolvedManifests;
import io.cdep.cdep.generator.GeneratorEnvironment;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.fail;

public class TestEnvironmentUtils {
  private final GeneratorEnvironment environment = new GeneratorEnvironment(new File("" +
      "./test-files/TestEnvironmentUtils/working"), null, false, false);

  @Test
  public void testAllResolvedManifests() throws Exception {
    Map<String, String> expected = new HashMap<>();
    expected.put("sqliteiOS", "'sqliteiOS' does not have archive");
    expected.put("sqliteAndroid", "'sqliteAndroid' does not have archive");
    expected.put("sqliteLinuxMultiple", "'sqliteLinuxMultiple' does not have archive");
    expected.put("sqliteLinux", "'sqliteLinux' does not have archive");
    expected.put("sqlite", "'sqlite' does not have archive");
    expected.put("archiveMissingFile", "'archiveMissingFile' does not have archive.include.file");
    expected.put("singleABI", "'singleABI' does not have archive");
    expected.put("singleABISqlite", "'singleABISqlite' does not have archive");
    expected.put("archiveMissingSha256", "'archiveMissingSha256' does not have archive.include");
    expected.put("admob", "'admob' does not have archive.include");
    expected.put("templateWithNullArchives", "'templateWithNullArchives' does not have "
        + "archive.include");
    expected.put("templateWithOnlyFile", "'templateWithOnlyFile' does not have archive.include");
    expected.put("fuzz1", "'fuzz1' does not have archive");
    expected.put("fuzz2", "Illegal character in path at index 2: ,S`&|[x0q;J.$9D#P6FUDG>+&69ZXG");
    boolean unexpectedFailure = false;
    for (ResolvedManifests.NamedManifest manifest : ResolvedManifests.all()) {
      String key = manifest.name;
      String expectedFailure = expected.get(key);
      try {
        EnvironmentUtils.getPackageLevelIncludeFolder(environment, key, manifest.resolved);
        if (expectedFailure != null) {
          fail("Expected failure");
        }
      } catch (RuntimeException e) {
        if (e.getMessage() == null) {
          throw e;
        }
        if (!e.getMessage().equals(expectedFailure)) {
          e.printStackTrace();
          System.out.printf("expected.put(\"%s\", \"%s\");\n", key, e.getMessage());
          unexpectedFailure = true;
        }
      }
    }
    if (unexpectedFailure) {
      throw new RuntimeException("Unexpected failures. See console.");
    }
  }
}
