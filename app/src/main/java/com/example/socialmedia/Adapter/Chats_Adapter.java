package com.example.socialmedia.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.socialmedia.Utils;
import com.example.socialmedia.daos.Chat_Dao;
import com.example.socialmedia.models.Chats;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

public class Chats_Adapter extends FirestoreRecyclerAdapter<Chats,Chats_Adapter.senderViewHolder> {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    public Chats_Adapter(@NotNull FirestoreRecyclerOptions<Chats> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull @NotNull Chats_Adapter.senderViewHolder senderViewHolder, int i, @NonNull @NotNull Chats chats) {
        senderViewHolder.senderMsg.setText(chats.message);
        senderViewHolder.senderTime.setText(new Utils().testDateFormat(chats.time));
        senderViewHolder.senderDate.setText(new Utils().long_ToDateFormat(chats.time));
    }

    @NonNull
    @NotNull
    @Override
    public senderViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.sample_sender,parent,false);

        return new senderViewHolder(view);
    }



//    @Override
//    public int getItemViewType(int position) {
//        Chat_Dao chat_dao=new Chat_Dao();
//        DocumentReference documentReference=chat_dao.chatsDocumentRef;
//        String sender_Room="10";
//        final int[] view_type = new int[0];
//        CollectionReference chatsCollections = chat_dao.chatsDocumentRef.collection(sender_Room);
//        chatsCollections.addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                for(QueryDocumentSnapshot snapshot : value){
//                    Chats chats=snapshot.toObject(Chats.class);
//                    if (chats.sender_uId.equals(mAuth.getCurrentUser().getUid()) ){
//                        // If the current user is the sender of the message
//
//                        view_type[0] =VIEW_TYPE_MESSAGE_SENT;
//
//                    }else{
//                        // If some other user sent the message
//                        view_type[0]=VIEW_TYPE_MESSAGE_RECEIVED;
//                    }
//                }
//            }
//        });
//
//
//        return view_type[0];
//
//
//    }

    public class senderViewHolder extends RecyclerView.ViewHolder{
        TextView senderMsg,senderTime,senderDate;
        public senderViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            senderMsg=itemView.findViewById(R.id.sender_text);
            senderTime=itemView.findViewById(R.id.sender_Time);
            senderDate=itemView.findViewById(R.id.sender_date);
        }
    }
    public class receiverViewHolder extends RecyclerView.ViewHolder{
        TextView receiverMsg,receiverTime;
        public receiverViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            receiverMsg=itemView.findViewById(R.id.receiver_text);
            receiverTime=itemView.findViewById(R.id.receiver_time);
        }
    }
}
