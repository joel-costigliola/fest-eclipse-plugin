package org.fest.eclipse.assertions.properties;

import static org.eclipse.swt.layout.GridData.FILL_HORIZONTAL;

import static org.fest.eclipse.assertions.preferences.FestAssertionsPreferencePage.createTestSourceFolderField;

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import org.fest.eclipse.assertions.preferences.Preferences;

/**
 * @author Joel Costigliola
 */
public class FestAssertionsPropertyPage extends PropertyPage {

  private Button projectSpecificSettingsCheckbox;
  private Text testSourceFolderField;

  @Override
  protected Control createContents(Composite parent) {
    Composite contentComposite = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout();
    layout.marginWidth = 0;
    layout.marginRight = 10;
    layout.numColumns = 2;
    contentComposite.setLayout(layout);
    contentComposite.setLayoutData(new GridData(FILL_HORIZONTAL));

    createCheckboxContent(contentComposite);
    testSourceFolderField = createTestSourceFolderField(contentComposite, getJavaProject());
    testSourceFolderField.setEnabled(shouldUseProjectSpecificSettings());

    return parent;
  }

  @Override
  public boolean performOk() {
    saveProperties();
    return super.performOk();
  }

  @Override
  protected void performApply() {
    saveProperties();
    super.performApply();
  }

  private void createCheckboxContent(Composite parent) {
    projectSpecificSettingsCheckbox = new Button(parent, SWT.CHECK);
    projectSpecificSettingsCheckbox.setText("Use project specific settings");

    projectSpecificSettingsCheckbox.addSelectionListener(new SelectionListener() {
      public void widgetDefaultSelected(SelectionEvent e) {
      }

      public void widgetSelected(SelectionEvent e) {
        // enable/disable text field according to projectSpecificSettingsCheckbox state.
        testSourceFolderField.setEnabled(shouldUseProjectSpecificSettings());
      }
    });

    GridData gridData = new GridData();
    gridData.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
    gridData.horizontalSpan = 2;

    projectSpecificSettingsCheckbox.setLayoutData(gridData);
    projectSpecificSettingsCheckbox
        .setSelection(Preferences.instance().hasProjectSpecificSettings(getJavaProject()));

  }

  private boolean shouldUseProjectSpecificSettings() {
    return projectSpecificSettingsCheckbox.getSelection();
  }

  private void saveProperties() {
    try {
      IJavaProject javaProject = getJavaProject();
      Preferences.instance().setHasProjectSpecificSettings(javaProject, shouldUseProjectSpecificSettings());
      if (shouldUseProjectSpecificSettings()) {
        Preferences.instance().setTestSourceDirectory(javaProject, testSourceFolderField.getText());
      }
      IPreferenceStore store = Preferences.instance().getProjectStore(javaProject);
      if (store instanceof ScopedPreferenceStore) {
        ((ScopedPreferenceStore) store).save();
      }
    } catch (IOException e) {
      // TODO log error
    }
  }

  // TODO : explain ...
  private IJavaProject getJavaProject() {
    if (getElement() instanceof IJavaProject) {
      return (IJavaProject) getElement();
    }
    return JavaCore.create((IProject) getElement());
  }

}
