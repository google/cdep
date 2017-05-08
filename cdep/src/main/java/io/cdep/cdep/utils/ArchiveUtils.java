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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ArchiveUtils {

  /**
   * Unzip the given file.
   */
  public static void unzip(@NotNull File localArchive, @NotNull File localUnzipFolder) throws IOException {
    ZipFile zipFile = new ZipFile(localArchive.getPath());
    Enumeration<?> enu = zipFile.entries();
    while (enu.hasMoreElements()) {
      ZipEntry zipEntry = (ZipEntry) enu.nextElement();

      String name = zipEntry.getName();
      //            long size = zipEntry.getSize();
      //            long compressedSize = zipEntry.getCompressedSize();
      //            System.out.printf("name: %-20s | size: %6d | compressed size: %6d\n",
      //                name, size, compressedSize);

      File file = new File(localUnzipFolder, name);
      if (name.endsWith("/")) {
        //noinspection ResultOfMethodCallIgnored
        file.mkdirs();
        continue;
      }

      File parent = file.getParentFile();
      if (parent != null) {
        //noinspection ResultOfMethodCallIgnored
        parent.mkdirs();
      }

      InputStream is = zipFile.getInputStream(zipEntry);
      FileOutputStream fos = new FileOutputStream(file);
      byte[] bytes = new byte[1024];
      int length;
      while ((length = is.read(bytes)) >= 0) {
        fos.write(bytes, 0, length);
      }
      is.close();
      fos.close();
    }
    zipFile.close();
  }

  public static void pack(@NotNull final Path folder, @NotNull final Path zipFilePath) throws IOException {
    try (
            FileOutputStream fos = new FileOutputStream(zipFilePath.toFile());
            ZipOutputStream zos = new ZipOutputStream(fos)
    ) {
        Files.walkFileTree(folder, new SimpleFileVisitor<Path>() {
            @NotNull
            public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                zos.putNextEntry(new ZipEntry(folder.relativize(file).toString()));
                Files.copy(file, zos);
                zos.closeEntry();
                return FileVisitResult.CONTINUE;
            }

            @NotNull
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attributes) throws IOException {
                zos.putNextEntry(new ZipEntry(folder.relativize(dir).toString() + "/"));
                zos.closeEntry();
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
}
