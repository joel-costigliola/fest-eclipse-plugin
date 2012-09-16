package org.fest.eclipse.assertions.generator.internal.generation;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.extractProperty;
import static org.fest.assertions.api.Assertions.failBecauseExceptionWasNotThrown;

import org.eclipse.jdt.core.IType;
import org.fest.assertions.generator.description.ClassDescription;
import org.fest.assertions.generator.description.GetterDescription;
import org.fest.assertions.generator.description.TypeName;
import org.fest.eclipse.assertions.generator.internal.log.Logger;
import org.fest.eclipse.test.SimpleProjectTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TypeToClassDescriptionConverterTest extends SimpleProjectTestCase {

  @Mock
  Logger logger;
  @InjectMocks
  TypeToClassDescriptionConverter converter;

  @Test
  public void should_describe_class() throws Exception {
    // given
    IType type = createClassFromSource(""
        + "package pack.age;"
        + "public class Thing {"
        + "  public String getName() {"
        + "    return null;"
        + "  }"
        + "}");

    // when
    ClassDescription classDesc = converter.convertToClassDescription(type);

    // then
    assertThat(classDesc.getClassName()).isEqualTo("Thing");
    assertThat(classDesc.getPackageName()).isEqualTo("pack.age");
  }

  @Test
  public void should_describe_primitive_getter() throws Exception {
    // given
    IType type = createClassFromSource(""
        + "package pack.age;"
        + "public class Thing1 {"
        + "  public short getShortValue() {"
        + "    return 2;"
        + "  }"
        + "}");

    // when
    ClassDescription classDesc = converter.convertToClassDescription(type);

    // then
    assertThat(classDesc.getGetters()).hasSize(1);

    GetterDescription getterDesc = classDesc.getGetters().iterator().next();
    assertThat(getterDesc.isArrayPropertyType()).isFalse();
    assertThat(getterDesc.isBooleanPropertyType()).isFalse();
    assertThat(getterDesc.isIterablePropertyType()).isFalse();
    assertThat(getterDesc.isPrimitivePropertyType()).isTrue();
    assertThat(getterDesc.getPropertyName()).isEqualTo("shortValue");
    assertThat(getterDesc.getPropertyTypeName()).isEqualTo("short");
    assertThatElementTypeNameIsNull(getterDesc);

    assertThat(classDesc.getImports()).isEmpty();
  }

  @Test
  public void should_describe_boolean_getter() throws Exception {
    // given
    IType type = createClassFromSource(""
        + "package pack.age;"
        + "public class Thing2 {"
        + "  public boolean isBoolProp() {"
        + "    return false;"
        + "  }"
        + "}");

    // when
    ClassDescription classDesc = converter.convertToClassDescription(type);

    // then
    assertThat(classDesc.getGetters()).hasSize(1);

    GetterDescription getterDesc = classDesc.getGetters().iterator().next();
    assertThat(getterDesc.isArrayPropertyType()).isFalse();
    assertThat(getterDesc.isBooleanPropertyType()).isTrue();
    assertThat(getterDesc.isIterablePropertyType()).isFalse();
    assertThat(getterDesc.isPrimitivePropertyType()).isTrue();
    assertThat(getterDesc.getPropertyName()).isEqualTo("boolProp");
    assertThat(getterDesc.getPropertyTypeName()).isEqualTo("boolean");
    assertThatElementTypeNameIsNull(getterDesc);

    assertThat(classDesc.getImports()).isEmpty();
  }

  @Test
  public void should_describe_object_getter() throws Exception {
    // given
    IType type = createClassFromSource(""
        + "package pack.age;"
        + "import java.util.Date;"
        + "public class Thing3 {"
        + "  public Date getSomeDate() {"
        + "    return null;"
        + "  }"
        + "}");

    // when
    ClassDescription classDesc = converter.convertToClassDescription(type);

    // then
    assertThat(classDesc.getGetters()).hasSize(1);

    GetterDescription getterDesc = classDesc.getGetters().iterator().next();
    assertThat(getterDesc.isArrayPropertyType()).isFalse();
    assertThat(getterDesc.isBooleanPropertyType()).isFalse();
    assertThat(getterDesc.isIterablePropertyType()).isFalse();
    assertThat(getterDesc.isPrimitivePropertyType()).isFalse();
    assertThat(getterDesc.getPropertyName()).isEqualTo("someDate");
    assertThat(getterDesc.getPropertyTypeName()).isEqualTo("Date");
    assertThatElementTypeNameIsNull(getterDesc);

    assertThat(classDesc.getImports()).containsOnly(new TypeName("java.util.Date"));
  }

  @Test
  public void should_describe_array_getter() throws Exception {
    // given
    IType type = createClassFromSource(""
        + "package pack.age;"
        + "import java.math.BigDecimal;"
        + "public class Thing4 {"
        + "  public BigDecimal[] getDecimals() {"
        + "    return null;"
        + "  }"
        + "}");

    // when
    ClassDescription classDesc = converter.convertToClassDescription(type);

    // then
    assertThat(classDesc.getGetters()).hasSize(1);

    GetterDescription getterDesc = classDesc.getGetters().iterator().next();
    assertThat(getterDesc.isArrayPropertyType()).isTrue();
    assertThat(getterDesc.isBooleanPropertyType()).isFalse();
    assertThat(getterDesc.isIterablePropertyType()).isFalse();
    assertThat(getterDesc.isPrimitivePropertyType()).isFalse();
    assertThat(getterDesc.getPropertyName()).isEqualTo("decimals");
    assertThat(getterDesc.getPropertyTypeName()).isEqualTo("BigDecimal[]");
    assertThat(getterDesc.getElementTypeName()).isEqualTo("BigDecimal");

    assertThat(classDesc.getImports()).containsOnly(new TypeName("java.math.BigDecimal"));
  }

  @Test
  public void should_describe_generic_getter() throws Exception {
    // given
    IType type = createClassFromSource(""
        + "package pack.age;"
        + "import java.util.Comparator;"
        + "import java.util.Date;"
        + "public class Thing5 {"
        + "  public Comparator<Date> getDateComparator() {"
        + "    return null;"
        + "  }"
        + "}");

    // when
    ClassDescription classDesc = converter.convertToClassDescription(type);

    // then
    assertThat(classDesc.getGetters()).hasSize(1);

    GetterDescription getterDesc = classDesc.getGetters().iterator().next();
    assertThat(getterDesc.isArrayPropertyType()).isFalse();
    assertThat(getterDesc.isBooleanPropertyType()).isFalse();
    assertThat(getterDesc.isIterablePropertyType()).isFalse();
    assertThat(getterDesc.isPrimitivePropertyType()).isFalse();
    assertThat(getterDesc.getPropertyName()).isEqualTo("dateComparator");
    assertThat(getterDesc.getPropertyTypeName()).isEqualTo("Comparator");
    assertThat(getterDesc.getElementTypeName()).isEqualTo("Date");

    assertThat(classDesc.getImports()).containsOnly(new TypeName("java.util.Comparator"), new TypeName("java.util.Date"));
  }

  @Test
  public void should_describe_iterable_getter() throws Exception {
    // given
    IType type = createClassFromSource(""
        + "package pack.age;"
        + "import java.util.Date;"
        + "import java.util.List;"
        + "public class Thing6 {"
        + "  public List<Date> getDates() {"
        + "    return null;"
        + "  }"
        + "}");

    // when
    ClassDescription classDesc = converter.convertToClassDescription(type);

    // then
    assertThat(classDesc.getGetters()).hasSize(1);

    GetterDescription getterDesc = classDesc.getGetters().iterator().next();
    assertThat(getterDesc.isArrayPropertyType()).isFalse();
    assertThat(getterDesc.isBooleanPropertyType()).isFalse();
    assertThat(getterDesc.isIterablePropertyType()).isTrue();
    assertThat(getterDesc.isPrimitivePropertyType()).isFalse();
    assertThat(getterDesc.getPropertyName()).isEqualTo("dates");
    assertThat(getterDesc.getPropertyTypeName()).isEqualTo("List");
    assertThat(getterDesc.getElementTypeName()).isEqualTo("Date");

    assertThat(classDesc.getImports()).containsOnly(new TypeName("java.util.Date"));
  }

  @Test
  public void should_describe_unparameterized_iterable_getter() throws Exception {
    // given
    IType type = createClassFromSource(""
        + "package pack.age;"
        + "import java.util.Set;"
        + "public class Thing7 {"
        + "  public Set getFoos() {"
        + "    return null;"
        + "  }"
        + "}");

    // when
    ClassDescription classDesc = converter.convertToClassDescription(type);

    // then
    assertThat(classDesc.getGetters()).hasSize(1);

    GetterDescription getterDesc = classDesc.getGetters().iterator().next();
    assertThat(getterDesc.isArrayPropertyType()).isFalse();
    assertThat(getterDesc.isBooleanPropertyType()).isFalse();
    assertThat(getterDesc.isIterablePropertyType()).isTrue();
    assertThat(getterDesc.isPrimitivePropertyType()).isFalse();
    assertThat(getterDesc.getPropertyName()).isEqualTo("foos");
    assertThat(getterDesc.getPropertyTypeName()).isEqualTo("Set");
    assertThat(getterDesc.getElementTypeName()).isEqualTo("Object");

    assertThat(classDesc.getImports()).isEmpty();
  }

  @Test
  public void should_describe_several_getters() throws Exception {
    // given
    IType type = createClassFromSource(""
        + "package pack.age;"
        + "import java.util.Date;"
        + "public class Thing8 {"
        + "  public Date getSomeDate() {"
        + "    return null;"
        + "  }"
        + "  public short getShortValue() {"
        + "    return 2;"
        + "  }"
        + "}");

    // when
    ClassDescription classDesc = converter.convertToClassDescription(type);

    // then
    assertThat(classDesc.getGetters()).hasSize(2);
    assertThat(extractProperty("propertyName").from(classDesc.getGetters())).contains("someDate", "shortValue");
  }

  /**
   * Calling {@code assertThat(getterDesc.getElementTypeName()).isNull()} throws a NPE, so...
   */
  private void assertThatElementTypeNameIsNull(GetterDescription getterDesc) {
    try {
      getterDesc.getElementTypeName();
      failBecauseExceptionWasNotThrown(NullPointerException.class);
    } catch (NullPointerException e) {
      // success
    }
  }
}
