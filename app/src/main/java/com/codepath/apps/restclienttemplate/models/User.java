package com.codepath.apps.restclienttemplate.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class User {

    public String name;
    public String twitterId;
    public String profileImageUrl;
    public long id;

    public User() {
    }

    //turns a User in the form of a json into a User object
    public static User fromJson(JSONObject userJson) throws JSONException {
        User user = new User();

        user.twitterId = "@" + userJson.getString("screen_name");
        user.name = userJson.getString("name");
        user.profileImageUrl = userJson.getString("profile_image_url_https");
        user.id = userJson.getLong("id");

        return user;
    }

    //turns a list of Users in the form of a json array into a list of User objects
    public static List<User> fromJsonArray(JSONArray userJsons) throws JSONException {
        ArrayList<User> users = new ArrayList<User>(userJsons.length());

        for (int i = 0; i < userJsons.length(); i++) {
            User user = fromJson(userJsons.getJSONObject(i));
            users.add(user);
        }
        return users;
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

    public long getId() {
        return id;
    }
}