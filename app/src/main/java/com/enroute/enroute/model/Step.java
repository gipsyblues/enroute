package com.enroute.enroute.model;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class Step {

    public double getStartLong() {
        return startlong;
    }

    public void setStartLong(double startlong) {
        this.startlong = startlong;
    }

    public double getStartLat() {
        return startlat;
    }

    public void setStartLat(double startlat) {
        this.startlat = startlat;
    }

    public double getEndLong() {
        return endlong;
    }

    public void setEndLong(double endlong) {
        this.endlong = endlong;
    }

    public double getEndLat() {
        return endlat;
    }

    public void setEndLat(double endlat) {
        this.endlat = endlat;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    //List out the attributes
    private int distance;
    //private static String ratingImageUrl;
    private double startlong;
    private double startlat;
    private double endlong;
    private double endlat;



    // Deserialize the JSON
    // create method to convert business.fromJson({..}") => <business>

    public static Step fromJSON(JSONObject jsonObject) {
        Step step = new Step();

        // Extract the values from the json, store them

        try {

            step.distance = jsonObject.getJSONObject("distance").getInt("value");
            step.startlong = jsonObject.getJSONObject("start_location").getDouble("lng");
            step.startlat = jsonObject.getJSONObject("start_location").getDouble("lat");
            step.endlong = jsonObject.getJSONObject("end_location").getDouble("lng");
            step.endlat = jsonObject.getJSONObject("end_location").getDouble("lat");

        } catch (JSONException e ){
            e.printStackTrace();

        }

        // Return the business object
        return step;

    }


    // Pass Json array and ourtout us list of businesss
    public static ArrayList<Step> fromJSONArray(JSONObject jsonObject) {
        ArrayList<Step> steps = new ArrayList<Step>();
        JSONArray jsonsteplist = null;
        try {
            jsonsteplist = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Iterrate the json array and create businesss
        System.out.println(jsonsteplist.length());
        for (int i=0; i < jsonsteplist.length() ; i++){

            try {
                JSONObject jsonstep = jsonsteplist.getJSONObject(i);

                Step stepz = Step.fromJSON(jsonstep);

                if (stepz != null ){
                    steps.add(stepz);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }

        }

        // return the finished list
        return steps;

    }


}
