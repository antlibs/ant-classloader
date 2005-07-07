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

package org.apache.tools.ant.types;

import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.classloader.ClassLoaderAdapter;
import org.apache.tools.ant.taskdefs.classloader.ClassLoaderAdapterAction;
import org.apache.tools.ant.taskdefs.classloader.ClassLoaderAdapterContext;
import org.apache.tools.ant.taskdefs.classloader.ClassLoaderHandler;
import org.apache.tools.ant.taskdefs.classloader.adapter.AntClassLoaderAdapter;
import org.apache.tools.ant.taskdefs.classloader.adapter.SimpleClassLoaderAdapter;
import org.apache.tools.ant.taskdefs.classloader.adapter.URLClassLoaderAdapter;

/**
 * ClassLoaderHandler.
 * @since Ant 1.7
 */
public final class LoaderHandler extends DataType implements ClassLoaderHandler, Cloneable {

    private static final LoaderHandler[] DEFAULT_HANDLERS = {
        new LoaderHandler("ant.clhandler.URLClassLoader"
                        , URLClassLoaderAdapter.class.getName()
                        , URLClassLoader.class.getName()),
        new LoaderHandler("ant.clhandler.AntClassLoader"
                        , AntClassLoaderAdapter.class.getName()
                        , AntClassLoader.class.getName()),
        new LoaderHandler("ant.clhandler.ClassLoader"
                        , SimpleClassLoaderAdapter.class.getName()
                        , ClassLoader.class.getName())
    };
    /**
     * adds the predefined handlers to the project's reference table.
     * @param project the current project
     */
    public static void addPredefined(Project project) {
        for (int i = 0; i < DEFAULT_HANDLERS.length; i++) {
            DEFAULT_HANDLERS[i].setDesiredId(project);
        }
    }
    /**
     * gets the predefined Loaderhandlers for a project
     * @param project current project
     * @return array of the predefined handlers
     */
    public static ClassLoaderHandler[] getDefaultHandlers(Project project) {
        addPredefined(project);
        ArrayList list = new ArrayList(DEFAULT_HANDLERS.length);
        for (int i = 0; i < DEFAULT_HANDLERS.length; i++) {
            Object o = project.getReference(DEFAULT_HANDLERS[i].desiredId);
            if ((o != null) && (o instanceof LoaderHandler)) {
                list.add(o);
            }
        }
        return (ClassLoaderHandler[]) list.toArray(new ClassLoaderHandler[list.size()]);
    }
    /**
     * gets all defined Loaderhandlers of a project
     * @param project current project
     * @return array of the defined handlers
     */
    public static ClassLoaderHandler[] getAllHandlers(Project project) {
        addPredefined(project);
        ArrayList list = new ArrayList(DEFAULT_HANDLERS.length);
        for (Iterator i = project.getReferences().keySet().iterator(); i.hasNext();) {
            Object o = project.getReference((String) i.next());
            if ((o != null) && (o instanceof LoaderHandler) && !((LoaderHandler) o).isReference()) {
                list.add(o);
            }
        }
        return (ClassLoaderHandler[]) list.toArray(new ClassLoaderHandler[list.size()]);
    }
    private String adapter = null;
    private String desiredId = null;
    private String loader = null;
    /**
     * Default Constructor.
     */
    private LoaderHandler() {
    }
    /**
     * Default Constructor.
     * @param project current project
     */
    public LoaderHandler(Project project) {
        setProject(project);
        addPredefined(project);
    }
    /**
     * Constructor for setXXX.
     * @param project current project
     * @param refid reference
     */
    public LoaderHandler(Project project, Reference refid) {
        this(project);
        setRefid(refid);
        check();
    }
    private LoaderHandler(String id, String adapter, String loader) {
        this.desiredId = id;
        this.adapter = adapter;
        this.loader = loader;
        check();
    }
    /**
     * checks whether both attributes are set.
     */
    public void check() {
        if (isReference()) {
            LoaderHandler r = (LoaderHandler) getCheckedRef(LoaderHandler.class, "LoaderHandler");
            r.check();
        }
        if (loader == null) {
            throw new BuildException("attribute loader is mandatory");
        }
        if (adapter == null) {
            throw new BuildException("attribute adapter is mandatory");
        }
    }
    /**
     * clones this Object
     * @return cloned object
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new BuildException(e);
        }
    }
    private LoaderHandler clone(Project project) {
        LoaderHandler result = (LoaderHandler) clone();
        result.setProject(project);
        return result;
    }
    /**
     * gets the adapter classname
     * @return the adapter classname
     */
    public String getAdapter() {
        check();
        if (isReference()) {
            LoaderHandler r = (LoaderHandler) getCheckedRef(LoaderHandler.class, "LoaderHandler");
            return r.getAdapter();
        }
        return adapter;
    }
    /**
     * returns an adapter instance
     * @param task the calling classloader task
     * @return the newly created adapter or null if an error occured
     */
    public ClassLoaderAdapter getAdapter(ClassLoaderAdapterContext task) {
        check();
        if (isReference()) {
            LoaderHandler r = (LoaderHandler) getCheckedRef(LoaderHandler.class, "LoaderHandler");
            return r.getAdapter(task);
        }

        try {
            return (ClassLoaderAdapter) Class
                .forName(adapter)
                .newInstance();
        } catch (Exception e) {
            task.handleError(
                "error instantiating ClassLoaderAdapter " + adapter,
                e);
            return null;
        }
    }
    /**
     * gets the classloaders classname
     * @return the classloader classname
     */
    public String getLoader() {
        check();
        if (isReference()) {
            LoaderHandler r = (LoaderHandler) getCheckedRef(LoaderHandler.class, "LoaderHandler");
            return r.getLoader();
        }
        return loader;
    }
    /**
     * checks whether a classloader is assignable to the loader
     * attribute and the adapter supports the required action.
     * @param task the calling classloader task.
     * @param assignable the ClassLoader instance to test against
     * @param action the required action
     * @return A Class-Object of the specified loader if the
     *         assignable ClassLoader is assignable to this loader
     *         and the adapter supports the required action; else null.
     */
    public Class getLoaderClass(
        ClassLoaderAdapterContext task,
        ClassLoader assignable,
        ClassLoaderAdapterAction action) {
        check();
        if (isReference()) {
            LoaderHandler r = (LoaderHandler) getCheckedRef(LoaderHandler.class, "LoaderHandler");
            return r.getLoaderClass(task, assignable, action);
        }
        try {
            Class result =
                Class.forName(
                    this.loader,
                    true,
                    assignable.getClass().getClassLoader());
            if (!(result.isAssignableFrom(assignable.getClass()))) {
                return null;
            }
            ClassLoaderAdapter adapter = getAdapter(task);
            if ((action != null) && !adapter.isSupported(action)) {
                return null;
            }
            return result;
        } catch (Throwable e) {
            return null;
        }
    }
    /**
     * sets the adapter attribute
     * @param classname the adapter's classname
     */
    public void setAdapter(String classname) {
        checkAttributesAllowed();
        adapter = classname;
    }
    private void setDesiredId(Project project) {
        if (desiredId != null) {
            if (project.getReference(desiredId) == null) {
                project.addReference(desiredId, clone(project));
            }
        }
    }
    /**
     * sets the loader attribute
     * @param classname the loader's classname
     */
    public void setLoader(String classname) {
        checkAttributesAllowed();
        loader = classname;
    }
}
