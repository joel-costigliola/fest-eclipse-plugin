package org.fest.eclipse.assertions.preferences;

import static org.eclipse.swt.layout.GridData.FILL_HORIZONTAL;

import static org.fest.eclipse.assertions.preferences.PreferenceConstants.TEXT_GENERAL_SETTINGS;
import static org.fest.eclipse.assertions.preferences.PreferenceConstants.TEXT_TEST_SOURCE_FOLDER;
import static org.fest.eclipse.assertions.preferences.PreferenceConstants.TOOLTIP_TEST_SOURCE_FOLDER;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import org.fest.eclipse.assertions.generator.internal.AssertionGeneratorPlugin;

/**
 * Inspired from MoreUnit code. org.fest.eclipse.assertions.AssertionsPreferencePage
 * 
 * @author Joel Costigliola
 */
public class FestAssertionsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{

  private Text testSourceFolderField;

  public FestAssertionsPreferencePage() {
    setDescription(TEXT_GENERAL_SETTINGS);
  }

  @Override
  protected Control createContents(Composite parent) {
    initializeDialogUnits(parent);

    Composite contentComposite = new Composite(parent, NONE);
    GridLayout layout = new GridLayout();
    layout.marginWidth = 0;
    layout.marginRight = 10;
    layout.numColumns = 2;
    contentComposite.setLayout(layout);
    contentComposite.setLayoutData(new GridData(FILL_HORIZONTAL));

    testSourceFolderField = createTestSourceFolderField(contentComposite, null);

    applyDialogFont(contentComposite);

    return parent;
  }

  public static Text createTestSourceFolderField(Composite parent, IJavaProject javaProject) {
    Label label = new Label(parent, NONE);
    label.setText(TEXT_TEST_SOURCE_FOLDER);
    label.setToolTipText(TOOLTIP_TEST_SOURCE_FOLDER);

    Text testSourceFolderField = new Text(parent, SWT.SINGLE | SWT.BORDER);
    testSourceFolderField.setText(Preferences.instance().getTestSourceDirectory(javaProject));
    testSourceFolderField.setToolTipText(TOOLTIP_TEST_SOURCE_FOLDER);

    GridData layoutForTextFields = new GridData(SWT.FILL, SWT.CENTER, true, false);
    layoutForTextFields.horizontalIndent = 30;
    layoutForTextFields.minimumWidth = 40;
    testSourceFolderField.setLayoutData(layoutForTextFields);
    return testSourceFolderField;
  }

  public void init(IWorkbench workbench) {
    setPreferenceStore(AssertionGeneratorPlugin.plugin().getPreferenceStore());
  }

  @Override
  protected IPreferenceStore doGetPreferenceStore() {
    return AssertionGeneratorPlugin.plugin().getPreferenceStore();
  }

  @Override
  public boolean performOk() {
    Preferences.instance().setTestSourceDirectory(null, testSourceFolderField.getText());
    Preferences.instance().clearProjectCache();
    return super.performOk();
  }
}