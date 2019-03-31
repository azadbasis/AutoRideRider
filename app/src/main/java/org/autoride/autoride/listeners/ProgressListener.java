package org.autoride.autoride.listeners;

public interface ProgressListener {
    void update(long bytesRead, long contentLength, boolean done);
}