package com.dynatrace.diagnostics.codelink.exceptions;

public class CodeLinkResponseException extends Exception {
    public CodeLinkResponseException(String message) {
        super(message);
    }

    public CodeLinkResponseException(String message, Throwable cause) {
        super(message, cause);
    }

    public CodeLinkResponseException(Throwable cause) {
        super(cause);
    }
}
