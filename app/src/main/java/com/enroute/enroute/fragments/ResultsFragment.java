package com.enroute.enroute.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.enroute.enroute.activities.MainActivity;
import com.enroute.enroute.R;
import com.enroute.enroute.adapter.BusinessArrayAdapter;


public class ResultsFragment extends Fragment {

    private MainActivity mParentActivity;

    private static BusinessArrayAdapter mBusinessAdapter;
    private ListView mBusinessesListView;

    public static ResultsFragment newInstance() {
        return new ResultsFragment();
    }

    public ResultsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
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
        mBusinessesListView = (ListView) mParentActivity.findViewById(R.id.lvBusinesses);

        // Construct the adapter
        mBusinessAdapter = mParentActivity.getBusinessArrayAdapter();

        // Connect listview to adapter
        mBusinessesListView.setAdapter(mBusinessAdapter);

        // Set onClick listeners
        mBusinessesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView address1 = (TextView) view.findViewById(R.id.business_address1);
                TextView address2 = (TextView) view.findViewById(R.id.business_address2);
                String fullAddress = address1
                        .getText().toString() + " " + address2.getText().toString();
                fullAddress = mParentActivity.cleanString(fullAddress);
                mParentActivity.startNavigation(fullAddress);
            }
        });
    }
}
