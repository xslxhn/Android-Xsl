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


//import xslPackage.XslTest.R;
//import xslPackage.XslTest.R.id;
//import xslPackage.XslTest.R.layout;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.xsltest.R;


public class AlarmActivity extends Activity {

	private static final int SETTING_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;
	private TextView set_time;
	private boolean isSaved;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_main);

        Bundle extra = getIntent().getExtras();
        if( extra != null ) {
        	if( extra.getInt("setting") > 0 ) {
    			Toast.makeText(AlarmActivity.this, "Set Timer", Toast.LENGTH_LONG).show();
        	}
        }
    }
    @Override
    protected void onResume() {
    	super.onResume();

    	TextView no_time = (TextView)findViewById(R.id.no_time);
        no_time.setVisibility(View.VISIBLE);
        isSaved = false;
        int[] time = getSavedTime();
		if( time != null ) {
			isSaved = true;
	        set_time = (TextView)findViewById(R.id.setting_time);
			set_time.setText(String.format("%2d : %02d", time[0], time[1]));
			no_time.setVisibility(View.GONE);
		}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);

    	menu.add(0, SETTING_ID, 0, "Setting");
    	menu.add(0, DELETE_ID, 0, "Delete");

    	return true;
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	super.onMenuItemSelected(featureId, item);
    	
    	switch(item.getItemId()) {
    	case SETTING_ID:
    		Intent i = new Intent(AlarmActivity.this, AlarmSettingActivity.class);
    		startActivity(i);
    		break;
    	case DELETE_ID:
            final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
    		if( isSaved ) {
	    		// 保存されていれば、ダイアログを表示
	            dialog.setTitle("Confirm");
	            dialog.setMessage("Do you really delete your wake up Time ?");
	            dialog.setPositiveButton("Yes", new OnClickListener(){
	    			public void onClick(DialogInterface dialog, int which) {
	    				// 保存した時刻を削除する
	    				final SharedPreferences sp = getSharedPreferences(AlarmSettingActivity.FILE_NAME, MODE_PRIVATE);
	    				SharedPreferences.Editor editor = sp.edit();
	    				editor.clear();
	    				editor.commit();
	    				isSaved = false;
	    				set_time.setText("");
	    				((TextView)findViewById(R.id.no_time)).setVisibility(View.VISIBLE);
	    			}
	            });
	            dialog.setNegativeButton("No", null);
    		} else {
    			dialog.setTitle("No save");
    			dialog.setMessage("Nothing time");
    			dialog.setNeutralButton("OK", null);
    		}
            dialog.create().show();
    		break;    
    	}
    	
    	return true;
    }
    
    private int[] getSavedTime() {
		final SharedPreferences sp = getSharedPreferences(AlarmSettingActivity.FILE_NAME, MODE_PRIVATE);
		if( !sp.contains(AlarmSettingActivity.TIME_HOUR) ) {
			return null;
		}
		
		int[] time = new int[2];
		time[0] = sp.getInt(AlarmSettingActivity.TIME_HOUR, 0);
		time[1] = sp.getInt(AlarmSettingActivity.TIME_MINUTES, 0);
		
		return time;
    }
    
}
