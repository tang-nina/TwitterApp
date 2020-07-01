package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ReplyActivity extends AppCompatActivity {
    private static final String TAG = "ReplyActivity";
    TwitterClient client;
    Button btnReply;
    EditText etCompose;
    Tweet tweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        getSupportActionBar().setTitle("Reply to Tweet");
        btnReply = findViewById(R.id.btnReply);
        etCompose = findViewById(R.id.etCompose);

        client = TwitterApplication.getRestClient(this);

        tweet = Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));
        etCompose.setText(tweet.getUser().getTwitterId());


        btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String body = etCompose.getText().toString();
                client.reply(body, tweet.getId(), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Toast.makeText(ReplyActivity.this, "Your reply tweet was successful! ", Toast.LENGTH_LONG).show();

                        Tweet tweet = null;
                        try {
                            tweet = Tweet.fromJson(json.jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Intent intent = new Intent();
                        intent.putExtra("tweet", Parcels.wrap(tweet));
                        //setResult(RESULT_OK, intent);

                        TimelineActivity.getInstance().reload(intent);
                        finish();
                        ///Intent intent = new Intent(ReplyActivity.this, TimelineActivity.class);
                        // startActivity(intent);
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Toast.makeText(ReplyActivity.this, "Your reply tweet was not successful. ", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "onFailure: reply", throwable);

                    }
                });

            }
        });
    }
}