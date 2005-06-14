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

import java.util.HashSet;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Classloader;
import org.apache.tools.ant.taskdefs.classloader.SimpleClassLoaderAdapter;

/**
 * specifies a Classloader's parameters.
 * @since Ant 1.7
 */
public class LoaderParameters
    extends DataType
    implements Classloader.ClassLoaderParameters
    , SimpleClassLoaderAdapter.Descriptor {
    /**
     * class for nested packageAssertionStatus and classAssertionStatus elements.
     */
    public class AssertionStatus {
        private String name = null;
        private boolean status = false;
        /**
         * checks whether name attribute is set and sets defaultStatus
         * @param pkg true if packageAssertionStatus element, false if classAssertionStatus element
         * @param defaultStatus defaultStatus if status is not set in nested element.
         */
        void check(boolean pkg) {
            if (name == null) {
                throw new BuildException(
                    "attribute "
                        + (pkg ? "package" : "class")
                        + " is mandatory");
            }
        }
        /**
         * returns the status
         * @return the status
         */
        public boolean getStatus() {
            return status;
        }
        /**
         * gets the classname or packagename
         * @return class- or packagename
         */
        public String getName() {
            return name;
        }
        /**
         * sets the assertion attribute
         * @param onOff if true, assertions for this class or package are enabled
         */
        public void setStatus(boolean onOff) {
            status = onOff;
        }
        /**
         * sets the class attribute
         * @param name the classname of the class for which
         *        to explicitely set the assertion status
         */
        public void setClass(String name) {
            this.name = name;
        }
        /**
         * sets the package attribute
         * @param name the packagename of the package for which
         *        to explicitely set the assertion status
         */
        public void setPackage(String name) {
            this.name = name;
        }

    }
    private HashSet classAssertions = new HashSet();
    private HashSet classNonAssertions = new HashSet();
    private Boolean defaultAssertionStatus = null;
    private HashSet packageAssertions = new HashSet();
    private HashSet packageNonAssertions = new HashSet();
    /**
     * Default Constructor
     * @param project current project
     */
    public LoaderParameters(Project project) {
        setProject(project);
        LoaderHandler.addPredefined(project);
    }
    /**
     * Constructor for setXXX
     * @param project current project
     * @param refid reference
     */
    public LoaderParameters(Project project, Reference refid) {
        this(project);
        setRefid(refid);
    }
    /**
     * sets a nested classAssertionStatus element
     * @param st the classAssertionStatus element
     */
    public void addConfiguredClassAssertionStatus(AssertionStatus st) {
        checkChildrenAllowed();
        st.check(false);
        if (st.getStatus()) {
            classAssertions.add(st.getName());
        } else {
            classNonAssertions.add(st.getName());
        }
    }
    /**
     * sets a nested packageAssertionStatus element
     * @param st the packageAssertionStatus element
     */
    public void addConfiguredPackageAssertionStatus(AssertionStatus st) {
        checkChildrenAllowed();
        st.check(true);
        if (st.getStatus()) {
            packageAssertions.add(st.getName());
        } else {
            packageNonAssertions.add(st.getName());
        }
    }
    /**
     * gets the classes specified for classAssertionStatus with status.
     * @param status status of the classAssertion
     * @return list of classnames with the specified assertionStatus.
     */
    public String[] getClassAssertions(boolean status) {
        if (isReference()) {
            SimpleClassLoaderAdapter.Descriptor r = (SimpleClassLoaderAdapter.Descriptor)
                getCheckedRef(SimpleClassLoaderAdapter.Descriptor.class, "loaderDescriptor");
            return r.getClassAssertions(status);
        }
        HashSet s = (status ? classAssertions : classNonAssertions);
        if (s.size() == 0) {
            return null;
        }
        return (String[]) s.toArray(new String[s.size()]);
    }
    /**
     * gets the default assertionStatus.
     * @return default assertionStatus or null if not specified.
     */
    public Boolean getDefaultAssertionStatus() {
        if (isReference()) {
            SimpleClassLoaderAdapter.Descriptor r = (SimpleClassLoaderAdapter.Descriptor)
              getCheckedRef(SimpleClassLoaderAdapter.Descriptor.class, "loaderDescriptor");
            return r.getDefaultAssertionStatus();
        }
        return defaultAssertionStatus;
    }
    /**
     * returns the default handler for this parameters.
     * @return the handler referred by "ant.clhandler.AntClassLoader".
     */
    public LoaderHandler getDefaultHandler() {
        if (isReference()) {
            Classloader.ClassLoaderParameters r = (Classloader.ClassLoaderParameters)
                getCheckedRef(Classloader.ClassLoaderParameters.class, "loaderDescriptor");
            return r.getDefaultHandler();
        }
        return (LoaderHandler) getProject().getReference("ant.clhandler.URLClassLoader");
    }
    /**
     * returns the descriptor which is either the instance itself
     * or a referenced descriptor.
     * @return descriptor.
     */
    public Classloader.ClassLoaderParameters getParameters() {
        if (isReference()) {
            return (Classloader.ClassLoaderParameters)
              getCheckedRef(Classloader.ClassLoaderParameters.class
                          , "loaderDescriptor");
        }
        return this;
    }
    /**
     * gets the packages specified for packageAssertionStatus with status.
     * @param status status of the packageAssertion
     * @return list of packagenames with the specified assertionStatus.
     */
    public String[] getPackageAssertions(boolean status) {
        if (isReference()) {
            SimpleClassLoaderAdapter.Descriptor r = (SimpleClassLoaderAdapter.Descriptor)
                getCheckedRef(SimpleClassLoaderAdapter.Descriptor.class, "loaderDescriptor");
            return r.getPackageAssertions(status);
        }
        HashSet s = (status ? packageAssertions : packageNonAssertions);
        if (s.size() == 0) {
            return null;
        }
        return (String[]) s.toArray(new String[s.size()]);
    }
    /**
     * sets the defaultAssertionStatus attribute
     * @param onOff true, to enable assertions by default.
     */
    public void setDefaultAssertionStatus(Boolean onOff) {
        checkAttributesAllowed();
        defaultAssertionStatus = onOff;
    }
}
