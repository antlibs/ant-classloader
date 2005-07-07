/*
 * Copyright  2005 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.tools.ant.taskdefs.classloader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public interface ClassLoaderURLUtil {
    /**
     * Creates a URL from a absolute or relative file or url.
     * @param fileOrURL Absolute or relative file or url.
     * @return An URL.
     * @throws MalformedURLException If <code>new URL()</code> throws it.
     */
    URL createURL(String fileOrURL) throws MalformedURLException;
    /**
     * Creates a file from a absolute or relative file or url.
     * @param fileOrURL Absolute or relative file or url.
     * @return A file.
     */
    File createFile(String fileOrURL);
}
