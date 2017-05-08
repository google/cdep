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
package io.cdep.cdep.yml;

import io.cdep.annotations.NotNull;
import io.cdep.cdep.ResolvedManifests;
import io.cdep.cdep.yml.cdepmanifest.AndroidABI;
import io.cdep.cdep.yml.cdepmanifest.AndroidArchive;
import io.cdep.cdep.yml.cdepmanifest.CDepManifestYml;
import io.cdep.cdep.yml.cdepmanifest.CxxLanguageFeatures;
import net.java.quickcheck.Generator;

import java.util.List;

import static io.cdep.cdep.utils.LongUtils.nullToZero;
import static net.java.quickcheck.generator.CombinedGenerators.arrays;
import static net.java.quickcheck.generator.PrimitiveGenerators.*;

public class AndroidArchiveGenerator implements Generator<AndroidArchive> {
  private static List<ResolvedManifests.TestManifest> allManifests = ResolvedManifests.allTestManifest();

  @NotNull
  final public Generator<Kind> kind = enumValues(Kind.class);
  @NotNull
  final public Generator<String> file = new ShortFilenameGenerator();
  @NotNull
  final public Generator<String> sha256 = strings();
  @NotNull
  final public Generator<AndroidABI> abi = new AndroidABIGenerator();
  @NotNull
  final public Generator<Long> size = longs();
  @NotNull
  final public Generator<String> include = strings();
  @NotNull
  final public Generator<String[]> libs = arrays(new ShortFilenameGenerator(), String.class);
  @NotNull
  final public Generator<Long> longs = longs();
  @NotNull
  final public Generator<String> strings = strings();
  @NotNull
  final public Generator<CxxLanguageFeatures[]> requires = arrays(enumValues(CxxLanguageFeatures.class),
      CxxLanguageFeatures.class);

  @Override
  public AndroidArchive next() {
    switch (kind.next()) {
      case missing:
        return null;
      case actual: {
        long n1 = Math.abs(nullToZero(longs.next()));

        CDepManifestYml manifest = allManifests.get((int) (n1 % allManifests.size())).manifest.cdepManifestYml;
        if (manifest.android != null && manifest.android.archives != null && manifest.android.archives.length > 0) {
          int n2 = (int) (Math.abs(nullToZero(longs.next()) % manifest.android.archives.length));
          return manifest.android.archives[n2];
        }
        break;
      }
    }
    return new AndroidArchive(
        file.next(),
        sha256.next(),
        nullToZero(size.next()),
        strings.next(),
        strings.next(),
        strings.next(),
        strings.next(),
        strings.next(),
        abi.next(),
        include.next(),
        libs.next(),
        strings.next()
    );
  }

  enum Kind {
    missing, random, actual
  }
}
