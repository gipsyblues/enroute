package com.enroute.enroute.utility;

import com.enroute.enroute.model.Business;

import java.util.Comparator;

/**
 * Created by Patrick on 5/9/2015.
 */
public class DistanceComparator implements Comparator<Business> {
    public int compare(Business b1, Business b2) {
        if (b1.getDistance() < b2.getDistance()) {
            return -1;
        } else if (b1.getDistance() == b2.getDistance()) {
            return 0;
        } else {
            return 1;
        }
    }
}
