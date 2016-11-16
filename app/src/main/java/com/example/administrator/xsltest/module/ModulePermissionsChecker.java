package com.example.administrator.xsltest.module;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * Created by Administrator on 2016/9/26.
 */

public class ModulePermissionsChecker {
    /*
    private final Context mContext;

    // 传入context
    public ModulePermissionsChecker(Context context) {
        mContext = context.getApplicationContext();
    }
    */

    // 判断权限集合

    /**
     *
     * @param permissions string[]
     * @return true:need permission     false:don't need permission
     */
    public boolean lacksPermissions(String... permissions) {
        for (String permission : permissions) {
            if (lacksPermission(permission)) {
                LogUtils.i(permission);
                return true;
            }
        }
        return false;
    }

    // 判断是否缺少权限
    /**
     * check permission is need?
     * @param permission is need
     * @return true:need permission     false:don't need permission
     */
    private boolean lacksPermission(String permission) {
        // 判断权限类
        /*
        return ContextCompat.checkSelfPermission(mContext, permission) ==
                PackageManager.PERMISSION_DENIED;
                */
        return ContextCompat.checkSelfPermission(AppContext.getContext(), permission) !=
                PackageManager.PERMISSION_GRANTED;
    }
}
