package io.cdep.cdep.utils;

import static io.cdep.cdep.utils.Invariant.failIf;

import io.cdep.annotations.NotNull;
import java.io.File;

public class CommandLineUtils {
  /**
   * Given a file like path/to/file/libmylib.a return mylib
   */
  @NotNull
  public static String getLibraryNameFromLibraryFilename(@NotNull File library) {
    String name = library.getName();
    if (failIf(!name.startsWith("lib"), "Library name from manifest wasn't in the form libXXXX.a")) {
      return name;
    }
    if (failIf(!name.endsWith(".a"), "Library name from manifest wasn't in the form libXXXX.a")) {
      return name;
    }
    name = name.substring(3, name.length() - 2);
    return name;
  }
}
