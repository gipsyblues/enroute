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
    TextView tvBusinessName;
    TextView tvPhone;
    TextView tvCategories;
    ImageView ivRating;
    TextView tvAddress1;
    TextView tvAddress2;
    ImageView ivLogo;
    TextView tvDistance;


    public BusinessArrayAdapter(Context context, List<Business> tweets) {
        super(context, 0, tweets);
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
        tvBusinessName = (TextView) convertView.findViewById(R.id.tvBusinessName);
        //TextView  tvMobile = (TextView) convertView.findViewById(R.id.tvMobile);
        tvPhone = (TextView) convertView.findViewById(R.id.tvPhone);
        ivRating = (ImageView) convertView.findViewById(R.id.ivRating);
        tvAddress1 = (TextView) convertView.findViewById(R.id.tvAddress1);
        tvAddress2 = (TextView) convertView.findViewById(R.id.tvAddress2);
        ivLogo = (ImageView) convertView.findViewById(R.id.ivLogo);
        tvDistance = (TextView) convertView.findViewById(R.id.tvDistance);

        tvAddress1.setText(business.getLocation1());
        tvAddress2.setText(business.getLocation2());

        double dist = business.getDistance();
        dist = dist / 1609.34;
        BigDecimal bd = new BigDecimal(dist);
        bd = bd.round(new MathContext(2));
        double rounded = bd.doubleValue();
        tvDistance.setText(Double.toString(rounded) + "mi");

         //populate data into subviews
        tvBusinessName.setText(business.getBusinessName());

        tvPhone.setText(business.getPhone());

         //clear out old image
//        ivProfileImage.setImageResource(android.R.color.transparent);
//        Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl()).into(ivProfileImage);
        Double rating = business.getStars();
        if (rating == 0.5) {
            ivRating.setImageResource(R.drawable.star0_5);
        } else if (rating == 1) {
            ivRating.setImageResource(R.drawable.star1);
        } else if (rating == 1.5) {
            ivRating.setImageResource(R.drawable.star1_5);
        } else if (rating == 2) {
            ivRating.setImageResource(R.drawable.star2);
        } else if (rating == 2.5) {
            ivRating.setImageResource(R.drawable.star2_5);
        } else if (rating == 3) {
            ivRating.setImageResource(R.drawable.star3);
        } else if (rating == 3.5) {
            ivRating.setImageResource(R.drawable.star3_5);
        } else if (rating == 4) {
            ivRating.setImageResource(R.drawable.star4);
        } else if (rating == 4.5) {
            ivRating.setImageResource(R.drawable.star4_5);
        } else if (rating == 5) {
            ivRating.setImageResource(R.drawable.star5);
        } else {
            Log.d("ERROR", "rating value incorrect");
        }
        Picasso.with(getContext()).load(business.getImageUrl()).into(ivLogo);
        return convertView;
    }

}
