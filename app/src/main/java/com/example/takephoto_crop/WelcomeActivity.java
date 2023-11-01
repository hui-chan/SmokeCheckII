package com.example.takephoto_crop;



import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

public class WelcomeActivity extends AppCompatActivity {
     private ImageView iv_welcome;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        handler.sendEmptyMessageDelayed(0,4000);
        iv_welcome=findViewById(R.id.iv_welcome);
        //根据屏幕缩放
        int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;//屏幕高度
        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;//屏幕宽度
        Bitmap welcomeImage = BitmapFactory.decodeResource(this.getResources(),R.drawable.welcome);
        welcomeImage=ImageUtil.scaleWithWH(welcomeImage,screenWidth,screenHeight);

        iv_welcome.setImageBitmap(welcomeImage);

    }
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            Intent intent=new Intent(WelcomeActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
            super.handleMessage(msg);
        }
    };




}