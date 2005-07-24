import java.net.URL;
import java.net.URLClassLoader;

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

public class TestCommonClassloaders {
    private static void analyze(URLClassLoader classloader) {
        boolean isParentAssigned = classloader.getParent() != null;
        System.out.println("  -  parent "
                + (isParentAssigned ? "is" : "IS NOT") + " assigned");
        URL[] urls = classloader.getURLs();
        if (urls == null)
            System.out.println("  -  urls: null");
        else {
            System.out.println("  -  urls: " + urls.length + " entries");
            for (int i = 0; i < urls.length; i++)
                System.out.println("        -> " + urls[i]);
        }
    }
    public static void main(String[] args) {
        URLClassLoader systemClassloader = (URLClassLoader) ClassLoader
                .getSystemClassLoader();
        boolean isSystemClassloader = TestCommonClassloaders.class
                .getClassLoader() == systemClassloader;
        System.out.println("Current ClassLoader "
                + (isSystemClassloader ? "is" : "IS NOT")
                + " System Classloader");
        System.out.println("------------------");
        System.out.println("java.class.path: "
                + System.getProperty("java.class.path"));
        System.out.println("System Classloader");
        analyze(systemClassloader);
        System.out.println("------------------");
        URLClassLoader extensionClassloader = (URLClassLoader) systemClassloader
                .getParent();
        System.out.println("java.ext.dirs: "
                + System.getProperty("java.ext.dirs"));
        System.out.println("Extension ClassLoader");
        analyze(extensionClassloader);
        System.out.println("------------------");
        System.out.println("sun.boot.class.path: "
                + System.getProperty("sun.boot.class.path"));
    }
}
