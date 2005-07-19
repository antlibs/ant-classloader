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
 * Provides some methods for ClassloaderTask and ClassloaderReport.
 */
public class ClassloaderUtil {

    public static class AdapterException extends Exception {
        public static final int NO_ADAPTER = 2;
        public static final int NO_HANDLER = 1;
        public static final int NO_HANDLERSET = 0;
        private static final long serialVersionUID = 1L;
        private final int reason;
        public AdapterException(int reason) {
            this.reason = reason;
        }
        public int getReason() {
            return reason;
        }
    }

    public static boolean containsEntry(ClassLoaderAdapterContext ctx,
            ClassLoader cl, String url) {
        ArrayList errors = new ArrayList();
        if (containsEntryDelegatedOrSelf(ctx, cl, url, errors)) {
            return true;
        }
        if (errors.size() > 0) {
            StringBuffer sb = new StringBuffer(50 * (1 + errors.size()));
            sb
                    .append("Check for duplicate entries fails due to the following reason(s):");
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
     * @param cl
     *            The classloader.
     * @param url
     *            The url.
     * @param errors
     *            A list of errors to report.
     * @return <code>true</code>, if the classloader or one of it's implicite
     *         or explicite parents contains the url. <code>false</code>
     *         otherwise.
     */
    private static boolean containsEntryDelegatedOrSelf(
            ClassLoaderAdapterContext ctx, ClassLoader cl, String url,
            List errors) {
        if (cl == null) {
            URL[] urls = ClassloaderUtil.getBootstrapClasspathURLs();
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
        ClassLoaderAdapter adapter = ClassloaderUtil.findAdapter(ctx, cl, null,
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
    private static boolean containsEntrySelf(ClassLoaderAdapterContext ctx,
            ClassLoader cl, String url, List errors) {
        ClassLoaderAdapter adapter = ClassloaderUtil.findAdapter(ctx, cl,
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
    public static ClassLoaderAdapter findAdapter(ClassLoaderAdapterContext ctx,
            ClassLoader cl, ClassLoaderAdapterAction action)
            throws AdapterException {
        ClassLoaderHandlerSet handlerSet = ctx.getHandlerSet();
        if (handlerSet == null) {
            throw new AdapterException(AdapterException.NO_HANDLERSET);
        }
        ClassLoaderHandler handler = handlerSet.getHandler(ctx, cl, action);
        if (handler == null) {
            throw new AdapterException(AdapterException.NO_HANDLER);
        }
        ClassLoaderAdapter adapter = handler.getAdapter(ctx);
        if (adapter == null) {
            throw new AdapterException(AdapterException.NO_ADAPTER);
        }
        return adapter;
    }
    public static ClassLoaderAdapter findAdapter(ClassLoaderAdapterContext ctx,
            ClassLoader cl, ClassLoaderAdapterAction action,
            ClassloaderReporter to, String errPrefix, String errSuffix) {
        try {
            return findAdapter(ctx, cl, action);
        } catch (AdapterException e) {
            switch (e.getReason()) {
            case AdapterException.NO_HANDLER:
                to.reportError(errPrefix
                        + " not investigatable (no Loaderhandler found)"
                        + errSuffix);
                break;
            case AdapterException.NO_ADAPTER:
                to
                        .reportError(errPrefix
                                + " not investigatable (Loaderhandler retrieves no adapter)"
                                + errSuffix);
                break;
            }
        }
        return null;
    }
    public static ClassLoaderAdapter findAdapter(ClassLoaderAdapterContext ctx,
            ClassLoader cl, ClassLoaderAdapterAction action, List errors,
            String errPrefix, String errSuffix) {
        try {
            return findAdapter(ctx, cl, action);
        } catch (AdapterException e) {
            switch (e.getReason()) {
            case AdapterException.NO_HANDLER:
                errors.add(errPrefix
                        + " not investigatable (no Loaderhandler found)"
                        + errSuffix);
                break;
            case AdapterException.NO_ADAPTER:
                errors
                        .add(errPrefix
                                + " not investigatable (Loaderhandler retrieves no adapter)"
                                + errSuffix);
                break;
            }
        }
        return null;
    }
    public static URL[] getBootstrapClasspathURLs() {
        try {
            Object urlClassPath = Class.forName("sun.misc.Launcher").getMethod(
                    "getBootstrapClassPath", null).invoke(null, null);
            return (URL[]) urlClassPath.getClass().getMethod("getURLs", null)
                    .invoke(urlClassPath, null);
        } catch (Exception e) {
            return null;
        }
    }
    private ClassloaderUtil() {
        super();
    }

}
