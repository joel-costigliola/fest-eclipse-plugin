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

import static org.fest.assertions.generator.util.ClassUtil.isValidGetterName;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class GetterCollector extends ASTVisitor {

  private final ProjectBindings bindings;
  private final List<Getter> result;

  public GetterCollector(IType type) {
    bindings = new ProjectBindings(type.getJavaProject());
    result = new ArrayList<Getter>();
  }

  @Override
  public void endVisit(MethodDeclaration method) {
    if (!isGetter(method)) {
      return;
    }

    ITypeBinding returnTypeBinding = method.getReturnType2().resolveBinding();

    String name = method.getName().toString();
    Type returnType = new Type(returnTypeBinding, bindings);
    result.add(new Getter(name, returnType));
  }

  private boolean isGetter(MethodDeclaration m) {
    return m.getReturnType2() != null && m.parameters().isEmpty() && isValidGetterName(m.getName().toString());
  }

  public List<Getter> getResult() {
    return result;
  }
}