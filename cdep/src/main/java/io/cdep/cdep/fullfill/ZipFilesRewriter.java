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

import static io.cdep.cdep.utils.Invariant.fail;
import static io.cdep.cdep.utils.Invariant.failIf;
import static io.cdep.cdep.utils.Invariant.require;

import io.cdep.annotations.NotNull;
import io.cdep.annotations.Nullable;
import io.cdep.cdep.utils.ArchiveUtils;
import io.cdep.cdep.utils.StringUtils;
import io.cdep.cdep.yml.cdepmanifest.AndroidArchive;
import io.cdep.cdep.yml.cdepmanifest.Archive;
import io.cdep.cdep.yml.cdepmanifest.CDepManifestYml;
import io.cdep.cdep.yml.cdepmanifest.CDepManifestYmlRewriter;
import io.cdep.cdep.yml.cdepmanifest.LinuxArchive;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Zip up file entries into layout folder. Doesn't record SHA or size since that is done
 * in a subsequent pass.
 */
public class ZipFilesRewriter extends CDepManifestYmlRewriter {
  private final File layout;
  private final File staging;
  private final List<File> zips = new ArrayList<>();
  @Nullable
  private String prefix = "";

  ZipFilesRewriter(File layout, File staging) {
    this.layout = layout;
    this.staging = staging;
  }

  @NotNull
  @Override
  public CDepManifestYml visitCDepManifestYml(@NotNull CDepManifestYml value) {
    prefix = value.coordinate.artifactId;
    prefix = prefix.replace("/", "-");
    prefix = prefix.replace("\\", "-");
    prefix = prefix.replace(":", "-");
    return super.visitCDepManifestYml(value);
  }

  @Nullable
  @Override
  protected Archive visitArchive(@Nullable Archive archive) {
    if (archive == null || archive.file.isEmpty()) {
      return null;
    }
    if (archive.file.endsWith(".zip")) {
      return archive;
    }
    PathMapping mappings[] = PathMapping.parse(archive.file);
    require(mappings.length > 0,
        "File mapping '%s' did not resolve to any local files", archive.file);
    File layoutZipFile = getLayoutZipFile(prefix, "headers");
    File stagingZipFolder = getStagingZipFolder(layoutZipFile, "include");

    copyFilesToStaging(mappings, stagingZipFolder);

    // Zip that file
    zipStagingFilesIntoArchive(stagingZipFolder.getParentFile(), layoutZipFile);

    zips.add(layoutZipFile);

    return new Archive(
        layoutZipFile.getName(),
        "",
        0L,
        "include",
        archive.requires
    );
  }

  @Nullable
  @Override
  protected AndroidArchive visitAndroidArchive(@Nullable AndroidArchive archive) {
    if (archive == null || archive.file.isEmpty()) {
      return archive;
    }
    if (archive.file.endsWith(".zip")) {
      return archive;
    }
    PathMapping mappings[] = PathMapping.parse(archive.file);
    require(mappings.length > 0,
        "File mapping '%s' did not resolve to any local files", archive.file);
    File layoutZipFile = getLayoutZipFile(
        prefix,
        "android",
        archive.ndk,
        archive.runtime,
        archive.platform,
        archive.builder,
        archive.flavor,
        archive.abi.name);
    File stagingZipFolder = getStagingZipFolder(layoutZipFile, "lib/" + archive.abi);

    copyFilesToStaging(mappings, stagingZipFolder);

    // Record the names of the libraries.
    String libs[] = new String[mappings.length];
    for (int i = 0; i < mappings.length; ++ i) {
      libs[i] = mappings[i].to.getName();
    }

    // Zip that file
    zipStagingFilesIntoArchive(stagingZipFolder.getParentFile().getParentFile(), layoutZipFile);

    zips.add(layoutZipFile);

    return new AndroidArchive(
        layoutZipFile.getName(),
        "",
        0L,
        archive.ndk,
        archive.compiler,
        archive.runtime,
        archive.platform,
        archive.builder,
        archive.abi,
        archive.include,
        libs,
        archive.flavor);
  }

