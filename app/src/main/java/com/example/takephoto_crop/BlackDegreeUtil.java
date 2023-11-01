package com.example.takephoto_crop;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.Color;


public class BlackDegreeUtil extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    // 计算图片的林格曼黑度值
    public static int calculateImageLingemannBlackness(Bitmap image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int totalPixels = width * height;
        int totalBlackness = 0;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixelColor = image.getPixel(x, y);

                // 获取像素的RGB分量
                int red = Color.red(pixelColor);
                int green = Color.green(pixelColor);
                int blue = Color.blue(pixelColor);

                // 计算每个点的“黑度”,值越小越黑，值为0为纯黑，值为255为纯白
                int blackness = (red + green + blue) / 3;

                totalBlackness += blackness;
            }
        }

        // 计算平均黑度
        double averageBlackness = (double)totalBlackness /totalPixels;
        //根据平均黑度与255的比值确定林格曼黑度
        double rate=averageBlackness/255;
        if(rate<0.05)return 5;
        else if (rate<0.2) return 4;
        else if (rate<0.4) return 3;
        else if(rate<0.6)return 2;
        else if(rate<0.8)return 1;
        else return 0;
    }



}

