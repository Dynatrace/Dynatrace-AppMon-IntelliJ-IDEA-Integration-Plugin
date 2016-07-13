package com.dynatrace.integration.idea.execution.result.ui;

import com.dynatrace.diagnostics.automation.rest.sdk.entity.TestStatus;

public interface StatusProvider {
    TestStatus getStatus();
}
