package com.example.takephoto_crop;


import static android.os.Environment.DIRECTORY_PICTURES;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public  class MainActivity extends AppCompatActivity {
    private Button btn_2 , btn_3, btn_4,btn_5;
    private TextView tv1;
    private ImageView iv_image;
    private ImageView iv_poster_image;
    private Uri tempUri;
    private Uri photoUri;//拍照所得照片的uri
    private Uri cropImageUri;//裁剪所得图片的uri
    private int blackDegree;
    public static Bitmap imageBitmap;




    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(this.getClass().getName(), "onCreate");
        setContentView(R.layout.activity_main);
        btn_2 = findViewById(R.id.btn_2);
        btn_3 = findViewById(R.id.btn_3);
        btn_4 = findViewById(R.id.btn_4);
        btn_5 = findViewById(R.id.btn_5);
        tv1=findViewById(R.id.tv1);
        iv_image = findViewById(R.id.iv_image);
        iv_poster_image = findViewById(R.id.iv_poster_image);

        requestAllPower();//获取动态权限

        //打开相册选择图片
        btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleP();
            }
        });

        //打开相机拍摄照片
        btn_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    openP();
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        //黑度检测
        btn_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blackDetection();
            }
        });

        //生成海报
        btn_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generatePoster();
            }
        });
    }

    //selete picture
    public void seleP(){
        tv1.setText("");//清空textview
        Log.d("tag","进入图库选择图片");
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, 1);
    }

    //open camera to take photo
    public void openP() throws FileNotFoundException {
        tv1.setText("");//清空textview
        Log.d("tag","打开相机拍摄照片");
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //拍照图片保存到指定的路径
        String photoName = "Smocheck"+System.currentTimeMillis() + ".jpg";
        File photo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),photoName);
        photoUri = Uri.fromFile(photo);
        Log.d("tag","照片存储到"+photoUri);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
        startActivityForResult(intent, 3);
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
        Log.d("tag","进入裁剪界面");
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
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
        File cropFile;
        String cropImageName = "Smocheck"+System.currentTimeMillis() + ".jpg";
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            //虽然getExternalStoragePublicDirectory方法被淘汰了，但是不影响使用
            cropFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),cropImageName);
        }else{
            cropFile = new File(getExternalCacheDir(),cropImageName);
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
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropImageUri);
        Log.d("tag","裁剪后图片存储到"+cropImageUri);
        startActivityForResult(intent, 2);
        intent = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.e("tag","进行界面跳转和传递数据");
        switch (requestCode) {
                //将图库选择的图片进行裁剪
                case 1:
                    Log.e("tag","将图库选择的图片进行裁剪");
                    if (intent != null){
                        if (intent.getData() != null) {
                            tempUri = intent.getData();
                            startCropImage(tempUri);
                        }
                    }
                    break;
                //将拍照所得的图片进行裁剪
                case 3:
                    Log.e("tag","拍摄完毕，将拍照所得的图片进行裁剪");
                    //将图片插入系统图册
                    try {
                        String photoName = "Smocheck"+System.currentTimeMillis() + ".jpg";
                        imageBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(photoUri));
                        saveToSystemGallery(imageBitmap,photoName);
                        startCropImage(photoUri);
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }

                    break;
                //裁剪完毕，退出裁剪界面
                case 2:
                    Log.e("tag","裁剪完毕");
                    if (resultCode == RESULT_OK) {
                        //将图片插入系统图册
                        try {
                            String photoName = "Smocheck"+System.currentTimeMillis() + ".jpg";
                            imageBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(cropImageUri));
                            saveToSystemGallery(imageBitmap,photoName);
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }

                        System.out.println("---" + cropImageUri);
                        iv_image.setImageURI(cropImageUri); // 将裁剪后的照片显示出来
                        cropImageUri = null;
                    }
                    break;
                default:
                    break;

        }

    }

    //黑度检测方法
    public void blackDetection(){
        Toast.makeText(getApplicationContext(),"黑度检测",Toast.LENGTH_SHORT).show();
        //在这里调用黑度检测方法
        BitmapDrawable bmpDrawable = (BitmapDrawable) iv_image.getDrawable();
        Bitmap bitmap = bmpDrawable.getBitmap();
        blackDegree= BlackDegreeUtil.calculateImageLingemannBlackness(bitmap);
        String text=String.valueOf(blackDegree);
        tv1.setText("林格曼黑度值为："+text);
    }


    //保存位图到本地
    public void saveToSystemGallery(Bitmap bmp,String fileName) throws FileNotFoundException {
        // 首先保存图片
        //调用 getExternalStoragePublicDirectory()获得目录，保存公共文件到外部存储
        File appDir = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES),"SmoCheck");

        if (!appDir.exists()) {
            appDir.mkdir();
        }
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), fileName, null);
        //高版本的android对文件权限的管控抓的很严格,理论上两个应用之间的文件传递现在都应该是用FileProvider去实现,而不能用Uri.fromFile(File file)
        Uri uri= FileProvider.getUriForFile(this, "com.example.takephoto_crop.fileProvider", file);
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));

    }

    public void generatePoster(){
        Intent intent = new Intent();
        intent.putExtra("black_degree",blackDegree);
        //前一个（MainActivity.this）是目前页面，后面一个是要跳转的下一个页面
        intent.setClass(MainActivity.this, ShareActivity.class);
        startActivity(intent);


    }



}