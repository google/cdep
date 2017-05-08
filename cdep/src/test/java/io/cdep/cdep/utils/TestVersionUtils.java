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

import io.cdep.cdep.Version;
import io.cdep.cdep.yml.VersionGenerator;
import net.java.quickcheck.QuickCheck;
import net.java.quickcheck.characteristic.AbstractCharacteristic;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class TestVersionUtils {
  private static Version version(String value) {
    return new Version(value);
  }

  @Test
  public void coverConstructor() {
    // Call constructor of tested class to cover that code.
    new CoverConstructor();
  }

  @Test
  public void simple() {
    assertThat(VersionUtils.checkVersion(version("1.2.3"))).isNull();
  }

  @Test
  public void tweak() {
    assertThat(VersionUtils.checkVersion(version("1.2.3-rev9"))).isNull();
  }

  @Test
  public void majorNotNumber() {
    assertThat(VersionUtils.checkVersion(version("x.2.3")))
        .isEqualTo("expected major.minor.point[-tweak] but major version 'x' wasn't a number");
  }

  @Test
  public void minorNotNumber() {
    assertThat(VersionUtils.checkVersion(version("1.y.3")))
        .isEqualTo("expected major.minor.point[-tweak] but minor version 'y' wasn't a number");
  }

  @Test
  public void pointNotNumber() {
    assertThat(VersionUtils.checkVersion(version("1.2.z")))
        .isEqualTo("expected major.minor.point[-tweak] but point version 'z' wasn't a number");
  }

  @Test
  public void pointWithTweakNotNumber() {
    assertThat(VersionUtils.checkVersion(version("1.2.1z-rev8")))
        .isEqualTo("expected major.minor.point[-tweak] but point version '1z' wasn't a number");
  }

  @Test
  public void noDots() {
    assertThat(VersionUtils.checkVersion(version("1")))
        .isEqualTo("expected major.minor.point[-tweak] but there were no dots");
  }

  @Test
  public void oneDot() {
    assertThat(VersionUtils.checkVersion(version("1.2")))
        .isEqualTo("expected major.minor.point[-tweak] but there was only one dot");
  }

  @Test
  public void fourDots() {
    assertThat(VersionUtils.checkVersion(version("1.2.3.4")))
        .isEqualTo("expected major.minor.point[-tweak] but there were 3 dots");
  }

  @Test
  public void sortAscendingTweak() {
    List<Version> versions = sortA("1.2.3-rev2", "1.2.3-rev1");
    assertThat(versions.get(0).value).isEqualTo("1.2.3-rev1");
    assertThat(versions.get(1).value).isEqualTo("1.2.3-rev2");
  }

  @Test
  public void sortDescendingTweak() {
    List<Version> versions = sortD("1.2.3-rev2", "1.2.3-rev1");
    assertThat(versions.get(0).value).isEqualTo("1.2.3-rev2");
    assertThat(versions.get(1).value).isEqualTo("1.2.3-rev1");
  }

  @Test
  public void sortAscendingNoTweak() {
    List<Version> versions = sortA("1.2.2", "1.2.3");
    assertThat(versions.get(0).value).isEqualTo("1.2.2");
    assertThat(versions.get(1).value).isEqualTo("1.2.3");
  }

  @Test
  public void sortDescendingNoTweak() {
    List<Version> versions = sortD("1.2.2", "1.2.3");
    assertThat(versions.get(0).value).isEqualTo("1.2.3");
    assertThat(versions.get(1).value).isEqualTo("1.2.2");
  }

  @Test
  public void sortAscendingStringVsInt() {
    List<Version> versions = sortA("1.2.b", "1.2.2");
    assertThat(versions.get(0).value).isEqualTo("1.2.2");
    assertThat(versions.get(1).value).isEqualTo("1.2.b");
  }

  @Test
  public void sortDescendingStringVsInt() {
    List<Version> versions = sortD("1.2.2", "1.2.b");
    assertThat(versions.get(0).value).isEqualTo("1.2.b");
    assertThat(versions.get(1).value).isEqualTo("1.2.2");
  }

  @Test
  public void sortAscendingStraggler() {
    List<Version> versions = sortA("1.2.2-rev1", "1.2.2");
    assertThat(versions.get(0).value).isEqualTo("1.2.2");
    assertThat(versions.get(1).value).isEqualTo("1.2.2-rev1");
  }

  @Test
  public void sortDescendingStraggler() {
    List<Version> versions = sortD("1.2.2", "1.2.2-rev1");
    assertThat(versions.get(0).value).isEqualTo("1.2.2-rev1");
    assertThat(versions.get(1).value).isEqualTo("1.2.2");
  }

  @Test
  public void sortAscendingMiddleString() {
    List<Version> versions = sortA("1.a.2", "1.b.2");
    assertThat(versions.get(0).value).isEqualTo("1.a.2");
    assertThat(versions.get(1).value).isEqualTo("1.b.2");
  }

  @Test
  public void sortDescendingMiddleString() {
    List<Version> versions = sortD("1.a.2", "1.b.2");
    assertThat(versions.get(0).value).isEqualTo("1.b.2");
    assertThat(versions.get(1).value).isEqualTo("1.a.2");
  }

  @Test
  public void sortAscendingDateLike() {
    List<Version> versions = sortA("2011.1.1", "2010.1.1");
    assertThat(versions.get(0).value).isEqualTo("2010.1.1");
    assertThat(versions.get(1).value).isEqualTo("2011.1.1");
  }

  @Test
  public void sortDescendingDateLike() {
    List<Version> versions = sortD("2011.1.1", "2010.1.1");
    assertThat(versions.get(0).value).isEqualTo("2011.1.1");
    assertThat(versions.get(1).value).isEqualTo("2010.1.1");
  }

  @Test
  public void sortAscendingNonSeparator() {
    List<Version> versions = sortA("2011revA", "2011revB");
    assertThat(versions.get(0).value).isEqualTo("2011revA");
    assertThat(versions.get(1).value).isEqualTo("2011revB");
  }

  @Test
  public void sortDescendingNonSeparator() {
    List<Version> versions = sortD("2011revA", "2011revB");
    assertThat(versions.get(0).value).isEqualTo("2011revB");
    assertThat(versions.get(1).value).isEqualTo("2011revA");
  }

  private List<Version> sortA(String... versions) {
    List<Version> list = new ArrayList<>();
    for (String version : versions) {
      list.add(new Version(version));
    }

    Collections.sort(list, VersionUtils.ASCENDING_COMPARATOR);
    return list;
  }

  private List<Version> sortD(String... versions) {
    List<Version> list = new ArrayList<>();
    for (String version : versions) {
      list.add(new Version(version));
    }

    Collections.sort(list, VersionUtils.DESCENDING_COMPARATOR);
    return list;
  }

  @Test
  public void fuzzTest() {
    for (int i = 0; i < 10; ++i)
    QuickCheck.forAll(new VersionGenerator(), new AbstractCharacteristic<Version>() {
      @Override
      protected void doSpecify(Version any) throws Throwable {
        VersionUtils.checkVersion(any);
      }
    });
  }

  private static class CoverConstructor extends VersionUtils {

  }
}
