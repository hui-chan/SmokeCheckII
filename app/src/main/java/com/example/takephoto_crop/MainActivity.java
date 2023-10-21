package com.example.takephoto_crop;


import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public  class MainActivity extends AppCompatActivity {
    private Button btn_2 , btn_3;
    private ImageView iv_image;
    private Uri iconUri;
    private Uri cropImageUri;
    private int a = 1;
    private int state;
    private String name = "crop_image.jpg";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(this.getClass().getName(), "onCreate");
        setContentView(R.layout.activity_main);
        btn_2 = findViewById(R.id.btn_2);
        btn_3 = findViewById(R.id.btn_3);
        iv_image = findViewById(R.id.iv_image);
        requestAllPower();//获取动态权限
        btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleP();
            }
        });
        btn_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openP();
            }
        });
    }
    public void seleP(){
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, 1);
    }

    public void openP(){
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //拍照图片保存到指定的路径
        name = "sdd" + name;
        File cropFilee = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),name);
        cropImageUri = Uri.fromFile(cropFilee);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,cropImageUri);
        startActivityForResult(intent, 3);
        state = 1;
    }


    //请求应用所需所有权限
    public void requestAllPower() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
    }

    /**
     * 裁减图片操作
     *
     * @param
     */
    private void startCropImage(Uri uri) {
        if(state == 1){
            state = 0;
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 使图片处于可裁剪状态
        intent.putExtra("crop", "true");
        // 裁剪框的比例（根据需要显示的图片比例进行设置）
        if (Build.MANUFACTURER.contains("HUAWEI")) {
            //硬件厂商为华为的，默认是圆形裁剪框，这里让它无法成圆形
            intent.putExtra("aspectX", 9999);
            intent.putExtra("aspectY", 9998);
        }else{
            //其他手机一般默认为方形
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
        }

        // 设置裁剪区域的形状，默认为矩形，也可设置为圆形，可能无效
        //intent.putExtra("circleCrop", true);

        // 让裁剪框支持缩放
        intent.putExtra("scale", true);

        // 传递原图路径
//        File cropFile = new File(Environment.getExternalStorageDirectory() + "/crop_image.jpg");
        File cropFile;
        name = a + "crop_image.jpg";
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            //虽然getExternalStoragePublicDirectory方法被淘汰了，但是不影响使用
            cropFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),name);
        }else{
            cropFile = new File(getExternalCacheDir(),name);
        }

        try {
            if (cropFile.exists()) {
                cropFile.delete();
            }
            cropFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        cropImageUri = Uri.fromFile(cropFile);
        cropFile = null;
        // 设置图片的输出格式
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        // return-data=true传递的为缩略图，小米手机默认传递大图，所以会导致onActivityResult调用失败
        intent.putExtra("return-data", false);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropImageUri);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropImageUri);

        startActivityForResult(intent, 2);
        intent = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        System.out.println("2222");
        if(state == 1){
            iconUri = cropImageUri;
            startCropImage(iconUri);
        }

        if (intent != null) {
            switch (requestCode) {
                // 将选择的图片进行裁剪
                case 1:
                    if (intent.getData() != null) {
                        iconUri = intent.getData();
                        startCropImage(iconUri);
                    }
                    break;
                case 3:
                    System.out.println("11");
                    System.out.println("22");
                    iconUri = cropImageUri;
                    startCropImage(iconUri);
                    break;
                case 2:
                    if (resultCode == RESULT_OK) {
//                           Bitmap bitmap = BitmapFactory.decodeStream
//                                  (getContentResolver()
//                                            .openInputStream(cropImageUri));
                        System.out.println("---" + cropImageUri);
                        iv_image.setImageURI(cropImageUri); // 将裁剪后的照片显示出来
                        cropImageUri = null;
                    }
                    a= a+1;
                    break;
                default:
                    break;

            }
        }
    }
}