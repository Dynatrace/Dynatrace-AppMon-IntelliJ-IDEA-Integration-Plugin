package com.dynatrace.integration.idea.execution.result.ui;

import com.dynatrace.diagnostics.automation.rest.sdk.entity.TestRun;
import com.dynatrace.diagnostics.automation.rest.sdk.entity.TestStatus;
import com.dynatrace.integration.idea.Icons;
import com.intellij.ui.treeStructure.SimpleNode;

import java.util.ArrayList;
import java.util.List;

public class TestRunNode extends SimpleNode implements StatusProvider {
    private final TestRun run;

    public TestRunNode(TestRun run) {
        this.run = run;
    }

    @Override
    protected void doUpdate() {
        this.setIcon(Icons.fromStatus(this.run.getStatus()));
    }

    @Override
    public SimpleNode[] getChildren() {
        List<TestResultNode> results = new ArrayList<>();
        this.run.getTestResults().forEach((result) -> results.add(new TestResultNode(result)));
        return results.toArray(new TestResultNode[results.size()]);
    }

    public String getName() {
        return this.run.getId();
    }

    @Override
    public TestStatus getStatus() {
        return this.run.getStatus();
    }
}
