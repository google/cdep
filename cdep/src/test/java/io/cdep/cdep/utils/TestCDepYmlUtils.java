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

import io.cdep.cdep.yml.cdep.CDepYml;
import org.junit.Test;

import java.io.File;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

public class TestCDepYmlUtils {

  @Test
  public void testDuplicateGenerator() {
    CDepYml cdep = CDepYmlUtils.fromString("builders: [cmake, cmake]");

    try {
      CDepYmlUtils.checkSanity(cdep, new File("cdep.yml"));
      fail("Expected exception");
    } catch (RuntimeException e) {
      assertThat(e).hasMessage("cdep.yml 'builders' contains 'cmake' more than once");
    }
  }

  @Test
  public void testNoBuilders() {
    CDepYml cdep = CDepYmlUtils.fromString("builders: []");

    try {
      CDepYmlUtils.checkSanity(cdep, new File("cdep.yml"));
      fail("Expected exception");
    } catch (RuntimeException e) {
      assertThat(e).hasMessage("cdep.yml 'builders' section is missing or empty. Valid values are: cmake cmakeExamples ndk-build.");
    }
  }

  @Test
  public void testWorksFine() {
    CDepYml cdep = CDepYmlUtils.fromString("builders: [cmake]");
    CDepYmlUtils.checkSanity(cdep, new File("cdep.yml"));
  }

  @Test
  public void coverConstructor() {
    new CDepYmlUtils() {
    };
  }
}
