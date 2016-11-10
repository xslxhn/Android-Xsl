/*************************************************************
 * 权限:		android.permission.INTERNET
 * 说明1:	使用ServerSocket创建TCP服务器端
 ************************************************************/
package com.example.administrator.xsltest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.Thread.State;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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
public class NetActivity extends Activity
{
	EditText et_input,et_output;
	TextView txtV1;
	Button button,button11;	
	Bundle bundle;	
	BufferedWriter writer;
	Handler et_out_Handler;
	
	TcpClientThread tcp_th;	
	UdpThread udp_th;
	
	int UdpSendCount=0;
	//----------协议常量定义
	public static final int NET_PROTOCOL_TCP_CONNECT 		= 0x0001;
	public static final int NET_PROTOCOL_TCP_CONNECT_R		= 0x0801;
	public static final int NET_PROTOCOL_TCP_DISCONNECT 	= 0x0002;
	public static final int NET_PROTOCOL_TCP_DISCONNECT_R 	= 0x0802;
	public static final int NET_PROTOCOL_TCP_SEND			= 0x0003;
	public static final int NET_PROTOCOL_TCP_SEND_R			= 0x0803;
	public static final int NET_PROTOCOL_TCP_RECEIVE_R		= 0x0804;
	public static final int NET_PROTOCOL_TCP_PORT			= 8081;
	public static final String NET_PROTOCOL_TCP_IP			= "192.168.18.104";
	
	public static final int NET_PROTOCOL_UDP_CONNECT 		= 0x0011;
	public static final int NET_PROTOCOL_UDP_CONNECT_R 		= 0x0811;
	public static final int NET_PROTOCOL_UDP_DISCONNECT 	= 0x0012;
	public static final int NET_PROTOCOL_UDP_DISCONNECT_R 	= 0x0812;
	public static final int NET_PROTOCOL_UDP_SEND			= 0x0013;
	public static final int NET_PROTOCOL_UDP_SEND_R			= 0x0813;
	public static final int NET_PROTOCOL_UDP_RECEIVE_R		= 0x0814;
	public static final int NET_PROTOCOL_UDP_PORT			= 8080;
	public static final int NET_PROTOCOL_UDP_LOCACITY_PORT	= 8240;
	public static final String NET_PROTOCOL_UDP_IP			= "192.168.18.104";
	//----------
	
	static final String Tag = "TextSendTag";

