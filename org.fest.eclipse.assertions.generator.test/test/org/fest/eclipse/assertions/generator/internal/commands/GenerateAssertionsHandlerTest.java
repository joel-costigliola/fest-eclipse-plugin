package org.fest.eclipse.assertions.generator.internal.commands;

import static org.fest.assertions.api.Assertions.assertThat;

import org.eclipse.jdt.core.IType;
import org.fest.eclipse.test.JavaProjectOptions;
import org.fest.eclipse.test.SimpleProjectTestCase;
import org.junit.Test;

import com.google.common.base.Strings;

public class GenerateAssertionsHandlerTest extends SimpleProjectTestCase {

  private static final String GENERATE_ASSERTIONS = "org.fest.eclipse.assertions.generator.commands.generateAssertions";
  private static final String NL = System.getProperty("line.separator");

  private static final String thingSourceCode = "package pack.age;"
      + "public class Thing {"
      + "  public String getName() {"
      + "    return null;"
      + "  }"
      + "}";

  @Test
  public void should_generate_assertion_type_for_type_open_in_editor() throws Exception {
    // given
    IType type = createClassFromSource(thingSourceCode);

    // when
    openEditor(type);
    executeCommand(GENERATE_ASSERTIONS);

    // then
    assertThat(getCompilationUnitInActiveEditor().getElementName()).isEqualTo("ThingAssert.java");
    assertThat(getCompilationUnitInActiveEditor().getParent().getElementName()).isEqualTo("pack.age");
  }

  @Test
  public void should_apply_user_format() throws Exception {
    // given
    JavaProjectOptions.defaults() //
        .with("org.eclipse.jdt.core.formatter.tabulation.char", "space") //
        .with("org.eclipse.jdt.core.formatter.tabulation.size", "7") //
        .applyTo(project);

    IType type = createClassFromSource(thingSourceCode);

    // when
    openEditor(type);
    executeCommand(GENERATE_ASSERTIONS);

    // then
    String expectedTabulation = Strings.padStart("", 7, ' ');

    assertThat(getCompilationUnitInActiveEditor().findPrimaryType() //
        .getMethod("hasName", new String[] { "QString;" }) //
        .getSource()).endsWith(NL + expectedTabulation + "}");
  }
}
