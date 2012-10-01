package org.fest.eclipse.assertions.util;

import static org.eclipse.ui.PlatformUI.getWorkbench;

import static org.fest.eclipse.assertions.util.StringConstants.EMPTY_STRING;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

// LogHandler;

public class PluginTools
{
  private static final String JAVA_FILES_PATTERN = "**/*.java";
  private static final Pattern MAVEN_RESOURCE_FOLDER = Pattern.compile("src/[^/]+/resources");

  public static IEditorPart getOpenEditorPart()
  {
    IWorkbenchWindow window = getWorkbench().getActiveWorkbenchWindow();
    if (window == null)
      return null;

    IWorkbenchPage page = window.getActivePage();
    return page == null ? null : page.getActiveEditor();
  }

  public static boolean isJavaFile(IWorkbenchPart part) {
    if (!(part instanceof IEditorPart))
      return false;

    IFile file = (IFile) ((IEditorPart) part).getEditorInput().getAdapter(IFile.class);
    return file == null ? false : "java".equals(file.getFileExtension());
  }

  public static IPackageFragmentRoot createPackageFragmentRoot(String projectName, String folderName) {
    IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
    IJavaProject javaProject = JavaCore.create(project);
    try {
      for (IPackageFragmentRoot aSourceFolder : javaProject.getPackageFragmentRoots()) {
        if (folderName.equals(getPathStringWithoutProjectName(aSourceFolder))) {
          return aSourceFolder;
        }
      }
    } catch (JavaModelException e) {
      // LogHandler.getInstance().handleExceptionLog(e);
    }
    return null;
  }

  public static IPackageFragmentRoot getSourceFolder(ICompilationUnit compilationUnit) {
    IJavaElement element = compilationUnit;
    while (!(element instanceof IPackageFragmentRoot)) {
      element = element.getParent();
    }
    return (IPackageFragmentRoot) element;
  }

  public static List<IPackageFragmentRoot> getAllSourceFolderFromProject(IJavaProject javaProject) {
    List<IPackageFragmentRoot> resultList = new ArrayList<IPackageFragmentRoot>();
    try {
      for (IPackageFragmentRoot root : javaProject.getPackageFragmentRoots()) {
        if (!root.isArchive() && root.getRawClasspathEntry().getEntryKind() == IClasspathEntry.CPE_SOURCE) {
          resultList.add(root);
        }
      }
    } catch (JavaModelException e) {
      // TODO LogHandler.getInstance().handleExceptionLog(e);
    }

    return resultList;
  }

  public static List<IPackageFragmentRoot> findJavaSourceFoldersFor(IJavaProject project) {
    List<IPackageFragmentRoot> javaSourceFolders = new ArrayList<IPackageFragmentRoot>();

    for (IPackageFragmentRoot sourceFolder : getAllSourceFolderFromProject(project)) {
      String sourceFolderPath = getPathStringWithoutProjectName(sourceFolder);
      if (!(excludesJavaFiles(sourceFolder) || isMavenLikeResourceFolder(sourceFolderPath))) {
        javaSourceFolders.add(sourceFolder);
      }
    }

    return javaSourceFolders;
  }

  private static boolean excludesJavaFiles(IPackageFragmentRoot srcFolder) {
    try {
      IPath[] exclusionPatterns = srcFolder.getRawClasspathEntry().getExclusionPatterns();
      if (exclusionPatterns == null) {
        return false;
      }

      for (IPath exclusionPattern : exclusionPatterns) {
        if (JAVA_FILES_PATTERN.equals(exclusionPattern.toString())) {
          return true;
        }
      }
    } catch (JavaModelException e) {
      // LogHandler.getInstance().handleExceptionLog(e);
    }
    return false;
  }

  private static boolean isMavenLikeResourceFolder(String srcFolderPath) {
    return MAVEN_RESOURCE_FOLDER.matcher(srcFolderPath).matches();
  }

  public static String getPathStringWithoutProjectName(IPackageFragmentRoot sourceFolder) {
    return sourceFolder == null ? EMPTY_STRING : sourceFolder.getPath().removeFirstSegments(1).toString();
  }

}
