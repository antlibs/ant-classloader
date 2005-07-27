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

package org.apache.tools.ant.taskdefs.classloader.adapter;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.tools.ant.taskdefs.classloader.ClassLoaderAdapterAction;
import org.apache.tools.ant.taskdefs.classloader.ClassloaderContext;

/**
 * A ClassLoaderAdapter for a java.net.URLClassLoader
 */
public class URLClassLoaderAdapter extends SimpleClassLoaderAdapter {
    /**
     * Appends a classpath to an existing classloader instance.
     *
     * @param task
     *            the calling ClassloaderBase-task.
     * @param classloader
     *            the classloader instance to append the path to.
     * @return The ClassLoader instance or null if an error occured.
     */
    public boolean appendClasspath(ClassloaderContext.CreateModify task,
            ClassLoader classloader) {

        URLClassLoader ucl = (URLClassLoader) classloader;
        String loaderId = task.getLoaderName();
        Method meth;
        try {
            meth = URLClassLoader.class.getDeclaredMethod("addURL",
                    new Class[] { URL.class });
            meth.setAccessible(true);
        } catch (SecurityException e1) {
            task.handleError("unable to setAccessible(true) for method addURL",
                    e1);
            return false;
        } catch (NoSuchMethodException e1) {
            task.handleError("method addURL not found", e1);
            return false;
        }
        Set localEntries = new HashSet();
        String[] list = task.getClasspathURLs();
        for (int i = 0; i < list.length; i++) {
            try {
                URL url = task.getURLUtil().createURL(list[i]);
                String sUrl = url.toString();
                if (localEntries.add(sUrl)
                        && task.handleClasspathEntry(ucl, sUrl)) {
                    meth.invoke(ucl, new Object[] { url });
                    task.handleDebug("URLClassLoader " + loaderId
                            + ": adding path " + url);
                }
            } catch (MalformedURLException e) {
                task.handleError("createURL(\"" + list[i] + "\")", e);
            } catch (Exception e) {
                task.handleError("unable to invoke URLClassLoader.addURL(url)",
                        e);
                return false;
            }
        }
        return true;
    }
    /**
     * returns the actual classpath of a classloader instance.
     *
     * @param task
     *            the calling ClassloaderBase-task.
     * @param classloader
     *            the classloader instance to get the path from.
     * @param defaultToFile
     *            if true, returned url-elements with file protocol should trim
     *            the leading 'file:/' prefix.
     * @return the path or null if an error occured
     */
    public String[] getClasspath(ClassloaderContext task,
            ClassLoader classloader, boolean defaultToFile) {
        URL[] urls = ((URLClassLoader) classloader).getURLs();
        String[] result = new String[urls.length];
        for (int i = 0; i < urls.length; i++) {
            if (defaultToFile && ("file".equals(urls[i].getProtocol()))) {
                result[i] = task.getURLUtil().createFile(urls[i].toString())
                        .toString();
            } else {
                result[i] = urls[i].toString();
            }
        }
        return result;
    }
    /**
     * Checks whether the adapter supports an action.
     *
     * @param action
     *            the action to check.
     * @return true, if action is supported.
     */
    public boolean isSupported(ClassLoaderAdapterAction action) {
        return true;
    }
    /**
     * creates a new ClassLoader instance.
     *
     * @param task
     *            the calling classloader task.
     * @return the newly created ClassLoader or null if an error occurs.
     */
    protected ClassLoader newClassLoader(
            ClassloaderContext.CreateModify task) {
        ClassLoader parent = task.getParentLoader();
        String loaderId = task.getLoaderName();

        String[] scp = task.getClasspathURLs();
        ArrayList ucp = new ArrayList(scp.length);
        Set localEntries = new HashSet();
        for (int i = 0; i < scp.length; i++) {
            try {
                URL url = task.getURLUtil().createURL(scp[i]);
                String sUrl = url.toString();
                if (localEntries.add(sUrl)
                        && task.handleClasspathEntry(parent, sUrl)) {
                    ucp.add(url);
                }
            } catch (Exception e) {
                task.handleError("createURL(\"" + scp[i] + "\")", e);
            }
        }
        // urlclassloader should always be created via
        // the bootstrap loader
        // so we don't need the superLoader
        URL[] urls = (URL[]) ucp.toArray(new URL[ucp.size()]);
        URLClassLoader cl = new URLClassLoader(urls, parent);
        task.handleDebug("URLClassLoader " + loaderId + " created.");
        for (int i = 0; i < urls.length; i++) {
            task.handleDebug("URLClassLoader " + loaderId + ": adding path "
                    + urls[i]);
        }

        if (parent != null) {
            task.handleDebug("URLClassLoader " + loaderId
                    + ": setting parent loader " + parent);
        }

        return cl;
    }

}
