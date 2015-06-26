package com.enroute.enroute.fragments;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.enroute.enroute.R;
import com.enroute.enroute.activities.MainActivity;
import com.enroute.enroute.utility.Utility;

/**
 * Created by Amy on 6/21/15.
 */
public class SearchFragment extends Fragment {
    private MainActivity mParentActivity;

    private View mBackground;
    private EditText mSearchField;
    private EditText mDestinationField;
    private ImageButton mImageButton;

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

        mSearchField = (EditText) rootView.findViewById(R.id.search_field);
        mDestinationField = (EditText) rootView.findViewById(R.id.destination_field);
        Utility.showKeyboard(mParentActivity, mSearchField);

        mImageButton = (ImageButton) rootView.findViewById(R.id.submit_button);
        mImageButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mImageButton.setAlpha(0.7f);
                        submit();
                        return true;
                    case MotionEvent.ACTION_UP:
                        mImageButton.setAlpha(1f);
                        Utility.hideKeyboard(mParentActivity, mImageButton);
                        back();
                        return true;
                }
                return false;
            }
        });

        mBackground = (View) rootView.findViewById(R.id.background);
        mBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.hideKeyboard(mParentActivity, mImageButton);
                back();
            }
        });
        return rootView;
    }

    public void submit() {
        String search = mSearchField.getText().toString();
        String destination = mDestinationField.getText().toString();
        mParentActivity.startSearch(search, destination);
    }

    public void back() {
        mParentActivity.getSupportFragmentManager().popBackStack();
        mParentActivity.getSupportActionBar().show();
    }
}
