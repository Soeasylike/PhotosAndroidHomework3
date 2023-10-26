package com.example.photosandroidhomework3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BitmapSQLiteHelper extends SQLiteOpenHelper {

    private static final String DB_NAME="ImageSqlite.db";
    private static final String creat_usersqlite="create table if not exists imagedata(byetes BLOB not null,str1 varchar,str2 varchar,str3 varchar);";
    public BitmapSQLiteHelper(@Nullable Context context) {
        super(context, DB_NAME, null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(creat_usersqlite);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public long addData(BitmapData b){
        SQLiteDatabase db=getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put("byetes",b.bytes);
        cv.put("str1",b.str1);
        cv.put("str2",b.str2);
        cv.put("str3",b.str3);
        long uu=db.insert("imagedata",null,cv);
        return uu;//只要返回值不是-1则插入成功
    }
    public List<BitmapData> searchData(){
        SQLiteDatabase db=getReadableDatabase();
        String sql="select byetes,str1,str2,str3 from imagedata";
        List<BitmapData> datalist=new ArrayList<>();
        Cursor cs=db.rawQuery(sql,null);
        while(cs.moveToNext()){
            BitmapData ud=new BitmapData();
            ud.bytes=cs.getBlob(0);
            ud.str1=cs.getString(1);
            ud.str2=cs.getString(2);
            ud.str3=cs.getString(3);
            datalist.add(ud);
        }
        cs.close();
        return datalist;
    }

    public int deleteData(){
        SQLiteDatabase db=getWritableDatabase();
        return db.delete("imagedata",null,null);
    }
}

