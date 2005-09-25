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

package org.apache.tools.ant.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Stack;
import java.util.StringTokenizer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.classloader.ClassloaderURLUtil;
import org.apache.tools.ant.taskdefs.condition.Os;

/**
 * This class encapsulates methods to create URLs.
 * @since Ant 1.7
 */

public final class URLUtils implements ClassloaderURLUtil {
    private static final FileUtils FILEUTILS = FileUtils.getFileUtils();
    private static final boolean ON_NETWARE = Os.isFamily("netware");
    private static URLUtils singleton = new URLUtils();
    /**
     * Gets the singleton instance.
     * @return The singleton instance.
     */
    public static URLUtils getURLUtils() {
        return singleton;
    }
    /**
     * creates a file from a absolute or relative file or url.
     * @param fileOrURL absolute or relative file or url
     * @return a file
     */
    public File createFile(String fileOrURL) {
        if (isURL(fileOrURL)) {
            return new File(FILEUTILS.fromURI(fileOrURL));
        }
        return new File(fileOrURL);
    }
    /**
     * creates a URL from a absolute or relative file or url
     * @param fileOrURL absolute or relative file or url
     * @return an URL
     * @throws MalformedURLException if <code>new URL()</code> throws it
     */
    public URL createURL(String fileOrURL) throws MalformedURLException {
        if (isURL(fileOrURL)) {
            return new URL(normalize(fileOrURL));
        }
        return FILEUTILS.getFileURL(new File(fileOrURL));
    }
    /**
     * indicates whether the denoted fileOrURL is absolute or relative.
     * @param fileOrURL absolute or relative file or url
     * @return true if <code>fileOrURL</code> denotes a absolute file or url
     *         , false if it is relative
     */
    public boolean isAbsolute(String fileOrURL)throws MalformedURLException {
        if (isURL(fileOrURL)) {
            URL url = new URL(transformFileSep(fileOrURL));
            String urlStr = url.toString();
            if (url.getProtocol() != null) {
                urlStr = urlStr.substring(url.getProtocol().length() + 1);
            }
            return urlStr.startsWith("/");
        }
        return new File(fileOrURL).isAbsolute();
    }
    /**
     * indicates whether the denoted fileOrURL is a file or a <code>file:</code> URL.
     * @param fileOrURL absolute or relative file or url
     * @return true if <code>fileOrURL</code> denotes a file or a <code>file:</code> URL
     *         , false otherwise
     */
    public boolean isFileOrFileURL(String fileOrURL) {
        if (!isURL(fileOrURL)) {
            return true;
        }
        return fileOrURL.startsWith("file:");
    }
    /**
     * indicates whether the denoted fileOrURL is an (absolute) url or not.
     * @param fileOrURL absolute or relative file or url
     * @return true if <code>fileOrURL</code> denotes a absolute url
     *         , false otherwise
     */
    public boolean isURL(String fileOrURL) {
        String x = transformFileSep(fileOrURL);
        if (x.startsWith("/")) {
            return false;
        }
        try {
            URL url = new URL(transformFileSep(fileOrURL));
            return (url.getProtocol() != null);
        } catch (MalformedURLException murlex) {
            return false;
        }
    }
    /**
     * normalizes an absolute or relative file or url
     * @param fileOrURL absolute or relative file or url
     * @return normalized file or url
     */
    public String normalize(String fileOrURL) throws MalformedURLException{
        if (!isURL(fileOrURL)) {
            return FILEUTILS.normalize(fileOrURL).toString();
        }
        String orig = fileOrURL;
        fileOrURL = transformFileSep(fileOrURL);
        fileOrURL = new URL(fileOrURL).toString();
        String root = fileOrURL.substring(0, fileOrURL.indexOf(':') + 1);
        fileOrURL = fileOrURL.substring(root.length());
        while (fileOrURL.startsWith("/")) {
            root += "/";
            fileOrURL = fileOrURL.substring(1);
        }

        int colon = fileOrURL.indexOf(':');
        if (colon >= 0) {
            if (ON_NETWARE) {
                root += fileOrURL.substring(0, colon + 1);
                fileOrURL = fileOrURL.substring(colon + 1);
            } else if (File.separatorChar == '\\'
                    && File.pathSeparatorChar == ';'
                    && colon == 1
                    && Character.isLetter(fileOrURL.charAt(0))) {
                 root += fileOrURL.substring(0, 2);
                 fileOrURL = fileOrURL.substring(2);
            }
            while (fileOrURL.startsWith("/")) {
                root += "/";
                fileOrURL = fileOrURL.substring(1);
            }
        }
        Stack s = new Stack();
        StringTokenizer tok = new StringTokenizer(fileOrURL, "/");
        while (tok.hasMoreTokens()) {
            String thisToken = tok.nextToken();
            if (".".equals(thisToken)) {
                //ignore
            } else if ("..".equals(thisToken)) {
                if (s.size() == 0) {
                    throw new BuildException("Cannot resolve path " + orig);
                }
                s.pop();
            } else { // plain component
                s.push(thisToken);
            }
        }

        StringBuffer sb = new StringBuffer(fileOrURL.length());
        sb.append(root);
        boolean isFirst = true;
        for (Iterator i = s.iterator(); i.hasNext();) {
            String x = (String) i.next();
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append('/');
            }
            sb.append(x);
        }
        if (fileOrURL.endsWith("/") && !((sb.charAt(sb.length() - 1) == '/'))) {
            sb.append('/');
        }
        return sb.toString();
    }
    /**
     * resolves an absolute or relative file or url with respect to a basedirectory.
     * @param fileOrURL absolute or relative file or url
     * @param dir base directory
     * @return resolved url
     * @throws MalformedURLException if URL can not created
     */
    public String resolve(String fileOrURL, File dir) throws MalformedURLException {

        if (isAbsolute(fileOrURL)) {
            return createURL(fileOrURL).toString();
        }
        if (!isFileOrFileURL(fileOrURL)) {
            return fileOrURL;
        }
        if (isURL(fileOrURL)) {
            String file = FILEUTILS.fromURI(transformFileSep(fileOrURL));
            return createURL(new File(dir, file).toString()).toString();
        }
        return createURL(new File(dir, fileOrURL).toString()).toString();
    }
    /**
     * transforms the fileseparators to url format ('/')
     * @param fileOrURL absolute or relative file or url
     * @return fileOrURL with '/'as fileSeparator
     */
    public String transformFileSep(String fileOrURL) {
        return fileOrURL.replace('\\', '/');
    }

    private URLUtils() {
    }

}
