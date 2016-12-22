/*
 * Copyright (c) 2015, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.example.administrator.xsltest.nrftoolbox.hts;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.widget.TextView;

import com.example.administrator.xsltest.R;
import com.example.administrator.xsltest.nrftoolbox.hts.settings.SettingsActivity;
import com.example.administrator.xsltest.nrftoolbox.hts.settings.SettingsFragment;
import com.example.administrator.xsltest.nrftoolbox.profile.BleProfileService;
import com.example.administrator.xsltest.nrftoolbox.profile.BleProfileServiceReadyActivity;

import java.text.DecimalFormat;
import java.util.UUID;

/**
 * HTSActivity is the main Health Thermometer activity. It implements {@link HTSManagerCallbacks} to receive callbacks from {@link HTSManager} class. The activity supports portrait and landscape
 * orientations.
 */

/**
 * 功能：
 * 1，健康温度计（每按一下按键温度值更新）
 * 2，需要绑定
 * 3，特性 --- 指示方式
 */
public class HTSActivity extends BleProfileServiceReadyActivity<HTSService.RSCBinder> {
    @SuppressWarnings("unused")
    private final String TAG = "HTSActivity";

    //
    private static final String VALUE = "value";
    private static final DecimalFormat mFormattedTemp = new DecimalFormat("#0.00");
    // 控件指针
    private TextView mHTSValue;
    private TextView mHTSUnit;
    // 温度更新
    private Double mValueC;

    /**
     * 实例化抽象函数	---	创建View
     *
     * @param savedInstanceState contains the data it most recently supplied in {@link #onSaveInstanceState(Bundle)}. Note: <b>Otherwise it is null</b>.
     */
    @Override
    protected void onCreateView(final Bundle savedInstanceState) {
        // 设置Layout
        setContentView(R.layout.activity_feature_hts);
        // 指向控件
        setGUI();
    }

    /**
     * 非用户主动操作的意外销毁时被调用(内存不足/用户直接按Home键...),保存临时信息
     *
     * @param outState outState
     */
    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        // 保存温度值
        if (mValueC != null)
            outState.putDouble(VALUE, mValueC);
    }

    /**
     * 实例化抽象函数	---	初始化
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onInitialize(final Bundle savedInstanceState) {
        // 注册本地广播
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, makeIntentFilter());

        // 保存实例不为空
        if (savedInstanceState != null) {
            // 有VALUE字段数据被保存
            if (savedInstanceState.containsKey(VALUE))
                // 获取数据
                mValueC = savedInstanceState.getDouble(VALUE);
        }
    }

    /**
     * 销毁
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销本地广播
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

    private void setGUI() {
        // 温度	---	值
        mHTSValue = (TextView) findViewById(R.id.text_hts_value);
        // 温度	---	单位
        mHTSUnit = (TextView) findViewById(R.id.text_hts_unit);

        // 更新温度值
        if (mValueC != null)
            mHTSValue.setText(String.valueOf(mValueC));
    }

    /**
     * 恢复	---	界面恢复默认值
     */
    @Override
    protected void onResume() {
        super.onResume();
        setUnits();
    }

    @Override
    protected void setDefaultUI() {
        mValueC = null;
        mHTSValue.setText(R.string.not_available_value);

        setUnits();
    }

    /**
     * 设置单位
     */
    private void setUnits() {
        // 获取共享参数句柄
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        // 获取数据	---	单位
        final int unit = Integer.parseInt(preferences.getString(SettingsFragment.SETTINGS_UNIT, String.valueOf(SettingsFragment.SETTINGS_UNIT_DEFAULT)));

        // 根据设置的单位显示单位
        switch (unit) {
            case SettingsFragment.SETTINGS_UNIT_C:
                mHTSUnit.setText(R.string.hts_unit_celsius);
                break;
            case SettingsFragment.SETTINGS_UNIT_F:
                mHTSUnit.setText(R.string.hts_unit_fahrenheit);
                break;
            case SettingsFragment.SETTINGS_UNIT_K:
                mHTSUnit.setText(R.string.hts_unit_kelvin);
                break;
        }
        // 更新温度数值
        if (mValueC != null)
            setHTSValueOnView(mValueC);
    }

    /**
     * 回调实例化    --- Activity与Service服务绑定
     *
     * @param binder binder
     */
    @Override
    protected void onServiceBinded(final HTSService.RSCBinder binder) {
        // not used
    }

    /**
     * 回调实例化    --- Activity与Service服务解绑
     */
    @Override
    protected void onServiceUnbinded() {
        // not used
    }

    /**
     * 回调实例化    --- 返回日志记录器标题资源ID
     *
     * @return int
     */
    @Override
    protected int getLoggerProfileTitle() {
        return R.string.hts_feature_title;
    }

    /**
     * 回调实例化    --- About信息
     *
     * @return int
     */
    @Override
    protected int getAboutTextId() {
        return R.string.hts_about_text;
    }

    /**
     * 选择菜单	---	创建
     *
     * @param menu menu
     * @return int
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.settings_and_about, menu);
        return true;
    }

    /**
     * 选择菜单	---	按键解析
     *
     * @param itemId the menu item id
     * @return boolean
     */
    @Override
    protected boolean onOptionsItemSelected(final int itemId) {
        switch (itemId) {
            case R.id.action_settings:
                final Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    /**
     * 回调实例化	---	返回默认设备名称
     *
     * @return int
     */
    @Override
    protected int getDefaultDeviceName() {
        return R.string.hts_default_name;
    }

    /**
     * 回调实例化	---	获取滤波UUID
     *
     * @return int
     */
    @Override
    protected UUID getFilterUUID() {
        return HTSManager.HT_SERVICE_UUID;
    }

    /**
     * 回调实例化	---	获取服务类
     *
     * @return BleProfileService
     */
    @Override
    protected Class<? extends BleProfileService> getServiceClass() {
        return HTSService.class;
    }

    /**
     * 回调实例化	---	发现服务
     *
     * @param device                the device which services got disconnected
     * @param optionalServicesFound optionalServicesFound
     */
    @Override
    public void onServicesDiscovered(final BluetoothDevice device, boolean optionalServicesFound) {
        // this may notify user or show some views
    }

    /**
     * 更新可视化控件
     *
     * @param value         温度值
     */
    private void setHTSValueOnView(double value) {
        mValueC = value;
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final int unit = Integer.parseInt(preferences.getString(SettingsFragment.SETTINGS_UNIT, String.valueOf(SettingsFragment.SETTINGS_UNIT_DEFAULT)));

        switch (unit) {
            case SettingsFragment.SETTINGS_UNIT_F:
                value = value * 1.8 + 32;
                break;
            case SettingsFragment.SETTINGS_UNIT_K:
                value += 273.15;
                break;
            case SettingsFragment.SETTINGS_UNIT_C:
                break;
        }
        mHTSValue.setText(mFormattedTemp.format(value));
    }

    /**
     * 广播接收器
     */
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();

            if (HTSService.BROADCAST_HTS_MEASUREMENT.equals(action)) {
                final double value = intent.getDoubleExtra(HTSService.EXTRA_TEMPERATURE, 0.0f);
                // Update GUI
                setHTSValueOnView(value);
            }
        }
    };

    /**
     * 生成滤波器
     *
     * @return IntentFilter
     */
    private static IntentFilter makeIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(HTSService.BROADCAST_HTS_MEASUREMENT);
        return intentFilter;
    }
}
