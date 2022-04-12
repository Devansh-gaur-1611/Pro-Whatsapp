package com.example.socialmedia;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.Adapter.PostAdapter;
import com.example.socialmedia.daos.postDao;
import com.example.socialmedia.models.Post;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements PostAdapter.OnClickedListener {
    FloatingActionButton fab;
    postDao postDao;
    PostAdapter postAdapter;
    private AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NotNull InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                mAdView.loadAd(adRequest);
            }
        });


        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Create_Post_Activity.class);
                startActivity(intent);
            }
        });

        setUpRecyclerAdapter();

    }

    private void setUpRecyclerAdapter() {
        postDao = new postDao();
        CollectionReference postsCollections = postDao.postCollection;
        Query query = postsCollections.orderBy("createdAt", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class).build();

        postAdapter = new PostAdapter(options, this);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(postAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        postAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        postAdapter.stopListening();
    }

    @Override
    public void onLikeClicked(String postId) {
        postDao.updatesLikes(postId);
    }

    @Override
    public void onImageClicked(Context context, FirestoreRecyclerOptions<Post> options, int position) {
        Intent intent = new Intent(getApplicationContext(), See_image.class);
        String img_user_url = Objects.requireNonNull(options.getSnapshots().getSnapshot(position).toObject(Post.class)).createdBy.imageUrl;
        String name_user = options.getSnapshots().getSnapshot(position).toObject(Post.class).createdBy.displayName;
        intent.putExtra("img_user_url", img_user_url);
        intent.putExtra("name_user", name_user);
        startActivity(intent);

    }


    @Override
    public void goToChat(String recieverUid) {
        if (!recieverUid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            Intent intent = new Intent(getApplicationContext(), Chats_activity.class);
            intent.putExtra("Reciever_Uid", recieverUid);
            startActivity(intent);
        } else {
            Toast.makeText(this, "You can't chat with yourself", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.three_dots_on_top, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent1 = new Intent(getApplicationContext(), Change_Image_Activity.class);
                startActivity(intent1);
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_signOut:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this, Sign_In.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            case R.id.menu_item_share:
                Toast.makeText(getApplicationContext(), "We will share the app ", Toast.LENGTH_SHORT).show();

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


}