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

import javax.xml.bind.annotation.*;
import java.util.List;
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

    public List<TestResult> getTestResults() {
        return this.testResults;
    }

    public boolean hasTestStatus(TestStatus status) {
        return this.testResults.stream().anyMatch((e) -> e.getStatus() == status);
    }

    public TestStatus getStatus() {
        if (this.hasTestStatus(TestStatus.FAILED)) {
            return TestStatus.FAILED;
        }
        if (this.hasTestStatus(TestStatus.DEGRADED)) {
            return TestStatus.DEGRADED;
        }
        if (this.hasTestStatus(TestStatus.VOLATILE)) {
            return TestStatus.VOLATILE;
        }
        if (this.hasTestStatus(TestStatus.IMPROVED)) {
            return TestStatus.IMPROVED;
        }
        return TestStatus.PASSED;
    }
}
