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

import net.java.quickcheck.QuickCheck;
import net.java.quickcheck.characteristic.AbstractCharacteristic;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static net.java.quickcheck.generator.PrimitiveGenerators.strings;

public class TestStringUtils {
  @Test
  public void isNumeric() throws Exception {
    assertThat(StringUtils.isNumeric("x")).isFalse();
    assertThat(StringUtils.isNumeric("1")).isTrue();
  }

  @Test
  public void joinOn() throws Exception {
    assertThat(StringUtils.joinOn("+", "1", "2", "3")).isEqualTo("1+2+3");
  }

  @Test
  public void joinOn1() throws Exception {
    assertThat(StringUtils.joinOn("+", new Integer[]{1, 2, 3})).isEqualTo("1+2+3");
  }

  @Test
  public void joinOn2() throws Exception {
    List<String> list = new ArrayList<>();
    list.add("1");
    list.add("3");
    assertThat(StringUtils.joinOn("+", list)).isEqualTo("1+3");
  }

  @Test
  public void joinOnSkipNull() throws Exception {
    assertThat(StringUtils.joinOnSkipNullOrEmpty("+", "1", null, "3")).isEqualTo("1+3");
  }

  @Test
  public void coverConstructor() {
    // Call constructor of tested class to cover that code.
    new CoverConstructor();
  }

  @Test
  public void checkIsNumber() {
    assertThat(StringUtils.isNumeric("1")).isTrue();
  }

  @Test
  public void checkIsNotNumber() {
    assertThat(StringUtils.isNumeric("x")).isFalse();
  }

  @Test
  public void fuzzTest() {
    QuickCheck.forAll(strings(), new AbstractCharacteristic<String>() {
      @Override
      protected void doSpecify(String any) throws Throwable {
        StringUtils.containsAny(any, any);
        StringUtils.firstAvailable(any, 7);

      }
    });
  }

  private static class CoverConstructor extends StringUtils {

  }
}
