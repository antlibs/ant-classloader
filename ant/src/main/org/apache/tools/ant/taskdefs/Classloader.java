/*
 * Copyright  2002-2005 The Apache Software Foundation
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

import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.AntTypeDefinition;
import org.apache.tools.ant.ComponentHelper;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.AntLoaderParameters;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.LoaderParameters;
import org.apache.tools.ant.types.LoaderHandler;
import org.apache.tools.ant.types.LoaderHandlerSet;
import org.apache.tools.ant.types.LoaderRef;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.URLPath;

/**
 * Create or modifies ClassLoader.
 *
 * The classpath is a regular path.
 *
 * Taskdef and typedef can use the loader you create with the loaderRef attribute.
 *
 * This tasks will not modify the core loader, the project loader
 * or the system loader if "build.sysclasspath=only"
 *
 * The typical use is:
 * <pre>
 *  &lt;path id="ant.deps" &gt;
 *     &lt;fileset dir="myDir" &gt;
 *        &lt;include name="junit.jar, bsf.jar, js.jar, etc"/&gt;
 *     &lt;/fileset&gt;
 *  &lt;/path&gt;
 *
 *  &lt;classloader loader="project" classpathRef="ant.deps" /&gt;
 *
 * </pre>
 *
 * @since Ant 1.7
 */
public class Classloader extends Task {
    /**
     * Actions for ClassLoaderAdapter.
     */
    public static final class Action {
        private static final int IDCREATE = 1;
        private static final int IDAPPEND = 2;
        private static final int IDGETPATH = 3;
        private static final int IDREPORT = 4;
        /**
         * Append Path to an existing ClassLoader instance.
         */
        public static final Action APPEND = new Action(IDAPPEND);
        /**
         * Create a new ClassLoader instance.
         */
        public static final Action CREATE = new Action(IDCREATE);
        /**
         * Get the path of an existing ClassLoader instance.
         */
        public static final Action GETPATH = new Action(IDGETPATH);
        /**
         * Get additional Report information.
         */
        public static final Action REPORT = new Action(IDREPORT);

