package com.example.socialmedia;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.socialmedia.daos.postDao;

public class Create_Post_Activity extends AppCompatActivity {
    AppCompatButton createPost;
    EditText Post;
    postDao postDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        createPost = findViewById(R.id.createPost);
        Post = findViewById(R.id.Post);


        createPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = Post.getText().toString();
                if (!input.isEmpty()) {
                    postDao = new postDao();
                    postDao.addPost(input);
                    finish();

                }
            }
        });
    }
}