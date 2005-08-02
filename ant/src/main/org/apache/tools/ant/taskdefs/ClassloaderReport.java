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
import org.apache.tools.ant.taskdefs.classloader.report.ClassloaderReportFlattenBuilder;
import org.apache.tools.ant.taskdefs.classloader.report.ClassloaderReportFormatter;
import org.apache.tools.ant.taskdefs.classloader.report.ClassloaderReportTextFormatter;
import org.apache.tools.ant.taskdefs.classloader.report.ClassloaderReportUtil;
import org.apache.tools.ant.taskdefs.classloader.report.ClassloaderReportBuilder;
import org.apache.tools.ant.taskdefs.classloader.report.ClassloaderReportHandle;
import org.apache.tools.ant.taskdefs.classloader.report.ClassloaderReporter;
import org.apache.tools.ant.taskdefs.classloader.report.ClassloaderReportXMLFormatter;
import org.apache.tools.ant.taskdefs.classloader.report.FormattedAntLoggerReporter;
import org.apache.tools.ant.taskdefs.classloader.report.ClassloaderReportTreeBuilder;
import org.apache.tools.ant.taskdefs.classloader.report.FormattedPrintStreamReporter;
import org.apache.tools.ant.types.EnumeratedAttribute;
/**
 * Creates a report for all currently used classloaders.
 * @since Ant1.7
 */
public class ClassloaderReport extends ClassloaderBase implements
        ClassloaderContext.Report {
    /**
     * Enumeration for the values of format attribute.
     */
    public static class Format extends EnumeratedAttribute {
        private static final int XML = 0;
        private static final int TXT = 1;
        /**
         * Default Constructor.
         */
        public Format() {
        }
        /**
         * Value'd Constructor.
         *
         * @param value
         *            One of enumerated values.
         */
        public Format(String value) {
            setValue(value);
        }
        /**
         * Get the logging level for reporting duplicate entries.
         *
         * @return Logging level for reporting duplicate entries.
         */
        public ClassloaderReportFormatter newFormatter() {
            switch (getIndex()) {
            case XML:
                return new ClassloaderReportXMLFormatter();
            case TXT:
                return new ClassloaderReportTextFormatter();
            default:
                return null;
            }
        }
        /**
         * Get the logging level for reporting duplicate entries.
         *
         * @return Logging level for reporting duplicate entries.
         */
        public Hierarchy getDefaultHierarchy() {
            switch (getIndex()) {
            case XML:
                return new Hierarchy("tree");
            case TXT:
                return new Hierarchy("flat");
            default:
                return null;
            }
        }
        /**
         * Get the values.
         *
         * @return An array of the allowed values for this attribute.
         */
        public String[] getValues() {
            return new String[] {"xml", "txt"};
        }
    }
    /**
     * Enumeration for the values of format attribute.
     */
    public static class Hierarchy extends EnumeratedAttribute {
        private static final int FLAT = 0;
        private static final int TREE = 1;
        /**
         * Default Constructor.
         */
        public Hierarchy() {
        }
        /**
         * Value'd Constructor.
         *
         * @param value
         *            One of enumerated values.
         */
        public Hierarchy(String value) {
            setValue(value);
        }
        /**
         * Get the logging level for reporting duplicate entries.
         * @param context The context.
         * @return Logging level for reporting duplicate entries.
         */
        public ClassloaderReportBuilder newBuilder(ClassloaderContext.Report context) {
            switch (getIndex()) {
            case FLAT:
                return new ClassloaderReportFlattenBuilder(context);
            case TREE:
                return new ClassloaderReportTreeBuilder(context);
            default:
                return null;
            }
        }
        /**
         * Get the values.
         *
         * @return An array of the allowed values for this attribute.
         */
        public String[] getValues() {
            return new String[] {"flat", "tree"};
        }
    }
    private Format format = null;
    private Hierarchy hierarchy = null;
    private boolean reportPackages = true;
    private File output = null;
    /**
     * Default constructor.
     */
    public ClassloaderReport() {
        super();
        setFailonerror(false);
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
        if (format == null) {
            format = (output == null) ? new Format("txt") : new Format("xml");
        }
        if (hierarchy == null) {
            hierarchy = format.getDefaultHierarchy();
        }
        ClassloaderReportBuilder to = hierarchy.newBuilder(this);
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
        reportUtil.report(this, handlesByLoader, loaderByHandle, to, addSuccess);
        ClassloaderReporter destReporter;
        
        if (output != null) {
            try {
                destReporter = new FormattedPrintStreamReporter(
                    format.newFormatter(),
                    new PrintStream(output));
            } catch (IOException e) {
                throw new BuildException(e);
            }
        } else {
            destReporter = new FormattedAntLoggerReporter(this,
                    format.newFormatter());
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
    public void setDestfile(File file) {
        this.output = file;
    }
    /**
     * Sets the format.
     * @param f The format.
     */
    public void setFormat(Format f) {
        this.format = f;
    }
    /**
     * Sets the hierarchy.
     * @param h The hierarchy.
     */
    public void setHierarchy(Hierarchy h) {
        this.hierarchy = h;
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
