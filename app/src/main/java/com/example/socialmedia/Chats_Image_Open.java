package com.example.socialmedia;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;

public class Chats_Image_Open extends AppCompatActivity {
    ImageView imageView_chat,backArrow_chat,share_chat;
    TextView name_chat,time_chat;
    Toolbar toolbar;
    ConstraintLayout constraintLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats_image_open);
        constraintLayout=findViewById(R.id.constraint_Layout);
        toolbar=findViewById(R.id.Toolbar);
        imageView_chat=findViewById(R.id.imageView_chat);
        backArrow_chat=findViewById(R.id.backArrow_chat);
        share_chat=findViewById(R.id.share_chat);
        name_chat=findViewById(R.id.name_chat);
        time_chat=findViewById(R.id.time_chat);
        backArrow_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Chats_Image_Open.super.onBackPressed();
            }
        });
        name_chat.setText(getIntent().getStringExtra("name"));
        time_chat.setText(getIntent().getStringExtra("time"));
        Glide.with(getApplicationContext()).load(getIntent().getStringExtra("image")).into(imageView_chat);

        imageView_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toolbar.getVisibility()==View.VISIBLE){
                toolbar.setVisibility(View.GONE);
                }else{
                    toolbar.setVisibility(View.VISIBLE);
                }
            }
        });
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toolbar.getVisibility()==View.VISIBLE){
                    toolbar.setVisibility(View.GONE);
                }else{
                    toolbar.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}