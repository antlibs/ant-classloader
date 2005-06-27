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

package org.apache.tools.ant.types;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.URLUtils;

/**
 * This object represents an url path as used by classloader task.
 * <p>
 * <code>
 * &lt;sometask&gt;<br>
 * &nbsp;&nbsp;&lt;somepath&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;pathelement location="/path/to/file.jar" /&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;pathelement path="/path/to/file2.jar:/path/to/class2;
 *                                               /path/to/class3" /&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;urlpathelement location="http://my.domain/my.jar" /&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;urlpathelement path="http://your.domain/your.1st.jar;
 *                                                  http://your.domain/your.2nd.jar" /&gt;<br>
 * &nbsp;&nbsp;&lt;/somepath&gt;<br>
 * &lt;/sometask&gt;<br>
 * </code>
 * <p>
 * The object implemention <code>sometask</code> must provide a method called
 * <code>createSomepath</code> which returns an instance of <code>URLPath</code>.
 * Nested path definitions are handled by the Path object and must be labeled
 * <code>pathelement</code> or <code>urlpathelement</code>.<p>
 *
 * The path element takes a parameter <code>path</code> which will be parsed
 * and split into single elements. It will usually be used
 * to define a path from an environment variable.
 */

public class URLPath extends DataType implements Cloneable {

    /**
     * Helper class, holds the nested <code>&lt;urlpathelement&gt;</code> values.
     */
    public class URLPathElement {
        private String[] parts = null;
        private void addToPath(Path result) {
            if (parts == null) {
                return;
            }
            for (int i = 0; i < parts.length; i++) {
                result.createPathElement().setLocation(
                    URLUtils.createFile(parts[i]));
            }
        }
        /**
         * gets the parts of this urlpathelement
         * @return array of parts
         */
        public String[] getParts() {
            return parts;
        }
        /**
         * sets the location attribute
         * @param loc url or file
         */
        public void setLocation(String loc) {
            if (parts != null) {
                throw new BuildException("either location or path can be defined");
            }
            if (loc != null) {
                loc = translateFile(loc);
                if (loc != null) {
                    parts = new String[] {loc };
                }
            }
        }
        /**
         * sets the path attribute
         * @param path ';' separated list of files or urls
         */
        public void setPath(String path) {
            if (parts != null) {
                throw new BuildException("either location or path can be defined");
            }
            parts = translateUrlPath(path);
        }
    }

    /**
     * Adds a String to the Vector if it isn't already included.
     */
    private static void addUnlessPresent(ArrayList v, Set set, String s) {
        if (set.add(s)) {
            v.add(s);
        }
    }

    /**
     * Resolve a filename with Project's help - if we know one that is.
     *
     * <p>Assume the filename is absolute if project is null.</p>
     */
    private static String resolveURL(Project project, String relativeName) {
        if (project != null) {
            try {
                return URLUtils.resolve(relativeName, project.getBaseDir());
            } catch (MalformedURLException murlex) {
                return relativeName;
            }
        }
        return relativeName;
    }

    private ArrayList elements;
    /**
     * simple constructor
     * @param project current project
     */
    public URLPath(Project project) {
        setProject(project);
        elements = new ArrayList();
    }

    /**
     * Invoked by IntrospectionHelper for <code>setXXX(URLPath p)</code>
     * attribute setters.
     * @param p current project
     * @param path a path like in <code>Path</Path>
     * @see Path
     */
    public URLPath(Project p, String path) {
        this(p);
        createPathElement().setPath(path);
    }

    /**
     * Adds a nested <code>&lt;dirset&gt;</code> element.
     * @param dset a <code>DirSet</code>
     */
    public void addDirset(DirSet dset) {
        checkChildrenAllowed();
        elements.add(dset);
        setChecked(false);
    }

    /**
    * Adds the components on the given path which exist to this
    * Path. Components that don't exist, aren't added.
    * @param source - source path whose components are examined for existence
    */
    public void addExisting(URLPath source) {
        addExisting(source, false);
    }

    /** Same as addExisting, but support classpath behavior if tryUserDir
     * is true. Classpaths are relative to user dir, not the project base.
     * That used to break jspc test
     * @param source a urlpath
     * @param tryUserDir true, to activate classpath behaviour
     */
    public void addExisting(URLPath source, boolean tryUserDir) {
        String[] list = source.list();
        File userDir =
            (tryUserDir) ? new File(System.getProperty("user.dir")) : null;

        for (int i = 0; i < list.length; i++) {
            File f = null;
            if (getProject() != null) {
                f = getProject().resolveFile(list[i]);
            } else {
                f = new File(list[i]);
            }
            // probably not the best choice, but it solves the problem of
            // relative paths in CLASSPATH
            if (tryUserDir && !f.exists()) {
                f = new File(userDir, list[i]);
            }
            if (f.exists()) {
                setLocation(f.toString());
            } else {
                log(
                    "dropping " + f + " from path as it doesn't exist",
                    Project.MSG_VERBOSE);
            }
        }
    }

