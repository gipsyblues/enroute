package com.enroute.enroute.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.enroute.enroute.R;
import com.enroute.enroute.model.Business;
import com.enroute.enroute.utility.GlobalVars;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

public class BusinessArrayAdapter extends ArrayAdapter<Business> {

    TextView mNameTextView;
    TextView mPhoneTextView;
    ImageView mRatingImageView;
    TextView mAddress1TextView;
    TextView mAddress2TextView;
    ImageView mIconImageView;
    TextView mDistanceTextView;


    public BusinessArrayAdapter(Context context, List<Business> businesses) {
        super(context, 0, businesses);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Business business = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.business_list_item, parent, false);
        }

        mNameTextView = (TextView) convertView.findViewById(R.id.business_name);
        mPhoneTextView = (TextView) convertView.findViewById(R.id.business_phone);
        mRatingImageView = (ImageView) convertView.findViewById(R.id.business_rating);
        mAddress1TextView = (TextView) convertView.findViewById(R.id.business_address1);
        mAddress2TextView = (TextView) convertView.findViewById(R.id.business_address2);
        mIconImageView = (ImageView) convertView.findViewById(R.id.business_icon);
        mDistanceTextView = (TextView) convertView.findViewById(R.id.business_distance);

        mAddress1TextView.setText(business.getLocationLine1());
        mAddress2TextView.setText(business.getLocationLine2());
        mNameTextView.setText(business.getName());
        mPhoneTextView.setText(business.getPhone());
        double distance = business.getDistance() / GlobalVars.METERS_PER_MILE;
        double rounded = Math.round(distance * 100.0) / 100.0;
        mDistanceTextView.setText(Double.toString(rounded) + "mi");
        Picasso.with(getContext()).load(business.getImageUrl()).into(mIconImageView);

        double rating = business.getRating();
        int ratingDrawableId = R.drawable.star2_5;
        if (rating == 0.5) {
            ratingDrawableId = R.drawable.star0_5;
        } else if (rating == 1) {
            ratingDrawableId = R.drawable.star1;
        } else if (rating == 1.5) {
            ratingDrawableId = R.drawable.star1_5;
        } else if (rating == 2) {
            ratingDrawableId = R.drawable.star2;
        } else if (rating == 2.5) {
            ratingDrawableId = R.drawable.star2_5;
        } else if (rating == 3) {
            ratingDrawableId = R.drawable.star3;
        } else if (rating == 3.5) {
            ratingDrawableId = R.drawable.star3_5;
        } else if (rating == 4) {
            ratingDrawableId = R.drawable.star4;
        } else if (rating == 4.5) {
            ratingDrawableId = R.drawable.star4_5;
        } else if (rating == 5) {
            ratingDrawableId = R.drawable.star5;
        } else {
            Log.d("ERROR", "rating value incorrect");
        }
        mRatingImageView.setImageResource(ratingDrawableId);
        return convertView;
    }
}
