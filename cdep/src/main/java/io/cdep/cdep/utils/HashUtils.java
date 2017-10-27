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
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Cryptographic hashing utility findFunctions.
 */
public class HashUtils {

  @NotNull
  private static String encodeHex(@NotNull byte[] digest) {
    StringBuilder sb = new StringBuilder();
    for (byte aDigest : digest) {
      sb.append(Integer.toString((aDigest & 0xff) + 0x100, 16).substring(1));
    }
    return sb.toString();
  }

  /*
   * Compute the SHA256 of the given local file. This produces the same result as:
   *
   * shasum -a 256 localfile.zip
   *
   * command run from bash.
   */
  @NotNull
  public static String getSHA256OfFile(@NotNull File local) throws NoSuchAlgorithmException, IOException {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    InputStream in = new FileInputStream(local);
    return hashToHex(digest, in);
  }

  @NotNull
  public static String getSHA256OfString(@NotNull String string) throws NoSuchAlgorithmException, IOException {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    InputStream in = new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8));
    return hashToHex(digest, in);
  }

  private static String hashToHex(MessageDigest digest, InputStream in) throws IOException {
    byte[] block = new byte[4096];
    int length;
    while ((length = in.read(block)) > 0) {
      digest.update(block, 0, length);
    }
    return encodeHex(digest.digest());
  }
}
