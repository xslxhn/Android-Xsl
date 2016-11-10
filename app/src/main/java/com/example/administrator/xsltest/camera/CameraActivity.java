/*
说明:
1,软件需要手动开启照相等重要权限
*/

// http://www.cnblogs.com/plokmju/p/Android_SystemCamera.html
package com.example.administrator.xsltest.camera;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Exchanger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.hardware.Camera;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.administrator.xsltest.R;
import com.example.administrator.xsltest.module.ModuleFile;

//import com.google.android.mms.ContentType;
//--------------------------------------------------------------------------------------------------调用系统照相机
public class CameraActivity extends Activity {
    private Button btn_CameraPicture, btn_CameraVideo;
    private Button btn_RecordStart, btn_RecordStop;
    //private Button btn_AudioRecordStart, btn_AudioRecordStop;
    private ImageView iv_CameraImg;

    private static final String TAG = "main";
    //private static final String FILE_PATH = "/sdcard/XslTest.mp4";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;

    private static File file = null;
    private static Uri fileUri = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 装载页面
        setContentView(R.layout.camera_main);
        // 装载按键
        btn_CameraPicture = (Button) findViewById(R.id.Btn_CameraPicture);
        btn_CameraVideo = (Button) findViewById(R.id.Btn_CameraVideo);
        btn_RecordStart = (Button) findViewById(R.id.Btn_RecordStart);
        btn_RecordStop = (Button) findViewById(R.id.Btn_RecordStop);
        //btn_AudioRecordStart = (Button) findViewById(R.id.Btn_AudioRecordStart);
        //btn_AudioRecordStop = (Button) findViewById(R.id.Btn_AudioRecordStop);
        // 装载图板
        iv_CameraImg = (ImageView) findViewById(R.id.iv_CameraImg);
        // 开启按键监听
        btn_CameraPicture.setOnClickListener(click);
        btn_CameraVideo.setOnClickListener(click);
        btn_RecordStart.setOnClickListener(click);
        btn_RecordStop.setOnClickListener(click);
        //btn_AudioRecordStart.setOnClickListener(click);
        //btn_AudioRecordStop.setOnClickListener(click);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult: requestCode: " + requestCode
                + ", resultCode: " + requestCode + ", data: " + data);
        // 照相
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            Log.i(TAG, "Picture");
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    // 没有指定特定存储路径的时候
                    Log.d(TAG, "data is NOT null, file on default position.");
                    Toast.makeText(this, "Image saved to:\n" + data.getData(),
                            Toast.LENGTH_LONG).show();
                    if (data.hasExtra("data")) {
                        Bitmap thumbnail = data.getParcelableExtra("data");
                        iv_CameraImg.setImageBitmap(thumbnail);
                    }
                } else {
                    // 没有有效数据
                    Log.d(TAG,
                            "data IS null, file saved on target position.");
                    int width = iv_CameraImg.getWidth();
                    int height = iv_CameraImg.getHeight();
                    BitmapFactory.Options factoryOptions = new BitmapFactory.Options();

                    factoryOptions.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(fileUri.getPath(), factoryOptions);

                    int imageWidth = factoryOptions.outWidth;
                    int imageHeight = factoryOptions.outHeight;

                    // Determine how much to scale down the image
                    int scaleFactor = Math.min(imageWidth / width, imageHeight
                            / height);

                    // Decode the image file into a Bitmap sized to fill the
                    // View
                    factoryOptions.inJustDecodeBounds = false;
                    factoryOptions.inSampleSize = scaleFactor;
                    factoryOptions.inPurgeable = true;

                    Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
                            factoryOptions);

                    iv_CameraImg.setImageBitmap(bitmap);
                }
                //iv_CameraImg.setImageURI(fileUri);
            } else if (resultCode == RESULT_CANCELED) {
                // 用户取消照相
            } else {
                // 照相失败
            }
        }
        // 录像
        if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE)

        {
            Log.i(TAG, "Video");
            if (resultCode == RESULT_OK) {
                iv_CameraImg.setImageURI(fileUri);
            } else if (resultCode == RESULT_CANCELED) {
                // 用户取消录像
            } else {
                // 录像失败
            }
        }

    }

    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()) {
                // 指定相机拍摄照片保存地址
                case R.id.Btn_CameraPicture:
                    // 指定开启系统相机Action
                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //intent.addCategory(Intent.CATEGORY_DEFAULT);
                    // 把文件地址转换成Uri格式
                    fileUri = ModuleFile.getOutputFileUri(ModuleFile.MEDIA_FILETYPE_IMAGE);
                    // 设置系统相机拍摄照片完成后图片文件的存放地址
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    // 启动
                    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                    break;
                // 不指定相机拍摄照片保存地址
                case R.id.Btn_CameraVideo:
                    intent = new Intent();
                    // 指定开启系统相机的摄像
                    intent.setAction(MediaStore.ACTION_VIDEO_CAPTURE);
                    //intent.addCategory(Intent.CATEGORY_DEFAULT);
                    // 把文件地址转换成Uri格式
                    fileUri = ModuleFile.getOutputFileUri(ModuleFile.MEDIA_FILETYPE_VIDEO);
                    // 设置系统相机拍摄照片完成后图片文件的存放地址
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    // 设置图像质量为高
                    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                    // 启动
                    startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
                    break;
                case R.id.Btn_RecordStart:
                    MediaRecorderStart();
                    break;
                case R.id.Btn_RecordStop:
                    MediaRecorderStop();
                    break;
                default:
                    break;
            }
        }
    };
    //----------------------------------------------------------------------------------------------MediaRecorder
    // 利用 MediaRecorder 进行录音（只能生成音频文件不能进行数据处理）
    private static MediaRecorder mRecorder;

    private static void MediaRecorderStart() {
        try {
            file = ModuleFile.getOutputFile(ModuleFile.MEDIA_FILETYPE_MEDIA_RECORD);
            mRecorder = new MediaRecorder();
            // 设置录音的声音来源
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            // 设置录制声音的格式
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            // 设置声音编码格式
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            // 设置文件输出路径
            mRecorder.setOutputFile(file.getAbsolutePath());
            mRecorder.prepare();
            mRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void MediaRecorderStop() {
        if (file != null && file.exists()) {
            mRecorder.stop();
            mRecorder.release();
        }
    }
}
//--------------------------------------------------------------------------------------------------自定义照相机
/*

public class CameraActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 请求窗口特性:无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 添加窗口特性:全屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 检查摄像头是否存在
        PackageManager pm = getPackageManager();
        boolean hasCamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)||
                pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)||
                Build.VERSION.SDK_INT>Build.VERSION_CODES.GINGERBREAD||
                Camera.getNumberOfCameras()>0;
        // 根据检查结果进行布局
        if(!hasCamera){
            setContentView(R.layout.activity_main);
            return;
        }
        //
        //setContentView(R.layout.camera_main);
        //
        CameraPreview cameraPreview = new CameraPreview(this);
        setContentView(cameraPreview);
    }

    private class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

        SurfaceHolder mHolder;
        Camera mCamera;

        public CameraPreview(Context context) {
            super(context);

            mHolder = getHolder();
            mHolder.addCallback(this);
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(width, height);
            try {
            	mCamera.setParameters(parameters);
            } catch (Exception e) {
            }
            mCamera.startPreview();

        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (mCamera != null)
                return;
            mCamera = Camera.open();
            try {
                mCamera.setPreviewDisplay(holder);
            } catch (IOException exception) {
                mCamera.release();
                mCamera = null;
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }

    }

}
*/