        private final int value;
        private Action(int value) {
            this.value = value;
        }
    }
    /**
     * ClassLoaderAdapter used to define classloader interaction.
     */
    public static interface ClassLoaderAdapter {
        /**
         * add classloader to the report queue.
         * the adapter should call task.addLoaderToReport to add a loader.
         * @param task the calling Classloader-task.
         * @param classloader the classloader to analyze.
         * @param name the name of the classloader instance.
         * @param loaderStack loaderStack to pass to Classloader.addLoaderToReport.
         * @param loaderNames loaderNames to pass to Classloader.addLoaderToReport.
         */
        void addReportable(
            Classloader task,
            ClassLoader classloader,
            String name,
            Map loaderStack,
            Map loaderNames);
        /**
         * Appends a classpath to an existing classloader instance.
         * @param task the calling Classloader-task.
         * @param classloader the classloader instance to append the path to.
         * @return The ClassLoader instance or null if an error occured.
         */
        boolean appendClasspath(Classloader task, ClassLoader classloader);
        /**
         * Creates a classloader instance.
         * @param task the calling Classloader-task.
         * @return the newly created ClassLoader instance or null if an error occured.
         */
        ClassLoader createClassLoader(Classloader task);
        /**
         * Returns the actual classpath of a classloader instance.
         * @param task the calling Classloader-task.
         * @param classloader the classloader instance to get the path from.
         * @param defaultToFile if true, returned url-elements with file protocol
         *         should trim the leading 'file:/' prefix.
         * @return the path or null if an error occured
         */
        String[] getClasspath(
            Classloader task,
            ClassLoader classloader,
            boolean defaultToFile);
        /**
         * Checks whether the adapter supports an action.
         * @param action the action to check.
         * @return true, if action is supported.
         */
        boolean isSupported(Action action);
        /**
         * performs additional reporting.
         * @param to the Reporter Object to report to.
         * @param task the calling Classloader-task.
         * @param classloader the classloader instance to report about.
         * @param name the name of the classloader instance.
         */
        void report(
            Reporter to,
            Classloader task,
            ClassLoader classloader,
            String name);
        /**
         * Gets the default parent classloader in the case, this classloader's
         * <code>getParent()</code> method returns <code>null</code>. 
         * @return The classloader implicitely used as parent loader or null
         * if the bootstrap loader is the implicitely parent loader. 
         */
        ClassLoader getDefaultParent();
    }
    /**
     * Mandatory Interface for ClassLoaderParameters.
     */
    public static interface ClassLoaderParameters {
        /**
         * returns the default handler for this descriptor.
         * @return handler.
         */
        LoaderHandler getDefaultHandler();
        /**
         * returns the valuable parameter object which is either the instance itself
         * or the resolved referenced parameters.
         * @return parameters.
         */
        ClassLoaderParameters getParameters();
    }
    /**
     * Enumeration for the values of duplicateEntry attribute.
     */
    public static class DuplicateEntry extends EnumeratedAttribute {
        /** Enumerated values */
        private static final int IGNORE = 0,
            WARN = 1,
            OMIT = 2;
        /**
         * Default Constructor.
         */
        public DuplicateEntry() {
        }
        /**
         * Value'd Constructor.
         * @param value One of enumerated values.
         */
        public DuplicateEntry(String value) {
            setValue(value);
        }
        /**
         * Get the values.
         * @return An array of the allowed values for this attribute.
         */
        public String[] getValues() {
            return new String[] {
                "ignore",
                "warn",
                "omit" };
        }
        /**
         * Indicates whether duplicate entries needs to be checked.
         * @return <code>true</code>, if duplicate entries needs to be checked,
         * <code>false</code> otherwise. 
         */
        public boolean requiresCheck() {
            return (getIndex()!=IGNORE); 
        }
        /**
         * Indicates whether duplicate entries should be omitted.
         * @return <code>true</code>, if duplicate entries should be omitted,
         * <code>false</code> otherwise. 
         */
        public boolean isOmitDuplicate() {
            return (getIndex()!=OMIT); 
        }
        /**
         * Get the logging level for reporting duplicate entries.
         * @return Logging level for reporting duplicate entries. 
         */
        public int getDuplicateLogLevel() {
            switch (getIndex()) {
            case OMIT:
                return Project.MSG_VERBOSE;
            case WARN:
                return Project.MSG_WARN;
            default:
                return -1;
            }
        }
    }
    /**
     * makes reporting destination transparent for reporting objects.
     */
    public static class Reporter {
        private PrintStream stream;
        private Classloader task;
        Reporter(Classloader task, PrintStream stream) {
            this.task = task;
            this.stream = stream;
        }
        /**
         * writes a message line to the reporting dest.
         * @param s the message line to report.
         */
        public void report(String s) {
            if (stream != null) {
                stream.println(s);
            } else {
                task.log(s, Project.MSG_INFO);
            }
        }
    }

