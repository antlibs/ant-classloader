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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.tools.ant.AntTypeDefinition;
import org.apache.tools.ant.ComponentHelper;
import org.apache.tools.ant.taskdefs.classloader.ClassLoaderAdapterContext;
import org.apache.tools.ant.taskdefs.classloader.report.ClassLoaderReportUtil;
import org.apache.tools.ant.taskdefs.classloader.report.ClassloaderReportHandle;
import org.apache.tools.ant.taskdefs.classloader.report.ClassloaderReporter;
import org.apache.tools.ant.taskdefs.classloader.report.ClassloaderXMLFormatter;
import org.apache.tools.ant.taskdefs.classloader.report.FormattedAntLoggerReporter;
import org.apache.tools.ant.taskdefs.classloader.report.TreeBuilderReporter;

public class ClassloaderReport extends ClassloaderBase implements
        ClassLoaderAdapterContext.Report {

    private boolean reportPackages = false;
    public ClassloaderReport() {
        super();
    }
    /**
     * handle the report.
     */
    public void execute() {
        // let's hope, that no classloader implementation overrides
        // equals/hashCode
        // for 1.4 IdentityHashMap should be used for handlesByLoader
        HashMap handlesByLoader = new HashMap();
        TreeMap loaderByHandle = new TreeMap();
        // fileoutput and xml-format to be implemented.
        TreeBuilderReporter to = new TreeBuilderReporter();
        boolean addSuccess = true;
        ClassLoader extCl = ClassLoader.getSystemClassLoader().getParent();
        ClassLoaderReportUtil reportUtil = ClassLoaderReportUtil
                .getReportUtil();
        if ((extCl != null)
                && (extCl.getClass().getName()
                        .equals("sun.misc.Launcher$ExtClassLoader"))) {
            if (!reportUtil.addLoaderToReport(this, extCl,
                    ClassloaderReportHandle.EXTENSIONHANDLE, handlesByLoader,
                    loaderByHandle, to)) {
                addSuccess = false;
            }
        }
        if (!reportUtil.addLoaderToReport(this, ClassLoader
                .getSystemClassLoader(), ClassloaderReportHandle.SYSTEMHANDLE,
                handlesByLoader, loaderByHandle, to)) {
            addSuccess = false;
        }
        if (!reportUtil.addLoaderToReport(this, getProject().getClass()
                .getClassLoader(), ClassloaderReportHandle.PROJECTHANDLE,
                handlesByLoader, loaderByHandle, to)) {
            addSuccess = false;
        }
        if (!reportUtil.addLoaderToReport(this, getClass().getClassLoader(),
                ClassloaderReportHandle.CURRENTHANDLE, handlesByLoader,
                loaderByHandle, to)) {
            addSuccess = false;
        }
        if (!reportUtil.addLoaderToReport(this, Thread.currentThread()
                .getContextClassLoader(), ClassloaderReportHandle.THREADHANDLE,
                handlesByLoader, loaderByHandle, to)) {
            addSuccess = false;
        }
        if (!reportUtil.addLoaderToReport(this, getProject().getCoreLoader(),
                ClassloaderReportHandle.COREHANDLE, handlesByLoader,
                loaderByHandle, to)) {
            addSuccess = false;
        }
        String[] rNames = (String[]) getProject().getReferences().keySet()
                .toArray(new String[getProject().getReferences().size()]);
        Arrays.sort(rNames);
        for (int i = 0; i < rNames.length; i++) {
            Object val = getProject().getReference(rNames[i]);
            if (val instanceof ClassLoader) {
                if (!reportUtil.addLoaderToReport(this, (ClassLoader) val,
                        new ClassloaderReportHandle(
                                ClassloaderReportHandle.REFERENCED, rNames[i]),
                        handlesByLoader, loaderByHandle, to)) {
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
                if (!reportUtil.addLoaderToReport(this, val.getClassLoader(),
                        new ClassloaderReportHandle(
                                ClassloaderReportHandle.DEFINED, rNames[i]),
                        handlesByLoader, loaderByHandle, to)) {
                    addSuccess = false;
                }
            }
        }
        rNames = null;
        reportUtil
                .report(this, handlesByLoader, loaderByHandle, to, addSuccess);
        ClassloaderReporter destReporter = new FormattedAntLoggerReporter(this,
                new ClassloaderXMLFormatter());
        to.execute(destReporter);

    }
    /**
     * Indicates whether packages should been reported
     *
     * @return <code>true</code>, if packages should been reported, else
     *         <code>false</code>.
     */
    public boolean isReportPackages() {
        return reportPackages;
    }
    /**
     * Sets the reportPackages attribute.
     *
     * @param onOff
     *            Indicates whether to include packages in the report or not.
     *            Defaults to <code>false</code>.
     */
    public void setReportpackages(boolean onOff) {
        reportPackages = onOff;
    }

}
