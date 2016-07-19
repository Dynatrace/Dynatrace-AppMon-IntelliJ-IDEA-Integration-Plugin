/*
 *  Dynatrace IntelliJ IDEA Integration Plugin
 *  Copyright (c) 2008-2016, DYNATRACE LLC
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification,
 *  are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  * Neither the name of the dynaTrace software nor the names of its contributors
 *  may be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 *  SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 *  TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 *  BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 *  ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 *  DAMAGE.
 *
 */

package com.dynatrace.integration.idea.execution.result.ui;

import com.dynatrace.diagnostics.automation.rest.sdk.entity.TestResult;
import com.dynatrace.diagnostics.automation.rest.sdk.entity.TestStatus;
import com.dynatrace.integration.idea.Icons;
import com.intellij.ui.treeStructure.SimpleNode;

import java.util.ArrayList;
import java.util.List;

public class TestResultNode extends SimpleNode implements StatusProvider {

    private final TestResult result;

    public TestResultNode(TestResult status) {
        this.result = status;
    }

    @Override
    protected void doUpdate() {
        this.setIcon(Icons.fromStatus(this.getStatus()));
    }

    @Override
    public TestMeasureNode[] getChildren() {
        List<TestMeasureNode> measures = new ArrayList<>();
        this.result.getTestMeasures().forEach((measure) -> measures.add(new TestMeasureNode(measure)));
        return measures.toArray(new TestMeasureNode[measures.size()]);
    }

    @Override
    public String getName() {
        return this.result.getTestName();
    }

    @Override
    public TestStatus getStatus() {
        return this.result.getStatus();
    }

    public TestResult getResult() {
        return this.result;
    }
}
