package org.autoride.autoride.networks.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.autoride.autoride.configs.AppsSingleton;
import org.autoride.autoride.constants.AppsConstants;
import org.autoride.autoride.model.RiderInfo;
import org.autoride.autoride.networks.managers.api.CallApi;
import org.autoride.autoride.networks.managers.api.RequestedBodyBuilder;
import org.autoride.autoride.networks.managers.api.RequestedHeaderBuilder;
import org.autoride.autoride.networks.managers.api.RequestedUrlBuilder;
import org.autoride.autoride.networks.parsers.RiderTaskParser;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class PerformRiderTaskWithHeader extends AsyncTask<String, Integer, RiderInfo> implements AppsConstants {

    private String TAG = "PerformRiderTask";
    private PerformRiderTaskListener taskListener;
    private OkHttpClient okHttpClient = AppsSingleton.getInstance().getOkHttpClient();
    private String methods;
    private String url;
    private JSONObject postBody;
    private JSONObject postHeader;

    public PerformRiderTaskWithHeader(String methods, String url, JSONObject postBody, JSONObject postHeader) {
        if (okHttpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(30, TimeUnit.SECONDS);
            builder.readTimeout(30, TimeUnit.SECONDS);
            builder.writeTimeout(30, TimeUnit.SECONDS);
            okHttpClient = builder.build();
            AppsSingleton.getInstance().setOkHttpClient(okHttpClient);
        }
        this.methods = methods;
        this.url = url;
        this.postBody = postBody;
        this.postHeader = postHeader;
    }

    @Override
    protected RiderInfo doInBackground(String... strings) {
        String response = null;
        try {
            if (methods.equalsIgnoreCase(POST)) {
                response = CallApi.POST(
                        okHttpClient,
                        RequestedUrlBuilder.buildRequestedPOSTUrl(url),
                        RequestedBodyBuilder.buildRequestedBody(postBody),
                        RequestedHeaderBuilder.buildRequestedHeader(postHeader)
                );
            } else if (methods.equalsIgnoreCase(GET)) {
                response = CallApi.GET(
                        okHttpClient,
                        RequestedUrlBuilder.buildRequestedGETUrl(url, postBody),
                        RequestedHeaderBuilder.buildRequestedHeader(postHeader)
                );
            }
            Log.i(TAG, HTTP_RESPONSE + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return RiderTaskParser.taskParse(response);
    }

    @Override
    protected void onPostExecute(RiderInfo result) {
        super.onPostExecute(result);
        Log.i(TAG, "result " + result);
        if (result != null) {
            taskListener.dataDownloadedSuccessfully(result);
        } else {
            taskListener.dataDownloadFailed(result);
        }
    }

    public PerformRiderTaskListener getTaskListener() {
        return taskListener;
    }

    public void setTaskListener(PerformRiderTaskListener taskListener) {
        this.taskListener = taskListener;
    }

    public interface PerformRiderTaskListener {

        void dataDownloadedSuccessfully(RiderInfo riderInfo);

        void dataDownloadFailed(RiderInfo riderInfo);
    }
}