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

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.AntLoaderParameters;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.LoaderHandler;
import org.apache.tools.ant.types.LoaderHandlerSet;
import org.apache.tools.ant.types.LoaderParameters;
import org.apache.tools.ant.types.LoaderRef;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.URLPath;

public class ClassloaderTask extends ClassloaderBase {

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

    private LoaderRef loader = null;
    private URLPath classpath = null;
    private ClassloaderTask.DuplicateEntry duplicateEntry = new ClassloaderTask.DuplicateEntry("omit");
    private LoaderRef superLoader = null;
    private boolean reset = false;
    private String property = null;
    private String loaderName = null;
    private LoaderRef parentLoader = null;
    private LoaderHandler handler = null;
    private ClassloaderTask.ClassLoaderParameters parameters = null;

    public ClassloaderTask() {
        super();
        // TODO Auto-generated constructor stub
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
     * Executes this task.
     */
    public void execute() {
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
    public ClassloaderTask.ClassLoaderParameters getParameters() {
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
    protected LoaderHandlerSet newHandlerSet() {
        LoaderHandlerSet result= new LoaderHandlerSet(getProject());
        result.addConfiguredHandler(getHandler());
        return result;
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
     * Sets a nested Descriptor element for an AntClassLoader.
     * @param desc the parameters.
     */
    public void addAntParameters(AntLoaderParameters desc) {
        parameters = desc;
    }
    /**
     * Sets a nested ClassLoaderParameters element.
     * @param desc The parameters.
     */
    public void addParameters(LoaderParameters desc) {
        parameters = desc;
    }

}
