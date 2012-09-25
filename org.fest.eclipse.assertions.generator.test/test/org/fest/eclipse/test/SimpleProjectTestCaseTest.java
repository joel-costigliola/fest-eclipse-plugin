package org.fest.eclipse.test;

import static org.fest.assertions.api.Assertions.assertThat;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.Test;

public class SimpleProjectTestCaseTest {

  SimpleProjectTestCaseSpy testCase = new SimpleProjectTestCaseSpy();

  @Test
  public void should_extract_package_name() throws Exception {
    // given
    String sourceCode = "package pack.age;"
        + "public class Thing {"
        + "  public String getName() {"
        + "    return null;"
        + "  }"
        + "}";

    // when
    testCase.createClassFromSource(sourceCode);

    // then
    assertThat(testCase.extractedPackageName).isEqualTo("pack.age");
  }

  @Test
  public void should_extract_class_name() throws Exception {
    // given
    String sourceCode = "package pack.age;"
        + "public class ThingClass {"
        + "  public String getName() {"
        + "    return null;"
        + "  }"
        + "}";

    // when
    testCase.createClassFromSource(sourceCode);

    // then
    assertThat(testCase.extractedClassName).isEqualTo("ThingClass");
  }

  @Test
  public void should_extract_class_name_when_implementing_interface() throws Exception {
    // given
    String sourceCode = "package pack.age;"
        + "public class ThingClass implements Something {"
        + "  public String getName() {"
        + "    return null;"
        + "  }"
        + "}";

    // when
    testCase.createClassFromSource(sourceCode);

    // then
    assertThat(testCase.extractedClassName).isEqualTo("ThingClass");
  }

  @Test
  public void should_extract_class_name_when_extending_other_class() throws Exception {
    // given
    String sourceCode = "package pack.age;"
        + "public class ThingClass extends Something {"
        + "  public String getName() {"
        + "    return null;"
        + "  }"
        + "}";

    // when
    testCase.createClassFromSource(sourceCode);

    // then
    assertThat(testCase.extractedClassName).isEqualTo("ThingClass");
  }

  @Test
  public void should_extract_enum_name() throws Exception {
    // given
    String sourceCode = "package pack.age;"
        + "public enum ThingEnum {"
        + "  public String getName() {"
        + "    return null;"
        + "  }"
        + "}";

    // when
    testCase.createClassFromSource(sourceCode);

    // then
    assertThat(testCase.extractedClassName).isEqualTo("ThingEnum");
  }

  @Test
  public void should_extract_interface_name() throws Exception {
    // given
    String sourceCode = "package pack.age;"
        + "public interface ThingIface {"
        + "  public String getName() {"
        + "    return null;"
        + "  }"
        + "}";

    // when
    testCase.createClassFromSource(sourceCode);

    // then
    assertThat(testCase.extractedClassName).isEqualTo("ThingIface");
  }

  private static class SimpleProjectTestCaseSpy extends SimpleProjectTestCase {
    String extractedPackageName;
    String extractedClassName;

    protected IType createClass(String packageName, String className, String source) throws JavaModelException {
      this.extractedPackageName = packageName;
      this.extractedClassName = className;
      return null;
    }
  }
}
