package com.example.socialmedia.daos;

import android.net.Uri;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.socialmedia.models.Chats;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Chat_Dao {
    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    public CollectionReference chatsCollections = db.collection("Chats");
    public DocumentReference chatsDocumentRef = chatsCollections.document("Our chats");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    StorageReference reference = FirebaseStorage.getInstance().getReference()
            .child("Image_Send").child(String.valueOf(System.currentTimeMillis()));



    public void send_Chat(String receiver_uId, String text) {
        String sender_uId = mAuth.getCurrentUser().getUid();
        long currentTime = System.currentTimeMillis();
        final String sender_Room = sender_uId + receiver_uId;
        final String receiver_Room = receiver_uId + sender_uId;
        String id = chatsDocumentRef.collection(sender_Room).document().getId();
        Chats chats = new Chats(sender_uId, receiver_uId, currentTime, text);
        chats.setRandomKey(id);

        chatsDocumentRef.collection(sender_Room).document(id).set(chats).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                chatsDocumentRef.collection(receiver_Room).document(id).set(chats);
            }
        });
    }

    public void send_And_upload_image(String receiver_uId, Uri image_uri,String text) {
        String sender_uId = mAuth.getCurrentUser().getUid();
        long currentTime = System.currentTimeMillis();
        final String sender_Room = sender_uId + receiver_uId;
        final String receiver_Room = receiver_uId + sender_uId;
        String id = chatsDocumentRef.collection(sender_Room).document().getId();
        Chats chats = new Chats(sender_uId, receiver_uId, currentTime, text);
        chats.setRandomKey(id);
        reference.putFile(image_uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String filePath = uri.toString();
                            chats.setImageUrl(filePath);
                            chatsDocumentRef.collection(sender_Room).document(id).set(chats).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    chatsDocumentRef.collection(receiver_Room).document(id).set(chats);
                                }
                            });
                        }
                    });
                }


            }
        });
    }

    public void deleteChat(String sender_uId,String receiver_uId,String randomKey){
        chatsDocumentRef.collection(sender_uId+receiver_uId).document(randomKey).delete();
    }
}
