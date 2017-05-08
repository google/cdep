package io.cdep.cdep.utils;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class ObjectUtilsTest {
  @Test
  public void nullToDefault() throws Exception {
    assertThat(ObjectUtils.nullToDefault(null, "bob")).isEqualTo("bob");
    assertThat(ObjectUtils.nullToDefault("tom", "bob")).isEqualTo("tom");
  }
}