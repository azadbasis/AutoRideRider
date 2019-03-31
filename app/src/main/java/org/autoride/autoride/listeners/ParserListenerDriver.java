package org.autoride.autoride.listeners;

public interface ParserListenerDriver {

    void onLoadCompleted(String driverInfo);

    void onLoadFailed(String driverInfo);
}