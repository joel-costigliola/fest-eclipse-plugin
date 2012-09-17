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

import org.eclipse.jdt.core.JavaModelException;
import org.fest.assertions.generator.util.ClassUtil;

public class Getter {

  private final String name;
  private final Type returnType;

  public Getter(String name, Type returnType) {
    this.name = name;
    this.returnType = returnType;
  }

  public String getName() {
    return name;
  }

  public Type getReturnType() {
    return returnType;
  }

  /**
   * Returns the property name of given getter method, examples :
   * 
   * <pre>
   * getName -> name
   * </pre>
   * 
   * <pre>
   * isMostValuablePlayer -> mostValuablePlayer
   * </pre>
   * 
   * @throws JavaModelException
   */
  public String getPropertyName() throws JavaModelException {
    String property = name.startsWith(ClassUtil.IS_PREFIX) ? name.substring(ClassUtil.IS_PREFIX.length()) : name.substring(ClassUtil.GET_PREFIX.length());
    return uncapitalizeFirstChar(property);
  }

  private String uncapitalizeFirstChar(String property) {
    String firstChar = property.substring(0, 1).toLowerCase();
    return property.length() == 1 ? firstChar : firstChar + property.substring(1);
  }
}