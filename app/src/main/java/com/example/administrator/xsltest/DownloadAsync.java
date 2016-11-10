package com.example.administrator.xsltest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * Description: <br/>
 * site: <a href="http://www.crazyit.org">crazyit.org</a> <br/>
 * Copyright (C), 2001-2014, Yeeku.H.Lee <br/>
 * This program is protected by copyright laws. <br/>
 * Program Name: <br/>
 * Date:
 * @author Yeeku.H.Lee kongyeeku@163.com
 * @version 1.0
 */
public class DownloadAsync extends Activity
{
	EditText show;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test1);
		show = (EditText) findViewById(R.id.Et_Test1_Input_1);
		//监听按键
		Button button;
		button = (Button)findViewById(R.id.Btn_Test1_11);
		button.setText("下载");	
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					download(v);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	//按键单击
	public void download(View source) throws MalformedURLException
	{
		DownTask task = new DownTask(this);
		task.execute(new URL("http://www.crazyit.org/ethos.php"));
	}
    /*
     * 说明1: 	做一个异步任务.
     * 注意1: 	每个AsyncTask只能被执行一次.
     * 注意2: 	必须在UI线程中创建AsyncTask的实例/调用execute的方法.
     * 注意3: 	AsyncTask的	onPreExecute(),
     * 						onPostExecute(Result result),
     * 						doInBackground(Params... params),
     * 						onProgressUpdate(Progress... values)方法,
     * 			不应该由程序员调用,而是由系统调用.
     */
	class DownTask extends AsyncTask<URL, Integer, String>
	{
		// 可变长的输入参数，与AsyncTask.exucute()对应
		ProgressDialog pdialog;
		// 定义记录已经读取行的数量
		int hasRead = 0;
		Context mContext;

		public DownTask(Context ctx)
		{
			mContext = ctx;
		}
        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#doInBackground(Params[])
         * 后台进程需要完成的任务,该方法可以调用publishProgress()更新任务的执行进度.
         */
		@Override
		protected String doInBackground(URL... params)
		{
			StringBuilder sb = new StringBuilder();
			try
			{
				URLConnection conn = params[0].openConnection();
				// 打开conn连接对应的输入流，并将它包装成BufferedReader
				BufferedReader br = new BufferedReader(
					new InputStreamReader(conn.getInputStream()
					, "utf-8"));
				String line = null;
				while ((line = br.readLine()) != null)
				{
					sb.append(line + "\n");
					hasRead++;
					publishProgress(hasRead);
				}
				return sb.toString();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return null;
		}
        /*
         * 当doInBackground()完成后,系统会自动调用onPostExecute()方法,
         * 并将doInBackground()方法的返回值传给该方法.
         */
		@Override
		protected void onPostExecute(String result)
		{
			// 返回HTML页面的内容
			show.setText(result);
			pdialog.dismiss();
		}
        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPreExecute()
         * 该方法将在执行后台耗时操作前被调用.通常完成一些初始化准备工作
         */
		@Override
		protected void onPreExecute()
		{
			pdialog = new ProgressDialog(mContext);
			// 设置对话框的标题
			pdialog.setTitle("任务正在执行中");
			// 设置对话框 显示的内容
			pdialog.setMessage("任务正在执行中，敬请等待...");
			// 设置对话框不能用“取消”按钮关闭
			pdialog.setCancelable(false);
			// 设置该进度条的最大进度值
			pdialog.setMax(202);
			// 设置对话框的进度条风格
			pdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			// 设置对话框的进度条是否显示进度
			pdialog.setIndeterminate(false);
			pdialog.show();
		}
        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onProgressUpdate(Progress[])
         * 在doInBackground()方法调用中调用publishProgress()方法更新任务
         * 的执行进度后,将会触发onProgressUpdate()方法.
         */
		@Override
		protected void onProgressUpdate(Integer... values)
		{
			// 更新进度
			show.setText("已经读取了【" + values[0] + "】行！");
			pdialog.setProgress(values[0]);
		}
	}
}
