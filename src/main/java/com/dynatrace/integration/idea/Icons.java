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
