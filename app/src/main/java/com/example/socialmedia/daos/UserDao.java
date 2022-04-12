package com.example.socialmedia.daos;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.socialmedia.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

public class UserDao {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference userCollection = db.collection("Users");
    String userImgUrl="";
    public void addUser(User user) {
        if (user != null) {
            userCollection.document(user.uid).set(user);
        }
    }

    public Task<DocumentSnapshot> getUserById(String uId) {
        return userCollection.document(uId).get();
    }

    public String UserNameById(String uId){
        final String[] userName = new String[1];
        userCollection.document(uId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user =documentSnapshot.toObject(User.class);
                userName[0]=user.displayName;
            }
        });

        return userName[0];
    }




}

