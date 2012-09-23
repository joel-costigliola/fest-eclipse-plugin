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
package org.fest.eclipse.assertions.generator.internal.commands;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.IDE;
import org.fest.eclipse.assertions.generator.internal.AssertionGeneratorPlugin;
import org.fest.eclipse.assertions.generator.internal.generation.EclipseAssertionGenerator;
import org.fest.eclipse.assertions.generator.internal.log.Logger;
import org.fest.eclipse.assertions.generator.internal.text.SourceFormatter;

public class GenerateAssertionsHandler extends AbstractHandler {

  private final Logger logger;
  private final TypeSelectionProvider selectionProvider;
  private final EclipseAssertionGenerator generator;
  private final SourceFormatter formatter;

  public GenerateAssertionsHandler() throws IOException {
    logger = AssertionGeneratorPlugin.get().getLogger();
    selectionProvider = new TypeSelectionProvider(logger);
    generator = new EclipseAssertionGenerator();
    formatter = new SourceFormatter(logger);
  }

  public Object execute(ExecutionEvent event) throws ExecutionException {
    IType type = selectionProvider.getSelectedType(event);
    if (type == null) {
      return null;
    }

    File assertionFile = generator.generateAssertionsFor(type);
    if (assertionFile == null) {
      return null;
    }

    IFile iFile = toIFile(assertionFile);

    // TODO organize imports, see http://stackoverflow.com/questions/2764428/calling-organize-imports-programmatically

    format(iFile);

    openEditor(iFile, event);

    return null;
  }

  private IFile toIFile(File file) {
    Path path = new Path(file.getAbsolutePath());
    return ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path);
  }

  private void format(IFile file) {
    formatter.format(JavaCore.createCompilationUnitFrom(file));
  }

  private void openEditor(IFile file, ExecutionEvent event) {
    if (logger.debugEnabled()) {
      logger.debug("Opening editor for file " + file);
    }

    IWorkbenchPage activePage = getActivePage(event);
    if (activePage == null) {
      return;
    }

    try {
      IDE.openEditor(activePage, file, true);
    } catch (PartInitException e) {
      logger.error("Could not open editor for file " + file);
    }
  }

  private static IWorkbenchPage getActivePage(ExecutionEvent event) {
    IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
    return window == null ? null : window.getActivePage();
  }
}
