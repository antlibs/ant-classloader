/*
 * Copyright  2004 The Apache Software Foundation
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

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.util.URLUtils;

import junit.framework.TestCase;

import java.io.File;
import java.net.MalformedURLException;

/**
 * JUnit testcases for org.apache.tools.ant.types.URLPath
 */

public class URLPathTest extends TestCase {

    public static boolean isUnixStyle = File.pathSeparatorChar == ':';
    public static boolean isNetWare = Os.isFamily("netware");
    private String current;
    private String root;

    private Project project;

    public URLPathTest(String name) {
        super(name);
    }

    public void setUp() {
        project = new Project();
        project.setBasedir(".");
        current = new File(".").getAbsolutePath();
        root = new File(File.separator).getAbsolutePath().toUpperCase();
    }

    public void testRelativePathUnixStyle() {
        project.setBasedir(project.getBaseDir().getAbsolutePath()+"/src/etc");
        URLPath p = new URLPath(project, "..;testcases");
        String[] l = p.list();
        assertEquals("two items, Unix style", 2, l.length);
        assertTrue("test '..' resolved relative to src/etc ("+l[0]+")",
                 l[0].endsWith("/src/"));
        assertTrue("test 'testcases' resolved relative to src/etc ("+l[1]+")",
                 l[1].endsWith("/src/etc/testcases/"));
    }

    public void testSetLocation() throws MalformedURLException {
        String rootUrl = URLUtils.createURL(root).toString();
        String currentUrl = URLUtils.createURL(current).toString();
        URLPath p = new URLPath(project);
        p.setLocation(new File(File.separatorChar+"a").toString());
        String[] l = p.list();
        assertEquals(1, l.length);
        assertEquals("1",rootUrl+"a",l[0]);
    }

    public void testAppending() {
        URLPath p = new URLPath(project, "/a;/b");
        String[] l = p.list();
        assertEquals("2 after construction", 2, l.length);
        p.setLocation("/c");
        l = p.list();
        assertEquals("3 after setLocation", 3, l.length);
        p.setPath("\\d;\\e");
        l = p.list();
        assertEquals("5 after setPath", 5, l.length);
        p.append(new URLPath(project, "\\f"));
        l = p.list();
        assertEquals("6 after append", 6, l.length);
        p.createPath().setLocation(new File("/g"));
        l = p.list();
        assertEquals("7 after append", 7, l.length);
    }

    public void testEmpyPath() {
        URLPath p = new URLPath(project, "");
        String[] l = p.list();
        assertEquals("0 after construction", 0, l.length);
        p.setPath("");
        l = p.list();
        assertEquals("0 after setPath", 0, l.length);
        p.append(new URLPath(project));
        l = p.list();
        assertEquals("0 after append", 0, l.length);
        p.createPath();
        l = p.list();
        assertEquals("0 after append", 0, l.length);
    }

    public void testUnique() {
        URLPath p = new URLPath(project, "/a;/a");
        String[] l = p.list();
        assertEquals("1 after construction", 1, l.length);
        p.setLocation(File.separatorChar+"a");
        l = p.list();
        assertEquals("1 after setLocation", 1, l.length);
        p.setPath("\\a:/a");
        l = p.list();
        String x=l[0];
        for(int i=1;i<l.length;i++)
            x+=";"+l[i];
        assertEquals("1 after setPath ("+x+")", 1, l.length);
        p.append(new URLPath(project, "/a;\\a;\\a"));
        l = p.list();
        assertEquals("1 after append new", 1, l.length);
        p.createPath().setPath("\\a;/a");
        l = p.list();
        assertEquals("1 after append", 1, l.length);
    }

