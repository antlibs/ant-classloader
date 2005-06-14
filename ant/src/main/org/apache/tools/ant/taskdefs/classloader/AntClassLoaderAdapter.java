/*
 * Copyright  2004-2005 The Apache Software Foundation
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

package org.apache.tools.ant.taskdefs.classloader;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Classloader;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.URLPath;
import org.apache.tools.ant.util.URLUtils;

/**
 * A ClassLoaderAdapter for an AntClassLoader.
 */
public class AntClassLoaderAdapter extends SimpleClassLoaderAdapter {
    /**
     * Descriptor definition used by this implementation.
     */
    public static interface Descriptor
        extends Classloader.ClassLoaderParameters {
        /**
         * gets the packagenames for those packages to set as loaderPackageRoot.
         * @return the packagenames or null if not specified.
         */
        String[] getLoaderPackageRoot();
        /**
         * gets the packagenames for those packages to set as systemPackageRoot.
         * @return the packagenames or null if not specified.
         */
        String[] getSystemPackageRoot();
        /**
         * indicates whether addJavaLibraries should be called.
         * @return true if addJavaLivbraries should be called.
         */
        boolean isAddJavaLibraries();
        /**
         * indicates whether isolated mode should be set.
         * @return true if isolated mode should be set.
         */
        boolean isIsolated();
        /**
         * indicates whether normal delegation model or reverse loader model should be used.
         * @return true if normal delegation model should be used,
         *         false if reverse model should be used.
         */
        boolean isParentFirst();
    }

    /**
     * Appends a classpath to an existing classloader instance.
     * @param task the calling Classloader-task.
     * @param classloader the classloader instance to append the path to.
     * @return The ClassLoader instance or null if an error occured.
     */
    public boolean appendClasspath(Classloader task, ClassLoader classloader) {
        return appendClasspath(
            task,
            classloader,
            task.getClasspath().toPath().list());
    }

    private boolean appendClasspath(
        Classloader task,
        ClassLoader classloader,
        String[] path) {
        try {
            Method m =
                classloader.getClass().getMethod(
                    "addPathElement",
                    new Class[] {String.class });
            for (int i = 0; i < path.length; i++) {
                File f = new File(path[i]);
                if (f.exists()) {
                    String sUrl = URLUtils.createURL(f.getAbsolutePath()).toString();
                    if (task.handleClasspathEntry(classloader, sUrl)) {
                        m.invoke(classloader, new Object[] {f.getAbsolutePath()});
                        task.log("AntClassLoader " + task.getLoaderName()
                            + ": adding path " + f.getAbsolutePath(),
                            Project.MSG_DEBUG);
                    }
                } else {
                    task.log("AntClassLoader " + task.getLoaderName()
                            + ": ignoring nonexistent path " + f.getAbsolutePath(),
                        Project.MSG_WARN);
                }

            }
            return true;
        } catch (Exception e) {
            task.handleError("can not add Path to AntClassLoader", e);
            return false;
        }
    }

    /**
     * returns the actual classpath of a classloader instance.
     * @param task the calling Classloader-task.
     * @param classloader the classloader instance to get the path from.
     * @param defaultToFile if true, returned url-elements with file protocol
     *         should trim the leading 'file:/' prefix.
     * @return the path or null if an error occured
     */
    public String[] getClasspath(
        Classloader task,
        ClassLoader classloader,
        boolean defaultToFile) {
        try {
            String cp =
                (String) classloader
                    .getClass()
                    .getMethod("getClasspath", null).invoke(classloader, null);
            ArrayList l = new ArrayList();
            for (StringTokenizer st = new StringTokenizer(cp, File.pathSeparator);
                st.hasMoreTokens();) {
                l.add(st.nextToken());
            }
            return (String[]) l.toArray(new String[l.size()]);
        } catch (Exception e) {
            task.handleError("unable to get Classpath", e);
            return null;
        }
    }

