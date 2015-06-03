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
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

public class BusinessArrayAdapter extends ArrayAdapter<Business> {
// Taking the business object and turning them into views
// that will be displayed in lists
    TextView mBusinessNameText;
    TextView mPhoneText;
    TextView mCategoriesText;
    ImageView mRatingImage;
    TextView mAddress1Text;
    TextView mAddress2Text;
    ImageView mLogo;
    TextView mDistanceText;


    public BusinessArrayAdapter(Context context, List<Business> businesses) {
        super(context, 0, businesses);
    }

    // override and setup custom template
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the business
        Business business = getItem(position);
        //business = getItem(position);
        // Find or inflate the template
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_restaurant, parent, false);

        }

        // find the subview to fill with data in template
        mBusinessNameText = (TextView) convertView.findViewById(R.id.business_name);
        //TextView  mMobileText = (TextView) convertView.findViewById(R.id.business_mobile);
        mPhoneText = (TextView) convertView.findViewById(R.id.business_phone);
        mRatingImage = (ImageView) convertView.findViewById(R.id.business_rating);
        mAddress1Text = (TextView) convertView.findViewById(R.id.business_address1);
        mAddress2Text = (TextView) convertView.findViewById(R.id.business_address2);
        mLogo = (ImageView) convertView.findViewById(R.id.business_logo);
        mDistanceText = (TextView) convertView.findViewById(R.id.business_distance);

        mAddress1Text.setText(business.getLocation1());
        mAddress2Text.setText(business.getLocation2());

        double dist = business.getDistance();
        dist = dist / 1609.34;
        BigDecimal bd = new BigDecimal(dist);
        bd = bd.round(new MathContext(2));
        double rounded = bd.doubleValue();
        mDistanceText.setText(Double.toString(rounded) + "mi");

         //populate data into subviews
        mBusinessNameText.setText(business.getBusinessName());

        mPhoneText.setText(business.getPhone());

         //clear out old image
//        ivProfileImage.setImageResource(android.R.color.transparent);
//        Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl()).into(ivProfileImage);
        Double rating = business.getStars();
        if (rating == 0.5) {
            mRatingImage.setImageResource(R.drawable.star0_5);
        } else if (rating == 1) {
            mRatingImage.setImageResource(R.drawable.star1);
        } else if (rating == 1.5) {
            mRatingImage.setImageResource(R.drawable.star1_5);
        } else if (rating == 2) {
            mRatingImage.setImageResource(R.drawable.star2);
        } else if (rating == 2.5) {
            mRatingImage.setImageResource(R.drawable.star2_5);
        } else if (rating == 3) {
            mRatingImage.setImageResource(R.drawable.star3);
        } else if (rating == 3.5) {
            mRatingImage.setImageResource(R.drawable.star3_5);
        } else if (rating == 4) {
            mRatingImage.setImageResource(R.drawable.star4);
        } else if (rating == 4.5) {
            mRatingImage.setImageResource(R.drawable.star4_5);
        } else if (rating == 5) {
            mRatingImage.setImageResource(R.drawable.star5);
        } else {
            Log.d("ERROR", "rating value incorrect");
        }
        Picasso.with(getContext()).load(business.getImageUrl()).into(mLogo);
        return convertView;
    }

}
