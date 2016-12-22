package com.example.administrator.xsltest.nrftoolbox.sample;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;

import com.example.administrator.xsltest.MainActivity;
import com.example.administrator.xsltest.R;
//import com.example.administrator.xsltest.nrftoolbox.sample.SampleActivity;
//import com.example.administrator.xsltest.nrftoolbox.sample.SampleManager;
//import com.example.administrator.xsltest.nrftoolbox.sample.SampleManagerCallbacks;
//import com.example.administrator.xsltest.nrftoolbox.sample.SampleService;
import com.example.administrator.xsltest.nrftoolbox.profile.BleManager;
import com.example.administrator.xsltest.nrftoolbox.profile.BleProfileService;

import no.nordicsemi.android.log.Logger;

/**
 * 功能：
 * 		1，通过广播接收器，处理断开连接事件
 * 		2，实例化车轮数据接收、曲柄数据接收
 * 		3，创建、初始化、销毁、绑定、解绑（Activity与Service）
 */

public class SampleService extends BleProfileService implements SampleManagerCallbacks {
    // 数据包
    public static final String BROADCAST_SAMPLE_MEASUREMENT = "com.example.administrator.xsltest.nrftoolbox.sample.BROADCAST_SAMPLE_MEASUREMENT";
    // 数据包  --- 参数
    public static final String EXTRA_TEMPERATURE = "com.example.administrator.xsltest.nrftoolbox.sample.EXTRA_TEMPERATURE";

    // 断开连接（用于广播接收器的滤波器）
    private final static String ACTION_DISCONNECT = "com.example.administrator.xsltest.nrftoolbox.sample.ACTION_DISCONNECT";

    // ？？？
    private final static int NOTIFICATION_ID = 267;
    private final static int OPEN_ACTIVITY_REQ = 0;
    private final static int DISCONNECT_REQ = 1;

    private SampleManager mManager;

    //
    private final LocalBinder mBinder = new SampleService.SampleBinder();

    // service与activity的绑定接口
    public class SampleBinder extends LocalBinder {
        // empty
    }

    //  获取绑定器
    @Override
    protected LocalBinder getBinder() {
        return mBinder;
    }

    // 初始化管理器
    @Override
    protected BleManager<SampleManagerCallbacks> initializeManager() {
        return mManager = new SampleManager(this);
    }

    // 创建服务
    @Override
    public void onCreate() {
        super.onCreate();

        final IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_DISCONNECT);
        registerReceiver(mDisconnectActionBroadcastReceiver, filter);
    }

    // 销毁服务
    @Override
    public void onDestroy() {
        // when user has disconnected from the sensor, we have to cancel the notification that we've created some milliseconds before using unbindService
        cancelNotification();
        unregisterReceiver(mDisconnectActionBroadcastReceiver);

        super.onDestroy();
    }

    // activity与service重新绑定
    @Override
    protected void onRebind() {
        // when the activity rebinds to the service, remove the notification
        cancelNotification();
    }

    // activity与service解绑
    @Override
    protected void onUnbind() {
        // when the activity closes we need to show the notification that user is connected to the sensor
        createNotification(R.string.hts_notification_connected_message, 0);
    }

    // 回调实例化	---	测量接收
    @Override
    public void onSampleValueReceived(final BluetoothDevice device, final double value) {
        final Intent broadcast = new Intent(BROADCAST_SAMPLE_MEASUREMENT);
        broadcast.putExtra(EXTRA_DEVICE, device);
        broadcast.putExtra(EXTRA_TEMPERATURE, value);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);

        if (!mBinded) {
            // Here we may update the notification to display the current temperature.
            // TODO modify the notification here
        }
    }

    // 创建android通知
    private void createNotification(final int messageResId, final int defaults) {
        //final Intent parentIntent = new Intent(this, FeaturesActivity.class);
        final Intent parentIntent = new Intent(this, MainActivity.class);
        parentIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        final Intent targetIntent = new Intent(this, SampleActivity.class);

        final Intent disconnect = new Intent(ACTION_DISCONNECT);
        final PendingIntent disconnectAction = PendingIntent.getBroadcast(this, DISCONNECT_REQ, disconnect, PendingIntent.FLAG_UPDATE_CURRENT);

        // both activities above have launchMode="singleTask" in the AndroidManifest.xml file, so if the task is already running, it will be resumed
        final PendingIntent pendingIntent = PendingIntent.getActivities(this, OPEN_ACTIVITY_REQ, new Intent[] { parentIntent, targetIntent }, PendingIntent.FLAG_UPDATE_CURRENT);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentIntent(pendingIntent);
        builder.setContentTitle(getString(R.string.app_name)).setContentText(getString(messageResId, getDeviceName()));
        builder.setSmallIcon(R.drawable.ic_stat_notify_hts);
        builder.setShowWhen(defaults != 0).setDefaults(defaults).setAutoCancel(true).setOngoing(true);
        builder.addAction(new NotificationCompat.Action(R.drawable.ic_action_bluetooth, getString(R.string.hts_notification_action_disconnect), disconnectAction));

        final Notification notification = builder.build();
        final NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(NOTIFICATION_ID, notification);
    }

    // 撤销android通知
    private void cancelNotification() {
        final NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(NOTIFICATION_ID);
    }

    // 广播接收器	---	处理断开连接按钮
    private final BroadcastReceiver mDisconnectActionBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            Logger.i(getLogSession(), "[Notification] Disconnect action pressed");
            if (isConnected())
                getBinder().disconnect();
            else
                stopSelf();
        }
    };
}
