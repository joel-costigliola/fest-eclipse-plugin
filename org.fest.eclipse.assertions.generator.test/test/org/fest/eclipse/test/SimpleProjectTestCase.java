package org.fest.eclipse.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.After;
import org.junit.Before;

/**
 * Shamelessly copied from org.moreunit.test.workspace.SimpleProjectTestCase and roughly adapted to our needs. It should definitively be refactored, for
 * instance so that it uses nice chained building methods.
 */
public abstract class SimpleProjectTestCase extends JdtTestCase {

  private static final String SOURCES_FOLDER_NAME = "src";
  private static final String TEST_FOLDER_NAME = "test";
  private static final String PACKAGE_NAME = "org";

  private static final Pattern TYPE_PATTERN = Pattern.compile(".*\\s*(?:class|interface|enum)\\s+([\\w]+)(?:\\s*\\{|\\s+extends\\s+|\\s+implements\\s+).*");
  private static final Pattern PACKAGE_PATTERN = Pattern.compile(".*\\s*package\\s+([\\w\\.]+)\\s*;.*");

  protected IJavaProject project;
  protected IPackageFragmentRoot sourcesFolder;
  protected IPackageFragment sourcesPackage;
  protected IPackageFragmentRoot testFolder;
  protected IPackageFragment testPackage;

  @Before
  public void createProject() throws Exception {
    project = WorkspaceHelper.createJavaProject("TestProject");
    sourcesFolder = WorkspaceHelper.createSourceFolderInProject(project, SOURCES_FOLDER_NAME);
    sourcesPackage = WorkspaceHelper.createNewPackageInSourceFolder(sourcesFolder, PACKAGE_NAME);
    testFolder = WorkspaceHelper.createSourceFolderInProject(project, TEST_FOLDER_NAME);
    testPackage = WorkspaceHelper.createNewPackageInSourceFolder(testFolder, PACKAGE_NAME);
  }

  @After
  public void deleteProject() throws Exception {
    WorkspaceHelper.deleteProject(project);
  }

  protected void buildProject() throws CoreException {
    build(project);
  }

  protected IType createClass(String className) throws JavaModelException {
    return createClass(sourcesPackage, className, null);
  }

  protected IType createClassFromSource(String source) throws JavaModelException {
    Matcher packageMatcher = PACKAGE_PATTERN.matcher(source);
    Matcher classMatcher = TYPE_PATTERN.matcher(source);
    if (!packageMatcher.matches() || !classMatcher.matches()) {
      throw new IllegalArgumentException("Source code neither declares package nor class name: " + source);
    }

    String packageName = packageMatcher.group(1);
    String className = classMatcher.group(1);

    return createClass(packageName, className, source);
  }

  protected IType createClass(String packageName, String className, String source) throws JavaModelException {
    IPackageFragment packageFragment = WorkspaceHelper.createNewPackageInSourceFolder(sourcesFolder, packageName);
    return createClass(packageFragment, className, source);
  }

  protected IType createTestCase(String className) throws JavaModelException {
    return createClass(testPackage, className, null);
  }

  private IType createClass(IPackageFragment packageFragment, String className, String source)
      throws JavaModelException {
    final IType type;
    if (source == null) {
      type = WorkspaceHelper.createClass(packageFragment, className);
    }
    else {
      type = WorkspaceHelper.createType(packageFragment, className, source);
    }
    return type;
  }
}
