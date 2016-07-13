package com.dynatrace.diagnostics.automation.rest.sdk.entity;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import javax.xml.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by krzysztof.necel on 2016-04-04.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "testRun")
public class TestRun {

    @XmlElement(name = "testResult")
    private List<TestResult> testResults;

    @XmlAttribute
    private String id;
    @XmlAttribute
    private TestCategory category;

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
                ", category=" + category + '}';
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

    public Iterable<TestResult> getTestResults() {
        return this.testResults;
    }

    public boolean hasTestStatus(TestStatus status) {
        return this.testResults.stream().anyMatch((e)->e.getStatus()==TestStatus.FAILED);
    }

    public TestStatus getStatus() {
        if(this.hasTestStatus(TestStatus.FAILED)) {
            return TestStatus.FAILED;
        }
        if(this.hasTestStatus(TestStatus.DEGRADED)) {
            return TestStatus.DEGRADED;
        }
        if(this.hasTestStatus(TestStatus.VOLATILE)) {
            return TestStatus.VOLATILE;
        }
        if(this.hasTestStatus(TestStatus.IMPROVED)) {
            return TestStatus.IMPROVED;
        }
        return TestStatus.PASSED;
    }
}
