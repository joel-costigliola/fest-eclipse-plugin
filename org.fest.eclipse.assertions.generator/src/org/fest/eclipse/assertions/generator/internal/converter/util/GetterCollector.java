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

import static org.fest.assertions.generator.util.ClassUtil.isValidGetterName;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import org.fest.eclipse.assertions.generator.internal.converter.Getter;
import org.fest.eclipse.assertions.generator.internal.converter.Type;

/**
 * 
 * Understands how to build {@link Getter}s from an {@link IType}.
 * 
 */
public class GetterCollector extends ASTVisitor {

  private final ProjectBindings projectBindings;
  private final List<Getter> collectedGetters;

  public GetterCollector(IType type) {
    projectBindings = new ProjectBindings(type.getJavaProject());
    collectedGetters = new ArrayList<Getter>();
  }

  @Override
  public void endVisit(MethodDeclaration method) {
    if (!isGetter(method)) {
      return;
    }

    ITypeBinding returnTypeBinding = method.getReturnType2().resolveBinding();
    String getterName = method.getName().toString();
    Type getterReturnType = new Type(returnTypeBinding, projectBindings);
    collectedGetters.add(new Getter(getterName, getterReturnType));
  }

  public List<Getter> collectedGetters() {
    return collectedGetters;
  }

  public static List<Getter> gettersOf(IType type) {
    CompilationUnit cu = parse(type.getCompilationUnit());
    GetterCollector collector = new GetterCollector(type);
    cu.accept(collector);
    return collector.collectedGetters();
  }

  private static CompilationUnit parse(ICompilationUnit cu) {
    return (CompilationUnit) AstParserFactory.astParserFor(cu).createAST(null);
  }

  private static boolean isGetter(MethodDeclaration method) {
    return method.getReturnType2() != null && method.parameters().isEmpty() && isValidGetterName(method.getName().toString());
  }

}