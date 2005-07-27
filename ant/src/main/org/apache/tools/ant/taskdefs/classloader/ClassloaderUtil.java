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

import java.net.URL;
import java.util.List;

import org.apache.tools.ant.taskdefs.classloader.report.ClassloaderReporter;
/**
 * Provides some utility methods for ClassloaderTask and ClassloaderReport.
 * @since Ant1.7
 */
public interface ClassloaderUtil {

    boolean containsEntry(ClassloaderContext ctx, ClassLoader cl,
            String url);

    ClassLoaderAdapter findAdapter(ClassloaderContext ctx,
            ClassLoader cl, ClassLoaderAdapterAction action)
            throws ClassloaderAdapterException;

    ClassLoaderAdapter findAdapter(ClassloaderContext ctx,
            ClassLoader cl, ClassLoaderAdapterAction action,
            ClassloaderReporter to, String errPrefix, String errSuffix);

    ClassLoaderAdapter findAdapter(ClassloaderContext ctx,
            ClassLoader cl, ClassLoaderAdapterAction action, List errors,
            String errPrefix, String errSuffix);

    URL[] getBootstrapClasspathURLs();

}