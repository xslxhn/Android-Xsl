package com.example.administrator.xsltest.nrftoolbox.sample;

import android.bluetooth.BluetoothDevice;

import com.example.administrator.xsltest.nrftoolbox.profile.BleManagerCallbacks;

/**
 * CSCManager管理器处理接收数据的扩展接口
 * 		在Manager里调用
 * 		在Service里实例化（发送本地广播到Activity）
 */

public interface SampleManagerCallbacks extends BleManagerCallbacks {

    // 接收解析
    void onSampleValueReceived(final BluetoothDevice device, double value);
}
