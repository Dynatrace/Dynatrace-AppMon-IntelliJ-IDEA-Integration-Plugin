package com.dynatrace.diagnostics.codelink;

public interface Callback<T> {
    void call(T param);
}
