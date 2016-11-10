/*******************************************************************
* -----SharedPreferences-----   
* 说明1: 	使用了 SharedPreferences 做少量的数据存储
* 说明2: 	SharedPreferences的读取权限
* 			MODE_PRIVATE
* 			MODE_WORLD_READABLE
* 			...
* 			访问其他应用程序数据关键是获取其Context
* -----File-----
* FileInputStream openFileInput(String name)
* FileOutputStream openFileOutput(String name,int mode)
* 		 mode:	MODE_PRIVATE		:只能被当前程序读写.
*               MODE_APPEND			:追加方式打开文件.
*               MODE_WORLD_READABLE	:可由其他程序读取.
*               MODE_WORLD_WRITEABLE:可由其他程序读写.
* getDir(String name,int mode)		:获取name对应的子目录.
* File getFilesDir()				:获取绝对路径
* String[] fileList()				:获取全部文件表
* deleteFile(String)				:删除指定文件
*******************************************************************/
package com.example.administrator.xsltest.sqlite;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.RandomAccessFile;

//import xslPackage.XslTest.R;
//import xslPackage.XslTest.R.id;
//import xslPackage.XslTest.R.layout;

import android.app.Activity;  
import android.content.SharedPreferences;
import android.content.res.Resources;
 
import android.os.Bundle;  
import android.os.Environment;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.administrator.xsltest.R;

