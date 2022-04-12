package com.example.socialmedia.Adapter;

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
import com.example.socialmedia.daos.Chat_Dao;
import com.example.socialmedia.models.Chats;
import com.example.socialmedia.models.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class User_name_adapter extends FirestoreRecyclerAdapter<User, User_name_adapter.UserViewholder> {

    FirestoreRecyclerOptions<User> options;
    OnClickedListener1 mOnClickedListener1;

    public User_name_adapter(@NonNull FirestoreRecyclerOptions<User> options, OnClickedListener1 mOnClickedListener1) {
        super(options);
        this.options = options;
        this.mOnClickedListener1 = mOnClickedListener1;
    }

    @NonNull
    @Override
    public UserViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_view, parent, false);
        return new UserViewholder(view);

    }

    @Override
    protected void onBindViewHolder(@NonNull UserViewholder holder, int i, @NonNull User user) {


        holder.User_Name.setText(user.displayName);
        Glide.with(holder.UserImage.getContext()).load(user.imageUrl)
                .centerCrop().circleCrop().placeholder(R.drawable.user)
                .into(holder.UserImage);
        holder.LastMessage.setText(R.string.Msg);
        holder.CreatedAt.setText(" ");
        String senderUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String receiverUid = user.uid;
        String senderRoom = senderUid + receiverUid;
        Query query = new Chat_Dao().chatsDocumentRef.collection(senderRoom).orderBy("time", Query.Direction.DESCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        String lastMessage = snapshot.toObject(Chats.class).message;
                        long lastMsgTime = snapshot.toObject(Chats.class).time;
                        String img = snapshot.toObject(Chats.class).getImageUrl();
                        String time = new Utils().convert(lastMsgTime);
                        holder.CreatedAt.setText(time);
                        holder.LastMessage.setText(lastMessage);
                        if (img != null) {
                            holder.img_view.setVisibility(View.VISIBLE);
                            if (lastMessage.equals("")) {
                                holder.LastMessage.setText("photo");
                            }
                        } else {
                            holder.img_view.setVisibility(View.GONE);
                        }

                        break;
                    }

                }
            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickedListener1.goToChatActivity(options, holder.getAdapterPosition());
            }
        });
        holder.UserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickedListener1.onImageClicked(options, holder.getAdapterPosition());
            }
        });


    }

    public class UserViewholder extends RecyclerView.ViewHolder {
        TextView LastMessage, CreatedAt, User_Name;
        ImageView UserImage, img_view;

        public UserViewholder(@NonNull View itemView) {
            super(itemView);
            LastMessage = itemView.findViewById(R.id.LastMessage_User_RV);
            CreatedAt = itemView.findViewById(R.id.createdAt_User_RV);
            User_Name = itemView.findViewById(R.id.UserName_User_RV);
            UserImage = itemView.findViewById(R.id.userImage_User_RV);
            img_view = itemView.findViewById(R.id.img);
        }
    }

    public interface OnClickedListener1 {
        void goToChatActivity(FirestoreRecyclerOptions<User> options, int position);

        void onImageClicked(FirestoreRecyclerOptions<User> options, int position);


    }
}
