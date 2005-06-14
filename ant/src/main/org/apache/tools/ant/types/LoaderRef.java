/*
 * Copyright  2004 The Apache Software Foundation
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

package org.apache.tools.ant.types;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.MagicNames;
import org.apache.tools.ant.Project;

/**
 * Specifies a Classloader.
 * @since Ant 1.7
 */
public class LoaderRef extends DataType {
    /**
     * Enumeration of some popular classloaders.
     */
    public static class LoaderSpec extends EnumeratedAttribute {
        /** Enumerated values */
        public static final int CORE = 0,
            SYSTEM = 1,
            CURRENT = 2,
            NONE = 3,
            THREAD = 4,
            PROJECT = 5;
        /** synchronous magic ids */
        public static final String [] MAGIC_NAMES = {
            MagicNames.SYSTEM_LOADER_REF ,
            null,
            null,
            null,
            null,
            null
        };
        /**
         * Default Constructor.
         */
        public LoaderSpec() {
        }
        /**
         * Value'd Constructor.
         * @param value the symbolic name of the ClassLoader.
         */
        public LoaderSpec(String value) {
            setValue(value);
        }
        /**
         * get the defined ClassLoader.
         * @param type the calling loaderRef instance
         * @return the defined ClassLoader.
         */
        public ClassLoader getClassLoader(LoaderRef type) {
            switch (this.getIndex()) {
                case CORE :
                    return type.getProject().getCoreLoader();
                case SYSTEM :
                    return ClassLoader.getSystemClassLoader();
                case CURRENT :
                    return type.getClass().getClassLoader();
                case THREAD :
                    return Thread.currentThread().getContextClassLoader();
                case PROJECT :
                    return type.getProject().getClass().getClassLoader();
                default : //NONE and unknown values
                    return null;
            }
        }
        /**
         * indicates whether reset is possible for the defined classloader.
         * @return true if reset is possible, false if not.
         */
        public boolean isResetPossible() {
            switch (this.getIndex()) {
                case THREAD :
                case CORE :
                    return true;
                default :
                    return (MAGIC_NAMES[getIndex()] != null);
            }
        }
        /**
         * sets the defined ClassLoader.
         * @param type the calling loaderRef instance
         * @param loader the classloader to set
         */
        public void set(LoaderRef type, ClassLoader loader) {
            switch (this.getIndex()) {
                case THREAD :
                    Thread.currentThread().setContextClassLoader(loader);
                    break;
                case CORE :
                    type.getProject().setCoreLoader(loader);
                    break;
                default :
                    break;
            }
            if (MAGIC_NAMES[getIndex()] != null) {
                type.getProject().addReference(MAGIC_NAMES[getIndex()], loader);
            }
        }
        /**
         * get the values
         * @return an array of the allowed values for this attribute.
         */
        public String[] getValues() {
            return new String[] {
                "core",
                "system",
                "current",
                "none",
                "thread",
                "project" };
        }
        /**
         * Overwrites the toString Method to return a ClassLoader Description.
         * @return common name of the denoted ClassLoader.
         */
        public String toString() {
            switch (getIndex()) {
                case CORE :
                    return "CoreLoader";
                case SYSTEM :
                    return "SystemClassLoader";
                case CURRENT :
                    return "Current ClassLoader";
                case THREAD :
                    return "ThreadContextClassLoader";
                case PROJECT :
                    return "ProjectClassLoader";
                default :
                    return null;
            }
        }
    }

