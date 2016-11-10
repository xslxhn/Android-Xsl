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
package com.example.administrator.xsltest.alarm;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TimePicker;

import com.example.administrator.xsltest.MainActivity;
import com.example.administrator.xsltest.R;

public class AlarmSettingActivity extends Activity {

	public static final String FILE_NAME = "wakeup_file";
	public static final String TIME_HOUR = "hour";
	public static final String TIME_MINUTES = "minitutes";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_setting);

        final TimePicker tp = (TimePicker)findViewById(R.id.setting_time);
        tp.setIs24HourView(true);

        // 保存されていたら、時間を設定
		final SharedPreferences sp = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
		if( sp.contains(TIME_HOUR) ) {
			tp.setCurrentHour(sp.getInt(TIME_HOUR, tp.getCurrentHour()));
			tp.setCurrentMinute(sp.getInt(TIME_MINUTES, tp.getCurrentMinute()));
		}

		// 時間を保存
        Button save_btn = (Button)findViewById(R.id.save_btn);
        save_btn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {

				// 設定時間を保存
				SharedPreferences.Editor editor = sp.edit();
				editor.putInt(TIME_HOUR, tp.getCurrentHour());
				editor.putInt(TIME_MINUTES, tp.getCurrentMinute());
				editor.commit();

				// 現在の時刻を取得
		        Time t = new Time();
		        t.setToNow();
		        long now_time = t.toMillis(false);

		        // アラーム起動時刻を設定
				t.hour = tp.getCurrentHour();
				t.minute = tp.getCurrentMinute();
				t.second = 0;
				long next_time = t.toMillis(false);

				// 翌日の時間に設定
				if( now_time > next_time ) {
					next_time += AlarmManager.INTERVAL_DAY;
				}

				// AlarmManagerのインスタンスを取得
				// ※直接インスタンス化しない
		        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
		        PendingIntent sender = PendingIntent.getActivity(AlarmSettingActivity.this, 0,
		        		new Intent(AlarmSettingActivity.this, AlarmWakeupActivity.class), PendingIntent.FLAG_CANCEL_CURRENT);

		        // アラームを設定
		        am.setRepeating(AlarmManager.RTC_WAKEUP, next_time, AlarmManager.INTERVAL_DAY, sender);

				Intent i = new Intent(AlarmSettingActivity.this, MainActivity.class);
				i.putExtra("setting", 1);
				startActivity(i);
				finish();
			}
        });
	}
}
