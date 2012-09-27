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
package org.fest.eclipse.assertions.generator.internal.converter;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ITypeBinding;

import org.fest.eclipse.assertions.generator.internal.converter.util.ProjectBindings;

public class Type {

  private static final String JAVA_LANG = "java.lang";

  private final ITypeBinding typeBinding;
  private final ProjectBindings projectBindings;
  private final List<Type> parameters;

  public Type(ITypeBinding iTypeBinding, ProjectBindings projectBindings) {
    this.typeBinding = iTypeBinding;
    this.projectBindings = projectBindings;

    this.parameters = new ArrayList<Type>();
    for (ITypeBinding typeArgument : iTypeBinding.getTypeArguments()) {
      this.parameters.add(new Type(typeArgument, projectBindings));
    }
  }

  public Type getFirstParameter() {
    return parameters.get(0);
  }

  public List<Type> getParameters() {
    return parameters;
  }

  public String getUnparameterizedQualifiedName() {
    String name = typeBinding.getQualifiedName();

    int sbIdx = name.indexOf('<');
    if (sbIdx == -1) {
      return name;
    }
    return name.substring(0, sbIdx);
  }

  public boolean isArray() {
    return typeBinding.isArray();
  }

  public boolean isIterable() {
    return projectBindings.isIterable(typeBinding);
  }

  public boolean isParameterized() {
    return !parameters.isEmpty();
  }

  public Type getComponentType() {
    return new Type(typeBinding.getComponentType(), projectBindings);
  }

  public boolean isPrimitive() {
    return typeBinding.isPrimitive();
  }

  public boolean belongsToJavaLang() {
    return typeBinding.getPackage().getName().equals(JAVA_LANG);
  }
}