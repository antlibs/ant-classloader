/*
 * Copyright  2001-2004 The Apache Software Foundation
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

import java.io.*;
import java.net.MalformedURLException;

import junit.framework.TestCase;

import org.apache.tools.ant.taskdefs.condition.Os;

/**
 * Tests for org.apache.tools.ant.util.URLUtils.
 *
 */
public class URLUtilsTest extends TestCase {

    private FileUtils fu;
    private File removeThis;
    private String current;
    private String root;

    public static final boolean isUnix = Os.isFamily("unix");
    public static boolean isWindows = Os.isFamily("windows"); 
    public URLUtilsTest(String name) {
        super(name);
    }

    public void setUp() {
        fu = FileUtils.getFileUtils();
        // Windows adds the drive letter in uppercase, unless you run Cygwin
        current = new File(".").getAbsolutePath();
        root = new File(File.separator).getAbsolutePath().toUpperCase();
    }

    public void tearDown() {
        if (removeThis != null && removeThis.exists()) {
            removeThis.delete();
        }
    }

    public void testCreateURL(String test,String expected,String fileOrURL) throws MalformedURLException {
        assertEquals(test,expected,URLUtils.createURL(fileOrURL).toString());
    }
    public void testNormalize(String test,String expected,String fileOrURL) {
        assertEquals(test,expected,URLUtils.normalize(fileOrURL));
    }
    public void testResolve(String test,String expected,String fileOrURL,File dir) throws MalformedURLException {
        assertEquals(test,expected,URLUtils.resolve(fileOrURL, dir));
    }

