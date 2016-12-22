package com.example.administrator.xsltest.nrftoolbox.sample;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;

import com.example.administrator.xsltest.nrftoolbox.parser.TemperatureMeasurementParser;
import com.example.administrator.xsltest.nrftoolbox.profile.BleManager;

import java.util.Deque;
import java.util.LinkedList;
import java.util.UUID;

import no.nordicsemi.android.log.Logger;
import no.nordicsemi.android.nrftoolbox.utility.DebugLogger;

/**
 * 功能：
 * 1，设定需要使用的UUID
 * 2，BleManagerGattCallback回调处理
 */

public class SampleManager extends BleManager<SampleManagerCallbacks> {
    //
    private static final String TAG = "SampleManager";
    // 服务UUID   --- Bluetooth Service UUID
    public final static UUID SAMPLE_SERVICE_UUID = UUID.fromString("00001809-0000-1000-8000-00805f9b34fb");
    // 特征UUID   --- Bluetooth Characteristic UUID
    private static final UUID SAMPLE_MEASUREMENT_CHARACTERISTIC_UUID = UUID.fromString("00002A1C-0000-1000-8000-00805f9b34fb");

    // 特征
    private BluetoothGattCharacteristic mSampleCharacteristic;

    // 私有
    private final static int HIDE_MSB_8BITS_OUT_OF_32BITS = 0x00FFFFFF;
    private final static int HIDE_MSB_8BITS_OUT_OF_16BITS = 0x00FF;
    private final static int SHIFT_LEFT_8BITS = 8;
    private final static int SHIFT_LEFT_16BITS = 16;
    private final static int GET_BIT24 = 0x00400000;
    private final static int FIRST_BIT_MASK = 0x01;


    /**
     * 调用父类
     *
     * @param context context
     */
    public SampleManager(final Context context) {
        super(context);
    }


    /**
     * 回调实例化	---	获取GattCallback
     *
     * @return BleManagerGattCallback
     */
    @Override
    protected BleManagerGattCallback getGattCallback() {
        return mGattCallback;
    }

    /**
     * 蓝牙回调处理   --- 触发条件：连接、断开、服务发现、接收指示等
     */
    private final BleManagerGattCallback mGattCallback = new BleManagerGattCallback() {

        // 初始化gatt
        @Override
        protected Deque<Request> initGatt(final BluetoothGatt gatt) {
            final LinkedList<Request> requests = new LinkedList<>();
            requests.add(Request.newEnableIndicationsRequest(mSampleCharacteristic));
            return requests;
        }

        // 判断服务：当远端设备支持服务，特征提取，并返回true
        @Override
        protected boolean isRequiredServiceSupported(final BluetoothGatt gatt) {
            final BluetoothGattService service = gatt.getService(SAMPLE_SERVICE_UUID);
            if (service != null) {
                mSampleCharacteristic = service.getCharacteristic(SAMPLE_MEASUREMENT_CHARACTERISTIC_UUID);
            }
            return mSampleCharacteristic != null;
        }

        // 设备断开连接：特征清空
        @Override
        protected void onDeviceDisconnected() {
            mSampleCharacteristic = null;
        }

        // 特征指示：指示需要客户端回复确认，服务器收到确认后才发下一条指示
        @Override
        public void onCharacteristicIndicated(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            // log
            Logger.a(mLogSession, "\"" + TemperatureMeasurementParser.parse(characteristic) + "\" received");
            //
            try {
                // 获取温度值
                final double tempValue = decodeTemperature(characteristic.getValue());
                // 回调应用层接口
                mCallbacks.onSampleValueReceived(gatt.getDevice(), tempValue);
            } catch (Exception e) {
                DebugLogger.e(TAG, "Invalid temperature value", e);
            }
        }

        // 特征通知：服务器可以任意时间发送
        @Override
        public void onCharacteristicNotified(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            // log
            // 提取数据
            // 回调应用层接口
        }
    };

    //----------------------------------------------------------------------------------------------私有数据处理函数
    /**
     * This method decode temperature value received from Health Thermometer device First byte {0} of data is flag and first bit of flag shows unit information of temperature. if bit 0 has value 1
     * then unit is Fahrenheit and Celsius otherwise Four bytes {1 to 4} after Flag bytes represent the temperature value in IEEE-11073 32-bit Float format
     */
    private double decodeTemperature(byte[] data) throws Exception {
        double temperatureValue;
        byte flag = data[0];
        byte exponential = data[4];
        short firstOctet = convertNegativeByteToPositiveShort(data[1]);
        short secondOctet = convertNegativeByteToPositiveShort(data[2]);
        short thirdOctet = convertNegativeByteToPositiveShort(data[3]);
        int mantissa = ((thirdOctet << SHIFT_LEFT_16BITS) | (secondOctet << SHIFT_LEFT_8BITS) | (firstOctet)) & HIDE_MSB_8BITS_OUT_OF_32BITS;
        mantissa = getTwosComplimentOfNegativeMantissa(mantissa);
        temperatureValue = (mantissa * Math.pow(10, exponential));

		/*
         * Conversion of temperature unit from Fahrenheit to Celsius if unit is in Fahrenheit
		 * Celsius = (Fahrenheit -32) 5/9
		 */
        if ((flag & FIRST_BIT_MASK) != 0) {
            temperatureValue = (float) ((temperatureValue - 32) * (5 / 9.0));
        }
        return temperatureValue;
    }

    private short convertNegativeByteToPositiveShort(byte octet) {
        if (octet < 0) {
            return (short) (octet & HIDE_MSB_8BITS_OUT_OF_16BITS);
        } else {
            return octet;
        }
    }

    private int getTwosComplimentOfNegativeMantissa(int mantissa) {
        if ((mantissa & GET_BIT24) != 0) {
            return ((((~mantissa) & HIDE_MSB_8BITS_OUT_OF_32BITS) + 1) * (-1));
        } else {
            return mantissa;
        }
    }
    //----------------------------------------------------------------------------------------------
}
