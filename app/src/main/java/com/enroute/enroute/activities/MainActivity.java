package com.enroute.enroute.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.enroute.enroute.R;
import com.enroute.enroute.YelpClient;
import com.enroute.enroute.adapter.BusinessArrayAdapter;
import com.enroute.enroute.fragments.MapFragment;
import com.enroute.enroute.fragments.ResultsFragment;
import com.enroute.enroute.fragments.SearchFragment;
import com.enroute.enroute.model.Business;
import com.enroute.enroute.model.Step;
import com.enroute.enroute.utility.DistanceComparator;
import com.enroute.enroute.utility.GlobalVars;
import com.enroute.enroute.utility.Utility;
import com.enroute.enroute.utility.VolleyInstance;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class MainActivity extends ActionBarActivity {

    private final String DIRECTIONS_API_KEY = "AIzaSyBOfq7knvV8qWFG2eztBeL7NKCnNYmB6mU";
    private final String DIRECTIONS_BASE_URL =
            "https://maps.googleapis.com/maps/api/directions/json?key=" + DIRECTIONS_API_KEY + "&";
    private final int POI_NAVIGATION_REQUEST = 1;
    private final String DEFAULT_DESTINATION = "2515 Benvenue Avenue, Berkeley, CA";

    private VolleyInstance mRequest;
    private RequestQueue mRequestQueue;
    private static YelpClient mYelpClient;

    private TextView mSearchField;

    private ArrayList<Step> mStepsArray;
    private ArrayList<Step> mFilteredStepsArray;
    private TreeSet<Business> mSortedBusinesses;
    private static BusinessArrayAdapter aBusinesses;
    private boolean star = true;



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
        mYelpClient = new YelpClient(this);
        aBusinesses = new BusinessArrayAdapter(this, new ArrayList<Business>());

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
                        "Search Expand");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        MenuItem searchViewItem = menu.findItem(R.id.search);
//        SearchView searchView = (SearchView) searchViewItem.getActionView();
//        String searchtext = searchView.getQuery().toString();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.switch_view) {
            Utility.replaceFragment(this, MapFragment.newInstance(), R.id.container);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startSearch(String search, String destination) {
        mSearchField.setText(search);
        Utility.replaceFragment(this, ResultsFragment.newInstance(), R.id.flContainer);

        search = cleanLocationString(search);
        destination = cleanLocationString(destination);
        String url = getRouteUrlFromCurrentLocation(destination);
        sendRouteRequest(url, search);
        if (destination.toUpperCase().equals(GlobalVars.HOME) ||
                destination.equals("")) destination = DEFAULT_DESTINATION;
    }

    /**
     *
     * Getter methods.
     *
     */
    public YelpClient getYelpClient() {
        return mYelpClient;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public BusinessArrayAdapter getBusinessArrayAdapter() {
        return aBusinesses;
    }

    /**
     *
     * Google Directions API Request Methods
     *
     */
    public String cleanLocationString(String location) {
        return location.trim().replace(" ", "+");
    }

    private Location getLastLocation() {
        return ((LocationManager) getSystemService(Context.LOCATION_SERVICE))
                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    private String getRouteUrlFromCurrentLocation(String destination) {
        Location origin = getLastLocation();
        if (origin == null) {
            Log.d("DEBUG", "origin is null."); // TODO: Fix this
            String startLocation = "260+Homer+Avenue,+Palo+Alto,+CA";
            return getRouteUrl(startLocation, destination);
        } else {
            return getRouteUrl(origin, destination);
        }
    }

    private String getRouteUrl(Location origin, String destination) {
        String originLatLong = String.valueOf(origin.getLatitude()) + ","
                + String.valueOf(origin.getLongitude());
        Log.d("DEBUG", "Origin lat/long: " + originLatLong);

        String url = DIRECTIONS_BASE_URL + "origin=" + originLatLong +
                "&destination=" + destination +
                "&sensor=True";
        return url;
    }

    private String getRouteUrl(String origin, String destination) {
        String url = DIRECTIONS_BASE_URL + "origin=" + origin +
                "&destination=" + destination +
                "&sensor=False";
        return url;
    }

    private void sendRouteRequest(String url, String search) {
        final String query = search;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.d("DEBUG", "onResponse hit.");
                        Log.d("DEBUG", "jsonObject:");
                        Log.d("DEBUG", jsonObject.toString());
                        mStepsArray = Step.fromJSONArray(jsonObject);
                        int totalDis = 0;
                        for (Step step : mStepsArray) {
                            totalDis += step.getDistance();
                            Log.d("DEBUG", "Step: " + step.toString());
                        }
                        filterSteps();
                        compileBusiness(query);
                        System.out.println("total distance: " + totalDis);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("DEBUG", "onErrorResponse hit. Error: " + volleyError.toString());
            }
        });
        mRequestQueue.add(request);
    }

    /**
     *
     * Filtering Google Direction Steps
     *
     */
    private void filterSteps() {
        int counter = 0; //counter for 5 miles.
        mFilteredStepsArray = new ArrayList<>();
        for (Step x: mStepsArray) {
            counter -= x.getDistance();
            if (counter <= 0) {
                mFilteredStepsArray.add(x);
                counter = 8064;
            }
        }
    }

    /**
     *
     * Accessing Yelp API
     *
     */
    private void compileBusiness(String search) {
        Log.d("DEBUG", "compileBusiness entered.");
        DistanceComparator comp = new DistanceComparator();
        mSortedBusinesses = new TreeSet<Business>(comp);
        for (Step x: mFilteredStepsArray) {
            new ReadYelpJSONFeedTask().execute(search, x.getEndLat(), x.getEndLong());
        }
    }

    private class ReadYelpJSONFeedTask extends AsyncTask<Object, Void, String> {
        protected String doInBackground(Object... param) {
            Log.d("DEBUG", "doInBackground entered.");

            String term = (String) param[0];
            Double lat = (Double) param[1];
            Double lng = (Double) param[2];

            String response = "";
            Log.d("DEBUG", "Calling yelpClient.getBusiness.");
            if (Utility.isConnectedToInternet(getApplicationContext())) {
                response = getYelpClient().getBusiness(term, lat, lng);
                Log.v("readJSONFeed  response ", response);
            }
            return response;
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        protected void onPostExecute(String result) {
            Log.v("Result ", String.valueOf(result));
            try {
                JSONObject o1 = new JSONObject(result);
                JSONArray businesses = o1.getJSONArray("businesses");
                ArrayList<Business> temp = Business.fromJSONArray(businesses);
                for (Business x: temp) {
                   boolean found = false;
                   for (Business y: mSortedBusinesses){
                       if (x.getLocationLine1().compareTo(y.getLocationLine1())== 0){
                           found = true;
                       }
                   }
                   if (x.getDistance() > 8064) {
                       found = true;
                   }
                   if (!found) {
                       aBusinesses.add(x);
                   }
                }
                System.out.println("Size of sorted business: " + mSortedBusinesses.size());
                for (Business z: mSortedBusinesses) {
                    System.out.println(z.getBusinessName() + " " + z.getDistance());
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.d("onPostExecute", e.getLocalizedMessage());
            }
        }
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
        address = cleanLocationString(address);
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
