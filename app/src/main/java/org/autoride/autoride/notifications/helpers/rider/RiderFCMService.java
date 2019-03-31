package org.autoride.autoride.notifications.helpers.rider;

import org.autoride.autoride.model.DataMessage;
import org.autoride.autoride.model.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface RiderFCMService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAiFp49eU:APA91bF7_KtvWHzHp96sWcZXLsXoAsetWBIj1HR-1_aNgM3NF6DzDIk_5Rai8oXHrpPGNLiBabQSPkvjQWn_Dl5x2gtoG3RxMbOoc1ZMDvqbXcmRxjBpJ_NHwLcsqNON04Q7uhtKStfc"
    })
    @POST("fcm/send")
    Call<FCMResponse> sendMessage(@Body DataMessage dody);
}