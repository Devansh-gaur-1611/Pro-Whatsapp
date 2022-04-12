package com.example.socialmedia.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmedia.R;
import com.example.socialmedia.Utils;
import com.example.socialmedia.models.Post;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PostAdapter extends FirestoreRecyclerAdapter<Post, PostAdapter.PostViewHolder> {

    private final OnClickedListener mOnClickedListener;
    Context context;
    FirestoreRecyclerOptions<Post> options;

    public PostAdapter(FirestoreRecyclerOptions<Post> options, OnClickedListener mOnClickedListener) {
        super(options);
        this.mOnClickedListener = mOnClickedListener;
        this.options=options;
    }

    @Override
    protected void onBindViewHolder(PostAdapter.PostViewHolder holder, int position, @NotNull Post model) {
        holder.userName.setText(model.createdBy.displayName);
        holder.postTitle.setText(model.text);
        holder.createdAt.setText(new Utils().convert(model.createdAt));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FirebaseAuth.getInstance().getCurrentUser().getUid()!=model.createdBy.uid) {
                    String reciever = model.createdBy.uid;
                    mOnClickedListener.goToChat(reciever);
                }
            }
        });



        Glide.with(holder.userImage.getContext()).load(model.createdBy.imageUrl)
                .centerCrop().circleCrop()
                .into(holder.userImage);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String currentUserId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        if (!(model.likedBy == null) && !model.likedBy.isEmpty()) {
            boolean isLiked = model.likedBy.contains(currentUserId);
            holder.likeCount.setText(Integer.toString(model.likedBy.size()));
            if (isLiked) {
                holder.likeButton.setImageResource(R.drawable.like);
            } else {
                holder.likeButton.setImageResource(R.drawable.unliked);
            }
            holder.likeCount.setText(Integer.toString(model.likedBy.size()));
        } else {
            holder.likeButton.setImageResource(R.drawable.unliked);
            holder.likeCount.setText("0");
        }


    }

    @NonNull
    @NotNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_post, viewGroup, false);
        PostViewHolder viewHolder = new PostViewHolder(view);
        viewHolder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickedListener.onLikeClicked(getSnapshots().getSnapshot(viewHolder.getAdapterPosition()).getId());

            }
        });
        viewHolder.userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mOnClickedListener.onImageClicked(context,options,viewHolder.getAdapterPosition());
            }
        });

        return viewHolder;
    }


    static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView userImage;
        TextView userName;
        TextView createdAt;
        TextView postTitle;
        TextView likeCount;
        ImageView likeButton;

        public PostViewHolder(@NotNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.userImage_User_RV);
            userName = itemView.findViewById(R.id.userName);
            createdAt = itemView.findViewById(R.id.createdAt_User_RV);
            postTitle = itemView.findViewById(R.id.LastMessage_User_RV);
            likeButton = itemView.findViewById(R.id.likeButton);
            likeCount = itemView.findViewById(R.id.likeCount);


        }


    }

    public interface OnClickedListener {
        void onLikeClicked(String postId);
        void onImageClicked(Context context,FirestoreRecyclerOptions<Post> options,int position);
        void goToChat(String receiverUid );

    }
}
