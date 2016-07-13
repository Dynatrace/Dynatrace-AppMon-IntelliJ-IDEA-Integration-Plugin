package com.dynatrace.diagnostics.automation.rest.sdk.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

/**
 * Created by krzysztof.necel on 2016-02-04.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class TestResult {

    private static final DateFormat FORMATTER = SimpleDateFormat.getDateTimeInstance();

    @XmlAttribute
    private Date timestamp;
    @XmlAttribute
    private String name;
    @XmlAttribute(name = "package")
    private String packageName;
    @XmlAttribute
    private String platform;
    @XmlAttribute(name = "status")
    private TestStatus status;

    @XmlElement(name = "measure")
    private Set<TestMeasure> measures;

    public String getTestName() {
        return name;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getPlatform() {
        return platform;
    }

    public TestStatus getStatus() {
        return status;
    }

    public Set<TestMeasure> getTestMeasures() {
        return measures;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getFormattedTimestamp() {
        return FORMATTER.format(timestamp);
    }

    public boolean getFailed() {
        return status == TestStatus.FAILED;
    }

    public TestMeasure getMeasureByName(String metricGroup, String measureName) {
        for (TestMeasure testMeasure : measures) {
            if (Objects.equals(testMeasure.getMetricGroup(), metricGroup)
                    && Objects.equals(testMeasure.getName(), measureName)) {
                return testMeasure;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "TestResult{" +
                "packageName='" + packageName + '\'' +
                ", testName='" + name + '\'' +
                ", platform='" + platform + '\'' +
                ", timestamp=" + timestamp +
                ", status=" + status +
                ", testMeasures=" + measures +
                '}';
    }
}
