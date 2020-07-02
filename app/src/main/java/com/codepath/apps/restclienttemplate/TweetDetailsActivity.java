package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.databinding.ActivityTweetDetailsBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

//activity for showing the details of a tweet, has functionality for liking or retweeting the tweet
public class TweetDetailsActivity extends AppCompatActivity {
    private static final String TAG = "TweetDetailsActivity";

    Tweet tweet;
    TwitterClient client;
    ActivityTweetDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTweetDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        getSupportActionBar().setTitle("Tweet");

        tweet = Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));
        client = TwitterApplication.getRestClient(this);

        binding.tvBody.setText(tweet.getBody());
        binding.tvName.setText(tweet.getUser().getName());
        binding.tvHandle.setText(tweet.getUser().getTwitterId());
        binding.tvTimestamp.setText(tweet.getRelativeTime());
        Glide.with(this).load(tweet.getUser().getProfileImageUrl())
                .circleCrop().into(binding.ivProfilePic);

        //if profile picture is clicked, send user to Profile page of the account who tweeted it
        binding.ivProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = TweetDetailsActivity.this;
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra(User.class.getSimpleName(), Parcels.wrap(tweet.getUser()));
                context.startActivity(intent);
            }
        });

        //if there is no media in the tweet, get rid of media iamge veiw.
        //else if there is media in the tweet, display it.
        if (tweet.getMediaUrl().equals("")) {
            binding.ivMedia.setVisibility(View.GONE);
        } else {
            binding.ivMedia.setVisibility(View.VISIBLE);
            Glide.with(this).load(tweet.getMediaUrl())
                    .fitCenter()
                    .transform(new RoundedCornersTransformation(20, 0))
                    .into(binding.ivMedia);
        }

        //check if the tweet has been liked or retweeted by the user and
        // set the heart and retweet icon accordingly
        client.getTweet(tweet.getId(), new JsonHttpResponseHandler() {
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
                    Glide.with(TweetDetailsActivity.this)
                            .load(R.drawable.ic_vector_heart).into(binding.ivLike);
                    binding.ivLike.setTag("liked");
                }

                if(retweeted) {
                    Glide.with(TweetDetailsActivity.this)
                            .load(R.drawable.ic_vector_retweet).into(binding.ivRetweet);
                    binding.ivRetweet.setTag("retweeted");
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure: oncreate",throwable );
            }
        });
    }

    //listener for if the user clicks the heart to like/unlike the tweet
    public void clickHeart(android.view.View like) {

        if (binding.ivLike.getTag().equals("liked")) { //if the tweet was already liked
            //call twitter api to unlike
            client.unlikeTweet(tweet.getId(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    Glide.with(TweetDetailsActivity.this).
                            load(R.drawable.ic_vector_heart_stroke).into(binding.ivLike);
                    binding.ivLike.setTag("unliked");
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Toast.makeText(TweetDetailsActivity.this, "Something went wrong - couldn't unlike tweet.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onFailure: unliking a tweet", throwable);
                }
            });

        } else { //if the tweet was already not liked
            //call twitter api to like
            client.likeTweet(tweet.getId(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    Glide.with(TweetDetailsActivity.this).load(R.drawable.ic_vector_heart).into(binding.ivLike);
                    binding.ivLike.setTag("liked");
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Toast.makeText(TweetDetailsActivity.this, "Something went wrong - couldn't like tweet.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onFailure: liking a tweet", throwable);
                }
            });
        }
    }

    //listener for if the user clicks the retweet sign to retweet/un-retweet the tweet
    public void clickRetweet(android.view.View retweet) {

        if (binding.ivRetweet.getTag().equals("retweeted")) { //if the tweet was already retweeted
            //call twitter api to un-retweet
            client.unRetweet(tweet.getId(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    Toast.makeText(TweetDetailsActivity.this, "Undid retweet successfully!", Toast.LENGTH_SHORT).show();
                    Glide.with(TweetDetailsActivity.this).load(R.drawable.ic_vector_retweet_stroke).into(binding.ivRetweet);
                    binding.ivRetweet.setTag("unretweeted");
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Toast.makeText(TweetDetailsActivity.this, "Something went wrong - couldn't undo retweet.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onFailure:  unretweet", throwable);
                }
            });

        } else { //if the tweet was not retweeted
            //call twitter api to retweet
            client.retweet(tweet.getId(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    Toast.makeText(TweetDetailsActivity.this, "Retweeted successfully!", Toast.LENGTH_SHORT).show();
                    Glide.with(TweetDetailsActivity.this).load(R.drawable.ic_vector_retweet).into(binding.ivRetweet);
                    binding.ivRetweet.setTag("retweeted");
                    //will not show user's own retweets on home timeline because actual
                    //twitter doesn't show it (unless you retweet with comment which is known
                    //as "replying" for this app)
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Toast.makeText(TweetDetailsActivity.this, "Something went wrong - couldn't retweet.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onFailure:  retweet", throwable);
                }
            });
        }
    }
}