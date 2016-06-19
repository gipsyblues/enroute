package com.enroute.enroute.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.enroute.enroute.R;
import com.enroute.enroute.adapter.BusinessArrayAdapter;
import com.enroute.enroute.fragments.MainFragment;
import com.enroute.enroute.fragments.ResultsFragment;
import com.enroute.enroute.fragments.SearchFragment;
import com.enroute.enroute.model.Business;
import com.enroute.enroute.utility.GlobalVars;
import com.enroute.enroute.utility.Utility;
import com.enroute.enroute.utility.VolleyInstance;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    private final String DIRECTIONS_BASE_URL = "http://dfe15b58.ngrok.io/api/directions?";
    private final String DEFAULT_DESTINATION = "2515 Benvenue Avenue, Berkeley, CA";
    private final int POI_NAVIGATION_REQUEST = 1;

    private VolleyInstance mRequest;
    private RequestQueue mRequestQueue;

    private TextView mSearchField;
    private String mDestination;

    private ArrayList<Business> mBusinesses;
//
//FloatingActionButton button = (FloatingActionButton) findViewById(R.id.fab);
//    button.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            if (star) {
//                FloatingActionButton btn = (FloatingActionButton)findViewById(R.id.fab);
//                btn.setImageResource(R.drawable.ruler2);
//                star = false;
//            } else {
//                FloatingActionButton btn = (FloatingActionButton)findViewById(R.id.fab);
//                btn.setImageResource(R.drawable.star);
//                star = true;
//            }
//        }
//    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRequest = VolleyInstance.getInstance(getApplicationContext());
        mRequestQueue = mRequest.getRequestQueue();

        Utility.replaceFragment(this, MainFragment.newInstance(), R.id.container, "FragmentMain");

        final ActionBar actionBar = getSupportActionBar();
        LayoutInflater inflater = (LayoutInflater)
                this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.actionbar_view, null);
        actionBar.setCustomView(customView,
                new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        Toolbar parent = (Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0, 0);

        final FragmentActivity activity = this;
        mSearchField = (TextView) customView.findViewById(R.id.searchField);
        mSearchField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportActionBar().hide();
                Utility.backStackFragment(activity, SearchFragment.newInstance(), R.id.container,
                        "SearchFragment");
            }
        });
    }

    public void startSearch(String search, String destination) {
        if (destination.toUpperCase().equals(GlobalVars.HOME) || destination.equals("")) {
            destination = DEFAULT_DESTINATION;
        }
        String url = getRouteUrl(destination, search);
        sendRouteRequest(url);

        mSearchField.setText(search);
        mDestination = destination;
    }

    /**
     *
     * Getter methods.
     *
     */
    public TextView getSearchField() {
        return mSearchField;
    }

    public String getDestinationString() {
        return mDestination;
    }

    public ArrayList<Business> getBusinesses() {
        return mBusinesses;
    }

    /**
     *
     * Ent Directions API Request Methods
     *
     */
    public String cleanString(String location) {
        return location.trim().replace(" ", "+");
    }

    private String getRouteUrl(String destination, String search) {
        destination = cleanString(destination);
        search = cleanString(search);
        String origin = getStartLocation();
        String url = DIRECTIONS_BASE_URL + "origin=" + origin + "&destination=" + destination
                + "&search=" + search;
        return url;
    }

    private String getStartLocation() {
        Location origin = getCurrentLocation();
        if (origin == null) {
            // TODO: Implement start location EditText OR popup to prompt user to turn on GPS
            Log.d("DEBUG", "origin is null.");
            return "3104+Heitman+Ct,+San+Jose,+CA";
        } else {
            return String.valueOf(origin.getLatitude()) + ","
                    + String.valueOf(origin.getLongitude());
        }
    }

    private Location getCurrentLocation() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getAllProviders();
        Location location;
        for (String provider : providers) {
            location = locationManager.getLastKnownLocation(provider);
            if (location != null) return location;
        }
        return null;
    }

    private void sendRouteRequest(final String url) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.d("DEBUG", "onResponse hit.");
                        Log.d("DEBUG", "jsonObject:");
                        Log.d("DEBUG", jsonObject.toString());
                        try {
                            JSONArray businessList = jsonObject.getJSONArray(GlobalVars.ENT_DATA);
                            mBusinesses = Business.fromJSONArray(businessList);
                            Utility.replaceFragment(MainActivity.this,
                                    ResultsFragment.newInstance(), R.id.container);
                        } catch (Exception e) {
                            Log.d("DEBUG", e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.d("DEBUG", "onErrorResponse hit. Error: " + volleyError.toString());
                    }
                }
        );
        mRequestQueue.add(request);
    }

    /**
     *
     * Google Maps API Methods
     *
     */
    public void startNavigation(double latitude, double longitude) {
        Uri gmapsIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
        launchNavigation(gmapsIntentUri);
    }

    public void startNavigation(String address) {
        // Just in case
        address = cleanString(address);
        Uri gmapsIntentUri = Uri.parse("google.navigation:q=" + address);
        launchNavigation(gmapsIntentUri);
    }

    private void launchNavigation(Uri gmapsUri) {
        Intent gmapsIntent = new Intent(Intent.ACTION_VIEW, gmapsUri);
        gmapsIntent.setPackage("com.google.android.apps.maps");

        // Verify it resolves
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(gmapsIntent, 0);
        boolean isIntentSafe = activities.size() > 0;

        if (isIntentSafe) startActivityForResult(gmapsIntent, POI_NAVIGATION_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Finished gmaps activity. Reroute to destination!
        // Navigate to 2515
        //startNavigation(37.864984, -122.254763);
        // TODO: Investigate whether or not Google Maps will return an onActivityResult.
        // TODO: Investigate if possible to listen to any Google Maps signals or events.
        // TODO: Investigate prompting user to continue to final destination or cancel
        //       when they open the app again.
        // TODO: Investigate a background task that starts another navigation when
        //       the user gets close to the middle destination.
        Log.d("DEBUG", "requestCode: " + requestCode + " resultCode: " + resultCode);
        Log.d("DEBUG", "Why is it hitting this case already.");
    }

    @Override
    public void onBackPressed() {
        int numFragments = getSupportFragmentManager().getBackStackEntryCount();

        if (numFragments > 0) {
            getSupportFragmentManager().popBackStack();
            getSupportActionBar().show();
        } else {
            super.onBackPressed();
        }
    }
}
