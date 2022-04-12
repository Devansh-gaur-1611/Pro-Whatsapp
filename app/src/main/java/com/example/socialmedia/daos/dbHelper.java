package com.example.socialmedia.daos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class dbHelper extends SQLiteOpenHelper {
    public dbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create Table Wallpaper/*name of table*/(SNo INTEGER primary key,Res_Id INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop Table if exists Wallpaper");
    }

    public boolean insertWallpaper(int sno, int res_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("SNo", sno);
        values.put("Res_Id", res_id);
        long result = db.insert("Wallpaper", null, values);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Boolean updateWallpaper(int sno, int res_id) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("Res_Id", res_id);


//        Cursor is like selecting the row
        Cursor cursor = DB.rawQuery("Select * from Wallpaper where SNo=? ",new String[]{String.valueOf(sno)});
        if (cursor.getCount() > 0) {
            long result = DB.update("Wallpaper", contentValues, "Sno=?", new String[]{String.valueOf(sno)});
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }
    public int getWallpaper_Data(int sno) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("Wallpaper", new String[]{"SNo", "Res_Id"}, "SNo=?", new String[]{String.valueOf(sno)},
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            return cursor.getInt(1);
        } else {
            Log.d("DEVA", "Some error occured");
            return 0;
        }
    }
}
