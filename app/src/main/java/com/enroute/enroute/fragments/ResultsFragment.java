package com.enroute.enroute.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.enroute.enroute.activities.MainActivity;
import com.enroute.enroute.R;
import com.enroute.enroute.adapter.BusinessArrayAdapter;
import com.enroute.enroute.model.Business;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;


public class ResultsFragment extends Fragment {

    private MainActivity mParentActivity;

    private ArrayList<Business> businesses;
    private static BusinessArrayAdapter aBusinesseses;
    private ListView lvBusinesses;

    public static ResultsFragment newInstance() { return new ResultsFragment(); }

    public ResultsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mParentActivity = (MainActivity) getActivity();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_results, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeViews();

    }

    private void initializeViews() {
        // Get yelp restaurants and display in the list  view
        lvBusinesses = (ListView) mParentActivity.findViewById(R.id.lvBusinesses);

        FloatingActionButton fab = (FloatingActionButton) mParentActivity.findViewById(R.id.fab);
        fab.attachToListView(lvBusinesses);

        // Create the arrayList
        businesses = new ArrayList<>();

        // Construct the adapter
        aBusinesseses = mParentActivity.getBusinessArrayAdapter();

        // Connect listview to adapter
        lvBusinesses.setAdapter(aBusinesseses);

        // Set onClick listeners
        lvBusinesses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView address1 = (TextView) view.findViewById(R.id.tvAddress1);
                TextView address2 = (TextView) view.findViewById(R.id.tvAddress2);
                Log.d("DEBUG", address1.getText().toString());
                Log.d("DEBUG", address2.getText().toString());
                String fullAddress = address1.getText().toString() + " " + address2.getText().toString();
                Log.d("DEBUG", fullAddress);
                fullAddress = mParentActivity.cleanLocationString(fullAddress);
                Log.d("DEBUG", fullAddress);
                mParentActivity.startNavigation(fullAddress);
            }
        });
    }

}
