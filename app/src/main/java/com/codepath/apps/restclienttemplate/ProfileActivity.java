package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";

    boolean onFollowers;

    User user;
    List<User> users;

    long curPageFollowing;
    long curPageFollowers;

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
        // layout of activity is stored in a special property called root
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


        scrollListener = new EndlessRecyclerViewScrollListener(llm) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page);
            }
        };
        // Adds the scroll listener to RecyclerView
        binding.rvAccounts.addOnScrollListener(scrollListener);

        client.getFollowers(user.getId(), curPageFollowers, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    JSONObject jsonObject = json.jsonObject;
                    JSONArray arrayFollowers = jsonObject.getJSONArray("users");
                    curPageFollowers = jsonObject.getLong("next_cursor");
                    followers = User.fromJsonArray(arrayFollowers);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure: getting followers", throwable);
            }
        });

        client.getFollowing(user.getId(), curPageFollowing, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    JSONObject jsonObject = json.jsonObject;
                    JSONArray arrayFollowing = jsonObject.getJSONArray("users");
                    curPageFollowing = jsonObject.getLong("next_cursor");
                    following = User.fromJsonArray(arrayFollowing);

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
            }
        });
    }

    public void loadNextDataFromApi(int offset) {

        if (onFollowers) {
            // Send an API request to retrieve appropriate paginated data
            client.getFollowers(user.getId(), curPageFollowers, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    //  --> Deserialize and construct new model objects from the API response
                    JSONObject jsonObject = json.jsonObject;
                    try {
                        JSONArray arrayFollowers = jsonObject.getJSONArray("users");
                        //  --> Append the new data objects to the existing set of items inside the array of items
                        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
                        adapter.addAll(User.fromJsonArray(arrayFollowers));

                    } catch (JSONException e) {
                        Log.e(TAG, "json exception");
                    }
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Log.e(TAG, "onFailure!", throwable);
                }
            });

        } else {
            // Send an API request to retrieve appropriate paginated data
            client.getFollowing(user.getId(), curPageFollowing, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    //  --> Deserialize and construct new model objects from the API response
                    JSONObject jsonObject = json.jsonObject;
                    try {
                        JSONArray arrayFollowing = jsonObject.getJSONArray("users");
                        //  --> Append the new data objects to the existing set of items inside the array of items
                        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
                        adapter.addAll(User.fromJsonArray(arrayFollowing));

                    } catch (JSONException e) {
                        Log.e(TAG, "json exception");
                    }
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Log.e(TAG, "onFailure!", throwable);
                }
            });
        }
    }

    public void onClickFollowing(android.view.View view) {
        binding.btnFollowing.setBackgroundColor(getResources().getColor(R.color.medium_gray_30));
        binding.btnFollowers.setBackgroundColor(getResources().getColor(R.color.white));

        onFollowers = false;

        adapter.clear();
        users.addAll(following);
        adapter.notifyDataSetChanged();
        binding.rvAccounts.smoothScrollToPosition(0);

    }

    public void onClickFollowers(android.view.View view) {
        binding.btnFollowers.setBackgroundColor(getResources().getColor(R.color.medium_gray_30));
        binding.btnFollowing.setBackgroundColor(getResources().getColor(R.color.white));

        onFollowers = true;

        adapter.clear();
        users.addAll(followers);
        adapter.notifyDataSetChanged();
        binding.rvAccounts.smoothScrollToPosition(0);
    }

    public void openTwitterWeb(android.view.View view) {
        String url = "https://twitter.com/" + user.getTwitterId() + "/followers";
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }
}
