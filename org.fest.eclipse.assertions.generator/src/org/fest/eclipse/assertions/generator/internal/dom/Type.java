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
package org.fest.eclipse.assertions.generator.internal.dom;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ITypeBinding;

public class Type {

  private static final String JAVA_LANG = "java.lang";

  private final ITypeBinding binding;
  private final ProjectBindings bindings;
  private final List<Type> parameters;

  Type(ITypeBinding binding, ProjectBindings bindings) {
    this.binding = binding;
    this.bindings = bindings;

    parameters = new ArrayList<Type>();

    for (ITypeBinding p : binding.getTypeArguments()) {
      parameters.add(new Type(p, bindings));
    }
  }

  public Type getFirstParameter() {
    return parameters.get(0);
  }

  public List<Type> getParameters() {
    return parameters;
  }

  public String getUnparameterizedQualifiedName() {
    String name = binding.getQualifiedName();

    int sbIdx = name.indexOf('<');
    if (sbIdx == -1) {
      return name;
    }
    return name.substring(0, sbIdx);
  }

  public boolean isArray() {
    return binding.isArray();
  }

  public boolean isIterable() {
    return bindings.isIterable(binding, false);
  }

  public boolean isParameterized() {
    return !parameters.isEmpty();
  }

  public Type getComponentType() {
    return new Type(binding.getComponentType(), bindings);
  }

  public boolean isPrimitive() {
    return binding.isPrimitive();
  }

  public boolean belongsToJavaLang() {
    return binding.getPackage().getName().equals(JAVA_LANG);
  }
}