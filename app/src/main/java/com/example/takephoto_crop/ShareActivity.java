package com.example.takephoto_crop;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.FileNotFoundException;

public class ShareActivity extends AppCompatActivity {
    private ImageView iv_poster_image;
    ImageButton btn_wechat;
    ImageButton btn_friendCircle;
    ImageButton btn_qq;

    Bitmap posterBitmap;
    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        iv_poster_image = findViewById(R.id.iv_poster_image);
        btn_wechat=findViewById(R.id.btn_wechat);
        btn_friendCircle=findViewById(R.id.btn_friendCircle);
        btn_qq=findViewById(R.id.btn_qq);
        Intent intent = getIntent();
        //从Intent当中根据key取得value
        int blackDegree = intent.getIntExtra("black_degree",-1);
        Bitmap image = MainActivity.imageBitmap;
        Bitmap poster_background = BitmapFactory.decodeResource(this.getResources(),R.drawable.posterbackground);

        //缩放检测图片
        double w=poster_background.getWidth()*0.7;
        double h=w;
        image=ImageUtil.scaleWithWH(image,w,h);

        //将检测图片放置于背景图片的中间
        posterBitmap=ImageUtil.createWaterMaskCenter(poster_background, image);

        //添加文字
        posterBitmap=ImageUtil.drawTextToTopCenter(this, posterBitmap, "黑度检测",60, Color.BLACK,20,60);
        posterBitmap=ImageUtil.drawTextToBottomCenter(this, posterBitmap, "林格曼黑度值为："+blackDegree,60, Color.BLACK,20,60);

        //将app图标放置于海报右下角
        Bitmap icon= BitmapFactory.decodeResource(this.getResources(),R.drawable.icon);
        icon=ImageUtil.scaleWithWH(icon,w/6,h/6);//缩放
        posterBitmap=ImageUtil.createWaterMaskRightBottom(this, posterBitmap, icon,0,0);

        //将海报显示出来
        iv_poster_image.setImageBitmap(posterBitmap);

        btn_wechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    jumpWechat();
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        btn_friendCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpCircle();
            }
        });

        btn_qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    jumpQQ();
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });


    }

    //分享微信好友
    void jumpWechat() throws FileNotFoundException {

        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.tencent.mm","com.tencent.mm.ui.tools.ShareImgUI");
        intent.setComponent(comp);
        intent.setAction("android.intent.action.SEND");
        intent.setType("image/*");
        //intent.putExtra(Intent.EXTRA_TEXT,"我是文字");
        Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), posterBitmap, null,null));
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(intent);
    }

    //分享微信朋友圈
    void jumpCircle(){

        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI");
        intent.setComponent(comp);
        intent.setAction("android.intent.action.SEND");
        intent.setType("image/*");
        //intent.putExtra(Intent.EXTRA_TEXT,"我是文字");
        Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), posterBitmap, null,null));
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(intent);
    }

    //分享QQ好友
    void jumpQQ() throws FileNotFoundException {

        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity");
        intent.setComponent(comp);
        intent.setAction("android.intent.action.SEND");
        intent.setType("image/*");
        //intent.putExtra(Intent.EXTRA_TEXT,"我是文字");
        Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), posterBitmap, null,null));
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(intent);
    }






}