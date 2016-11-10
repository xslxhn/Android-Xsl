package com.example.administrator.xsltest;

import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Description:
 * <br/>site: <a href="http://www.crazyit.org">crazyit.org</a>
 * <br/>Copyright (C), 2001-2014, Yeeku.H.Lee
 * <br/>This program is protected by copyright laws.
 * <br/>Program Name:
 * <br/>Date:
 * @author  Yeeku.H.Lee kongyeeku@163.com
 * @version  1.0
 */
public class SpeechActivity extends Activity
{
	TextToSpeech tts;
	EditText et_input;
	Button speech;
	Button record;
	
	TextView txtV1;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test1);
		// 初始化TextToSpeech对象
		tts = new TextToSpeech(this, new OnInitListener()
		{
			@Override
			public void onInit(int status)
			{
				// 如果装载TTS引擎成功
				if (status == TextToSpeech.SUCCESS)
				{
					// 设置使用美式英语朗读
					//int result = tts.setLanguage(Locale.US);
					// 使用中文朗读
					int result = tts.setLanguage(Locale.CHINA);
					// 如果不支持所设置的语言
					if (result != TextToSpeech.LANG_COUNTRY_AVAILABLE
						&& result != TextToSpeech.LANG_AVAILABLE)
					{
						Toast.makeText(SpeechActivity.this
							, "TTS暂时不支持这种语言的朗读。", Toast.LENGTH_LONG)
							.show();
					}
				}
			}

		});
		//
		et_input = (EditText) findViewById(R.id.Et_Test1_Input_1);
		et_input.setText("1 2 3 4 5,上山打老虎!");
		//----------------------------------第一行
		//文本		
		txtV1 = (TextView)findViewById(R.id.Tv_Test1_11);
		txtV1.setText("朗读实验:");		
		speech = (Button) findViewById(R.id.Btn_Test1_11);
		speech.setText("朗读");	
		speech.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				// 执行朗读
				tts.speak(et_input.getText().toString(),
					TextToSpeech.QUEUE_ADD, null);
			}
		});
		record = (Button) findViewById(R.id.Btn_Test1_12);
		record.setText("记录");	
		record.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				// 将朗读文本的音频记录到指定文件
				tts.synthesizeToFile(et_input.getText().toString()
					, null, "/mnt/sdcard/sound.wav");
				Toast.makeText(SpeechActivity.this, "声音记录成功!"
					, Toast.LENGTH_LONG).show();
			}
		});
	}

	@Override
	public void onDestroy()
	{
		// 关闭TextToSpeech对象
		if (tts != null)
		{
			tts.shutdown();
		}
	}
}