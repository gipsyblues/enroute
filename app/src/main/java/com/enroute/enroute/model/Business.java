package com.enroute.enroute.model;

import com.enroute.enroute.utility.GlobalVars;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Business {

    private String name;
    private double distance;
    private String imageUrl;
    private String locationLine1;
    private String locationLine2;
    private String phone;
    private double rating;

    public String getName() {
        return name;
    }

    public double getDistance() {
        return distance;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getLocationLine1() {
        return locationLine1;
    }

    public String getLocationLine2() {
        return locationLine2;
    }

    public String getPhone() {
        return phone;
    }

    public double getRating() {
        return rating;
    }

    // Convert JSONObject to a Business object
    public static Business fromJSON(JSONObject jsonObject) {
        try {
            Business business = new Business();
            business.name = jsonObject.getString(GlobalVars.YELP_NAME);
            business.distance = jsonObject.getDouble(GlobalVars.ENT_DISTANCE);
            business.imageUrl = jsonObject.getString(GlobalVars.YELP_IMG_URL);
            business.phone = jsonObject.getString(GlobalVars.YELP_PHONE);
            business.rating = jsonObject.getDouble(GlobalVars.YELP_RATING);
            JSONArray jsonLocation = jsonObject
                    .getJSONObject(GlobalVars.YELP_LOCATION).getJSONArray(GlobalVars.YELP_ADDRESS);
            business.locationLine1 = jsonLocation.getString(0);
            if (jsonLocation.length() < 3 ) {
                business.locationLine2 = jsonLocation.getString(1);
            } else if (jsonLocation.length() > 3 ) {
                business.locationLine2 = jsonLocation.getString(3);
            } else {
                business.locationLine2 = jsonLocation.getString(2);
            }
            return business;
        } catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    // Convert JSONArray to an ArrayList of Businesses
    public static ArrayList<Business> fromJSONArray(JSONArray jsonArray) {
        ArrayList<Business> businessList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++){
            try {
                JSONObject jsonBusiness = jsonArray.getJSONObject(i);
                Business business = Business.fromJSON(jsonBusiness);
                if (business != null ){
                    businessList.add(business);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return businessList;
    }
}
