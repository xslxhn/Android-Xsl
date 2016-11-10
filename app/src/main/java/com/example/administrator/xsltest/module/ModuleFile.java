package com.example.administrator.xsltest.module;

import android.content.res.Resources;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.example.administrator.xsltest.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/10/8.
 */

public class ModuleFile {
    private static final String TAG = "ModuleFile";
    //
    public static final int MEDIA_FILETYPE_IMAGE = 1;
    public static final int MEDIA_FILETYPE_VIDEO = 2;
    public static final int MEDIA_FILETYPE_MEDIA_RECORD = 3;
    public static final int MEDIA_FILETYPE_RADIO_RECORD_RAW = 4;
    public static final int MEDIA_FILETYPE_RADIO_RECORD_WAV = 5;
    public static final int MEDIA_FILETYPE_LOG_APP = 6;
    public static final int MEDIA_FILETYPE_LOG_SERVICE = 7;
    // 存储位置
    public static final int MEDIA_SAVETYPE_MEMORY = 1;
    public static final int MEDIA_SAVETYPE_SDCARD = 2;
    // 时间格式
    public static final String SIMPLE_DATE_FORMAT = "yyyyMMddHHmmss";

    //
    public static void init() {
        //
        File file;
        // 创建文件夹---MEMORY
        file = new File(getDir(MEDIA_SAVETYPE_MEMORY));
        if (!file.isDirectory()) {
            if (!file.mkdirs()) {
                file.mkdirs();
                Log.d(TAG, "failed to create directory, check if you have the WRITE_EXTERNAL_STORAGE permission");
            }
        }
        // 创建文件夹---SDCARD
        if (getSaveType() == MEDIA_SAVETYPE_SDCARD) {
            file = new File(getDir(MEDIA_SAVETYPE_SDCARD));
            if (!file.isDirectory()) {
                if (!file.mkdirs()) {
                    file.mkdirs();
                    Log.d(TAG, "failed to create directory, check if you have the WRITE_EXTERNAL_STORAGE permission");
                }
            }
        }
        // 创建文件---Log Service
        file = getOutputFile(MEDIA_FILETYPE_LOG_SERVICE);
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    file.createNewFile();
                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    // 获取当前应存储在内存中还是存储在SDCard中
    public static int getSaveType() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return MEDIA_SAVETYPE_MEMORY;
        } else {
            return MEDIA_SAVETYPE_SDCARD;
        }
    }

    // 获取路径
    public static String getDir(int type) {
        if (type == MEDIA_SAVETYPE_MEMORY) {
            return AppContext.getContext().getFilesDir().getAbsolutePath();
            // AppContext.getContext().getFilesDir().getAbsolutePath(), AppContext.getContext().getString(R.string.app_name)
        } else {
            // 可卸载目录(或清除数据) SDCard/Android/data/包名/
            return AppContext.getContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString();
            // Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), AppContext.getContext().getString(R.string.app_name)
        }
    }

    public static String getFileNameString(int type) {
        String timeStamp = new SimpleDateFormat(SIMPLE_DATE_FORMAT).format(new Date());
        String string;
        if (type == MEDIA_FILETYPE_IMAGE) {
            string = "IMG_" + timeStamp + ".jpg";
        } else if (type == MEDIA_FILETYPE_VIDEO) {
            string = "VID_" + timeStamp + ".mp4";
        } else if (type == MEDIA_FILETYPE_MEDIA_RECORD) {
            string = "REC_" + timeStamp + ".amr";
        } else if (type == MEDIA_FILETYPE_RADIO_RECORD_RAW) {
            string = "REC_" + timeStamp + ".raw";
        } else if (type == MEDIA_FILETYPE_RADIO_RECORD_WAV) {
            string = "REC_" + timeStamp + ".wav";
        } else if (type == MEDIA_FILETYPE_LOG_APP) {
            string = "LogApp_" + timeStamp + ".log";
        } else if (type == MEDIA_FILETYPE_LOG_SERVICE) {
            string = "LogService.log";
        } else {
            string = null;
        }
        return string;
    }

    // 获取文件路径字符串
    public static String getFilePathString(int type) {
        String string;
        if (type == MEDIA_FILETYPE_IMAGE) {
            string = getDir(MEDIA_SAVETYPE_SDCARD) + File.separator + getFileNameString(type);
        } else if (type == MEDIA_FILETYPE_VIDEO) {
            string = getDir(MEDIA_SAVETYPE_SDCARD) + File.separator + getFileNameString(type);
        } else if (type == MEDIA_FILETYPE_MEDIA_RECORD) {
            string = getDir(MEDIA_SAVETYPE_SDCARD) + File.separator + getFileNameString(type);
        } else if (type == MEDIA_FILETYPE_RADIO_RECORD_RAW) {
            string = getDir(MEDIA_SAVETYPE_SDCARD) + File.separator + getFileNameString(type);
        } else if (type == MEDIA_FILETYPE_RADIO_RECORD_WAV) {
            string = getDir(MEDIA_SAVETYPE_SDCARD) + File.separator + getFileNameString(type);
        } else if (type == MEDIA_FILETYPE_LOG_APP) {
            string = getDir(MEDIA_SAVETYPE_SDCARD) + File.separator + getFileNameString(type);
        } else if (type == MEDIA_FILETYPE_LOG_SERVICE) {
            string = getDir(MEDIA_SAVETYPE_SDCARD) + File.separator + getFileNameString(type);
        } else {
            string = null;
        }
        return string;
    }

    // 创建一个文件
    public static File getOutputFile(int type) {
        File mediaFile;
        mediaFile = new File(getFilePathString(type));
        return mediaFile;
    }

    // 创建一个Uri
    public static Uri getOutputFileUri(int type) {
        return Uri.fromFile(getOutputFile(type));
    }

    // 路径提取文件名
    public static String getFileNameFromPath(String pathandname){
        int start=pathandname.lastIndexOf("/");
        int end=pathandname.lastIndexOf(".");
        /*
        if(start!=-1 && end!=-1){
            return pathandname.substring(start+1,end);
        }else{
            return null;
        }
        */
        if(start!=-1){
            return pathandname.substring(start+1);
        }else{
            return null;
        }
    }
    // 文件名提取日期
    public static String getDateFromFileName(String filename){
        int start=filename.lastIndexOf("_");
        int end=filename.lastIndexOf(".");
        if(start!=-1 && end!=-1){
            return filename.substring(start+1,end);
        }else{
            return null;
        }
    }
}
