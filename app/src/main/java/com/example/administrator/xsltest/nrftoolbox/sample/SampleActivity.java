package com.example.administrator.xsltest.nrftoolbox.sample;

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
import com.example.administrator.xsltest.nrftoolbox.sample.settings.SettingsActivity;
import com.example.administrator.xsltest.nrftoolbox.sample.settings.SettingsFragment;
import com.example.administrator.xsltest.nrftoolbox.profile.BleProfileService;
import com.example.administrator.xsltest.nrftoolbox.profile.BleProfileServiceReadyActivity;

import java.text.DecimalFormat;
import java.util.UUID;

/**
 * 功能：样本
 * 流程：处理公共功能之外的事件(公共功能由继承类处理)
 * 1，接收本地广播信息
 * 2，更新显示控件
 * 3，实例化回调函数
 * 4，帮助信息字符串(实例化回调方式)
 * 5，菜单之设置键处理
 * 公共功能：不在本文件处理
 * 1，Connect按钮的处理（启动与注销服务）
 */
public class SampleActivity extends BleProfileServiceReadyActivity<SampleService.SampleBinder> {
    //private static final String TAG = "SampleActivity";

    // 控件指针
    private TextView mHTSValue;
    private TextView mHTSUnit;
    // 温度更新
    private Double mValueC;
    // 存储字段
    private static final String VALUE = "value";
    // 数据格式转换
    private static final DecimalFormat mFormattedTemp = new DecimalFormat("#0.00");

    //----------------------------------------------------------------------------------------------
    // 实例化抽象函数	---	创建View
    @Override
    protected void onCreateView(final Bundle savedInstanceState) {
        // 设置Layout
        setContentView(R.layout.activity_feature_hts);
        // 指向控件
        setGUI();
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
    //----------------------------------------------------------------------------------------------

    // 非用户主动操作的意外销毁时被调用(内存不足/用户直接按Home键...),保存临时信息
    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        // 保存
        if (mValueC != null)
            outState.putDouble(VALUE, mValueC);
    }

    //----------------------------------------------------------------------------------------------

    // 实例化抽象函数	---	初始化
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

    // 销毁
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销本地广播
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }
    //----------------------------------------------------------------------------------------------

    // 恢复	---	界面恢复默认值
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

    // 设置单位
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
    //----------------------------------------------------------------------------------------------

    // 选择菜单	---	创建
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.settings_and_about, menu);
        return true;
    }

    // 选择菜单	---	按键解析
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
    //----------------------------------------------------------------------------------------------

    // 回调实例化    --- Activity与Service服务绑定
    @Override
    protected void onServiceBinded(final SampleService.SampleBinder binder) {
        // not used
    }

    // 回调实例化    --- Activity与Service服务解绑
    @Override
    protected void onServiceUnbinded() {
        // not used
    }

    // 回调实例化    --- 返回日志记录器标题资源ID
    @Override
    protected int getLoggerProfileTitle() {
        return R.string.hts_feature_title;
    }

    // 回调实例化    --- About信息
    @Override
    protected int getAboutTextId() {
        return R.string.hts_about_text;
    }

    // 回调实例化	---	返回默认设备名称
    @Override
    protected int getDefaultDeviceName() {
        return R.string.hts_default_name;
    }

    // 回调实例化	---	获取滤波UUID
    @Override
    protected UUID getFilterUUID() {
        return SampleManager.SAMPLE_SERVICE_UUID;
    }

    // 回调实例化	---	获取服务类
    @Override
    protected Class<? extends BleProfileService> getServiceClass() {
        return SampleService.class;
    }

    // 回调实例化	---	发现服务
    @Override
    public void onServicesDiscovered(final BluetoothDevice device, boolean optionalServicesFound) {
        // this may notify user or show some views
    }

    //----------------------------------------------------------------------------------------------
    // 更新可视化控件
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

    // 广播接收器
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();

            if (SampleService.BROADCAST_SAMPLE_MEASUREMENT.equals(action)) {
                final double value = intent.getDoubleExtra(SampleService.EXTRA_TEMPERATURE, 0.0f);
                // Update GUI
                setHTSValueOnView(value);
            }
        }
    };

    // 生成滤波器
    private static IntentFilter makeIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SampleService.BROADCAST_SAMPLE_MEASUREMENT);
        return intentFilter;
    }
    //----------------------------------------------------------------------------------------------
}
