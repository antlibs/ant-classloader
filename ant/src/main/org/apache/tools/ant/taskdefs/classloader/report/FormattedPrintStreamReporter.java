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
package org.apache.tools.ant.taskdefs.classloader.report;

import java.io.PrintStream;

/**
 * Reports into a PrintStream or System.out.
 */
public class FormattedPrintStreamReporter extends AbstractFormattedReporter {
    private final PrintStream stream;
    /**
     * public constructor.
     * @param fmt The formatter to use.
     * @param stream The stream to report to. If null, System.out is used.
     */
    public FormattedPrintStreamReporter(ClassloaderReportFormatter fmt,
            PrintStream stream) {
        super(fmt);
        this.stream = (stream == null) ? System.out : stream;
    }
    /**
     * writes a message line to the reporting dest.
     *
     * @param s
     *            the message line to report.
     */
    protected void report(String s) {
        if (s != null) {
            stream.println(s);
        }
    }
}