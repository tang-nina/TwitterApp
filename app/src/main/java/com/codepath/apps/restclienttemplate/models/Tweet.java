// models/Tweet.java
package com.codepath.apps.restclienttemplate.models;

import android.text.format.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Parcel
public class Tweet {
    private static final String TAG = "Tweet";

    public String body;
    public String createdAt;
    public User user;
    public String relativeTime;
    public String mediaUrl;

    public Tweet(){}

    public static Tweet fromJson(JSONObject json) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = json.getString("text");
        tweet.createdAt = json.getString("created_at");
        tweet.user = User.fromJson(json.getJSONObject("user"));
        tweet.relativeTime = Tweet.getRelativeTimeAgo(tweet.createdAt);

        JSONObject entities = json.getJSONObject("entities");
        JSONArray media = null;
        try {
           media = entities.getJSONArray("media");
           JSONObject first_pic = media.getJSONObject(0);
           tweet.mediaUrl = first_pic.getString("media_url_https");
        }catch(JSONException e){
            System.out.println(tweet.body);
            System.out.println(entities);
            System.out.println();
            tweet.mediaUrl = "";
        }


        return tweet;
    }

    public static List<Tweet> fromJson(JSONArray jsonArray) throws JSONException {
        ArrayList<Tweet> tweets = new ArrayList<Tweet>(jsonArray.length());

        for (int i=0; i < jsonArray.length(); i++) {
            Tweet tweet = null;
            tweet = fromJson(jsonArray.getJSONObject(i));
            tweets.add(tweet);
        }

        return tweets;
    }

    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return relativeDate;
    }

    public String getBody() {
        return body;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    public String getRelativeTime() {
        return relativeTime;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }
}