package org.fest.eclipse.assertions.generator.internal.util;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.eclipse.assertions.generator.internal.util.ClassUtil.isValidGetterName;

import org.junit.Test;

public class ClassUtilTest {

  @Test
  public void should_validate_standard_getter_name() throws Exception {
    assertThat(isValidGetterName("getTeam")).isTrue();
  }

  @Test
  public void should_validate_boolean_getter_name() throws Exception {
    assertThat(isValidGetterName("isRookie")).isTrue();
  }

  @Test
  public void should_invalidate_non_getter_names() throws Exception {
    assertThat(isValidGetterName("someMethod")).isFalse();
    assertThat(isValidGetterName("getteam")).isFalse();
    assertThat(isValidGetterName("isrookie")).isFalse();
  }
}
