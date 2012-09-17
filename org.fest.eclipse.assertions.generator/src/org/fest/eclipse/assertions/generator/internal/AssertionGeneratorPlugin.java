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
package org.fest.eclipse.assertions.generator.internal;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.fest.eclipse.assertions.generator.internal.log.DefaultLogger;
import org.fest.eclipse.assertions.generator.internal.log.Logger;
import org.osgi.framework.BundleContext;

public class AssertionGeneratorPlugin extends AbstractUIPlugin {

  public static final String PLUGIN_ID = "org.fest.eclipse.assertions.generator"; //$NON-NLS-1$

  private static final String LOG_LEVEL_PROPERTY = PLUGIN_ID + ".log.level"; //$NON-NLS-1$

  private static AssertionGeneratorPlugin plugin;

  private Logger logger;

  public AssertionGeneratorPlugin() {
  }

  public void start(BundleContext context) throws Exception {
    super.start(context);
    plugin = this;

    logger = new DefaultLogger(getLog(), LOG_LEVEL_PROPERTY);

    logger.info("Plugin started");
  }

  public void stop(BundleContext context) throws Exception {
    logger.info("Stopping plugin...");

    plugin = null;
    super.stop(context);
  }

  public static AssertionGeneratorPlugin get() {
    return plugin;
  }

  public Logger getLogger() {
    return logger;
  }
}
