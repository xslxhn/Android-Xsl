package com.example.administrator.xsltest;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * Description:
 * <br/>website: <a href="http://www.crazyit.org">crazyit.org</a>
 * <br/>Copyright (C), 2001-2014, Yeeku.H.Lee
 * <br/>This program is protected by copyright laws.
 * <br/>Program Name:
 * <br/>Date:
 * @author Yeeku.H.Lee kongyeeku@163.com
 * @version 1.0
 */
public class CalPrime extends Activity
{
	static final String UPPER_NUM = "upper";
	EditText etNum;
	CalThread calThread;	
	EditText et_output;
	
	private Handler et_out_Handler;
	List<Integer> nums;
	// 定义一个线程类
	class CalThread extends Thread
	{
		public Handler mHandler;

		public void run()
		{
			Looper.prepare();
			mHandler = new Handler()
			{
				// 定义处理消息的方法
				@Override
				public void handleMessage(Message msg)
				{
					if(msg.what == 0x123)
					{
						int upper = msg.getData().getInt(UPPER_NUM);
						nums = new ArrayList<Integer>();
						// 计算从2开始、到upper的所有质数
						outer:
						for (int i = 2 ; i <= upper ; i++)
						{
							// 用i处于从2开始、到i的平方根的所有数
							for (int j = 2 ; j <= Math.sqrt(i) ; j++)
							{
								// 如果可以整除，表明这个数不是质数
								if(i != 2 && i % j == 0)
								{
									continue outer;
								}
							}
							nums.add(i);
						}
						// 使用Toast显示统计出来的所有质数
						/*
						Toast.makeText(CalPrime.this , nums.toString()
							, Toast.LENGTH_LONG).show();
						*/
						//发送空消息
						et_out_Handler.sendEmptyMessage(0x0001);						
					}
				}
			};
			Looper.loop();
		}
	}	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test1);
		//监听按键---计算质数
		Button button;
		button = (Button)findViewById(R.id.Btn_Test1_11);
		button.setText("计算质数");	
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// 创建消息
				Message msg = new Message();
				msg.what = 0x123;
				Bundle bundle = new Bundle();
				bundle.putInt(UPPER_NUM ,
					Integer.parseInt(etNum.getText().toString()));
				msg.setData(bundle);
				//
				et_output.setText("正在计算......");
				// 向新线程中的Handler发送消息
				calThread.mHandler.sendMessage(msg);
			}
		});
		//监听按键---JAVA支持语言
		button = (Button)findViewById(R.id.Btn_Test1_12);
		button.setText("JAVA支持语言");	
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Locale[] localeList=Locale.getAvailableLocales();
				for(int i=0;i<localeList.length;i++)
				{				
					Editable edit = et_output.getText();//获取EditText的文字
					edit.insert(edit.length(),localeList[i].getDisplayCountry()+"="+localeList[i].getCountry()+
					          " " +localeList[i].getDisplayLanguage()+"="+localeList[i].getLanguage()+"\n");//光标所在位置插入文字
				}
			}
		});
		//输入文本框初始化		
		etNum = (EditText)findViewById(R.id.Et_Test1_Input_1);
		etNum.setText("");
		//文本框的输入键盘设为数字键盘
		etNum.setInputType(InputType.TYPE_CLASS_NUMBER);
		//输出文本框初始化
		et_output = (EditText)findViewById(R.id.editText_Output_1);
		et_output.setText("");
		//新建消息处理
		et_out_Handler = new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				if(msg.what==0x0001)
				{
					et_output.setText("总个数: "+String.valueOf(nums.size())+"\n"+nums.toString());
				}
			}
		};
		//新建线程
		calThread = new CalThread();			
		//启动线程
		calThread.start();
	}
}
