package com.dynatrace.integration.idea.execution.result.ui;

import com.dynatrace.diagnostics.automation.rest.sdk.entity.TestMeasure;
import com.intellij.util.ui.ColumnInfo;

import javax.swing.tree.DefaultMutableTreeNode;

class TestMeasureColumnInfo extends ColumnInfo {
    enum MeasureProperty {
        //NAME("Name", TestMeasure::getName, 3),
        GROUP("Group", TestMeasure::getMetricGroup, 180),
        MIN("Min", TestMeasure::getExpectedMin, 40),
        MAX("Max", TestMeasure::getExpectedMax, 40),
        VALUE("Value", TestMeasure::getValue, 40),
        UNIT("Unit", TestMeasure::getUnit, 50),
        VIOLATION("Violation %", TestMeasure::getViolationPercentage, 80);

        private interface ValueSupplier {
            Object getValue(TestMeasure measure);
        }

        final String name;
        private final ValueSupplier supplier;
        final int width;

        MeasureProperty(String name, ValueSupplier supplier, int width) {
            this.name = name;
            this.supplier = supplier;
            this.width = width;
        }

        Object getValue(TestMeasure measure) {
            return this.supplier.getValue(measure);
        }
    }

    private final MeasureProperty field;

    public TestMeasureColumnInfo(MeasureProperty field) {
        super(field.name);
        this.field = field;
    }

    @Override
    public Object valueOf(Object object) {
        if (object instanceof DefaultMutableTreeNode) {
            Object obj = ((DefaultMutableTreeNode) object).getUserObject();
            if (obj instanceof TestMeasureNode) {
                TestMeasure measure = ((TestMeasureNode) obj).getMeasure();
                return this.field.getValue(measure);
            }
        }
        return null;
    }
}
