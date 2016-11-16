package com.example.administrator.xsltest.media;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.VideoView;

import com.example.administrator.xsltest.R;
import com.example.administrator.xsltest.module.LogUtils;

public class UseVideoViewActivity extends Activity {
	VideoView vv = null;
	int seekTime = 0;  //時間   
	int totalTime = 0; //总時間
	boolean preparedFlg = false; // 再生準備
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_main);
		
        //加载VideoView
		vv = (VideoView) findViewById(R.id.videoView1);
		//加载源文件--视频
		String dst = "android.resource://"+ this.getPackageName() +"/"+R.raw.sample_mp4;
		vv.setVideoPath(dst);	
		//下面android:resource://是固定的，org.dengzh是我的包名，R.raw.movie_1是id名称 
		//vv.setVideoURI(Uri.parse("android.resource://org.dengzh/"+R.raw.movie_1)); 
		// 视频-->接收回调通知-->接收出错通知
		vv.setOnErrorListener(new MediaPlayer.OnErrorListener() {
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
		//视频-->接收回调通知-->接收播放结束的通知
		vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			public void onCompletion(MediaPlayer mp) {
				LogUtils.i("onCompletion");
				vv.seekTo(0); // 从头播放
			}
		});
	}
	//---------------------------------------------视频播放
	public void Btn_VideoStartClickHandler(View v){		
		vv.start();
	}
	//---------------------------------------------视频暂停
	public void Btn_VideoPauseClickHandler(View v){		
		vv.pause();
	}
	//---------------------------------------------音频前进
	public void Btn_VideoFfClickHandler(View v){	
		//获取总时间
		totalTime = vv.getDuration();
		//获取当前时间
		seekTime = vv.getCurrentPosition();
		//当前时间前进5s
		if(seekTime + 5000 > totalTime){
			seekTime = totalTime;
		}
		else {
			seekTime += 5000;
		}
		//执行
		vv.seekTo(seekTime);
	}
	//---------------------------------------------音频后退
	public void Btn_VideoRewClickHandler(View v){	
		//获取总时间
		totalTime = vv.getDuration(); 
		//获取当前时间
		seekTime = vv.getCurrentPosition();
		//当前时间后退5s
		if(seekTime < 5000){
			seekTime = 0; 
		}
		else {
			seekTime -= 5000;
		}
		//执行
		vv.seekTo(seekTime);
	}
	@Override
    protected void onDestroy() {
    	super.onDestroy();
    	// 終了
        //mp.stop();
        //vv.stopPlayback();
        // 解放
        //mp.release(); 
    }
}