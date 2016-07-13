package com.dynatrace.integration.idea.execution.result.ui;

import com.dynatrace.diagnostics.automation.rest.sdk.entity.TestStatus;
import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public class Icons {
    public static final Icon SUCCESS = IconLoader.getIcon("/icons/success_ico.png");
    public static final Icon FAILING = IconLoader.getIcon("/icons/failing_ico.png");
    public static final Icon VOLATILE = IconLoader.getIcon("/icons/volatile_ico.png");

    public static Icon fromStatus(TestStatus status) {
        if(status==null) {
            return Icons.FAILING;
        }
        switch (status) {
            case PASSED:
                return Icons.SUCCESS;
            case FAILED:
                return Icons.FAILING;
            case VOLATILE:
                return Icons.VOLATILE;
        }
        return Icons.SUCCESS;
    }
}
