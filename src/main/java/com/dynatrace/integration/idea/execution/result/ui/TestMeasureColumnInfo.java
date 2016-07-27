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

import com.dynatrace.server.sdk.testautomation.models.TestMeasure;
import com.intellij.util.ui.ColumnInfo;

import javax.swing.tree.DefaultMutableTreeNode;

class TestMeasureColumnInfo extends ColumnInfo {
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

    enum MeasureProperty {
        //NAME("Name", TestMeasure::getName, 3),
        GROUP("Group", TestMeasure::getMetricGroup, 180),
        MIN("Min", TestMeasure::getExpectedMin, 40),
        MAX("Max", TestMeasure::getExpectedMax, 40),
        VALUE("Value", TestMeasure::getValue, 40),
        UNIT("Unit", TestMeasure::getUnit, 50),
        VIOLATION("Violation %", TestMeasure::getViolationPercentage, 80);

        final String name;
        final int width;
        private final ValueSupplier supplier;

        MeasureProperty(String name, ValueSupplier supplier, int width) {
            this.name = name;
            this.supplier = supplier;
            this.width = width;
        }

        Object getValue(TestMeasure measure) {
            return this.supplier.getValue(measure);
        }

        private interface ValueSupplier {
            Object getValue(TestMeasure measure);
        }
    }
}
