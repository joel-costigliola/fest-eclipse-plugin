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

public interface Logger {

  boolean debugEnabled();

  void debug(Object message);

  boolean infoEnabled();

  void info(Object message);

  boolean warnEnabled();

  void warn(Object message);

  void warn(Object message, Throwable throwable);

  boolean errorEnabled();

  void error(Object message);

  void error(Throwable throwable);

  void error(Object message, Throwable throwable);
}
