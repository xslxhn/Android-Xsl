package com.example.administrator.xsltest.module;

import android.os.Environment;
import android.util.Log;

import com.example.administrator.xsltest.model.ReceiveData;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by NH on 2016/4/15.
 */
public class FileUtil {

    private static final String TAG = "FileUtil";

    public static String fileName = null;



    public static String saveAsCSV(List<ReceiveData> scanResults) throws IOException {
        File cacheFile = createCacheFile();
        FileWriter writer = new FileWriter(cacheFile, true);
        BufferedWriter bufferedWriter = new BufferedWriter(writer);
        for (int i = 0; i < scanResults.size(); i++) {
            //0.02;1.27617
            String msg = (float) (i * 0.01) + ";" + scanResults.get(i).getPressure() + "\r\n";
            bufferedWriter.write(msg);
            Log.i(TAG, "saveAsCSV: " + msg);
        }
        bufferedWriter.close();
        return cacheFile.getPath();
    }

    /**
     * 创建缓存文件
     */
    private static File createCacheFile() throws IOException {
        String fileNamePath;
        if(fileName == null || "".equals(fileName) || fileName.length() == 0) {
            String date = DateUtils.getCurrentTime(DateUtils.DATE_FORMAT_TYPE_YYYY_MM_DD_HH_MM);
            fileName = "压力传感器_" + date + ".csv";
        }
        fileNamePath = Environment.getExternalStorageDirectory() + "/" +fileName;
        File file = new File(fileNamePath);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if (!file.exists()) {
                file.createNewFile();
            }
        }
        return file;
    }
}
