package com.example.administrator.xsltest.media;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.administrator.xsltest.R;
import com.example.administrator.xsltest.module.LogUtils;

public class UseMediaPlayerActivity extends Activity {
	MediaPlayer mp = null;
	int seekTime = 0;  //時間   
	int totalTime = 0; //总時間
	boolean preparedFlg = false; // 再生準備
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.media_main);
		//加载源文件---音频
		mp = MediaPlayer.create(this, R.raw.test_cbr);
        //音频-->接收回调通知-->接收出错通知
		mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
			public boolean onError(MediaPlayer mp, int what, int extra) {
				switch(what) {
				// 未知错误
				case MediaPlayer.MEDIA_ERROR_UNKNOWN:
					LogUtils.i("MEDIA_ERROR_UNKNOWN");
					return true;
				// 多媒体服务器停止
				case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
					LogUtils.i("MEDIA_ERROR_SERVER_DIED");
					return true;
				// 多媒体数据异常
				case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
					LogUtils.i("MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK");
					return true;
				}
				return false;
			}
		});
		//音频-->接收回调通知-->接收播放结束的通知
		mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
			public void onCompletion(MediaPlayer mp){
				LogUtils.i("onCompletion");
				mp.seekTo(0); // 从头播放
			}
		});
	}
	//---------------------------------------------音频播放
	public void Btn_StartClickHandler(View v){		
		mp.start();
	}
	//---------------------------------------------音频暂停
	public void Btn_PauseClickHandler(View v){		
		mp.pause();
	}
	//---------------------------------------------音频前进
	public void Btn_FfClickHandler(View v){	
		//获取总时间
		totalTime = mp.getDuration();
		//获取当前时间
		seekTime = mp.getCurrentPosition();
		//当前时间前进5s
		if(seekTime + 5000 > totalTime){
			seekTime = totalTime;
		}
		else {
			seekTime += 5000;
		}
		//执行
		mp.seekTo(seekTime);
	}
	//---------------------------------------------音频后退
	public void Btn_RewClickHandler(View v){	
		//获取总时间
		totalTime = mp.getDuration(); 
		//获取当前时间
		seekTime = mp.getCurrentPosition();
		//当前时间后退5s
		if(seekTime < 5000){
			seekTime = 0; 
		}
		else {
			seekTime -= 5000;
		}
		//执行
		mp.seekTo(seekTime);
	}
	@Override
    protected void onDestroy() {
    	super.onDestroy();
    	// 終了
        mp.stop();
        // 解放
        mp.release(); 
    }
}