package com.example.administrator.xsltest.module;

/**
 * Created by XSL on 2016/10/28.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.administrator.xsltest.AudioParseActivity;
import com.example.administrator.xsltest.R;

import static android.R.attr.type;

/**
 * 日志服务，日志默认会存储在SDcar里,如果没有SDcard会存储在内存中的安装目录下面。
 * 1.本服务默认在SDcard中每天生成一个日志文件,
 * 2.如果有SDCard的话会将之前内存中的文件拷贝到SDCard中
 * 3.如果没有SDCard，在安装目录下只保存当前在写日志
 * 4.SDcard的装载卸载动作会在步骤2,3中切换
 * 5.SDcard中的日志文件只保存7天
 * 6.监视App运行的LogCat日志（sdcard mounted）:sdcard/MyApp/log/yyyy-MM-dd HHmmss.log（默认）
 * 7.监视App运行的LogCat日志（sdcard unmounted）:/data/data/包名/files/log/yyyy-MM-dd HHmmss.log
 * 8.本service的运行日志：/data/data/包名/files/log/Log.log
 *
 * @author Administrator
 */
public class LogService extends Service {
    // TAG
    private static final String TAG = "LogService";

    // 内存中日志文件最大值，10M
    private static final int MEMORY_LOG_FILE_MAX_SIZE = 10 * 1024 * 1024;
    // 内存中的日志文件大小监控时间间隔，10分钟
    private static final int MEMORY_LOG_FILE_MONITOR_INTERVAL = 10 * 60 * 1000;
    // sd卡中日志文件的最多保存天数
    private static final int SDCARD_LOG_FILE_SAVE_DAYS = 7;
    @SuppressWarnings("unused")

    //当前的日志记录类型
    private int CURR_LOG_TYPE = ModuleFile.MEDIA_SAVETYPE_SDCARD;

    //如果当前的日志写在内存中，记录当前的日志文件名称
    private String CURR_INSTALL_LOG_NAME;

    //本服务输出的日志文件名称
    private String logServiceLogName = "LogService.log";
    private SimpleDateFormat myLogSdf = new SimpleDateFormat(ModuleFile.SIMPLE_DATE_FORMAT);
    private OutputStreamWriter writer;

    //日志名称格式
    private SimpleDateFormat sdf = new SimpleDateFormat(ModuleFile.SIMPLE_DATE_FORMAT);

    private Process process;

    private WakeLock wakeLock;
    /* 是否正在监测日志文件大小；
     * 如果当前日志记录在SDcard中则为false
     * 如果当前日志记录在内存中则为true*/
    private boolean logSizeMoniting = false;

