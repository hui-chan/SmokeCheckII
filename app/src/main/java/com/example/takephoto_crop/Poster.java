package com.example.takephoto_crop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

public class Poster extends AppCompatActivity {
    private ImageView iv_poster_image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.poster);
        iv_poster_image = findViewById(R.id.iv_poster_image);
        Intent intent = getIntent();
        //从Intent当中根据key取得value
        int blackDegree = intent.getIntExtra("black_degree",-1);
        Bitmap image = MainActivity.imageBitmap;
        Bitmap poster_background = BitmapFactory.decodeResource(this.getResources(),R.drawable.background1);

        //缩放检测图片
        double w=poster_background.getWidth()*0.9;
        double h=w;
        image=ImageUtil.scaleWithWH(image,w,h);

        //将检测图片放置于背景图片的中间
        Bitmap posterBitmap=ImageUtil.createWaterMaskCenter(poster_background, image);

        //添加文字
        posterBitmap=ImageUtil.drawTextToTopCenter(this, posterBitmap, "黑度检测",60, Color.BLACK,20,60);
        posterBitmap=ImageUtil.drawTextToBottomCenter(this, posterBitmap, "林格曼黑度值为："+blackDegree,60, Color.BLACK,20,60);

        //将app图标放置于海报右下角
        Bitmap icon= BitmapFactory.decodeResource(this.getResources(),R.drawable.icon);
        icon=ImageUtil.scaleWithWH(icon,w/6,h/6);//缩放
        posterBitmap=ImageUtil.createWaterMaskRightBottom(this, posterBitmap, icon,0,0);

        //将海报显示出来
        iv_poster_image.setImageBitmap(posterBitmap);
    }
}