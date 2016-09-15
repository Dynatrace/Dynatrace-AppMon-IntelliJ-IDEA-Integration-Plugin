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

package com.dynatrace.integration.idea.execution;

import com.dynatrace.integration.idea.execution.configuration.DynatraceRunConfigurationExtension;
import com.dynatrace.integration.idea.execution.result.TestRunResultsCoordinator;
import com.intellij.execution.testframework.AbstractTestProxy;
import com.intellij.execution.testframework.TestStatusListener;
import com.intellij.execution.testframework.sm.runner.SMTestProxy;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

public class DynatraceTestStatusListener extends TestStatusListener {

    private static int getPureTestCount(AbstractTestProxy root) {
        int testCount = 0;
        for (AbstractTestProxy children : root.getChildren()) {
            if (children.getChildren().isEmpty()) {
                testCount++;
            } else {
                testCount += getPureTestCount(children);
            }
        }
        return testCount;
    }

    @Override
    public void testSuiteFinished(@Nullable AbstractTestProxy root) {
    }

    @Override
    public void testSuiteFinished(@Nullable AbstractTestProxy root, Project project) {
        super.testSuiteFinished(root, project);
        if (!(root instanceof SMTestProxy.SMRootTestProxy)) {
            return;
        }
        SMTestProxy.SMRootTestProxy testProxy = (SMTestProxy.SMRootTestProxy) root;
        if (testProxy.getHandler() == null) {
            return;
        }
        String profileName = testProxy.getHandler().getCopyableUserData(DynatraceRunConfigurationExtension.PROFILE_KEY);
        String trId = testProxy.getHandler().getCopyableUserData(DynatraceRunConfigurationExtension.TRID_KEY);

        if (profileName == null || trId == null) {
            return;
        }

        TestRunResultsCoordinator.getInstance(project).requestTestRunResults(profileName, trId, getPureTestCount(root));
    }
}
