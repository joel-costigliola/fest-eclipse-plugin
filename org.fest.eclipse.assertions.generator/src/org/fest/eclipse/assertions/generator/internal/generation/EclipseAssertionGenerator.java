/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 * 
 * Copyright @2012 the original author or authors.
 */
package org.fest.eclipse.assertions.generator.internal.generation;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import org.fest.assertions.generator.BaseAssertionGenerator;
import org.fest.assertions.generator.description.ClassDescription;
import org.fest.eclipse.assertions.generator.internal.AssertionGeneratorPlugin;
import org.fest.eclipse.assertions.generator.internal.converter.TypeToClassDescriptionConverter;
import org.fest.eclipse.assertions.generator.internal.log.Logger;
import org.fest.eclipse.assertions.preferences.Preferences;

/**
 * An assertion generator taking {@link IType} as input.
 */
public class EclipseAssertionGenerator {

  private final Logger logger;
  private final TypeToClassDescriptionConverter classDescriptionConverter;
  private final BaseAssertionGenerator assertionGenerator;

  public EclipseAssertionGenerator() throws IOException {
    logger = AssertionGeneratorPlugin.get().getLogger();
    classDescriptionConverter = new TypeToClassDescriptionConverter(logger);
    assertionGenerator = new BaseAssertionGenerator();
  }

  public File generateAssertionsFor(IType type) {
    ClassDescription classDescription = classDescriptionConverter.convertToClassDescription(type);

    File assertionFile = null;
    try {
      File destinationFolder = getAssertionClassDestinationDirectory(type);
      // create directory if needed
      if (!destinationFolder.exists()) {
        destinationFolder.mkdirs();
      }

      assertionFile = generateAssertionFor(classDescription, destinationFolder);

      if (assertionFile != null) {
        // refresh project to show assertions class
        refreshResource(type.getCompilationUnit().getJavaProject().getCorrespondingResource());
      }
    } catch (JavaModelException e) {
      logger.error("Could not generate assertions for " + classDescription.getClassName(), e);
    }

    return assertionFile;
  }

  private static File getAssertionClassDestinationDirectory(IType type) throws JavaModelException {
    IJavaProject javaProject = type.getCompilationUnit().getJavaProject();
    // get test base directory as defined in preferences, ex : src/test/java
    String testSourceDirectory = Preferences.getInstance().getTestSourceDirectoryFromPreferences(javaProject);
    IPath javaProjectPath = javaProject.getCorrespondingResource().getLocation();
    IPath destinationFolderPath = javaProjectPath.append(testSourceDirectory);
    // get type's package elements, if type is org.demo.Player, then package elements -> ["org", "demo"]
    String[] typePackageElementNames = type.getPackageFragment().getElementName().split("\\.");
    for (String packageElementName : typePackageElementNames) {
      destinationFolderPath = destinationFolderPath.append(packageElementName);
    }
    // following the given example, it would be : src/test/java/org/demo
    return destinationFolderPath.toFile();
  }

  private File generateAssertionFor(ClassDescription classDescription, File destinationFolder) {
    File assertionFile = null;
    try {
      assertionGenerator.setDirectoryWhereAssertionFilesAreGenerated(destinationFolder.getAbsolutePath());
      assertionFile = assertionGenerator.generateCustomAssertionFor(classDescription);
    } catch (IOException e) {
      logger.error("Could not generate assertions for " + classDescription.getClassName() + " in folder "
          + destinationFolder, e);
    }
    return assertionFile;
  }

  private void refreshResource(IResource resource) {
    try {
      resource.refreshLocal(IResource.DEPTH_INFINITE, null);
    } catch (CoreException e) {
      logger.error("Could not refresh package " + resource.getName(), e);
    }
  }
}
