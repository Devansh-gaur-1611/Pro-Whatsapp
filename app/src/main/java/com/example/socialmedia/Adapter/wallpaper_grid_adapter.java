package com.example.socialmedia.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.socialmedia.R;
import com.example.socialmedia.models.wallpaper;

import java.util.List;

public class wallpaper_grid_adapter extends ArrayAdapter<wallpaper> {

    List<wallpaper> objects;
    public wallpaper_grid_adapter(@NonNull Context context, int resource, @NonNull List<wallpaper> objects) {
        super(context, resource, objects);
        this.objects=objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.wallpaper_view,parent,false);
        ImageView image_wallpaper=convertView.findViewById(R.id.image_wallpaper);

        image_wallpaper.setImageResource(getItem(position).getImageId());

        return convertView;
    }
}
