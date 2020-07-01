package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.databinding.ActivityTweetDetailsBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

public class TweetDetailsActivity extends AppCompatActivity {
    private static final String TAG = "TweetDetailsActivity";

    Tweet tweet;
    TwitterClient client;
    ActivityTweetDetailsBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTweetDetailsBinding.inflate(getLayoutInflater());
        // layout of activity is stored in a special property called root
        View view = binding.getRoot();
        setContentView(view);

        tweet = Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));
        client = TwitterApplication.getRestClient(this);

        binding.tvBody.setText(tweet.getBody());
        binding.tvName.setText(tweet.getUser().getName());
        binding.tvHandle.setText(tweet.getUser().getTwitterId());
        binding.tvTimestamp.setText(tweet.getRelativeTime());

        Glide.with(this).load(tweet.getUser().getProfileImageUrl()).into(binding.ivProfilePic);

        if (tweet.getMediaUrl().equals("")) {
            binding.ivMedia.setVisibility(View.GONE);
        } else {
            binding.ivMedia.setVisibility(View.VISIBLE);
            Glide.with(this).load(tweet.getMediaUrl()).fitCenter().transform(new RoundedCornersTransformation(20, 0)).into(binding.ivMedia);
        }

        client.getTweet(tweet.getId(), new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;
                boolean liked = false;
                boolean retweeted = false;

                try {
                    retweeted = jsonObject.getBoolean("retweeted");
                    liked = jsonObject.getBoolean("favorited");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(liked) {
                    Glide.with(TweetDetailsActivity.this).load(R.drawable.ic_vector_heart).into(binding.ivLike);
                    binding.ivLike.setTag("liked");
                }

                if(retweeted) {
                    Glide.with(TweetDetailsActivity.this).load(R.drawable.ic_vector_retweet).into(binding.ivRetweet);
                    binding.ivRetweet.setTag("retweeted");
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure: oncreate",throwable );

            }
        });
    }

    public void clickHeart(android.view.View like){

        if (binding.ivLike.getTag().equals("liked")) {
            //twitter api to unlike
            client.unlikeTweet(tweet.getId(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    //nothing needs to be done
                    Glide.with(TweetDetailsActivity.this).load(R.drawable.ic_vector_heart_stroke).into(binding.ivLike);
                    binding.ivLike.setTag("unliked");
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Toast.makeText(TweetDetailsActivity.this, "Something went wrong - couldn't unlike tweet.", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "onFailure: unliking a tweet", throwable);

                }
            });

        }else{
            //twitter api to like
            client.likeTweet(tweet.getId(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    Glide.with(TweetDetailsActivity.this).load(R.drawable.ic_vector_heart).into(binding.ivLike);
                    binding.ivLike.setTag("liked");
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Toast.makeText(TweetDetailsActivity.this, "Something went wrong - couldn't like tweet.", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "onFailure: liking a tweet", throwable);

                }
            });
        }
    }

    public void clickRetweet(android.view.View retweet){
        //open up something to make a retweet, call twitter api

        if (binding.ivRetweet.getTag().equals("retweeted")) {

            client.unRetweet(tweet.getId(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    Toast.makeText(TweetDetailsActivity.this, "Undid retweet successfully!", Toast.LENGTH_LONG).show();

                    Glide.with(TweetDetailsActivity.this).load(R.drawable.ic_vector_retweet_stroke).into(binding.ivRetweet);
                    binding.ivRetweet.setTag("unretweeted");
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Toast.makeText(TweetDetailsActivity.this, "Something went wrong - couldn't undo retweet.", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "onFailure:  unretweet", throwable);

                }
            });

        }else{
            client.retweet(tweet.getId(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    System.out.println("HERERERERERERER");
                    System.out.println(json.jsonObject);
                    Toast.makeText(TweetDetailsActivity.this, "Retweeted successfully!", Toast.LENGTH_LONG).show();

                    Glide.with(TweetDetailsActivity.this).load(R.drawable.ic_vector_retweet).into(binding.ivRetweet);
                    binding.ivRetweet.setTag("retweeted");

                    //will not shown my own retweets on timeline

//                    Intent intent = new Intent();
//                    try {
//                        intent.putExtra("tweet", Parcels.wrap(Tweet.fromJson(json.jsonObject)));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                    //TimelineActivity.getInstance().addToFront(intent);
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Toast.makeText(TweetDetailsActivity.this, "Something went wrong - couldn't retweet.", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "onFailure:  retweet", throwable);

                }
            });

        }


    }

}