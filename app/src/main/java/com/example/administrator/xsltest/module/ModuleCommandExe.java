package com.example.administrator.xsltest.module;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.util.Log;

/**
 * Created by XSL on 2016/11/9.
 */

public class ModuleCommandExe {
    private static final String TAG = "ModuleCommandExe";
    public static final String COMMAND_SH = "sh";
    public static final String COMMAND_LINE_END = "\n";
    public static final String COMMAND_EXIT = "exit\n";

    public static final String COMMAND_CLEAR_LOG = "logcat -c";
     // USER PID PPID VSIZE RSS WCHAN PC NAME
     // root 1 0 416 300 c00d4b28 0000cd5c S /init
    public static final String COMMAND_PS = "logcat -c";

    /**
     * 执行单条命令
     *
     * @param command
     * @return
     */
    public static List<String> execute(String command) {
        return execute(new String[]{command});
    }

    /**
     * 可执行多行命令（bat）
     *
     * @param commands
     * @return
     */
    public static List<String> execute(String[] commands) {
        List<String> ok_results = new ArrayList<>();
        List<String> err_results = new ArrayList<>();
        int status = -1;
        // 容错
        if (commands == null || commands.length == 0) {
            return null;
        }
        // 准备
        Log.d(TAG, "execute command start : " + commands);
        Process process = null;
        BufferedReader successReader = null;
        BufferedReader errorReader = null;
        StringBuilder errorMsg = null;
        DataOutputStream dos = null;
        //
        try {
            // 进入sh
            process = Runtime.getRuntime().exec(COMMAND_SH);
            dos = new DataOutputStream(process.getOutputStream());
            // 逐个发送
            for (String command : commands) {
                if (command == null) {
                    continue;
                }
                dos.write(command.getBytes());
                dos.writeBytes(COMMAND_LINE_END);
                dos.flush();
            }
            // 退出sh
            dos.writeBytes(COMMAND_EXIT);
            dos.flush();
            //------------------------------
            StreamConsumer errorConsumer = new StreamConsumer(process.getErrorStream(),err_results);
            StreamConsumer outputConsumer = new StreamConsumer(process.getInputStream(), ok_results);
            errorConsumer.start();
            outputConsumer.start();
            //------------------------------
            // 等待
            status = process.waitFor();
            /*
            errorMsg = new StringBuilder();
            successReader = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            errorReader = new BufferedReader(new InputStreamReader(
                    process.getErrorStream()));
            String lineStr;
            // 捕获正确返回
            while ((lineStr = successReader.readLine()) != null) {
                results.add(lineStr);
                Log.d(TAG, " command line item : " + lineStr);
            }
            // 捕获错误返回
            while ((lineStr = errorReader.readLine()) != null) {
                errorMsg.append(lineStr);
            }
            */

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭输出、输入、错误缓存
            try {
                if (dos != null) {
                    dos.close();
                }
                if (successReader != null) {
                    successReader.close();
                }
                if (errorReader != null) {
                    errorReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (process != null) {
                    process.destroy();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        Log.d(TAG, String.format(Locale.CHINA,
                "execute command end,errorMsg:%s,and status %d: ",
                err_results.toArray(new String[err_results.size()]),
                status));
        return ok_results;
    }
    //
    /**
     * 读线程
     */
    static class StreamConsumer extends Thread {
        InputStream is;
        List<String> list;
        //
        StreamConsumer(InputStream is) {
            this.is = is;
        }
        StreamConsumer(InputStream is, List<String> list) {
            this.is = is;
            this.list = list;
        }

        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line;
                while ((line = br.readLine()) != null) {
                    if (list != null) {
                        list.add(line);
                    }
                    Log.d(TAG, " command line item : " + line);
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    /**
     * 根据ps命令得到的内容获取PID，User，name等信息
     */
    private List<ProcessInfo> getProcessInfoList(List<String> orgProcessList) {
        List<ProcessInfo> procInfoList = new ArrayList<ProcessInfo>();
        for (int i = 1; i < orgProcessList.size(); i++) {
            String processInfo = orgProcessList.get(i);
            String[] proStr = processInfo.split(" ");
            // USER PID PPID VSIZE RSS WCHAN PC NAME
            // root 1 0 416 300 c00d4b28 0000cd5c S /init
            List<String> orgInfo = new ArrayList<String>();
            for (String str : proStr) {
                if (!"".equals(str)) {
                    orgInfo.add(str);
                }
            }
            if (orgInfo.size() == 9) {
                ProcessInfo pInfo = new ProcessInfo();
                pInfo.user = orgInfo.get(0);
                pInfo.pid = orgInfo.get(1);
                pInfo.ppid = orgInfo.get(2);
                pInfo.name = orgInfo.get(8);
                procInfoList.add(pInfo);
            }
        }
        return procInfoList;
    }
    class ProcessInfo {
        public String user;
        public String pid;
        public String ppid;
        public String name;

        @Override
        public String toString() {
            String str = "user=" + user + " pid=" + pid + " ppid=" + ppid
                    + " name=" + name;
            return str;
        }
    }
}
