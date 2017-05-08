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
package io.cdep.cdep.yml.cdepmanifest.v3;

import io.cdep.annotations.NotNull;
import io.cdep.annotations.Nullable;
import io.cdep.cdep.utils.ArrayUtils;
import io.cdep.cdep.utils.LongUtils;
import io.cdep.cdep.utils.ObjectUtils;
import io.cdep.cdep.utils.StringUtils;
import io.cdep.cdep.yml.cdepmanifest.AndroidABI;
import io.cdep.cdep.yml.cdepmanifest.CDepManifestYmlVersion;
import io.cdep.cdep.yml.cdepmanifest.HardNameDependency;
import io.cdep.cdep.yml.cdepmanifest.License;
import io.cdep.cdep.yml.cdepmanifest.v2.V2Reader;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static io.cdep.cdep.Coordinate.EMPTY_COORDINATE;
import static io.cdep.cdep.utils.Invariant.require;

public class V3Reader {

  @NotNull
  public static io.cdep.cdep.yml.cdepmanifest.CDepManifestYml convertStringToManifest(@NotNull String content) {
    Yaml yaml = new Yaml(new Constructor(io.cdep.cdep.yml.cdepmanifest.v3.CDepManifestYml.class));
    io.cdep.cdep.yml.cdepmanifest.CDepManifestYml manifest;
    try {
      CDepManifestYml prior = (CDepManifestYml) yaml.load(
          new ByteArrayInputStream(content.getBytes(StandardCharsets
              .UTF_8)));
      prior.sourceVersion = CDepManifestYmlVersion.v3;
      manifest = convert(prior);
      require(manifest.sourceVersion == CDepManifestYmlVersion.v3);
    } catch (YAMLException e) {
      manifest = convert(V2Reader.convertStringToManifest(content));
    }
    return manifest;
  }

  @NotNull
  private static io.cdep.cdep.yml.cdepmanifest.CDepManifestYml convert(@NotNull CDepManifestYml manifest) {
    return new io.cdep.cdep.yml.cdepmanifest.CDepManifestYml(
        manifest.sourceVersion != null ? manifest.sourceVersion : CDepManifestYmlVersion.vlatest,
        ObjectUtils.nullToDefault(manifest.coordinate, EMPTY_COORDINATE),
        ArrayUtils.nullToEmpty(manifest.dependencies, HardNameDependency.class),
        new License(),
        manifest.interfaces,
        convertAndroid(manifest.android),
        convertiOS(manifest.iOS),
        convertLinux(manifest.linux),
        StringUtils.nullToEmpty(manifest.example));
  }

  @Nullable
  private static io.cdep.cdep.yml.cdepmanifest.Linux convertLinux(@Nullable Linux linux) {
    if (linux == null) {
      return null;
    }
    assert linux.archives != null;
    return new io.cdep.cdep.yml.cdepmanifest.Linux(convertLinuxArchiveArray(linux.archives));
  }

  @NotNull
  private static io.cdep.cdep.yml.cdepmanifest.LinuxArchive[] convertLinuxArchiveArray(@NotNull LinuxArchive[] archives) {
    io.cdep.cdep.yml.cdepmanifest.LinuxArchive result[] = new io.cdep.cdep.yml.cdepmanifest.LinuxArchive[archives.length];
    for (int i = 0; i < result.length; ++i) {
      result[i] = convertLinuxArchive(archives[i]);
    }
    return result;
  }

  @NotNull
  private static io.cdep.cdep.yml.cdepmanifest.LinuxArchive convertLinuxArchive(@NotNull LinuxArchive archive) {
    assert archive.file != null;
    assert archive.sha256 != null;
    assert archive.size != null;
    return new io.cdep.cdep.yml.cdepmanifest.LinuxArchive(
        archive.file,
        archive.sha256,
        archive.size,
        new String[] { archive.lib },
        StringUtils.nullToEmpty(archive.include)
    );
  }

  @Nullable
  private static io.cdep.cdep.yml.cdepmanifest.iOS convertiOS(@Nullable iOS ios) {
    if (ios == null) {
      return null;
    }
    return new io.cdep.cdep.yml.cdepmanifest.iOS(ios.dependencies, convertiOSArchiveArray(ios.archives));
  }

  @Nullable
  private static io.cdep.cdep.yml.cdepmanifest.iOSArchive[] convertiOSArchiveArray(@Nullable iOSArchive[] archives) {
    if (archives == null) {
      return null;
    }
    io.cdep.cdep.yml.cdepmanifest.iOSArchive result[] = new io.cdep.cdep.yml.cdepmanifest.iOSArchive[archives.length];
    for (int i = 0; i < result.length; ++i) {
      result[i] = convertiOSArchive(archives[i]);
    }
    return result;
  }

  @Nullable
  private static io.cdep.cdep.yml.cdepmanifest.iOSArchive convertiOSArchive(@NotNull iOSArchive archive) {
    return new io.cdep.cdep.yml.cdepmanifest.iOSArchive(
        StringUtils.nullToEmpty(archive.file),
        StringUtils.nullToEmpty(archive.sha256),
        LongUtils.nullToZero(archive.size),
        archive.platform,
        archive.architecture,
        StringUtils.nullToEmpty(archive.sdk),
        StringUtils.nullToEmpty(archive.include),
        new String[] { archive.lib },
        StringUtils.nullToEmpty(archive.flavor)
    );
  }

  @Nullable
  private static io.cdep.cdep.yml.cdepmanifest.Android convertAndroid(@Nullable Android android) {
    if (android == null) {
      return null;
    }
    return new io.cdep.cdep.yml.cdepmanifest.Android(android.dependencies, convertAndroidArchiveArray(android.archives));
  }

  @Nullable
  private static io.cdep.cdep.yml.cdepmanifest.AndroidArchive[] convertAndroidArchiveArray(@Nullable AndroidArchive[] archives) {
    if (archives == null) {
      return null;
    }
    io.cdep.cdep.yml.cdepmanifest.AndroidArchive result[] = new io.cdep.cdep.yml.cdepmanifest.AndroidArchive[archives.length];
    for (int i = 0; i < result.length; ++i) {
      result[i] = convertAndroidArchive(archives[i]);
    }
    return result;
  }

  @Nullable
  private static io.cdep.cdep.yml.cdepmanifest.AndroidArchive convertAndroidArchive(@NotNull AndroidArchive archive) {
    //noinspection ConstantConditions
    return new io.cdep.cdep.yml.cdepmanifest.AndroidArchive(
        StringUtils.nullToEmpty(archive.file),
        StringUtils.nullToEmpty(archive.sha256),
        LongUtils.nullToZero(archive.size),
        StringUtils.nullToEmpty(archive.ndk),
        StringUtils.nullToEmpty(archive.compiler),
        StringUtils.nullToEmpty(archive.runtime),
        StringUtils.nullToEmpty(archive.platform),
        StringUtils.nullToEmpty(archive.builder),
        new AndroidABI(StringUtils.nullToEmpty(archive.abi)),
        StringUtils.nullToEmpty(archive.include),
        StringUtils.singletonArrayOrEmpty(archive.lib),
        StringUtils.nullToEmpty(archive.flavor)
    );
  }
}
