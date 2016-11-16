package com.example.administrator.xsltest.module;

import android.Manifest;
import android.app.Activity;
import android.os.Build;

/**
 * Created by XSL on 2016/11/10.
 */

public class ModuleInit {
    private static Boolean runFlag  =   false;
    //--------------------权限
    // 请求码(Activity退出时返回)
    private static final int REQUEST_PERMISSIONS = 0;
    // 所需的全部权限
    private static final String[] PERMISSIONS = new String[]{
            // 外部存储器读写
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            //Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
            // 通过访问CellID或Wifi获取位置信息
            Manifest.permission.ACCESS_COARSE_LOCATION,
            // 摄像头
            Manifest.permission.CAMERA,
            // 联网权限
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            //Manifest.permission.CHANGE_NETWORK_STATE,
            Manifest.permission.CHANGE_WIFI_MULTICAST_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.RECEIVE_BOOT_COMPLETED,
            // 录音权限
            Manifest.permission.RECORD_AUDIO,
            // 读取LOG权限
            //Manifest.permission.READ_LOGS,
            // 运行监控权限
            Manifest.permission.RECEIVE_BOOT_COMPLETED,
            Manifest.permission.WAKE_LOCK,
            // Phone
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.SEND_SMS,
            Manifest.permission.CALL_PHONE,
    };
    public static void init(){
        if(runFlag)
        {
            return;
        }
        runFlag=true;
        ModuleFile.init();
        LogUtils.init();
    }
    public static void onResume(Activity activity){
        ModulePermissionsChecker mPermissionsChecker;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mPermissionsChecker = new ModulePermissionsChecker();
            if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
                ModulePermissionsActivity.startActivityForResult(activity,REQUEST_PERMISSIONS, PERMISSIONS);
            }
        }
    }
}
