package org.autoride.autoride.networks.parsers;

import android.util.Log;

import org.autoride.autoride.model.Address;
import org.autoride.autoride.model.RiderInfo;
import org.autoride.autoride.constants.AppsConstants;
import org.json.JSONException;
import org.json.JSONObject;

public class RiderTaskParser implements AppsConstants {
    public static RiderInfo taskParse(String sResponse) {
        String TAG = "RiderTaskParser";
        RiderInfo riderInfo = new RiderInfo();
        JSONObject responseObj = null;
        try {
            if (sResponse != null) {
                responseObj = new JSONObject(sResponse);
                if (responseObj.has(WEB_RESPONSE_STATUS_CODE)) {

                    if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(String.valueOf(200))) {

                        if (responseObj.optString(WEB_RESPONSE_STATUS).equalsIgnoreCase(WEB_RESPONSE_SUCCESS)) {
                            riderInfo.setStatus(responseObj.optString(WEB_RESPONSE_STATUS));
                            if (responseObj.has(WEB_RESPONSE_MESSAGE)) {
                                riderInfo.setWebMessage(responseObj.optString(WEB_RESPONSE_MESSAGE));
                            } else {
                                riderInfo.setWebMessage(WEB_ERRORS_MESSAGE);
                            }
                        }

                        if (responseObj.optString(WEB_RESPONSE_STATUS).equalsIgnoreCase(WEB_RESPONSE_ERROR)) {
                            riderInfo.setStatus(responseObj.optString(WEB_RESPONSE_STATUS));
                            if (responseObj.has(WEB_RESPONSE_MESSAGE)) {
                                riderInfo.setWebMessage(responseObj.optString(WEB_RESPONSE_MESSAGE));
                            } else {
                                riderInfo.setWebMessage(WEB_ERRORS_MESSAGE);
                            }
                        }

                        if (responseObj.has(RIDER_FIRE_BASE_TOKEN)) {
                            riderInfo.setRiderFireBaseToken(responseObj.getString(RIDER_FIRE_BASE_TOKEN));
                        }

                        if (responseObj.has(PROFILE_PHOTO)) {
                            riderInfo.setProfilePhoto(responseObj.optString(PROFILE_PHOTO));
                        }
                    }

                    if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_401)) {
                        riderInfo.setStatus(WEB_RESPONSE_ERRORS);
                        if (responseObj.has(WEB_RESPONSE_ERROR)) {
                            riderInfo.setWebMessage(responseObj.optString(WEB_RESPONSE_ERROR));
                            Log.i(TAG, WEB_RESPONSE_ERRORS + " " + riderInfo.getWebMessage());
                        }
                    }

                    if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_404)) {
                        riderInfo.setStatus(WEB_RESPONSE_ERRORS);
                        if (responseObj.has(WEB_RESPONSE_ERROR)) {
                            riderInfo.setWebMessage(responseObj.optString(WEB_RESPONSE_ERROR));
                            Log.i(TAG, WEB_RESPONSE_ERRORS + " " + riderInfo.getWebMessage());
                        }
                    }

                    if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_406)) {
                        riderInfo.setStatus(WEB_RESPONSE_ERRORS);
                        if (responseObj.has(WEB_RESPONSE_ERROR)) {
                            riderInfo.setWebMessage(responseObj.optString(WEB_RESPONSE_ERROR));
                            Log.i(TAG, WEB_RESPONSE_ERRORS + " " + riderInfo.getWebMessage());
                        }
                    }

                    if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_500)) {
                        riderInfo.setStatus(WEB_RESPONSE_ERRORS);
                        if (responseObj.has(WEB_RESPONSE_ERROR)) {
                            riderInfo.setWebMessage(responseObj.optString(WEB_RESPONSE_ERROR));
                            Log.i(TAG, WEB_RESPONSE_ERRORS + " " + riderInfo.getWebMessage());
                        }
                    }
                } else {
                    riderInfo.setStatus(WEB_RESPONSE_ERRORS);
                    riderInfo.setWebMessage(WEB_ERRORS_MESSAGE);
                }

                if (responseObj.has(WEB_RESPONSE_DATA)) {
                    JSONObject dataObj = responseObj.optJSONObject(WEB_RESPONSE_DATA);
                    if (dataObj != null) {
                        try {

                            if (dataObj.has(ACCESS_TOKEN)) {
                                riderInfo.setAccessToken(dataObj.optString(ACCESS_TOKEN));
                            }

                            if (dataObj.has(REMEMBER_TOKEN)) {
                                riderInfo.setRememberToken(dataObj.optString(REMEMBER_TOKEN));
                            }

                            if (dataObj.has(PROMOTION_CODE)) {
                                riderInfo.setPromotionCode(dataObj.optString(PROMOTION_CODE));
                            }

                            if (dataObj.has(IS_AVAILABLE)) {
                                riderInfo.setAvailable(dataObj.optBoolean(IS_AVAILABLE));
                                if (riderInfo.isAvailable()) {
                                    riderInfo.setStatus(WEB_RESPONSE_SUCCESS);
                                } else if (!riderInfo.isAvailable()) {
                                    riderInfo.setStatus(WEB_RESPONSE_SUCCESS);
                                }
                            }

                            if (dataObj.has(WEB_RESPONSE_USER)) {
                                JSONObject userObj = dataObj.optJSONObject(WEB_RESPONSE_USER);
                                if (userObj != null) {

                                    if (userObj.has(RIDER_ID)) {
                                        riderInfo.setRiderId(userObj.optString(RIDER_ID));
                                    }

                                    if (userObj.has(PHONE)) {
                                        riderInfo.setPhone(userObj.optString(PHONE));
                                    }

                                    if (userObj.has(RIDER_RATING)) {
                                        riderInfo.setRiderRating(userObj.optString(RIDER_RATING));
                                    }

                                    if (userObj.has(FIRST_NAME)) {
                                        riderInfo.setFirstName(userObj.optString(FIRST_NAME));
                                    }

                                    if (userObj.has(LAST_NAME)) {
                                        riderInfo.setLastName(userObj.optString(LAST_NAME));
                                    }
                                    riderInfo.setFullName(userObj.optString(FIRST_NAME) + " " + userObj.optString(LAST_NAME));

                                    if (userObj.has(ROLE)) {
                                        riderInfo.setRole(userObj.optString(ROLE));
                                    }

                                    if (userObj.has(USABLE_DISCOUNT)) {
                                        riderInfo.setUsableDiscount(userObj.optString(USABLE_DISCOUNT));
                                    }

                                    if (userObj.has(LAT)) {
                                        riderInfo.setLastLatitude(userObj.optString(LAT));
                                    }

                                    if (userObj.has(LNG)) {
                                        riderInfo.setLastLongitude(userObj.optString(LNG));
                                    }

                                    // this profile photo in data user
                                    if (userObj.has(PROFILE_PHOTO)) {
                                        riderInfo.setProfilePhoto(userObj.optString(PROFILE_PHOTO));
                                    }

                                    if (userObj.has(COVER_PHOTO)) {
                                        riderInfo.setCoverPhoto(userObj.optString(COVER_PHOTO));
                                    }

                                    if (userObj.has(EMAIL)) {
                                        riderInfo.setEmail(userObj.optString(EMAIL));
                                    }

                                    // this promotion code in data user
                                    if (userObj.has(PROMOTION_CODE)) {
                                        riderInfo.setPromotionCode(userObj.optString(PROMOTION_CODE));
                                    }

                                    if (userObj.has(RIDER_MAIN_BALANCE)) {
                                        riderInfo.setMainBalance(userObj.optString(RIDER_MAIN_BALANCE));
                                    }

                                    if (userObj.has(USABLE_BALANCE)) {
                                        riderInfo.setUsableBalance(userObj.optString(USABLE_BALANCE));
                                    }

                                    if (userObj.has(COMMISSION)) {
                                        riderInfo.setCommission(userObj.optString(COMMISSION));
                                    }

                                    if (userObj.has(TOTAL_RIDE)) {
                                        riderInfo.setTotalRide(userObj.optString(TOTAL_RIDE));
                                    }

                                    if (userObj.has(TOTAL_COMMISSION)) {
                                        riderInfo.setTotalCommission(userObj.optString(TOTAL_COMMISSION));
                                    }

                                    if (userObj.has(RIDER_ACCOUNT_NO)) {
                                        riderInfo.setRiderAccountNo(userObj.optString(RIDER_ACCOUNT_NO));
                                    }

                                    if (userObj.has(ADDRESS)) {
                                        JSONObject addressObj = userObj.optJSONObject(ADDRESS);
                                        if (addressObj != null) {
                                            Address addressInfo = new Address();
                                            if (addressObj.has(HOUSE)) {
                                                addressInfo.setHouse(addressObj.optString(HOUSE));
                                            }

                                            if (addressObj.has(ROAD)) {
                                                addressInfo.setRoad(addressObj.optString(ROAD));
                                            }

                                            if (addressObj.has(ZIP_CODE)) {
                                                addressInfo.setZipCode(addressObj.optString(ZIP_CODE));
                                            }

                                            if (addressObj.has(FAX)) {
                                                addressInfo.setFax(addressObj.optString(FAX));
                                            }

                                            if (addressObj.has(UNIT)) {
                                                addressInfo.setUnit(addressObj.optString(UNIT));
                                            }

                                            if (addressObj.has(CITY)) {
                                                addressInfo.setCity(addressObj.optString(CITY));
                                            }

                                            if (addressObj.has(COUNTRY)) {
                                                addressInfo.setCountry(addressObj.optString(COUNTRY));
                                            }
                                            riderInfo.setRiderAddress(addressInfo);
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            // riderInfo = null;
                            riderInfo.setStatus(WEB_RESPONSE_ERRORS);
                            riderInfo.setWebMessage(WEB_ERRORS_MESSAGE);
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                riderInfo.setStatus(WEB_RESPONSE_ERRORS);
                riderInfo.setWebMessage(WEB_ERRORS_MESSAGE);
            }
        } catch (JSONException e) {
            // riderInfo = null;
            riderInfo.setStatus(WEB_RESPONSE_ERRORS);
            riderInfo.setWebMessage(WEB_ERRORS_MESSAGE);
            e.printStackTrace();
        }
        return riderInfo;
    }
}