public class ParaSaveActivity extends Activity {
	EditText et_input,et_output;
	int id;
	String name,pass;		
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		setContentView(R.layout.test1);
		//----------------------------------
		TextView txtV1;
		Button button;
		//----------------------------------第一行输入文本
		//输入文本框初始化		
		et_input = (EditText)findViewById(R.id.Et_Test1_Input_1);
		et_input.setText("");
		//文本框的输入键盘设为数字键盘
		et_input.setInputType(InputType.TYPE_CLASS_NUMBER);
		//输出文本框初始化
		et_output = (EditText)findViewById(R.id.editText_Output_1);
		et_output.setText("");
		//----------------------------------第一行按键
		//文本		
		txtV1 = (TextView)findViewById(R.id.Tv_Test1_11);
		txtV1.setText("SharedPreferences:");
		//监听按键---写入		
		button = (Button)findViewById(R.id.Btn_Test1_11);
		button.setText("写入");	
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Resources r = getResources();
				SharedPreferences sp =  getSharedPreferences((String) r.getText(R.string.SharedPreferencesName),MODE_PRIVATE);
				SharedPreferences.Editor editor = sp.edit();
				id = 1;
				name = "xslxhn";
				pass = "820824";
				editor.putInt("id", id);
				editor.putString("name", name);
				editor.putString("pass", pass);
				editor.commit();
				et_output.setText(String.format("参数保存: id=%d name=%s pass=%s.",id, name,pass));
			}
		});
		//监听按键---读取
		button = (Button)findViewById(R.id.Btn_Test1_12);
		button.setText("读取");	
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Resources r = getResources();
				SharedPreferences sp =  getSharedPreferences((String) r.getText(R.string.SharedPreferencesName),MODE_PRIVATE);
				id=sp.getInt("id", -1);
				name=sp.getString("name","");
				pass=sp.getString("pass","");
				//----------读取其他应用程序的数据
				String Time = sp.getString((String) r.getText(R.string.SPN_StartTime),"");
				//----------
				et_output.setText(String.format("参数读取: id=%d name=%s pass=%s.\n%s",id, name,pass,Time));
			}
		});
		//监听按键---删除
		button = (Button)findViewById(R.id.Btn_Test1_13);
		button.setText("删除");	
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Resources r = getResources();
				SharedPreferences sp =  getSharedPreferences((String) r.getText(R.string.SharedPreferencesName),MODE_PRIVATE);
				SharedPreferences.Editor editor = sp.edit();
				editor.remove("id");
				editor.remove("name");
				editor.remove("pass");
				//调用下面语句才是真正的删除
				editor.commit();
				et_output.setText(String.format("参数删除..."));
			}
		});
		//----------------------------------第二行
		//文本		
		txtV1 = (TextView)findViewById(R.id.Tv_Test1_21);
		txtV1.setText("File: ");
		//监听按键---写入				
		button = (Button)findViewById(R.id.Btn_Test1_21);
		button.setText("写入");	
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// 将edit1中的内容写入文件中
				Resources r = getResources();
				write(et_input.getText().toString(),(String) r.getText(R.string.FileName),MODE_APPEND);
				et_input.setText("");				
			}
		});		
		//监听按键---读取		
		button = (Button)findViewById(R.id.Btn_Test1_22);
		button.setText("读取");	
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// 读取指定文件中的内容，并显示出来	
				Resources r = getResources();
				et_output.setText(read((String) r.getText(R.string.FileName)));
			}
		});
		//监听按键---删除		
		button = (Button)findViewById(R.id.Btn_test1_23);
		button.setText("删除");	
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Resources r = getResources();
				File f=new File((String) r.getText(R.string.FileName));
				if(true==DeleteFile(f))
					et_output.setText("删除成功");
				else
					et_output.setText("文件不存在");
			}
		});		
		//----------------------------------第三行
		//文本		
		txtV1 = (TextView)findViewById(R.id.Tv_Test1_31);
		txtV1.setText("SDFile: ");
		//监听按键---写入				
		button = (Button)findViewById(R.id.Btn_Test1_31);
		button.setText("SD写入");	
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// 将edit1中的内容写入文件中
				Resources r = getResources();
				SDwrite(et_input.getText().toString(),(String) r.getText(R.string.SDFileName));
				et_input.setText("");				
			}
		});		
		//监听按键---读取		
		button = (Button)findViewById(R.id.Btn_Test1_32);
		button.setText("SD读取");	
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// 读取指定文件中的内容，并显示出来	
				Resources r = getResources();
				et_output.setText(SDread((String) r.getText(R.string.SDFileName)));
			}
		});
		//监听按键---删除		
		button = (Button)findViewById(R.id.Btn_Test1_33);
		button.setText("SD删除(待开发)");
		/*
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Resources r = getResources();
				File f=new File((String) r.getText(R.string.SDFileName));
				if(true==DeleteFile(f))
					et_output.setText("删除成功");
				else
					et_output.setText("文件不存在");
			}
		});
		*/	
		//----------------------------------
    }
    //----------------------------------File-->读取数据流
    private String read(String FileName)
	{    	
		try
		{			
			// 打开文件输入流
			FileInputStream fis = openFileInput(FileName);
			byte[] buff = new byte[1024];
			int hasRead = 0;
			StringBuilder sb = new StringBuilder("");
			// 读取文件内容
			while ((hasRead = fis.read(buff)) > 0)
			{
				sb.append(new String(buff, 0, hasRead));
			}
			// 关闭文件输入流
			fis.close();
			return sb.toString();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
    //----------------------------------File-->写入数据流
    /*
     * Str		-->	待写入字符串
     * FileName	-->	文件名称
     * mode		--> 追加(MODE_APPEND)读写(MODE_PRIVATE)
     */
	private void write(String Str,String FileName,int mode)
	{
		try
		{
			// 以追加模式打开文件输出流
			FileOutputStream fos = openFileOutput(FileName, mode);
			// 将FileOutputStream包装成PrintStream
			PrintStream ps = new PrintStream(fos);
			// 输出文件内容
			ps.println(Str);
			// 关闭文件输出流
			ps.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	//----------------------------------
	private Boolean DeleteFile(File file)
	{	
		if (file.exists()) 
		{ // 判断文件是否存在
	        if (file.isFile()) 
	        { // 判断是否是文件
	            file.delete(); // delete()方法 你应该知道 是删除的意思;
	        } 
	        else if (file.isDirectory()) 
	        { // 否则如果它是一个目录
	            File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
	            for (int i = 0; i < files.length; i++)
	            { // 遍历目录下所有的文件
	                this.DeleteFile(files[i]); // 把每个文件 用这个方法进行迭代
	            }
	        }
	        file.delete();
	        return true;
	    }
		else 
		{
			return false;
			//Constants.Logdada("文件不存在！"+"\n");
	    }
	}
	//----------------------------------SDFile-->读取数据流
    private String SDread(String FileName)
	{    	
    	try
		{
			// 如果手机插入了SD卡，而且应用程序具有访问SD的权限
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED))
			{
				// 获取SD卡对应的存储目录
				File sdCardDir = Environment.getExternalStorageDirectory();
				// 获取指定文件对应的输入流
				FileInputStream fis = new FileInputStream(
					sdCardDir.getCanonicalPath() + FileName);
				// 将指定输入流包装成BufferedReader
				BufferedReader br = new BufferedReader(new 
					InputStreamReader(fis));
				StringBuilder sb = new StringBuilder("");
				String line = null;
				// 循环读取文件内容				
				while ((line = br.readLine()) != null)
				{
					sb.append(line);
				}
				// 关闭资源
				br.close();
				return sb.toString();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
    //----------------------------------SDFile-->写入数据流
    /*
     * Str		-->	待写入字符串
     * FileName	-->	文件名称
     */
	private void SDwrite(String Str,String FileName)
	{
		try
		{
			// 如果手机插入了SD卡，而且应用程序具有访问SD的权限
			if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED))
			{
				// 获取SD卡的目录
				File sdCardDir = Environment.getExternalStorageDirectory();
				File targetFile = new File(sdCardDir
					.getCanonicalPath() + FileName);
				// 以指定文件创建 RandomAccessFile对象
				RandomAccessFile raf = new RandomAccessFile(
					targetFile, "rw");
				// 将文件记录指针移动到最后
				raf.seek(targetFile.length());
				// 输出文件内容
				raf.write(Str.getBytes());
				// 关闭RandomAccessFile				
				raf.close();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}