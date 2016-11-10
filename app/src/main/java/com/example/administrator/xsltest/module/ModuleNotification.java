package com.example.administrator.xsltest.module;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import com.example.administrator.xsltest.MainActivity;
import com.example.administrator.xsltest.R;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by XSL on 2016/11/8.
 */

public class ModuleNotification {
    //这个type是为了Notification更新信息的，这个不明白的朋友可以去搜搜，很多
    public static final String TYPE = "type";
    private static String NOTIFICATION_CLICK_ACTION = "notification_clicked";
    private static String NOTIFICATION_CANCEL_ACTION = "notification_cancelled";
    private static NotificationBroadcastReceive notificationBroadcastReceiver;
    private static NotificationCompat.Builder mBuilder;
    private static NotificationManager notificationManager;
    public static void Init() {
        Intent intent;
        // ------------------------------注册接收器
        IntentFilter notificationFilter = new IntentFilter();
        notificationFilter.addAction(NOTIFICATION_CLICK_ACTION);
        notificationFilter.addAction(NOTIFICATION_CANCEL_ACTION);
        notificationBroadcastReceiver = new NotificationBroadcastReceive();
        AppContext.getContext().registerReceiver(notificationBroadcastReceiver, notificationFilter);
        // ------------------------------设定PendingIntent
        // 跳转到指定Activity
        Intent intent1 = new Intent(AppContext.getContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(AppContext.getContext(), 0, intent1, 0);
        // 发送指定广播信息
        intent = new Intent(ModuleNotification.NOTIFICATION_CLICK_ACTION);
        PendingIntent pendingIntentClick = PendingIntent.getBroadcast(AppContext.getContext(), 0, intent, 0);
        intent = new Intent(ModuleNotification.NOTIFICATION_CANCEL_ACTION);
        PendingIntent pendingIntentCancel = PendingIntent.getBroadcast(AppContext.getContext(), 0, intent, 0);
        // ------------------------------ 通知
        // 1-获取状态通知栏管理
        notificationManager = (NotificationManager) AppContext.getContext().getSystemService(NOTIFICATION_SERVICE);
        // 2-实例化通知栏构造器
        mBuilder = new NotificationCompat.Builder(AppContext.getContext());
        // 3-对Builder进行配置
        mBuilder.setContentTitle("测试标题")
                .setContentText("测试内容")
                .setContentIntent(pendingIntentClick)
                .setDeleteIntent(pendingIntentCancel)
                // 点击后自动消失
                .setAutoCancel(true)
                .setTicker("测试通知来啦")
                .setWhen(System.currentTimeMillis())
                //.setShowWhen(true)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setOngoing(false)
                .setDefaults(Notification.DEFAULT_LIGHTS)
                .setSmallIcon(R.drawable.ic_launcher);
        // 设置提醒标识符
        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_SHOW_LIGHTS;
        //notification.ledARGB=0xff0000ff;
        //notification.ledOnMS=300;
        //notification.ledOffMS=300;
        // 显示

        //
        Toast.makeText(AppContext.getContext(), "消息启动", Toast.LENGTH_SHORT).show();
        //---------- PendingIntent
        /*
        Intent intentClick = new Intent();
        intentClick.setClass(AppContext.getContext(), NotificationBroadcastReceiver.class);
        intentClick.setAction(NOTIFICATION_CLICK_ACTION);
        PendingIntent pendingIntentClick = PendingIntent.getBroadcast(AppContext.getContext(), 0, intentClick, 0);
        */
    }

    public static void deInit() {
        AppContext.getContext().unregisterReceiver(notificationBroadcastReceiver);
    }

    public static void notifyShow(int id) {
        notificationManager.notify(id, mBuilder.build());
    }

    public static class NotificationBroadcastReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (NOTIFICATION_CLICK_ACTION.equals(action)) {
                //处理点击事件
                Toast.makeText(AppContext.getContext(), "点击", Toast.LENGTH_SHORT).show();
            }

            if (NOTIFICATION_CANCEL_ACTION.equals(action)) {
                //处理滑动清除和点击删除事件
                Toast.makeText(AppContext.getContext(), "删除", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
