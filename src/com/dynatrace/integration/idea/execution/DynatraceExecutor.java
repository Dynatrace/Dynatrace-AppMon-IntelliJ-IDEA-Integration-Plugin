package com.dynatrace.integration.idea.execution;

import com.intellij.execution.Executor;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.wm.ToolWindowId;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class DynatraceExecutor extends Executor {
    public static String ID = "dynatrace.executor";

    @Override
    public String getToolWindowId() {
        return ToolWindowId.RUN; //put in run menu
    }

    @Override
    public Icon getToolWindowIcon() {
        return null; //fallback to getIcon()
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return IconLoader.getIcon("/icons/dynatrace_run.png");
    }

    @Override
    public Icon getDisabledIcon() {
        return null; //fallback to getIcon() with a gray tint
    }

    @Override
    public String getDescription() {
        return "Run selected configuration with AppMon enabled.";
    }

    @NotNull
    @Override
    public String getActionName() {
        return "Dynatrace"; //TODO ?
    }

    @NotNull
    @Override
    public String getId() {
        return ID;
    }

    @NotNull
    @Override
    public String getStartActionText() {
        return "Run with Dynatrace";
    }

    @Override
    public String getContextActionId() {
        return "RunWithDynatrace"; //TODO ?
    }

    @Override
    public String getHelpId() {
        return null; //TODO
    }

    @Override
    public String getStartActionText(String configurationName) {
        return "Run " + (StringUtil.isEmpty(configurationName)?"":" \'" + StringUtil.first(configurationName, 30, true) + "\'") + " with Dynatrace";
    }
}
