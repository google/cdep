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
import io.cdep.annotations.Nullable;
import io.cdep.cdep.Coordinate;
import io.cdep.cdep.yml.cdepmanifest.*;

import static io.cdep.cdep.Coordinate.EMPTY_COORDINATE;
import static io.cdep.cdep.Version.EMPTY_VERSION;
import static io.cdep.cdep.utils.ArrayUtils.nullToEmpty;
import static io.cdep.cdep.utils.ArrayUtils.removeNullElements;
import static io.cdep.cdep.utils.LongUtils.nullToZero;
import static io.cdep.cdep.utils.ObjectUtils.nullToDefault;
import static io.cdep.cdep.utils.StringUtils.nullToEmpty;

/**
 * When Yaml is de-serialized, it may contain null in fields marked @NonNull.
 * Fix those
 */
public class ConvertNullToDefaultRewriter extends CDepManifestYmlRewriter {

  @Nullable
  @Override
  protected AndroidArchive visitAndroidArchive(@NotNull AndroidArchive archive) {
    return super.visitAndroidArchive(new AndroidArchive(
        nullToEmpty(archive.file),
        nullToEmpty(archive.sha256),
        nullToZero(archive.size),
        nullToEmpty(archive.ndk),
        nullToEmpty(archive.compiler),
        nullToEmpty(archive.runtime),
        nullToEmpty(archive.platform),
        nullToEmpty(archive.builder),
        nullToDefault(archive.abi, AndroidABI.EMPTY_ABI),
        nullToEmpty(archive.include),
        removeNullElements(nullToEmpty(archive.libs, String.class), String.class),
        nullToEmpty(archive.flavor)));
  }

  @NotNull
  @Override
  protected LinuxArchive visitLinuxArchive(@NotNull LinuxArchive archive) {
    return new LinuxArchive(
        nullToEmpty(archive.file),
        nullToEmpty(archive.sha256),
        nullToZero(archive.size),
        removeNullElements(nullToEmpty(archive.libs, String.class), String.class),
        nullToEmpty(archive.include));
  }

  @Nullable
  @Override
  protected Linux visitLinux(@Nullable Linux linux) {
    if (linux == null) {
      return null;
    }
    return new Linux(
        nullToEmpty(visitLinuxArchiveArray(linux.archives), LinuxArchive.class)
    );
  }

  @NotNull
  @Override
  public CDepManifestYml visitCDepManifestYml(@NotNull CDepManifestYml value) {
    return super.visitCDepManifestYml(new CDepManifestYml(
        value.sourceVersion,
        nullToDefault(value.coordinate, EMPTY_COORDINATE),
        removeNullElements(
            nullToEmpty(value.dependencies, HardNameDependency.class),
            HardNameDependency.class),
        nullToDefault(value.license, new License()),
        value.interfaces,
        value.android,
        value.iOS,
        value.linux,
        nullToEmpty(value.example)));
  }

  @NotNull
  @Override
  protected Coordinate visitCoordinate(@NotNull Coordinate coordinate) {
    return super.visitCoordinate(new Coordinate(
        nullToEmpty(coordinate.groupId),
        nullToEmpty(coordinate.artifactId),
        nullToDefault(coordinate.version, EMPTY_VERSION)
    ));
  }

  @Nullable
  @Override
  protected HardNameDependency visitHardNameDependency(@NotNull HardNameDependency dependency) {
    return super.visitHardNameDependency(new HardNameDependency(
        nullToEmpty(dependency.compile),
        nullToEmpty(dependency.sha256)));
  }

  @Nullable
  @Override
  protected Archive visitArchive(@Nullable Archive archive) {
    if (archive == null) {
      return null;
    }
    return super.visitArchive(new Archive(
        nullToEmpty(archive.file),
        nullToEmpty(archive.sha256),
        nullToZero(archive.size),
        nullToEmpty(archive.include),
        nullToEmpty(archive.requires, CxxLanguageFeatures.class)));
  }

  @NotNull
  @Override
  public iOSArchive visitiOSArchive(@NotNull iOSArchive archive) {
    return super.visitiOSArchive(new iOSArchive(
        nullToEmpty(archive.file),
        nullToEmpty(archive.sha256),
        nullToZero(archive.size),
        archive.platform,
        archive.architecture,
        nullToEmpty(archive.sdk),
        nullToEmpty(archive.include),
        removeNullElements(nullToEmpty(archive.libs, String.class), String.class),
        nullToEmpty(archive.flavor)));
  }
}
