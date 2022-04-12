package com.example.socialmedia.Adapter;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.socialmedia.R;
import com.example.socialmedia.Utils;
import com.example.socialmedia.daos.Chat_Dao;
import com.example.socialmedia.models.Chats;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.OutputStream;
import java.util.Objects;

public class Chats_Adapter1 extends FirestoreRecyclerAdapter<Chats, RecyclerView.ViewHolder> {


    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private final FirestoreRecyclerOptions<Chats> options;
    OnClickListener_chats mOnClickListener_chats;
    FirebaseAuth mAuth=FirebaseAuth.getInstance();

    public Chats_Adapter1(@NonNull @NotNull FirestoreRecyclerOptions<Chats> options, OnClickListener_chats mOnClickListener_chats) {
        super(options);
        this.options = options;
        this.mOnClickListener_chats = mOnClickListener_chats;

    }

    @Override
    protected void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int i, @NonNull @NotNull Chats chats) {

        Chats chat_at_position_i = options.getSnapshots().getSnapshot(viewHolder.getAdapterPosition()).toObject(Chats.class);

        assert chat_at_position_i != null;


        if (chat_at_position_i.sender_uId.equals(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())) {

            // If the current user is the sender of the message

            ((senderViewHolder)viewHolder).senderMsg.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnClickListener_chats.deleteChat(options, viewHolder.getAdapterPosition());
                    return true;
                }
            });
            ((senderViewHolder)viewHolder).image_sended.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnClickListener_chats.deleteChat(options, viewHolder.getAdapterPosition());
                    return true;
                }
            });

            if (chat_at_position_i.message.equals("")) {
                ((senderViewHolder) viewHolder).senderMsg.setVisibility(View.GONE);
            } else {
                ((senderViewHolder) viewHolder).senderMsg.setVisibility(View.VISIBLE);
                ((senderViewHolder) viewHolder).senderMsg.setText(chat_at_position_i.message);
            }

            ((senderViewHolder) viewHolder).senderTime.setText(new Utils().testDateFormat(chats.time));
            ((senderViewHolder) viewHolder).senderDate.setText(new Utils().long_ToDateFormat(chats.time));

            if (chat_at_position_i.getMobileStoragePosition().equals("")) {
//                Image not saved in phone

                if (chat_at_position_i.getImageUrl() != null) {

                    ((senderViewHolder) viewHolder).image_sended.setVisibility(View.VISIBLE);


                    ((senderViewHolder) viewHolder).image_sended.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnClickListener_chats.openImage(options, viewHolder.getAdapterPosition());
                        }
                    });

                    Glide.with(((senderViewHolder) viewHolder).image_sended.getContext())
                            .load(chat_at_position_i.getImageUrl())
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                                    BitmapDrawable drawable = (BitmapDrawable) resource;
                                    Bitmap bitmap = drawable.getBitmap();
                                    String imagePos = saveImage(viewHolder, bitmap);
                                    String sender_Room = chat_at_position_i.sender_uId
                                            + chat_at_position_i.receiver_uId;

                                    String randomKey = chat_at_position_i.randomKey;
                                    new Chat_Dao().chatsDocumentRef.collection(sender_Room).document(randomKey).get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    Chats chat = documentSnapshot.toObject(Chats.class);
                                                    assert chat != null;
                                                    chat.setMobileStoragePosition(imagePos);
                                                    new Chat_Dao().chatsDocumentRef.collection(sender_Room).document(randomKey).set(chat)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {

                                                                }
                                                            });
                                                }
                                            });
                                    return true;
                                }
                            }).placeholder(R.drawable.placeholder).into(((senderViewHolder) viewHolder).image_sended);


                }

            } else {
                ((senderViewHolder) viewHolder).image_sended.setVisibility(View.VISIBLE);
                ((senderViewHolder) viewHolder).image_sended.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnClickListener_chats.openImage(options, viewHolder.getAdapterPosition());
                    }
                });

                Glide.with(((senderViewHolder) viewHolder).image_sended.getContext())
                        .load(chat_at_position_i.getMobileStoragePosition())
                        .placeholder(R.drawable.placeholder)
                        .into(((senderViewHolder) viewHolder).image_sended);

            }

        }
        else {
            // If some other user sent the message

            if(chat_at_position_i.message.equals("")){
                ((receiverViewHolder) viewHolder).receiverMsg.setVisibility(View.GONE);
            }
            else{
                ((receiverViewHolder) viewHolder).receiverMsg.setVisibility(View.VISIBLE);
                ((receiverViewHolder) viewHolder).receiverMsg.setText(chat_at_position_i.message);
            }

            ((receiverViewHolder) viewHolder).receiverTime.setText(new Utils().testDateFormat(chats.time));
            ((receiverViewHolder) viewHolder).receiverDate.setText(new Utils().long_ToDateFormat(chats.time));

            if (chat_at_position_i.getMobileStoragePosition().equals("")) {

                if (chat_at_position_i.getImageUrl() != null) {

                    ((receiverViewHolder) viewHolder).image_receieved.setVisibility(View.VISIBLE);

                    ((receiverViewHolder) viewHolder).image_receieved.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnClickListener_chats.openImage(options, viewHolder.getAdapterPosition());
                        }
                    });


                    Glide.with(((receiverViewHolder) viewHolder).image_receieved.getContext())
                            .load(chat_at_position_i.getImageUrl())
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    BitmapDrawable drawable = (BitmapDrawable) resource;
                                    Bitmap bitmap = drawable.getBitmap();
                                    String imagePos = saveImage_Receiver(viewHolder, bitmap);
                                    String receiver_Room = chat_at_position_i.receiver_uId + chat_at_position_i.sender_uId;
                                    String randomKey = chat_at_position_i.randomKey;
                                    new Chat_Dao().chatsDocumentRef.collection(receiver_Room).document(randomKey).get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    Chats chat = documentSnapshot.toObject(Chats.class);
                                                    assert chat != null;
                                                    chat.setMobileStoragePosition(imagePos);
                                                    new Chat_Dao().chatsDocumentRef.collection(receiver_Room).document(randomKey).set(chat);
                                                }
                                            });
                                    return true;
                                }
                            }).placeholder(R.drawable.placeholder).into(((receiverViewHolder) viewHolder).image_receieved);
                }
            } else {
                ((receiverViewHolder) viewHolder).image_receieved.setVisibility(View.VISIBLE);
                ((receiverViewHolder) viewHolder).image_receieved.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnClickListener_chats.openImage(options, viewHolder.getAdapterPosition());
                    }
                });


                Glide.with(((receiverViewHolder) viewHolder).image_receieved.getContext())
                        .load(chat_at_position_i.getMobileStoragePosition())
                        .placeholder(R.drawable.placeholder)
                        .into(((receiverViewHolder) viewHolder).image_receieved);
            }
        }
    }


    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            View view1 = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.sample_sender1, parent, false);
            return new senderViewHolder(view1);
        } else {

            View view2 = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.sample_receiver1, parent, false);
            return new receiverViewHolder(view2);

        }

    }

    @Override
    public int getItemViewType(int position) {
        Chats chat_At_pos_i=options.getSnapshots().getSnapshot(position).toObject(Chats.class);
        assert chat_At_pos_i != null;
        if (chat_At_pos_i.sender_uId.equals((Objects.requireNonNull(mAuth.getCurrentUser())).getUid())) {
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    public class senderViewHolder extends RecyclerView.ViewHolder {
        TextView senderMsg, senderTime, senderDate;
        ImageView image_sended;

        public senderViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            senderMsg = itemView.findViewById(R.id.sender_text);
            senderTime = itemView.findViewById(R.id.sender_Time);
            senderDate = itemView.findViewById(R.id.sender_date);
            image_sended = itemView.findViewById(R.id.image_sended);
        }
    }

    public class receiverViewHolder extends RecyclerView.ViewHolder {
        TextView receiverMsg, receiverTime, receiverDate;
        ImageView image_receieved;

        public receiverViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            receiverMsg = itemView.findViewById(R.id.receiver_text);
            receiverTime = itemView.findViewById(R.id.receiver_time);
            receiverDate = itemView.findViewById(R.id.receiver_date);
            image_receieved = itemView.findViewById(R.id.image_received);
        }
    }

    public interface OnClickListener_chats {
        void openImage(FirestoreRecyclerOptions<Chats> options, int position);

        void deleteChat(FirestoreRecyclerOptions<Chats> options, int position);

    }

    private String saveImage(RecyclerView.ViewHolder viewHolder, Bitmap bitmap) {
        OutputStream fos;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                String imagePosition;
                ContentResolver resolver = ((senderViewHolder) viewHolder).image_sended.getContext().getContentResolver();
                ContentValues contentValues = new ContentValues();
                String img = "Image_" + System.currentTimeMillis() + ".jpg";
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, img);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + "Social Media" + File.separator + "Sended Images");
                Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                imagePosition = imageUri.toString();
                fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                Objects.requireNonNull(fos);

                return imagePosition;
            }
        } catch (Exception e) {

//            Toast.makeText(viewHolder).image_sended.getContext(), "ImageNot saved error:  " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return "";
        }
        return "";
    }

    private String saveImage_Receiver(RecyclerView.ViewHolder viewHolder, Bitmap bitmap) {
        OutputStream fos;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                String imagePosition;
                ContentResolver resolver = ((receiverViewHolder) viewHolder).image_receieved.getContext().getContentResolver();
                ContentValues contentValues = new ContentValues();
                String img = "Image_" + System.currentTimeMillis() + ".jpg";
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, img);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + "Social Media" + File.separator + "Received Images");
                Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                imagePosition = imageUri.toString();
                fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                Objects.requireNonNull(fos);

                return imagePosition;
            }
        } catch (Exception e) {

//            Toast.makeText(viewHolder).image_sended.getContext(), "ImageNot saved error:  " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return "";
        }
        return "";
    }


}
