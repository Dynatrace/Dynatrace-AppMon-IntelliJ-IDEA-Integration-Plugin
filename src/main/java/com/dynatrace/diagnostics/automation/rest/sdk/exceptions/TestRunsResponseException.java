package com.dynatrace.diagnostics.automation.rest.sdk.exceptions;

public class TestRunsResponseException extends Exception {
    public TestRunsResponseException(String message) {
        super(message);
    }

    public TestRunsResponseException(String message, Throwable cause) {
        super(message, cause);
    }

    public TestRunsResponseException(Throwable cause) {
        super(cause);
    }
}
