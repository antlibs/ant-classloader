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

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.classloader.ClassloaderContext;
import org.apache.tools.ant.taskdefs.classloader.ClassLoaderHandlerSet;
import org.apache.tools.ant.taskdefs.classloader.ClassloaderURLUtil;
import org.apache.tools.ant.taskdefs.classloader.ClassloaderUtil;
import org.apache.tools.ant.taskdefs.classloader.SimpleClassloaderUtil;
import org.apache.tools.ant.types.LoaderHandlerSet;
import org.apache.tools.ant.util.URLUtils;

/**
 * Base class for ClassloaderTask and ClassloaderReport.
 * @since Ant 1.7
 */
public class ClassloaderBase extends Task implements ClassloaderContext {
    private boolean failOnError;
    private ClassLoaderHandlerSet handlerSet = null;
    /**
     * Default constructor
     */
    public ClassloaderBase() {
    }
    /**
     * Sets a nested HandlerSet element.
     *
     * @param handlerSet
     *            The handlerSet
     */
    public void addHandlerSet(LoaderHandlerSet handlerSet) {
        if (this.handlerSet != null) {
            throw new BuildException(
                    "nested element handlerSet may only specified once");
        }
        this.handlerSet = handlerSet;
    }
    /**
     * Sets a HandlerSet ref.
     *
     * @param handlerSet
     *            The handlerSet
     */
    public void setHandlerSet(LoaderHandlerSet handlerSet) {
        this.handlerSet = handlerSet;
    }
    /**
     * Creates a new handlerset.
     * @return The newly created handlerset.
     */
    protected LoaderHandlerSet newHandlerSet() {
        return new LoaderHandlerSet(getProject());
    }
    /**
     * Gets the handlerset to analyze a given classloader with.
     *
     * @return The handlerset.
     */
    public ClassLoaderHandlerSet getHandlerSet() {
        if (handlerSet == null) {
            handlerSet = newHandlerSet();
        }
        return handlerSet;
    }
    /**
     * This implementation logs the msg with MSG_WARN.
     * @param msg message.
     */
    public void handleWarning(String msg) {
        log(msg, Project.MSG_WARN);
    }
    /**
     * This implementation logs the msg with MSG_DEBUG.
     * @param msg message.
     */
    public void handleDebug(String msg) {
        log(msg, Project.MSG_DEBUG);
    }

    /**
     * Handles an error with respect to the failonerror attribute.
     *
     * @param msg
     *            Error message.
     */
    public void handleError(String msg) {
        handleError(msg, null, null);
    }
    /**
     * Handles an error with respect to the failonerror attribute.
     *
     * @param msg
     *            Error message.
     * @param ex
     *            Causing exception.
     */
    public void handleError(String msg, Throwable ex) {
        handleError(msg, ex, null);
    }
    /**
     * Handles an error with respect to the failonerror attribute.
     *
     * @param msg
     *            Error message.
     * @param ex
     *            Causing exception.
     * @param loc
     *            Location.
     */
    public void handleError(String msg, Throwable ex, Location loc) {
        if (loc == null) {
            loc = this.getLocation();
        }
        if ((msg == null) && (ex != null)) {
            msg = ex.getMessage();
        }
        if (failOnError) {
            throw new BuildException(msg, ex, loc);
        }
        log(loc + "Error: " + msg, Project.MSG_ERR);
    }
    /**
     * Indicates whether build should fail on error.
     * @return true, if build should fail on error; false otherwise.
     */
    public boolean isFailOnError() {
        return failOnError;
    }
    /**
     * Sets the failonerror attribute.
     *
     * @param onOff Value.
     */
    public void setFailonerror(boolean onOff) {
        this.failOnError = onOff;
    }
    /**
     * Gets the url utilities.
     * @return The url utilities.
     */
    public ClassloaderURLUtil getURLUtil() {
        return URLUtils.getURLUtils();
    }
    /**
     * Gets the utilities.
     * @return The utilities.
     */
    public ClassloaderUtil getUtil() {
        return SimpleClassloaderUtil.getClassLoaderUtil();
    }

}
