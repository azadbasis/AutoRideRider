package org.autoride.autoride.utils;

public class Constants {

    public static final String KEY_ANIM_TYPE = "anim_type";
    public static final String KEY_TITLE = "anim_title";

    public enum TransitionType {
        ExplodeJava, ExplodeXML, SlideJava, SlideXML, FadeJava, FadeXML
    }

    public static final String URL = "http://128.199.80.10/golden/app/user/reference/executive?userId=";   // userId=5adc77eac31240181c1a919c
    public static final String SMS_URL = "http://128.199.80.10/golden/app/sendcode/phone?phone=";   // userId=5adc77eac31240181c1a919c
    public static final String  INVITE_REQUEST_CHECK_URL = "http://128.199.80.10/golden/app/user/check/tracking/invite?userId=";   // userId=5adc77eac31240181c1a919c
  // public static final String INVITE_REQUEST_CHECK_URL = "http://128.199.80.10/golden/app/user/check/tracking/invite?phone=";   // userId=5adc77eac31240181c1a919c
    public static final String REQUEST_STATUS_CHECK_URL = "http://128.199.80.10/golden/app/user/tracking/invite?userId=";//5adc77eac31240181c1a919c&trackerId=5aefef5fc3124043931c33f1";   // userId=5adc77eac31240181c1a919c
    public static final String TRACK_USER_LIST_URL = "http://128.199.80.10/golden/app/user/tracking/user/list?userId=";
    public static final String TRACK_ME_LIST_URL = "http://128.199.80.10/golden/app/user/tracking/self/list?userId=";
    public static final String TRACK_LOCATION_URL = "http://128.199.80.10/golden/app/user/get/tracking/location?userId=";
    public static final String  SET_LOCATION_URL = "http://128.199.80.10/golden/app/user/set/tracking/location?userId=";
    public static final String  LOCAL_TRACKING_STATUS_URL = "http://128.199.80.10/golden/app/user/set/local/tracking/status?userId=";
    public static final String  GLOBAL_TRACKING_STATUS_URL = "http://128.199.80.10/golden/app/user/set/global/tracking/status?userId=";
    public static final String  USER_ACTION_SELF_TRACKING_URL = "http://128.199.80.10/golden/app/user/action/tracking?action=";

}