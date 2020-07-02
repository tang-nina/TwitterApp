package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.codepath.apps.restclienttemplate.databinding.ActivityReplyBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ReplyActivity extends AppCompatActivity {
    private static final String TAG = "ReplyActivity";
    TwitterClient client;
    Tweet tweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityReplyBinding binding = ActivityReplyBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        getSupportActionBar().setTitle("Reply to Tweet");

        client = TwitterApplication.getRestClient(this);
        tweet = Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        binding.tilCounter.setHint("Replying to " + tweet.getUser().getTwitterId());

        //listener for reply button
        binding.btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String body = binding.etCompose.getText().toString();
                //api call to publish reply tweet
                client.reply(body, tweet.getId(), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Toast.makeText(ReplyActivity.this, "Your reply tweet was successful! ", Toast.LENGTH_SHORT).show();

                        Tweet tweet = null;
                        try {
                            tweet = Tweet.fromJson(json.jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //send back to main screen and add it to the top of the feed
                        Intent intent = new Intent();
                        intent.putExtra("tweet", Parcels.wrap(tweet));
                        TimelineActivity.getInstance().reload(intent, true);
                        finish();
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Toast.makeText(ReplyActivity.this, "Something went wrong. Please try again soon.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onFailure: reply", throwable);
                    }
                });
            }
        });
    }
}