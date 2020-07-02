package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.codepath.apps.restclienttemplate.databinding.ActivityComposeBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    private static final String TAG = "ComposeActivity";
    public static final int MAX_TWEET_LENGTH = 280;

    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //create the view
        super.onCreate(savedInstanceState);
        final ActivityComposeBinding binding = ActivityComposeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        client = TwitterApplication.getRestClient(this);

        getSupportActionBar().setTitle("Compose Tweet");

        binding.btnTweet.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String text = binding.etCompose.getText().toString();

                //checking if tweet is valid
                if (text.isEmpty()) {
                    Toast.makeText(ComposeActivity.this, "Your tweet cannot be empty. ", Toast.LENGTH_SHORT).show();
                    return;
                } else if (text.length() > MAX_TWEET_LENGTH) {
                    Toast.makeText(ComposeActivity.this, "Your tweet is too long. ", Toast.LENGTH_SHORT).show();
                    return;
                }

                //API call to publish tweet
                client.postTweet(text, new JsonHttpResponseHandler(){

                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        JSONObject jsonObject = json.jsonObject;
                        try {
                            Tweet tweet = Tweet.fromJson(jsonObject);
                            Intent intent = new Intent();
                            intent.putExtra("tweet", Parcels.wrap(tweet));
                            setResult(RESULT_OK, intent);
                            finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "onFailure publish tweet", throwable);
                        Toast.makeText(ComposeActivity.this, "Could not tweet. Please try again soon. ", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}