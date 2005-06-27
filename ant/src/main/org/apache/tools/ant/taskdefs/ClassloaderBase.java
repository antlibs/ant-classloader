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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.classloader.report.ClassloaderReporter;
import org.apache.tools.ant.taskdefs.classloader.report.ClassloaderReportHandle;
import org.apache.tools.ant.types.LoaderHandler;
import org.apache.tools.ant.types.LoaderHandlerSet;

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
public class ClassloaderBase extends Task {
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
    private static class AdapterException extends Exception {
        public static final int NO_HANDLERSET = 0;
        public static final int NO_HANDLER = 1;
        public static final int NO_ADAPTER = 2;
        private static final long serialVersionUID = 1L;
        private final int reason;
        public AdapterException(int reason) {
            this.reason = reason;
        }
        public int getReason() {
            return reason;
        }
    }
    /**
     * ClassLoaderAdapter used to define classloader interaction.
     */
    public static interface ClassLoaderAdapter {
        /**
         * add classloader to the report queue.
         * the adapter should call task.addLoaderToReport to add a loader.
         * @param task the calling ClassloaderBase-task.
         * @param classloader the classloader to analyze.
         * @param name the name of the classloader instance.
         * @param loaderStack loaderStack to pass to ClassloaderBase.addLoaderToReport.
         * @param loaderNames loaderNames to pass to ClassloaderBase.addLoaderToReport.
         */
        void addReportable(
            ClassloaderReport task,
            ClassLoader classloader,
            ClassloaderReportHandle role,
            Map loaderStack,
            Map loaderNames);
        /**
         * Appends a classpath to an existing classloader instance.
         * @param task the calling ClassloaderBase-task.
         * @param classloader the classloader instance to append the path to.
         * @return The ClassLoader instance or null if an error occured.
         */
        boolean appendClasspath(ClassloaderTask task, ClassLoader classloader);
        /**
         * Creates a classloader instance.
         * @param task the calling ClassloaderBase-task.
         * @return the newly created ClassLoader instance or null if an error occured.
         */
        ClassLoader createClassLoader(ClassloaderTask task);
        /**
         * Returns the actual classpath of a classloader instance.
         * @param task the calling ClassloaderBase-task.
         * @param classloader the classloader instance to get the path from.
         * @param defaultToFile if true, returned url-elements with file protocol
         *         should trim the leading 'file:/' prefix.
         * @return the path or null if an error occured
         */
        String[] getClasspath(
            ClassloaderBase task,
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
         * @param task the calling ClassloaderBase-task.
         * @param classloader the classloader instance to report about.
         * @param name the name of the classloader instance.
         */
        void report(
            ClassloaderReporter to,
            ClassloaderReport task,
            ClassLoader classloader,
            ClassloaderReportHandle name);
        /**
         * Gets the default parent classloader in the case, this classloader's
         * <code>getParent()</code> method returns <code>null</code>. 
         * @return The classloader implicitely used as parent loader or null
         * if the bootstrap loader is the implicitely parent loader. 
         */
        ClassLoader getDefaultParent();
        /**
         * Gets the parent classloader. 
         * Necessary, because AntClassLoader's <code>getParent()</code> method
         * returns an invalid result. 
         * @param classLoader The classloader to get the parent from.
         * @return The classloader explicitely used as parent loader or <code>null</code>.
         */
        ClassLoader getParent(ClassLoader classLoader);
    }
    protected boolean failOnError;
    private LoaderHandlerSet handlerSet = null;
    /**
     * Default constructor
     */
    public ClassloaderBase() {
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
    /*
    private String formatIndex(int i) {
        String x = String.valueOf(i + 1);
        if (x.length() == 1) {
            return " " + x;
        }
        return x;
    }
    */
    protected ClassLoaderAdapter findAdapter(ClassLoader cl, Action action) throws AdapterException {
        LoaderHandlerSet handlerSet = getHandlerSet();
        if (handlerSet == null) {
            throw new AdapterException(AdapterException.NO_HANDLERSET);
        }
        LoaderHandler handler = handlerSet.getHandler(this, cl, action);
        if (handler == null) {
            throw new AdapterException(AdapterException.NO_HANDLER);
        }
        ClassLoaderAdapter adapter = handler.getAdapter(this);
        if (adapter == null) {
            throw new AdapterException(AdapterException.NO_ADAPTER);
        }
        return adapter;
    }
    protected ClassLoaderAdapter findAdapter(ClassLoader cl, Action action, ClassloaderReporter to, String errPrefix, String errSuffix) {
        try {
            return findAdapter(cl, Action.REPORT);
        } catch (AdapterException e) {
            switch (e.getReason()) {
            case AdapterException.NO_HANDLERSET:
                throw new BuildException("internal error: handlerset is null");
            case AdapterException.NO_HANDLER:
                to.reportError(errPrefix + " not investigatable (no Loaderhandler found)" + errSuffix);
                break;
            case AdapterException.NO_ADAPTER:
                to.reportError(errPrefix + " not investigatable (Loaderhandler retrieves no adapter)" + errSuffix);
                break;
            }
        }
        return null;
    }
    protected ClassLoaderAdapter findAdapter(ClassLoader cl, Action action, List errors, String errPrefix, String errSuffix) {
        try {
            return findAdapter(cl, Action.REPORT);
        } catch (AdapterException e) {
            switch (e.getReason()) {
            case AdapterException.NO_HANDLERSET:
                throw new BuildException("internal error: handlerset is null");
            case AdapterException.NO_HANDLER:
                errors.add(errPrefix + " not investigatable (no Loaderhandler found)" + errSuffix);
                break;
            case AdapterException.NO_ADAPTER:
                errors.add(errPrefix + " not investigatable (Loaderhandler retrieves no adapter)" + errSuffix);
                break;
            }
        }
        return null;
    }
    protected LoaderHandlerSet newHandlerSet() {
        return new LoaderHandlerSet(getProject());
    }
    /**
     * Gets the handlerset to analyze a given classloader with.
     * @return The handlerset.
     */
    public LoaderHandlerSet getHandlerSet() {
        if (handlerSet == null) {
            handlerSet = newHandlerSet();
        }
        return handlerSet;
    }
    /**
     * Handles an error with respect to the failonerror attribute.
     * @param msg Error message.
     */
    public void handleError(String msg) {
        handleError(msg, null, null);
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
    protected URL[] getBootstrapClasspathURLs() {
        try {
			Object urlClassPath=Class.forName("sun.misc.Launcher").getMethod("getBootstrapClassPath",null).invoke(null,null);
			return (URL[])urlClassPath.getClass().getMethod("getURLs",null).invoke(urlClassPath,null);
		} catch (Exception e) {
			return null;
		}
    }
    private boolean containsEntrySelf(ClassLoader cl, String url, List errors) {
        ClassLoaderAdapter adapter = findAdapter(cl, Action.GETPATH, errors,
                "path for classloader "+cl.getClass().getName(), "");
        if (adapter == null) {
            return false;
        }
        String[] cp = adapter.getClasspath(this, cl, false);
        if (cp == null) {
            errors.add("path for classloader "+cl.getClass().getName()+" not investigatable (adapter retrieves no path)");
            return false;
        }
        for (int i=0; i < cp.length; i++) {
            if (cp[i].equals(url)) {
                return true;
            }
        }
        return false;
    }
    protected boolean containsEntry(ClassLoader cl, String url) {
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
        ClassLoaderAdapter adapter = findAdapter(cl, null, errors, "  parent of classloader " + cl.getClass().getModifiers(), "");
        ClassLoader parent = null;
        if (adapter != null) {
            parent = adapter.getParent(cl);
            if (parent == null) {
                parent = adapter.getDefaultParent();
            }
        }
        return containsEntryDelegatedOrSelf(parent, url, errors);
    }

    /**
     * Sets the failonerror attribute.
     * @param onOff Value.
     */
    public void setFailonerror(boolean onOff) {
        this.failOnError = onOff;
    }

}
