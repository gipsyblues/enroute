package com.enroute.enroute;

import android.content.Context;
import android.util.Log;

import com.codepath.oauth.OAuthBaseClient;
import com.enroute.enroute.utility.GlobalVars;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.Api;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

public class YelpClient extends OAuthBaseClient {

    OAuthService service;
    Token accessToken;

    public static final Class<? extends Api> REST_API_CLASS = YelpApi.class;

    public static final String REST_URL = "api.yelp.com"; // Change this, base API URL
    public static final String REST_CALLBACK_URL = "oauth://attenroute"; // Change this (here and in manifest)

    private static final String REST_CONSUMER_KEY = "qdtTK1b9jOG2_7txscXMDA";
    private static final String REST_CONSUMER_SECRET = "sr1yOmMRM-IpKKXPYah_QzjrlVw";
    private static final String REST_TOKEN = "E0C_rSfFi-_F0JvuqfDNArDO0P5oG6Ik";
    private static final String REST_TOKEN_SECRET = "ShksC-ByzhQyN142JicbDXiVhs4";

    private static final String YELP_API_URL = "http://api.yelp.com/v2/search";

    public YelpClient(Context context) {
        super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY,
                REST_CONSUMER_SECRET, REST_CALLBACK_URL);
        this.service = new ServiceBuilder().provider(YelpApi.class).apiKey(REST_CONSUMER_KEY)
                .apiSecret(REST_CONSUMER_SECRET).build();
        this.accessToken = new Token(REST_TOKEN, REST_TOKEN_SECRET);
    }

    /* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */

    public String getBusiness(String term, double latitude, double longitude) {
        try {
            OAuthRequest request = new OAuthRequest(Verb.GET, YELP_API_URL);
            request.addQuerystringParameter(GlobalVars.YELP_SEARCH_TERM, term);
            request.addQuerystringParameter(GlobalVars.YELP_SEARCH_LIMIT, "6");
            request.addQuerystringParameter(GlobalVars.YELP_SEARCH_LONG_LAT,
                    latitude + "," + longitude);
            request.addQuerystringParameter(GlobalVars.YELP_SEARCH_SORT,"1");
            request.addQuerystringParameter(GlobalVars.YELP_SEARCH_RADIUS,"8064");
            this.service.signRequest(this.accessToken, request);
            Response response = request.send();
            String s = response.toString();
            return response.getBody();
        } catch (Exception e) {
            Log.d("ERROR", e.toString());
        }
        return "";
    }
}
