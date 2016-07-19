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
