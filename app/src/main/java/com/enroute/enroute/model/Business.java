package com.enroute.enroute.model;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class Business {

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getRatingImageUrl() {
        return ratingImageUrl;
    }

    public void setRatingImageUrl(String ratingImageUrl) {
        this.ratingImageUrl = ratingImageUrl;
    }

    public double getStars() {
        return stars;
    }

    //List out the attributes
    private String businessName;
    private String ratingImageUrl;
    private String mobile;
    private String categories;
    private String location2;
    private String location1;
    private String phone;
    private double distance;
    private String imageUrl;
    private double stars;

    public String getImageUrl() {
        return imageUrl;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }




    public String getLocation1() {
        return location1;
    }


    public String getLocation2() {
        return location2;
    }



    public double getDistance() {
        return distance;
    }



    // Deserialize the JSON
    // create method to convert business.fromJson({..}") => <business>

    public static Business fromJSON(JSONObject jsonObject) {
        Business business = new Business();
        String cat= null;
        String category="Food";
        String phone = null;

        // Extract the values from the json, store them

        try {

            business.businessName = jsonObject.getString("name");
            business.ratingImageUrl = jsonObject.getString("rating_img_url");
            business.mobile = jsonObject.getString("mobile_url");
            business.location1 = jsonObject.getJSONObject("location").getJSONArray("display_address").getString(0);
            if (jsonObject.getJSONObject("location").getJSONArray("display_address").length() < 3 ) {
                business.location2 = jsonObject.getJSONObject("location").getJSONArray("display_address").getString(1);
            } else if (jsonObject.getJSONObject("location").getJSONArray("display_address").length() > 3 ) {
                business.location2 = jsonObject.getJSONObject("location").getJSONArray("display_address").getString(3);
            } else {
                business.location2 = jsonObject.getJSONObject("location").getJSONArray("display_address").getString(2);
            }
            business.imageUrl = jsonObject.getString("image_url");
            business.distance = jsonObject.getDouble("distance");
            business.stars = jsonObject.getDouble("rating");

            try {
                cat = jsonObject.getString("categories");
                int ind= cat.indexOf(',');
                category=cat.substring(3,ind-1);
            }catch(Exception e) {
                category = "Food";
            }
            business.categories = category;
            try {
                phone = jsonObject.getString("display_phone");
            }catch(Exception e){
                phone = jsonObject.getString("phone");
            }
            business.phone=phone;

        } catch (JSONException e ){
            e.printStackTrace();

        }

        // Return the business object
        return business;

    }


    // Pass Json array and ourtout us list of businesss
    public static ArrayList<Business> fromJSONArray(JSONArray jsonArray) {

        ArrayList<Business> businesss = new ArrayList<>();

        // Iterrate the json array and create businesss
        for (int i=0; i< jsonArray.length() ; i++){

            try {
                JSONObject businessJson = jsonArray.getJSONObject(i);
                Business business = Business.fromJSON(businessJson);
                //System.out.println(business.getBusinessName());

                if (business != null ){
                    businesss.add(business);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }

        }

        // return the finished list
        return businesss;

    }


}
