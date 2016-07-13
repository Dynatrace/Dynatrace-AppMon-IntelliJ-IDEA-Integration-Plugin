package com.dynatrace.integration.idea.execution.result.ui;

import com.dynatrace.diagnostics.automation.rest.sdk.entity.TestMeasure;
import com.intellij.ui.treeStructure.SimpleNode;

public class TestMeasureNode extends SimpleNode {
    private final TestMeasure measure;

    public TestMeasureNode(TestMeasure measure) {
        this.measure = measure;
    }

    @Override
    public SimpleNode[] getChildren() {
        return new SimpleNode[0];
    }

    public TestMeasure getMeasure() {
        return this.measure;
    }

    @Override
    public String getName() {
        return this.measure.getName();
    }
}
