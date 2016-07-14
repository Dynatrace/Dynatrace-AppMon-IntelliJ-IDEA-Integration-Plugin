package com.dynatrace.integration.idea.execution;

import com.dynatrace.integration.idea.Messages;
import com.intellij.execution.Executor;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.wm.ToolWindowId;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class DynatraceExecutor extends Executor {
    public static final String ID = "dynatrace.executor";

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
        return Messages.getMessage("execution.executor.description");
    }

    @NotNull
    @Override
    public String getActionName() {
        return Messages.getMessage("execution.executor.action");
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
        return "RunWithDynatrace";
    }

    @Override
    public String getHelpId() {
        return null; //TODO
    }

    @Override
    public String getStartActionText(String configurationName) {
        return Messages.getMessage("execution.executor.run", (StringUtil.isEmpty(configurationName) ? "" : " \'" + StringUtil.first(configurationName, 30, true) + "\'"));
    }
}
