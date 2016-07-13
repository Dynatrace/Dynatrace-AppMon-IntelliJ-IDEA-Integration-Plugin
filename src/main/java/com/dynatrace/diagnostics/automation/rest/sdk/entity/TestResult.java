package com.dynatrace.diagnostics.automation.rest.sdk.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by krzysztof.necel on 2016-02-04.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class TestResult {

    private static final DateFormat FORMATTER = SimpleDateFormat.getDateTimeInstance();

    private final Date timestamp;
    private final String testName;
    private final String packageName;
    private final String platform;
    private final TestStatus status;

    @XmlElement(name = "testMeasure")
    private final Set<TestMeasure> testMeasures;

    public TestResult(Date timestamp, String testName, String packageName, String platform, TestStatus status, Set<TestMeasure> testMeasures) {
        this.timestamp = timestamp;
        this.testName = testName;
        this.packageName = packageName;
        this.platform = platform;
        this.status = status;
        this.testMeasures = testMeasures;
    }

    // Required by JAXB
    private TestResult() {
        this.timestamp = null;
        this.testName = null;
        this.packageName = null;
        this.platform = null;
        this.status = null;
        this.testMeasures = new TreeSet<TestMeasure>();
    }

    public String getTestName() {
        return testName;
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
        return testMeasures;
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
        for (TestMeasure testMeasure : testMeasures) {
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
                ", testName='" + testName + '\'' +
                ", platform='" + platform + '\'' +
                ", timestamp=" + timestamp +
                ", status=" + status +
                ", testMeasures=" + testMeasures +
                '}';
    }
}
