package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.databinding.ActivityProfileBinding;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

//activity for displaying the profile of a twitter account
public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    User user;
    List<User> users;

    //cursors for fetching next page of handles from twitter api
    long curPageFollowing;
    long curPageFollowers;
    long secondPageFollowing;
    long secondPageFollowers;

    boolean onFollowers;

    //first page of following/followers
    List<User> following;
    List<User> followers;

    TwitterClient client;
    ActivityProfileBinding binding;
    FollowAdapter adapter;
    LinearLayoutManager llm;

    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        client = TwitterApplication.getRestClient(this);
        users = new ArrayList<>();
        curPageFollowing = -1;
        curPageFollowers = -1;

        getSupportActionBar().setTitle("Profile");

        user = Parcels.unwrap(getIntent().getParcelableExtra(User.class.getSimpleName()));

        binding.tvName.setText(user.getName());
        binding.tvHandle.setText(user.getTwitterId());
        Glide.with(this).load(user.getProfileImageUrl()).circleCrop().into(binding.ivProfilePic);

        adapter = new FollowAdapter(this, users);
        llm = new LinearLayoutManager(this);
        binding.rvAccounts.setLayoutManager(llm);
        binding.rvAccounts.setAdapter(adapter);

        //listener - fetch more data once user has scrolled through everything the adapter is
        //currently user
        scrollListener = new EndlessRecyclerViewScrollListener(llm) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page);
            }
        };
        binding.rvAccounts.addOnScrollListener(scrollListener);

        //getting first page of followers
        client.getFollowers(user.getId(), curPageFollowers, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    JSONObject jsonObject = json.jsonObject;
                    JSONArray arrayFollowers = jsonObject.getJSONArray("users");
                    curPageFollowers = jsonObject.getLong("next_cursor");
                    secondPageFollowers = curPageFollowers;
                    followers = User.fromJsonArray(arrayFollowers);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure: getting followers", throwable);
                Toast.makeText(ProfileActivity.this, "Could not get followers. ", Toast.LENGTH_SHORT).show();
            }
        });

        //getting first page of following
        client.getFollowing(user.getId(), curPageFollowing, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    JSONObject jsonObject = json.jsonObject;
                    JSONArray arrayFollowing = jsonObject.getJSONArray("users");
                    curPageFollowing = jsonObject.getLong("next_cursor");
                    secondPageFollowing = curPageFollowing;
                    following = User.fromJsonArray(arrayFollowing);

                    //displaying onto screen right away
                    binding.btnFollowing.setBackgroundColor(getResources().getColor(R.color.medium_gray_30));
                    users.addAll(following);
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure: getting following", throwable);
                Toast.makeText(ProfileActivity.this, "Could not get users this account is following. ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //load more data to recycler view by calling api
    public void loadNextDataFromApi(int offset) {

        if (onFollowers) { //if we need more followers
            // Send an API request to retrieve appropriate paginated data
            client.getFollowers(user.getId(), curPageFollowers, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    JSONObject jsonObject = json.jsonObject;
                    try {
                        JSONArray arrayFollowers = jsonObject.getJSONArray("users");
                        curPageFollowers = jsonObject.getLong("next_cursor");
                        //append new data to adapter and notify it
                        adapter.addAll(User.fromJsonArray(arrayFollowers));
                    } catch (JSONException e) {
                        Log.e(TAG, "json exception");
                    }
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Log.e(TAG, "onFailure!", throwable);
                    Toast.makeText(ProfileActivity.this, "Could not get followers. ", Toast.LENGTH_SHORT).show();
                }
            });

        } else { //if we need more following
            // Send an API request to retrieve appropriate paginated data
            client.getFollowing(user.getId(), curPageFollowing, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    JSONObject jsonObject = json.jsonObject;
                    try {
                        JSONArray arrayFollowing = jsonObject.getJSONArray("users");
                        curPageFollowing = jsonObject.getLong("next_cursor");
                        //append new data to adapter and notify it
                        adapter.addAll(User.fromJsonArray(arrayFollowing));
                    } catch (JSONException e) {
                        Log.e(TAG, "json exception");
                    }
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Log.e(TAG, "onFailure!", throwable);
                    Toast.makeText(ProfileActivity.this, "Could not get users this count is following. ", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    //display following starting from the top
    public void onClickFollowing(android.view.View view) {
        //change following button to gray
        binding.btnFollowing.setBackgroundColor(getResources().getColor(R.color.medium_gray_30));
        binding.btnFollowers.setBackgroundColor(getResources().getColor(R.color.white));

        onFollowers = false;

        adapter.clear();
        users.addAll(following);
        curPageFollowing = secondPageFollowing;
        adapter.notifyDataSetChanged();
        binding.rvAccounts.smoothScrollToPosition(0);
    }

    //display followers starting from the top
    public void onClickFollowers(android.view.View view) {
        //change followers button to gray
        binding.btnFollowers.setBackgroundColor(getResources().getColor(R.color.medium_gray_30));
        binding.btnFollowing.setBackgroundColor(getResources().getColor(R.color.white));

        onFollowers = true;

        adapter.clear();
        users.addAll(followers);
        curPageFollowers = secondPageFollowers;
        adapter.notifyDataSetChanged();
        binding.rvAccounts.smoothScrollToPosition(0);
    }
}
