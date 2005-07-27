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

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.tools.ant.AntTypeDefinition;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ComponentHelper;
import org.apache.tools.ant.taskdefs.classloader.ClassloaderContext;
import org.apache.tools.ant.taskdefs.classloader.report.ClassloaderReportUtil;
import org.apache.tools.ant.taskdefs.classloader.report.ClassloaderReportBuilder;
import org.apache.tools.ant.taskdefs.classloader.report.ClassloaderReportHandle;
import org.apache.tools.ant.taskdefs.classloader.report.ClassloaderReporter;
import org.apache.tools.ant.taskdefs.classloader.report.ClassloaderReportXMLFormatter;
import org.apache.tools.ant.taskdefs.classloader.report.FormattedAntLoggerReporter;
import org.apache.tools.ant.taskdefs.classloader.report.ClassloaderReportTreeBuilder;
import org.apache.tools.ant.taskdefs.classloader.report.FormattedPrintStreamReporter;
/**
 * Creates a report for all currently used classloaders.
 * @since Ant1.7
 */
public class ClassloaderReport extends ClassloaderBase implements
        ClassloaderContext.Report {

    private boolean reportPackages = false;
    private File output = null;
    /**
     * Default constructor.
     */
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
        ClassloaderReportBuilder to = new ClassloaderReportTreeBuilder();
        boolean addSuccess = true;
        ClassLoader extCl = ClassLoader.getSystemClassLoader().getParent();
        ClassloaderReportUtil reportUtil = ClassloaderReportUtil
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
                                ClassloaderReportHandle.ANT_REFERENCED, rNames[i]),
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
                                ClassloaderReportHandle.ANT_DEFINED, rNames[i]),
                        handlesByLoader, loaderByHandle, to)) {
                    addSuccess = false;
                }
            }
        }
        rNames = null;
        reportUtil
                .report(this, handlesByLoader, loaderByHandle, to, addSuccess);
        ClassloaderReporter destReporter;
        if (output == null) {
            try {
                destReporter = new FormattedPrintStreamReporter(
                    new ClassloaderReportXMLFormatter(),
                    new PrintStream(output));
            } catch (IOException e) {
                throw new BuildException(e);
            }
        } else {
            destReporter = new FormattedAntLoggerReporter(this,
                    new ClassloaderReportXMLFormatter());
        }
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
     * Sets the output file.
     * @param file Output file.
     */
    public void setOutput(File file) {
        this.output = file;
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