    public void testIsURL() {
        assertEquals(true,URLUtils.isURL("http://ant.apache.org"));
        assertEquals(true,URLUtils.isURL("file:/a/b"));
        assertEquals(true,URLUtils.isURL("file://a/b"));
        assertEquals(true,URLUtils.isURL("file:///a/b"));
        assertEquals(true,URLUtils.isURL("file:////a/b"));
        assertEquals(true,URLUtils.isURL("file:/C:/a/b"));
        assertEquals(true,URLUtils.isURL("file://C:/a/b"));
        assertEquals(true,URLUtils.isURL("file:///C:/a/b"));
        assertEquals(false,URLUtils.isURL("C:/a/b"));
        assertEquals(false,URLUtils.isURL("/a/b"));
        assertEquals(false,URLUtils.isURL("a/b"));
        assertEquals(false,URLUtils.isURL("//a/b"));
        assertEquals(false,URLUtils.isURL("///a/b"));
        assertEquals(false,URLUtils.isURL("."));
    }
    public void testIsFileOrFileURL() {
        assertEquals(false,URLUtils.isFileOrFileURL("http://ant.apache.org"));
        assertEquals(true,URLUtils.isFileOrFileURL("file:/a/b"));
        assertEquals(true,URLUtils.isFileOrFileURL("file://a/b"));
        assertEquals(true,URLUtils.isFileOrFileURL("file:///a/b"));
        assertEquals(true,URLUtils.isFileOrFileURL("file:////a/b"));
        assertEquals(true,URLUtils.isFileOrFileURL("file:/C:/a/b"));
        assertEquals(true,URLUtils.isFileOrFileURL("file://C:/a/b"));
        assertEquals(true,URLUtils.isFileOrFileURL("file:///C:/a/b"));
        assertEquals(true,URLUtils.isFileOrFileURL("C:/a/b"));
        assertEquals(true,URLUtils.isFileOrFileURL("/a/b"));
        assertEquals(true,URLUtils.isFileOrFileURL("a/b"));
        assertEquals(true,URLUtils.isFileOrFileURL("//a/b"));
        assertEquals(true,URLUtils.isFileOrFileURL("///a/b"));
        assertEquals(true,URLUtils.isFileOrFileURL("."));
    }
    public void testIsAbsolute() {
        assertEquals("2",true,URLUtils.isAbsolute("file:/a/b"));
        assertEquals("3",true,URLUtils.isAbsolute("file://a/b"));
        assertEquals("4",true,URLUtils.isAbsolute("file:///a/b"));
        assertEquals("5",true,URLUtils.isAbsolute("file:////a/b"));
        assertEquals("6",true,URLUtils.isAbsolute("file:/C:/a/b"));
        assertEquals("7",true,URLUtils.isAbsolute("file://C:/a/b"));
        assertEquals("8",true,URLUtils.isAbsolute("file:///C:/a/b"));
        assertEquals("9",true,URLUtils.isAbsolute("C:/a/b"));
        assertEquals("10",File.separatorChar == '/',URLUtils.isAbsolute("/a/b"));
        assertEquals("11",false,URLUtils.isAbsolute("a/b"));
        assertEquals("12",true,URLUtils.isAbsolute("//a/b"));
        assertEquals("13",true,URLUtils.isAbsolute("///a/b"));
        assertEquals("14",false,URLUtils.isAbsolute("."));
    }
    public void testCreateURL() throws MalformedURLException{
        String rootUrl = URLUtils.createURL(root).toString();
        String currentUrl = URLUtils.createURL(current).toString();

        testCreateURL("http"
                     , "http://ant.apache.org"
                     , "http://ant.apache.org");
        testCreateURL("file:1"
                     ,"file:/a/b"
                     ,"file:/a/b");
        testCreateURL("file:2"
                     ,"file://a/b"
                     ,"file://a/b");
        testCreateURL("file:3"
                     ,"file:/a/b"
                     ,"file:///a/b");
        testCreateURL("file:4"
                     ,"file://a/b"
                     ,"file:////a/b");
        testCreateURL("file:win"
                     ,"file:C:/a/b"
                     ,"file:C:/a/b");
        testCreateURL("file:win1"
                     ,"file:/C:/a/b"
                     ,"file:/C:/a/b");
        testCreateURL("file:win2"
                     ,"file://C:/a/b"
                     ,"file://C:/a/b");
        testCreateURL("file:win3"
                     ,"file:/C:/a/b"
                     ,"file:///C:/a/b");
        testCreateURL("current"
                     ,currentUrl
                     ,".");
        testCreateURL("rel"
                     ,currentUrl+"a/b"
                     ,"a/b");
        testCreateURL("unix"
                     ,rootUrl+"a/b"
                     ,"/a/b");
        if (isWindows) {
            testCreateURL("win"
                         ,"file:/C:/a/b"
                         ,"C:/a/b");
            testCreateURL("unc"
                         ,"file://a/b"
                         ,"//a/b");
        }
    }
    private String convertSlashes(String x) {
        return x.replace('/', File.separatorChar);
    }
    public void testNormalize() throws MalformedURLException{
        String rootUrl = URLUtils.createURL(root).toString();
        String currentUrl = URLUtils.createURL(current).toString();

        testNormalize("http"
                     , "http://ant.apache.org"
                     , "http://ant.apache.org");
        testNormalize("file:1"
                     ,"file:/a/b"
                     ,"file:/a/b");
        testNormalize("file:2"
                     ,"file://a/b"
                     ,"file://a/b");
        testNormalize("file:3"
                     ,"file:/a/b"
                     ,"file:///a/b");
        testNormalize("file:4"
                     ,"file://a/b"
                     ,"file:////a/b");
        testNormalize("file:win"
                     ,"file:C:/a/b"
                     ,"file:C:/a/b");
        testNormalize("file:win1"
                     ,"file:/C:/a/b"
                     ,"file:/C:/a/b");
        testNormalize("file:win2"
                     ,"file://C:/a/b"
                     ,"file://C:/a/b");
        testNormalize("file:win3"
                     ,"file:/C:/a/b"
                     ,"file:///C:/a/b");
        testNormalize("file: with dot dot 1"
                     ,"file:/C:/a"
                     ,"file:/C:/a/b/..");
        testNormalize("file: with dot dot 2"
                     ,"file:/C:/a/"
                     ,"file:/C:/a/b/../");
        testNormalize("file: with dot 1"
                     ,"file:/C:/a"
                     ,"file:/C:/a/.");
        testNormalize("file: with dot 2"
                     ,"file:/C:/a/"
                     ,"file:/C:/a/./");
        testNormalize("file: with dot dot 3"
                     ,"file:/C:/"
                     ,"file:/C:/a/..");
        testNormalize("file: with dot dot 4"
                     ,"file:/C:/"
                     ,"file:/C:/a/../");
        testNormalize("file: with dot 3"
                     ,"file:/C:/a"
                     ,"file:/C:/a/.");
        testNormalize("file: with dot 4"
                     ,"file:/C:/a/"
                     ,"file:/C:/a/./");
        if (isWindows) {
            testNormalize("file with dot"
                         ,"C:\\a"
                         ,"C:/a/.");
            testNormalize("file with dot dot"
                         ,"C:\\"
                         ,"C:/a/..");
            testNormalize("win"
                         ,convertSlashes("C:/a/b")
                         ,"C:/a/b");
            testNormalize("unc"
                         ,convertSlashes("//a/b")
                         ,"//a/b");
        }
        if (isUnix) {
            testNormalize("unix"
                         ,convertSlashes("/a/b")
                         ,"/a/b");
        }
    }
    public void testResolve() throws MalformedURLException{
        String rootUrl = URLUtils.createURL(root).toString();
        String currentUrl = URLUtils.createURL(current).toString();

        File dir = new File("C:/a");
        String dirUrl = URLUtils.createURL(dir.getAbsolutePath()).toString();
         
        testResolve("http"
                     , "http://ant.apache.org"
                     , "http://ant.apache.org", dir);
        testResolve("file:1"
                     ,"file:/a/b"
                     ,"file:/a/b", dir);
        testResolve("file:2"
                     ,"file://a/b"
                     ,"file://a/b", dir);
        testResolve("file:3"
                     ,"file:/a/b"
                     ,"file:///a/b", dir);
        testResolve("file:4"
                     ,"file://a/b"
                     ,"file:////a/b", dir);
        testResolve("file:win1"
                     ,"file:/C:/a/b"
                     ,"file:/C:/a/b", dir);
        testResolve("file:win2"
                     ,"file://C:/a/b"
                     ,"file://C:/a/b", dir);
        testResolve("file:win3"
                     ,"file:/C:/a/b"
                     ,"file:///C:/a/b", dir);
        testResolve("rel 1"
                     ,dirUrl+"/a"
                     ,"a", dir);
        if (isWindows) {
            testResolve("unc"
                         ,"file://a/b"
                         ,"//a/b", dir);
        }
    }

}
