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
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;

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
import com.enroute.enroute.model.Business;
import com.enroute.enroute.model.Step;
import com.enroute.enroute.utility.DistanceComparator;
import com.enroute.enroute.utility.GlobalVars;
import com.enroute.enroute.utility.Utility;
import com.enroute.enroute.utility.VolleyInstance;
import com.melnykov.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

public class MainActivity extends ActionBarActivity {

    private final String DIRECTIONS_API_KEY = "AIzaSyBOfq7knvV8qWFG2eztBeL7NKCnNYmB6mU";
    private final String DIRECTIONS_BASE_URL = "https://maps.googleapis.com/maps/api/directions/json?key=" + DIRECTIONS_API_KEY + "&";
    private final int POI_NAVIGATION_REQUEST = 1;
    private final String DEFAULT_DESTINATION = "2515 Benvenue Avenue, Berkeley, CA";

    private String mStartLocation;
    private String mMiddleLocation;
    private String mDestinationLocation;

    private VolleyInstance mRequest;
    private RequestQueue mRequestQueue;
    private static YelpClient mYelpClient;

    private ArrayList<Step> mStepsArray;
    private ArrayList<Step> mFilteredStepsArray;
    private TreeSet<Business> mSortedBusinesses;
    private static BusinessArrayAdapter aBusinesseses;
    private ListView lvBusinesses;

    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        ActionBar bar = getActionBar();
//        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#9FA8DA")));


        Intent intent = getIntent();
        mStartLocation = intent.getStringExtra(GlobalVars.START_LOC);
        mMiddleLocation = intent.getStringExtra(GlobalVars.MIDDLE_LOC);
        mDestinationLocation = intent.getStringExtra(GlobalVars.DEST);
        if (mStartLocation.equals("")) mStartLocation = GlobalVars.GET_CURRENT_LOCATION;
        if (mDestinationLocation.toUpperCase().equals(GlobalVars.HOME) ||
                mDestinationLocation.equals("")) mDestinationLocation = DEFAULT_DESTINATION;
        cleanLocationStrings();

        mRequest = VolleyInstance.getInstance(getApplicationContext());
        mRequestQueue = mRequest.getRequestQueue();
        mYelpClient = new YelpClient(this);

        Utility.replaceFragment(this, ResultsFragment.newInstance(), R.id.container);

        if (true || mStartLocation.equals(GlobalVars.GET_CURRENT_LOCATION)) {
            getRouteFromCurrentLocation(mDestinationLocation);
        } else {
            getRoute(mStartLocation, mDestinationLocation);
        }

