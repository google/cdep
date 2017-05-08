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
package io.cdep.cdep.io;

import static io.cdep.cdep.utils.StringUtils.safeFormat;
import static org.fusesource.jansi.Ansi.Attribute.INTENSITY_FAINT;
import static org.fusesource.jansi.Ansi.Color.GREEN;
import static org.fusesource.jansi.Ansi.Color.RED;
import static org.fusesource.jansi.Ansi.Color.WHITE;
import static org.fusesource.jansi.Ansi.ansi;

import io.cdep.annotations.NotNull;
import io.cdep.cdep.utils.HashUtils;
import java.io.IOException;
import java.io.PrintStream;
import java.security.NoSuchAlgorithmException;
import org.fusesource.jansi.AnsiConsole;

/**
 * Methods for dealing with command-line IO, messages, errors, etc.
 */
public class IO {
  final private static IO io = new IO();
  private PrintStream out = AnsiConsole.out;
  private PrintStream err = AnsiConsole.err;
  private boolean ansi = true;

  /**
   * Set the out stream and return the prior out stream.
   */
  public static PrintStream setOut(PrintStream out) {
    PrintStream original = io.out;
    io.out = out;
    return original;
  }

  /**
   * Set the error stream and return the prior out stream.
   */
  public static PrintStream setErr(PrintStream err) {
    PrintStream original = io.err;
    io.err = err;
    return original;
  }

  /**
   * Whether or not streams support ansi codes
   */
  public static void setAnsi(boolean ansi) {
    io.ansi = ansi;
  }

  /**
   * Print an info message.
   */
  public static void info(@NotNull String format, Object... args) {
    io.infoImpl(format, args);
  }

  /**
   * Print an info message.
   */
  public static void infogreen(@NotNull String format, Object... args) {
    io.infogreenImpl(format, args);
  }
  /**
   * Print an info message with a line-feed.
   */
  public static void infoln(Object format, Object... args) {
    io.infoImpl(format + "\n", args);
  }

  /**
   * Print an info message with a line-feed.
   */
  public static void errorln(String format, Object... args) {
    io.errorImpl(format, args);
  }

  private void infoImpl(@NotNull String format, Object... args) {
    if (ansi) {
      out.print(ansi().a(INTENSITY_FAINT).fg(WHITE).a(safeFormat(format, args)).reset());
    } else {
      out.print(safeFormat(format, args));
    }
  }

  private void errorImpl(String format, Object... args) {
    // Create a code that is fairly unique for this error message
    String code;
    try {
      code = HashUtils.getSHA256OfString(format).substring(0, 7);
    } catch (NoSuchAlgorithmException e) {
      code = "no-algo";
    } catch (IOException e) {
      code = "io-failed";
    }
    String prefix = String.format("FAILURE (%s): ", code);
    if (ansi) {
      err.print(ansi().fg(RED).a(prefix).a(safeFormat(format, args)).a("\n").reset());
    } else {
      err.printf(prefix);
      err.print(safeFormat(format, args));
      err.printf("\n");
    }
  }

  private void infogreenImpl(@NotNull String format, Object... args) {
    if (ansi) {
      out.print(ansi().a(INTENSITY_FAINT).fg(GREEN).a(safeFormat(format, args)).reset());
    } else {
      out.print(safeFormat(format, args));
    }
  }
}
