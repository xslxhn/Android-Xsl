/*
package xslPackage.XslTest;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
 */
package com.example.administrator.xsltest;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.lang.ref.WeakReference;

public class DialogActivity extends AppCompatActivity {
    //
    private int mProgress = 0;

    private EditText mResultText = null;
    private Dialog mDialog = null;
    private EditText mDateTimeText = null;
    private ProgressDialog mProgressDialog;

    private static class MyHandler extends Handler{
        WeakReference<DialogActivity> mActivity;
        MyHandler(DialogActivity activity){
            mActivity=new WeakReference<>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
             super.handleMessage(msg);
            DialogActivity theActivity = mActivity.get();
            switch (msg.what){
                case 0:
                    theActivity.ProgressMsg();
                    break;
                default:
                    break;
            }
        }
    }
    private MyHandler mProgressHandler = new MyHandler(this);

    private void ProgressMsg(){
        if (mProgress >= 100) {
            mProgressDialog.dismiss();
            mResultText.setText(String.format(getResources().getString(R.string.ProgressDialog_Finish), mProgress));
        } else {
            mProgress++;
            mProgressDialog.incrementProgressBy(1);
            mProgressHandler.sendEmptyMessageDelayed(0, 100);
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        setContentView(R.layout.dialog_main);
        // 装载文本框
        mResultText = (EditText) findViewById(R.id.textResult);
    }

    // 自定义对话框
    public void DialogOkCancel_OnClickHandler(View v) {
        // 新建Dialog
        mDialog = new Dialog(DialogActivity.this);
        // 显示Layout
        mDialog.setContentView(R.layout.dialog_ok_cancel);
        // 设置标题
        mDialog.setTitle("Dialog with OK Cancel");
        //获取并设置窗口信息
        Window dialogWindow = mDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.START | Gravity.TOP);
        lp.x = 100;
        lp.y = 100;
        lp.width = 1000;
        lp.height = 1500;
        lp.alpha = 0.7f;
        dialogWindow.setAttributes(lp);
        // 监听按键
        mDialog.findViewById(R.id.ButtonOk).setOnClickListener(
                new OnClickListener() {
                    public void onClick(View v) {
                        mDialog.dismiss();
                        mResultText.setText(R.string.Dialog_OK);
                    }
                });
        mDialog.findViewById(R.id.ButtonCancel).setOnClickListener(
                new OnClickListener() {
                    public void onClick(View v) {
                        mDialog.dismiss();
                        mResultText.setText(R.string.Dialog_Canceled);
                    }
                });
        mDialog.show();
    }

    public void AlertDialogOkNeutralCancel_OnClickHandler(View v) {
        new AlertDialog.Builder(this)
                .setTitle("Alert Dialog OK Cancel")
                .setMessage("Alert Dialog with OK Neutral Cancel Button.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                mResultText.setText(R.string.AlertDialog_OK);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                mResultText.setText(R.string.AlertDialog_Canceled);
                            }
                        })
                .setNeutralButton("Neutral",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                mResultText.setText(R.string.AlertDialog_Neutral);
                            }
                        }).create().show();
    }

    public void AlertDialogList_OnClickHandler(View v) {
        final String[] DIALOG_ITEM = new String[]{"one", "two", "three", "four"};
        new AlertDialog.Builder(this)
                .setTitle("Alert Dialog List")
                .setItems(DIALOG_ITEM,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                mResultText.setText(DIALOG_ITEM[which]);
                            }
                        }).create().show();
    }

    // 进度条对话框
    public void ProgressDialog_OnClickHandler(View v) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Progress Dialog");
        // 条形进度条
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        // 圆形进度条
        //mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMax(100);

        mProgressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        mResultText.setText(String.format(getResources().getString(R.string.ProgressDialog_OK), mProgress));
                        mProgressDialog.dismiss();
                    }
                });

        mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton) {
                        mResultText.setText(String.format(getResources().getString(R.string.ProgressDialog_Canceled), mProgress));
                        mProgressDialog.dismiss();
                    }
                });
        mProgress = 0;
        mProgressDialog.show();
        mProgressDialog.setProgress(0);
        // 触发
        mProgressHandler.sendEmptyMessage(0);
    }

    //日期对话框
    public void DatePickerDialogl_OnClickHandler(View v) {
        mDateTimeText = (EditText) findViewById(R.id.Et_DatePickerDialog);
        // 设置键解析
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        mResultText.setText(String.format(getResources().getString(R.string.Date),
                                year, monthOfYear + 1, dayOfMonth));
                        mDateTimeText.setText(String.format(getResources().getString(R.string.Date),
                                year, monthOfYear + 1, dayOfMonth));
                    }
                }, 2009, 11, 1);
        // 取消键解析
        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                "Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mResultText.setText(R.string.DatePickerDialog_Canceled);
                        mDateTimeText.setText(R.string.DateDefault);
                    }
                });
        datePickerDialog.show();
    }

    // 时间对话框
    public void TimePickerDialog_OnClickHandler(View v) {
        mDateTimeText = (EditText) findViewById(R.id.Et_TimePickerDialog);
        // 设置键解析
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new OnTimeSetListener() {
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        mResultText.setText(String.format(getResources().getString(R.string.Time), hourOfDay, minute));
                        mDateTimeText.setText(String.format(getResources().getString(R.string.Time), hourOfDay, minute));
                    }
                }, 0, 0, true);
        // 取消键解析
        timePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                mResultText.setText(R.string.TimePickerDialog_Canceled);
                mDateTimeText.setText(R.string.TimeDefault);
            }
        });
        timePickerDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setContentView(R.layout.activity_main);
    }
}
