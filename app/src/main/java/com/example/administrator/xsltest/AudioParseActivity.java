package com.example.administrator.xsltest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.xsltest.model.ReceiveData;
import com.example.administrator.xsltest.module.AppContext;
import com.example.administrator.xsltest.module.ChartUtils;
// MPAndroidChart库
import com.example.administrator.xsltest.module.LogService;
import com.example.administrator.xsltest.module.ModuleFile;
import com.example.administrator.xsltest.module.ModuleNotification;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/26.
 */

public class AudioParseActivity extends AppCompatActivity {
    private static final String TAG = "AudioParseActivity";

    //
    private RelativeLayout mRelativeLayout;
    private boolean isPause = true;
    // 建立图表
    private LineChart lineChart;
    private TextView textView1;
    private EditText editText1;
    private EditText editText2;
    //
    private long thresholdValue_1 = 0;
    private int jitterDelayMs = 0, jitterDelayMsCmt = 0;
    ;
    private int thresholdCount_1 = 0;
    private int thresholdState_1 = 0;
    //
    private boolean threadState = false;
    // 消息机制 控件刷新
    //private Message msg = new Message();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //
            if (msg.what == 1) {
                // 更新UI
                textView1.setText(thresholdCount_1 + "");
            } else if (msg.what == 2) {
                // 启动监测
                Log.i(TAG, "onOptionsItemSelected: Press Start");
                isPause = !isPause;
                if (isPause) {
                    menu1.findItem(R.id.menu_start).setIcon(android.R.drawable.ic_media_play);
                    AudioRecorderStop();
                    //
                    menu1.findItem(R.id.menu_add).setEnabled(true);
                    menu1.findItem(R.id.menu_save).setEnabled(true);
                } else {
                    menu1.findItem(R.id.menu_start).setIcon(android.R.drawable.ic_media_pause);
                    // 清空表格数据
                    // lineChart.clearValues();
                    // lineChart.clear();
                    lineChart = ChartUtils.getLineChart(AudioParseActivity.this, null);
                    mRelativeLayout.removeAllViews();
                    mRelativeLayout.addView(lineChart);
                    //
                    AudioRecorderStart();
                    //
                    menu1.findItem(R.id.menu_add).setEnabled(false);
                    menu1.findItem(R.id.menu_save).setEnabled(false);
                }
            } else if (msg.what == 3) {
                // 文件显示
                FileToUtil();
            } else if (msg.what == 4) {
                // 文件保存
                UtilToFile();
            } else if (msg.what == 5) {
                if (threadState == false) {
                    // 释放录音器
                    audioRecord.stop();
                    audioRecord.release();
                    audioRecord = null;
                } else {
                    handler.sendEmptyMessage(5);
                }
            }
        }
    };

    // 创建
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_parse);
        // 装载图表控件到相应容器
        mRelativeLayout = (RelativeLayout) findViewById(R.id.RL_AudioParse);
        lineChart = ChartUtils.getLineChart(this, null);
        mRelativeLayout.addView(lineChart);
        //
        textView1 = (TextView) findViewById(R.id.ActivityAudioParseText1);
        editText1 = (EditText) findViewById(R.id.ActivityAudioParseEdit1);
        editText2 = (EditText) findViewById(R.id.ActivityAudioParseEdit2);
        // 变量初始化
        thresholdValue_1 = Long.parseLong(editText1.getText().toString());
        jitterDelayMs = Integer.parseInt(editText2.getText().toString());
        thresholdCount_1 = 0;
        thresholdState_1 = 0;
        // 启动服务
        Intent intent = new Intent(this, LogService.class);
        startService(intent);
        //intent = new Intent(this, MonitorRunService.class);
        //startService(intent);
        //
        ModuleNotification.Init();
        ModuleNotification.notifyShow(0);
    }
    // 启动
    @Override
    protected void onStart() {
        super.onStart();
    }

    // 销毁
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isPause) {
            AudioRecorderStop();
        }
        //
        Intent intent = new Intent(this, LogService.class);
        stopService(intent);
        //intent = new Intent(this, MonitorRunService.class);
        //stopService(intent);
        ModuleNotification.deInit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //
        setContentView(R.layout.activity_audio_parse);
        // 装载图表控件到相应容器
        mRelativeLayout = (RelativeLayout) findViewById(R.id.RL_AudioParse);
        lineChart = ChartUtils.getLineChart(this, null);
        mRelativeLayout.addView(lineChart);
        //
        textView1 = (TextView) findViewById(R.id.ActivityAudioParseText1);
        editText1 = (EditText) findViewById(R.id.ActivityAudioParseEdit1);
        editText2 = (EditText) findViewById(R.id.ActivityAudioParseEdit2);
        // 变量初始化
        thresholdValue_1 = Long.parseLong(editText1.getText().toString());
        jitterDelayMs = Integer.parseInt(editText2.getText().toString());
        thresholdCount_1 = 0;
        thresholdState_1 = 0;
        // 启动服务
        //Intent intent = new Intent(this, LogService.class);
        //startService(intent);
        //intent = new Intent(this, MonitorRunService.class);
        //startService(intent);
    }

    // 创建菜单
    private static Menu menu1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_audio_parse_menu, menu);
        menu1 = menu;
        return true;
    }

    // 解析菜单
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.menu_start:
                handler.sendEmptyMessage(2);
                break;
            case R.id.menu_add:
                handler.sendEmptyMessage(3);
                break;
            case R.id.menu_save:
                handler.sendEmptyMessage(4);
                break;
        }
        return true;
    }

    //----------------------------------------------------------------------------------------------AudioRecord
    // 利用 AudioRecord 进行录音（可进行数据处理）
    private AudioRecord audioRecord = null;
    // 正在录制标志
    private boolean isAudioRecord = false;
    // 音频获取源
    private int audioSource = MediaRecorder.AudioSource.MIC;
    // 设置音频采样率 8000 11025 16000 22050 44100
    private int sampleRateInHz = 8000;
    // 设置音频录制声道
    // ---单声道   AudioFormat.CHANNEL_IN_MONO
    // ---双声道   AudioFormat.CHANNEL_IN_STEREO
    private int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    // 设置音频数据格式
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    //private static int audioFormat = AudioFormat.E;
    // 缓存区字节大小
    private int bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
    ;
    //
    private File fileWav = null;

    private void ParaInOut(boolean b) {
        if (b == true) {
            // 设置装载
            thresholdValue_1 = Long.parseLong(editText1.getText().toString());
            jitterDelayMs = Integer.parseInt(editText2.getText().toString());
            // 标志置位
            isAudioRecord = true;
            // 设置禁用
            editText1.setEnabled(false);
            editText2.setEnabled(false);
        } else {
            // 标志清空
            isAudioRecord = false;
            // 设置使能
            editText1.setEnabled(true);
            editText2.setEnabled(true);
        }
    }

    private void AudioRecorderStart() {
        ParaInOut(true);
        // 创建对象
        if (audioRecord == null) {
            audioRecord = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes);
        }
        // 启动录音
        audioRecord.startRecording();
        //开启音频文件写入线程
        new Thread(new AudioRecordThread()).start();

    }

    private void AudioRecorderStop() {
        if (audioRecord != null) {
            ParaInOut(false);
            //----------
            // 释放录音器
            handler.sendEmptyMessage(5);
        }

    }

    class AudioRecordThread implements Runnable {
        @Override
        public void run() {
            byte[] audioData = new byte[bufferSizeInBytes];
            int readSize;
            int num;
            int v1, v2;
            int value;
            int i = 0;
            long i32 = 0;
            threadState = true;
            while (isAudioRecord) {
                //
                if (audioRecord == null) continue;
                // 读取音频数据
                try {
                    readSize = audioRecord.read(audioData, 0, bufferSizeInBytes);
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
                // 数据有效验证
                if (AudioRecord.ERROR_INVALID_OPERATION == readSize) {
                    continue;
                }
                // 数据有效处理
                for (num = 0; num < readSize; num += 2) {
                    v1 = audioData[num];
                    v2 = audioData[num + 1];
                    v1 = v1 & 0xFF;
                    v2 = v2 & 0xFF;
                    if (v1 != 0 || v2 != 0) {
                        value = v2 << 8;
                        value = value + v1;
                    } else {
                        value = 0;
                    }
                    value = (short) value;
                    i32 += Math.abs(value);
                    i++;
                    if (i >= 80) {
                        i = 0;
                        // 最高限值
                        if (i32 < 0 || i32 > 100000) {
                            i32 = 100000;
                        }
                        // 阈值处理
                        if (thresholdState_1 == 0) {
                            if (i32 >= thresholdValue_1) {
                                thresholdState_1 = 1;
                                thresholdCount_1++;
                                jitterDelayMsCmt = 0;
                                handler.sendEmptyMessage(1);
                            }
                        } else if (thresholdState_1 == 1) {
                            if (i32 >= thresholdValue_1) {
                                jitterDelayMsCmt = 0;
                            } else {
                                jitterDelayMsCmt += (8000 / 80);
                                if (jitterDelayMsCmt >= jitterDelayMs) {
                                    thresholdState_1 = 2;
                                }
                            }
                        }
                        if (thresholdState_1 == 2) {
                            thresholdState_1 = 0;
                        }
                        //
                        try {
                            ChartUtils.addEntry(lineChart, i32);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //
                        i32 = 0;
                    }
                }
            }
            threadState = false;
        }
    }

    private void UtilToFile() {
        File file = null;
        FileOutputStream fos;
        int i16;
        byte[] buf = new byte[2];
        if (lineChart.getXValCount() == 0) {
            Log.i(TAG, "UtilToFile: NULL");
            return;
        }
        try {
            // 新建文件
            file = ModuleFile.getOutputFile(ModuleFile.MEDIA_FILETYPE_RADIO_RECORD_RAW);
            fos = new FileOutputStream(file);
            // 存储
            List<ILineDataSet> dataSets = lineChart.getData().getDataSets();
            ILineDataSet dataSet = dataSets.get(0);
            for (int i = 0; i < dataSet.getEntryCount(); i++) {
                float val = dataSet.getEntryForIndex(i).getVal();
                //Log.i(TAG, "UtilToFile: XVal = " + val);
                i16 = (int) val;
                buf[0] = (byte) i16;
                buf[1] = (byte) (i16 >> 8);
                fos.write(buf, 0, 2);
            }
            // 关闭
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void FileToUtil() {
        FileInputStream in;
        long totalAudioLen;
        long num;
        int value;
        int v1, v2;
        // 装载文件
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "XslTest");
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "REC.raw");
        if (mediaFile.length() == 0) {
            Log.i(TAG, "FileToUtil: File null");
            return;
        }
        // 装载参数
        ParaInOut(true);
        // 清空曲线
        lineChart = ChartUtils.getLineChart(this, null);
        mRelativeLayout.removeAllViews();
        mRelativeLayout.addView(lineChart);
        // 显示曲线
        try {
            in = new FileInputStream(mediaFile);
            totalAudioLen = in.getChannel().size();
            for (num = 0; num < totalAudioLen; num += 2) {
                v1 = in.read();
                v2 = in.read();
                v1 = v1 & 0xFF;
                v2 = v2 & 0xFF;
                if (v1 != 0 || v2 != 0) {
                    value = v2 << 8;
                    value = value + v1;
                } else {
                    value = 0;
                }
                ChartUtils.addEntry(lineChart, value);
            }
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 释放参数
        ParaInOut(false);
    }


    // 将音频数据文件转换为 WAV 文件
    /*
    private void AudioFileToWav(File inFileName, File outFileName) {
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = sampleRateInHz;
        int channels = 1;
        long byteRate = 16 * sampleRateInHz * channels / 8;
        byte[] data = new byte[bufferSizeInBytes];
        try {
            in = new FileInputStream(inFileName);
            out = new FileOutputStream(outFileName);
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;
            WriteWaveFileHeader(out, totalAudioLen, totalDataLen, longSampleRate, channels, byteRate);
            while (in.read(data) != -1) {
                out.write(data);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen, long totalDataLen,
                                     long longSampleRate, int channels, long byteRate)
            throws IOException {
        byte[] header = new byte[44];
        header[0] = 'R';
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xFF);
        header[5] = (byte) ((totalDataLen >> 8) & 0xFF);
        header[6] = (byte) ((totalDataLen >> 16) & 0xFF);
        header[7] = (byte) ((totalDataLen >> 24) & 0xFF);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f';
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16;
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1;
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xFF);
        header[25] = (byte) ((longSampleRate >> 8) & 0xFF);
        header[26] = (byte) ((longSampleRate >> 16) & 0xFF);
        header[27] = (byte) ((longSampleRate >> 24) & 0xFF);
        header[28] = (byte) (byteRate & 0xFF);
        header[29] = (byte) ((byteRate >> 8) & 0xFF);
        header[30] = (byte) ((byteRate >> 16) & 0xFF);
        header[31] = (byte) ((byteRate >> 24) & 0xFF);
        header[32] = (byte) (2 * 16 / 8);
        header[33] = 0;
        header[34] = 16;
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xFF);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xFF);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xFF);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xFF);
        out.write(header, 0, 44);
    }
    */
    //----------------------------------------------------------------------------------------------生成文件

}
