package com.example.socialmedia;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.socialmedia.Adapter.wallpaper_grid_adapter;
import com.example.socialmedia.daos.dbHelper;
import com.example.socialmedia.models.wallpaper;

import java.util.ArrayList;

public class Wallpaper_Activity extends AppCompatActivity {
    GridView gridView;
    int[] mwallpaper = {R.drawable.w1, R.drawable.w2, R.drawable.w3, R.drawable.w4, R.drawable.w5,
            R.drawable.w6, R.drawable.w7, R.drawable.w8, R.drawable.w9,
            R.drawable.w10, R.drawable.w11, R.drawable.w12};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper);
        gridView = findViewById(R.id.Grid_Wallpaper);

        ArrayList<wallpaper> wallpaperArrayList = new ArrayList<>();
        wallpaperArrayList.add(new wallpaper(R.drawable.w1));
        wallpaperArrayList.add(new wallpaper(R.drawable.w2));
        wallpaperArrayList.add(new wallpaper(R.drawable.w3));
        wallpaperArrayList.add(new wallpaper(R.drawable.w4));
        wallpaperArrayList.add(new wallpaper(R.drawable.w5));
        wallpaperArrayList.add(new wallpaper(R.drawable.w6));
        wallpaperArrayList.add(new wallpaper(R.drawable.w7));
        wallpaperArrayList.add(new wallpaper(R.drawable.w8));
        wallpaperArrayList.add(new wallpaper(R.drawable.w9));
        wallpaperArrayList.add(new wallpaper(R.drawable.w10));
        wallpaperArrayList.add(new wallpaper(R.drawable.w11));
        wallpaperArrayList.add(new wallpaper(R.drawable.w12));

        wallpaper_grid_adapter adapter = new wallpaper_grid_adapter(this, R.layout.wallpaper_view, wallpaperArrayList);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int Res_id = mwallpaper[position];
                dbHelper dbHelper = new dbHelper(getApplicationContext(), "Wallpaper.db", null, 1);
                if (dbHelper.insertWallpaper(1, Res_id)) {
                    Toast.makeText(getApplicationContext(), "Background set", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(getApplicationContext(),Users_In_RecyclerView.class);
                    startActivity(intent);
                    finishAffinity();

                } else {
                    if (dbHelper.updateWallpaper(1, Res_id)) {
                        Toast.makeText(getApplicationContext(), "Background Set", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(getApplicationContext(),Users_In_RecyclerView.class);
                        startActivity(intent);
                        finishAffinity();
                    } else {
                        Toast.makeText(getApplicationContext(), "Background not set", Toast.LENGTH_SHORT).show();
                    }
                }
                dbHelper.close();
            }
        });


    }
}