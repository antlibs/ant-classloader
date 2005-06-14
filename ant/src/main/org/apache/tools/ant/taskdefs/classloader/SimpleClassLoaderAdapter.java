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

package org.apache.tools.ant.taskdefs.classloader;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Classloader;

/**
 * An abstract classloader adapter.
 * @since Ant 1.7
 */
public class SimpleClassLoaderAdapter
    implements Classloader.ClassLoaderAdapter {
    /**
     * Descriptor definition used by this implementation.
     */
    public static interface Descriptor
        extends Classloader.ClassLoaderParameters {
        /**
         * gets the default assertionStatus.
         * @return default assertionStatus or null if not specified.
         */
        Boolean getDefaultAssertionStatus();
        /**
         * gets the classes specified for classAssertionStatus with status.
         * @param status status of the classAssertion
         * @return list of classnames with the specified assertionStatus.
         */
        String[] getClassAssertions(boolean status);
        /**
         * gets the packages specified for packageAssertionStatus with status.
         * @param status status of the packageAssertion
         * @return list of packagenames with the specified assertionStatus.
         */
        String[] getPackageAssertions(boolean status);
    }
    private boolean handleSetClassAssertionStatus(
        Classloader task,
        ClassLoader cl,
        String name,
        String[] classes,
        boolean onOff) {
        if (classes == null) {
            return true;
        }
        try {
            Method m =
                cl.getClass().getMethod(
                    "setClassAssertionStatus",
                    new Class[] {String.class, Boolean.TYPE });
            for (int i = 0; i < classes.length; i++) {
                m.invoke(
                    cl,
                    new Object[] {
                        classes[i],
                        onOff ? Boolean.TRUE : Boolean.FALSE });
                task.log(
                    "Loader "
                        + name
                        + ": setting ClassAssertionStatus for "
                        + classes[i]
                        + "="
                        + onOff,
                    Project.MSG_DEBUG);
            }
            return true;
        } catch (NoSuchMethodException e) {
            task.log(
                "Loader " + name + ": ClassAssertionStatus not supported.",
                Project.MSG_WARN);
            return true;
        } catch (Exception e) {
            task.handleError(e.getMessage(), e);
            return false;
        }
    }
    private boolean handleSetPackageAssertionStatus(
        Classloader task,
        ClassLoader cl,
        String name,
        String[] pkgs,
        boolean onOff) {
        if (pkgs == null) {
            return true;
        }
        try {
            Method m =
                cl.getClass().getMethod(
                    "setPackageAssertionStatus",
                    new Class[] {String.class, Boolean.TYPE });
            for (int i = 0; i < pkgs.length; i++) {
                m.invoke(
                    cl,
                    new Object[] {
                        pkgs[i],
                        onOff ? Boolean.TRUE : Boolean.FALSE });
                task.log(
                    "Loader "
                        + name
                        + ": setting PackageAssertionStatus for "
                        + pkgs[i]
                        + "="
                        + onOff,
                    Project.MSG_DEBUG);
            }
            return true;
        } catch (NoSuchMethodException e) {
            task.log(
                "Loader " + name + ": PackageAssertionStatus not supported.",
                Project.MSG_WARN);
            return true;
        } catch (Exception e) {
            task.handleError(e.getMessage(), e);
            return false;
        }
    }

    private Package[] handleGetPackages(
        Classloader task,
        ClassLoader cl,
        String name) {
        try {
            Method m = ClassLoader.class.getDeclaredMethod("getPackages", null);
            m.setAccessible(true);
            return (Package[]) m.invoke(cl, null);
        } catch (NoSuchMethodException e) {
            task.log(
                "Loader " + name + ": oops, getPackages not supported (java < 1.2 ?).",
                Project.MSG_WARN);
            return null;
        } catch (SecurityException e) {
            task.handleError(
                "unable to setAccessible(true) for method getPackages",
                e);
            return null;
        } catch (Exception e) {
            task.handleError(e.getMessage(), e);
            return null;
        }
    }
    private boolean handleSetDefaultAssertionStatus(
        Classloader task,
        ClassLoader cl,
        String name,
        Boolean onOff) {
        if (onOff == null) {
            return true;
        }
        try {
            Method m =
                cl.getClass().getMethod(
                    "setDefaultAssertionStatus",
                    new Class[] {Boolean.TYPE });
            m.invoke(cl, new Object[] {onOff });
            task.log(
                "Loader " + name + ": setting DefaultAssertionStatus=" + onOff,
                Project.MSG_DEBUG);
            return true;
        } catch (NoSuchMethodException e) {
            task.log(
                "Loader " + name + ": PackageAssertionStatus not supported.",
                Project.MSG_WARN);
            return true;
        } catch (Exception e) {
            task.handleError(e.getMessage(), e);
            return false;
        }
    }

    /**
     * creates a new ClassLoader instance.
     * @param task the calling classloader task.
     * @return the newly created ClassLoader or null if an error occurs.
     */
    protected ClassLoader newClassLoader(Classloader task) {
        task.handleError("new ClassLoader not supported (Adapter error).");
        return null;
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
            if (!handleSetDefaultAssertionStatus(task,
                classloader,
                loaderId,
                dd.getDefaultAssertionStatus())) {
                    return null;
                }
            if (!handleSetPackageAssertionStatus(task,
                classloader,
                loaderId,
                dd.getPackageAssertions(true),
                true)) {
                    return null;
                }
            if (!handleSetPackageAssertionStatus(task,
                classloader,
                loaderId,
                dd.getPackageAssertions(false),
                false)) {
                    return null;
                }
            if (!handleSetClassAssertionStatus(task,
                classloader,
                loaderId,
                dd.getClassAssertions(true),
                true)) {
                    return null;
                }
            if (!handleSetClassAssertionStatus(task,
                classloader,
                loaderId,
                dd.getClassAssertions(false),
                false)) {
                    return null;
                }
        }
        return classloader;
    }

    /**
     * creates a new classloader instance.
     * @param task the calling classloader instance
     * @return the newly created classloader or null if an error occurs.
     */
    public final ClassLoader createClassLoader(Classloader task) {
        ClassLoader cl = newClassLoader(task);
        if (cl == null) {
            return null;
        }
        return initClassLoader(task, cl);
    }

    /**
     * appends a classpath to an existing classloader.
     * @param task the calling classloader instance.
     * @param classloader the classloader to modify.
     * @return true if executed successful, false on error
     */
    public boolean appendClasspath(Classloader task, ClassLoader classloader) {
        task.handleError("append not supported (Adapter error)");
        return false;
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
        task.handleError("getClasspath not supported (Adapter error)");
        return null;
    }

    /**
     * performs additional reporting.
     * @param to the Reporter Object to report to.
     * @param task the calling Classloader-task.
     * @param classloader the classloader instance to report about.
     * @param name the name of the classloader instance.
     */
    public void report(
        Classloader.Reporter to,
        Classloader task,
        ClassLoader classloader,
        String name) {
        if (classloader.getParent()==null)
            to.report("    implicitely used parent loader is " + getDefaultParentName());
        if (task.isReportPackages()) {
            Package[] pkgs = this.handleGetPackages(task, classloader, name);
            if (pkgs == null) {
                to.report("    packages: - not investigatable -");
            } else {
                Arrays.sort(pkgs, PackageComparator.SINGLETON);
                to.report("    packages: " + pkgs.length + " entries");
                for (int i = 0; i < pkgs.length; i++) {
                    to.report("            > " + pkgs[i].getName());
                }
            }
        }
    }

    /**
     * add classloader to the report queue.
     * the adapter should call task.addLoaderToReport to add a loader.
     * @param task the calling Classloader-task.
     * @param classloader the classloader to analyze.
     * @param name the name of the classloader instance.
     * @param loaderStack loaderStack to pass to Classloader.addLoaderToReport.
     * @param loaderNames loaderNames to pass to Classloader.addLoaderToReport.
     */
    public void addReportable(
        Classloader task,
        ClassLoader classloader,
        String name,
        Map loaderStack,
        Map loaderNames) {
    }

    /**
     * Checks whether the adapter supports an action.
     * @param action the action to check.
     * @return true, if action is supported.
     */
    public boolean isSupported(Classloader.Action action) {
        if (Classloader.Action.REPORT.equals(action)) {
            return true;
        }
        return false;
    }
    private static final class PackageComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            return ((Package) o1).getName().compareTo(((Package) o2).getName());
        }
        private PackageComparator() {
        }
        public static final Comparator SINGLETON = new PackageComparator();
    }
    /**
     * Gets the default parent classloader in the case, this classloader's
     * <code>getParent()</code> method returns <code>null</code>. <br>
     * This implementation returns null (i.e. bootstrap), as this is the normal 
     * behaviour of classloaders.
     * @return The classloader implicitely used as parent loader or null
     * if the bootstrap loader is the implicitely parent loader. 
     */
    public ClassLoader getDefaultParent() {
        return null;
    }
    /**
     * Gets the symbolic name of the default parent classloader in the case, 
     * this classloader's <code>getParent()</code> method returns <code>null</code>. <br>
     * This implementation returns "Bootstrap", as this is the normal 
     * behaviour of classloaders.
     * NOTE: Hold this method in sync with {@link #getDefaultParent()}.  
     * @return The symbolic name of the classloader implicitely used as parent loader. 
     */
    protected String getDefaultParentName() {
        return "Bootstrap";
    }
}