    public void testEmptyElementIfIsReference() {
        URLPath p = new URLPath(project, "/a;/a");
        try {
            p.setRefid(new Reference("dummyref"));
            fail("Can add reference to Path with elements from constructor");
        } catch (BuildException be) {
            assertEquals("You must not specify more than one attribute when using refid",
                         be.getMessage());
        }

        p = new URLPath(project);
        p.setLocation("/a");
        try {
            p.setRefid(new Reference("dummyref"));
            fail("Can add reference to Path with elements from setLocation");
        } catch (BuildException be) {
            assertEquals("You must not specify more than one attribute when using refid",
                         be.getMessage());
        }

        URLPath another = new URLPath(project, "/a;/a");
        project.addReference("dummyref", another);
        p = new URLPath(project);
        p.setRefid(new Reference("dummyref"));
        try {
            p.setLocation("/a");
            fail("Can set location in Path that is a reference.");
        } catch (BuildException be) {
            assertEquals("You must not specify more than one attribute when using refid",
                         be.getMessage());
        }

        try {
            p.setPath("/a;\\a");
            fail("Can set path in Path that is a reference.");
        } catch (BuildException be) {
            assertEquals("You must not specify more than one attribute when using refid",
                         be.getMessage());
        }

        try {
            p.createPath();
            fail("Can create nested Path in Path that is a reference.");
        } catch (BuildException be) {
            assertEquals("You must not specify nested elements when using refid",
                         be.getMessage());
        }

        try {
            p.createPathElement();
            fail("Can create nested PathElement in Path that is a reference.");
        } catch (BuildException be) {
            assertEquals("You must not specify nested elements when using refid",
                         be.getMessage());
        }

        try {
            p.addFileset(new FileSet());
            fail("Can add nested FileSet in Path that is a reference.");
        } catch (BuildException be) {
            assertEquals("You must not specify nested elements when using refid",
                         be.getMessage());
        }

        try {
            p.addFilelist(new FileList());
            fail("Can add nested FileList in Path that is a reference.");
        } catch (BuildException be) {
            assertEquals("You must not specify nested elements when using refid",
                         be.getMessage());
        }

        try {
            p.addDirset(new DirSet());
            fail("Can add nested Dirset in Path that is a reference.");
        } catch (BuildException be) {
            assertEquals("You must not specify nested elements when using refid",
                         be.getMessage());
        }
    }

    public void testCircularReferenceCheck() {
        URLPath p = new URLPath(project);
        project.addReference("dummy", p);
        p.setRefid(new Reference("dummy"));
        try {
            p.list();
            fail("Can make Path a Reference to itself.");
        } catch (BuildException be) {
            assertEquals("This data type contains a circular reference.",
                         be.getMessage());
        }

        // dummy1 --> dummy2 --> dummy3 --> dummy1
        URLPath p1 = new URLPath(project);
        project.addReference("dummy1", p1);
        URLPath p2 = p1.createUrlpath();
        project.addReference("dummy2", p2);
        URLPath p3 = p2.createUrlpath();
        project.addReference("dummy3", p3);
        p3.setRefid(new Reference("dummy1"));
        try {
            p1.list();
            fail("Can make circular reference.");
        } catch (BuildException be) {
            assertEquals("This data type contains a circular reference.",
                         be.getMessage());
        }

        // dummy1 --> dummy2 --> dummy3 (with Path "/a")
        p1 = new URLPath(project);
        project.addReference("dummy1", p1);
        p2 = p1.createUrlpath();
        project.addReference("dummy2", p2);
        p3 = p2.createUrlpath();
        project.addReference("dummy3", p3);
        p3.setLocation("/a");
        String[] l = p1.list();
        assertEquals("One element buried deep inside a nested path structure",
                     1, l.length);
    }

    public void testFileList()throws Exception {
        URLPath p = new URLPath(project);
        FileList f = new FileList();
        f.setProject(project);
        f.setDir(project.resolveFile("."));
        f.setFiles("build.xml");
        p.addFilelist(f);
        String[] l = p.list();
        assertEquals(1, l.length);
        assertEquals(project.resolveFile("build.xml").getAbsoluteFile().toURL().toExternalForm(), l[0]);
    }

    public void testFileSet()throws Exception {
        URLPath p = new URLPath(project);
        FileSet f = new FileSet();
        f.setProject(project);
        f.setDir(project.resolveFile("."));
        f.setIncludes("build.xml");
        p.addFileset(f);
        String[] l = p.list();
        assertEquals(1, l.length);
        assertEquals(project.resolveFile("build.xml").getAbsoluteFile().toURL().toExternalForm(), l[0]);
    }

    public void testDirSet()throws Exception {
        URLPath p = new URLPath(project);
        DirSet d = new DirSet();
        d.setProject(project);
        d.setDir(project.resolveFile("."));
        d.setIncludes("src");
        p.addDirset(d);
        String[] l = p.list();
        assertEquals(1, l.length);
        assertEquals(project.resolveFile("src").getAbsoluteFile().toURL().toExternalForm(), l[0]);
    }

}
