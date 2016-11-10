/*
package xslPackage.XslTest;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
*/
package com.example.administrator.xsltest.camera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

public class CameraWithSavingPreviewActivity extends Activity {

    private static final File OUTPUT_FILE = new File("/sdcard/usecamera.jpg");

    private Camera mCamera = null;

    private Object lockObject = new Object();
    private byte[] mData = null;
    private byte[] mTempData = null;

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(new CameraPreview(this));

        mCamera = Camera.open();
        mCamera.setPreviewCallback(new PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                synchronized (lockObject) {
                    mData = data;
                }
            }
        });

    }

    /**
     * プレビュー画像の保存
     */
    private void savePreviewImage() {
        if (mData != null) {
            if (mTempData == null) {
                mTempData = new byte[mData.length];
            }

            synchronized (lockObject) {
                for (int i = 0; i < mTempData.length; i++) {
                    mTempData[i] = mData[i];
                }
            }

            Size size = mCamera.getParameters().getPreviewSize();
            int[] out = decodeYUV(mTempData, size.width, size.height);

            Bitmap bmp = Bitmap.createBitmap(out, size.width, size.height, Config.ARGB_8888);

            FileOutputStream stream = null;
            try {
                stream = new FileOutputStream(OUTPUT_FILE);

                bmp.compress(CompressFormat.PNG, 90, stream);

                stream.flush();
                stream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            Thread th = new Thread() {
                public void run() {
                    savePreviewImage();
                }
            };
            th.start();

            return true;
        }
        return super.onTouchEvent(event);
    }

    private class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

        SurfaceHolder mHolder;

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
            mCamera.setParameters(parameters);
            mCamera.startPreview();

        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // if(mCamera != null) return;

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

    /**
     * YUV420SP を RGB565 に変換
     * 
     * @param yuvDataArray
     * @param width
     *            画像の横幅
     * @param height
     *            画像の高さ
     * @return ARGBのint型配列
     * @throws NullPointerException
     * @throws IllegalArgumentException
     */
    private static int[] decodeYUV(byte[] yuvDataArray, int width, int height)
            throws NullPointerException, IllegalArgumentException {
        int size = width * height;

        if (yuvDataArray == null)
            throw new NullPointerException("buffer yuvDataArray is null");
        if (yuvDataArray.length < size)
            throw new IllegalArgumentException("buffer yuvDataArray size " + yuvDataArray.length
                    + " < minimum " + size * 3 / 2);

        int[] out = new int[size];

        int i, j;
        int Y, Cr = 0, Cb = 0;
        for (j = 0; j < height; j++) {
            int pixelIdx = j * width;
            int jDiv2 = j >> 1;

            for (i = 0; i < width; i++) {
                Y = yuvDataArray[pixelIdx];
                if (Y < 0)
                    Y += 255;
                if ((i & 0x1) != 1) {
                    int cOff = size + jDiv2 * width + (i >> 1) * 2;
                    Cb = yuvDataArray[cOff];
                    if (Cb < 0) {
                        Cb += 127;
                    } else {
                        Cb -= 128;
                    }
                    Cr = yuvDataArray[cOff + 1];
                    if (Cr < 0) {
                        Cr += 127;
                    } else {
                        Cr -= 128;
                    }
                }

                // 赤成分
                int R = Y + Cr + (Cr >> 2) + (Cr >> 3) + (Cr >> 5);
                if (R < 0) {
                    R = 0;
                } else if (R > 255) {
                    R = 255;
                }

                // 緑成分
                int G = Y - (Cb >> 2) + (Cb >> 4) + (Cb >> 5) - (Cr >> 1) + (Cr >> 3) + (Cr >> 4)
                        + (Cr >> 5);
                if (G < 0) {
                    G = 0;
                } else if (G > 255) {
                    G = 255;
                }

                // 青成分
                int B = Y + Cb + (Cb >> 1) + (Cb >> 2) + (Cb >> 6);
                if (B < 0) {
                    B = 0;
                } else if (B > 255) {
                    B = 255;
                }

                // 各成分の合成
                out[pixelIdx++] = 0xff000000 + (B << 16) + (G << 8) + R;
            }
        }

        return out;
    }
}