    //日志文件监测action
    private static String MONITOR_LOG_SIZE_ACTION = "MONITOR_LOG_SIZE";
    //切换日志文件action
    private static String SWITCH_LOG_FILE_ACTION = "SWITCH_LOG_FILE_ACTION";
    //
    private static String NOTIFICATION_CLICK_ACTION = "notification_clicked";
    private static String NOTIFICATION_CANCEL_ACTION = "notification_cancelled";
    // 消息接收器
    private SDStateMonitorReceiver sdStateReceiver;
    private LogTaskReceiver logTaskReceiver;
    //----------------------------------------------------------------------------------------------
    // Service的虚方法,必须实现
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // This is the new method that instead of the old onStart method on the pre-2.0 platform.
    // 开始服务(可以多次开启),执行更新widget组件的操作
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Log
        Log.i(TAG, "===================onStartCommand===========================");
        // 通知
        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //
        ModuleFile.init();
        //
        try {
            writer = new OutputStreamWriter(new FileOutputStream(
                    ModuleFile.getFilePathString(ModuleFile.MEDIA_FILETYPE_LOG_SERVICE), true));
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        // ----------电源控制
        // 获取实例
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        // 获取原因锁实例
        // ---PARTIAL_WAKE_LOCK         ：保持CPU运转    屏幕/键盘灯可关闭
        // ---SCREEN_DIM_WAKE_LOCK      ：保持CPU运转    允许保持屏幕但可能灰色     允许关闭键盘灯
        // ---SCREEN_BRIGHT_WAKE_LOCK   ：保持CPU运转    允许保持屏幕高亮           允许关闭键盘灯
        // ---FULL_WAKE_LOCK            ：保持CPU运转    保持屏幕/键盘灯高亮显示
        // ---ACQUIRE_CAUSES_WAKEUP     ：
        // ---ON_AFTER_RELEASE          ：
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        // ----------获取当前存储类型   SD优先
        CURR_LOG_TYPE = ModuleFile.getSaveType();
        // ----------注册系统消息接收器
        // SD卡状态    监控挂载与卸载
        IntentFilter sdCarMonitorFilter = new IntentFilter();
        sdCarMonitorFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        sdCarMonitorFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        sdCarMonitorFilter.addDataScheme("file");
        sdStateReceiver = new SDStateMonitorReceiver();
        registerReceiver(sdStateReceiver, sdCarMonitorFilter);
        // LOG任务    监控自定义的两个事件
        IntentFilter logTaskFilter = new IntentFilter();
        logTaskFilter.addAction(MONITOR_LOG_SIZE_ACTION);
        logTaskFilter.addAction(SWITCH_LOG_FILE_ACTION);
        logTaskReceiver = new LogTaskReceiver();
        registerReceiver(logTaskReceiver, logTaskFilter);
        // ----------闹钟开启   部署日志切换任务，每天凌晨切换日志文件(实质是发送一个广播信号)
        Intent intent = new Intent(SWITCH_LOG_FILE_ACTION);
        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
        // 日历类实例化
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        // 获取系统闹钟实例
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        // 将 Intent 加入
        // ---  am.set          指定时长后执行某项操作
        //      am.setRepeating 周期性执行某项操作
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, sender);
        // ----------打印LogService到文件
        recordLogServiceLog("deployNextTask succ,next task time is:" + myLogSdf.format(calendar.getTime()));
        // ----------LOG
        Log.i(TAG, "LogService onCreate");
        // ----------线程启动
        new LogCollectorThread().start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 对于通过startForeground启动的service，需要通过stopForeground来取消前台运行状态
        stopForeground(true);
        // 写ServiceLog
        recordLogServiceLog("LogService onDestroy");
        // 关闭文件流
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //
        if (process != null) {
            process.destroy();
        }
        // 注销接收器
        unregisterReceiver(sdStateReceiver);
        unregisterReceiver(logTaskReceiver);
    }



    /**
     * 监控SD卡状态
     */
    class SDStateMonitorReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            //存储卡被卸载
            if (Intent.ACTION_MEDIA_UNMOUNTED.equals(intent.getAction())) {
                if (CURR_LOG_TYPE == ModuleFile.MEDIA_SAVETYPE_SDCARD) {
                    Log.d(TAG, "SDcar is UNMOUNTED");
                    CURR_LOG_TYPE = ModuleFile.MEDIA_SAVETYPE_MEMORY;
                    new LogCollectorThread().start();
                }
            }
            //存储卡被挂载
            else if (Intent.ACTION_MEDIA_MOUNTED.equals(intent.getAction())) {
                if (CURR_LOG_TYPE == ModuleFile.MEDIA_SAVETYPE_MEMORY) {
                    Log.d(TAG, "SDcar is MOUNTED");
                    CURR_LOG_TYPE = ModuleFile.MEDIA_SAVETYPE_SDCARD;
                    new LogCollectorThread().start();
                }
            }
        }
    }

    /**
     * 日志任务接收
     * 切换日志，监控日志大小
     */
    class LogTaskReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // 切换日志
            if (SWITCH_LOG_FILE_ACTION.equals(action)) {
                new LogCollectorThread().start();
            }
            // 监控日志大小
            else if (MONITOR_LOG_SIZE_ACTION.equals(action)) {
                checkLogSize();
            }
        }
    }

    /**
     * 线程---日志收集
     * 1.清除日志缓存
     * 2.杀死应用程序已开启的Logcat进程防止多个进程写入一个日志文件
     * 3.开启日志收集进程
     * 4.处理日志文件
     * 移动 OR 删除
     */
    class LogCollectorThread extends Thread {

        // XSL---好像只运行一次
        public LogCollectorThread() {
            super("LogCollectorThread");
            Log.d(TAG, "LogCollectorThread is create");
        }

        @Override
        public void run() {
            try {
                // 唤醒手机
                wakeLock.acquire();
                // 清除日志缓存
                clearLogCache();
                // 杀掉由本程序开启的logcat进程
                List<String> orgProcessList = getAllProcess();
                List<ProcessInfo> processInfoList = getProcessInfoList(orgProcessList);
                killLogcatProc(processInfoList);
                // 开始收集日志信息
                createLogCollector();
                //休眠，创建文件，然后处理文件，不然该文件还没创建，会影响文件删除
                Thread.sleep(1000);
                // 如果日志文件存储位置切换到内存
                if (CURR_LOG_TYPE == ModuleFile.MEDIA_SAVETYPE_MEMORY) {
                    // 部署日志大小监控任务，控制日志大小不超过规定值
                    deployLogSizeMonitorTask();
                    // 删除内存中的过期日志
                    deleteMemoryExpiredLog();
                }
                // 如果日志文件存储位置切换到SDCard
                else if (CURR_LOG_TYPE == ModuleFile.MEDIA_SAVETYPE_SDCARD) {
                    // 移动所有存储在内存中的日志到SDCard中
                    moveLogfile();
                    // 取消之前部署的日志大小监控
                    cancelLogSizeMonitorTask();
                    // 删除7天之前的日志
                    deleteSDcardExpiredLog();
                }
                // 唤醒锁释放
                wakeLock.release();
            } catch (Exception e) {
                e.printStackTrace();
                recordLogServiceLog(Log.getStackTraceString(e));
            }
        }
    }

    /**
     * 每次记录日志之前先清除日志的缓存, 不然会在两个日志文件中记录重复的日志
     */
    private void clearLogCache() {
        Process proc = null;
        List<String> commandList = new ArrayList<String>();
        commandList.add("logcat");
        commandList.add("-c");
        try {
            proc = Runtime.getRuntime().exec(
                    commandList.toArray(new String[commandList.size()]));
            StreamConsumer errorGobbler = new StreamConsumer(proc.getErrorStream());

            StreamConsumer outputGobbler = new StreamConsumer(proc.getInputStream());

            errorGobbler.start();
            outputGobbler.start();
            if (proc.waitFor() != 0) {
                Log.e(TAG, " clearLogCache proc.waitFor() != 0");
                recordLogServiceLog("clearLogCache clearLogCache proc.waitFor() != 0");
            }
        } catch (Exception e) {
            Log.e(TAG, "clearLogCache failed", e);
            recordLogServiceLog("clearLogCache failed");
        } finally {
            try {
                proc.destroy();
            } catch (Exception e) {
                Log.e(TAG, "clearLogCache failed", e);
                recordLogServiceLog("clearLogCache failed");
            }
        }
    }

    /**
     * 关闭由本程序开启的logcat进程：
     * 根据用户名称杀死进程(如果是本程序进程开启的Logcat收集进程那么两者的USER一致)
     * 如果不关闭会有多个进程读取logcat日志缓存信息写入日志文件
     */
    private void killLogcatProc(List<ProcessInfo> allProcList) {
        if (process != null) {
            process.destroy();
        }
        String packName = this.getPackageName();
        String myUser = getAppUser(packName, allProcList);
        recordLogServiceLog("app user is:" + myUser);
        recordLogServiceLog("============= START TYPING PROC LIST INFO ==============");
        //只打印本App和logcat的进程信息，其他进程信息不打印
        for (ProcessInfo processInfo : allProcList) {
            if (myUser.equals(processInfo.user) || "logcat".equals(processInfo.name)) {
                recordLogServiceLog(processInfo.toString());
            }
        }
        recordLogServiceLog("============= END TYPING PROC LIST INFO ==============");
        for (ProcessInfo processInfo : allProcList) {
            if (processInfo.name.toLowerCase().equals("logcat")
                    && processInfo.user.equals(myUser)) {
                android.os.Process.killProcess(Integer
                        .parseInt(processInfo.pid));
                recordLogServiceLog("kill another logcat process success,the process info is:"
                        + processInfo);
            }
        }
    }

    /**
     * 获取本程序的用户名称
     */
    private String getAppUser(String packName, List<ProcessInfo> allProcList) {
        for (ProcessInfo processInfo : allProcList) {
            if (processInfo.name.equals(packName)) {
                return processInfo.user;
            }
        }
        return null;
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

    /**
     * 运行PS命令得到进程信息
     *
     * @return USER PID PPID VSIZE RSS WCHAN PC NAME
     * root 1 0 416 300 c00d4b28 0000cd5c S /init
     */
    private List<String> getAllProcess() {
        List<String> orgProcList = new ArrayList<String>();
        Process proc = null;
        try {
            proc = Runtime.getRuntime().exec("ps");
            StreamConsumer errorConsumer = new StreamConsumer(proc.getErrorStream());
            StreamConsumer outputConsumer = new StreamConsumer(proc.getInputStream(), orgProcList);
            errorConsumer.start();
            outputConsumer.start();
            if (proc.waitFor() != 0) {
                Log.e(TAG, "getAllProcess proc.waitFor() != 0");
                recordLogServiceLog("getAllProcess proc.waitFor() != 0");
            }
        } catch (Exception e) {
            Log.e(TAG, "getAllProcess failed", e);
            recordLogServiceLog("getAllProcess failed");
        } finally {
            try {
                proc.destroy();
            } catch (Exception e) {
                Log.e(TAG, "getAllProcess failed", e);
                recordLogServiceLog("getAllProcess failed");
            }
        }
        return orgProcList;
    }

    /**
     * 开始收集日志信息
     */
    public void createLogCollector() {
        String logFilePath = ModuleFile.getFilePathString(ModuleFile.MEDIA_FILETYPE_LOG_APP);
        String logFileName = ModuleFile.getFileNameFromPath(logFilePath);
        List<String> commandList = new ArrayList<String>();
        commandList.add("logcat");
        commandList.add("-f");
        commandList.add(logFilePath);
        commandList.add("-v");
        commandList.add("time");
        commandList.add("*:I");
        //-----
        if (CURR_LOG_TYPE == ModuleFile.MEDIA_SAVETYPE_MEMORY) {
            CURR_INSTALL_LOG_NAME = logFileName;
            Log.d(TAG, "Log stored in memory, the path is:" + ModuleFile.getDir(ModuleFile.MEDIA_SAVETYPE_MEMORY) + File.separator + logFileName);
        } else {
            CURR_INSTALL_LOG_NAME = null;
            Log.d(TAG, "Log stored in SDcard, the path is:" + ModuleFile.getDir(ModuleFile.MEDIA_SAVETYPE_SDCARD) + File.separator + logFileName);
        }
        //-----
        //commandList.add("*:E");// 过滤所有的错误信息

        // 过滤指定TAG的信息
        // commandList.add("MyAPP:V");
        // commandList.add("*:S");
        try {
            process = Runtime.getRuntime().exec(
                    commandList.toArray(new String[commandList.size()]));
            recordLogServiceLog("start collecting the log,and log name is:"
                    + ModuleFile.getFileNameString(ModuleFile.MEDIA_FILETYPE_LOG_APP));
            // process.waitFor();
        } catch (Exception e) {
            Log.e(TAG, "CollectorThread == >" + e.getMessage(), e);
            recordLogServiceLog("CollectorThread == >" + e.getMessage());
        }
    }

    /**
     * 部署日志大小监控任务
     */
    private void deployLogSizeMonitorTask() {
        if (logSizeMoniting) {    //如果当前正在监控着，则不需要继续部署
            return;
        }
        logSizeMoniting = true;
        Intent intent = new Intent(MONITOR_LOG_SIZE_ACTION);
        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), MEMORY_LOG_FILE_MONITOR_INTERVAL, sender);
        Log.d(TAG, "deployLogSizeMonitorTask() succ !");
        Calendar calendar = Calendar.getInstance();
        recordLogServiceLog("deployLogSizeMonitorTask() succ ,start time is " + calendar.getTime().toLocaleString());
    }

    /**
     * 取消部署日志大小监控任务
     */
    private void cancelLogSizeMonitorTask() {
        logSizeMoniting = false;
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(MONITOR_LOG_SIZE_ACTION);
        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
        am.cancel(sender);

        Log.d(TAG, "canelLogSizeMonitorTask() succ");
    }

    /**
     * 检查日志文件大小是否超过了规定大小
     * 如果超过了重新开启一个日志收集进程
     */
    private void checkLogSize() {
        if (CURR_INSTALL_LOG_NAME != null && !"".equals(CURR_INSTALL_LOG_NAME)) {
            String path = ModuleFile.getDir(ModuleFile.MEDIA_SAVETYPE_MEMORY) + File.separator + CURR_INSTALL_LOG_NAME;
            File file = new File(path);
            if (!file.exists()) {
                return;
            }
            Log.d(TAG, "checkLog() ==> The size of the log is too big?");
            if (file.length() >= MEMORY_LOG_FILE_MAX_SIZE) {
                Log.d(TAG, "The log's size is too big!");
                new LogCollectorThread().start();
            }
        }
    }

    /**
     * 将日志文件转移到SD卡下面
     */
    private void moveLogfile() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            recordLogServiceLog("move file failed, sd card does not mount");
            return;
        }
        File file = new File(ModuleFile.getDir(ModuleFile.MEDIA_SAVETYPE_SDCARD));
        if (!file.isDirectory()) {
            boolean mkOk = file.mkdirs();
            if (!mkOk) {
                recordLogServiceLog("move file failed,dir is not created succ");
                return;
            }
        }

        file = new File(ModuleFile.getDir(ModuleFile.MEDIA_SAVETYPE_MEMORY));
        if (file.isDirectory()) {
            File[] allFiles = file.listFiles();
            for (File logFile : allFiles) {
                String fileName = logFile.getName();
                if (logServiceLogName.equals(fileName)) {
                    continue;
                }
                boolean isSucc = copy(logFile, new File(ModuleFile.getDir(ModuleFile.MEDIA_SAVETYPE_SDCARD)
                        + File.separator + fileName));
                if (isSucc) {
                    logFile.delete();
                    recordLogServiceLog("move file success,log name is:" + fileName);
                }
            }
        }
    }

    /**
     * 删除SDCard下过期的日志
     */
    private void deleteSDcardExpiredLog() {
        File file = new File(ModuleFile.getDir(ModuleFile.MEDIA_SAVETYPE_SDCARD));
        // 确定路径存在
        if (file.isDirectory()) {
            // 获取文件集合
            File[] allFiles = file.listFiles();
            // 循环处理文件
            for (File logFile : allFiles) {
                String fileName = logFile.getName();
                // 不处理logService,log文件
                if (logServiceLogName.equals(fileName)) {
                    continue;
                }
                // 获取文件日期字符串
                String createDateInfo = ModuleFile.getDateFromFileName(fileName);
                //
                boolean canDel;
                // 创建日历对象
                Calendar calendar = Calendar.getInstance();
                // 设定有效域---日期
                calendar.add(Calendar.DAY_OF_MONTH, -1 * SDCARD_LOG_FILE_SAVE_DAYS);//删除7天之前日志
                // 获取当前Data
                Date expiredDate = calendar.getTime();
                try {
                    // 提取文件创建日期
                    Date createDate = sdf.parse(createDateInfo);
                    // 日期比较
                    canDel = createDate.before(expiredDate);
                } catch (ParseException e) {
                    Log.e(TAG, e.getMessage(), e);
                    canDel = false;
                }
                //
                if (canDel) {
                    logFile.delete();
                    Log.d(TAG, "delete expired log success,the log path is:"
                            + logFile.getAbsolutePath());

                }
            }
        }
    }

    /**
     * 删除内存中的过期日志，删除规则：
     * 除了当前的日志和离当前时间最近的日志保存其他的都删除
     */
    private void deleteMemoryExpiredLog() {
        File file = new File(ModuleFile.getDir(ModuleFile.MEDIA_SAVETYPE_MEMORY));
        if (file.isDirectory()) {
            File[] allFiles = file.listFiles();
            Arrays.sort(allFiles, new FileComparator());
            for (int i = 0; i < allFiles.length - 2; i++) {    //"-2"保存最近的两个日志文件
                File _file = allFiles[i];
                if (logServiceLogName.equals(_file.getName()) || _file.getName().equals(CURR_INSTALL_LOG_NAME)) {
                    continue;
                }
                _file.delete();
                Log.d(TAG, "delete expired log success,the log path is:" + _file.getAbsolutePath());
            }
        }
    }

    /**
     * 拷贝文件
     */
    private boolean copy(File source, File target) {
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            if (!target.exists()) {
                boolean createSucc = target.createNewFile();
                if (!createSucc) {
                    return false;
                }
            }
            in = new FileInputStream(source);
            out = new FileOutputStream(target);
            byte[] buffer = new byte[8 * 1024];
            int count;
            while ((count = in.read(buffer)) != -1) {
                out.write(buffer, 0, count);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage(), e);
            recordLogServiceLog("copy file fail");
            return false;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage(), e);
                recordLogServiceLog("copy file fail");
                return false;
            }
        }
    }

    /**
     * 记录日志服务的基本信息 防止日志服务有错，在LogCat日志中无法查找
     * 此日志名称为LogService.log
     */
    private void recordLogServiceLog(String msg) {
        if (writer != null) {
            try {
                Date time = new Date();
                writer.write(myLogSdf.format(time) + " : " + msg);
                writer.write("\n");
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage(), e);
            }
        }
        Log.i(TAG, "recordLogServiceLog: " + msg);
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

    class StreamConsumer extends Thread {
        InputStream is;
        List<String> list;

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
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    class FileComparator implements Comparator<File> {
        public int compare(File file1, File file2) {
            if (logServiceLogName.equals(file1.getName())) {
                return -1;
            } else if (logServiceLogName.equals(file2.getName())) {
                return 1;
            }

            String createInfo1 = ModuleFile.getDateFromFileName(file1.getName());
            String createInfo2 = ModuleFile.getDateFromFileName(file2.getName());

            try {
                Date create1 = sdf.parse(createInfo1);
                Date create2 = sdf.parse(createInfo2);
                if (create1.before(create2)) {
                    return -1;
                } else {
                    return 1;
                }
            } catch (ParseException e) {
                return 0;
            }
        }
    }
}

