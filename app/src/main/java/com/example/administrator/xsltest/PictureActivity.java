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
package com.example.administrator.xsltest;

import android.app.Activity;
import android.content.ContentResolver;
//import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
//import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class PictureActivity extends Activity {

    private Cursor mCursor = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_main);       
    }
    //---------------------------------------------显示图像
	public void Btn_ShowImageClickHandler(View v){		
		//使用 ContentResolver方法,可以实现获取/注册/删除
        ContentResolver cr = getContentResolver();
        //要获取的列的字符串数组
        String[] columns = {
            MediaStore.Images.Media.DATA,         // 画像的文件路径
            MediaStore.Images.Media.DISPLAY_NAME  // 画像的文件名称
        };
        //获取外部存储器中图像的文件路径和显示名称
        //从数据库获取信息(参数1:   EXTERNAL_CONTENT_URI表示外部存储器中存储多媒体信息的数据库URI)
        //                参数2:   指定了前面所讲述的获取的对象列
        //                参数3,4: 查询条件
        //                参数5:   排序
        mCursor = cr.query(MediaStore.Images.Media.INTERNAL_CONTENT_URI /*EXTERNAL_CONTENT_URI*/,
                           columns,
                           null,
                           null,
                           MediaStore.Images.Media.DEFAULT_SORT_ORDER);
        //因为光标的初始位置不确定,先移动至最开始处
        if(mCursor != null) {
            mCursor.moveToFirst();
            showImage();
        }
	}
	//---------------------------------------------前图像
	public void Btn_PrevImageClickHandler(View v){		
		if (mCursor != null) {
            if (!mCursor.moveToPrevious()) {
                // 光标的位置移动到前一个
                mCursor.moveToLast();
            }
            showImage();
        }
	}
	//---------------------------------------------后图像
	public void Btn_NextImageClickHandler(View v){		
		if (mCursor != null) {
            if (!mCursor.moveToNext()) {
                // 光标的位置移动到后一个
                mCursor.moveToFirst();
            }
            showImage();
        }
	}
    //-------------------------------------------画像表示を行う
    void showImage() {
        int columnIndex;
        String columnName;

        // 从光标的当前位置获取图像的显示名称
        columnIndex = mCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
        columnName = mCursor.getString(columnIndex);
        // 更新图像的显示名称
        ((TextView)findViewById(R.id.DisplayName)).setText(columnName);
        // 从当前光标的位置获取图像的文件路径
        columnIndex = mCursor.getColumnIndex(MediaStore.Images.Media.DATA);
        columnName = mCursor.getString(columnIndex);
        //对从文件路径获取的位图进行解码并显示
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(columnName, options);

        ImageView iv = (ImageView)findViewById(R.id.CurrentImage);

        // ImageViewのサイズに合わせてX方向とY方向それぞれのスケーリング値を求める
        int scale_x = 1 + (options.outWidth  / iv.getWidth());
        int scale_y = 1 + (options.outHeight / iv.getHeight());

        // スケーリング値を設定し実際に画像のデコードを行う
        options.inSampleSize = Math.max(scale_x, scale_y);
        options.inJustDecodeBounds = false;
        bmp = BitmapFactory.decodeFile(columnName, options);

        // デコードした画像がImageViewより大きければImageView側でスケーリングを行う
        if ((iv.getWidth() < options.outWidth) || (iv.getHeight() < options.outHeight)) {
            iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } else {
            iv.setScaleType(ImageView.ScaleType.CENTER);
        }

        iv.setImageBitmap(bmp);
    }
}
