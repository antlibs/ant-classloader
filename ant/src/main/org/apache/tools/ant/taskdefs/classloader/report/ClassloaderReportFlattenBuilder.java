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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.tools.ant.taskdefs.classloader.ClassloaderContext;

/**
 * Builds a flatten representation of reporting elements.
 * @since Ant1.7
 */
public final class ClassloaderReportFlattenBuilder implements ClassloaderReportBuilder {
    private static class CL {
        private static class Attr {
            private final String name;
            private final String value;
            Attr(String name, String value) {
                this.name = name;
                this.value = value;
            }
        }
        private static class Entry {
            private final String type;
            private final String value;
            Entry(String type, String value) {
                this.type = type;
                this.value = value;
            }
        }
        private ArrayList attributes = new ArrayList();
        private SortedSet childs;
        private Class clazz = null;
        private ArrayList entries = new ArrayList();
        private boolean expliciteParent;
        private final ClassloaderReportHandle handle;
        private SortedSet packages = new TreeSet();
        private ClassloaderReportHandle parent;
        private SortedSet roles = new TreeSet();
        public CL(ClassloaderReportHandle handle) {
            this.handle = handle;
        }
    }
    private Map cLbyHandle = new HashMap();
    private CL currentCL;
    private ArrayList errors = new ArrayList();
    private SortedSet unassigned = new TreeSet();
    private final ClassloaderContext.Report context;
    /**
     * Constructor.
     * @param context The context.
     */
    public ClassloaderReportFlattenBuilder(ClassloaderContext.Report context) {
        this.context = context;
    }
    /**
     * Indicates start of attributes-section.
     * @param num Number of elements.
     */
    public void beginAttributes(int num) {
    }
    /**
     * Indicates start of child-section.
     * @param num Number of elements.
     */
    public void beginChildLoaders(int num) {
    }
    /**
     * Indicates start of classloader reporting.
     * @param name Handle of the classloader.
     */
    public void beginClassloader(ClassloaderReportHandle name) {
        currentCL = new CL(name);
        cLbyHandle.put(name, currentCL);
    }
    /**
     * Indicates start of entries-section.
     * @param num Number of elements.
     */
    public void beginEntries(int num) {
    }
    /**
     * Indicates start of error-section.
     * @param num Number of elements.
     */
    public void beginErrors(int num) {
    }
    /**
     * Indicates start of packages-section.
     * @param num Number of elements.
     */
    public void beginPackages(int num) {
    }
    /**
     * Indicates start of report.
     */
    public void beginReport() {
    }
    /**
     * Indicates start of role-section.
     * @param num Number of elements.
     */
    public void beginRoles(int num) {
    }
    /**
     * Indicates start of unassigned-roles-section.
     * @param num Number of elements.
     */
    public void beginUnassignedRoles(int num) {
    }
    /**
     * Indicates end of attributes-section.
     * @param num Number of elements.
     */
    public void endAttributes(int num) {
    }
    /**
     * Indicates end of child-section.
     * @param num Number of elements.
     */
    public void endChildLoaders(int num) {
    }
    /**
     * Indicates end of classloader reporting.
     * @param name Handle of the classloader.
     */
    public void endClassloader(ClassloaderReportHandle name) {
    }
    /**
     * Indicates end of entries-section.
     * @param num Number of elements.
     */
    public void endEntries(int num) {
    }
    /**
     * Indicates end of errors-section.
     * @param num Number of elements.
     */
    public void endErrors(int num) {
    }
    /**
     * Indicates end of packages-section.
     * @param num Number of elements.
     */
    public void endPackages(int num) {
    }
    /**
     * Indicates end of report.
     */
    public void endReport() {
    }
    /**
     * Indicates end of roles-section.
     * @param num Number of elements.
     */
    public void endRoles(int num) {
    }
    /**
     * Indicates end of unassigned-roles-section.
     * @param num Number of elements.
     */
    public void endUnassignedRoles(int num) {
    }
    /**
     * Executes this Builder to another reporter.
     * @param to Another reporter.
     */
    public void execute(ClassloaderReporter to) {
        to.beginReport();
        if (errors.size() > 0) {
            to.beginErrors(errors.size());
            for (Iterator i = errors.iterator(); i.hasNext();) {
                to.reportError((String) i.next());
            }
            to.endErrors(errors.size());
        }
        if (unassigned.size() > 0) {
            to.beginUnassignedRoles(unassigned.size());
            for (Iterator i = unassigned.iterator(); i.hasNext();) {
                to.reportUnassignedRole((ClassloaderReportHandle) i.next());
            }
            to.endUnassignedRoles(unassigned.size());
        }
        // (re-)initialize childmaps
        for (Iterator i = cLbyHandle.values().iterator(); i.hasNext();) {
            ((CL) i.next()).childs = new TreeSet();
        }
        TreeMap roots = new TreeMap();
        for (Iterator i = cLbyHandle.values().iterator(); i.hasNext();) {
            CL cl = (CL) i.next();
            roots.put(cl.handle, cl);
            if (cl.parent != null) {
                CL parent = (CL) cLbyHandle.get(cl.parent);
                if (parent == null) {
                    throw new RuntimeException("internal error: " + cl.parent
                            + " not found");
                }
                parent.childs.add(cl.handle);
            }
        }
        for (Iterator i = roots.values().iterator(); i.hasNext();) {
            execute(to, (CL) i.next());
        }
        to.endReport();
    }
    private void execute(ClassloaderReporter to, CL cl) {
        to.beginClassloader(cl.handle);
        if (cl.parent != null) {
            if (cl.expliciteParent) {
                to.reportExlicitelyParent(cl.parent);
            } else {
                to.reportImplicitelyParent(cl.parent);
            }
        }
        if (cl.clazz != null) {
            to.reportClass(cl.clazz);
        }
        if (cl.attributes != null) {
            to.beginAttributes(cl.attributes.size());
            for (Iterator iA = cl.attributes.iterator(); iA.hasNext();) {
                CL.Attr attr = (CL.Attr) iA.next();
                to.reportAttribute(attr.name, attr.value);
            }
            to.endAttributes(cl.attributes.size());
        }
        if (cl.entries != null) {
            to.beginEntries(cl.entries.size());
            for (Iterator iE = cl.entries.iterator(); iE.hasNext();) {
                CL.Entry e = (CL.Entry) iE.next();
                to.reportEntry(e.type, e.value);
            }
            to.endEntries(cl.entries.size());
        }
        if (cl.roles != null) {
            to.beginRoles(cl.roles.size());
            for (Iterator iR = cl.roles.iterator(); iR.hasNext();) {
                to.reportRole((ClassloaderReportHandle) iR.next());
            }
            to.endRoles(cl.roles.size());
        }
        if (context.isReportPackages() && (cl.packages != null)) {
            to.beginPackages(cl.packages.size());
            for (Iterator iP = cl.packages.iterator(); iP.hasNext();) {
                to.reportPackage((String) iP.next());
            }
            to.endPackages(cl.packages.size());
        }
        if (cl.childs.size() > 0) {
            to.beginChildLoaders(cl.childs.size());
            for (Iterator iC = cl.childs.iterator(); iC.hasNext();) {
                to.reportChild((ClassloaderReportHandle) iC.next());
            }
            to.endChildLoaders(cl.childs.size());
        }
        to.endClassloader(cl.handle);
    }
    /**
     * Reports a single attribute.
     * @param name Name of the attribute.
     * @param value Value of the attribute.
     */
    public void reportAttribute(String name, String value) {
        currentCL.attributes.add(new CL.Attr(name, value));
    }
    /**
     * Reports a single child.
     * @param name Role of the child.
     */
    public void reportChild(ClassloaderReportHandle name) {
    }
    /**
     * Reports the classloader's class.
     * @param s Class of the classloader.
     */
    public void reportClass(Class s) {
        currentCL.clazz = s;
    }
    /**
     * Reports a single entry.
     * @param type Type of the entry (f.e. url or file).
     * @param entry The entry.
     */
    public void reportEntry(String type, String entry) {
        currentCL.entries.add(new CL.Entry(type, entry));
    }
    /**
     * Reports a single url entry.
     * Same as reportEntry("url", url.toString());
     * @param url The url.
     */
    public void reportEntry(URL url) {
        reportEntry("url", url.toString());
    }
    /**
     * Reports an error.
     * @param msg The error message.
     */
    public void reportError(String msg) {
        errors.add(msg);
    }
    /**
     * Reports an explicit parent classloader.
     * @param handle The parent's handle.
     */
    public void reportExlicitelyParent(ClassloaderReportHandle handle) {
        currentCL.parent = handle;
        currentCL.expliciteParent = true;
    }
    /**
     * Reports an implicit parent classloader.
     * @param handle The parent's handle.
     */
    public void reportImplicitelyParent(ClassloaderReportHandle handle) {
        currentCL.parent = handle;
        currentCL.expliciteParent = false;
    }
    /**
     * Reports a single defined package.
     * @param pkg The package name.
     */
    public void reportPackage(String pkg) {
        currentCL.packages.add(pkg);
    }
    /**
     * Reports a single role.
     * @param handle The role.
     */
    public void reportRole(ClassloaderReportHandle handle) {
        currentCL.roles.add(handle);
    }
    /**
     * Reports a single unassigned role.
     * @param handle The unassigned role.
     */
    public void reportUnassignedRole(ClassloaderReportHandle handle) {
        unassigned.add(handle);
    }
}