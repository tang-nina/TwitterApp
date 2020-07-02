package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
        tweets.clear(); //modify the refernece, never assign an empty list!!
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
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

            itemView.setOnClickListener(this);

            ivReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        System.out.println("HERE");
                        Intent intent = new Intent(context, ReplyActivity.class);
                        Tweet curTweet = tweets.get(position);
                        intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(curTweet));
                        //intent.putExtra("position", position);
                        context.startActivity(intent);
                    }

                }
            });

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

        public void bind(Tweet tweet) {
            tvBody.setText(tweet.getBody());
            tvName.setText(tweet.getUser().getName());
            tvHandle.setText(tweet.getUser().getTwitterId());
            tvRelativeTime.setText(tweet.getRelativeTime());

            Glide.with(context).load(tweet.getUser().getProfileImageUrl()).circleCrop().into(ivProfilePic);

            if (tweet.getMediaUrl().equals("")) {
                ivMedia.setVisibility(View.GONE);
            } else {
                ivMedia.setVisibility(View.VISIBLE);
                Glide.with(context).load(tweet.getMediaUrl()).fitCenter().transform(new RoundedCornersTransformation(20, 0)).into(ivMedia);
            }
        }

        @Override
        public void onClick(View view) {
            System.out.println("here");
            int position = getAdapterPosition();

            if (position != RecyclerView.NO_POSITION) {
                Log.i("here", "onClick: ");
                Intent intent = new Intent(context, TweetDetailsActivity.class);
                Tweet curTweet = tweets.get(position);
                intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(curTweet));
                //intent.putExtra("position", position);
                context.startActivity(intent);
            }
        }
    }
}
