package com.dynatrace.integration.idea.execution.result.ui;

import com.dynatrace.diagnostics.automation.rest.sdk.entity.TestResult;
import com.dynatrace.diagnostics.automation.rest.sdk.entity.TestStatus;
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
        this.result.getTestMeasures().forEach((measure) -> {
            measures.add(new TestMeasureNode(measure));
        });
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
}
