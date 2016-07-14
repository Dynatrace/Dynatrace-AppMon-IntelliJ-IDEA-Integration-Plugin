package com.dynatrace.diagnostics.codelink;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class CodeLinkClient {
    public static final Logger LOGGER = Logger.getLogger("#" + CodeLinkClient.class.getName());
    public static final int DEFAULT_INTERVAL = 2;
    public static final TimeUnit DEFAULT_UNIT = TimeUnit.SECONDS;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final PollingWorker worker;
    private ScheduledFuture future;

    public CodeLinkClient(ICodeLinkSettings clSettings, IIDEDescriptor ideSettings) {
        this.worker = new PollingWorker(ideSettings, clSettings);
    }

    public synchronized boolean startPolling(int interval, TimeUnit unit) {
        if (this.future != null) {
            return false;
        }
        this.future = this.scheduler.scheduleWithFixedDelay(this.worker, 2, interval, unit);
        return true;
    }

    public synchronized void stopPolling() {
        if (this.future != null) {
            this.future.cancel(false);
            this.future = null;
        }
    }
}
