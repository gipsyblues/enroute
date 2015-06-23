package com.enroute.enroute.model;

import com.enroute.enroute.utility.GlobalVars;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Step {

    private int distance;
    private double endLat;
    private double endLong;
    private double startLat;
    private double startLong;

    public int getDistance() {
        return distance;
    }

    public double getEndLat() {
        return endLat;
    }

    public double getEndLong() {
        return endLong;
    }

    public double getStartLat() {
        return startLat;
    }

    public double getStartLong() {
        return startLong;
    }

    // Convert JSONObject to a Step Object
    public static Step fromJSON(JSONObject jsonObject) {
        Step step = new Step();
        try {
            step.distance = jsonObject
                    .getJSONObject(GlobalVars.MAP_DISTANCE).getInt(GlobalVars.MAP_VALUE);
            step.startLong = jsonObject
                    .getJSONObject(GlobalVars.MAP_START_LOC).getDouble(GlobalVars.MAP_LONG);
            step.startLat = jsonObject
                    .getJSONObject(GlobalVars.MAP_START_LOC).getDouble(GlobalVars.MAP_LAT);
            step.endLong = jsonObject
                    .getJSONObject(GlobalVars.MAP_END_LOC).getDouble(GlobalVars.MAP_LONG);
            step.endLat = jsonObject
                    .getJSONObject(GlobalVars.MAP_END_LOC).getDouble(GlobalVars.MAP_LAT);
        } catch (JSONException e){
            e.printStackTrace();
        }
        return step;
    }

    // Convert JSONArray to an ArrayList of Steps
    public static ArrayList<Step> fromJSONArray(JSONObject jsonObject) {
        ArrayList<Step> stepsList = new ArrayList<>();
        JSONArray jsonArrayObject;
        try {
            jsonArrayObject = jsonObject
                    .getJSONArray(GlobalVars.MAP_ROUTES).getJSONObject(0)
                    .getJSONArray(GlobalVars.MAP_LEGS).getJSONObject(0)
                    .getJSONArray(GlobalVars.MAP_STEPS);
            for (int i = 0; i < jsonArrayObject.length(); i++) {
                JSONObject jsonStep = jsonArrayObject.getJSONObject(i);
                Step step = Step.fromJSON(jsonStep);
                if (step != null) {
                    stepsList.add(step);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return stepsList;
    }
}
