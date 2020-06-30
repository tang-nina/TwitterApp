package com.codepath.apps.restclienttemplate.models;
// models/User.java

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class User {

    public String name;
    public String twitterId;
    public String profileImageUrl;

    public User(){}

    public static User fromJson(JSONObject tweetJson) throws JSONException {
        User user = new User();

        user.twitterId = "@" + tweetJson.getString("screen_name");
        user.name = tweetJson.getString("name");
        user.profileImageUrl = tweetJson.getString("profile_image_url_https");

        return user;
    }

    public String getName() {
        return name;
    }

    public String getTwitterId() {
        return twitterId;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}