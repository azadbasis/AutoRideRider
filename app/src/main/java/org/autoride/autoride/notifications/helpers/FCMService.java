package org.autoride.autoride.notifications.helpers;

import org.autoride.autoride.model.DataMessage;
import org.autoride.autoride.model.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface FCMService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAfsGsFFk:APA91bFPCB8xuFoxsdCDLa9-q-WnQ_nbI4AepDK2nwhNSypw4foiM9v8O1oefdVuhh2yja0GljEaoAjkAsl-MVaiW54SWXbjQ7udR53eYmYgHcq3oj8LNNOAXgKnGsro-vPb38VlVCNS"
    })
    @POST("fcm/send")
    Call<FCMResponse> sendMessage(@Body DataMessage dody);
}