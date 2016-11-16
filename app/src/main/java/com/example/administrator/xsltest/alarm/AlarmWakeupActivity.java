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
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.administrator.xsltest.MainActivity;
import com.example.administrator.xsltest.R;

public class AlarmWakeupActivity extends Activity {
	private MediaPlayer player;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_wakeup);
        Time t = new Time();
        t.setToNow();

        // アラームをループ再生
        player = MediaPlayer.create(this, R.raw.test_cbr);
        player.setLooping(true);
        player.start();

		Button stop_btn = (Button)findViewById(R.id.stop_btn);
		stop_btn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				// アラームを止める
				player.stop();
				
				Intent intent = new Intent(AlarmWakeupActivity.this, MainActivity.class);
				startActivity(intent);
			}
		});
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		player.stop();
		finish();
	}
}
