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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
/*
android系统包:
android.app         //提供高层的程序模型、提供基本的运行环境
android.os          //提供系统服务、消息传输和IPC机制
android.view        //提供基础的用户界面接口框架
android.content     //包含各种对设备上的数据进行访问和发布的类
android.database    //通过内容提供者浏览和操作数据库
android.graphics    //提供底层的图形库，包含画布、颜色过滤、点、矩阵
android.location    //提供定位和相关服务的类
android.media       //提供一些类，管理多种音频、视频的媒体接口
android.net         //提供网络访问的类
android.opengl      //提供OpenGL的工具
android.provider    //提供类访问Android的内容提供者
android.telephony   //提供拨打电话相关交互
android.util        //提供涉及工具性的方法,例如时间日期的操作
android.webkit      //提供默认浏览器操作接口
android.widget      //提供各种UI元素在应用程序中使用的类
*/
/*
import xslPackage.XslTest.MainActivity;
import xslPackage.XslTest.alarm.AlarmActivity;
import xslPackage.XslTest.book.book_Main;
import xslPackage.XslTest.camera.CameraWithShutterActivity;
import xslPackage.XslTest.google.GoogleLocationMap;
import xslPackage.XslTest.sqlite.*;
import xslPackage.XslTest.media.*;
import xslPackage.XslTest.Game.Plane.PlaneGame;
*/
//import android.media.audiofx.BassBoost.Settings;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
//-----AlertDialog-----
import android.os.Message;
//---------------------参数保存/读取/删除
import android.content.SharedPreferences;
//---------------------
import android.content.res.Resources;

import com.example.administrator.xsltest.Game.Plane.PlaneGame;
import com.example.administrator.xsltest.alarm.AlarmActivity;
import com.example.administrator.xsltest.book.book_Main;
import com.example.administrator.xsltest.camera.CameraActivity;
import com.example.administrator.xsltest.media.UseMediaPlayerActivity;
import com.example.administrator.xsltest.media.UseVideoViewActivity;
import com.example.administrator.xsltest.module.LogUtils;
import com.example.administrator.xsltest.module.ModuleInit;
import com.example.administrator.xsltest.nrftoolbox.bpm.BPMActivity;
import com.example.administrator.xsltest.nrftoolbox.cgms.CGMSActivity;
import com.example.administrator.xsltest.nrftoolbox.csc.CSCActivity;
import com.example.administrator.xsltest.nrftoolbox.dfu.DfuActivity;
import com.example.administrator.xsltest.nrftoolbox.gls.GlucoseActivity;
import com.example.administrator.xsltest.nrftoolbox.hrs.HRSActivity;
import com.example.administrator.xsltest.nrftoolbox.hts.HTSActivity;
import com.example.administrator.xsltest.nrftoolbox.proximity.ProximityActivity;
import com.example.administrator.xsltest.nrftoolbox.rsc.RSCActivity;
import com.example.administrator.xsltest.nrftoolbox.sample.SampleActivity;
import com.example.administrator.xsltest.nrftoolbox.template.TemplateActivity;
import com.example.administrator.xsltest.nrftoolbox.uart.UARTActivity;
import com.example.administrator.xsltest.sqlite.ParaSaveActivity;
import com.example.administrator.xsltest.sqlite.SQLiteActivity;

// 主活动
public class MainActivity extends AppCompatActivity {
    public static int i = 0;
    // 定义周期性显示的图片ID
    int[] imageIDs = new int[] { R.drawable.arrow1, R.drawable.arrow2,
            R.drawable.arrow3, R.drawable.arrow4, R.drawable.arrow5 };
    int currentImageId = 0;
    private ImageView show;
    private Handler ArrowHandler;

