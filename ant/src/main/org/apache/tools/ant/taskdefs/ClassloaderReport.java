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
package org.apache.tools.ant.taskdefs;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.tools.ant.AntTypeDefinition;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ComponentHelper;
import org.apache.tools.ant.taskdefs.classloader.ClassLoaderAdapter;
import org.apache.tools.ant.taskdefs.classloader.ClassLoaderAdapterAction;
import org.apache.tools.ant.taskdefs.classloader.ClassLoaderAdapterContext;
import org.apache.tools.ant.taskdefs.classloader.ClassloaderUtil;
import org.apache.tools.ant.taskdefs.classloader.report.ClassLoaderReportUtil;
import org.apache.tools.ant.taskdefs.classloader.report.ClassloaderReportHandle;
import org.apache.tools.ant.taskdefs.classloader.report.ClassloaderReporter;
import org.apache.tools.ant.taskdefs.classloader.report.ClassloaderXMLFormatter;
import org.apache.tools.ant.taskdefs.classloader.report.FormattedAntLoggerReporter;
import org.apache.tools.ant.taskdefs.classloader.report.TreeBuilderReporter;

public class ClassloaderReport extends ClassloaderBase implements ClassLoaderAdapterContext.Report {

    private boolean reportPackages = false;
    public ClassloaderReport() {
        super();
        // TODO Auto-generated constructor stub
    }
    /**
     * handle the report.
     */
    public void execute() {
        //let's hope, that no classloader implementation overrides
        // equals/hashCode
        //for 1.4 IdentityHashMap should be used for loaderStack
        HashMap handlesByLoader = new HashMap();
        TreeMap loaderByHandle = new TreeMap();
        //fileoutput and xml-format to be implemented.
        TreeBuilderReporter to = new TreeBuilderReporter();
        boolean addSuccess = true;
        ClassLoader extCl=ClassLoader.getSystemClassLoader().getParent();
        if((extCl!=null)&&(extCl.getClass().getName().equals("sun.misc.Launcher$ExtClassLoader"))) {
            if (!addLoaderToReport(
               extCl,
                    ClassloaderReportHandle.EXTENSIONHANDLE,
                    handlesByLoader,
                    loaderByHandle, to)) {
                    addSuccess = false;
                }
        }
        if (!addLoaderToReport(
            ClassLoader.getSystemClassLoader(),
            ClassloaderReportHandle.SYSTEMHANDLE,
            handlesByLoader,
            loaderByHandle, to)) {
            addSuccess = false;
        }
        if (!addLoaderToReport(
            getProject().getClass().getClassLoader(),
            ClassloaderReportHandle.PROJECTHANDLE,
            handlesByLoader,
            loaderByHandle, to)) {
            addSuccess = false;
        }
        if (!addLoaderToReport(
            getClass().getClassLoader(),
            ClassloaderReportHandle.CURRENTHANDLE,
            handlesByLoader,
            loaderByHandle, to)) {
            addSuccess = false;
        }
        if (!addLoaderToReport(
            Thread.currentThread().getContextClassLoader(),
            ClassloaderReportHandle.THREADHANDLE,
            handlesByLoader,
            loaderByHandle, to)) {
            addSuccess = false;
        }
        if (!addLoaderToReport(
            getProject().getCoreLoader(),
            ClassloaderReportHandle.COREHANDLE,
            handlesByLoader,
            loaderByHandle, to)) {
            addSuccess = false;
        }
        String[] rNames =
            (String[]) getProject().getReferences().keySet().toArray(
                new String[getProject().getReferences().size()]);
        Arrays.sort(rNames);
        for (int i = 0; i < rNames.length; i++) {
            Object val = getProject().getReference(rNames[i]);
            if (val instanceof ClassLoader) {
                if (!addLoaderToReport(
                    (ClassLoader) val,
                    new ClassloaderReportHandle(ClassloaderReportHandle.REFERENCED, rNames[i]),
                    handlesByLoader,
                    loaderByHandle, to)) {
                    addSuccess = false;
                }
            }
        }
        ComponentHelper ch = ComponentHelper.getComponentHelper(getProject());
        Map types = ch.getAntTypeTable();
        rNames = (String[]) types.keySet().toArray(new String[types.size()]);
        Arrays.sort(rNames);
        for (int i = 0; i < rNames.length; i++) {
            AntTypeDefinition val = ch.getDefinition(rNames[i]);
            if (val.getClassLoader() != null) {
                if (!addLoaderToReport(
                    val.getClassLoader(),
                    new ClassloaderReportHandle(ClassloaderReportHandle.DEFINED, rNames[i]),
                    handlesByLoader,
                    loaderByHandle, to)) {
                    addSuccess = false;
                }
            }
        }
        rNames = null;

        to.beginReport();
        if (!addSuccess) {
            to.reportError("WARNING: As of missing Loaderhandlers, this report might not be complete.");
        }
        URL[] urls = ClassloaderUtil.getBootstrapClasspathURLs();
        if (urls==null) {
           to.reportError("WARNING: Unable to determine bootstrap classpath."
                       +"\n         Please report this error to Ant's bugtracking system with information"
                       +"\n         about your environment (JVM-Vendor, JVM-Version, OS, application context).");
        } else {
            to.beginClassloader(ClassloaderReportHandle.BOOTSTRAPHANDLE);
            to.beginEntries(urls.length);
            for (int i = 0; i < urls.length; i++) {
                to.reportEntry(urls[i]);
            }
            to.endEntries(urls.length);
            to.endClassloader(ClassloaderReportHandle.BOOTSTRAPHANDLE);
        }
        for (Iterator iRole = loaderByHandle.keySet().iterator(); iRole.hasNext();) {
            ClassloaderReportHandle role = (ClassloaderReportHandle) iRole.next();
            ClassLoader cl = (ClassLoader) loaderByHandle.get(role);
            if (cl == null) {
                if (role.isPopular()) {
                    to.reportUnassignedRole(role);
                }
            } else {
                SortedSet handles = (SortedSet) handlesByLoader.get(cl);
                if (role.equals(handles.first())) {
                    report(to, cl, role, handlesByLoader);
                }
            }
        }
        to.endReport();
        ClassloaderReporter destReporter = new FormattedAntLoggerReporter(this, new ClassloaderXMLFormatter());
        to.execute(destReporter);        

    }
    /**
     * handle the report for a single classloader
     * @param to Reporter to report
     * @param cl ClassloaderBase instance to report
     * @param name name of the classloader instance.
     */
    public void report(ClassloaderReporter to, ClassLoader cl, ClassloaderReportHandle name, Map handlesByLoader) {
        to.beginClassloader(name);
        ClassLoaderAdapter baseAdapter = ClassloaderUtil.findAdapter(this, cl, null, to, "parent for "+name, "");
        if (baseAdapter != null) {
            ClassLoader parent = baseAdapter.getParent(cl);
            
            if (parent != null) {
                SortedSet handles = (SortedSet) handlesByLoader.get(parent); 
                to.reportExlicitelyParent((ClassloaderReportHandle)handles.first());
            } else {
                parent = baseAdapter.getDefaultParent();
                if (parent != null) {
                    SortedSet handles = (SortedSet) handlesByLoader.get(parent);
                    to.reportImplicitelyParent((ClassloaderReportHandle)handles.first());
                } else {
                    to.reportImplicitelyParent(ClassloaderReportHandle.BOOTSTRAPHANDLE);
                }
            }
        }
        to.reportClass(cl.getClass());
        SortedSet roles = (SortedSet) handlesByLoader.get(cl);
        for (Iterator iRole = roles.iterator(); iRole.hasNext();) {
            to.reportRole((ClassloaderReportHandle)iRole.next());
        }
        ClassLoaderAdapter adapter = ClassloaderUtil.findAdapter(this, cl, ClassLoaderAdapterAction.GETPATH, to, "entries for "+name, "");
        if (adapter != null) {
            String[] cp = adapter.getClasspath(this, cl, false);
            if (cp == null) {
                to.reportError("entries for "+name+" not investigatable (adapter retrieves no path)");
            } else {
                to.beginEntries(cp.length);
                for (int i = 0; i < cp.length; i++) {
                    to.reportEntry("url", cp[i]);
                }
            }
        }
        if (isReportPackages())
            ClassLoaderReportUtil.reportPackages(to, this, baseAdapter, cl, name);
        adapter =  ClassloaderUtil.findAdapter(this, cl, ClassLoaderAdapterAction.REPORT, to , "additional parameters for " + name, "");
        if (adapter != null) {
            adapter.report(to, this, cl, name);
        }
    }
    /**
     * Callback method for ClassLoaderAdapters to add classloaders
     * to the list of loaders to report.
     * @param cl The classloader instance to add.
     * @param name The name of the classloader instance.
     * @param loaderStack A list of loader names by instance.
     * @param loaderNames A list of loader instances by name.
     * @return <code>true</code>, if successfully executed, <code>false</code> otherwise.
     */
    public boolean addLoaderToReport(
        ClassLoader cl,
        ClassloaderReportHandle role,
        Map/*<ClassLoader,SortedSet<ReportHandle>*/ handlesByLoader,
        Map/*<ReportHandle,ClassLoader>*/ loaderByHandle,
        ClassloaderReporter to) {
        Object old = loaderByHandle.put(role, cl);
        if (old != null) {
            throw new BuildException("duplicate classloader " + role);
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
                ClassLoaderAdapter adapter = ClassloaderUtil.findAdapter(this, cl, null, to, role+"->parent", role.getName());
                boolean adapterFound = (adapter!= null);
                if (adapterFound) {
                    ClassLoader parent = adapter.getParent(cl);
                    if (parent == null) {
                        parent = adapter.getDefaultParent();
                    }
                    if (parent != null) {
                        addLoaderToReport(adapter.getParent(cl),new ClassloaderReportHandle(ClassloaderReportHandle.PARENT, role.toString()),handlesByLoader,loaderByHandle,to);
                    }
                }
                adapter = ClassloaderUtil.findAdapter(this, cl, ClassLoaderAdapterAction.REPORT, to, "report for " + role, "");
                if (adapter != null) {
                    adapter.addReportable(this, cl, role, handlesByLoader, loaderByHandle);
                }
                return adapterFound && (adapter != null);
            }
        }
        return true;
    }
    /**
     * Indicates whether packages should been reported
     * @return <code>true</code>, if packages should been reported, else <code>false</code>.
     */
    public boolean isReportPackages() {
        return reportPackages;
    }
    /**
     * Sets the reportPackages attribute.
     * @param onOff Indicates whether to include packages in the report or not. 
     * Defaults to <code>false</code>.
     */
    public void setReportpackages(boolean onOff) {
        reportPackages = onOff;
    }

}