    private boolean failOnError = true;
    private LoaderSpec loader = null;
    private String loaderRef = null;
    /**
     * Default Constructor.
     */
    private LoaderRef() {
    }
    /**
     * Constructor.
     * @param project the current project
     */
    public LoaderRef(Project project) {
        setProject(project);
    }
    /**
     * Constructor used by setXXX methods
     * @param project the current project.
     * @param ref the reference which may be either a reference to an existing object
     *        or - if no object with this id exists - the symbolic name of a LoaderSpec's
     *        ClassLoader
     */
    public LoaderRef(Project project, String ref) {
        this(project);
        Object r = getProject().getReference(ref);
        if (r == null) {
            try {
                loader = new LoaderSpec(ref);
            } catch (Exception ex) {
                loaderRef = ref;
            }
        } else if (r instanceof LoaderRef) {
            setRefid(new Reference(ref));
        } else if (r instanceof ClassLoader) {
            loaderRef = ref;
        } else {
            throw new BuildException("Reference " + ref
                                   + " denotes an object of class " + r.getClass().getName()
                                   + " which is neither a org.apache.tools.ant.types.LoaderRef"
                                   + " nor a java.lang.ClassLoader");
        }
    }
    /**
     * gets the specified ClassLoader
     * @param defaultLoader a Loader to return if the classLoader
     *        specified by reference is not found.
     * @return the specified ClassLoader
     */
    public ClassLoader getClassLoader(LoaderSpec defaultLoader) {
        return getClassLoader(defaultLoader, failOnError, true);
    }
    /**
     * gets the specified ClassLoader
     * @param defaultLoader a Loader to return if the classLoader
     *        specified by reference is not found.
     * @param failOnError overrides the failOnError attribute
     * @param allowNullRef if true, a not found reference is not an error.
     * @return the specified ClassLoader
     */
    public ClassLoader getClassLoader(
        LoaderSpec defaultLoader,
        boolean failOnError,
        boolean allowNullRef) {
        Object obj = null;
        if (this.isReference()) {
            return getRef().getClassLoader(
                    defaultLoader,
                    failOnError,
                    allowNullRef);
        }
        if (loaderRef != null) {
            obj = getProject().getReference(loaderRef);
            if (obj == null) {
                if (allowNullRef) {
                    return null;
                }
                handleError(
                    "Referenced object " + loaderRef + " not found",
                    failOnError);
                return null;
            }
            if (!(obj instanceof ClassLoader)) {
                handleError(
                    "Referenced object "
                        + loaderRef
                        + " is not a ClassLoader: "
                        + obj.getClass().getName(),
                    failOnError);
                return null;
            }
            return (ClassLoader) obj;
        }
        if (loader != null) {
            return loader.getClassLoader(this);
        }
        if (defaultLoader != null) {
            return defaultLoader.getClassLoader(this);
        }
        return null;
    }
    /**
     * get the specified loader's reference id.
     * @return the reference id or null if no reference id found.
     */
    public String getLoaderId() {
        if (isReference()) {
            return getRef().getLoaderId();
        }
        if (loaderRef != null) {
            return loaderRef;
        }
        if (loader != null) {
            return LoaderSpec.MAGIC_NAMES[loader.getIndex()];
        }
        return null;
    }
    /**
     * get the specified loader's name
     * @return the specified loader's name
     */
    public String getName() {
        if (isReference()) {
            return getRef().getName();
        }
        if (loaderRef != null) {
            return loaderRef;
        }
        if (loader != null) {
            return loader.toString();
        }
        return null;
    }
    private LoaderRef getRef() {
        return (LoaderRef) getCheckedRef(LoaderRef.class, "loaderRef");
    }
    /**
     * handle an error with respect to the fail on error attribute.
     * @param msg error message
     */
    public void handleError(String msg) {
        handleError(msg, null, failOnError);
    }
    /**
     * handle an error with respect to the fail on error attribute.
     * @param msg error message
     * @param fail overrides the failOnError attribute
     */
    public void handleError(String msg, boolean fail) {
        handleError(msg, null, fail);
    }
    /**
     * handle an error with respect to the fail on error attribute.
     * @param msg error message
     * @param ex causing Exception
     */
    public void handleError(String msg, Throwable ex) {
        handleError(msg, null, failOnError);
    }
    /**
     * handle an error with respect to the fail on error attribute.
     * @param msg error message
     * @param ex causing exception
     * @param fail overrides the failOnError attribute
     */
    public void handleError(String msg, Throwable ex, boolean fail) {
        if ((msg == null) && (ex != null)) {
            msg = ex.getMessage();
        }
        if (fail) {
            throw new BuildException(msg, ex);
        }
        log("Error: " + msg, Project.MSG_ERR);
    }
    /**
     * indicates whether the specified StandardLoader is specified.
     * @param id index of the enumerated loader.
     * @return true if the CoreLoader is specified.
     */
    public boolean isStandardLoader(int id) {
        if (isReference()) {
            return getRef().isStandardLoader(id);
        }
        if (loaderRef != null) {
            return false;
        }
        if (loader != null) {
            return (loader.getIndex() == id);
        }
        return false;
    }
    /**
     * indicates whether the specified Loader is affected by
     * property <code>build.sysclasspath=none</code>.
     * @return true if the loader is affected.
     */
    public boolean equalsSysLoader() {
        if (isReference()) {
            return getRef().equalsSysLoader();
        }
        if ((loaderRef == null)
         && (loader != null)
         && ((loader.getIndex() == LoaderSpec.CORE)
          || (loader.getIndex() == LoaderSpec.PROJECT)
          || (loader.getIndex() == LoaderSpec.SYSTEM))) {
            return true;
        }
        ClassLoader cl = this.getClassLoader(null);
        if (cl == null) {
            return false;
        }
        ClassLoader scl = new LoaderSpec(LoaderSpec.MAGIC_NAMES[LoaderSpec.CORE])
                          .getClassLoader(this);
        if (scl == cl) {
            return true;
        }
        cl = new LoaderSpec(LoaderSpec.MAGIC_NAMES[LoaderSpec.PROJECT]).getClassLoader(this);
        if (scl == cl) {
            return true;
        }
        cl = new LoaderSpec(LoaderSpec.MAGIC_NAMES[LoaderSpec.SYSTEM]).getClassLoader(this);
        return (scl == cl);
    }
    /**
     * indicates whether reset is possible for the defined loader.
     * @return true if reset is possible for the defined loader, else false.
     */
    public boolean isResetPossible() {
        if (isReference()) {
            return getRef().isResetPossible();
        }
        if (loaderRef != null) {
            return true;
        }
        if (loader != null) {
            return loader.isResetPossible();
        }
        return false;
    }
    /**
     * sets a classloader as the defined loader. This method is only permitted if
     * isResetPossible returns true.
     * @param classloader classloader to set.
     */
    public void setClassLoader(ClassLoader classloader) {
        if (isReference()) {
            getRef().setClassLoader(classloader);
        } else if (loaderRef != null) {
            getProject().addReference(loaderRef, classloader);
        } else if (loader != null) {
            loader.set(this, classloader);
        }
    }
    /**
     * sets the failonerror attribute
     * @param onOff true to end build on error
     */
    public void setFailonerror(boolean onOff) {
        checkAttributesAllowed();
        failOnError = onOff;
    }
    /**
     * sets the loader attribute
     * @param spec loader
     */
    public void setLoader(LoaderSpec spec) {
        checkAttributesAllowed();
        loader = spec;
    }
    /**
     * sets the loaderRef attribute
     * @param id reference
     */
    public void setLoaderRef(Reference id) {
        checkAttributesAllowed();
        String sid = id.getRefId();
        for (int i = 0; i < LoaderSpec.MAGIC_NAMES.length; i++) {
            if (sid.equals(LoaderSpec.MAGIC_NAMES[i])) {
                setLoader(new LoaderSpec(new LoaderSpec().getValues()[i]));
                return;
            }
        }
        loaderRef = id.getRefId();
    }

}
