package com.dynatrace.integration.idea;

import com.dynatrace.diagnostics.automation.rest.sdk.entity.TestStatus;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class Icons {
    public static final Icon DYNATRACE13 = IconLoader.getIcon("/icons/dynatrace_13.png");
    public static final Icon DYNATRACE_RUN = IconLoader.getIcon("/icons/dynatrace_run.png");
    public static final Icon CROSSED = IconLoader.getIcon("/icons/crossed_logo.png");
    public static final Icon SUCCESS = IconLoader.getIcon("/icons/success_ico.png");
    public static final Icon FAILING = IconLoader.getIcon("/icons/failing_ico.png");
    public static final Icon VOLATILE = IconLoader.getIcon("/icons/volatile_ico.png");
    public static final Icon IMPROVING = IconLoader.getIcon("/icons/improving_ico.png");
    public static final Icon DEGRADING = IconLoader.getIcon("/icons/degrading_ico.png");

    @NotNull
    public static Icon fromStatus(TestStatus status) {
        if (status == null) {
            return Icons.FAILING;
        }
        switch (status) {
            case PASSED:
                return Icons.SUCCESS;
            case FAILED:
                return Icons.FAILING;
            case VOLATILE:
                return Icons.VOLATILE;
            case DEGRADED:
                return Icons.DEGRADING;
            case IMPROVED:
                return Icons.IMPROVING;
        }
        return Icons.SUCCESS;
    }
}
