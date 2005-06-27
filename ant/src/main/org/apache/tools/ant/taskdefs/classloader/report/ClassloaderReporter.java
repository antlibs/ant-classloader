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
package org.apache.tools.ant.taskdefs.classloader.report;

import java.net.URL;


public interface ClassloaderReporter {

    void reportClass(Class s);

    void beginReport();

    void endReport();

    void beginClassloader(ClassloaderReportHandle name);

    void endClassloader(ClassloaderReportHandle name);

    void beginEntries(int num);

    void endEntries(int num);

    void beginAttributes(int num);

    void endAttributes(int num);

    void beginChildLoaders(int num);

    void endChildLoaders(int num);

    void reportEntry(URL url);

    void reportEntry(String type, String entry);

    void beginErrors(int num);
    
    void endErrors(int num);
    
    void beginUnassignedRoles(int num);
    
    void endUnassignedRoles(int num);

    void beginPackages(int num);

    void endPackages(int num);

    void beginRoles(int num);

    void endRoles(int num);

    void reportPackage(String pkg);

    void reportAttribute(String name, String value);

    void reportExlicitelyParent(ClassloaderReportHandle handle);

    void reportImplicitelyParent(ClassloaderReportHandle handle);

    void reportError(String msg);

    void reportUnassignedRole(ClassloaderReportHandle handle);

    void reportRole(ClassloaderReportHandle handle);

}