    private URLPath classpath = null;
    private DuplicateEntry duplicateEntry = new DuplicateEntry("omit");
    private ClassLoaderParameters parameters = null;
    private boolean failOnError;
    private LoaderHandler handler = null;
    private LoaderHandlerSet handlerSet = null;
    private LoaderRef loader = null;
    private String loaderName = null;
    private LoaderRef parentLoader = null;
    private String property = null;
    private boolean report = false;
    private boolean reportPackages = false;
    private boolean reset = false;
    private LoaderRef superLoader = null;
    /**
     * Default constructor
     */
    public Classloader() {
    }
    /**
     * Sets a nested Descriptor element for an AntClassLoader.
     * @param desc the parameters.
     */
    public void addAntParameters(AntLoaderParameters desc) {
        parameters = desc;
    }
    /**
     * Sets a nested LoaderHandler element.
     * @param handler the loaderHandler.
     */
    public void addConfiguredHandler(LoaderHandler handler) {
        handler.check();
        if (this.handler != null) {
            throw new BuildException("nested element handler can only specified once");
        }
        this.handler = handler;
    }
    /**
     * Sets a nested LoaderHandler element.
     * @param handler The loaderHandler.
     */
    public void setHandler(LoaderHandler handler) {
        handler.check();
        this.handler = handler;
    }
    /**
     * Sets a nested ClassLoaderParameters element.
     * @param desc The parameters.
     */
    public void addParameters(LoaderParameters desc) {
        parameters = desc;
    }
    /**
     * Sets a nested HandlerSet element.
     * @param handlerSet The handlerSet
     */
    public void addHandlerSet(LoaderHandlerSet handlerSet) {
        if (this.handlerSet != null) {
            throw new BuildException("nested element handlerSet may only specified once");
        }
        this.handlerSet = handlerSet;
    }
    /**
     * Sets a HandlerSet ref.
     * @param handlerSet The handlerSet
     */
    public void setHandlerSet(LoaderHandlerSet handlerSet) {
        this.handlerSet = handlerSet;
    }
    /**
     * Sets a nested loader element.
     * @param loader The loader definition.
     */
    public void addLoader(LoaderRef loader) {
        if (loader.isStandardLoader(LoaderRef.LoaderSpec.NONE)) {
            throw new BuildException("nested element loader can not be 'none'");
        }
        this.loader = loader;
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
        String name,
        Map loaderStack,
        Map loaderNames) {
        if (cl == null) {
            Object old = loaderNames.put(name, null);
            if (old != null) {
                throw new BuildException("duplicate classloader name " + name);
            }
        } else {
            Object old = loaderNames.put(name, cl);
            if (old != null) {
                throw new BuildException("duplicate classloader name " + name);
            }
            old = loaderStack.get(cl);
            boolean isNew = (old == null);
            if (old == null) {
                old = new ArrayList();
                loaderStack.put(cl, old);
            }
            ((ArrayList) old).add(name);

            if (isNew) {
                addLoaderToReport(
                    cl.getParent(),
                    name + "->parent",
                    loaderStack,
                    loaderNames);

                LoaderHandlerSet handlerSet = getHandlerSet();
                if (handlerSet == null) {
                    return false;
                }
                LoaderHandler handler =
                    handlerSet.getHandler(this, cl, Action.REPORT);
                if (handler == null) {
                    return false;
                }
                ClassLoaderAdapter adapter = handler.getAdapter(this);
                if (adapter == null) {
                    return false;
                }
                adapter.addReportable(this, cl, name, loaderStack, loaderNames);
            }
        }
        return true;
    }
    /**
     * Sets the nested parentLoader element.
     * @param loader The parentLoader
     */
    public void addParentLoader(LoaderRef loader) {
        this.parentLoader = loader;
    }
    /**
     * Sets the nested superLoader element.
     * @param loader The superLoader.
     */
    public void addSuperLoader(LoaderRef loader) {
        this.parentLoader = loader;
    }
    /**
     * creates a nested classpath element.
     * @return the classpath.
     */
    public URLPath createClasspath() {
        if (this.classpath == null) {
            this.classpath = new URLPath(getProject());
        }
        return this.classpath.createUrlpath();
    }
    /**
     * Executes this task.
     */
    public void execute() {
        if (report) {
            executeReport();
            return;
        }
        if (loader == null) {
            throw new BuildException("no loader specified");
        }
        if (!executeCreateModify()) {
            return;
        }
        if (property != null) {
            this.executeProperty();
        }
    }
    private boolean executeCreateModify() {
        URLPath classPath = getClasspath();
        ClassLoader classloader = null;
        // Are any other references held ? Can we 'close' the loader
        // so it removes the locks on jars ?
        // Can we replace the system classloader by just changing the
        // referenced object?
        // however, is reset really useful?
        if (!reset) {
            classloader = loader.getClassLoader(null, false, true);
        }

        boolean create = (classloader == null);
        boolean modify = ((classloader != null) && (classPath != null));
        if (!(create || modify)) {
            return true;
        }

        // Gump friendly - don't mess with the core loader if only classpath
        if ("only".equals(getProject().getProperty("build.sysclasspath"))
         && loader.equalsSysLoader()) {
            log("Changing " + loader.getName() + " is disabled "
                    + "by build.sysclasspath=only",
                Project.MSG_WARN);
            return true;
        }

        if (reset && !loader.isResetPossible()) {
            this.handleError("reseting " + loader.getName() + " is not possible");
            return false;
        }
        if (create && !loader.isResetPossible()) {
            this.handleError("creating " + loader.getName() + " is not possible");
            return false;
        }
        log(
            "handling "
                + this.getLoaderName()
                + ": "
                + ((classloader == null) ? "not " : "")
                + "found, cp="
                + this.getClasspath(),
            Project.MSG_DEBUG);
        LoaderHandlerSet handlerSet = null;
        if (classloader == null) {
            LoaderHandler handler = getHandler();
            if (handler == null) {
                throw new BuildException("internal error: handler is null");
            }
            ClassLoaderAdapter adapter = handler.getAdapter(this);
            if (adapter == null) {
                return false;
            }
            classloader = adapter.createClassLoader(this);
            if (classloader == null) {
                return false;
            }
            loader.setClassLoader(classloader);
        } else if (classPath != null) {
            handlerSet = getHandlerSet();
            if (handlerSet == null) {
                throw new BuildException("internal error: handlerset is null");
            }
            LoaderHandler handler =
                handlerSet.getHandler(this, classloader, Action.APPEND);
            if (handler == null) {
                log("NO HANDLER", Project.MSG_DEBUG);
                return false;
            }
            ClassLoaderAdapter adapter = handler.getAdapter(this);
            if (adapter == null) {
                log("NO ADAPTER", Project.MSG_DEBUG);
                return false;
            }
            if (!adapter.appendClasspath(this, classloader)) {
                log("NO APPEND", Project.MSG_DEBUG);
                return false;
            }
        }
        return true;
    }
    private boolean executeProperty() {
        ClassLoader cl = loader.getClassLoader(null);
        LoaderHandlerSet handlerSet = getHandlerSet();
        if (handlerSet == null) {
            throw new BuildException("internal error: handlerset is null");
        }
        LoaderHandler handler =
            handlerSet.getHandler(this, cl, Action.GETPATH);
        if (handler == null) {
            return false;
        }
        ClassLoaderAdapter adapter = handler.getAdapter(this);
        if (adapter == null) {
            return false;
        }
        String[] propPath = adapter.getClasspath(this, cl, true);
        if (propPath == null) {
            return false;
        }
        StringBuffer propValue = new StringBuffer();
        if (propPath.length > 0) {
            propValue.append(propPath[0]);
        }
        for (int i = 1; i < propPath.length; i++) {
            propValue.append(';').append(propPath[i]);
        }
        getProject().setProperty(property, propValue.toString());
        return true;
    }
    private String formatIndex(int i) {
        String x = String.valueOf(i + 1);
        if (x.length() == 1) {
            return " " + x;
        }
        return x;
    }
    /**
     * Gets the classpath to add to a classloader.
     * @return The classpath.
     */
    public URLPath getClasspath() {
        return classpath;
    }
    /**
     * Gets the parameters for a newly created classloader.
     * @return The parameters
     */
    public ClassLoaderParameters getParameters() {
        if (parameters == null) {
            parameters = new LoaderParameters(getProject());
        }
        return parameters;
    }
    /**
     * Gets the handler to create a new classloader.
     * @return The handler
     */
    public LoaderHandler getHandler() {
        if (handler == null) {
            handler = getParameters().getDefaultHandler();
        }
        return handler;
    }
    /**
     * Gets the handlerset to analyze a given classloader with.
     * @return The handlerset.
     */
    public LoaderHandlerSet getHandlerSet() {
        if (handlerSet == null) {
            handlerSet = new LoaderHandlerSet(getProject());
            handlerSet.addConfiguredHandler(getHandler());
        }
        return handlerSet;
    }
    /**
     * Gets the name of the described classloader for logging and report purposes.
     * @return The name.
     */
    public String getLoaderName() {
        if (loaderName == null) {
            loaderName = loader.getName();
        }
        return loaderName;
    }
    /**
     * Gets the parent ClassLoader as defined via the parentLoader attribute.
     * @return parent ClassLoader or null if not defined.
     */
    public ClassLoader getParentLoader() {
        if (parentLoader == null) {
            return null;
        }
        return parentLoader.getClassLoader(null, failOnError, false);
    }
    /**
     * Gets the super classloader to create a new classloader with.
     * @return the super loader.
     */
    public ClassLoader getSuperLoader() {
        if (superLoader == null) {
            return getClass().getClassLoader();
        }
        return superLoader.getClassLoader(null, failOnError, false);
    }
    /**
     * Indicates whether packages should been reported
     * @return <code>true</code>, if packages should been reported, else <code>false</code>.
     */
    public boolean isReportPackages() {
        return reportPackages;
    }
    /**
     * Handles an error with respect to the failonerror attribute.
     * @param msg Error message.
     */
    public void handleError(String msg) {
        handleError(msg, null, null);
    }
    /**
     * Handles a classpath entry. 
     * @param entry The entry.
     * @return Indicates, whether the adapter should add the duplicate entry
     * to the existing classloader or not.
     */
    public boolean handleClasspathEntry(ClassLoader cl, String entryUrl) {
        if (!duplicateEntry.requiresCheck()) {
            return true;
        }
        if (!containsEntry(cl, entryUrl)) {
            return true;
        }
        int logLevel = duplicateEntry.getDuplicateLogLevel();
        if (logLevel >= 0) {
            log("duplicate classpath entry: " + entryUrl, logLevel);
        }
        return !duplicateEntry.isOmitDuplicate();
    }
    /**
     * Handles an error with respect to the failonerror attribute.
     * @param msg Error message.
     * @param ex Causing exception.
     */
    public void handleError(String msg, Throwable ex) {
        handleError(msg, ex, null);
    }
    /**
     * Handles an error with respect to the failonerror attribute.
     * @param msg Error message.
     * @param ex Causing exception.
     * @param loc Location.
     */
    public void handleError(String msg, Throwable ex, Location loc) {
        if (loc == null) {
            loc = this.getLocation();
        }
        if ((msg == null) && (ex != null)) {
            msg = ex.getMessage();
        }
        if (failOnError) {
            throw new BuildException(msg, ex, loc);
        } else {
            log(loc + "Error: " + msg, Project.MSG_ERR);
        }
    }
    private URL[] getBootstrapClasspathURLs() {
        try {
			Object urlClassPath=Class.forName("sun.misc.Launcher").getMethod("getBootstrapClassPath",null).invoke(null,null);
			return (URL[])urlClassPath.getClass().getMethod("getURLs",null).invoke(urlClassPath,null);
		} catch (Exception e) {
			return null;
		}
    }
    private boolean containsEntrySelf(ClassLoader cl, String url, List errors) {
        LoaderHandlerSet handlerSet = getHandlerSet();
        if (handlerSet == null) {
            throw new BuildException("internal error: handlerset is null");
        }
        LoaderHandler handler = handlerSet.getHandler(this, cl, Action.GETPATH);
        ClassLoaderAdapter adapter;
        if (handler == null) {
            errors.add("path for classloader "+cl.getClass().getName()+" not investigatable (no Loaderhandler found)");
            return false;
        } else {
            adapter = handler.getAdapter(this);
            if (adapter == null) {
                errors.add("path for classloader "+cl.getClass().getName()+" not investigatable (Loaderhandler retrieves no adapter)");
                return false;
            } else {
                //doing 'special' default parent handling here.
                //so we don't need to investigate the adapter in the calling method.
                if ((cl.getParent() == null) && (adapter.getDefaultParent() != null)) {
                    if (containsEntryDelegatedOrSelf(adapter.getDefaultParent(), url, errors)) {
                        return true;
                    }
                }
                //
                String[] cp = adapter.getClasspath(this, cl, false);
                if (cp == null) {
                    errors.add("path for classloader "+cl.getClass().getName()+" not investigatable (adapter retrieves no path)");
                    return false;
                } else {
                    for (int i=0; i < cp.length; i++) {
                        if (cp[i].equals(url)) {
                            return true;
                        }
                    }
                    return false;
                }
            }
        }
    }
    private boolean containsEntry(ClassLoader cl, String url) {
        ArrayList errors = new ArrayList();
        if (containsEntryDelegatedOrSelf(cl, url, errors)) {
            return true;
        }
        if (errors.size() > 0) {
            StringBuffer sb = new StringBuffer(50 * (1 + errors.size()));
            sb.append("Check for duplicate entries fails due to the following reason(s):");
            for (Iterator i = errors.iterator(); i.hasNext();) {
                sb.append("\n").append(i.next());
            }
            log(sb.toString(), Project.MSG_WARN);
        }
        return false;
    }
    /**
     * Checks whether an url is in the classpath of a classloader or
     * it's delegation hierarchy. <br>
     * NOTE: As of performance reasons, this method does not do the check
     * in the loading order (parentloader - childloader).
     * @param cl The classloader.
     * @param url The url.
     * @param errors A list of errors to report.
     * @return <code>true</code>, if the classloader or one of it's implicite
     * or explicite parents contains the url. <code>false</code> otherwise.
     */
    private boolean containsEntryDelegatedOrSelf(ClassLoader cl, String url, List errors) {
        if (cl==null) {
            URL[] urls=getBootstrapClasspathURLs();
            if (urls==null) {
                errors.add("bootstrap classpath not investigatable");
                return false;
            }
            for (int i=0; i < urls.length; i++) {
                if (urls[i].toString().equals(url)) {
                    return true;
                }
            }
            return false;
        }
        if (containsEntrySelf(cl, url, errors)) {
            return true;
        }
        return containsEntryDelegatedOrSelf(cl.getParent(), url, errors);
    }
    /**
     * handle the report.
     */
    protected void executeReport() {
        //let's hope, that no classloader implementation overrides
        // equals/hashCode
        //for 1.4 IdentityHashMap should be used for loaderStack
        HashMap loaderStack = new HashMap();
        HashMap loaderNames = new HashMap();
        boolean addSuccess = true;
        if (!addLoaderToReport(
            ClassLoader.getSystemClassLoader(),
            "1-SystemClassLoader",
            loaderStack,
            loaderNames)) {
            addSuccess = false;
        }
        if (!addLoaderToReport(
            getProject().getClass().getClassLoader(),
            "2-ProjectClassLoader",
            loaderStack,
            loaderNames)) {
            addSuccess = false;
        }
        if (!addLoaderToReport(
            getClass().getClassLoader(),
            "3-CurrentClassLoader",
            loaderStack,
            loaderNames)) {
            addSuccess = false;
        }
        if (!addLoaderToReport(
            Thread.currentThread().getContextClassLoader(),
            "4-ThreadContextClassLoader",
            loaderStack,
            loaderNames)) {
            addSuccess = false;
        }
        if (!addLoaderToReport(
            getProject().getCoreLoader(),
            "5-CoreLoader",
            loaderStack,
            loaderNames)) {
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
                    "6-id=" + rNames[i],
                    loaderStack,
                    loaderNames)) {
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
                    "7-def=" + rNames[i],
                    loaderStack,
                    loaderNames)) {
                    addSuccess = false;
                }
            }
        }
        rNames = null;
        String[] names =
            (String[]) loaderNames.keySet().toArray(
                new String[loaderNames.size()]);
        Arrays.sort(names);
        for (int i = names.length - 1; i >= 0; i--) {
            Object cl = loaderNames.get(names[i]);
            if (cl != null) {
                loaderStack.put(cl, names[i]);
            }
        }
        //fileoutput and xml-format to be implemented.
        Reporter to = new Reporter(this, null);

        to.report("---------- ClassLoader Report ----------");
        if (!addSuccess) {
            to.report("WARNING: As of missing Loaderhandlers, this report might not be complete.");
        }
        to.report(" ");
        URL[] urls = getBootstrapClasspathURLs();
        if (urls==null) {
           to.report("WARNING: Unable to determine bootstrap classpath.");
           to.report("         Please report this error to Ant's bugtracking system with information");
           to.report("         about your environment (JVM-Vendor, JVM-Version, OS, application context).");
        } else {
            to.report(" 0. bootstrap classpath: " + urls.length + " elements");
            for (int i = 0; i < urls.length; i++) {
                to.report("         > " + urls[i]);
            }
        }
        for (int i = 0; i < names.length; i++) {
            to.report(" ");
            ClassLoader cl = (ClassLoader) loaderNames.get(names[i]);
            if (cl == null) {
                to.report(
                    formatIndex(i)
                        + ". "
                        + names[i].substring(2)
                        + " is not assigned.");
            } else {
                Object n = loaderStack.get(cl);
                if (names[i].equals(n)) {
                    to.report(formatIndex(i) + ". " + names[i].substring(2));
                    report(to, cl, names[i].substring(2));
                } else {
                    to.report(
                        formatIndex(i)
                            + ". "
                            + names[i].substring(2)
                            + " = "
                            + ((String) n).substring(2)
                            + ". (See above.)");
                }
            }
        }
        to.report("---------- End Of ClassLoader Report ----------");
    }
    /**
     * handle the report for a single classloader
     * @param to Reporter to report
     * @param cl Classloader instance to report
     * @param name name of the classloader instance.
     */
    public void report(Reporter to, ClassLoader cl, String name) {
        to.report("    class: " + cl.getClass().getName());
        LoaderHandlerSet handlerSet = getHandlerSet();
        if (handlerSet == null) {
            throw new BuildException("internal error: handlerset is null");
        }
        LoaderHandler handler = handlerSet.getHandler(this, cl, Action.GETPATH);
        ClassLoaderAdapter adapter;
        if (handler == null) {
            to.report("    path:  - not investigatable (no Loaderhandler found) -");
        } else {
            adapter = handler.getAdapter(this);
            if (adapter == null) {
                to.report("    path:  - not investigatable (Loaderhandler retrieves no adapter) -");
            } else {
                String[] cp = adapter.getClasspath(this, cl, false);
                if (cp == null) {
                    to.report("    path:  - not investigatable (adapter retrieves no path) -");
                } else {
                    to.report("    path:  " + cp.length + " elements");
                    for (int i = 0; i < cp.length; i++) {
                        to.report("         > " + cp[i]);
                    }
                }
            }
        }
        handler = handlerSet.getHandler(this, cl, Action.REPORT);
        if (handler == null) {
            to.report("    - additional parameters not investigatable (no Loaderhandler found) -");
            return;
        }
        adapter = handler.getAdapter(this);
        if (adapter == null) {
            to.report("    - additional parameters not investigatable "
                    + "(Loaderhandler retrieves no adapter) -");
            return;
        }
        adapter.report(to, this, cl, name);
    }

    /**
     * Specify which path will be used. If the loader already exists
     * the path will be added to the loader.
     * @param classpath An Ant Path object containing the classpath.
     */
    public void setClasspath(URLPath classpath) {
        if (this.classpath == null) {
            this.classpath = classpath;
        } else {
            this.classpath.append(classpath);
        }
    }
    /**
     * Specify which path will be used. If the loader already exists
     * the path will be added to the loader.
     * @param pathRef Reference to a path defined elsewhere
     */
    public void setClasspathRef(Reference pathRef) {
        createClasspath().addReference(pathRef);
    }
    /**
     * Sets the failonerror attribute.
     * @param onOff Value.
     */
    public void setFailonerror(boolean onOff) {
        this.failOnError = onOff;
    }
    /**
     * Sets the loader attribute.
     * @param loader The loader.
     */
    public void setLoader(LoaderRef loader) {
        if (loader.isStandardLoader(LoaderRef.LoaderSpec.NONE)) {
            throw new BuildException("attribute loader can not be 'none'");
        }
        this.loader = loader;
    }
    /**
     * Sets the parameters attribute.
     * @param desc The parameters.
     */
    public void setParameters(LoaderParameters desc) {
        parameters = desc;
    }
    /**
     * Sets the parentLoader attribute.
     * @param loader The parent loader.
     */
    public void setParentLoader(LoaderRef loader) {
        this.parentLoader = loader;
    }
    /**
     * Sets the property to put the ClassLoader's path into.
     * @param property Name of the property.
     */
    public void setProperty(String property) {
        this.property = property;
    }
    /**
     * Sets the report attribute.
     * @param onOff Indicates whether to generate a report or not. Defaults to <code>false</code>.
     */
    public void setReport(boolean onOff) {
        report = onOff;
    }
    /**
     * Sets the reportPackages attribute.
     * @param onOff Indicates whether to include packages in the report or not. 
     * Defaults to <code>false</code>.
     */
    public void setReportpackages(boolean onOff) {
        reportPackages = onOff;
    }

    /**
     * Reset the classloader, if it already exists. A new loader will
     * be created and all the references to the old one will be removed.
     * (it is not possible to remove paths from a loader). The new
     * path will be used.
     *
     * @param onOff <code>false</code> if the loader is to be reset.
     */
    public void setReset(boolean onOff) {
        this.reset = onOff;
    }
    /**
     * Sets the superLoader attribute.
     * @param loader The superLoader.
     */
    public void setSuperLoader(LoaderRef loader) {
        this.parentLoader = loader;
    }
}
