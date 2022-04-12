package com.example.socialmedia.daos;

import androidx.annotation.NonNull;

import com.example.socialmedia.models.Post;
import com.example.socialmedia.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class postDao {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public CollectionReference postCollection = db.collection("posts");
    FirebaseAuth auth = FirebaseAuth.getInstance();


    public void addPost(String text) {
        String currentUserId = Objects.requireNonNull(auth.getCurrentUser()).getUid();


        UserDao userDao = new UserDao();
        userDao.getUserById(currentUserId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User User = documentSnapshot.toObject(User.class);
                long currentTime = System.currentTimeMillis();
                ArrayList<String> arrayList = new ArrayList<>();

                Post post = new Post(text, User, currentTime, arrayList);

                postCollection.document().set(post);


            }
        });

    }

    public Task<DocumentSnapshot> getPostById(String postId) {
        return postCollection.document(postId).get();
    }

    public void updatesLikes(String postId) {
        String currentUserId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        getPostById(postId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Post post = documentSnapshot.toObject(Post.class);

                assert post != null;
                boolean isLiked = post.likedBy.contains(currentUserId);

                if (isLiked) {
                    post.likedBy.remove(currentUserId);
                } else {
                    post.likedBy.add(currentUserId);
                }
                postCollection.document(postId).set(post);
            }
        });
    }

    public void change_profile_Pic(String newImg_Url) {
        db.collection("posts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Post post = document.toObject(Post.class);
                                if (post.createdBy.uid.equals(auth.getCurrentUser().getUid())) {
                                    String s1 = auth.getCurrentUser().getPhotoUrl().toString();

                                    post.createdBy.imageUrl = s1.replaceAll(auth.getCurrentUser().getPhotoUrl().toString(), newImg_Url);
                                    postCollection.document(document.getId()).set(post);
//                                    Toast.makeText(context,post.text+"  "+ post.createdBy.imageUrl, Toast.LENGTH_SHORT).show();
                                }

                            }
                        } else {
//                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void change_Name(String name) {
        db.collection("posts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Post post = document.toObject(Post.class);
                                if (post.createdBy.uid.equals(auth.getCurrentUser().getUid())) {
                                    String s1 = post.createdBy.displayName;

                                    post.createdBy.displayName = s1.replaceAll(s1, name);
                                    postCollection.document(document.getId()).set(post);
//                                    Toast.makeText(context,post.text+"  "+ post.createdBy.imageUrl, Toast.LENGTH_SHORT).show();
                                }

                            }
                        } else {
//                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }


}
