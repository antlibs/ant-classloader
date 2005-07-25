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
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.tools.ant.taskdefs.classloader.ClassLoaderAdapter;
import org.apache.tools.ant.taskdefs.classloader.ClassLoaderAdapterAction;
import org.apache.tools.ant.taskdefs.classloader.ClassLoaderAdapterContext;
import org.apache.tools.ant.taskdefs.classloader.ClassloaderUtil;

/**
 * Utility methods for the classloader report.
 *
 * @since Ant 1.7
 */
public class ClassloaderReportUtil {
    private static final class PackageComparator implements Comparator {
        public static final Comparator SINGLETON = new PackageComparator();
        private PackageComparator() {
        }
        public int compare(Object o1, Object o2) {
            return ((Package) o1).getName().compareTo(((Package) o2).getName());
        }
    }
    private static ClassloaderReportUtil singleton = new ClassloaderReportUtil();
    /**
     * Gets the singleton report util.
     *
     * @return The singleton report util.
     */
    public static ClassloaderReportUtil getReportUtil() {
        return singleton;
    }
    /**
     * Sets the singleton report util.
     *
     * @param util
     *            New singleton report util instance.
     */
    public static void setReportUtil(ClassloaderReportUtil util) {
        if (util != null) {
            singleton = util;
        }
    }
    /**
     * Constructor for derived classes.
     */
    protected ClassloaderReportUtil() {
    }
    /**
     * Callback method to add classloaders to the list of loaders to report.
     *
     * @param context
     *            The context.
     * @param cl
     *            The classloader instance to add.
     * @param role
     *            The name of the classloader instance.
     * @param handlesByLoader
     *            A list of loader names by instance.
     * @param loaderByHandle
     *            A list of loader instances by name.
     * @param to
     *            The reporter to report errors against.
     * @return <code>true</code>, if successfully executed,
     *         <code>false</code> otherwise.
     */
    public boolean addLoaderToReport(ClassLoaderAdapterContext.Report context,
            ClassLoader cl, ClassloaderReportHandle role,
            Map/* <ClassLoader,SortedSet<ReportHandle> */handlesByLoader,
            Map/* <ReportHandle,ClassLoader> */loaderByHandle,
            ClassloaderReporter to) {
        Object old = loaderByHandle.put(role, cl);
        if (old != null) {
            throw new RuntimeException("duplicate classloader " + role);
        }
        if (cl != null) {
            old = handlesByLoader.get(cl);
            boolean isNew = (old == null);
            if (old == null) {
                old = new TreeSet();
                handlesByLoader.put(cl, old);
            }
            ((Set) old).add(role);

            if (isNew) {
                ClassLoaderAdapter adapter = ClassloaderUtil.findAdapter(
                        context, cl, null, to, role + "->parent", role
                                .getName());
                boolean adapterFound = (adapter != null);
                if (adapterFound) {
                    ClassLoader parent = adapter.getParent(cl);
                    if (parent == null) {
                        parent = adapter.getDefaultParent();
                    }
                    if (parent != null) {
                        addLoaderToReport(context, adapter.getParent(cl),
                                new ClassloaderReportHandle(
                                        ClassloaderReportHandle.PARENT, role
                                                .toString()), handlesByLoader,
                                loaderByHandle, to);
                    }
                }
                adapter = ClassloaderUtil.findAdapter(context, cl,
                        ClassLoaderAdapterAction.REPORT, to, "report for "
                                + role, "");
                if (adapter != null) {
                    adapter.addReportable(context, cl, role, handlesByLoader,
                            loaderByHandle);
                }
                return adapterFound && (adapter != null);
            }
        }
        return true;
    }
    /**
     * handle the report for a single classloader
     * @param context The context.
     * @param to
     *            Reporter to report.
     * @param cl
     *            ClassloaderBase instance to report.
     * @param name
     *            name of the classloader instance.
     * @param handlesByLoader Handles by loader.
     */
    public void report(ClassLoaderAdapterContext.Report context,
            ClassloaderReporter to, ClassLoader cl,
            ClassloaderReportHandle name, Map handlesByLoader) {
        to.beginClassloader(name);
        ClassLoaderAdapter baseAdapter = ClassloaderUtil.findAdapter(context,
                cl, null, to, "parent for " + name, "");
        if (baseAdapter != null) {
            ClassLoader parent = baseAdapter.getParent(cl);

            if (parent != null) {
                SortedSet handles = (SortedSet) handlesByLoader.get(parent);
                to.reportExlicitelyParent((ClassloaderReportHandle) handles
                        .first());
            } else {
                parent = baseAdapter.getDefaultParent();
                if (parent != null) {
                    SortedSet handles = (SortedSet) handlesByLoader.get(parent);
                    to
                            .reportImplicitelyParent((ClassloaderReportHandle) handles
                                    .first());
                } else {
                    to
                            .reportImplicitelyParent(ClassloaderReportHandle.BOOTSTRAPHANDLE);
                }
            }
        }
        to.reportClass(cl.getClass());
        SortedSet roles = (SortedSet) handlesByLoader.get(cl);
        for (Iterator iRole = roles.iterator(); iRole.hasNext();) {
            to.reportRole((ClassloaderReportHandle) iRole.next());
        }
        ClassLoaderAdapter adapter = ClassloaderUtil
                .findAdapter(context, cl, ClassLoaderAdapterAction.GETPATH, to,
                        "entries for " + name, "");
        if (adapter != null) {
            String[] cp = adapter.getClasspath(context, cl, false);
            if (cp == null) {
                to.reportError("entries for " + name
                        + " not investigatable (adapter retrieves no path)");
            } else {
                to.beginEntries(cp.length);
                for (int i = 0; i < cp.length; i++) {
                    to.reportEntry("url", cp[i]);
                }
            }
        }
        if (context.isReportPackages()) {
            reportPackages(context, to, baseAdapter, cl, name);
        }
        adapter = ClassloaderUtil.findAdapter(context, cl,
                ClassLoaderAdapterAction.REPORT, to,
                "additional parameters for " + name, "");
        if (adapter != null) {
            adapter.report(to, context, cl, name);
        }
    }
    /**
     * Handles the report.
     *
     * @param context
     *            The report context.
     * @param handlesByLoader
     *            A map.
     * @param loaderByHandle
     *            A map.
     * @param to
     *            The reporter to report to.
     * @param allHandlersFound
     *            a flag indicating whether all handlers for classloaders where
     *            found.
     */
    public void report(ClassLoaderAdapterContext.Report context,
            Map/* <ClassLoader,SortedSet<ReportHandle>> */handlesByLoader,
            Map/* <ReportHandle,ClassLoader> */loaderByHandle,
            ClassloaderReporter to, boolean allHandlersFound) {

        to.beginReport();
        if (!allHandlersFound) {
            to.reportError("WARNING: As of missing Loaderhandlers,"
              + " this report might not be complete.");
        }
        URL[] urls = ClassloaderUtil.getBootstrapClasspathURLs();
        if (urls == null) {
            to.reportError("WARNING: Unable to determine bootstrap classpath."
              + "\n         Please report this error to Ant's bugtracking "
              + " system with information"
              + "\n         about your environment "
              + " (JVM-Vendor, JVM-Version, OS, application context).");
        } else {
            to.beginClassloader(ClassloaderReportHandle.BOOTSTRAPHANDLE);
            to.beginEntries(urls.length);
            for (int i = 0; i < urls.length; i++) {
                to.reportEntry(urls[i]);
            }
            to.endEntries(urls.length);
            to.endClassloader(ClassloaderReportHandle.BOOTSTRAPHANDLE);
        }
        for (Iterator iRole = loaderByHandle.keySet().iterator(); iRole
                .hasNext();) {
            ClassloaderReportHandle role = (ClassloaderReportHandle) iRole
                    .next();
            ClassLoader cl = (ClassLoader) loaderByHandle.get(role);
            if (cl == null) {
                if (role.isPopular()) {
                    to.reportUnassignedRole(role);
                }
            } else {
                SortedSet handles = (SortedSet) handlesByLoader.get(cl);
                if (role.equals(handles.first())) {
                    report(context, to, cl, role, handlesByLoader);
                }
            }
        }
        to.endReport();
    }
    private void reportPackages(ClassLoaderAdapterContext.Report task,
            ClassloaderReporter to, ClassLoaderAdapter adapter,
            ClassLoader classloader, ClassloaderReportHandle role) {
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
