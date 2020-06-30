package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

public class TweetDetailsActivity extends AppCompatActivity {
    private static final String TAG = "TweetDetailsActivity";

    Tweet tweet;

    ImageView ivProfilePic;
    TextView tvBody;
    TextView tvName;
    TextView tvHandle;
    TextView tvTimestamp;
    ImageView ivMedia;
    ImageView ivLike;
    ImageView ivRetweet;
    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);

        tweet = Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        ivProfilePic = findViewById(R.id.ivProfilePic);
        tvBody = findViewById(R.id.tvBody);
        tvName = findViewById(R.id.tvName);
        tvHandle = findViewById(R.id.tvHandle);
        tvTimestamp = findViewById(R.id.tvTimestamp);
        ivMedia = findViewById(R.id.ivMedia);

        ivLike = findViewById(R.id.ivLike);
        ivRetweet = findViewById(R.id.ivRetweet);

        client = TwitterApplication.getRestClient(this);

        //LIKE AND RETWEET PICTURES MUST BE SET APPROPRIATELY ACCORDING TO WHAT HAS HAPPENED

        tvBody.setText(tweet.getBody());
        tvName.setText(tweet.getUser().getName());
        tvHandle.setText(tweet.getUser().getTwitterId());
        tvTimestamp.setText(tweet.getRelativeTime());

        Glide.with(this).load(tweet.getUser().getProfileImageUrl()).into(ivProfilePic);

        if (tweet.getMediaUrl().equals("")) {
            ivMedia.setVisibility(View.GONE);
        } else {
            ivMedia.setVisibility(View.VISIBLE);
            Glide.with(this).load(tweet.getMediaUrl()).fitCenter().transform(new RoundedCornersTransformation(20, 0)).into(ivMedia);
        }

    }

    public void clickHeart(android.view.View like){

        if(ivLike.getTag().equals("liked")){
            Glide.with(this).load(R.drawable.ic_vector_heart_stroke).into(ivLike);
            ivLike.setTag("unliked");

            //twitter api to unlike
            client.unlikeTweet(tweet.getId(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    //nothing needs to be done
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Log.e(TAG, "onFailure: unliking a tweet", throwable);

                }
            });

        }else{
            Glide.with(this).load(R.drawable.ic_vector_heart).into(ivLike);
            ivLike.setTag("liked");

            //twitter api to like
            client.likeTweet(tweet.getId(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    //nothing needs to be done
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Log.e(TAG, "onFailure: liking a tweet", throwable);

                }
            });
        }
    }

    public void clickRetweet(android.view.View retweet){
        //open up something to make a retweet, call twitter api

    }
}