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

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;

public class FileUtils {

  public static void copyFile(@NotNull File sourceFile, @NotNull File destFile) throws IOException {
    if (!destFile.exists()) {
      //noinspection ResultOfMethodCallIgnored
      destFile.createNewFile();
    }

    FileChannel source = null;
    FileChannel destination = null;

    try {
      source = new FileInputStream(sourceFile).getChannel();
      destination = new FileOutputStream(destFile).getChannel();
      destination.transferFrom(source, 0, source.size());
    } finally {
      if (source != null) {
        source.close();
      }
      if (destination != null) {
        destination.close();
      }
    }
  }

  @NotNull
  public static String readAllText(@NotNull File file) throws IOException {
    return new String(Files.readAllBytes(Paths.get(file.getCanonicalPath())), UTF_8);
  }

  public static void writeTextToFile(@NotNull File file, @NotNull String body) throws IOException {
    BufferedWriter writer = null;
    //noinspection ResultOfMethodCallIgnored
    file.getAbsoluteFile().getParentFile().mkdirs();
    //noinspection ResultOfMethodCallIgnored
    file.delete();
    try {
      writer = Files.newBufferedWriter(file.toPath(), UTF_8);
      writer.write(body);
    } finally {
      if (writer != null) {
        writer.close();
      }
    }
  }

  @NotNull
  public static Collection<File> listFileTree(@Nullable File dir) {
    Set<File> fileTree = new HashSet<>();
    if (dir == null || dir.listFiles() == null) {
      return fileTree;
    }
    //noinspection ConstantConditions
    for (File entry : dir.listFiles()) {
      if (entry.isFile())
        fileTree.add(entry);
      else
        fileTree.addAll(listFileTree(entry));
    }
    return fileTree;
  }

  @NotNull
  public static String readAllText(@NotNull InputStream stream) {
    java.util.Scanner s = new java.util.Scanner(stream).useDelimiter("\\A");
    return s.hasNext() ? s.next() : "";
  }
}
