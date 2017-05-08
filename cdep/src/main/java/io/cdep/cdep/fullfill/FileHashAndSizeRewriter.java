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
package io.cdep.cdep.fullfill;

import static io.cdep.cdep.utils.Invariant.failIf;

import io.cdep.annotations.NotNull;
import io.cdep.annotations.Nullable;
import io.cdep.cdep.utils.HashUtils;
import io.cdep.cdep.yml.cdepmanifest.AndroidArchive;
import io.cdep.cdep.yml.cdepmanifest.Archive;
import io.cdep.cdep.yml.cdepmanifest.CDepManifestYmlRewriter;
import io.cdep.cdep.yml.cdepmanifest.LinuxArchive;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class FileHashAndSizeRewriter extends CDepManifestYmlRewriter {
  private final File layoutFolder;

  FileHashAndSizeRewriter(File layoutFolder) {
    this.layoutFolder = layoutFolder;
  }

  @Nullable
  @Override
  protected Archive visitArchive(Archive archive) {
    if (archive == null) {
      return null;
    }
    if (archive.sha256.isEmpty()) {
      File file = new File(layoutFolder, archive.file);
      if (failIf(!file.isFile(),
          "Could not hash file %s because it didn't exist",
          archive.file)) {
        return archive;
      }
      try {
        return new Archive(
            archive.file,
            HashUtils.getSHA256OfFile(file),
            file.length(),
            archive.include,
            archive.requires);
      } catch (@NotNull NoSuchAlgorithmException | IOException e) {
        throw new RuntimeException(e);
      }
    }
    return archive;
  }

  @Nullable
  @Override
  protected AndroidArchive visitAndroidArchive(@NotNull AndroidArchive archive) {
    if (archive.sha256.isEmpty()) {
      File file = new File(layoutFolder, archive.file);
      if (failIf(!file.isFile(),
          "Could not hash file %s because it didn't exist",
          archive.file)) {
        return archive;
      }
      try {
        return new AndroidArchive(
            archive.file,
            HashUtils.getSHA256OfFile(file),
            file.length(),
            archive.ndk,
            archive.compiler,
            archive.runtime,
            archive.platform,
            archive.builder,
            archive.abi,
            archive.include,
            archive.libs,
            archive.flavor);
      } catch(@NotNull NoSuchAlgorithmException | IOException e) {
        throw new RuntimeException(e);
      }
    }
    return archive;
  }

  @Override
  protected LinuxArchive visitLinuxArchive(@NotNull LinuxArchive archive) {
    if (archive.sha256.isEmpty()) {
      File file = new File(layoutFolder, archive.file);
      if (failIf(!file.isFile(),
          "Could not hash file %s because it didn't exist",
          archive.file)) {
        return archive;
      }
      try {
        return new LinuxArchive(
            archive.file,
            HashUtils.getSHA256OfFile(file),
            file.length(),
            archive.libs,
            archive.include);
      } catch(@NotNull NoSuchAlgorithmException | IOException e) {
        throw new RuntimeException(e);
      }
    }
    return archive;
  }
}
