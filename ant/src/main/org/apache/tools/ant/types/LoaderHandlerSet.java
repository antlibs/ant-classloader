/*
 * Copyright  2002-2004 The Apache Software Foundation
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Classloader;

/**
 * Set of ClassLoaderHandlers.
 * @since Ant 1.7
 */
public class LoaderHandlerSet extends DataType {

    private static class HandlerHolder {
        private final LoaderHandler handler;
        private final Class loaderClass;
        public HandlerHolder(LoaderHandler handler, Class loaderClass) {
            this.handler = handler;
            this.loaderClass = loaderClass;
        }
    }
    private boolean addDefault = true;
    private boolean addAll = false;
    private final ArrayList handlerList = new ArrayList();
    private final HashSet handlerSet = new HashSet();

    /**
     * Default Constructor.
     */
    private LoaderHandlerSet() {
    }
    /**
     * Constructor.
     * @param project current project
     */
    public LoaderHandlerSet(Project project) {
        setProject(project);
        LoaderHandler.addPredefined(project);
    }
    /**
     * Constructor for setXXX
     * @param project current project
     * @param refid reference id
     */
    public LoaderHandlerSet(Project project, Reference refid) {
        this(project);
        setRefid(refid);
    }
    /**
     * sets a nested handler element.
     * @param handler the handler to add.
     */
    public void addConfiguredHandler(LoaderHandler handler) {
        checkChildrenAllowed();
        handler.check();
        if (handlerSet.add(handler.getLoader())) {
            handlerList.add(handler);
        }
    }
    /**
     * gets the best fitting LoaderHandler for a classloader
     * and a required action.
     * @param task the calling classloader task.
     * @param loader the ClassLoader to find a handler for.
     * @param action the required action.
     * @return the best fitting LoaderHandler or null if an error occured.
     */
    public LoaderHandler getHandler(
        Classloader task,
        ClassLoader loader,
        Classloader.Action action) {
        if (isReference()) {
            LoaderHandlerSet r = (LoaderHandlerSet) getCheckedRef(LoaderHandlerSet.class
                                                                , "loaderHandlerSet");
            return r.getHandler(task, loader, action);
        }
        if (addAll) {
            LoaderHandler[] allHandlers = LoaderHandler.getAllHandlers(getProject());
            for (int i = 0; i < allHandlers.length; i++) {
                addConfiguredHandler(allHandlers[i]);
            }
            addAll = false;
            addDefault = false;
        }
        if (addDefault) {
            LoaderHandler[] defHandlers = LoaderHandler.getDefaultHandlers(getProject());
            for (int i = 0; i < defHandlers.length; i++) {
                addConfiguredHandler(defHandlers[i]);
            }
            addDefault = false;
        }
        ArrayList holderList = new ArrayList();
        for (Iterator i = handlerList.iterator(); i.hasNext();) {
            LoaderHandler handler = (LoaderHandler) i.next();
            Class loaderClass = handler.getLoaderClass(task, loader, action);
            if (loaderClass != null) {
                holderList.add(new HandlerHolder(handler, loaderClass));
            }
        }
        if (holderList.size() == 0) {
            task.handleError("No Handler found for ClassLoader "
                           + loader.getClass().getName()
                           + " and action "
                           + action);
            return null;
        }
        HandlerHolder[] holders =
            (HandlerHolder[]) holderList.toArray(
                new HandlerHolder[holderList.size()]);
        for (int current = 1; current < holders.length; current++) {
            boolean changed = false;
            for (int sorted = 0; (!changed) && (sorted < current); sorted++) {
                //if an already sorted loader (dest) is assignable from
                //the current loader, the current loader
                //has to been tested before the already sorted one.
                if (holders[sorted]
                    .loaderClass
                    .isAssignableFrom(holders[current].loaderClass)) {
                    HandlerHolder temp = holders[current];
                    System.arraycopy(
                        holders,
                        sorted,
                        holders,
                        sorted + 1,
                        current - sorted);
                    holders[sorted] = temp;
                    changed = true;
                }
            }
        }
        return holders[0].handler;
    }
    /**
     * sets addDefault attribute
     * @param onOff true, to add the default handlers to this set.
     */
    public void setAddDefault(boolean onOff) {
        checkAttributesAllowed();
        addDefault = onOff;
    }
    /**
     * sets addall attribute
     * @param onOff true, to add all handlers to this set.
     */
    public void setAddAll(boolean onOff) {
        checkAttributesAllowed();
        addAll = onOff;
    }
    /**
     * sets a handler attribute.
     * @param handler the handler to add.
     */
    public void setHandler(LoaderHandler handler) {
        checkAttributesAllowed();
        handler.check();
        if (handlerSet.add(handler.getLoader())) {
            handlerList.add(handler);
        }
    }
}
