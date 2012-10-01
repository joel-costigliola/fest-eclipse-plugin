package org.fest.eclipse.assertions.preferences;

import static org.fest.eclipse.assertions.util.StringConstants.NEWLINE;

public interface PreferenceConstants
{
  String TEXT_GENERAL_SETTINGS = "General settings for Fest assertions (can be refined for each project):";
  String USE_PROJECT_SPECIFIC_SETTINGS = "org.moreunit.useprojectsettings";
  String TEST_SOURCE_DIRECTORY = "org.fest.eclipse.assertions.preferences.test_source_directory";
  String TEST_SOURCE_DIRECTORY_DEFAULT = "src/test/java";
  String TEXT_TEST_SOURCE_FOLDER = "Test base source folder:";
  String TOOLTIP_TEST_SOURCE_FOLDER = "Enter the name of the base folder that contains your test sources, e.g. 'test' or 'src/test/java'"
      + NEWLINE
      + NEWLINE
      + "The generated assertions classes will be placed from that base folder, for instance 'com.domain.Player' assertions class location will be :"
      + NEWLINE
      + "${test_base_folder}/com/domain/PlayerAssert.java";
}
