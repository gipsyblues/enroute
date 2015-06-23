package com.enroute.enroute.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.enroute.enroute.R;
import com.enroute.enroute.activities.MainActivity;

/**
 * Created by Amy on 6/21/15.
 */
public class SearchFragment extends Fragment {
    private MainActivity mParentActivity;

    private View mBackground;

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mParentActivity = (MainActivity) getActivity();
        View rootView = inflater.inflate(R.layout.search_expand_layout, container, false);

        mBackground = (View) rootView.findViewById(R.id.background);
        mBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
        return rootView;
    }

    public void back() {
        mParentActivity.getSupportFragmentManager().popBackStack();
        mParentActivity.getSupportActionBar().show();
    }
}
