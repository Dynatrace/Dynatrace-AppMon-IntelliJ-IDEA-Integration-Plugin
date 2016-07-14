package com.dynatrace.diagnostics.codelink.exceptions;

public class CodeLinkConnectionException extends Exception {
    public CodeLinkConnectionException(String message) {
        super(message);
    }

    public CodeLinkConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public CodeLinkConnectionException(Throwable cause) {
        super(cause);
    }
}
