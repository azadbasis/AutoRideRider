package org.autoride.autoride.constants;

import org.autoride.autoride.R;
import org.autoride.autoride.applications.AutoRideRiderApps;

public interface AppsConstants {

    String WEB_RESPONSE_STATUS_CODE = "statusCode";
    String WEB_RESPONSE_STATUS = "status";
    String WEB_RESPONSE_SUCCESS = "success";
    String IS_DATA = "is_data";
    String WEB_RESPONSE_ERROR = "error";
    String WEB_RESPONSE_ERRORS = "errors";
    String WEB_RESPONSE_MESSAGE = "message";
    String WEB_ERRORS_MESSAGE = "Something Went Wrong. Please Try Again Later!!!";
    String UNABLE_FOUND_DRIVER = "Unable to found nearest Driver, Please try again";
    String UNABLE_FOUND_FARE = "Unable to found fare details";
    String VERSION_NAME = "versionName";

    String WEB_RESPONSE_CODE_200 = "200";
    String WEB_RESPONSE_CODE_401 = "401";
    String WEB_RESPONSE_CODE_404 = "404";
    String WEB_RESPONSE_CODE_406 = "406";
    String WEB_RESPONSE_CODE_500 = "500";

    String WEB_RESPONSE_DATA = "data";
    String WEB_RESPONSE_USER = "user";
    String IS_AVAILABLE = "is_available";

    String SESSION_SHARED_PREFERENCES = "session";
    String ACCESS_TOKEN = "accessToken";
    String ACCESS_TOKENS = "access_token";
    String REMEMBER_TOKEN = "rememberToken";
    String RIDER_ID = "userId";
    String USER_ID = "userId";
    String PUBLIC_KEY = "publicKey";
    String PRIVATE_KEY = "privateKey";
    String TRIP_DRIVER_FIRE_BASE_TOKEN = "fcm_token";

    String WEB_RESPONSE_DRIVER = "driver";
    String DRIVER_ID = "userId";
    String DRIVER_FIRE_BASE_TOKEN = "fireBaseToken";
    String RIDER_FIRE_BASE_TOKEN = "fireBaseToken";
    String DRIVER_LOCATION = "location";

    String PHONE = "phone";
    String PASSWORD = "password";
    String FIRST_NAME = "firstName";
    String LAST_NAME = "lastName";
    String FULL_NAME = "fullName";
    String ROLE = "role";
    String USABLE_DISCOUNT = "useableDiscount";
    String RIDER_SEARCH_VEHICLE_TYPE = "rider_vehicle";
    String LAT = "lat";
    String LNG = "lng";
    String PROFILE_PHOTO = "imageUrl";
    String COVER_PHOTO = "coverImageUrl";
    String PROMOTION_CODE = "promoCode";
    String EMAIL = "email";
    String RIDER_ACCOUNT_NO = "accountNo";
    String RIDER_MAIN_BALANCE = "balance";
    String USABLE_BALANCE = "usableBalance";
    String COMMISSION = "commission";
    String TOTAL_COMMISSION = "totalCommission";
    String TOTAL_RIDE = "totalRide";
    String RIDER_IDENTIFIER = "identifier";
    String RIDER_RATING = "rating";
    String PROFILE_PICTURE = "profilePic";
    String CUR_PASS = "currentPassword";
    String NEW_PASS = "newPassword";

    String ADDRESS = "address";
    String HOUSE = "house";
    String ROAD = "road";
    String ZIP_CODE = "zipCode";
    String FAX = "fax";
    String UNIT = "unit";
    String CITY = "stateProvince";
    String COUNTRY = "country";

    String RIDE_HISTORY = "history";
    String RIDE_DATE_TIME = "startTime";
    String RIDE_AMOUNT = "amount";
    String RIDE_STATUS = "modeStatus";
    String PAYMENT_TYPE = "Cash";
    String VEHICLE = "vehicle";
    String RIDE_TEXT = "You ride with ";
    String RIDE_RECEIPT = "receipt";
    String DISTANCE_FARE = "distance";
    String TIME_FARE = "time";
    String TAKA = "Tk ";
    String RECEIPT_TITLE = "Auto Ride ";
    String RECEIPT_TITLE2 = " Receipt";

    String PROMOTION_CODE_START = "Promotion Code ( ";
    String PROMOTION_CODE_END = " )";
    String ACCOUNT_NO_START = "Account Number ( ";
    String ACCOUNT_NO_END = " )";
    String DOUBLE_QUOTES = "";
    String NULLS = "null";

    String HOUSES = "House : ";
    String ROADS = ", Road : ";
    String UNITS = "Unit : ";
    String ZIP_CODES = ", ZipCode : ";
    String FAXES = "Fax : ";
    String CITIES = "City : ";
    String COUNTRIES = ", Country : ";

    String PROTOCOL_HTTP = "http";
    String PROTOCOL_HTTPS = "https";
    String AMPERSAND = "&";
    String CURRENCY = "BDT ";

    String GET = "GET";
    String POST = "POST";
    String ERROR_RESPONSE = "error_response ";
    String HTTP_RESPONSE = "ok_http_response ";

    String CAR = "car";
    String CAR_EXECUTIVE = "carExecutive";
    String CAR_HOURLY = "carHourly";
    String CAR_HOURLY_EXECUTIVE = "carExecutiveHourly";
    String BIKE = "bike";
    String BIKE_HOURLY = "bikeHourly";
    String CNG = "cng";
    String CNG_HOURLY = "cngHourly";
    String PICKUP = "pickup";

    String BASE_FARE = "baseFare";
    String MINIMUM_FARE = "minimumFare";
    String FARE_PER_MINUTE = "minuteFare";
    String FARE_PER_KILOMETER = "kilometerFare";
    String FARE_OUT_OF_CITY = "outOfCity";

    String NO_NETWORK_AVAILABLE = AutoRideRiderApps.getInstance().getResources().getString(R.string.no_network_available);
    String LOCATION_DISABLE = AutoRideRiderApps.getInstance().getResources().getString(R.string.location_disable);

    String LOCATION_NAME = "https://maps.googleapis.com/maps/api/geocode/json?";
    String POLY_POINTS = "https://maps.googleapis.com/maps/api/directions/json?";
    String SEPARATOR = "/";

    String RIDING_MODE_STATUS = "modeStatus";
    String MODE_STATUS_RIDING = "riding";
    String MODE_STATUS_COMPLETE = "complete";
    String TRIP_DETAILS = "tripDetails";

    String PREDICTED_AMOUNT = "predictedAmount";
    String PREDICTED_KILOMETER = "predictedKilometer";
    String PREDICTED_MINUTE = "predictedMinute";

    String PICKUP_LOCATION = "pickupLocation";
    String DESTINATION = "destination";

    String VEHICLE_DETAILS = "vehicleDetails";
    String DRIVER_DETAILS = "driverDetails";
    String RIDER_DETAILS = "userDetails";

    String VEHICLE_TYPE = "vehicleType";
    String VEHICLE_BRAND = "vehicleBrand";
    String VEHICLE_MODEL = "vehicleModel";
    String VEHICLE_NUMBER = "vehicleNumber";
    String PARTNER_RATING = "driverRating";
    String RATING = "rating";
    String DRIVER_IDS = "driverId";
    String FIRE_BASE_TOKEN = "fireBaseToken";

    String FATE_NOTES = "The fare will  be the price presented upon\nbooking,or, if the journey changes,the fare\nwill be based on the rates provided.Tap  information icon -- ⓘ --for fare details.";
    String BIKE_NOTES = "Bikes for quick rides";
    String CAR_NOTES = "Rental for local city travel";
}