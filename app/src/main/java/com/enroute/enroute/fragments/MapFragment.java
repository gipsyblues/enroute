package com.enroute.enroute.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.enroute.enroute.activities.MainActivity;
import com.enroute.enroute.R;


public class MapFragment extends Fragment {

    private MainActivity mParentActivity;

    public static MapFragment newInstance() { return new MapFragment(); }

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mParentActivity = (MainActivity) getActivity();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }


}
