/*
 * Copyright 2004-2005 The Apache Software Foundation
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

import org.apache.tools.ant.BuildFileTest;

/**
 * Tests ClassloaderBase task.
 */
public class ClassloaderReportTest extends BuildFileTest {

    public ClassloaderReportTest(String name) {
        super(name);
    }

    public void setUp() {
        configureProject("src/etc/testcases/taskdefs/classloaderreport.xml");
    }

    public void testReport() {
        expectLogContaining("test.report","<classloaderreport>");
        expectLogContaining("test.report","</classloaderreport>");
    }

    public void tearDown() {
        executeTarget("cleanup");
    }

}
