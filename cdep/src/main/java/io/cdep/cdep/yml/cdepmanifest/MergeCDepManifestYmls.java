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
package io.cdep.cdep.yml.cdepmanifest;

import io.cdep.annotations.NotNull;
import io.cdep.annotations.Nullable;
import io.cdep.cdep.Coordinate;

import static io.cdep.cdep.yml.cdepmanifest.CDepManifestBuilder.*;
import static io.cdep.cdep.yml.cdepmanifest.CDepManifestBuilder.iOS;

/**
 * Semantic merge of two manifests
 */
public class MergeCDepManifestYmls extends CDepManifestYmlEquality {

  @Nullable
  private Object returnValue = null;

  @NotNull
  public static CDepManifestYml merge(CDepManifestYml left, CDepManifestYml right) {
    MergeCDepManifestYmls thiz = new MergeCDepManifestYmls();
    thiz.covisit(left, right);
    if (!thiz.areEqual) {
      throw new RuntimeException(String.format("Manifests were different at %s", thiz.firstDifference));
    }
    assert thiz.returnValue != null;
    return (CDepManifestYml) thiz.returnValue;
  }

  @Override
  protected void covisit(String name, Object left, Object right, @NotNull Class<?> type) {
    returnValue = null;
    super.covisit(name, left, right, type);
  }

  @Override
  public void covisitString(@Nullable String name, String left, String right) {
    if (name != null && name.equals("sha256")) {
      // Ignore hashes when merging
      return;
    }
    super.covisitString(name, left, right);
  }

  @Override
  public void covisitCDepManifestYml(String name, @Nullable CDepManifestYml left, @Nullable CDepManifestYml right) {
    if (left == null && right == null) {
      returnValue = null;
      return;
    }
    if (left == null) {
      returnValue = right;
      return;
    }
    if (right == null) {
      returnValue = left;
      return;
    }
    covisit("coordinate", left.coordinate, right.coordinate, Coordinate.class);
    covisitHardNameDependencyArray("dependencies", left.dependencies, right.dependencies);
    covisit("interfaces", left.interfaces, right.interfaces, Interfaces.class);
    covisit("example", left.example, right.example, String.class);
    covisit("linux", left.linux, right.linux, Object.class);
    covisit("iOS", left.iOS, right.iOS, iOS.class);
    iOS ios = (iOS) returnValue;
    covisit("android", left.android, right.android, Android.class);
    Android android = (Android) returnValue;
    covisit("linux", left.linux, right.linux, Linux.class);
    Linux linux = (Linux) returnValue;

    CDepManifestYmlVersion sourceVersion = left.sourceVersion;
    if (right.sourceVersion.ordinal() < sourceVersion.ordinal()) {
      sourceVersion = right.sourceVersion;
    }
    // If any differences were allow, return the "right" version.
    returnValue = new CDepManifestYml(
        sourceVersion,
        right.coordinate,
        right.dependencies,
        right.license,
        right.interfaces,
        android, ios, linux, left.example);
  }

  @Override
  public void covisitAndroid(String name, @Nullable Android left, @Nullable Android right) {
    if (left == null && right == null) {
      returnValue = null;
      return;
    }
    if (left == null) {
      returnValue = right;
      return;
    }
    if (right == null) {
      returnValue = left;
      return;
    }
    covisitHardNameDependencyArray("dependencies", left.dependencies, right.dependencies);
    covisitAndroidArchiveArray("archive", left.archives, right.archives);
    AndroidArchive archives[] = (AndroidArchive[]) returnValue;
    returnValue = android(left.dependencies, archives);
  }

  @Override
  public void covisitiOS(String name, @Nullable iOS left, @Nullable iOS right) {
    if (left == null && right == null) {
      returnValue = null;
      return;
    }
    if (left == null) {
      returnValue = right;
      return;
    }
    if (right == null) {
      returnValue = left;
      return;
    }
    covisitHardNameDependencyArray("dependencies", left.dependencies, right.dependencies);
    covisitiOSArchiveArray("archive", left.archives, right.archives);
    iOSArchive archives[] = (iOSArchive[]) returnValue;
    assert archives != null;
    returnValue = iOS(left.dependencies, archives);
  }

  @Override
  public void covisitLinux(String name, @Nullable Linux left, @Nullable Linux right) {
    if (left == null && right == null) {
      returnValue = null;
      return;
    }
    if (left == null) {
      returnValue = right;
      return;
    }
    if (right == null) {
      returnValue = left;
      return;
    }
    covisitLinuxArchiveArray("archive", left.archives, right.archives);
    LinuxArchive archives[] = (LinuxArchive[]) returnValue;
    assert archives != null;
    returnValue = linux(archives);
  }

  @Override
  public void covisitAndroidArchiveArray(String name, @Nullable AndroidArchive[] left, @Nullable AndroidArchive[] right) {
    if (left == null) {
      returnValue = right;
      return;
    }
    if (right == null) {
      returnValue = left;
      return;
    }
    AndroidArchive result[] = new AndroidArchive[left.length + right.length];
    int j = 0;
    for (int i = 0; i < left.length; ++i, ++j) {
      result[j] = left[i];
    }
    for (int i = 0; i < right.length; ++i, ++j) {
      result[j] = right[i];
    }
    returnValue = result;
  }

  @Override
  public void covisitiOSArchiveArray(String name, @Nullable iOSArchive[] left, @Nullable iOSArchive[] right) {
    if (left == null) {
      returnValue = right;
      return;
    }
    if (right == null) {
      returnValue = left;
      return;
    }
    iOSArchive result[] = new iOSArchive[left.length + right.length];
    int j = 0;
    for (int i = 0; i < left.length; ++i, ++j) {
      result[j] = left[i];
    }
    for (int i = 0; i < right.length; ++i, ++j) {
      result[j] = right[i];
    }
    returnValue = result;
  }

  @Override
  public void covisitLinuxArchiveArray(String name, @Nullable LinuxArchive[] left, @Nullable LinuxArchive[] right) {
    if (left == null) {
      returnValue = right;
      return;
    }
    if (right == null) {
      returnValue = left;
      return;
    }
    LinuxArchive result[] = new LinuxArchive[left.length + right.length];
    int j = 0;
    for (int i = 0; i < left.length; ++i, ++j) {
      result[j] = left[i];
    }
    for (int i = 0; i < right.length; ++i, ++j) {
      result[j] = right[i];
    }
    returnValue = result;
  }
}
