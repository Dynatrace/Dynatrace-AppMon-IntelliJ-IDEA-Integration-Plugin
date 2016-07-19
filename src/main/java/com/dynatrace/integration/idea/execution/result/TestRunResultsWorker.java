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

package com.dynatrace.integration.idea.execution.result;

import com.dynatrace.diagnostics.automation.rest.sdk.TestRunsEndpoint;
import com.dynatrace.diagnostics.automation.rest.sdk.entity.TestRun;
import com.dynatrace.diagnostics.automation.rest.sdk.exceptions.TestRunsConnectionException;
import com.dynatrace.diagnostics.automation.rest.sdk.exceptions.TestRunsResponseException;
import com.dynatrace.integration.idea.Messages;
import com.dynatrace.integration.idea.execution.result.ui.TestRunResultsView;
import com.dynatrace.integration.idea.plugin.codelink.IDEDescriptor;
import com.dynatrace.integration.idea.plugin.settings.DynatraceSettingsProvider;

import java.util.logging.Level;
import java.util.logging.Logger;

// Fetches results from DynatraceServer and displays them in UI
public class TestRunResultsWorker implements Runnable {
    public static final Logger LOG = Logger.getLogger(TestRunResultsWorker.class.getName());
    private static final long DELAY = 2000L; // 2 seconds delay

    private final String profileName;
    private final String testRunId;
    private final long startTime;
    private final DynatraceSettingsProvider.State settings;
    private final TestRunResultsView view;
    private final int testCount;

    public TestRunResultsWorker(TestRunResultsView view, String profileName, String testRunId, DynatraceSettingsProvider.State settings, int testCount) {
        this.view = view;
        this.profileName = profileName;
        this.testRunId = testRunId;
        this.settings = settings;
        this.testCount = testCount;
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public void run() {
        int lastFetched = 0;
        while (System.currentTimeMillis() - this.startTime < this.settings.getServer().getTimeout() * 1000L) {
            TestRunsEndpoint endpoint = new TestRunsEndpoint(this.settings.getServer());
            try {
                TestRun testRun = endpoint.getTestRun(this.profileName, this.testRunId);
                if (!testRun.isEmpty() && testRun.getTestResults().size() > lastFetched) {
                    lastFetched = testRun.getTestResults().size();
                    this.view.setTestRun(testRun);
                    if (testRun.getTestResults().size() >= this.testCount) {
                        IDEDescriptor.getInstance().log(Level.INFO, "TestRuns", "", Messages.getMessage("execution.result.worker.success", this.testRunId), false);
                        LOG.log(Level.INFO, Messages.getMessage("execution.result.worker.success", this.testRunId));
                        return;
                    } else {
                        IDEDescriptor.getInstance().log(Level.INFO, "TestRuns", "", Messages.getMessage("execution.result.worker.partial", testRun.getTestResults().size(), this.testCount, this.testRunId), false);
                        LOG.log(Level.INFO, Messages.getMessage("execution.result.worker.partial", testRun.getTestResults().size(), this.testCount, this.testRunId));
                    }
                }
                Thread.sleep(DELAY);
            } catch (TestRunsResponseException | TestRunsConnectionException e) {
                IDEDescriptor.getInstance().log(Level.SEVERE, "TestRuns", "", Messages.getMessage("execution.result.worker.error", e.getLocalizedMessage()), false);
                LOG.log(Level.WARNING, Messages.getMessage("execution.result.worker.error", e.getLocalizedMessage()));
                break;
            } catch (InterruptedException e) {
                LOG.log(Level.WARNING, Messages.getMessage("execution.result.worker.interrupted"));
                break;
            }
        }
        IDEDescriptor.getInstance().log(Level.WARNING, "TestRuns", "", Messages.getMessage("execution.result.worker.timeout", lastFetched, this.testCount, this.testRunId), false);
        this.view.setEmptyText(Messages.getMessage("execution.result.ui.errorloading"));
    }
}
