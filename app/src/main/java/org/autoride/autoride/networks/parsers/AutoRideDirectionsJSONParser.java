package org.autoride.autoride.networks.parsers;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AutoRideDirectionsJSONParser {

    public List<List<HashMap<String, String>>> arDParser(JSONObject jsObject) {

        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
        JSONArray arJRoutes = null;
        JSONArray arJLegs = null;
        JSONArray arJSteps = null;
        JSONObject arJDistance = null;
        JSONObject arJDuration = null;

        try {

            arJRoutes = jsObject.getJSONArray("routes");

            /** Traversing all routes */
            for (int i = 0; i < arJRoutes.length(); i++) {
                arJLegs = ((JSONObject) arJRoutes.get(i)).getJSONArray("legs");

                List<HashMap<String, String>> arPath = new ArrayList<HashMap<String, String>>();

                /** Traversing all legs */
                for (int j = 0; j < arJLegs.length(); j++) {

                    /** Getting distance from the json data */
                    arJDistance = ((JSONObject) arJLegs.get(j)).getJSONObject("distance");
                    HashMap<String, String> arHMDistance = new HashMap<String, String>();
                    arHMDistance.put("distance", arJDistance.getString("text"));

                    /** Getting duration from the json data */
                    arJDuration = ((JSONObject) arJLegs.get(j)).getJSONObject("duration");
                    HashMap<String, String> arHMDuration = new HashMap<String, String>();
                    arHMDuration.put("duration", arJDuration.getString("text"));

                    /** Adding distance object to the path */
                    arPath.add(arHMDistance);

                    /** Adding duration object to the path */
                    arPath.add(arHMDuration);

                    arJSteps = ((JSONObject) arJLegs.get(j)).getJSONArray("steps");

                    /** Traversing all steps */
                    for (int k = 0; k < arJSteps.length(); k++) {
                        String arPolyline = "";
                        arPolyline = (String) ((JSONObject) ((JSONObject) arJSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> llList = arPolyLineDecode(arPolyline);

                        /** Traversing all points */
                        for (int l = 0; l < llList.size(); l++) {
                            HashMap<String, String> arHM = new HashMap<String, String>();
                            arHM.put("lat", Double.toString(((LatLng) llList.get(l)).latitude));
                            arHM.put("lng", Double.toString(((LatLng) llList.get(l)).longitude));
                            arPath.add(arHM);
                        }
                    }
                }

                routes.add(arPath);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }

        return routes;
    }

    /**
     * Method to decode polyline points
     * Courtesy : jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     */
    private List<LatLng> arPolyLineDecode(String arEncoded) {

        List<LatLng> polyList = new ArrayList<LatLng>();
        int index = 0, len = arEncoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = arEncoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = arEncoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng pLatLon = new LatLng((((double) lat / 1E5)), (((double) lng / 1E5)));

            polyList.add(pLatLon);
        }

        return polyList;
    }
}