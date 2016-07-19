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
        for(AbstractTestProxy children : root.getChildren()) {
            if(children.getChildren().isEmpty()) {
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
        String profileName = testProxy.getHandler().getCopyableUserData(DynatraceRunConfigurationExtension.PROFILE_KEY);
        String trId = testProxy.getHandler().getCopyableUserData(DynatraceRunConfigurationExtension.TRID_KEY);

        if (profileName == null || trId == null) {
            return;
        }
        TestRunResultsCoordinator.getInstance(project).requestTestRunResults(profileName, trId, getPureTestCount(root));
    }
}
