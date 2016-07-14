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

    public static String formatDouble(Double d) {
        return d == null ? FORMAT_DOUBLE_NULL_VALUE : DECIMAL_FORMAT.format(d);
    }

    public static String formatDoublePercentage(Double d) {
        return d == null ? FORMAT_DOUBLE_NULL_VALUE : DECIMAL_FORMAT.format(d * 100);
    }

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
