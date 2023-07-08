package com.heuristic.microbloggingapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.heuristic.microbloggingapp.databinding.PostCardItemBinding;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Posts> posts;

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PostCardItemBinding view = PostCardItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        holder.bind(posts.get(position));
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void setData(List<Posts> posts) {
        if (this.posts != null) {
            this.posts.clear();
        } else {
            this.posts = new ArrayList<>();
        }
        this.posts.addAll(posts);
        notifyDataSetChanged();
    }

    class PostViewHolder extends RecyclerView.ViewHolder{

        private PostCardItemBinding binding;
        public PostViewHolder(@NonNull PostCardItemBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        public void bind(Posts posts) {
            binding.name.setText(posts.getUser_name());
            binding.content.setText(posts.getPost_description());
        }
    }
}
