/*
说明：实现了在页面显示各种传感器的值
*/
package com.example.administrator.xsltest;

import java.lang.String;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
//-----指南针
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
//-----
/*
* */
public class SensorActivity extends Activity implements SensorEventListener
{
	//获取传感器管理器
    SensorManager sensorManager;
    //陀螺仪传感器
    private TextView mTvSensorValue1 = null;
    //照度传感器
    private TextView txtSensorLight = null;
    //加速度传感器
    private TextView txtSensorAccelerometer = null;
    //磁场传感器
    private TextView txtSensorMagnetic = null;
    //温度传感器
    private TextView txtSensorTemperature = null;
    //压力传感器
    private TextView txtSensorPressure = null;
    //指南针
    ImageView znzImage;
    float currentDegree = 0f;
    // 加速度传感器
    float[] accelerometerValues = new float[3];
    float[] magneticFieldValues = new float[3];
    // 地磁传感器
    //-----
    /*
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // 父方法
        super.onCreate(savedInstanceState);
        // 显示页面
        setContentView(R.layout.sersor_main);
        // 获取页面控件的引用---文本
        mTvSensorValue1 = (TextView) findViewById(R.id.TXT_ORIENTATION);
        txtSensorLight  = (TextView) findViewById(R.id.TXT_LIGHT);
        txtSensorAccelerometer = (TextView) findViewById(R.id.TXT_ACCELEROMETR);
        txtSensorMagnetic = (TextView) findViewById(R.id.TXT_MAGNETIC);
        txtSensorTemperature = (TextView) findViewById(R.id.TXT_TEMPERATURE);
        txtSensorPressure = (TextView) findViewById(R.id.TXT_PRESSURE);
        znzImage = (ImageView)findViewById(R.id.imageView_Arrow);
        // 获取传感器管理器的引用
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //
        mTvSensorValue1.setText("");
        txtSensorLight.setText("");
        txtSensorAccelerometer.setText("");
        txtSensorMagnetic.setText("");
        txtSensorTemperature.setText("");
        txtSensorPressure.setText("");
    }
    /*
     * @see android.app.Activity#onPause()
     */
    @Override
    public void onResume() {
        super.onResume();
        // 初始化传感器
        initSensor();
    }
    /*
     * @see android.app.Activity#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
    /*
     * @see android.app.Activity#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
    	sensorManager.unregisterListener(this);
    }
 // 以下是实现SensorEventListener接口必须实现的方法
 	// 当传感器的值发生改变时回调该方法
	@Override
 	public void onSensorChanged(SensorEvent event)
 	{
        // 提取数据
		float[] values = event.values;
        // 提取事件
		int sensorType=event.sensor.getType();
        //
        String str;
		switch(sensorType)
		{
		  case Sensor.TYPE_LIGHT:
               str = "照度传感器: "+String.valueOf(event.values[0])+"Lux";
			   txtSensorLight.setText(str);
			   break;
		  case Sensor.TYPE_ACCELEROMETER:
               accelerometerValues = values;
               str = "加速度传感器:" +
                      "\n    X->" +
                      values[0] +
                      "\n    Y->" +
                      values[1] +
                      "\n    Z->" +
                      values[2];
              txtSensorAccelerometer.setText(str);
	     	   break;
		  case Sensor.TYPE_MAGNETIC_FIELD:
              magneticFieldValues = values;
               str ="磁场传感器(uT):"+"\n    X->"+String.valueOf(event.values[0])+"\n    Y->"+String.valueOf(event.values[1])+"\n    Z->"+String.valueOf(event.values[2]);
			   txtSensorMagnetic.setText(str);
			   break;
		  case Sensor.TYPE_AMBIENT_TEMPERATURE:
               str = "温度传感器: "+String.valueOf(event.values[0])+"℃";
			   txtSensorTemperature.setText(str);
			   break;
		  case Sensor.TYPE_PRESSURE:
              str = "压力传感器: "+String.valueOf(event.values[0]);
			   txtSensorPressure.setText(str);
			   break;
		}
        // 根据加速度与磁场传感器换算出方向数据
        if(sensorType == Sensor.TYPE_ACCELEROMETER || sensorType == Sensor.TYPE_MAGNETIC_FIELD)
        {
            float[] orientationValues = new float[3];
            float[] R = new float[9];
            SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticFieldValues);
            SensorManager.getOrientation(R, orientationValues);
            //单位转换
            orientationValues[0] = (float) Math.toDegrees(orientationValues[0]);
            orientationValues[1] = (float) Math.toDegrees(orientationValues[1]);
            orientationValues[2] = (float) Math.toDegrees(orientationValues[2]);
            //
            str = "方向传感器:" + "\n    方向->"+String.valueOf(orientationValues[0])+"\n    前后倾斜度->"+String.valueOf(orientationValues[1])+"\n    左右倾斜度->"+String.valueOf(orientationValues[2]);
            mTvSensorValue1.setText(str);
            //-----指南针
            //创建旋转动画(反向旋转degree度)
            RotateAnimation ra = new RotateAnimation(currentDegree,
                    -orientationValues[0],
                    Animation.RELATIVE_TO_SELF,
                    0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);
            //设置动画的持续时间
            ra.setDuration(200);
            //运行动画
            znzImage.startAnimation(ra);
            currentDegree = -orientationValues[0];
            //-----
        }
 	}
 // 当传感器精度改变时回调该方法。
 	@Override
 	public void onAccuracyChanged(Sensor sensor, int accuracy)
 	{
 	}
    /**
     * 初始化传感器
     */
    private void initSensor() {        
        //-------------------------------------
        //获取取值的频度
        int sensorDelay;
        //sensorDelay = SensorManager.SENSOR_DELAY_FASTEST;	//最快频率(高耗电,影响其他性能)
        //sensorDelay = SensorManager.SENSOR_DELAY_GAME;	//适合游戏的频率
        //sensorDelay = SensorManager.SENSOR_DELAY_NORMAL;	//正常频率
        sensorDelay = SensorManager.SENSOR_DELAY_UI;		//普通用户界面频率(省电,延迟大,只适合普通小程序)
        //传感器类型->照度传感器
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), sensorDelay);
        //-------------------------------------
        //传感器类型->加速度传感器
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), sensorDelay);
      //-------------------------------------
        //传感器类型->磁场传感器
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), sensorDelay);
      //-------------------------------------
        //传感器类型->温度传感器
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE), sensorDelay);
      //-------------------------------------
        //传感器类型->压力传感器
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE), sensorDelay);
    }
    /*
     * 返回按钮单击句柄
     */
    public void Btn_ReturnMainClickHandler(View v){		
    	startActivity(new Intent(SensorActivity.this, MainActivity.class));
	}
}
