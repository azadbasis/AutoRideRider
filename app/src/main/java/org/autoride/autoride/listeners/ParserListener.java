package org.autoride.autoride.listeners;

import org.autoride.autoride.model.RiderInfo;

public interface ParserListener {

    void onLoadCompleted(RiderInfo riderInfo);

    void onLoadFailed(RiderInfo riderInfo);
}