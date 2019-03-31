package org.autoride.autoride.service.fcm;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceIdService;

import org.autoride.autoride.networks.managers.api.CallApi;
import org.autoride.autoride.networks.managers.api.RequestedBodyBuilder;
import org.autoride.autoride.networks.managers.api.RequestedUrlBuilder;
import org.autoride.autoride.model.FCMToken;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;

public class FcmInstanceIdService extends FirebaseInstanceIdService {

    private OkHttpClient client;
    private String TAG = "FcmInstanceIdService";
    private String fcmToken;
    private String driverId;

    @Override
    public void onCreate() {
        super.onCreate();
        //client = new OkHttpClient();
    }

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        // String refreshToken = FirebaseInstanceId.getInstance().getToken();
        //updateTokenToServer(refreshToken);
    }

    private void updateTokenToServer(String refreshToken) {
        FCMToken FCMToken = new FCMToken(refreshToken);
        fcmToken = FCMToken.getToken();
        driverId = "";
        new SaveFCMToken().execute("");
    }

    @SuppressLint("StaticFieldLeak")
    private class SaveFCMToken extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String response = null;
            try {
                response = CallApi.POST(
                        client,
                        RequestedUrlBuilder.buildRequestedPOSTUrl(url[0]),
                        RequestedBodyBuilder.buildRequestedBody(getSaveTokenBodyJSON())
                );
                Log.i(TAG, "ok_http_response " + response);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            parserFCMToken(result);
        }
    }

    private void parserFCMToken(String response) {

//
//        assert authBean != null;
//        if (authBean.getStatus().equalsIgnoreCase("Success")) {
//            Toast.makeText(getBaseContext(), authBean.getWebMessage(), Toast.LENGTH_SHORT).show();
//        } else if (authBean.getStatus().equalsIgnoreCase("Error")) {
//            Toast.makeText(getBaseContext(), authBean.getWebMessage(), Toast.LENGTH_SHORT).show();
//        } else if (authBean.getStatus().equalsIgnoreCase("Errors")) {
//            Toast.makeText(getBaseContext(), R.string.message_something_went_wrong, Toast.LENGTH_SHORT).show();
//        }
    }

    private JSONObject getSaveTokenBodyJSON() {
        JSONObject postBody = new JSONObject();
        try {
            postBody.put("userId", driverId);
            postBody.put("fcmToken", fcmToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postBody;
    }
}