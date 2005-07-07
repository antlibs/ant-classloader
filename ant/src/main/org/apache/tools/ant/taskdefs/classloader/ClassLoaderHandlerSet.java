/*
 * Copyright 2005 The JTools Project
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

public interface ClassLoaderHandlerSet {
    /**
     * Gets the best fitting LoaderHandler for a classloader
     * and a required action.
     * @param context The calling classloader task.
     * @param loader The ClassLoader to find a handler for.
     * @param action The required action.
     * @return The best fitting LoaderHandler or null if an error occured.
     */
    ClassLoaderHandler getHandler(
        ClassLoaderAdapterContext context,
        ClassLoader loader,
        ClassLoaderAdapterAction action);
    
}