        // Construct the adapter
        aBusinesseses = new BusinessArrayAdapter(this, new ArrayList<Business>());
    }

    boolean star = true;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.fab);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (star) {
                    FloatingActionButton btn = (FloatingActionButton)findViewById(R.id.fab);
                    btn.setImageResource(R.drawable.ruler2);
                    star = false;
                } else {
                    FloatingActionButton btn = (FloatingActionButton)findViewById(R.id.fab);
                    btn.setImageResource(R.drawable.star);
                    star = true;
                }
            }
        });


        MenuItem searchViewItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchViewItem.getActionView();
        String searchtext = searchView.getQuery().toString();

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


    /**
     *
     * Google Directions API Request Methods
     *
     */
    public String cleanLocationString(String location) {
        return location.trim().replace(" ", "+");
    }
    private void cleanLocationStrings() {
        mStartLocation = mStartLocation.trim().replace(" ", "+");
        mMiddleLocation = mMiddleLocation.trim().replace(" ", "+");
        mDestinationLocation = mDestinationLocation.trim().replace(" ", "+");
    }

    private Location getLastLocation() {
        return ((LocationManager) getSystemService(Context.LOCATION_SERVICE)).getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    private void getRouteFromCurrentLocation(String destination) {
        Location origin = getLastLocation();
        if (origin == null) {
            Log.d("DEBUG", "origin is null."); // TODO: Fix this
            mStartLocation = "260+Homer+Avenue,+Palo+Alto,+CA";
            getRoute(mStartLocation, destination);
        } else {
            getRoute(origin, destination);
        }
    }

    private void getRoute(Location origin, String destination) {
        // String destinationPlaceholder = "2515+Benvenue+Avenue,+Berkeley";
        String originLatLong = String.valueOf(origin.getLatitude()) + "," + String.valueOf(origin.getLongitude());
        Log.d("DEBUG", "Origin lat/long: " + originLatLong);

        String url = DIRECTIONS_BASE_URL + "origin=" + originLatLong +
                "&destination=" + destination +
                "&sensor=True";
        sendRouteRequest(url);
    }

    private void getRoute(String origin, String destination) {
        String url = DIRECTIONS_BASE_URL + "origin=" + origin +
                "&destination=" + destination +
                "&sensor=False";
        sendRouteRequest(url);
    }

    private void sendRouteRequest(String url) {
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
                        compileBusiness();
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
     * Filtering Google Direction Steps and accessing Yelp API
     */

    private void filterSteps() {
        int counter = 0; //counter for 5 miles.
        mFilteredStepsArray = new ArrayList<Step>();
        for (Step x: mStepsArray) {
            counter -= x.getDistance();
            if (counter <= 0) {
                mFilteredStepsArray.add(x);
                counter = 8064;
            }
        }
    }

    private void compileBusiness() {
        Log.d("DEBUG", "compileBusiness entered.");
        DistanceComparator comp = new DistanceComparator();
        mSortedBusinesses = new TreeSet<Business>(comp);
        for (Step x: mFilteredStepsArray) {
            new ReadYelpJSONFeedTask().execute(mMiddleLocation, x.getEndLat(), x.getEndLong());
            //String response = mYelpClient.getBusiness(mMiddleLocation, x.getEndLat(), x.getEndLong());
        }
//        ArrayList<Business> sortedArray = new ArrayList<Business>();
//        sortedArray.addAll(mSortedBusinesses);
//        for (Business x: sortedArray) {
//            System.out.println("Inside SortedBusiness" + x.getBusinessName() + " dis: " + x.getDistance());
//        }
//        aBusinesseses.addAll(sortedArray);
        //System.out.println("Size of SortedBusiness: " + mSortedBusinesses.size());
        //for (Business x: mSortedBusinesses) {
        //   System.out.println("Inside SortedBusiness" + x.getBusinessName());
        //}

    }

    public BusinessArrayAdapter getBusinessArrayAdapter() {
        return aBusinesseses;
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
            //return readJSONFeed(urls[0]);
        }
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        protected void onPostExecute(String result) {
            Log.v("Result ", String.valueOf(result));
            HashSet<String> already = new HashSet<String>();
            try {
                JSONObject o1 = new JSONObject(result);
                JSONArray businesses = o1.getJSONArray("businesses");
                ArrayList<Business> temp = Business.fromJSONArray(businesses);
                for (Business x: temp) {
                   boolean found = false;
                   for(Business y: mSortedBusinesses){
                       if (x.getLocation1().compareTo(y.getLocation1())== 0){
                           found = true;
                       }
                   }
                   if (x.getDistance() > 8064) {
                       found = true;
                   }
                   if (!found) {
                       aBusinesseses.add(x);
                   }
                }
                //mSortedBusinesses.addAll(temp);
                System.out.println("Size of sortedbusiness: " + mSortedBusinesses.size());
                for (Business z: mSortedBusinesses) {
                    System.out.println(z.getBusinessName() + " " + z.getDistance());
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.d("onPostExecute", e.getLocalizedMessage());
            }
//            ArrayList<Business> sortedArray = new ArrayList<Business>();
//            sortedArray.addAll(mSortedBusinesses);
//            for (Business x: sortedArray) {
//                System.out.println("Inside SortedBusiness" + x.getBusinessName() + " dis: " + x.getDistance());
//            }
//            aBusinesseses.addAll(sortedArray);

            // try
        } // post
    } //read

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
        // startActivity(gmapsIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Finished gmaps activity. Reroute to destination!
        // Navigate to 2515
        //startNavigation(37.864984, -122.254763);
        Log.d("DEBUG", "requestCode: " + requestCode + " resultCode: " + resultCode);
        Log.d("DEBUG", "Why is it hitting this case already.");
    }

}
