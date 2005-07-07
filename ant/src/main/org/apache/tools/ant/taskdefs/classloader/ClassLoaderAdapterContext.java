/*
/*
 * Copyright  2004-2005 The Apache Software Foundation
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

public interface ClassLoaderAdapterContext {
    public static interface Report extends ClassLoaderAdapterContext {
        boolean isReportPackages();
    }
    public static interface CreateModify extends ClassLoaderAdapterContext {
        String getLoaderName();
        ClassLoaderParameters getParameters();
        String[] getClasspathURLs();
        String[] getClasspathFiles();
        boolean handleClasspathEntry(ClassLoader cl, String entryUrl);
        ClassLoader getParentLoader();
        ClassLoader getSuperLoader();
        Object getAntProject();
    }
    void handleDebug(String msg);
    void handleWarning(String msg);
    void handleError(String msg);
    void handleError(String msg, Throwable ex);
    ClassLoaderURLUtil getURLUtil();
    ClassLoaderHandlerSet getHandlerSet();
}
