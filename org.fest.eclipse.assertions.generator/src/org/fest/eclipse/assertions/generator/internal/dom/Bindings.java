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

import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

/**
 * Utility methods pertaining to {@link IBinding}s.
 */
public class Bindings {

  protected Bindings() {
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
   * @param considerTypeArguments
   *          if <code>true</code>, consider type arguments of <code>type</code>
   * @return <code>true</code> iff <code>possibleSuperType</code> is a super type of <code>type</code> or is equal to it
   */
  public static boolean isSuperType(ITypeBinding possibleSuperType, ITypeBinding type, boolean considerTypeArguments) {
    if (type.isArray() || type.isPrimitive()) {
      return false;
    }
    if (!considerTypeArguments) {
      type = type.getTypeDeclaration();
    }
    if (type.isEqualTo(possibleSuperType)) {
      return true;
    }
    ITypeBinding superClass = type.getSuperclass();
    if (superClass != null) {
      if (isSuperType(possibleSuperType, superClass, considerTypeArguments)) {
        return true;
      }
    }

    if (possibleSuperType.isInterface()) {
      ITypeBinding[] superInterfaces = type.getInterfaces();
      for (int i = 0; i < superInterfaces.length; i++) {
        if (isSuperType(possibleSuperType, superInterfaces[i], considerTypeArguments)) {
          return true;
        }
      }
    }
    return false;
  }
}
