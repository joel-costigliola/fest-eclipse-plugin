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
package org.fest.eclipse.assertions.generator.internal.generation;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.fest.assertions.generator.BaseAssertionGenerator;
import org.fest.assertions.generator.description.ClassDescription;
import org.fest.eclipse.assertions.generator.internal.AssertionGeneratorPlugin;
import org.fest.eclipse.assertions.generator.internal.log.Logger;

public class Generator {

  private final Logger logger;
  private final TypeToClassDescriptionConverter classDescriptionConverter;
  private final BaseAssertionGenerator assertionGenerator;

  public Generator() throws IOException {
    logger = AssertionGeneratorPlugin.get().getLogger();
    classDescriptionConverter = new TypeToClassDescriptionConverter(logger);
    assertionGenerator = new BaseAssertionGenerator();
  }

  public File generateAssertionsFor(IType type) {
    ClassDescription classDescription = toClassDescription(type);
    File destinationFolder = getPackageFolder(type);

    File assertionFile = generateAssertionFor(classDescription, destinationFolder);

    if (assertionFile != null) {
      refreshPackage(type);
    }

    return assertionFile;
  }

  private ClassDescription toClassDescription(IType type) {
    return classDescriptionConverter.convertToClassDescription(type);
  }

  private File getPackageFolder(IType type) {
    try {
      IPackageFragment packageFragment = type.getPackageFragment();
      return packageFragment.getCorrespondingResource().getLocation().toFile();
    } catch (JavaModelException e) {
      logger.error("Could not retrieve folder for " + type.getFullyQualifiedName(), e);
      return null;
    }
  }

  private File generateAssertionFor(ClassDescription classDescription, File destinationFolder) {
    File assertionFile = null;
    try {
      assertionGenerator.setDirectoryWhereAssertionFilesAreGenerated(destinationFolder.getAbsolutePath());
      assertionFile = assertionGenerator.generateCustomAssertionFor(classDescription);
    } catch (IOException e) {
      logger.error("Could not generate assertions for " + classDescription.getClassName(), e);
    }
    return assertionFile;
  }

  private void refreshPackage(IType type) {
    IPackageFragment packageFragment = type.getPackageFragment();
    try {
      IResource resource = packageFragment.getCorrespondingResource();
      resource.refreshLocal(IResource.DEPTH_ONE, null);
    } catch (JavaModelException e) {
      logger.error("Could not refresh package " + packageFragment.getElementName(), e);
    } catch (CoreException e) {
      logger.error("Could not refresh package " + packageFragment.getElementName(), e);
    }
  }
}
