package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.User;

import java.util.List;

//adapter for the recycler view displaying followers/following
public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.ViewHolder> {

    Context context;
    List<User> users;

    public FollowAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public FollowAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_profile, parent, false);
        return new FollowAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowAdapter.ViewHolder holder, int position) {
        User user = users.get(position);
        holder.bind(user);
    }

    //clear all data the adapter is using
    public void clear() {
        users.clear();
        notifyDataSetChanged();
    }

    //add to the data the adapter is binding
    public void addAll(List<User> list) {
        users.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    //view holder for individual follower/following items in the recycler view
    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProfilePic;
        TextView tvName;
        TextView tvHandle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfilePic = itemView.findViewById(R.id.ivProfilePic);
            tvName = itemView.findViewById(R.id.tvName);
            tvHandle = itemView.findViewById(R.id.tvHandle);
        }

        public void bind(User user) {
            tvName.setText(user.getName());
            tvHandle.setText(user.getTwitterId());
            Glide.with(context).load(user.getProfileImageUrl()).circleCrop().into(ivProfilePic);
        }
    }
}
