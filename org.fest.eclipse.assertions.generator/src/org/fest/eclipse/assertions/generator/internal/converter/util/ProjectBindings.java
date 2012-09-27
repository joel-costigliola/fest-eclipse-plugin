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
package org.fest.eclipse.assertions.generator.internal.converter.util;

import org.eclipse.jdt.core.BindingKey;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ITypeBinding;

/**
 * Utility methods pertaining to {@link ITypeBinding}s, contextually to a given project.
 */
public class ProjectBindings {

  private final IJavaProject project;
  private ITypeBinding iterableTypeBinding;

  public ProjectBindings(IJavaProject project) {
    this.project = project;
    this.iterableTypeBinding = getTypeBinding("java.lang.Iterable");
  }

  public boolean isIterable(ITypeBinding binding) {
    return isSuperType(iterableTypeBinding, binding);
  }

  private ITypeBinding getTypeBinding(String typeName) {
    final ASTParser projectParser = AstParserFactory.astParserFor(project);

    String[] keys = new String[] { BindingKey.createTypeBindingKey(typeName) };
    final ITypeBindingRequestor requestor = new ITypeBindingRequestor();
    projectParser.createASTs(new ICompilationUnit[] {}, keys, requestor, null);
    return requestor.getITypeBinding();
  }

  /**
   * Returns <code>true</code> if the given type is a super type of a candidate. <code>true</code> is returned if the two type bindings are identical.
   * 
   * <p>
   * Note: shamelessly copied from org.eclipse.jdt.internal.corext.dom.Bindings
   * </p>
   * 
   * @param possibleSuperType
   *          the type to inspect
   * @param type
   *          the type whose super types are looked at
   * @return <code>true</code> iff <code>possibleSuperType</code> is a super type of <code>type</code> or is equal to it
   */
  private static boolean isSuperType(ITypeBinding possibleSuperType, ITypeBinding type) {
    if (type.isArray() || type.isPrimitive()) {
      return false;
    }
    ITypeBinding typeDeclaration = type.getTypeDeclaration();
    if (typeDeclaration.isEqualTo(possibleSuperType)) {
      return true;
    }
    ITypeBinding superClass = typeDeclaration.getSuperclass();
    if (superClass != null && isSuperType(possibleSuperType, superClass)) {
      return true;
    }

    if (possibleSuperType.isInterface()) {
      ITypeBinding[] superInterfaces = typeDeclaration.getInterfaces();
      for (int i = 0; i < superInterfaces.length; i++) {
        if (isSuperType(possibleSuperType, superInterfaces[i])) {
          return true;
        }
      }
    }
    return false;
  }
}
