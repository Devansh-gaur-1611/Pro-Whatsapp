package com.example.socialmedia;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.socialmedia.daos.UserDao;
import com.example.socialmedia.daos.postDao;
import com.example.socialmedia.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Change_Image_Activity extends AppCompatActivity {
    private static final int PICK_IMAGE = 123;
    FirebaseAuth mAuth;
    ImageView userImage;
    Button changeImage;
    String imageURL;
    Uri Img_uri;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_image);
        mAuth = FirebaseAuth.getInstance();
        userImage = findViewById(R.id.newUserImage);
        changeImage = findViewById(R.id.change_Image_Btn);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference userCollection = db.collection("Users");
        userCollection.document(mAuth.getCurrentUser().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        User user = task.getResult().toObject(User.class);
                        assert user != null;
                        String Img_User_Url = user.imageUrl;
                        Glide.with(getApplicationContext()).load(Img_User_Url).placeholder(R.drawable.user).circleCrop().into(userImage);

                    }
                });


        progressBar = findViewById(R.id.progressBar);

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImages();
            }
        });
        changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setImage();
            }
        });
    }

    private void openImages() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {

            if (data != null) {
                Img_uri = data.getData();
                imageURL = Img_uri.toString();
                Glide.with(getApplicationContext()).load(Img_uri).circleCrop().into(userImage);
                Toast.makeText(getApplicationContext(), imageURL, Toast.LENGTH_SHORT).show();

            } else {
                Intent intent = new Intent(getApplicationContext(), Change_Image_Activity.class);
                startActivity(intent);
                finishAffinity();
            }
        } else {
            Intent intent = new Intent(getApplicationContext(), Change_Image_Activity.class);
            startActivity(intent);
            finishAffinity();
        }

    }

    public void setImage() {
        progressBar.setVisibility(View.VISIBLE);
        changeImage.setVisibility(View.INVISIBLE);

        StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("Profile_Pic").child(mAuth.getCurrentUser().getUid());

        if (Img_uri != null) {
            reference.putFile(Img_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            CollectionReference userCollection = db.collection("Users");
                            userCollection.document(mAuth.getCurrentUser().getUid()).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            User user = task.getResult().toObject(User.class);

                                            User user1 = new User(user.uid, user.displayName
                                                    , uri.toString());
                                            UserDao userDao = new UserDao();
                                            userDao.addUser(user1);
                                            new postDao().change_profile_Pic(uri.toString());
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(intent);
                                            finishAffinity();
                                        }
                                    });

                        }
                    });
                }
            });
        } else {
            Toast.makeText(this, "Select an New Profile Img First", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
            changeImage.setVisibility(View.VISIBLE);

        }


    }
}