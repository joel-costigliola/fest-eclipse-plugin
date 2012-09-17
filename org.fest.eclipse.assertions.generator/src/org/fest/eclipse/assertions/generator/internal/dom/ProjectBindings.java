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

import org.eclipse.jdt.core.BindingKey;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

/**
 * Utility methods pertaining to {@link IBinding}s, contextually to a given project.
 */
public class ProjectBindings {

  private final IJavaProject project;
  private final AstParserFactory parserFactory;
  private ITypeBinding iterableBinding;

  public ProjectBindings(IJavaProject project) {
    this.project = project;
    parserFactory = new AstParserFactory();
  }

  public ITypeBinding getTypeBinding(String typeName) {
    final ASTParser parser = parserFactory.parserFor(project);

    String[] keys = new String[] { BindingKey.createTypeBindingKey(typeName) };
    final TypeBindingRequestor requestor = new TypeBindingRequestor();
    parser.createASTs(new ICompilationUnit[] {}, keys, requestor, null);
    return requestor.getResult();
  }

  public boolean isIterable(ITypeBinding binding, boolean considerTypeArguments) {
    return Bindings.isSuperType(getIterableBinding(), binding, considerTypeArguments);
  }

  private ITypeBinding getIterableBinding() {
    if (iterableBinding == null) {
      iterableBinding = getTypeBinding("java.lang.Iterable");
    }
    return iterableBinding;
  }
}
