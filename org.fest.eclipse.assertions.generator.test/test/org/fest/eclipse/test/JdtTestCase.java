package org.fest.eclipse.test;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.ide.IDE;

/**
 * Shamelessly copied from org.moreunit.test.workspace.WorkspaceTestCase and org.moreunit.core.commands.TmpProjectTestCase
 */
public abstract class JdtTestCase {

  protected static void build(IJavaProject project) throws CoreException {
    project.getProject().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
  }

  protected static void openEditor(IType type) throws PartInitException, JavaModelException {
    openEditor((IFile) type.getCompilationUnit().getUnderlyingResource().getAdapter(IFile.class));
  }

  protected static void openEditor(IFile sourceFile) throws PartInitException {
    IDE.openEditor(getActivePage(), sourceFile, true);
  }

  protected static IWorkbenchPage getActivePage() {
    return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
  }

  protected static IFile getFileInActiveEditor() {
    IEditorPart editor = getActiveEditor();
    if (editor == null) {
      return null;
    }
    return (IFile) editor.getEditorInput().getAdapter(IFile.class);
  }

  protected static ICompilationUnit getCompilationUnitInActiveEditor() {
    IFile file = getFileInActiveEditor();
    return JavaCore.createCompilationUnitFrom(file);
  }

  protected static IEditorPart getActiveEditor() {
    return getActivePage().getActiveEditor();
  }

  protected static void executeCommand(String commandId) throws ExecutionException, NotDefinedException, NotEnabledException, NotHandledException {
    IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getService(IHandlerService.class);
    handlerService.executeCommand(commandId, null);
  }
}
