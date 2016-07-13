package com.dynatrace.diagnostics.automation.rest.sdk.entity;

/**
 * Created by krzysztof.necel on 2016-02-04.
 */
public enum TestStatus {
    FAILED,
    DEGRADED,
    VOLATILE,
    IMPROVED,
    PASSED,
    INVALIDATED;

    public static TestStatus fromString(String status) {
        return TestStatus.valueOf(status.toUpperCase());
    }
}