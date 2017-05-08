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

import io.cdep.annotations.Nullable;
import io.cdep.cdep.utils.ArrayUtils;
import io.cdep.cdep.yml.cdepmanifest.AndroidABI;
import io.cdep.cdep.yml.cdepmanifest.AndroidArchive;
import io.cdep.cdep.yml.cdepmanifest.CDepManifestYmlRewriter;

import java.io.File;

public class FillMissingFieldsBasedOnFilepathRewriter extends CDepManifestYmlRewriter {
  // Note, this list must be in order such that long strings come before shorter prefixes
  // of the same string.
  private final String[] androidABIs = new String[] {
      "arm64-v8a",
      "armeabi-v7a",
      "armeabi",
      "mips64",
      "mips",
      "x86_64",
      "x86"};

  @Nullable
  @Override
  protected AndroidArchive[] visitAndroidArchiveArray(@Nullable AndroidArchive[] archives) {
    if (archives == null) {
      return null;
    }
    AndroidArchive[] result = new AndroidArchive[archives.length];
    for (int i = 0; i < result.length; ++i) {
      result[i] = visitAndroidArchive(archives[i]);
    }
    return ArrayUtils.removeNullElements(result, AndroidArchive.class);
  }

  @Nullable
  @Override
  protected AndroidArchive visitAndroidArchive(@Nullable AndroidArchive archive) {
    if (archive == null || archive.file.isEmpty()) {
      return null;
    }
    AndroidABI abi = archive.abi;
    if (abi.name.isEmpty()) {
      for (String androidABI : androidABIs) {
        if (archive.file.contains(androidABI)) {
          abi = new AndroidABI(androidABI);
          break;
        }
      }
    }

    String runtime = archive.runtime;
    if (runtime.isEmpty()) {
      if (archive.file.contains("c++")) {
        runtime = "c++";
      } else if (archive.file.contains("cxx")) {
        runtime = "c++";
      } else if (archive.file.contains("gnustl")) {
        runtime = "gnustl";
      } else if (archive.file.contains("stlport")) {
        runtime = "stlport";
      }
    }

    String libs[] = archive.libs;
    if (libs.length == 0) {
      File file = new File(archive.file);
      libs = new String[] { file.getName() };
    }

    String platform = archive.platform;
    if (platform.isEmpty()) {
      File remaining = new File(archive.file);
      while (remaining != null) {
        String segment = remaining.getName();
        if (segment.startsWith("android-")) {
          platform = segment.substring(segment.lastIndexOf("-") + 1);
          break;
        }
        remaining = remaining.getParentFile();
      }
      if (platform.isEmpty()) {
        // If the platform isn't specified then optimistically choose a very old version for
        // best compatibility.
        platform = "12";
      }
    }
    return new AndroidArchive(
        archive.file,
        archive.sha256,
        archive.size,
        archive.ndk,
        archive.compiler,
        runtime,
        platform,
        archive.builder,
        abi,
        archive.include,
        libs,
        archive.flavor);
  }
}
