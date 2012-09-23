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
package org.fest.eclipse.assertions.generator.internal.log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.fest.eclipse.assertions.generator.internal.AssertionGeneratorPlugin;

public class DefaultLogger implements Logger {

  private final ILog logger;
  private final Level logLevel;

  public DefaultLogger(ILog log, String logLevelProperty) {
    this.logger = log;
    logLevel = Level.valueOf(System.getProperty(logLevelProperty, Level.INFO.name()).toUpperCase());
  }

  public boolean debugEnabled() {
    return levelEnabled(Level.DEBUG);
  }

  private boolean levelEnabled(Level level) {
    return !level.isLowerThan(logLevel);
  }

  public void debug(Object message) {
    if (debugEnabled()) {
      log(IStatus.INFO, "[DEBUG] " + message);
    }
  }

  public boolean infoEnabled() {
    return levelEnabled(Level.INFO);
  }

  public void info(Object message) {
    if (infoEnabled()) {
      log(IStatus.INFO, message);
    }
  }

  public boolean warnEnabled() {
    return levelEnabled(Level.WARNING);
  }

  public void warn(Object message) {
    if (warnEnabled()) {
      log(IStatus.WARNING, message);
    }
  }

  public void warn(Object message, Throwable throwable) {
    if (warnEnabled()) {
      log(IStatus.WARNING, message, throwable);
    }
  }

  public boolean errorEnabled() {
    return levelEnabled(Level.ERROR);
  }

  public void error(Object message) {
    if (errorEnabled()) {
      log(IStatus.ERROR, message);
    }
  }

  public void error(Throwable throwable) {
    if (errorEnabled()) {
      error(getStackTrace(throwable));
    }
  }

  private String getStackTrace(Throwable throwable) {
    ByteArrayOutputStream os = null;
    PrintStream printStream = null;
    try {
      os = new ByteArrayOutputStream();
      printStream = new PrintStream(os);
      throwable.printStackTrace(printStream);
      return os.toString();
    } finally {
      printStream.close();
      try {
        os.close();
      } catch (IOException e) {
        // ignored
      }
    }
  }

  public void error(Object message, Throwable throwable) {
    if (errorEnabled()) {
      log(IStatus.ERROR, message, throwable);
    }
  }

  private void log(int severity, Object message) {
    logger.log(new Status(severity, AssertionGeneratorPlugin.PLUGIN_ID, String.valueOf(message)));
  }

  private void log(int severity, Object message, Throwable throwable) {
    logger.log(new Status(severity, AssertionGeneratorPlugin.PLUGIN_ID, String.valueOf(message), throwable));
  }
}
