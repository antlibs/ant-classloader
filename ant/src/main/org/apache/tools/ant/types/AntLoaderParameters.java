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
import java.util.StringTokenizer;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.classloader.AntClassLoaderAdapter;

/**
 * specifies a Ant Classloader's parameters.
 */
public class AntLoaderParameters
    extends LoaderParameters
    implements AntClassLoaderAdapter.Descriptor {
    private boolean addJavaLibraries = false;

    private boolean isolated = false;
    private String[] loaderPackageRoot = null;
    private boolean parentFirst = true;
    private String[] systemPackageRoot = null;
    /**
     * Constructor
     * @param project current project
     */
    public AntLoaderParameters(Project project) {
        super(project);
    }
    /**
     * Constructor for setXXX
     * @param project current project
     * @param refid Reference
     */
    public AntLoaderParameters(Project project, Reference refid) {
        super(project, refid);
    }
    /**
     * gets the default Handler.
     * @return the handler referred by "ant.clhandler.AntClassLoader".
     */
    public LoaderHandler getDefaultHandler() {
        if (isReference()) {
            return super.getDefaultHandler();
        }
        return (LoaderHandler) getProject().getReference("ant.clhandler.AntClassLoader");
    }
    /**
     * gets the packagenames for those packages to set as loaderPackageRoot.
     * @return the packagenames or null if not specified.
     */
    public String[] getLoaderPackageRoot() {
        if (isReference()) {
            AntClassLoaderAdapter.Descriptor r = (AntClassLoaderAdapter.Descriptor)
             getCheckedRef(AntClassLoaderAdapter.Descriptor.class, "antLoaderDescriptor");
            return r.getLoaderPackageRoot();
        }
        return loaderPackageRoot;
    }
    /**
     * gets the packagenames for those packages to set as systemPackageRoot.
     * @return the packagenames or null if not specified.
     */
    public String[] getSystemPackageRoot() {
        if (isReference()) {
            AntClassLoaderAdapter.Descriptor r = (AntClassLoaderAdapter.Descriptor)
             getCheckedRef(AntClassLoaderAdapter.Descriptor.class, "antLoaderDescriptor");
            return r.getSystemPackageRoot();
        }
        return systemPackageRoot;
    }
    /**
     * indicates whether addJavaLibraries should be called.
     * @return true if addJavaLivbraries should be called.
     */
    public boolean isAddJavaLibraries() {
        if (isReference()) {
            AntClassLoaderAdapter.Descriptor r = (AntClassLoaderAdapter.Descriptor)
             getCheckedRef(AntClassLoaderAdapter.Descriptor.class, "antLoaderDescriptor");
            return r.isAddJavaLibraries();
        }
        return addJavaLibraries;
    }
    /**
     * indicates whether isolated mode should be set.
     * @return true if isolated mode should be set.
     */
    public boolean isIsolated() {
        if (isReference()) {
            AntClassLoaderAdapter.Descriptor r = (AntClassLoaderAdapter.Descriptor)
             getCheckedRef(AntClassLoaderAdapter.Descriptor.class, "antLoaderDescriptor");
            return r.isIsolated();
        }
        return isolated;
    }
    /**
     * indicates whether normal delegation model or reverse loader model should be used.
     * @return true if normal delegation model should be used,
     *         false if reverse model should be used.
     */
    public boolean isParentFirst() {
        if (isReference()) {
            AntClassLoaderAdapter.Descriptor r = (AntClassLoaderAdapter.Descriptor)
             getCheckedRef(AntClassLoaderAdapter.Descriptor.class, "antLoaderDescriptor");
            return r.isParentFirst();
        }
        return parentFirst;
    }
    /**
     * sets the addJavaLibraries attribute.
     * @param onOff true to call addJavaLibraries, else false
     */
    public void setAddJavaLibraries(boolean onOff) {
        checkAttributesAllowed();
        addJavaLibraries = onOff;
    }
    /**
     * sets the isolated attribute
     * @param b true to set isolated mode, else false
     */
    public void setIsolated(boolean b) {
        checkAttributesAllowed();
        isolated = b;
    }
    /**
     * sets the packages to be set as LoaderPackageRoot.
     * @param pkgs ',', ';' or ':' separated list of PackageNames.
     */
    public void setLoaderPackageRoot(String pkgs) {
        checkAttributesAllowed();
        HashSet set = new HashSet();
        for (StringTokenizer st = new StringTokenizer(pkgs, ",;:");
            st.hasMoreTokens();) {
            set.add(st.nextToken().trim());
        }
        loaderPackageRoot = (String[]) set.toArray(new String[set.size()]);
    }
    /**
     * sets the parentFirst attribute
     * @param b true for normal delegation model, false for reverse model
     */
    public void setParentFirst(boolean b) {
        checkAttributesAllowed();
        parentFirst = b;
    }
    /**
     * sets the reverseLoader attribute. This is the inverse of parentFirst.
     * @param b true for reverse delegation model, false for normal delegation model
     */
    public void setReverseLoader(boolean b) {
        checkAttributesAllowed();
        parentFirst = !b;
    }
    /**
     * sets the packages to be set as SystemPackageRoot.
     * @param pkgs ',', ';' or ':' separated list of PackageNames.
     */
    public void setSystemPackageRoot(String pkgs) {
        checkAttributesAllowed();
        HashSet set = new HashSet();
        for (StringTokenizer st = new StringTokenizer(pkgs, ",;:");
            st.hasMoreTokens();) {
            set.add(st.nextToken().trim());
        }
        systemPackageRoot = (String[]) set.toArray(new String[set.size()]);
    }

}
