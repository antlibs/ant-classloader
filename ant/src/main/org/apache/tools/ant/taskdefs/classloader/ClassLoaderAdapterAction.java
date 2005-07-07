package org.apache.tools.ant.taskdefs.classloader;

/**
 * Actions for ClassLoaderAdapter.
 */
public final class ClassLoaderAdapterAction {
    private static final int IDCREATE = 1;
    private static final int IDAPPEND = 2;
    private static final int IDGETPATH = 3;
    private static final int IDREPORT = 4;
    /**
     * Append Path to an existing ClassLoader instance.
     */
    public static final ClassLoaderAdapterAction APPEND = new ClassLoaderAdapterAction(IDAPPEND);
    /**
     * Create a new ClassLoader instance.
     */
    public static final ClassLoaderAdapterAction CREATE = new ClassLoaderAdapterAction(IDCREATE);
    /**
     * Get the path of an existing ClassLoader instance.
     */
    public static final ClassLoaderAdapterAction GETPATH = new ClassLoaderAdapterAction(IDGETPATH);
    /**
     * Get additional Report information.
     */
    public static final ClassLoaderAdapterAction REPORT = new ClassLoaderAdapterAction(IDREPORT);

    private ClassLoaderAdapterAction(int value) {
    }
}