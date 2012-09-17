/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 * Copyright @2012 the original author or authors.
 */
package org.fest.eclipse.assertions.generator.internal.generation;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.fest.assertions.generator.description.ClassDescription;
import org.fest.assertions.generator.description.GetterDescription;
import org.fest.assertions.generator.description.TypeDescription;
import org.fest.assertions.generator.description.TypeName;
import org.fest.assertions.generator.description.converter.ClassDescriptionConverter;
import org.fest.eclipse.assertions.generator.internal.dom.DomRequestor;
import org.fest.eclipse.assertions.generator.internal.dom.Getter;
import org.fest.eclipse.assertions.generator.internal.dom.Type;
import org.fest.eclipse.assertions.generator.internal.log.Logger;

// TODO extract logic common to ClassToClassDescriptionConverter and TypeToClassDescriptionConverter
public class TypeToClassDescriptionConverter implements ClassDescriptionConverter<IType> {

  private final Logger logger;
  private final DomRequestor domRequestor;

  public TypeToClassDescriptionConverter(Logger logger) {
    this.logger = logger;
    domRequestor = new DomRequestor();
  }

  public ClassDescription convertToClassDescription(IType type) {
    List<Getter> getters = domRequestor.gettersOf(type);

    ClassDescription classDescription = new ClassDescription(new TypeName(type.getElementName(), type.getPackageFragment().getElementName()));
    classDescription.addGetterDescriptions(createGetterDescriptions(getters));
    classDescription.addTypeToImport(getNeededImportsFor(getters));
    return classDescription;
  }

  private Set<GetterDescription> createGetterDescriptions(List<Getter> getters) {
    Set<GetterDescription> getterDescriptions = new TreeSet<GetterDescription>();

    for (Getter getter : getters) {
      try {
        getterDescriptions.add(createGetterDescription(getter));
      } catch (JavaModelException e) {
        logger.error("Could not create description for getter " + getter, e);
      }
    }
    return getterDescriptions;
  }

  private GetterDescription createGetterDescription(Getter getter) throws JavaModelException {
    Type propertyType = getter.getReturnType();
    TypeDescription typeDescription = new TypeDescription(new TypeName(propertyType.getUnparameterizedQualifiedName()));

    if (propertyType.isParameterized()) {
      typeDescription.setGeneric(true);
      typeDescription.setElementTypeName(new TypeName(propertyType.getFirstParameter().getUnparameterizedQualifiedName()));
    }

    if (propertyType.isArray()) {
      typeDescription.setElementTypeName(new TypeName(propertyType.getComponentType().getUnparameterizedQualifiedName()));
      typeDescription.setArray(true);
    } else if (propertyType.isIterable()) {
      typeDescription.setIterable(true);
      if (typeDescription.getElementTypeName() == null) {
        typeDescription.setElementTypeName(new TypeName("java.lang.Object"));
      }
    }

    // TODO what if there is several parameter types ?
    return new GetterDescription(getter.getPropertyName(), typeDescription);
  }

  private Set<TypeName> getNeededImportsFor(List<Getter> getters) {
    // imports as String
    Set<TypeName> imports = new TreeSet<TypeName>();

    for (Getter getter : getters) {
      try {
        addImportsForGetter(getter, imports);
      } catch (JavaModelException e) {
        logger.error("Could not add imports for getter " + getter, e);
      }
    }
    return imports;
  }

  private void addImportsForGetter(Getter getter, Set<TypeName> imports) throws JavaModelException {
    Type propertyType = getter.getReturnType();

    if (propertyType.isArray()) {
      // we only need the component type, that is T in T[] array
      addImportsFor(propertyType.getComponentType(), imports);
    } else if (propertyType.isIterable()) {
      // we need the Iterable parameter type, that is T in Iterable<T>
      // we don't need to import the Iterable since it does not appear directly in generated code, ex :
      // assertThat(actual.getTeamMates()).contains(teamMates); // teamMates -> List
      addImportsForTypeParameters(propertyType, imports);
    } else {
      addImportsFor(propertyType, imports);
    }
  }

  private void addImportsFor(Type type, Collection<TypeName> imports) {
    if (!(type.isPrimitive() || type.belongsToJavaLang())) {
      imports.add(new TypeName(type.getUnparameterizedQualifiedName()));
    }
    addImportsForTypeParameters(type, imports);
  }

  private void addImportsForTypeParameters(Type type, Collection<TypeName> imports) {
    for (Type param : type.getParameters()) {
      addImportsFor(param, imports);
    }
  }
}
