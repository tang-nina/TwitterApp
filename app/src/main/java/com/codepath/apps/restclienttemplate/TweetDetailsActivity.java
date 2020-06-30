package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetDetailsActivity extends AppCompatActivity {
    Tweet tweet;

    ImageView ivProfilePic;
    TextView tvBody;
    TextView tvName;
    TextView tvHandle;
    TextView tvTimestamp;
    ImageView ivMedia;

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
}