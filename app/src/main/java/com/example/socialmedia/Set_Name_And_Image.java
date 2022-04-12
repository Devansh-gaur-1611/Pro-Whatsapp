package com.example.socialmedia;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.bumptech.glide.Glide;
import com.example.socialmedia.daos.UserDao;
import com.example.socialmedia.daos.postDao;
import com.example.socialmedia.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Set_Name_And_Image extends AppCompatActivity {
    EditText Name_user;
    ImageView User_Img;
    ImageView SetImg_Btn;
    AppCompatButton SetName_Img_Btn;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    Uri User_Img_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_name_and_image);
        getSupportActionBar().hide();
        Name_user = findViewById(R.id.Name_user);
        User_Img = findViewById(R.id.new_user_img);
        SetImg_Btn = findViewById(R.id.set_image);
        SetName_Img_Btn = findViewById(R.id.SetName_Img_Btn);
        progressBar=findViewById(R.id.progressBar);
        mAuth=FirebaseAuth.getInstance();
        SetImg_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 123);
            }
        });
        SetName_Img_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadImage();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {
            if (data != null) {
                assert data.getData() != null;
                if (data.getData() != null) {
                    User_Img_uri = data.getData();
                    Glide.with(this).load(User_Img_uri).placeholder(R.drawable.user_placeholder).circleCrop()
                            .into(User_Img);
                }

            } else {
                Intent intent = new Intent(getApplicationContext(), Set_Name_And_Image.class);
                startActivity(intent);
            }
        }
    }

    public void UploadImage() {
        progressBar.setVisibility(View.VISIBLE);
        SetName_Img_Btn.setVisibility(View.INVISIBLE);
        String User_Name = Name_user.getText().toString();
        String currentUserUid=mAuth.getCurrentUser().getUid();

        StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("Profile_Pic").child(mAuth.getCurrentUser().getUid());

        if (!User_Name.isEmpty()) {
            if (User_Img_uri != null) {

                reference.putFile(User_Img_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                User user=new User(currentUserUid,User_Name,uri.toString());
                                UserDao userDao = new UserDao();
                                userDao.addUser(user);
                                postDao postDao=new postDao();
                                postDao.change_profile_Pic(User_Img_uri.toString());
                                postDao.change_Name(User_Name);
                                Intent intent = new Intent(getApplicationContext(), Users_In_RecyclerView.class);

                                startActivity(intent);
                                finishAffinity();

                            }
                        });
                    }
                });
            }else{ Toast.makeText(this, "Please select your profile Image", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                SetName_Img_Btn.setVisibility(View.VISIBLE);

            }
        } else {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
            SetName_Img_Btn.setVisibility(View.VISIBLE);

        }


    }

}