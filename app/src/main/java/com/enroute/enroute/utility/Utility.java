package com.enroute.enroute.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Ricky on 5/9/2015.
 */
public class Utility {

    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    public static void setHideKeyboardListener(final FragmentActivity activity, final View layout) {
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideKeyboard(activity, layout);
                return false;
            }
        });
    }

    public static void hideKeyboard(FragmentActivity activity, View layout) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(FragmentActivity.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isAcceptingText()) {
            View viewFocus = activity.getCurrentFocus();
            if (viewFocus != null) { inputMethodManager.hideSoftInputFromWindow(layout.getWindowToken(), 0); }
        }
    }

    public static void replaceFragment(FragmentActivity activity, Fragment fragment, int id) {
        replaceFragment(activity, fragment, id, null);
    }

    public static void replaceFragment(FragmentActivity activity, Fragment fragment, int id, String tag) {
        commit(activity.getSupportFragmentManager().beginTransaction().replace(id, fragment, tag));
    }

    public static void backStackFragment(FragmentActivity activity, Fragment fragment, int id, String tag) {
        commit(activity.getSupportFragmentManager().beginTransaction().replace(id, fragment, tag).addToBackStack(tag));
    }

    public static void commit(android.support.v4.app.FragmentTransaction transaction) { transaction.commitAllowingStateLoss(); }
}
