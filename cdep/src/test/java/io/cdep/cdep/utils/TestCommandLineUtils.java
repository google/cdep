package io.cdep.cdep.utils;

import static com.google.common.truth.Truth.assertThat;

import java.io.File;
import org.junit.Test;

public class TestCommandLineUtils {
  @Test
  public void testBasic() {
    assertThat(CommandLineUtils.getLibraryNameFromLibraryFilename(
        new File("/path/to/libmyfile.a"))).isEqualTo("myfile");

  }
}