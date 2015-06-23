package com.enroute.enroute.model;

import com.enroute.enroute.utility.GlobalVars;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Business {

    private String businessName;
    private String categories;
    private double distance;
    private String imageUrl;
    private String locationLine1;
    private String locationLine2;
    private String mobileUrl;
    private String phone;
    private String ratingImageUrl;
    private double stars;

    public String getBusinessName() {
        return businessName;
    }

    public String getCategories() {
        return categories;
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

    public String getMobileUrl() {
        return mobileUrl;
    }

    public String getPhone() {
        return phone;
    }

    public String getRatingImageUrl() {
        return ratingImageUrl;
    }

    public double getStars() {
        return stars;
    }

    // Convert JSONObject to a Business object
    public static Business fromJSON(JSONObject jsonObject) {
        Business business = new Business();
        String category;
        try {
            business.businessName = jsonObject.getString(GlobalVars.YELP_NAME);
            business.distance = jsonObject.getDouble(GlobalVars.YELP_DISTANCE);
            business.imageUrl = jsonObject.getString(GlobalVars.YELP_IMG_URL);
            business.mobileUrl = jsonObject.getString(GlobalVars.YELP_MOBILE_URL);
            business.phone = jsonObject.getString(GlobalVars.YELP_PHONE);
            business.ratingImageUrl = jsonObject.getString(GlobalVars.YELP_RATING_URL);
            business.stars = jsonObject.getDouble(GlobalVars.YELP_RATING);
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
            try {
                String categoryString = jsonObject.getString(GlobalVars.YELP_CATEGORIES);
                int ind = categoryString.indexOf(',');
                category = categoryString.substring(3, ind - 1);
            } catch (Exception e) {
                category = "";
            }
            business.categories = category;
        } catch (JSONException e){
            e.printStackTrace();
        }
        return business;
    }

    // Convert JSONArray to an ArrayList of Businesses
    public static ArrayList<Business> fromJSONArray(JSONArray jsonArray) {
        ArrayList<Business> businessesList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++){
            try {
                JSONObject jsonBusiness = jsonArray.getJSONObject(i);
                Business business = Business.fromJSON(jsonBusiness);
                if (business != null ){
                    businessesList.add(business);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return businessesList;
    }
}
