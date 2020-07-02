package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.databinding.ActivityProfileBinding;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";

    List<User> users;

    List<User> following;
    List<User> followers;

    TwitterClient client;
    ActivityProfileBinding binding;
    FollowAdapter adapter;
    LinearLayoutManager llm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        // layout of activity is stored in a special property called root
        View view = binding.getRoot();
        setContentView(view);

        client = TwitterApplication.getRestClient(this);
        users = new ArrayList<>();
        getSupportActionBar().setTitle("Profile");

        User user = Parcels.unwrap(getIntent().getParcelableExtra(User.class.getSimpleName()));

        binding.tvName.setText(user.getName());
        binding.tvHandle.setText(user.getTwitterId());
        Glide.with(this).load(user.getProfileImageUrl()).circleCrop().into(binding.ivProfilePic);

        adapter = new FollowAdapter(this, users);
        llm = new LinearLayoutManager(this);
        binding.rvAccounts.setLayoutManager(llm);
        binding.rvAccounts.setAdapter(adapter);

        client.getFollowers(user.getId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    JSONArray objectFollowers = json.jsonObject.getJSONArray("users");
                    followers = User.fromJsonArray(objectFollowers);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure: getting followers", throwable);
            }
        });

        client.getFollowing(user.getId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    JSONArray objectFollowers = json.jsonObject.getJSONArray("users");
                    following = User.fromJsonArray(objectFollowers);
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

    public void onClickFollowing(android.view.View view) {
        binding.btnFollowing.setBackgroundColor(getResources().getColor(R.color.medium_gray_30));
        binding.btnFollowers.setBackgroundColor(getResources().getColor(R.color.white));

        adapter.clear();
        users.addAll(following);
        adapter.notifyDataSetChanged();
        binding.rvAccounts.smoothScrollToPosition(0);

    }

    public void onClickFollowers(android.view.View view) {
        binding.btnFollowers.setBackgroundColor(getResources().getColor(R.color.medium_gray_30));
        binding.btnFollowing.setBackgroundColor(getResources().getColor(R.color.white));

        adapter.clear();
        users.addAll(followers);
        adapter.notifyDataSetChanged();
        binding.rvAccounts.smoothScrollToPosition(0);

    }
}
