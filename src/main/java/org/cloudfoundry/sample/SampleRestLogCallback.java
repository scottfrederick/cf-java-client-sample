package org.cloudfoundry.sample;

import org.cloudfoundry.client.lib.RestLogCallback;
import org.cloudfoundry.client.lib.RestLogEntry;

public class SampleRestLogCallback implements RestLogCallback {
    @Override
    public void onNewLogEntry(RestLogEntry logEntry) {
        System.out.println(String.format("REQUEST: %s %s", logEntry.getMethod(), logEntry.getUri()));
        System.out.println(String.format("RESPONSE: %s %s %s",
                logEntry.getHttpStatus().toString(), logEntry.getStatus(), logEntry.getMessage()));
    }
}