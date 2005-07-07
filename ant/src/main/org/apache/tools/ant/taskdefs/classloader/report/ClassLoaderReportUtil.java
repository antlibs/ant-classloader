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

import java.util.Arrays;
import java.util.Comparator;

import org.apache.tools.ant.taskdefs.classloader.ClassLoaderAdapter;
import org.apache.tools.ant.taskdefs.classloader.ClassLoaderAdapterContext;

public class ClassLoaderReportUtil {
    private static final class PackageComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            return ((Package) o1).getName().compareTo(((Package) o2).getName());
        }
        private PackageComparator() {
        }
        public static final Comparator SINGLETON = new PackageComparator();
    }

    private ClassLoaderReportUtil() {
    }
    public static void reportPackages(
            ClassloaderReporter to,
            ClassLoaderAdapterContext.Report task,
            ClassLoaderAdapter adapter,
            ClassLoader classloader,
            ClassloaderReportHandle role) {
                Package[] pkgs = adapter.getPackages(task, classloader, role);
                if (pkgs == null) {
                    to.reportError("packages of " + role + " not investigatable");
                } else {
                    Arrays.sort(pkgs, PackageComparator.SINGLETON);
                    to.beginPackages(pkgs.length);
                    for (int i = 0; i < pkgs.length; i++) {
                        to.reportPackage(pkgs[i].getName());
                    }
                    to.endPackages(pkgs.length);
                }
        }

}
