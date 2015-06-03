package com.enroute.enroute;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.enroute.enroute.adapter.BusinessArrayAdapter;
import com.enroute.enroute.model.Businesses;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class YelpTest extends ActionBarActivity {

    private static YelpClient yelpClient = null;
    public static Context context;
    private ArrayList<Businesses> businesseses;
    private static BusinessArrayAdapter aBusinesseses;
    private ListView lvBusinesses;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get yelp restaurants and display in the list  view
        lvBusinesses  = (ListView) findViewById(R.id.lvBusinesses);

        // Create the arrayList
        businesseses = new ArrayList<>();

        // Construct the adapter
        aBusinesseses = new BusinessArrayAdapter(this, businesseses);

        // Connect listview to adapter
        lvBusinesses.setAdapter(aBusinesseses);


        yelpClient = new YelpClient(this);
        populateBusinesses("restaurants", 37.871899, -122.25854);
    }

    private void populateBusinesses(String term, Double lat, Double lng){
        new ReadYelpJSONFeedTask().execute(term,lat,lng);

    }

    private class ReadYelpJSONFeedTask extends AsyncTask<Object, Void, String> {
        protected String doInBackground(Object... param) {

            String term = (String)param[0];
            Double lat = (Double) param[1];
            Double lng = (Double) param[2];

            String response=null;
            response = yelpClient.getBusiness(term,lat,lng);
            Log.v("readJSONFeed  response ", response);
            return response;
            //return readJSONFeed(urls[0]);
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        protected void onPostExecute(String result) {

            Log.v("Result ", result);
            try {

                JSONObject o1 = new JSONObject(result);
                JSONArray businesses = o1.getJSONArray("businesses");

                aBusinesseses.addAll(Businesses.fromJSONArray(businesses));

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.d("onPostExecute", e.getLocalizedMessage());

            }
            // try
        } // post
    } //read

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_yelp_test, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }
}
