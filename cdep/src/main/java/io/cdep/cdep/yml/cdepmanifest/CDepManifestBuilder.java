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

public class CDepManifestBuilder {

  @NotNull
  public static HardNameDependency hardname(@NotNull String compile, @NotNull String sha256) {
    return new HardNameDependency(compile, sha256);
  }

  @SuppressWarnings("SameParameterValue")
  @NotNull
  public static Archive archive(@NotNull String file, @NotNull String sha256, long size,
      @NotNull String include, @NotNull CxxLanguageFeatures requires[]) {
    return new Archive(file, sha256, size, include, requires);
  }

  @NotNull
  public static Android android(@Nullable HardNameDependency[] dependencies, @Nullable AndroidArchive archives[]) {
    return new Android(dependencies, archives);
  }

  @NotNull
  public static iOS iOS(@Nullable HardNameDependency[] dependencies, @NotNull iOSArchive archives[]) {
    return new iOS(dependencies, archives);
  }

  @NotNull
  public static Linux linux(@NotNull LinuxArchive archives[]) {
    return new Linux(archives);
  }
}
