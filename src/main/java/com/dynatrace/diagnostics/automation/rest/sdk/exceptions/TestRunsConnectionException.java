package com.dynatrace.diagnostics.automation.rest.sdk.exceptions;

public class TestRunsConnectionException extends Exception {
    public TestRunsConnectionException(String message) {
        super(message);
    }

    public TestRunsConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public TestRunsConnectionException(Throwable cause) {
        super(cause);
    }
}
