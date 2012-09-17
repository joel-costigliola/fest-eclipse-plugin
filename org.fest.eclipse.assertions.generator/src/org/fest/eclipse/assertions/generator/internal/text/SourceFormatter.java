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
package org.fest.eclipse.assertions.generator.internal.text;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.text.edits.TextEdit;
import org.fest.eclipse.assertions.generator.internal.log.Logger;

public class SourceFormatter {

  private Logger logger;

  public SourceFormatter(Logger logger) {
    this.logger = logger;
  }

  public void format(ICompilationUnit cu) {
    IProgressMonitor pm = new NullProgressMonitor();

    try {
      ICompilationUnit wc = createWorkingCopy(cu, pm);
      wc.getBuffer().setContents(getFormattedSource(wc));
      wc.commitWorkingCopy(false, pm);
    } catch (JavaModelException e) {
      logger.warn("Could not format " + cu.getElementName(), e);
    } catch (BadLocationException e) {
      logger.warn("Could not format " + cu.getElementName(), e);
    }
  }

  private ICompilationUnit createWorkingCopy(ICompilationUnit compilationUnit, IProgressMonitor pm) throws JavaModelException
  {
    if (!compilationUnit.isOpen()) {
      compilationUnit.open(pm);
    }
    return compilationUnit.getWorkingCopy(pm);
  }

  private String getFormattedSource(ICompilationUnit compilationUnit) throws BadLocationException, JavaModelException {
    CodeFormatter formatter = ToolFactory.createCodeFormatter(compilationUnit.getJavaProject().getOptions(true));

    IDocument document = new Document(compilationUnit.getSource());
    String lineDelimiter = TextUtilities.getDefaultLineDelimiter(document);
    TextEdit edit = formatter.format(CodeFormatter.K_COMPILATION_UNIT, document.get(), 0, document.getLength(), 0, lineDelimiter);

    if (edit != null) {
      edit.apply(document);
    }

    return document.get();
  }
}
