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
package io.cdep;

import io.cdep.annotations.NotNull;
import io.cdep.annotations.Nullable;
import io.cdep.cdep.generator.GeneratorEnvironment;
import io.cdep.cdep.utils.ReflectionUtils;
import org.junit.Test;

import java.io.*;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class TestAPI {
  private final GeneratorEnvironment environment = new GeneratorEnvironment(
      new File("./test-files/TestAPI/working"),
      null,
      false,
      false);

  private static String execute(List<String> command) throws IOException, InterruptedException {
    System.out.printf("%s\n", command);
    String result = Spawner.spawn(command);
    System.out.printf("%s\n", result);
    return result;
  }

  @Test
  public void testGetJvmLocation() {
    System.out.printf("%s", API.getJvmLocation());
  }

  @Test
  public void testGetAPIJar() throws Exception {
    System.out.printf("%s", ReflectionUtils.getLocation(API.class));
  }

  @Test
  public void testExecute() throws Exception {
    String result = execute(API.generateCDepCall(environment, "show", "folders"));
    System.out.print(result);
    assertThat(result).contains("cdep");
  }

  private static class Spawner {
    private static final int THREAD_JOIN_TIMEOUT_MILLIS = 2000;

    @NotNull
    private static String spawn(List<String> command) throws IOException, InterruptedException {
      Process proc = Runtime.getRuntime().exec(command.toArray(new String[command.size()]));

      // any error message?
      StreamReaderThread errorThread = new StreamReaderThread(proc.getErrorStream());

      // any output?
      StreamReaderThread outputThread = new StreamReaderThread(proc.getInputStream());

      // kick them off
      errorThread.start();
      outputThread.start();

      // Wait for process to finish
      proc.waitFor();

      // Wait for output capture threads to finish
      errorThread.join(THREAD_JOIN_TIMEOUT_MILLIS);
      outputThread.join(THREAD_JOIN_TIMEOUT_MILLIS);

      if (proc.exitValue() != 0) {
        System.err.println(errorThread.result());
        throw new RuntimeException(
            String.format("Spawned process failed with code %s", proc.exitValue()));
      }

      if (errorThread.ioe != null) {
        throw new RuntimeException(
            String.format("Problem reading stderr: %s", errorThread.ioe));
      }

      if (outputThread.ioe != null) {
        throw new RuntimeException(
            String.format("Problem reading stdout: %s", outputThread.ioe));
      }

      return outputThread.result();
    }

    /**
     * Read an input stream off of the main thread
     */
    private static class StreamReaderThread extends Thread {
      private final InputStream is;
      @SuppressWarnings("StringBufferField")
      private final StringBuilder output = new StringBuilder();
      @Nullable
      IOException ioe = null;

      public StreamReaderThread(InputStream is) {
        this.is = is;
      }

      @NotNull
      public String result() {
        return output.toString();
      }

      @Override
      public void run() {
        try {
          InputStreamReader streamReader = new InputStreamReader(is);
          try (BufferedReader bufferedReader = new BufferedReader(streamReader)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
              output.append(line);
              output.append("\n");
            }
          }
          //noinspection ThrowFromFinallyBlock

        } catch (IOException ioe) {
          this.ioe = ioe;
        }
      }
    }
  }
}