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
package org.apache.tools.ant.taskdefs.classloader;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.taskdefs.classloader.report.ClassloaderReporter;

/**
 * Provides some utility methods for ClassloaderTask and ClassloaderReport.
 * @since Ant1.7
 */
public final class SimpleClassloaderUtil implements ClassloaderUtil {
    private static final int BUFFER_MULT = 50;
    private static final ClassloaderUtil SINGLETON = new SimpleClassloaderUtil();
    /**
     * Gets the singleton instance of this implementation.
     * @return The singleton instance of this implementation.
     */
    public static ClassloaderUtil getClassLoaderUtil() {
        return SINGLETON;
    }
    /**
     * Indicates whether a classloader or it's delegation parents
     * contains an entry.
     * @param ctx The context.
     * @param cl The classloader.
     * @param url The entry as an url.
     * @return True if the entry was found, false if not.
     */
    public boolean containsEntry(ClassloaderContext ctx,
            ClassLoader cl, String url) {
        ArrayList errors = new ArrayList();
        if (containsEntryDelegatedOrSelf(ctx, cl, url, errors)) {
            return true;
        }
        if (errors.size() > 0) {
            StringBuffer sb = new StringBuffer(BUFFER_MULT * (1 + errors.size()));
            sb.append("Check for duplicate entries fails due to the following reason(s):");
            for (Iterator i = errors.iterator(); i.hasNext();) {
                sb.append("\n").append(i.next());
            }
            ctx.handleWarning(sb.toString());
        }
        return false;
    }

