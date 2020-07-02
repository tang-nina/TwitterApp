package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {
    private static final String TAG = "TimelineActivity";
    private final int REQUEST_CODE = 20;

    private static TimelineActivity instance;

    TwitterClient client;
    List<Tweet> tweets;
    TweetsAdapter adapter;
    LinearLayoutManager layoutManager;
    ActivityTimelineBinding binding;
    MenuItem miActionProgressItem;

    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTimelineBinding.inflate(getLayoutInflater());
        // layout of activity is stored in a special property called root
        View view = binding.getRoot();
        setContentView(view);

        instance = this;
        client = TwitterApplication.getRestClient(this);

        binding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showProgressBar();
                Log.i(TAG, "onRefresh: here");
                adapter.clear();
                populateHomeTimeline();
                binding.swipeContainer.setRefreshing(false);
                hideProgressBar();
            }
        });
        // Configure the refreshing colors
        binding.swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        tweets = new ArrayList<Tweet>();

        adapter = new TweetsAdapter(this, tweets);
        binding.rvTweets.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(this);
        binding.rvTweets.setLayoutManager(layoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page);
            }
        };
        // Adds the scroll listener to RecyclerView
        binding.rvTweets.addOnScrollListener(scrollListener);
        populateHomeTimeline();
    }

    public static TimelineActivity getInstance() {
        return instance;
    }

    public void loadNextDataFromApi(int offset) {
        // Send an API request to retrieve appropriate paginated data
        showProgressBar();
        client.getNextPage(tweets.get(tweets.size() - 1).getId(), new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                //  --> Deserialize and construct new model objects from the API response
                JSONArray array = json.jsonArray;

                try {
                    //  --> Append the new data objects to the existing set of items inside the array of items
                    //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
                    adapter.addAll(Tweet.fromJson(array));
                    hideProgressBar();

                }catch(JSONException e){
                    Log.e(TAG, "json exception");
                    hideProgressBar();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure!", throwable);
                hideProgressBar();
            }
        });
    }

    private void populateHomeTimeline(){
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONArray array = json.jsonArray;
                try{
                    tweets.addAll(Tweet.fromJson(array));
                    adapter.notifyDataSetChanged();
                    hideProgressBar();
                }catch(JSONException e){
                    Log.e(TAG, "json exception");
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure!", throwable);
            }
        });
    }

    //for menus
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true; //must be true for menu to be displayed
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        miActionProgressItem.setVisible(true);
        return super.onPrepareOptionsMenu(menu); //finish
    }

    public void showProgressBar() {
        // Show progress item
        miActionProgressItem.setVisible(true);
    }

    public void hideProgressBar() {
        // Hide progress item
        miActionProgressItem.setVisible(false);
    }

    //passes the item on the actionbar that is clicked into this method, you can check it and carry
    //out actions accordingly
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.compose: //compose button tapped
                Intent intent = new Intent(this, ComposeActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void reload(@Nullable Intent data, boolean flag) {
        Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
        tweets.add(0, tweet);
        adapter.notifyItemInserted(0);
        if (flag) {
            binding.rvTweets.smoothScrollToPosition(0); //will set your screen to the very top of the list
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //System.out.println("here activity result " + requestCode);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            reload(data, true);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}