    // private static final int REQ_SYSTEM_SETTINGS=0;
    @SuppressLint("SimpleDateFormat")
    @Override
    // 生成Activity类时被调用的onCreate方法
    public void onCreate(Bundle savedInstanceState) {
        // 调用父类方法
        super.onCreate(savedInstanceState);
        // 设置Activity持有的界面资源
        setContentView(R.layout.activity_main);
        // ----------以下为用户代码----------
        Button button;
        int UseCount;
        TextView TxtV;
        // ---------------------------------使用次数累加
        Resources r = getResources();
        SharedPreferences sp = getSharedPreferences(
                (String) r.getText(R.string.SharedPreferencesName),
                MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        // 使用次数
        UseCount = sp.getInt((String) r.getText(R.string.SPN_UseCount), -1);
        if (UseCount == -1)
            UseCount = 1;
        else
            UseCount++;
        editor.putInt((String) r.getText(R.string.SPN_UseCount), UseCount);
        // 启动时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 " + "hh:mm:ss");
        editor.putString((String) r.getText(R.string.SPN_StartTime),
                sdf.format(new Date()));
        // 保存
        editor.commit();
        // 显示
        TxtV = (TextView) findViewById(R.id.TxtV_Main_UseCount);
        TxtV.setText(String.format("使用次数: %d. 开机时间: %s.", UseCount,
                sdf.format(new Date())));
        // ---------------------------------
        button = (Button) findViewById(R.id.Btn_ShowIntent);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // 显式INTENT-->启动浏览器
                Intent intent = new Intent();
                intent.setClassName("com.android.browser",
                        "com.android.browser.BrowserActivity");
                startActivity(intent);
            }
        });
        button = (Button) findViewById(R.id.Btn_HideIntent);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // 隐式INTENT-->启动浏览器
                Uri uri = Uri.parse("http://google.co.jp");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        // ----------
        show = (ImageView) findViewById(R.id.imageView_Arrow);
        ArrowHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0x1233) {
                    show.setImageResource(imageIDs[currentImageId++
                            % imageIDs.length]);
                }
            }
        };
        // 定义一个定时器,让该计时器周期性的执行指定任务
        new Timer().schedule(new TimerTask() {
            public void run() {
                // 发送空消息
                ArrowHandler.sendEmptyMessage(0x1233);
            }
        }, 0, 100);
    }

    // ---------------------------------------------传感器
    public void Btn_SensorClickHandler(View v) {
        startActivity(new Intent(MainActivity.this, SensorActivity.class));
    }

    // ---------------------------------------------游戏-飞机
    public void Btn_GamePlaneHandler(View v) {
        startActivity(new Intent(MainActivity.this, PlaneGame.class));
    }

    // ---------------------------------------------计算-质数
    public void Btn_CalPrimeHandler(View v) {
        startActivity(new Intent(MainActivity.this, CalPrime.class));
    }

    // ---------------------------------------------GPS
	/*
	public void Btn_GpsHandler(View v) {
		startActivity(new Intent(MainActivity.this, GpsActivity.class));
	}

	// ---------------------------------------------Google
	public void Btn_GoogleMapHandler(View v) {
		startActivity(new Intent(MainActivity.this, GoogleLocationMap.class));
	}
    */
    // ---------------------------------------------网络
    public void Btn_NetHandler(View v) {
        startActivity(new Intent(MainActivity.this, NetActivity.class));
    }

    // ---------------------------------------------朗读
    public void Btn_SpeechHandler(View v) {
        startActivity(new Intent(MainActivity.this, SpeechActivity.class));
    }

    // ---------------------------------------------计算-下载(异步任务)
    public void Btn_AsyncHandler(View v) {
        startActivity(new Intent(MainActivity.this, DownloadAsync.class));
    }

    // ---------------------------------------------参数保存
    public void Btn_ParaSaveHandler(View v) {
        startActivity(new Intent(MainActivity.this, ParaSaveActivity.class));
    }

    // ---------------------------------------------系统参数
    public void Btn_SysConfigClickHandler(View v) {
        startActivity(new Intent(MainActivity.this, SysConfigActivity.class));
    }

    // ---------------------------------------------定时器
    public void Btn_AlarmClickHandler(View v) {
        startActivity(new Intent(MainActivity.this, AlarmActivity.class));
    }

    // ---------------------------------------------照相机
    public void Btn_CameraClickHandler(View v) {
        startActivity(new Intent(MainActivity.this, CameraActivity.class));
        // startActivity(new Intent(MainActivity.this,
        // CameraWithSavingPreviewActivity.class));
        //startActivity(new Intent(MainActivity.this,CameraWithShutterActivity.class));
    }

    // ---------------------------------------------音频播放
    public void Btn_Mp3ClickHandler(View v) {
        startActivity(new Intent(MainActivity.this,
                UseMediaPlayerActivity.class));
    }

    // ---------------------------------------------音频播放
    public void Btn_VideoClickHandler(View v) {
        startActivity(new Intent(MainActivity.this, UseVideoViewActivity.class));
    }

    // ---------------------------------------------图片
    public void Btn_PictureClickHandler(View v) {
        startActivity(new Intent(MainActivity.this, PictureActivity.class));
    }

    // ---------------------------------------------SQLite
    public void Btn_SQLiteClickHandler(View v) {
        startActivity(new Intent(MainActivity.this, SQLiteActivity.class));
    }

    // ---------------------------------------------联系人
    public void Btn_BookClickHandler(View v) {
        startActivity(new Intent(MainActivity.this, book_Main.class));
    }
    // ---------------------------------------------Dialg相关解析
    public void Btn_DialogMainClickHandler(View v) {
        startActivity(new Intent(MainActivity.this, DialogActivity.class));
    }
    // ---------------------------------------------Telephone
    public void Btn_TelephoneHandler(View v) {
        startActivity(new Intent(MainActivity.this, TelephonyActivity.class));
    }
    // ---------------------------------------------音频解析
    public void Btn_AudioParseClickHandler(View v){
        startActivity(new Intent(MainActivity.this, AudioParseActivity.class));
    }
    // ---------------------------------------------蓝牙
    public void Btn_BluetoothHandler(View v) {
        startActivity(new Intent(MainActivity.this, SampleActivity.class));
    }
    public void Btn_Bluetooth1Handler(View v) {
        startActivity(new Intent(MainActivity.this, GlucoseActivity.class));
    }
    public void Btn_Bluetooth2Handler(View v) {
        startActivity(new Intent(MainActivity.this, BPMActivity.class));
    }
    public void Btn_Bluetooth3Handler(View v) {
        startActivity(new Intent(MainActivity.this, CGMSActivity.class));
    }
    public void Btn_Bluetooth4Handler(View v) {
        startActivity(new Intent(MainActivity.this, CSCActivity.class));
    }
    public void Btn_Bluetooth5Handler(View v) {
        startActivity(new Intent(MainActivity.this, DfuActivity.class));
    }
    public void Btn_Bluetooth6Handler(View v) {
        startActivity(new Intent(MainActivity.this, HRSActivity.class));
    }
    public void Btn_Bluetooth7Handler(View v) {
        startActivity(new Intent(MainActivity.this, HTSActivity.class));
    }
    public void Btn_Bluetooth8Handler(View v) {
        startActivity(new Intent(MainActivity.this, ProximityActivity.class));
    }
    public void Btn_Bluetooth9Handler(View v) {
        startActivity(new Intent(MainActivity.this, RSCActivity.class));
    }
    public void Btn_Bluetooth10Handler(View v) {
        startActivity(new Intent(MainActivity.this, TemplateActivity.class));
    }
    public void Btn_Bluetooth11Handler(View v) {
        startActivity(new Intent(MainActivity.this, UARTActivity.class));
    }
    // ---------------------------------------------图片按钮-->图片切换
    public void imageButton1ClickHandler(View v) {
        if (i == 0) {
            ((ImageButton) v).setImageResource(R.drawable.ic_launcher);
        } else {
            ((ImageButton) v).setImageResource(R.drawable.xsl_bmp);
        }
        i = ~i;
    }

    // 程序重启
    public void resetProgram() {
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        resetProgram();
    }

	/*
	public void refreshDate() {
		try {
			mAlertDialog = new AlertDialog.Builder(this)
					.setCancelable(false)
					.setTitle("数据恢复中")
					.create();
			mAlertDialog.show();
			List<ReceiveData> receiveData = mDatabaseHelper.getReceiveDataDao().queryForAll();
			lineChart = ChartUtils.getLineChart(this, Constants.MAX_X_YAL, receiveData);
			mRelativeLayout.removeAllViews();
			mRelativeLayout.addView(lineChart);
			floats = new ArrayList<>();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			mAlertDialog.dismiss();
		}
	}
	*/
}
