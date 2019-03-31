package org.autoride.autoride.networks.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.autoride.autoride.configs.AppsSingleton;
import org.autoride.autoride.constants.AppsConstants;
import org.autoride.autoride.networks.managers.api.CallApi;
import org.autoride.autoride.networks.managers.api.RequestedBodyBuilder;
import org.autoride.autoride.networks.managers.api.RequestedHeaderBuilder;
import org.autoride.autoride.networks.managers.api.RequestedUrlBuilder;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class PerformRidersDriverTask extends AsyncTask<String, Integer, String> implements AppsConstants {

    private String TAG = "PerformRiderTask";
    private PerformRidersRiderTaskListener taskListener;
    private OkHttpClient okHttpClient = AppsSingleton.getInstance().getOkHttpClient();
    private String methods;
    private String url;
    private JSONObject postBody;
    private JSONObject postHeader;

    public PerformRidersDriverTask(String methods, String url, JSONObject postBody, JSONObject postHeader) {
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
    protected String doInBackground(String... strings) {
        String response = null;
        try {
            if (methods.equalsIgnoreCase(POST) && postHeader == null) {
                response = CallApi.POST(
                        okHttpClient,
                        RequestedUrlBuilder.buildRequestedPOSTUrl(url),
                        RequestedBodyBuilder.buildRequestedBody(postBody)
                );
            } else if (methods.equalsIgnoreCase(GET) && postHeader == null) {
                response = CallApi.GET(
                        okHttpClient,
                        RequestedUrlBuilder.buildRequestedGETUrl(url, postBody)
                );
            } else if (methods.equalsIgnoreCase(POST) && postHeader != null) {
                response = CallApi.POST(
                        okHttpClient,
                        RequestedUrlBuilder.buildRequestedPOSTUrl(url),
                        RequestedBodyBuilder.buildRequestedBody(postBody),
                        RequestedHeaderBuilder.buildRequestedHeader(postHeader)
                );
            } else if (methods.equalsIgnoreCase(GET) && postHeader != null) {
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
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.i(TAG, "result " + result);
        if (result != null) {
            taskListener.dataDownloadedSuccessfully(result);
        } else {
            taskListener.dataDownloadFailed(result);
        }
    }

    public PerformRidersRiderTaskListener getTaskListener() {
        return taskListener;
    }

    public void setTaskListener(PerformRidersRiderTaskListener taskListener) {
        this.taskListener = taskListener;
    }

    public interface PerformRidersRiderTaskListener {

        void dataDownloadedSuccessfully(String driverInfo);

        void dataDownloadFailed(String driverInfo);
    }
}