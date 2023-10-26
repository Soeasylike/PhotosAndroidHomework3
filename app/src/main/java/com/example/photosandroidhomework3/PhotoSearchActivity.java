package com.example.photosandroidhomework3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.GridLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class PhotoSearchActivity extends AppCompatActivity {
    GridLayout gridLayout;
    List<BitmapData> bitmapData;
    BitmapSQLiteHelper bitmapSQLiteHelper;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchphotos);
        gridLayout=findViewById(R.id.gridlayout);
        bitmapData=new ArrayList<>();
        bitmapSQLiteHelper=new BitmapSQLiteHelper(this);
        bitmapData=bitmapSQLiteHelper.searchData();
        int len=bitmapData.size();
        for(int i=0;i<len;i++)
        {
            byte[] bytes;
            bytes=bitmapData.get(i).bytes;
            ImageView imageView=new ImageView(this);
            Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            imageView.setImageBitmap(bitmap);
            gridLayout.addView(imageView);
        }
    }
}