	@Override
	public void onCreate(Bundle savedInstanceState)
	{		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test1);
		//----------------------------------
		button11 = (Button)findViewById(R.id.Btn_Test1_11);
		//----------------------------------第一行输入
		txtV1 = (TextView)findViewById(R.id.Tv_Test1_1);
		txtV1.setText("输入: "); 
		//输入文本框初始化		
		et_input = (EditText)findViewById(R.id.Et_Test1_Input_1);
		et_input.setText("Test...");
		et_input.setFocusable(true);
		et_input.setFocusableInTouchMode(true);;
		//文本框的输入键盘设为数字键盘
		//et_input.setInputType(InputType.TYPE_CLASS_NUMBER);		
		//----------------------------------第二行输入
		txtV1 = (TextView)findViewById(R.id.Tv_Test1_2);
		txtV1.setVisibility(View.INVISIBLE);
		et_input = (EditText)findViewById(R.id.Et_Test1_Input_2);
		et_input.setVisibility(View.INVISIBLE);
		//----------------------------------第三行输入
		txtV1 = (TextView)findViewById(R.id.Tv_Test1_3);
		txtV1.setVisibility(View.INVISIBLE);
		et_input = (EditText)findViewById(R.id.Et_Test1_Input_3);
		et_input.setVisibility(View.INVISIBLE); 
		//----------------------------------第一行按键
		//文本		
		txtV1 = (TextView)findViewById(R.id.Tv_Test1_11);
		txtV1.setText("TcpClient:");
		//监听按键---连接		
		button = (Button)findViewById(R.id.Btn_Test1_11);
		button.setText("连接");	
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				button = (Button)findViewById(R.id.Btn_Test1_11);			
				if(button.getText().toString() != "连接")
				{
					button = (Button)findViewById(R.id.Btn_Test1_12);	
					button.setEnabled(false);
					tcp_th.stop();
				}
				else
				{
					tcp_th.start();
				}				
			}
		});
		//监听按键---发送
		button = (Button)findViewById(R.id.Btn_Test1_12);
		button.setText("发送");	
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {		
				et_input = (EditText)findViewById(R.id.Et_Test1_Input_1);
				// 发送消息
				Message msg = new Message();
				msg.what = NET_PROTOCOL_TCP_SEND;
				msg.obj = et_input.getText().toString();
				// 向新线程中的Handler发送消息
				tcp_th.revHandler.sendMessage(msg);
				//清空文本框
				et_input.setText("");
			}
		});
		button.setEnabled(false);
		//监听按键---发送
		button = (Button)findViewById(R.id.Btn_Test1_13);
		button.setText("清空接手区");	
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {		
				et_output.setText("");
			}
		});	
		//----------------------------------第二行按键
		//文本		
		txtV1 = (TextView)findViewById(R.id.Tv_Test1_21);
		txtV1.setText("Udp:");
		//监听按键---连接		
		button = (Button)findViewById(R.id.Btn_Test1_21);
		button.setVisibility(View.INVISIBLE); 
		//监听按键---发送
		button = (Button)findViewById(R.id.Btn_Test1_22);
		button.setText("发送");	
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				et_input = (EditText)findViewById(R.id.Et_Test1_Input_1);
				// 发送消息
				Message msg = new Message();
				msg.what = NET_PROTOCOL_UDP_SEND;
				msg.obj = et_input.getText().toString();
				// 向新线程中的Handler发送消息
				udp_th.revHandler.sendMessage(msg);
			}
		});	
		button = (Button)findViewById(R.id.Btn_test1_23);
		button.setVisibility(View.INVISIBLE); 
		//----------------------------------第三行按键
		//文本		
		txtV1 = (TextView)findViewById(R.id.Tv_Test1_31);
		txtV1.setVisibility(View.INVISIBLE); 
		//监听按键---连接		
		button = (Button)findViewById(R.id.Btn_Test1_31);
		button.setVisibility(View.INVISIBLE); 
		button = (Button)findViewById(R.id.Btn_Test1_32);
		button.setVisibility(View.INVISIBLE); 
		button = (Button)findViewById(R.id.Btn_Test1_33);
		button.setVisibility(View.INVISIBLE); 
		//----------------------------------输出文本框初始化
		et_output = (EditText)findViewById(R.id.editText_Output_1);
		et_output.setText("说明1: 目前程序固定连接: "+NET_PROTOCOL_TCP_IP+":30000\n"+
		                  "说明2: 服务器发送的数据要用换行符结尾\n");
		//----------------------------------列表隐藏
		ListView LV=(ListView)findViewById(R.id.LV_Test1_1);
		LV.setVisibility(View.INVISIBLE);
		//----------------------------------
		//新建消息处理
		et_out_Handler = new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				//接收到消息,更新文本框
				if(msg.what==NET_PROTOCOL_TCP_RECEIVE_R)
				{
					et_output.append(msg.obj.toString()+"\n");
				}
				else if(msg.what==NET_PROTOCOL_UDP_RECEIVE_R)
				{
					et_output.append(msg.obj.toString()+"\n");
				}
				else if(msg.what==NET_PROTOCOL_TCP_CONNECT_R)
				{
					button = (Button)findViewById(R.id.Btn_Test1_11);
					button.setText("断开");
					button = (Button)findViewById(R.id.Btn_Test1_12);
					button.setEnabled(true);
				}
				else if(msg.what==NET_PROTOCOL_UDP_SEND_R)
				{
					button = (Button)findViewById(R.id.Btn_Test1_22);
					if(UdpSendCount<10000)	UdpSendCount++;
					else					UdpSendCount=0;
					button.setText("发送" + "(" + Integer.toString(UdpSendCount)+")");
				}
			}
		};
		//启动线程---TCP
		tcp_th = new TcpClientThread();
		//calThread.start();
		//启动线程---UDP
		udp_th = new UdpThread();
		udp_th.start();
	}
	//----------------------------------线程(UDP)
	class UdpThread extends Thread
	{			
		public Handler revHandler;	
		DatagramSocket socket=null;
        
		public void run()
		{
			try {
				//-----XSL-----指定端口后,返回再进入界面不好使,不知为什么
				socket = new DatagramSocket();	//NET_PROTOCOL_UDP_LOCACITY_PORT
				//启动一条子线程来读取服务器相应的数据
	            new Thread()
	            {       
	            	byte[] message = new byte[1024];  
	            	public void run()
	            	{            		
		                // 接收的字节大小，客户端发送的数据不能超过这个大小                   
	                    DatagramPacket datagramPacket = new DatagramPacket(message,message.length);  
	                    try {  
	                        while (true) {  
	                            // 准备接收数据  
	                        	socket.receive(datagramPacket);   
	                            //每当读到来自服务器的数据后,发送消息显示数据(线程无权操作界面)
	            				Message msg = new Message();
	            				msg.what = NET_PROTOCOL_UDP_RECEIVE_R;
	            				msg.obj=datagramPacket.getAddress().getHostAddress().toString()+
	            						":\n"+
	            						new String(datagramPacket.getData());
	            				et_out_Handler.sendMessage(msg);
	                        }  
	                    } catch (IOException e) {  
	                        e.printStackTrace();  
	                    }  
	            	}
	            }.start();
	            //为当前线程初始化Looper
	            Looper.prepare();
	            revHandler = new Handler()
				{
					// 定义处理消息的方法
					@Override
					public void handleMessage(Message msg)
					{
						//----------连接/断开
						if(msg.what == NET_PROTOCOL_UDP_SEND)
						{
							try {					
								//首先创建一个DatagramSocket对象
								//socket = new DatagramSocket(NET_PROTOCOL_UDP_LOCACITY_PORT);
					            //创建一个InetAddree
					            InetAddress serverAddress = InetAddress.getByName(NET_PROTOCOL_UDP_IP);
					            String str = msg.obj.toString()+"\0";
					            byte data [] = str.getBytes();
					            //创建一个DatagramPacket对象，并指定要讲这个数据包发送到网络当中的哪个地址，以及端口号
					            DatagramPacket packet = new DatagramPacket(data,data.length,serverAddress,NET_PROTOCOL_UDP_PORT);
					            //调用socket对象的send方法，发送数据
					            socket.send(packet);				            
					            //发送测试
					            et_out_Handler.sendEmptyMessage(NET_PROTOCOL_UDP_SEND_R);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				};
				Looper.loop();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}			
	}
	//----------------------------------线程(TCP客户端:负责连接,发送,接收数据)
	class TcpClientThread extends Thread /*implements Runnable*/
	{
		public Handler revHandler;
		Socket socket;
		BufferedReader br=null;
		OutputStream os=null;

		public void run()
		{
			try
			{
				//
				socket = new Socket(NET_PROTOCOL_TCP_IP,NET_PROTOCOL_TCP_PORT);
				//设置10秒之后即认为是超时
	            //socket.setSoTimeout(10000);
	            //发送消息-->连接成功
	            et_out_Handler.sendEmptyMessage(NET_PROTOCOL_TCP_CONNECT_R);
	            //
	            br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
	            os=socket.getOutputStream();
	            //启动一条子线程来读取服务器相应的数据
	            new Thread()
	            {
	            	public void run()
	            	{
	            		String content =null;
	            		//不断读取Socket输入流中的内容
	            		try
	            		{
	            			while((content = br.readLine()) != null)
	            			{
	            				//每当读到来自服务器的数据后,发送消息显示数据(线程无权操作界面)
	            				Message msg = new Message();
	            				msg.what = NET_PROTOCOL_TCP_RECEIVE_R;
	            				msg.obj=content;
	            				et_out_Handler.sendMessage(msg);
	            			}
	            		}
	            		catch(IOException e)
	            		{
	            			e.printStackTrace();
	            		}
	            	}
	            }.start();
	            //为当前线程初始化Looper
	            Looper.prepare();
		            
				revHandler = new Handler()
				{
					// 定义处理消息的方法
					@Override
					public void handleMessage(Message msg)
					{
						if(msg.what == NET_PROTOCOL_TCP_SEND)
						{
							try
							{
								os.write((msg.obj.toString()+"\r\n").getBytes("utf-8"));
							}
							catch(IOException e)
							{
								e.printStackTrace();
							}
						}
					}
				};
				Looper.loop();
			}
			catch(Exception e)
			{
				Log.e("IOException","来自服务器的数据");
				e.printStackTrace();
			}
		}
	}
}
