package com.example.photosandroidhomework3;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class PhotoTakeActivity extends AppCompatActivity {
    private boolean isPreview = false;//是否为预览状态，false表示未非预览状态
    private Camera camera;//定义一个摄像头对象
    private Button bt1;//拍照按钮
    private Button bt2;//预览按钮
    //------------------------------------------添加水印
    Bitmap realBitmap;//合成后真正的照片
    TextView textView;//用于显示位置信息
    String str0;//保存位置信息，要加到照片上的文字，也就是水印
    String str1;//保存位置信息，要加到照片上的文字，也就是水印
    String str2;//保存位置信息，要加到照片上的文字，也就是水印
    String str3;//保存位置信息，要加到照片上的文字，也就是水印
    //--------------------------------------存储到SQLite数据库里
    BitmapSQLiteHelper bitmapSQLiteHelper;
    BitmapData bitmapData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takephotos);
        bt1 = findViewById(R.id.bt1);//拍照按钮
        bt2 = findViewById(R.id.bt2);//预览按钮
        textView = findViewById(R.id.textView);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏显示
        if (!android.os.Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "请安装SD卡", Toast.LENGTH_LONG).show();
        }//判断手机是否安装了SD卡
        //SQLite数据库
        bitmapSQLiteHelper=new BitmapSQLiteHelper(this);
        bitmapData=new BitmapData();
        //位置
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //进行权限检查的代码
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,//指定GPS定位的提供者
                1000,//间隔时间
                1,//位置间隔1米
                new LocationListener() {//监听GPS定位信息是否改变
                    @Override
                    public void onLocationChanged(Location location) {//当GPS定位信息发生改变时调用的方法

                    }

                    public void onStatusChanged(String provider, int status, Bundle extras) {//当GPS定位状态发生改变时调用的方法

                    }

                    public void onProviderEnabled(String provider) {//定位提供者启动时调用

                    }

                    public void onProviderDisabled(String provider) {//定位提供者关闭时触发
                        throw new RuntimeException("Stub!");
                    }
                }
        );
        //获取最新的定位信息
        Location location = locationManager.getLastKnownLocation((LocationManager.GPS_PROVIDER));
        locationUpdates(location);//将最新的定位信息传递给这个方法

        //打开摄像头并预览
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surfaceview);//用于显示摄像头预览的
        SurfaceHolder surfaceHolder = surfaceView.getHolder();//获取SurfaceHolder
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//设置SurfaceView自己维护缓冲
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isPreview)//现在是非预览状态
                {
                    camera = Camera.open();//打开摄像头
                    isPreview = true;//设置为预览状态
                }
                try {
                    camera.setPreviewDisplay(surfaceHolder);//设置用于显示预览的SurfaceView
                    Camera.Parameters parameters = camera.getParameters();//获取摄像头参数
                    parameters.setPictureFormat(PixelFormat.JPEG);//设置图片为JPG图片
                    parameters.set("jpeg-quality", 80);//设置图片的质量
                    camera.setParameters(parameters);//重新设置摄像头参数
                    camera.startPreview();//开始预览
                    camera.autoFocus(null);//设置自动对焦
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        //获取拍照按钮，实现拍照功能
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (camera != null) {
                    camera.takePicture(null, null, jpeg);//进行拍照
                }
            }
        });
    }

    //获取经纬度
    public void locationUpdates(Location location) {
        if (location != null)//location是用来获取位置信息的
        {
            StringBuilder stringBuilder = new StringBuilder();//创建一个字符串构建器，用于记录定位信息
            stringBuilder.append("您的位置是：\n");
            stringBuilder.append("经度：");
            stringBuilder.append(location.getLongitude());
            stringBuilder.append("\n维度：");
            stringBuilder.append(location.getLatitude());
            stringBuilder.append("\n海拔：");
            stringBuilder.append(location.getAltitude());
            str0 = "您的位置是：";
            str1 = "经度：" + location.getLongitude();
            str2 = "纬度：" + location.getLatitude();
            str3 = "海拔：" + location.getAltitude();
            textView.setText(stringBuilder.toString());//显示到页面上
        } else {
            textView.setText("没有获取到GPS信息");
        }
    }

    //摄像
    final Camera.PictureCallback jpeg = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);//根据拍照所得数据创建位图
            camera.stopPreview();//停止预览
            isPreview = false;//设置为非预览状态
            realBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(realBitmap);
            Paint paint = new Paint();
            paint.setColor(Color.YELLOW);
            paint.setTextSize(90);
            paint.setAntiAlias(true);
            canvas.drawBitmap(bitmap, 0, 0, null);
            canvas.save();
            Canvas canvas1 = new Canvas(realBitmap);
            canvas1.drawText(str0, realBitmap.getWidth() / 2, realBitmap.getHeight() / 2, paint);
            canvas1.drawText(str1, realBitmap.getWidth() / 2, realBitmap.getHeight() / 2 + 100, paint);
            canvas1.drawText(str2, realBitmap.getWidth() / 2, realBitmap.getHeight() / 2 + 200, paint);
            canvas1.drawText(str3, realBitmap.getWidth() / 2, realBitmap.getHeight() / 2 + 300, paint);
            canvas1.save();
            //将Bitmap存储到SQLite数据库中
            int size=bitmap.getHeight()*bitmap.getWidth();
            ByteArrayOutputStream baos=new ByteArrayOutputStream(size);
            byte[] bytes1;
            realBitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
            bytes1=baos.toByteArray();
            bitmapData.bytes=bytes1;
            bitmapData.str1=str1;
            bitmapData.str2=str2;
            bitmapData.str3=str3;
            bitmapSQLiteHelper.addData(bitmapData);
            //获取SD卡中相片保存的位置
            File appDir = new File(Environment.getExternalStorageDirectory(), "/DCIM/Camera/");
            if (!appDir.exists()) {
                appDir.mkdir();//如果该目录不存在就创建该目录
            }
            String fileName = System.currentTimeMillis() + ".jpg";//将获取当前系统时间设置为照片名称
            File file = new File(appDir, fileName);//创建文件对象
            try {//保存拍到的图片
                FileOutputStream fos = new FileOutputStream(file);//创建一个文件输出流对象
                realBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);//将图片内容压缩为JPEG格式输出到输出流
                //bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);//将图片内容压缩为JPEG格式输出到输出流
                fos.flush();//将缓冲区中的数据全部写出到输出流中
                fos.close();//关闭文件输出流对象

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //将相片插入到系统图库
            try {
                MediaStore.Images.Media.insertImage(PhotoTakeActivity.this.getContentResolver(), file.getAbsolutePath(), fileName, null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            //最后通知图库更新
            PhotoTakeActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + "")));
            Toast.makeText(PhotoTakeActivity.this, "照片保存至：" + file, Toast.LENGTH_LONG).show();
            resetCamera();//重置相机预览
        }
    };

    private void resetCamera() {
        if (!isPreview) {
            camera.startPreview();//开启预览
            isPreview = true;
        }
    }

    protected void onPause() {
        super.onPause();
        //停止预览并释放摄像头资源
        if (camera != null) {
            camera.stopPreview();//停止预览
            camera.release();//释放资源
        }
    }
}