    /**
     * Emulation of extdirs feature in java >= 1.2.
     * This method adds all files in the given
     * directories (but not in sub-directories!) to the classpath,
     * so that you don't have to specify them all one by one.
     * @param extdirs - Path to append files to
     */
    public void addExtdirs(Path extdirs) {
        checkChildrenAllowed();
        if (extdirs == null) {
            String extProp = System.getProperty("java.ext.dirs");
            if (extProp != null) {
                extdirs = new Path(getProject(), extProp);
            } else {
                return;
            }
        }

        String[] dirs = extdirs.list();
        for (int i = 0; i < dirs.length; i++) {
            File dir = getProject().resolveFile(dirs[i]);
            if (dir.exists() && dir.isDirectory()) {
                FileSet fs = new FileSet();
                fs.setDir(dir);
                fs.setIncludes("*");
                addFileset(fs);
            }
        }
    }

    /**
     * Adds a nested <code>&lt;filelist&gt;</code> element.
     * @param fl a filelist
     */
    public void addFilelist(FileList fl) {
        checkChildrenAllowed();
        elements.add(fl);
        setChecked(false);
    }

    /**
     * Adds a nested <code>&lt;fileset&gt;</code> element.
     * @param fs a fileset
     */
    public void addFileset(FileSet fs) {
        checkChildrenAllowed();
        elements.add(fs);
        setChecked(false);
    }

    /**
     * Add the Java Runtime classes to this Path instance.
     */
    public void addJavaRuntime() {
        createPath().addJavaRuntime();
    }

    /**
     * Adds a nested path
     * @param path the nested path
     */
    public void addPath(Path path) {
        checkChildrenAllowed();
        elements.add(path);
        setChecked(false);

    }
    /**
     * Adds some reference that might be a fileset, afilelist,
     * a dirset, a path or an urlpath.
     * @param id the reference
     */
    public void addReference(Reference id) {
        checkChildrenAllowed();
        elements.add(id);
        setChecked(false);
    }

    private Path addToPath(Path result) {
        if (result == null) {
            result = new Path(getProject());
        }
        for (int i = 0; i < elements.size(); i++) {
            Object o = elements.get(i);
            if (o instanceof Reference) {
                Reference r = (Reference) o;
                o = r.getReferencedObject(getProject());
            }

            if (o instanceof URLPathElement) {
                ((URLPathElement) o).addToPath(result);
            } else if (o instanceof URLPath) {

                ((URLPath) o).addToPath(result);
            } else if (o instanceof Path) {
                result.add((Path) o);
            } else if (o instanceof DirSet) {
                result.addDirset((DirSet) o);
            } else if (o instanceof FileSet) {
                result.addFileset((FileSet) o);
            } else if (o instanceof FileList) {
                result.addFilelist((FileList) o);
            }
        }
        return result;
    }

    /**
     * Adds absolute path names of listed files in the given directory
     * to the Vector if they are not already included.
     * @param v the list
     * @param set a set
     * @param dir the basedir
     * @param s an array of absolute or relative files or urls
     */
    private void addUnlessPresent(ArrayList v, Set set, File dir, String[] s) {
        for (int j = 0; j < s.length; j++) {
            try {
                String absolutePath = URLUtils.resolve(s[j], dir);
                addUnlessPresent(v, set, absolutePath);
            } catch (MalformedURLException murlex) {
                //maybe a non file url
                addUnlessPresent(v, set, s[j]);
            }
        }
    }
    /**
     * Adds a nested url path
     * @param path the urlpath
     */
    public void addURLPath(URLPath path) {
        checkChildrenAllowed();
        elements.add(path);
        setChecked(false);

    }

    /**
     * Append the contents of the other Path instance to this.
     * @param other the other urlpath
     */
    public void append(URLPath other) {
        if (other == null) {
            return;
        }
        for (Iterator i = other.elements.iterator(); i.hasNext();) {
            elements.add(i.next());
        }
        setChecked(false);
    }

    /**
     * Return a Path that holds the same elements as this instance.
     * @return the cloned urlpath
     */
    public Object clone() {
        try {
            URLPath p = (URLPath) super.clone();
            p.elements = (ArrayList) elements.clone();
            return p;
        } catch (CloneNotSupportedException e) {
            throw new BuildException(e);
        }
    }

    /**
     * Concatenates the system class path in the order specified by
     * the ${build.sysclasspath} property - using &quot;last&quot; as
     * default value.
     * @return the newly created urlpath
     */
    public URLPath concatSystemClasspath() {
        return concatSystemClasspath("last");
    }

