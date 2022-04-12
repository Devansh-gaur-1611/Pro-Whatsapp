package com.example.socialmedia;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmedia.Adapter.Chats_Adapter;
import com.example.socialmedia.Adapter.Chats_Adapter1;
import com.example.socialmedia.daos.Chat_Dao;
import com.example.socialmedia.daos.UserDao;
import com.example.socialmedia.daos.dbHelper;
import com.example.socialmedia.models.Chats;
import com.example.socialmedia.models.User;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Chats_activity extends AppCompatActivity implements Chats_Adapter1.OnClickListener_chats {
    EditText send_text;
    FloatingActionButton sendChatBtn;
    TextView receiver_Name;
    Chat_Dao chat_dao;
    Chats_Adapter chats_adapter;
    ImageView receiver_image;
    ImageView backArrow;
    Toolbar toolbar;
    Chats_Adapter1 chats_adapter1;
    AppCompatButton img_sendBtn;
    ProgressBar SendingImgProgressBar;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        toolbar = findViewById(R.id.Toolbar_chats_activity);
        setSupportActionBar(toolbar);
        backArrow = findViewById(R.id.backArrow_activity_chats);
        receiver_image = findViewById(R.id.user_img_chatsActivity);
        send_text = findViewById(R.id.send_text_sendImage);
        sendChatBtn = findViewById(R.id.sendChatBtn);
        receiver_Name = findViewById(R.id.reciever_name);
        img_sendBtn = findViewById(R.id.img_sendBtn);
        SendingImgProgressBar = findViewById(R.id.SendingImgProgressBar);


        dbHelper dbHelper=new dbHelper(this,"Wallpaper.db",null,1);
        int Wallpaper_res_id=dbHelper.getWallpaper_Data(1);
        if(Wallpaper_res_id!=0){
            findViewById(R.id.background_chats).setBackgroundResource(Wallpaper_res_id);
        }
        dbHelper.close();

        Intent intent = getIntent();
        String receiver_Uid = intent.getStringExtra("Receiver_Uid");

        FirebaseFirestore.getInstance().collection("Users").document(receiver_Uid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        assert user != null;
                        String userImgUrl = user.imageUrl;
                        Glide.with(getApplicationContext()).load(userImgUrl).circleCrop().placeholder(R.drawable.user)
                                .into(receiver_image);
                    }
                });


        receiver_Name.setText(getIntent().getStringExtra("name_Receiver"));
        img_sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent();
                intent1.setType("image/*");
                intent1.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent1, "Select Picture"), 123);

            }
        });


        sendChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Chat_Dao chat_dao = new Chat_Dao();
                String chat_Text = send_text.getText().toString();
                if (!chat_Text.isEmpty()) {
                    chat_dao.send_Chat(receiver_Uid, chat_Text);
                    send_text.clearFocus();
                    send_text.getText().clear();

                } else {
                    send_text.setError("Enter the message");
                    send_text.requestFocus();
                }
            }
        });
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Chats_activity.super.onBackPressed();
            }
        });
        setUpRecyclerAdapter(FirebaseAuth.getInstance().getCurrentUser().getUid(), receiver_Uid);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {
            if (data != null) {
                SendingImgProgressBar.setVisibility(View.VISIBLE);
                Uri Img_uri = data.getData();
                String img_url = Img_uri.toString();
                Intent intent = new Intent(getApplicationContext(), send_image_from_chats.class);
                intent.putExtra("Receiver_Uid", getIntent().getStringExtra("Receiver_Uid"));
                intent.putExtra("img_uri", img_url);
                startActivity(intent);
                SendingImgProgressBar.setVisibility(View.GONE);
            }
        }
    }

    private void setUpRecyclerAdapter(String senderUid, String ReceiverUid) {
        final String sender_Room = senderUid + ReceiverUid;
        final String receiverRoom = ReceiverUid + senderUid;
        chat_dao = new Chat_Dao();
        CollectionReference chatsCollections = chat_dao.chatsDocumentRef.collection(sender_Room);

        Query query = chatsCollections.orderBy("time", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Chats> options = new FirestoreRecyclerOptions.Builder<Chats>()
                .setQuery(query, Chats.class).build();

        chats_adapter1 = new Chats_Adapter1(options, this);

         recyclerView= findViewById(R.id.chats_Details);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(chats_adapter1);
    }

    @Override
    protected void onStart() {
        super.onStart();
        chats_adapter1.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        chats_adapter1.stopListening();
    }

    @Override
    public void openImage(FirestoreRecyclerOptions<Chats> options, int position) {
        Intent intent = new Intent(getApplicationContext(), Chats_Image_Open.class);
        FirebaseUser current_User=FirebaseAuth.getInstance().getCurrentUser();
        Chats particular_chat=options.getSnapshots().getSnapshot(position).toObject(Chats.class);
        assert current_User != null;
        assert particular_chat != null;
        if (current_User.getUid().equals(particular_chat.sender_uId)) {
            intent.putExtra("name", "You");
        } else {
            intent.putExtra("name", new UserDao().UserNameById(particular_chat.sender_uId));
        }
        intent.putExtra("time", new Utils().convert(
                particular_chat.time
        ));
        intent.putExtra("image", particular_chat.getImageUrl());
        startActivity(intent);
    }

    @Override
    public void deleteChat(FirestoreRecyclerOptions<Chats> options, int position) {
        FirebaseUser current_User=FirebaseAuth.getInstance().getCurrentUser();
        Chats particular_chat=options.getSnapshots().getSnapshot(position).toObject(Chats.class);
        assert particular_chat != null;
        String senderuId = particular_chat.sender_uId;

        assert current_User != null;
        if (!senderuId.equals(current_User.getUid())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Chats_activity.this);
            builder.setTitle("Do you want to delete the message ? ");
            builder.setPositiveButton("Delete for me", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new Chat_Dao().deleteChat(senderuId, current_User.getUid(),
                            particular_chat.getRandomKey());
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else {
            long time_of_send = particular_chat.time;
            long time_diff = System.currentTimeMillis() - time_of_send;

            if (time_diff < (30 * 60 * 1000)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Chats_activity.this);
                builder.setTitle("Do you want to delete the message ? ");
                builder.setPositiveButton("Delete for everyone", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Chat_Dao().deleteChat(current_User.getUid(),
                                getIntent().getStringExtra("Receiver_Uid"),
                                particular_chat.getRandomKey());
                        new Chat_Dao().deleteChat(current_User.getUid(),
                                getIntent().getStringExtra("Receiver_Uid"),
                                particular_chat.getRandomKey());

                    }
                });
                builder.setNegativeButton("Delete for me", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Chat_Dao().deleteChat(current_User.getUid(),
                                getIntent().getStringExtra("Receiver_Uid"),
                                particular_chat.getRandomKey());

                    }
                });

                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(Chats_activity.this);
                builder.setTitle("Do you want to delete the message ?");

                builder.setPositiveButton("Delete for me", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Chat_Dao().deleteChat(current_User.getUid(),
                                getIntent().getStringExtra("Receiver_Uid"),
                                particular_chat.getRandomKey());

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chats_activity_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.view_contact:
            case R.id.mute_notifications:
            case R.id.media:

            case R.id.report:
            case R.id.Block:
            case R.id.search:

            case R.id.Export_chats:
            case R.id.call_audio:
            case R.id.call_video:
            case R.id.Add_shortcut:
                Toast.makeText(getApplicationContext(), "This Feature will be available soon..", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.wallpaper:
                Intent intent = new Intent(Chats_activity.this, Wallpaper_Activity.class);
                startActivity(intent);
                return true;

            case R.id.Clear_Chat:
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference reference = db.collection("Chats");
                reference.document("Our chats").collection(
                        FirebaseAuth.getInstance().getCurrentUser().getUid() + getIntent().getStringExtra("Receiver_Uid")
                ).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                            Chats chats = snapshot.toObject(Chats.class);
                            reference.document("Our chats").collection(
                                    FirebaseAuth.getInstance().getCurrentUser().getUid() + getIntent().getStringExtra("Receiver_Uid")
                            ).document(chats.getRandomKey()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                }
                            });
                        }
                    }
                });
            default:
                return super.onOptionsItemSelected(item);

        }
    }

}