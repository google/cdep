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
import io.cdep.cdep.Version;
import io.cdep.cdep.pod.PlainOldDataReadonlyVisitor;

@SuppressWarnings("unused")
abstract public class CDepManifestYmlReadonlyVisitor extends PlainOldDataReadonlyVisitor {

  @SuppressWarnings("EmptyMethod")
  public void visitCDepManifestYmlVersion(@NotNull String name, CDepManifestYmlVersion sourceVersion) {

  }

  public void visitCDepManifestYml(@Nullable String name, @NotNull CDepManifestYml value) {
    visitPlainOldDataObject(name, value);
  }

  public void visitLicense(@Nullable String name, @NotNull License value) {
    visitPlainOldDataObject(name, value);
  }


  public void visitInterfaces(@Nullable String name, @NotNull Interfaces value) {
    visitPlainOldDataObject(name, value);
  }

  public void visitHardNameDependency(@Nullable String name, @NotNull HardNameDependency value) {
    visitPlainOldDataObject(name, value);
  }

  public void visitCoordinate(@Nullable String name, @NotNull Coordinate value) {
    visitPlainOldDataObject(name, value);
  }

  public void visitVersion(@Nullable String name, @NotNull Version value) {
    visitPlainOldDataObject(name, value);
  }

  public void visitHardNameDependencyArray(@Nullable String name, @NotNull HardNameDependency array[]) {
    visitArray(name, array, HardNameDependency.class);
  }

  public void visitAndroidArchiveArray(@Nullable String name, @NotNull AndroidArchive array[]) {
    visitArray(name, array, AndroidArchive.class);
  }

  public void visitiOSArchiveArray(@Nullable String name, @NotNull iOSArchive array[]) {
    visitArray(name, array, iOSArchive.class);
  }

  public void visitiOSPlatform(@Nullable String name, @NotNull iOSPlatform value) {
    visitPlainOldDataObject(null, value);
  }

  public void visitiOSArchitecture(@Nullable String name, @NotNull iOSArchitecture value) {
    visitPlainOldDataObject(null, value);
  }

  public void visitCxxLanguageFeaturesArray(@Nullable String name, @NotNull CxxLanguageFeatures value[]) {
    visitArray(name, value, CxxLanguageFeatures.class);
  }

  public void visitCxxLanguageFeatures(@Nullable String name, @NotNull CxxLanguageFeatures value) {
  }

  public void visitArchive(@Nullable String name, @NotNull Archive value) {
    visitPlainOldDataObject(name, value);
  }

  public void visitAndroid(@Nullable String name, @NotNull Android value) {
    visitPlainOldDataObject(name, value);
  }

  public void visitiOS(@Nullable String name, @NotNull iOS value) {
    visitPlainOldDataObject(name, value);
  }

  public void visitAndroidArchive(@Nullable String name, @NotNull AndroidArchive value) {
    visitPlainOldDataObject(name, value);
  }

  public void visitAndroidABI(@Nullable String name, @NotNull AndroidABI value) {
    visitPlainOldDataObject(name, value);
  }

  public void visitiOSArchive(@Nullable String name, @NotNull iOSArchive value) {
    visitPlainOldDataObject(name, value);
  }

  public void visitLinux(@Nullable String name, @NotNull Linux value) {
    visitPlainOldDataObject(name, value);
  }

  public void visitLinuxArchiveArray(@Nullable String name, @NotNull LinuxArchive array[]) {
    visitArray(name, array, LinuxArchive.class);
  }

  public void visitLinuxArchive(@Nullable String name, @NotNull LinuxArchive value) {
    visitPlainOldDataObject(name, value);
  }
}
