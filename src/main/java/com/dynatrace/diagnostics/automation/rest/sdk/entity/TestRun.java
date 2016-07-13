package com.dynatrace.diagnostics.automation.rest.sdk.entity;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;

/**
 * Created by krzysztof.necel on 2016-04-04.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "testRun")
public class TestRun {

    @XmlElement(name = "testResult")
    private final List<TestResult> testResults;

    private final Map<TestStatus, Integer> summary;
    private final String id;
    private final TestCategory category;

    public TestRun(List<TestResult> testResults, Map<TestStatus, Integer> summary, String id, TestCategory category) {
        this.testResults = testResults;
        this.summary = summary;
        this.id = id;
        this.category = category;
    }

    // Required by JAXB
    private TestRun() {
        this.testResults = new ArrayList<TestResult>();
        this.summary = new EnumMap<TestStatus, Integer>(TestStatus.class);
        this.id = null;
        this.category = null;
    }

    public Map<TestStatus, Integer> getSummary() {
        return summary;
    }

    public String getId() {
        return id;
    }

    public TestCategory getCategory() {
        return category;
    }

    public boolean isEmpty() {
        return testResults == null || testResults.isEmpty();
    }

    @Override
    public String toString() {
        return "TestRun{" +
                "id='" + id + '\'' +
                ", category=" + category +
                ", summary=" + summary +
                '}';
    }

    public TestResult getCorrespondingTestResult(TestResult testResult) {
        for (TestResult result : testResults) {
            if (Objects.equals(result.getTestName(), testResult.getTestName())
                    && Objects.equals(result.getPackageName(), testResult.getPackageName())
                    && Objects.equals(result.getPlatform(), testResult.getPlatform())) {
                return result;
            }
        }
        return null;
    }

    public int getFailedCount() {
        return summary.get(TestStatus.FAILED);
    }

    public int getDegradedCount() {
        return summary.get(TestStatus.DEGRADED);
    }

    public int getVolatileCount() {
        return summary.get(TestStatus.VOLATILE);
    }

    public int getImprovedCount() {
        return summary.get(TestStatus.IMPROVED);
    }

    public int getPassedCount() {
        return summary.get(TestStatus.PASSED);
    }

    public Iterable<TestResult> getFailedTestResults() {
        return getTestResults(TestStatus.FAILED);
    }

    public Iterable<TestResult> getDegradedTestResults() {
        return getTestResults(TestStatus.DEGRADED);
    }

    public Iterable<TestResult> getVolatileTestResults() {
        return getTestResults(TestStatus.VOLATILE);
    }

    public Iterable<TestResult> getImprovedTestResults() {
        return getTestResults(TestStatus.IMPROVED);
    }

    public Iterable<TestResult> getPassedTestResults() {
        return getTestResults(TestStatus.PASSED);
    }

    private Iterable<TestResult> getTestResults(final TestStatus status) {
        return Iterables.filter(testResults, new Predicate<TestResult>() {
            @Override
            public boolean apply(TestResult testResult) {
                return testResult.getStatus() == status;
            }
        });
    }
}
