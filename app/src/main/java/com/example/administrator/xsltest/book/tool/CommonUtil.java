/*
 * 类说明: 调试信息输出
 */
package com.example.administrator.xsltest.book.tool;

import android.content.Context;
import android.widget.Toast;

public class CommonUtil {

	public static void Log_Info(String tag, String msg) {
		android.util.Log.i(tag, msg);
	}

	public static void Log_Error(String tag, String msg) {
		android.util.Log.e(tag, msg);
	}

	public static void Alert(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}
}
