package com.example.administrator.xsltest.module;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;
//import java.util.logging.Handler;
import android.os.Handler;

/**
 * Created by Administrator on 2016/10/10.
 */

public class MonitorRunService extends Service {
    Timer timer = new Timer();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        timer.schedule(new TestTask(), 5000, 5000);
        Toast.makeText(this, "服务启动", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startID){
        //兼容版本
        return START_STICKY_COMPATIBILITY;
    }

    @Override
    public void  onStart(Intent intent,int startId){
        // 再次动态注册广播
        IntentFilter localIntentFilter = new IntentFilter("android.intent.action.USER_PRESENT");
        localIntentFilter.setPriority(Integer.MAX_VALUE);// 整形最大值
        ServiceReceiver searchReceiver = new ServiceReceiver();
        registerReceiver(searchReceiver, localIntentFilter);
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent localIntent = new Intent();
        localIntent.setClass(this, MonitorRunService.class);
        this.startService(localIntent);
    }

    class  TestTask extends TimerTask {

        @Override
        public void run() {
            handler.sendEmptyMessage(1);
        }
    }


    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){
                Toast.makeText(MonitorRunService.this,"服务运行",Toast.LENGTH_SHORT).show();
            }
        }
    };

    public class ServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            context.startService(new Intent(context, MonitorRunService.class));
        }
    }
}
