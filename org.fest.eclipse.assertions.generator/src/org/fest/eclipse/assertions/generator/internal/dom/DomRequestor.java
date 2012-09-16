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

import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class DomRequestor {

  private final AstParserFactory parserFactory;

  public DomRequestor() {
    parserFactory = new AstParserFactory();
  }

  public List<Getter> gettersOf(IType type) {
    CompilationUnit cu = parse(type.getCompilationUnit());
    GetterCollector collector = new GetterCollector(type);
    cu.accept(collector);
    return collector.getResult();
  }

  private CompilationUnit parse(ICompilationUnit cu) {
    return (CompilationUnit) parserFactory.parserFor(cu).createAST(null);
  }
}