  @Override
  protected LinuxArchive visitLinuxArchive(@NotNull LinuxArchive archive) {
    if (archive == null || archive.file.isEmpty()) {
      return archive;
    }
    if (archive.file.endsWith(".zip")) {
      return archive;
    }
    PathMapping mappings[] = PathMapping.parse(archive.file);
    require(mappings.length > 0,
        "File mapping '%s' did not resolve to any local files", archive.file);
    File layoutZipFile = getLayoutZipFile(
        prefix,
        "linux");
    File stagingZipFolder = getStagingZipFolder(layoutZipFile, "lib/");

    copyFilesToStaging(mappings, stagingZipFolder);

    // Record the names of the libraries.
    String libs[] = new String[mappings.length];
    for (int i = 0; i < mappings.length; ++ i) {
      libs[i] = mappings[i].to.getName();
    }

    // Zip that file
    zipStagingFilesIntoArchive(stagingZipFolder.getParentFile().getParentFile(), layoutZipFile);

    zips.add(layoutZipFile);

    return new LinuxArchive(
        layoutZipFile.getName(),
        "",
        0L,
        libs,
        archive.include);
  }

  @NotNull
  private File getStagingZipFolder(@NotNull File layoutZipFile, @NotNull String folder) {
    File stagingZipFolder = new File(staging, layoutZipFile.getName());
    stagingZipFolder = new File(stagingZipFolder, folder);
    //noinspection ResultOfMethodCallIgnored
    stagingZipFolder.delete();
    return stagingZipFolder;
  }

  @NotNull
  private File getLayoutZipFile(String ... keys) {
    String prefix = StringUtils.joinOnSkipNullOrEmpty("-", keys);
    File layoutZipFile = new File(layout, prefix + ".zip");
    if (layoutZipFile.exists()) {
      //noinspection ResultOfMethodCallIgnored
      layoutZipFile.delete();
    }
    return replaceInvalidCharacters(layoutZipFile);
  }

  @NotNull
  private File replaceInvalidCharacters(@NotNull File file) {
    String baseName = file.getName();
    baseName = baseName.replace("/", "-");
    baseName = baseName.replace("\\", "-");
    baseName = baseName.replace(":", "-");
    baseName = baseName.replace("+", "p");
    return new File(file.getParentFile(), baseName);
  }

  private void copyFilesToStaging(@NotNull PathMapping[] mappings, File stagingZipFolder) {
    for (PathMapping mapping : mappings) {
      if (failIf(
          !mapping.from.exists(),
          "Could not zip file %s because it didn't exist",
          mapping.from.getAbsoluteFile())) {
        continue;
      }
      File stagingZipFile = new File(stagingZipFolder, mapping.to.getPath());

      // Make the staging zip folder
      //noinspection ResultOfMethodCallIgnored
      stagingZipFile.getParentFile().mkdirs();

      // Copy the single header file to the staging zip folder.
      copyFileToStaging(mapping, stagingZipFile);
    }
  }

  private void zipStagingFilesIntoArchive(@NotNull File stagingZipFolder,
      @NotNull File layoutZipFile) {
    try {
      ArchiveUtils.pack(stagingZipFolder.toPath(), layoutZipFile.toPath());
    } catch(InvalidPathException e) {
      fail("Path isn't valid: %s", e.getMessage());
    } catch (FileNotFoundException| NoSuchFileException e) {
      fail("File %s doesn't exist", e.getMessage());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void copyFileToStaging(@NotNull PathMapping mapping, @NotNull File stagingZipFile) {
    try {
      Files.copy(mapping.from.toPath(), stagingZipFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    } catch (InvalidPathException e) {
      fail("Path isn't valid: %s", e.getMessage());
    } catch (FileNotFoundException| NoSuchFileException e) {
      fail("File %s doesn't exist", e.getMessage());
    } catch(FileSystemException e) {
      // Seems to be unix-only
      fail("File had a problem: %s", e.getMessage());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @NotNull
  public Collection<? extends File> getZips() {
    return zips;
  }
}
