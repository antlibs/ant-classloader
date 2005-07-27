package org.apache.tools.ant.taskdefs.classloader;

/**
 * Exception for searching for an adapter.
 */
public class ClassloaderAdapterException extends Exception {
    public static final int NO_ADAPTER = 2;
    public static final int NO_HANDLER = 1;
    public static final int NO_HANDLERSET = 0;
    private static final long serialVersionUID = 1L;
    final int reason;
    public ClassloaderAdapterException(int reason) {
        this.reason = reason;
    }
    public int getReason() {
        return reason;
    }
}