    /**
     * Checks whether an url is in the classpath of a classloader or it's
     * delegation hierarchy. <br>
     * NOTE: As of performance reasons, this method does not do the check in the
     * loading order (parentloader - childloader).
     *
     * @param cl The classloader.
     * @param url The url.
     * @param errors A list of errors to report.
     * @return <code>true</code>, if the classloader or one of it's implicite
     *         or explicite parents contains the url. <code>false</code>
     *         otherwise.
     */
    private static boolean containsEntryDelegatedOrSelf(
            ClassloaderContext ctx, ClassLoader cl, String url,
            List errors) {
        if (cl == null) {
            URL[] urls = ctx.getUtil().getBootstrapClasspathURLs();
            if (urls == null) {
                errors.add("bootstrap classpath not investigatable");
                return false;
            }
            for (int i = 0; i < urls.length; i++) {
                if (urls[i].toString().equals(url)) {
                    return true;
                }
            }
            return false;
        }
        if (containsEntrySelf(ctx, cl, url, errors)) {
            return true;
        }
        ClassLoaderAdapter adapter = ctx.getUtil().findAdapter(ctx, cl, null,
                errors, "  parent of classloader "
                        + cl.getClass().getModifiers(), "");
        ClassLoader parent = null;
        if (adapter != null) {
            parent = adapter.getParent(cl);
            if (parent == null) {
                parent = adapter.getDefaultParent();
            }
        }
        return containsEntryDelegatedOrSelf(ctx, parent, url, errors);
    }
    private static boolean containsEntrySelf(ClassloaderContext ctx,
            ClassLoader cl, String url, List errors) {
        ClassLoaderAdapter adapter = ctx.getUtil().findAdapter(ctx, cl,
                ClassLoaderAdapterAction.GETPATH, errors,
                "path for classloader " + cl.getClass().getName(), "");
        if (adapter == null) {
            return false;
        }
        String[] cp = adapter.getClasspath(ctx, cl, false);
        if (cp == null) {
            errors.add("path for classloader " + cl.getClass().getName()
                    + " not investigatable (adapter retrieves no path)");
            return false;
        }
        for (int i = 0; i < cp.length; i++) {
            if (cp[i].equals(url)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Gets the adapter for the specified classloader and action.
     * @param ctx The context.
     * @param cl The classloader to get an adapter for.
     * @param action The action to get an adapter for or null if
     *     the adapter is needed for common methods.
     * @return The adapter.
     * @throws ClassloaderAdapterException if no adapter can found.
     */
    public ClassLoaderAdapter findAdapter(ClassloaderContext ctx,
            ClassLoader cl, ClassLoaderAdapterAction action)
            throws ClassloaderAdapterException {
        ClassLoaderHandlerSet handlerSet = ctx.getHandlerSet();
        if (handlerSet == null) {
            throw new ClassloaderAdapterException(ClassloaderAdapterException.NO_HANDLERSET);
        }
        ClassLoaderHandler handler = handlerSet.getHandler(ctx, cl, action);
        if (handler == null) {
            throw new ClassloaderAdapterException(ClassloaderAdapterException.NO_HANDLER);
        }
        ClassLoaderAdapter adapter = handler.getAdapter(ctx);
        if (adapter == null) {
            throw new ClassloaderAdapterException(ClassloaderAdapterException.NO_ADAPTER);
        }
        return adapter;
    }
    /**
     * Gets the adapter for the specified classloader and action.
     * @param ctx The context.
     * @param cl The classloader to get an adapter for.
     * @param action The action to get an adapter for or null if
     *     the adapter is needed for common methods.
     * @param to The reporter to report errors against.
     * @param errPrefix A prefix for error messages.
     * @param errSuffix A suffix for error messages.
     * @return The adapter or null if an error occured.
     */
    public ClassLoaderAdapter findAdapter(ClassloaderContext ctx,
            ClassLoader cl, ClassLoaderAdapterAction action,
            ClassloaderReporter to, String errPrefix, String errSuffix) {
        try {
            return findAdapter(ctx, cl, action);
        } catch (ClassloaderAdapterException e) {
            switch (e.getReason()) {
            case ClassloaderAdapterException.NO_HANDLER:
                to.reportError(errPrefix
                        + " not investigatable (no Loaderhandler found)"
                        + errSuffix);
                break;
            case ClassloaderAdapterException.NO_ADAPTER:
                to.reportError(errPrefix
                                + " not investigatable (Loaderhandler retrieves no adapter)"
                                + errSuffix);
                break;
            default:
                ctx.handleError(errPrefix
                        + " not investigatable (no Loaderhandlerset)"
                        + errSuffix, e);

            }
        }
        return null;
    }
    /**
     * Gets the adapter for the specified classloader and action.
     * @param ctx The context.
     * @param cl The classloader to get an adapter for.
     * @param action The action to get an adapter for or null if
     *     the adapter is needed for common methods.
     * @param errors A list to add a message if an error occurs.
     * @param errPrefix A prefix for error messages.
     * @param errSuffix A suffix for error messages.
     * @return The adapter or null if an error occured.
     */
    public ClassLoaderAdapter findAdapter(ClassloaderContext ctx,
            ClassLoader cl, ClassLoaderAdapterAction action, List errors,
            String errPrefix, String errSuffix) {
        try {
            return findAdapter(ctx, cl, action);
        } catch (ClassloaderAdapterException e) {
            switch (e.getReason()) {
            case ClassloaderAdapterException.NO_HANDLER:
                errors.add(errPrefix
                        + " not investigatable (no Loaderhandler found)"
                        + errSuffix);
                break;
            case ClassloaderAdapterException.NO_ADAPTER:
                errors
                        .add(errPrefix
                                + " not investigatable (Loaderhandler retrieves no adapter)"
                                + errSuffix);
                break;
            default:
                ctx.handleError(errPrefix
                        + " not investigatable (no Loaderhandlerset)"
                        + errSuffix, e);
            }
        }
        return null;
    }
    /**
     * Gets the Urls of the bootstrap classpath.
     * @return The urls of the bootstrap classpath or null if they
     *    can not determined.
     */
    public URL[] getBootstrapClasspathURLs() {
        try {
            Object urlClassPath = Class.forName("sun.misc.Launcher").getMethod(
                    "getBootstrapClassPath", null).invoke(null, null);
            return (URL[]) urlClassPath.getClass().getMethod("getURLs", null)
                    .invoke(urlClassPath, null);
        } catch (Exception e) {
            return null;
        }
    }
    private SimpleClassloaderUtil() {
        super();
    }

}
