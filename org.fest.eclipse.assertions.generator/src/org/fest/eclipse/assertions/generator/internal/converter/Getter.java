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

import static org.apache.commons.lang3.StringUtils.uncapitalize;

import static org.fest.assertions.generator.util.ClassUtil.GET_PREFIX;
import static org.fest.assertions.generator.util.ClassUtil.IS_PREFIX;

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
   * isMostValuablePlayer -> mostValuablePlayer
   * </pre>
   */
  public String getPropertyName() {
    String property = name.startsWith(IS_PREFIX) ? name.substring(IS_PREFIX.length()) : name.substring(GET_PREFIX.length());
    return uncapitalize(property);
  }
}