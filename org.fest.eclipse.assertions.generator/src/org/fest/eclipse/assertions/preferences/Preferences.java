package org.fest.eclipse.assertions.preferences;

import static org.fest.eclipse.assertions.generator.internal.AssertionGeneratorPlugin.PLUGIN_ID;
import static org.fest.eclipse.assertions.preferences.PreferenceConstants.TEST_SOURCE_DIRECTORY;
import static org.fest.eclipse.assertions.preferences.PreferenceConstants.TEST_SOURCE_DIRECTORY_DEFAULT;
import static org.fest.eclipse.assertions.preferences.PreferenceConstants.USE_PROJECT_SPECIFIC_SETTINGS;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import org.fest.eclipse.assertions.generator.internal.AssertionGeneratorPlugin;
import org.fest.eclipse.assertions.util.PluginTools;

public class Preferences {
  private static Preferences instance = new Preferences();

  private Map<IJavaProject, IPreferenceStore> preferenceMap = new HashMap<IJavaProject, IPreferenceStore>();

  protected Preferences() {
    initStore(getWorkbenchStore());
  }

  public static Preferences getInstance() {
    return instance;
  }

  /**
   * Necessary for easier testing
   */
  protected static void setInstance(Preferences preferences) {
    instance = preferences;
  }

  protected static final void initStore(IPreferenceStore store) {
    store.setDefault(TEST_SOURCE_DIRECTORY, TEST_SOURCE_DIRECTORY_DEFAULT);
  }

  protected IPreferenceStore getWorkbenchStore() {
    return AssertionGeneratorPlugin.plugin().getPreferenceStore();
  }

  public boolean hasProjectSpecificSettings(IJavaProject javaProject) {
    return javaProject == null ? false : store(javaProject).getBoolean(USE_PROJECT_SPECIFIC_SETTINGS);
  }

  public void setHasProjectSpecificSettings(IJavaProject javaProject, boolean hasProjectSpecificSettings) {
    getProjectStore(javaProject).setValue(USE_PROJECT_SPECIFIC_SETTINGS, hasProjectSpecificSettings);
  }

  public String getTestSourceDirectoryFromPreferences(IJavaProject javaProject) {
    return store(javaProject).getString(TEST_SOURCE_DIRECTORY);
  }

  public void setTestSourceDirectory(String directory) {
    getProjectStore(null).setValue(TEST_SOURCE_DIRECTORY, directory);
  }

  private IPreferenceStore store(IJavaProject javaProject) {
    IPreferenceStore projectStore = getProjectStore(javaProject);
    return projectStore.getBoolean(USE_PROJECT_SPECIFIC_SETTINGS) ? projectStore : getWorkbenchStore();
  }

  public IPreferenceStore getProjectStore(IJavaProject javaProject) {
    if (javaProject == null)
      return getWorkbenchStore();

    if (preferenceMap.containsKey(javaProject)) {
      return preferenceMap.get(javaProject);
    }

    ProjectScope projectScopeContext = new ProjectScope(javaProject.getProject());
    ScopedPreferenceStore preferenceStore = new ScopedPreferenceStore(projectScopeContext, PLUGIN_ID);
    preferenceStore.setSearchContexts(new IScopeContext[] { projectScopeContext });
    preferenceMap.put(javaProject, preferenceStore);
    return preferenceStore;
  }

  public void clearProjectCache() {
    synchronized (preferenceMap) {
      preferenceMap.clear();
    }
  }

  public IPackageFragmentRoot getTestSourceFolder(IJavaProject project, IPackageFragmentRoot mainSrcFolder) {
    String testSourceDirectory = getTestSourceDirectoryFromPreferences(project);
    for (IPackageFragmentRoot packageFragmentRoot : PluginTools.findJavaSourceFoldersFor(project)) {
      if (PluginTools.getPathStringWithoutProjectName(packageFragmentRoot).equals(testSourceDirectory)) {
        return packageFragmentRoot;
      }
    }
    // falls back to given source folder
    return mainSrcFolder;
  }

  public static ProjectPreferences forProject(IJavaProject project) {
    return getInstance().getProjectView(project);
  }

  public ProjectPreferences getProjectView(IJavaProject project) {
    return new ProjectPreferences(this, project);
  }

  public static class ProjectPreferences {
    private final Preferences prefs;
    private final IJavaProject project;

    public ProjectPreferences(Preferences prefs, IJavaProject project) {
      this.prefs = prefs;
      this.project = project;
    }

    public IPackageFragmentRoot getTestSourceFolder(IPackageFragmentRoot mainSrcFolder) {
      return prefs.getTestSourceFolder(project, mainSrcFolder);
    }

    public boolean hasSpecificSettings() {
      return prefs.hasProjectSpecificSettings(project);
    }

  }
}
