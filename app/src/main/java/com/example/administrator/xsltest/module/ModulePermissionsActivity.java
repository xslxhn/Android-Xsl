package com.example.administrator.xsltest.module;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.example.administrator.xsltest.R;

/**
 * Created by Administrator on 2016/9/26.
 */

public class ModulePermissionsActivity extends AppCompatActivity {
    // 权限授权
    public static final int PERMISSIONS_GRANTED = 0;
    // 权限
    public static final int PERMISSIONS_DENIED = 1;
    // 系统权限管理页面的参数
    private static final int PERMISSION_REQUEST_CODE=0;
    // 权限参数
    private static final String EXTRA_PERMISSIONS =
            "me.chunyu.clwang.permission.extra_permission";
    // 方案
    private static final String PACKAGE_URL_SCHEME = "package:";
    // 权限检测器
    private ModulePermissionsChecker mChecker;
    // 是否需要系统权限检测
    private boolean isRequireCheck;

    // 启动当前权限页面的公开接口
    public static void startActivityForResult(Activity activity, int requestCode, String... permissions) {
        Intent intent = new Intent(activity, ModulePermissionsActivity.class);
        // 打入参数
        intent.putExtra(EXTRA_PERMISSIONS, permissions);
        // 开启带返回值的Activity
        ActivityCompat.startActivityForResult(activity, intent, requestCode, null);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 容错
        if(getIntent()==null || !getIntent().hasExtra(EXTRA_PERMISSIONS)){
            // 抛出异常
            throw new RuntimeException("PermissionsActivity需要使用静态startActivityForResult方法启动!");
        }
        // 设定layout
        setContentView(R.layout.activity_permissions);
        // 权限检测器实例化
        mChecker = new ModulePermissionsChecker();
        // 启动系统监测
        isRequireCheck = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isRequireCheck) {
            // 获取权限列表
            String[] permissions = getPermissions();
            // 判断权限
            if (mChecker.lacksPermissions(permissions)) {
                // 请求权限
                requestPermissions(permissions);
            } else {
                // 全部权限都已获取
                allPermissionsGranted();
            }
        } else {
            isRequireCheck = true;
        }
    }

    // 返回传递的权限参数
    private String[] getPermissions() {
        return getIntent().getStringArrayExtra(EXTRA_PERMISSIONS);
    }

    // 请求权限兼容低版本
    private void requestPermissions(String... permissions) {
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
    }

    // 全部权限均已获取
    private void allPermissionsGranted() {
        setResult(PERMISSIONS_GRANTED);
        finish();
    }

    /**
     * 用户权限处理,
     * 如果全部获取, 则直接过.
     * 如果权限缺失, 则提示Dialog.
     *
     * @param requestCode  请求码
     * @param permissions  权限
     * @param grantResults 结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && hasAllPermissionsGranted(grantResults)) {
            isRequireCheck = true;
            allPermissionsGranted();
        } else {
            isRequireCheck = false;
            showMissingPermissionDialog();
        }
    }

    // 含有全部的权限
    private boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    // 显示缺失权限提示
    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ModulePermissionsActivity.this);
        builder.setTitle(R.string.help);
        builder.setMessage(R.string.string_help_text);

        // 拒绝, 退出应用
        builder.setNegativeButton(R.string.quit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setResult(PERMISSIONS_DENIED);
                finish();
            }
        });

        builder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startAppSettings();
            }
        });

        builder.show();
    }

    // 启动应用的设置
    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse(PACKAGE_URL_SCHEME + getPackageName()));
        startActivity(intent);
    }
}