    /**
     * Concatenates the system class path in the order specified by
     * the ${build.sysclasspath} property - using the supplied value
     * if ${build.sysclasspath} has not been set.
     * @param defValue default order
     * @return the newly created urlpath
     */
    public URLPath concatSystemClasspath(String defValue) {

        URLPath result = new URLPath(getProject());

        String order = defValue;
        if (getProject() != null) {
            String o = getProject().getProperty("build.sysclasspath");
            if (o != null) {
                order = o;
            }
        }

        if (order.equals("only")) {
            // only: the developer knows what (s)he is doing
            result.addExisting(new URLPath(null, System.getProperty("java.class.path")), true);

        } else if (order.equals("first")) {
            // first: developer could use a little help
            result.addExisting(new URLPath(null, System.getProperty("java.class.path")), true);
            result.addExisting(this);

        } else if (order.equals("ignore")) {
            // ignore: don't trust anyone
            result.addExisting(this);

        } else {
            // last: don't trust the developer
            if (!order.equals("last")) {
                log(
                    "invalid value for build.sysclasspath: " + order,
                    Project.MSG_WARN);
            }

            result.addExisting(this);
            result.addExisting(new URLPath(null, System.getProperty("java.class.path")), true);
        }

        return result;

    }

    /**
     * Creates a nested <code>&lt;path&gt;</code> element.
     * @return the new path
     */
    public Path createPath() {
        checkChildrenAllowed();
        Path p = new Path(getProject());
        elements.add(p);
        setChecked(false);
        return p;
    }

    /**
     * Creates the nested <code>&lt;pathelement&gt;</code> element.
     * @return the new pathelement
     */
    public Path.PathElement createPathElement() {
        checkChildrenAllowed();
        return createPath().createPathElement();
    }
    /**
     * Creates the nested <code>&lt;urlpath&gt;</code> element.
     * @return the new urlpath
     */
    public URLPath createUrlpath() {
        checkChildrenAllowed();
        URLPath p = new URLPath(getProject());
        elements.add(p);
        setChecked(false);
        return p;
    }

    /**
     * Creates the nested <code>&lt;urlpathelement&gt;</code> element.
     * @return the new urlpathelement
     */
    public URLPathElement createUrlpathelement() {
        checkChildrenAllowed();
        URLPathElement pe = new URLPathElement();
        elements.add(pe);
        return pe;
    }

    /**
     * Overrides the version of DataType to recurse on all DataType
     * child elements that may have been added.
     * @param stk the stack
     * @param p the project
     * @throws BuildException on circular reference
     */
    public void dieOnCircularReference(final Stack stk, final Project p)
        throws BuildException {

        if (isChecked()) {
            return;
        }
        Iterator e = elements.iterator();
        while (e.hasNext()) {
            Object o = e.next();
            if (o instanceof Reference) {
                o = ((Reference) o).getReferencedObject(p);
            }

            if (o instanceof DataType) {
                if (stk.contains(o)) {
                    throw circularReference();
                } else {
                    stk.push(o);
                    ((DataType) o).dieOnCircularReference(stk, p);
                    stk.pop();
                }
            }
        }
        setChecked(true);
    }
    /**
     * Returns all path elements defined by this and nested path objects.
     * @return list of path elements.
     */
    public String[] list() {

        if (!isChecked()) {
            // make sure we don't have a circular reference here
            Stack stk = new Stack();
            stk.push(this);
            dieOnCircularReference(stk, getProject());
        }

        ArrayList result = new ArrayList(2 * elements.size());
        HashSet set = new HashSet();
        for (int i = 0; i < elements.size(); i++) {
            Object o = elements.get(i);
            if (o instanceof Reference) {
                Reference r = (Reference) o;
                o = r.getReferencedObject(getProject());
            }

            if (o instanceof URLPathElement) {
                String[] parts = ((URLPathElement) o).getParts();
                if (parts == null) {
                    throw new BuildException(
                        "You must either set location or"
                            + " path on <urlpathelement>");
                }
                for (int j = 0; j < parts.length; j++) {
                    addUnlessPresent(
                        result,
                        set,
                        resolveURL(getProject(), parts[j]));
                }
            } else if (o instanceof URLPath) {
                URLPath p = (URLPath) o;
                if (p.getProject() == null) {
                    p.setProject(getProject());
                }
                String[] parts = p.list();
                for (int j = 0; j < parts.length; j++) {
                    addUnlessPresent(
                        result,
                        set,
                        resolveURL(p.getProject(), parts[j]));
                }
            } else if (o instanceof Path) {
                Path p = (Path) o;
                if (p.getProject() == null) {
                    p.setProject(getProject());
                }
                String[] parts = p.list();
                for (int j = 0; j < parts.length; j++) {
                    addUnlessPresent(
                        result,
                        set,
                        resolveURL(p.getProject(), parts[j]));
                }
            } else if (o instanceof DirSet) {
                DirSet dset = (DirSet) o;
                DirectoryScanner ds = dset.getDirectoryScanner(getProject());
                String[] s = ds.getIncludedDirectories();
                File dir = dset.getDir(getProject());
                addUnlessPresent(result, set, dir, s);
            } else if (o instanceof FileSet) {
                FileSet fs = (FileSet) o;
                DirectoryScanner ds = fs.getDirectoryScanner(getProject());
                String[] s = ds.getIncludedFiles();
                File dir = fs.getDir(getProject());
                addUnlessPresent(result, set, dir, s);
            } else if (o instanceof FileList) {
                FileList fl = (FileList) o;
                String[] s = fl.getFiles(getProject());
                File dir = fl.getDir(getProject());
                addUnlessPresent(result, set, dir, s);
            }
        }
        return (String[]) result.toArray(new String[result.size()]);
    }

