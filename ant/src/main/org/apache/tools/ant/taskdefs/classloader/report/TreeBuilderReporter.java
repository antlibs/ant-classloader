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
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * makes reporting destination transparent for reporting objects.
 */
public final class TreeBuilderReporter implements ClassloaderReporter {
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
        private SortedMap childs;
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
    public void beginAttributes(int num) {
    }
    public void beginChildLoaders(int num) {
    }
    /*
     * (non-Javadoc)
     *
     * @see org.apache.tools.ant.taskdefs.ClassLoaderReporter#beginClassloader(org.apache.tools.ant.taskdefs.ClassloaderBase.ReportHandle)
     */
    public void beginClassloader(ClassloaderReportHandle name) {
        currentCL = new CL(name);
        cLbyHandle.put(name, currentCL);
    }
    /*
     * (non-Javadoc)
     *
     * @see org.apache.tools.ant.taskdefs.ClassLoaderReporter#beginEntries(int)
     */
    public void beginEntries(int num) {
    }
    public void beginErrors(int num) {
    }
    /*
     * (non-Javadoc)
     *
     * @see org.apache.tools.ant.taskdefs.ClassLoaderReporter#beginPackages(int)
     */
    public void beginPackages(int num) {
    }
    /*
     * (non-Javadoc)
     *
     * @see org.apache.tools.ant.taskdefs.ClassLoaderReporter#beginReport()
     */
    public void beginReport() {
    }
    public void beginRoles(int num) {
    }
    public void beginUnassignedRoles(int num) {
    }
    public void endAttributes(int num) {
    }
    public void endChildLoaders(int num) {
    }
    /*
     * (non-Javadoc)
     *
     * @see org.apache.tools.ant.taskdefs.ClassLoaderReporter#endClassloader(org.apache.tools.ant.taskdefs.ClassloaderBase.ReportHandle)
     */
    public void endClassloader(ClassloaderReportHandle name) {
    }
    /*
     * (non-Javadoc)
     *
     * @see org.apache.tools.ant.taskdefs.ClassLoaderReporter#endEntries(int)
     */
    public void endEntries(int num) {
    }
    public void endErrors(int num) {
    }
    /*
     * (non-Javadoc)
     *
     * @see org.apache.tools.ant.taskdefs.ClassLoaderReporter#endPackages(int)
     */
    public void endPackages(int num) {
    }
    /*
     * (non-Javadoc)
     *
     * @see org.apache.tools.ant.taskdefs.ClassLoaderReporter#endReport()
     */
    public void endReport() {
    }
    public void endRoles(int num) {
    }
    public void endUnassignedRoles(int num) {
    }
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
            ((CL) i.next()).childs = new TreeMap();
        }
        TreeMap roots = new TreeMap();
        for (Iterator i = cLbyHandle.values().iterator(); i.hasNext();) {
            CL cl = (CL) i.next();
            if (cl.parent == null) {
                roots.put(cl.handle, cl);
            } else {
                CL parent = (CL) cLbyHandle.get(cl.parent);
                if (parent == null) {
                    throw new RuntimeException("internal error: " + cl.parent
                            + " not found");
                }
                parent.childs.put(cl.handle, cl);
            }
        }
        for (Iterator i = roots.values().iterator(); i.hasNext();) {
            execute(to, (CL) i.next());
        }
        to.endReport();
    }
    public void execute(ClassloaderReporter to, CL cl) {
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
        if (cl.packages != null) {
            to.beginPackages(cl.packages.size());
            for (Iterator iP = cl.packages.iterator(); iP.hasNext();) {
                to.reportPackage((String) iP.next());
            }
            to.endPackages(cl.packages.size());
        }
        if (cl.childs.size() > 0) {
            to.beginChildLoaders(cl.childs.size());
            for (Iterator iC = cl.childs.values().iterator(); iC.hasNext();) {
                execute(to, (CL) iC.next());
            }
            to.endChildLoaders(cl.childs.size());
        }
        to.endClassloader(cl.handle);
    }
    /*
     * (non-Javadoc)
     *
     * @see org.apache.tools.ant.taskdefs.ClassLoaderReporter#reportAttribute(java.lang.String,
     *      java.lang.String)
     */
    public void reportAttribute(String name, String value) {
        currentCL.attributes.add(new CL.Attr(name, value));
    }
    /*
     * (non-Javadoc)
     *
     * @see org.apache.tools.ant.taskdefs.ClassLoaderReporter#reportClass(java.lang.Class)
     */
    public void reportClass(Class s) {
        currentCL.clazz = s;
    }
    /*
     * (non-Javadoc)
     *
     * @see org.apache.tools.ant.taskdefs.ClassLoaderReporter#reportEntry(java.lang.String,
     *      java.lang.String)
     */
    public void reportEntry(String type, String entry) {
        currentCL.entries.add(new CL.Entry(type, entry));
    }
    /*
     * (non-Javadoc)
     *
     * @see org.apache.tools.ant.taskdefs.ClassLoaderReporter#reportEntry(java.net.URL)
     */
    public void reportEntry(URL url) {
        reportEntry("url", url.toString());
    }
    /*
     * (non-Javadoc)
     *
     * @see org.apache.tools.ant.taskdefs.ClassLoaderReporter#reportError(java.lang.String)
     */
    public void reportError(String msg) {
        errors.add(msg);
    }
    /*
     * (non-Javadoc)
     *
     * @see org.apache.tools.ant.taskdefs.ClassLoaderReporter#reportExlicitelyParent(org.apache.tools.ant.taskdefs.ClassloaderBase.ReportHandle)
     */
    public void reportExlicitelyParent(ClassloaderReportHandle handle) {
        currentCL.parent = handle;
        currentCL.expliciteParent = true;
    }
    /*
     * (non-Javadoc)
     *
     * @see org.apache.tools.ant.taskdefs.ClassLoaderReporter#reportImplicitelyParent(org.apache.tools.ant.taskdefs.ClassloaderBase.ReportHandle)
     */
    public void reportImplicitelyParent(ClassloaderReportHandle handle) {
        currentCL.parent = handle;
        currentCL.expliciteParent = false;
    }
    /*
     * (non-Javadoc)
     *
     * @see org.apache.tools.ant.taskdefs.ClassLoaderReporter#reportPackage(java.lang.String)
     */
    public void reportPackage(String pkg) {
        currentCL.packages.add(pkg);
    }
    /*
     * (non-Javadoc)
     *
     * @see org.apache.tools.ant.taskdefs.ClassLoaderReporter#reportRole(org.apache.tools.ant.taskdefs.ClassloaderBase.ReportHandle)
     */
    public void reportRole(ClassloaderReportHandle handle) {
        currentCL.roles.add(handle);
    }
    /*
     * (non-Javadoc)
     *
     * @see org.apache.tools.ant.taskdefs.ClassLoaderReporter#reportUnassignedPopularLoader(org.apache.tools.ant.taskdefs.ClassloaderBase.ReportHandle)
     */
    public void reportUnassignedRole(ClassloaderReportHandle handle) {
        unassigned.add(handle);
    }
}