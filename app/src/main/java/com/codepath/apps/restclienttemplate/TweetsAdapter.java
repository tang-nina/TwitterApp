package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;

import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

//adapter for the recycler view displaying tweets
public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {
    Context context;
    List<Tweet> tweets;

    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tweet tweet = tweets.get(position);
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivProfilePic;
        TextView tvBody;
        TextView tvName;
        TextView tvHandle;
        TextView tvRelativeTime;
        ImageView ivMedia;
        ImageView ivReply;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProfilePic = itemView.findViewById(R.id.ivProfilePic);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvName = itemView.findViewById(R.id.tvName);
            tvHandle = itemView.findViewById(R.id.tvHandle);
            tvRelativeTime = itemView.findViewById(R.id.tvRelativeTime);
            ivMedia = itemView.findViewById(R.id.ivMedia);
            ivReply = itemView.findViewById(R.id.ivReply);

            //clicking the tweet will send user to the details page of the tweet
            itemView.setOnClickListener(this);

            //clicking reply button will direct you to reply activity
            ivReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Intent intent = new Intent(context, ReplyActivity.class);
                        Tweet curTweet = tweets.get(position);
                        intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(curTweet));
                        context.startActivity(intent);
                    }
                }
            });

            //clicking profile pic will sent you to the profile page of the original tweeter
            ivProfilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Intent intent = new Intent(context, ProfileActivity.class);
                        User curUser = tweets.get(position).getUser();
                        intent.putExtra(User.class.getSimpleName(), Parcels.wrap(curUser));
                        context.startActivity(intent);
                    }
                }
            });
        }

        //populate the view
        public void bind(Tweet tweet) {
            tvBody.setText(tweet.getBody());
            tvName.setText(tweet.getUser().getName());
            tvHandle.setText(tweet.getUser().getTwitterId());
            tvRelativeTime.setText(tweet.getRelativeTime());

            Glide.with(context).load(tweet.getUser().getProfileImageUrl()).circleCrop()
                    .into(ivProfilePic);

            //if there is no media in the tweet, collapse media image view.
            // Otherwise, display media.
            if (tweet.getMediaUrl().equals("")) {
                ivMedia.setVisibility(View.GONE);
            } else {
                ivMedia.setVisibility(View.VISIBLE);
                Glide.with(context).load(tweet.getMediaUrl()).fitCenter()
                        .transform(new RoundedCornersTransformation(20, 0))
                        .into(ivMedia);
            }
        }

        //click listener for the tweet item.
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Intent intent = new Intent(context, TweetDetailsActivity.class);
                Tweet curTweet = tweets.get(position);
                intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(curTweet));
                context.startActivity(intent);
            }
        }
    }
}
