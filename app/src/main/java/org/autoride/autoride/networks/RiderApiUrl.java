package org.autoride.autoride.networks;

public interface RiderApiUrl {

    String BASE_URL = "http://128.199.80.10:80/golden/app/";

    String PHONE_NUMBER_EXIST_URL = BASE_URL + "phone/exist";

    String LOGIN_URL = BASE_URL + "user/login";

    String FORGOT_PASSWORD_URL = BASE_URL + "user/forgot/password";

    String PROMO_CODE_SEARCH_URL = BASE_URL + "near/user/provider?";   //  lat=23.8365&lng=90.3695

    String PROMO_CODE_EXIST_URL = BASE_URL + "user/promocode/exist";

    String REGISTRATION_URL = BASE_URL + "user/registration";

    String PROFILE_URL = BASE_URL + "user/profile?";   // userId=

    String UPLOAD_PROFILE_PHOTO_URL = BASE_URL + "user/profile/image/update";

    String SAVE_FIRST_NAME = BASE_URL + "user/firstname/edit";

    String SAVE_LAST_NAME = BASE_URL + "user/lastname/edit";

    String SAVE_EMAIL = BASE_URL + "user/email/edit";

    String SAVE_ADDRESS = BASE_URL + "user/address/edit";

    String UPDATE_PASSWORD_URL = BASE_URL + "user/reset/password";

    String SET_FIRE_BASE_TOKEN = BASE_URL + "driver/fire/base/token/set"; // yet to use rider

    String NEAREST_DRIVER = BASE_URL + "near/driver?";   // vehicle=car&range=20&lat=23.8365&lng=90.3695

    String REQUESTED_DRIVER_URL = BASE_URL + "rider/request/driver?";  // vehicle=car&range=20&lat=23.8365&lng=90.3695

    String FARE_RATE_URL = BASE_URL + "fare/rate";

    String RIDE_CANCEL = BASE_URL + "trip/cancel";

    String DRIVER_LOCATION_URL = BASE_URL + "get/driver/location?";  // driverId=5ab74075c31240384732964e

    String RIDER_VERSION_URL = BASE_URL + "user/version";

    String RIDING_MODE_URL = BASE_URL + "user/trip/data?";  // userId=5ac366cfc312403b0177d5b3

    String RIDE_HISTORY_URL = BASE_URL + "user/riding/history?";  // userId=5ac366cfc312403b0177d5b3

}