    /**
     * Adds a element definition to the path.
     * @param location the location of the element to add (must not be
     * <code>null</code> nor empty.
     */
    public void setLocation(String location) {
        checkAttributesAllowed();
        createUrlpathelement().setLocation(location);
    }

    /**
     * Parses a path definition and creates single PathElements.
     * @param path the path definition.
     */
    public void setPath(String path) {
        checkAttributesAllowed();
        createPathElement().setPath(path);
    }
    /**
     * Makes this instance in effect a reference to another Path instance.
     *
     * <p>You must not set another attribute or nest elements inside
     * this element if you make it a reference.</p>
     * @param r the reference
     */
    public void setRefid(Reference r) {
        if (!elements.isEmpty()) {
            throw tooManyAttributes();
        }
        elements.add(r);
        super.setRefid(r);
    }

    /**
     * Parses a path definition and creates single PathElements.
     * @param path the path definition.
     */
    public void setUrlpath(String path) {
        checkAttributesAllowed();
        createUrlpathelement().setPath(path);
    }

    /**
     * How many parts does this Path instance consist of.
     * @return number of parts
     */
    public int size() {
        return list().length;
    }
    /**
     * gets a <code>Path</code> that represents the same entries
     * as this urlpath.
     * @return the equivalent path
     * @throws BuildException if an element can not converted into a file.
     */
    public Path toPath() throws BuildException {
        return addToPath(null);
    }

    /**
     * Returns a textual representation of the path, which can be used as
     * CLASSPATH or PATH environment variable definition.
     * @return a textual representation of the path.
     */
    public String toString() {
        final String[] list = list();

        // empty path return empty string
        if (list.length == 0) {
            return "";
        }

        // path containing one or more elements
        final StringBuffer result = new StringBuffer(list[0].toString());
        for (int i = 1; i < list.length; i++) {
            result.append(';');
            result.append(list[i]);
        }

        return result.toString();
    }

    /**
     * Returns its argument with all file separator characters
     * replaced with '/'.
     * @param source the argument
     * @return the normalized file
     */
    public String translateFile(String source) {
        if (source == null) {
            return null;
        }
        try {
            if (!URLUtils.isURL(source)) {
                source = getProject().resolveFile(source).getAbsolutePath();
            }
            return URLUtils.resolve(source, getProject().getBaseDir());
        } catch (MalformedURLException murlex) {
            return source;
        }
    }
    /**
     * Splits a URLPATH (with ; as separators) into its parts.
     * @param source the urlpath
     * @return array of pathelements
     */
    /*
    private String[] translatePath(String source) {
        ArrayList result = new ArrayList();
        if (source == null) {
            return new String[0];
        }
        for (StringTokenizer st = new StringTokenizer(source, ";:");
            st.hasMoreTokens();) {
            String pathElement = st.nextToken().trim();
            try {
                String s = translateFile(pathElement);
                if (s != null) {
                    result.add(s);
                }
            } catch (BuildException e) {
                log(
                    "Dropping path element "
                        + pathElement
                        + " as it is not valid relative to the project",
                    Project.MSG_VERBOSE);
            }
        }
        return (String[]) result.toArray(new String[result.size()]);
    }
    */
    private String[] translateUrlPath(String source) {
        ArrayList result = new ArrayList();
        if (source == null) {
            return new String[0];
        }
        for (StringTokenizer st = new StringTokenizer(source, ";");
            st.hasMoreTokens();) {
            String pathElement = st.nextToken().trim();
            if (pathElement.length() > 0) {
                pathElement = translateFile(pathElement);
                if (pathElement != null) {
                    result.add(pathElement);
                }
            }
        }
        return (String[]) result.toArray(new String[result.size()]);
    }
}
