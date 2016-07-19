/*
 *  Dynatrace IntelliJ IDEA Integration Plugin
 *  Copyright (c) 2008-2016, DYNATRACE LLC
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification,
 *  are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  * Neither the name of the dynaTrace software nor the names of its contributors
 *  may be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 *  SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 *  TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 *  BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 *  ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 *  DAMAGE.
 *
 */

package com.dynatrace.integration.idea.execution;

import com.dynatrace.integration.idea.Icons;
import com.dynatrace.integration.idea.Messages;
import com.intellij.execution.Executor;
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
        return Icons.DYNATRACE_RUN;
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
        return Messages.getMessage("execution.executor.actiontext");
    }

    @Override
    public String getContextActionId() {
        return "RunWithAppMon";
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
