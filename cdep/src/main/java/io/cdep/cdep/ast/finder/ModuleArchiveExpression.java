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
package io.cdep.cdep.ast.finder;

import io.cdep.annotations.NotNull;
import io.cdep.annotations.Nullable;
import io.cdep.cdep.yml.cdepmanifest.CxxLanguageFeatures;

import java.net.URL;

import static io.cdep.cdep.utils.Invariant.require;

public class ModuleArchiveExpression extends StatementExpression {
  @NotNull final public URL file; // The zip file.
  @NotNull final public String sha256;
  @NotNull final public Long size;
  @Nullable final public String include;
  @Nullable final public Expression includePath;
  @NotNull final public String libs[];
  @NotNull final public Expression libraryPaths[];
  @NotNull final public CxxLanguageFeatures requires[];

  public ModuleArchiveExpression(
      @NotNull URL file,
      @NotNull String sha256,
      @NotNull Long size,
      @Nullable String include, // Like "include"
      @Nullable Expression fullIncludePath,
      @NotNull String libs[], // Like "lib/libsqlite.a"
      @NotNull Expression libraryPaths[],
      @NotNull CxxLanguageFeatures requires[]) {
    require(libs.length == libraryPaths.length);
    this.file = file;
    this.sha256 = sha256;
    this.size = size;
    this.include = include;
    this.includePath = fullIncludePath;
    this.libs = libs;
    this.libraryPaths = libraryPaths;
    this.requires = requires;
  }
}
