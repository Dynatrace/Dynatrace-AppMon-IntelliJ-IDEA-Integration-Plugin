package com.dynatrace.diagnostics.automation.rest.sdk.entity;

import org.apache.commons.lang.builder.CompareToBuilder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.text.DecimalFormat;

/**
 * Created by krzysztof.necel on 2016-02-04.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class TestMeasure implements Comparable<TestMeasure> {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
    private static final String FORMAT_DOUBLE_NULL_VALUE = "N/A";

    public static String formatDouble(Double d) {
        return d == null ? FORMAT_DOUBLE_NULL_VALUE : DECIMAL_FORMAT.format(d);
    }

    public static String formatDoublePercentage(Double d) {
        return d == null ? FORMAT_DOUBLE_NULL_VALUE : DECIMAL_FORMAT.format(d * 100);
    }

    private final String name;
    private final String metricGroup;
    private final Double expectedMin;
    private final Double expectedMax;
    private final Double value;
    private final String unit;
    private final Double violationPercentage;

    public TestMeasure(String name, String metricGroup, Double expectedMin, Double expectedMax,
                       Double value, String unit, Double violationPercentage) {
        this.name = name;
        this.metricGroup = metricGroup;
        this.expectedMin = expectedMin;
        this.expectedMax = expectedMax;
        this.value = value;
        this.unit = unit;
        this.violationPercentage = violationPercentage;
    }

    // Required by JAXB
    private TestMeasure() {
        this.name = null;
        this.metricGroup = null;
        this.expectedMin = null;
        this.expectedMax = null;
        this.value = null;
        this.unit = null;
        this.violationPercentage = null;
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
    public int compareTo(TestMeasure o) {
        return new CompareToBuilder()
                .append(metricGroup, o.metricGroup)
                .append(name, o.name)
                .toComparison();
    }
}