    private boolean handleAddJavaLibraries(
        Classloader task,
        ClassLoader cl,
        String loaderId,
        boolean onOff) {
        if (!onOff) {
            return true;
        }
        try {
            Method m =
                cl.getClass().getMethod("addJavaLibraries", null);
            m.invoke(cl, null);
            task.log(
                "Loader " + loaderId + ": calling addJavaLibraries",
                Project.MSG_DEBUG);
            return true;
        } catch (Exception e) {
            task.handleError(
                "unable to call addJavaLibraries on AntClassLoader " + loaderId,
                e);
            return false;
        }
    }
    private boolean handleAddLoaderPackageRoot(
        Classloader task,
        ClassLoader cl,
        String loaderId,
        String[] pkgs) {
        if (pkgs == null) {
            return true;
        }
        try {
            Method m =
                cl.getClass().getMethod(
                    "addLoaderPackageRoot",
                    new Class[] {String.class });
            for (int i = 0; i < pkgs.length; i++) {
                m.invoke(cl, new Object[] {pkgs[i] });
                task.log(
                    "Loader "
                        + loaderId
                        + ": calling addLoaderPackageRoot(\""
                        + pkgs[i]
                        + "\")",
                    Project.MSG_DEBUG);
            }
            return true;
        } catch (Exception e) {
            task.handleError(
                "unable to call addLoaderPackageRoot on AntClassLoader "
                    + loaderId,
                e);
            return false;
        }
    }
    private boolean handleAddSystemPackageRoot(
        Classloader task,
        ClassLoader cl,
        String loaderId,
        String[] pkgs) {
        if (pkgs == null) {
            return true;
        }
        try {
            Method m =
                cl.getClass().getMethod(
                    "addSystemPackageRoot",
                    new Class[] {String.class });
            for (int i = 0; i < pkgs.length; i++) {
                m.invoke(cl, new Object[] {pkgs[i] });
                task.log(
                    "Loader "
                        + loaderId
                        + ": calling addSystemPackageRoot(\""
                        + pkgs[i]
                        + "\")",
                    Project.MSG_DEBUG);
            }
            return true;
        } catch (Exception e) {
            task.handleError(
                "unable to call addSystemPackageRoot on AntClassLoader "
                    + loaderId,
                e);
            return false;
        }
    }
    private ClassLoader handleCreateLoader(
        Classloader task,
        ClassLoader superLoader,
        Path path,
        String loaderId) {
        ClassLoader loader = null;
        try {
            loader =
                (ClassLoader) Class
                    .forName(
                        "org.apache.tools.ant.loader.AntClassLoader2",
                        true,
                        superLoader)
                    .newInstance();
            task.log(
                "AntClassLoader " + loaderId + " created",
                Project.MSG_DEBUG);
            //check whether Project classes are compatible
            Class osl =
                Class.forName(
                    "org.apache.tools.ant.Project",
                    true,
                    superLoader);
            Class psl =
                Class.forName(
                    "org.apache.tools.ant.Project",
                    true,
                    task.getProject().getClass().getClassLoader());
            if (osl == psl) {
                loader.getClass().getMethod(
                    "setProject",
                    new Class[] {osl }).invoke(
                    loader,
                    new Object[] {task.getProject()});
            } else {
                task.log(
                    "can not set project for AntClassLoader "
                        + loaderId
                        + ": Project classes are not compatible",
                    Project.MSG_WARN);
            }
            if (!appendClasspath(task, loader, path.list())) {
                return null;
            }
            return loader;
        } catch (Exception e) {
            task.handleError(
                "Unable to create AntClassLoader "
                    + loaderId
                    + ": "
                    + e.getMessage(),
                e);
            return null;
        }
    }
    private boolean handleSetIsolated(
        Classloader task,
        ClassLoader cl,
        String loaderId,
        boolean onOff) {
        if (!onOff) {
            return true;
        }
        try {
            Method m =
                cl.getClass().getMethod(
                    "setIsolated",
                    new Class[] {Boolean.TYPE });
            m.invoke(cl, new Object[] {Boolean.TRUE });
            task.log(
                "Loader " + loaderId + ": setting isolated=true",
                Project.MSG_DEBUG);
            return true;
        } catch (Exception e) {
            task.handleError(
                "unable to call setIsolated on AntClassLoader " + loaderId,
                e);
            return false;
        }
    }
    private void handleSetParent(
        Classloader task,
        ClassLoader cl,
        ClassLoader parent,
        String loaderId) {
        if (parent == null) {
            return;
        }
        try {
            Method m =
                cl.getClass().getMethod(
                    "setParent",
                    new Class[] {ClassLoader.class });
            m.invoke(cl, new Object[] {parent });
            task.log(
                "Loader " + loaderId + ": setting parentLoader",
                Project.MSG_DEBUG);
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }
    private boolean handleSetParentFirst(
        Classloader task,
        ClassLoader cl,
        String loaderId,
        boolean onOff) {
        if (onOff) {
            return true;
        }
        try {
            Method m =
                cl.getClass().getMethod(
                    "setParentFirst",
                    new Class[] {Boolean.TYPE });
            m.invoke(cl, new Object[] {Boolean.FALSE });
            task.log(
                "Loader " + loaderId + ": setting parentFirst=false",
                Project.MSG_DEBUG);
            return true;
        } catch (Exception e) {
            task.handleError(
                "unable to call setParentFirst on AntClassLoader " + loaderId,
                e);
            return false;
        }
    }
    /**
     * initialises a newly created ClassLoader with the Descriptor-defined attributes.
     * @param task the calling classloader task.
     * @param classloader the newly created ClassLoader.
     * @return the classloader instance or null if an error occurs.
     */
    protected ClassLoader initClassLoader(
        Classloader task,
        ClassLoader classloader) {
        Classloader.ClassLoaderParameters d = task.getParameters().getParameters();
        if (d instanceof Descriptor) {
            Descriptor dd = (Descriptor) d;
            String loaderId = task.getLoaderName();
            if (!handleSetIsolated(
                    task,
                    classloader,
                    loaderId,
                    dd.isIsolated())) {
                return null;
            }
            if (!handleSetParentFirst(
                    task,
                    classloader,
                    loaderId,
                    dd.isParentFirst())) {
                return null;
            }
            if (!handleAddJavaLibraries(
                    task,
                    classloader,
                    loaderId,
                    dd.isAddJavaLibraries())) {
                return null;
            }
            if (!handleAddLoaderPackageRoot(
                    task,
                    classloader,
                    loaderId,
                    dd.getLoaderPackageRoot())) {
                return null;
            }
            if (!handleAddSystemPackageRoot(
                    task,
                    classloader,
                    loaderId,
                    dd.getSystemPackageRoot())) {
                return null;
            }
        }
        return super.initClassLoader(task, classloader);
    }
    /**
     * Checks whether the adapter supports an action.
     * @param action the action to check.
     * @return true, if action is supported.
     */
    public boolean isSupported(Classloader.Action action) {
        return true;
    }
    /**
     * creates a new ClassLoader instance.
     * @param task the calling classloader task.
     * @return the newly created ClassLoader or null if an error occurs.
     */
    protected ClassLoader newClassLoader(Classloader task) {
        ClassLoader superLoader = task.getSuperLoader();
        ClassLoader parent = task.getParentLoader();
        String loaderId = task.getLoaderName();
        URLPath path = task.getClasspath();
        if (path!=null) {
            ClassLoader explParent = parent;
            if (explParent == null) {
                explParent = getDefaultParent();
            }
            String[] list=path.list();
            Set localEntries=new HashSet();
            URLPath newPath = new URLPath(path.getProject());
            for (int i = 0; i < list.length; i++) {
                try {
                    URL url = URLUtils.createURL(list[i]);
                    String sUrl=url.toString();
                    if (localEntries.add(sUrl) && task.handleClasspathEntry(explParent, sUrl)) {
                        newPath.addURLPath(new URLPath(newPath.getProject(),sUrl));
                    }
                } catch (MalformedURLException e) {
                    task.handleError("createURL(\"" + list[i] + "\")", e);
                }
            }
            path = newPath;
        }
        ClassLoader result;
        if (superLoader == null) {
            result =
                task.getProject().createClassLoader(
                    (path == null) ? null : path.toPath());
        } else {
            result =
                handleCreateLoader(
                    task,
                    superLoader,
                    (path == null) ? null : path.toPath(),
                    loaderId);
            if (result == null) {
                return null;
            }
        }
        handleSetParent(task, result, parent, loaderId);
        return result;
    }
    /**
     * Gets the default parent classloader in the case, this classloader's
     * <code>getParent()</code> method returns <code>null</code>. <br>
     * This implementation returns the system classloader, as this is the normal 
     * behaviour of AntClassLoader.
     * @return The classloader implicitely used as parent loader or null
     * if the bootstrap loader is the implicitely parent loader. 
     */
    public ClassLoader getDefaultParent() {
        return ClassLoader.getSystemClassLoader();
    }
    /**
     * Gets the symbolic name of the default parent classloader in the case, 
     * this classloader's <code>getParent()</code> method returns <code>null</code>. <br>
     * This implementation returns "SystemClassLoader", as this is the normal 
     * behaviour of classloaders.
     * NOTE: Hold this method in sync with {@link #getDefaultParent()}.  
     * @return The symbolic name of the classloader implicitely used as parent loader. 
     */
    protected String getDefaultParentName() {
        return "SystemClassLoader";
    }
}
