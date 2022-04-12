package com.example.socialmedia;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class See_image extends AppCompatActivity {
    ImageView imageView;
    TextView name;
    ImageView backArrow;
    ImageView share;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_image);
        getSupportActionBar().hide();
        imageView = findViewById(R.id.imageView_chat);
        name=findViewById(R.id.name_chat);
        backArrow=findViewById(R.id.backArrow_chat);
        share=findViewById(R.id.share_chat);
        Intent intent = getIntent();
        String img_url = intent.getStringExtra("img_user_url");
        String name_user = intent.getStringExtra("name_user");
        Glide.with(getApplicationContext()).load(img_url).placeholder(R.drawable.placeholder).into(imageView);
        name.setText(name_user);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                See_image.super.onBackPressed();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, img_url);
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            }
        });
    }


}