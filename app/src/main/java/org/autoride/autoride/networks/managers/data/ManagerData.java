package org.autoride.autoride.networks.managers.data;

import org.autoride.autoride.listeners.ParserListener;
import org.autoride.autoride.listeners.ParserListenerDriver;
import org.autoride.autoride.model.RiderInfo;
import org.autoride.autoride.networks.tasks.PerformRiderTask;
import org.autoride.autoride.networks.tasks.PerformRiderTaskWithHeader;
import org.autoride.autoride.networks.tasks.PerformRidersDriverTask;
import org.autoride.autoride.constants.AppsConstants;
import org.json.JSONObject;

public class ManagerData implements AppsConstants {

    public static void taskManager(String method, String apiUrl, JSONObject postBody, final ParserListener listener) {
        PerformRiderTask task = new PerformRiderTask(method, apiUrl, postBody);
        task.setTaskListener(new PerformRiderTask.PerformRiderTaskListener() {
            @Override
            public void dataDownloadedSuccessfully(RiderInfo riderInfo) {
                if (riderInfo == null) {
                    listener.onLoadFailed(riderInfo);
                } else {
                    if (riderInfo.getStatus().equalsIgnoreCase(WEB_RESPONSE_SUCCESS)) {
                        listener.onLoadCompleted(riderInfo);
                    } else if (riderInfo.getStatus().equalsIgnoreCase(WEB_RESPONSE_ERROR)) {
                        listener.onLoadFailed(riderInfo);
                    } else if (riderInfo.getStatus().equalsIgnoreCase(WEB_RESPONSE_ERRORS)) {
                        listener.onLoadFailed(riderInfo);
                    }
                }
            }

            @Override
            public void dataDownloadFailed(RiderInfo riderInfo) {
                listener.onLoadFailed(riderInfo);
            }
        });
        task.execute();
    }

    public static void taskManager(String method, String apiUrl, JSONObject postBody, JSONObject postHeader, final ParserListener listener) {
        PerformRiderTaskWithHeader task = new PerformRiderTaskWithHeader(method, apiUrl, postBody, postHeader);
        task.setTaskListener(new PerformRiderTaskWithHeader.PerformRiderTaskListener() {
            @Override
            public void dataDownloadedSuccessfully(RiderInfo riderInfo) {
                if (riderInfo == null) {
                    listener.onLoadFailed(riderInfo);
                } else {
                    if (riderInfo.getStatus().equalsIgnoreCase(WEB_RESPONSE_SUCCESS)) {
                        listener.onLoadCompleted(riderInfo);
                    } else if (riderInfo.getStatus().equalsIgnoreCase(WEB_RESPONSE_ERROR)) {
                        listener.onLoadFailed(riderInfo);
                    } else if (riderInfo.getStatus().equalsIgnoreCase(WEB_RESPONSE_ERRORS)) {
                        listener.onLoadFailed(riderInfo);
                    }
                }
            }

            @Override
            public void dataDownloadFailed(RiderInfo riderInfo) {
                listener.onLoadFailed(riderInfo);
            }
        });
        task.execute();
    }

    public static void driverTaskManager(String method, String apiUrl, JSONObject postBody, JSONObject postHeader, final ParserListenerDriver listener) {
        PerformRidersDriverTask task = new PerformRidersDriverTask(method, apiUrl, postBody, postHeader);
        task.setTaskListener(new PerformRidersDriverTask.PerformRidersRiderTaskListener() {
            @Override
            public void dataDownloadedSuccessfully(String driverInfo) {
                if (driverInfo == null) {
                    listener.onLoadFailed(driverInfo);
                } else {
                    listener.onLoadCompleted(driverInfo);
                }
            }

            @Override
            public void dataDownloadFailed(String driverInfo) {
                listener.onLoadFailed(driverInfo);
            }
        });
        task.execute();
    }
}
