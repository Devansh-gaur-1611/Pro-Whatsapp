package com.example.socialmedia;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.Adapter.User_name_adapter;
import com.example.socialmedia.daos.Chat_Dao;
import com.example.socialmedia.models.User;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;

public class Users_In_RecyclerView extends AppCompatActivity implements User_name_adapter.OnClickedListener1 {
    RecyclerView User_RecyclerView;
    User_name_adapter Adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_in_recyclerview);
        User_RecyclerView = findViewById(R.id.Users_RecyclerView);
        setUpUserAdapter();
    }

    private void setUpUserAdapter() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("Users");

        Query query = collectionReference.whereNotEqualTo("uid", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderBy("uid", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class).build();

        Adapter = new User_name_adapter(options, this);
        User_RecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//        User_RecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
//                DividerItemDecoration.VERTICAL));
        User_RecyclerView.setItemAnimator(new DefaultItemAnimator());

        User_RecyclerView.setAdapter(Adapter);


    }

    @Override
    protected void onStart() {
        super.onStart();
        Adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Adapter.stopListening();
    }

    @Override
    public void goToChatActivity(FirestoreRecyclerOptions<User> options, int position) {
        Intent intent = new Intent(getApplicationContext(), Chats_activity.class);
        String receiverUid = Objects.requireNonNull(options.getSnapshots().getSnapshot(position).toObject(User.class)).uid;
        String name_Receiver = Objects.requireNonNull(options.getSnapshots().getSnapshot(position).toObject(User.class)).displayName;
        intent.putExtra("name_Receiver", name_Receiver);
        intent.putExtra("Receiver_Uid", receiverUid);
        startActivity(intent);
    }

    @Override
    public void onImageClicked(FirestoreRecyclerOptions<User> options, int position) {
        Intent intent = new Intent(getApplicationContext(), See_image.class);
        String img_Url = Objects.requireNonNull(options.getSnapshots().getSnapshot(position).toObject(User.class)).imageUrl;
        String Receiver_name = Objects.requireNonNull(options.getSnapshots().getSnapshot(position).toObject(User.class)).displayName;
        intent.putExtra("img_user_url", img_Url);
        intent.putExtra("name_user", Receiver_name);
        startActivity(intent);
    }
}