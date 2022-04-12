package com.example.socialmedia;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.example.socialmedia.daos.Chat_Dao;
import com.example.socialmedia.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class send_image_from_chats extends AppCompatActivity {
    ImageView backArrow, UserImage, SendedImage;
    TextView receiverName;
    FloatingActionButton send;
    EditText sendText;
    ConstraintLayout cL;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_image_from_chats);
        backArrow = findViewById(R.id.backArrow_activity_send_image);
        UserImage = findViewById(R.id.user_img_activity_send_image);
        SendedImage = findViewById(R.id.imageView_sendImage_activity);
        receiverName = findViewById(R.id.receiver_name_activity_send_image);
        send = findViewById(R.id.sendChatBtn_sendImage);
        sendText = findViewById(R.id.send_text_sendImage);
        cL = findViewById(R.id.constraint_Layout_send_Image);
        toolbar = findViewById(R.id.toolbar_send_image);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_image_from_chats.super.onBackPressed();
            }
        });

        Intent intent = getIntent();
        String receiver_uId = intent.getStringExtra("Receiver_Uid");
        String Image_uri = intent.getStringExtra("img_uri");
        Uri img_Uri = Uri.parse(Image_uri);

        Glide.with(this).load(img_Uri).placeholder(R.drawable.placeholder).into(SendedImage);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Chat_Dao().send_And_upload_image(receiver_uId, img_Uri, sendText.getText().toString());
                send_image_from_chats.super.onBackPressed();

            }
        });

        cL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toolbar.getVisibility() == View.VISIBLE)
                    toolbar.setVisibility(View.GONE);
                else
                    toolbar.setVisibility(View.VISIBLE);
            }
        });

        FirebaseFirestore.getInstance().collection("Users").document(receiver_uId)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                receiverName.setText(user.displayName);
                Glide.with(getApplicationContext()).load(user.imageUrl).placeholder(R.drawable.user)
                        .circleCrop().into(UserImage);
            }
        });
    }
}