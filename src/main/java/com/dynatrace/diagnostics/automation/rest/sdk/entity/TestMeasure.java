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

package com.dynatrace.diagnostics.automation.rest.sdk.entity;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import java.text.DecimalFormat;

/**
 * Created by krzysztof.necel on 2016-02-04.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class TestMeasure implements Comparable<TestMeasure> {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
    private static final String FORMAT_DOUBLE_NULL_VALUE = "N/A";
    @XmlAttribute
    private String name;
    @XmlAttribute
    private String metricGroup;
    @XmlAttribute
    private Double expectedMin;
    @XmlAttribute
    private Double expectedMax;
    @XmlAttribute
    private Double value;
    @XmlAttribute
    private String unit;
    @XmlAttribute
    private Double violationPercentage;

    public static String formatDouble(Double d) {
        return d == null ? FORMAT_DOUBLE_NULL_VALUE : DECIMAL_FORMAT.format(d);
    }

    public static String formatDoublePercentage(Double d) {
        return d == null ? FORMAT_DOUBLE_NULL_VALUE : DECIMAL_FORMAT.format(d * 100);
    }

    public String getName() {
        return name;
    }

    public String getMetricGroup() {
        return metricGroup;
    }

    public String getExpectedMin() {
        return formatDouble(expectedMin);
    }

    public String getExpectedMax() {
        return formatDouble(expectedMax);
    }

    public String getValue() {
        return formatDouble(value);
    }

    public String getUnit() {
        return unit;
    }

    public String getViolationPercentage() {
        return formatDoublePercentage(violationPercentage);
    }

    @Override
    public String toString() {
        return "TestMeasure{" +
                "name='" + name + '\'' +
                ", metricGroup='" + metricGroup + '\'' +
                ", expectedMin='" + expectedMin + '\'' +
                ", expectedMax='" + expectedMax + '\'' +
                ", value='" + value + '\'' +
                ", unit='" + unit + '\'' +
                ", violationPercentage='" + violationPercentage + '\'' +
                '}';
    }

    @Override
    public int compareTo(@NotNull TestMeasure o) {
        return new CompareToBuilder()
                .append(metricGroup, o.metricGroup)
                .append(name, o.name)
                .toComparison();
    }
}
