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
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.view.View.OnClickListener;

public class SysConfigActivity extends Activity {
	String ori;
	String navigation;
	String touch;
	String mnc;
	EditText et_output;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test1);
		// 获取应用界面中的界面组件
		Button bn = (Button)findViewById(R.id.Btn_Test1_11);
		et_output = (EditText) findViewById(R.id.editText_Output_1);
		bn.setText("获取设备信息");
		bn.setOnClickListener(new OnClickListener()
		{
			// 为按钮绑定事件监听器
			@Override
			public void onClick(View source)
			{
				// 获取系统的Configuration对象
				Configuration cfg = getResources().getConfiguration();
				String screen = cfg.orientation ==
					Configuration.ORIENTATION_LANDSCAPE 
					? "横向": "竖向";
				String mncCode = cfg.mnc + "";
				String naviName = cfg.orientation ==
					Configuration.NAVIGATION_NONAV
					? "没有方向控制" :
					cfg.orientation == Configuration.NAVIGATION_WHEEL
					? "滚轮控制方向" :
					cfg.orientation == Configuration.NAVIGATION_DPAD
					? "方向键控制方向" : "轨迹球控制方向";				
				String touchName = cfg.touchscreen == 
					Configuration.TOUCHSCREEN_NOTOUCH
					? "无" : "支持";				
				ori = "屏幕方向: " + screen + "\n";
				navigation = naviName + "\n";
				touch = "触摸屏: " + touchName + "\n";
				mnc = "移动网络代号: "+ mncCode + "\n";
				et_output.setText(ori+navigation+touch+mnc);
			}
		});
		bn = (Button)findViewById(R.id.Btn_Test1_12);
		bn.setText("屏幕翻转");		
		bn.setOnClickListener(new OnClickListener()
		{
			// 为按钮绑定事件监听器
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 获取系统的Configuration对象
				Configuration cfg = getResources().getConfiguration();
				//如果当前是横屏
				if(cfg.orientation == Configuration.ORIENTATION_LANDSCAPE)
				{
					SysConfigActivity.this.setRequestedOrientation(
							ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				}
				//如果当前是竖屏
				else if(cfg.orientation == Configuration.ORIENTATION_PORTRAIT)
				{
					SysConfigActivity.this.setRequestedOrientation(
							ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				}
			}			
		});		
	}		
}
