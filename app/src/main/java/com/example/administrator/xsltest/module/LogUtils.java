package com.example.administrator.xsltest.module;

import android.content.pm.ApplicationInfo;
import android.util.Log;

/**
 * Created by XSL on 2016/11/10.
 * 使用说明：
 * 1   使用方式：LogUtil.v("~test~");
 * 2   public static void v(String msg, int... printStackNum)
 * printStackNum为可选参数，表示打印多少层调用栈
 * 3   curLogLevel表示当前打印日志的最低等级
 * 比如设置为Log.INFO，则info以上的打印，以下的不打印，
 * 这样在Debug阶段，设置为Log.VERBOSE，在Release阶段设置为Log.ERROR或者Log.ASSERT,
 * 就可以不打印哪些调试日志
 * 4   这样使用Thread.currentThread().getStackTrace()，效率可能比较低.
 * 调试可以用，发行版本，最好关闭日志.
 * 其他说明：
 * log.v(String tag,String msg)    // 冗余消息 2
 * log.d(String tag,String msg)    // 调试消息 3
 * log.i(String tag,String msg)    // 普通消息 4
 * log.w(String tag,String msg)    // 警告消息 5
 * log.e(String tag,String msg)    // 错误消息 6
 */

public class LogUtils {
    private static StackTraceElement[] currentThread;
    private static String tagName;
    private static String msgT;
    private static String msgC;
    private static String callTraceStack;

    //--------------------等级操作
    // 设定默认等级
    private static int curLogLevel = Log.VERBOSE;

    // 获取等级
    public static int getCurLogLevel() {
        return curLogLevel;
    }

    // 设置等级
    public static void setCurLogLevel(int curLogLevel) {
        LogUtils.curLogLevel = curLogLevel;
    }

    // 初始化
    public static void init() {
        if (AppContext.getContext().getApplicationInfo().flags == ApplicationInfo.FLAG_DEBUGGABLE) {
            LogUtils.curLogLevel = Log.VERBOSE;
        } else {
            LogUtils.curLogLevel = Log.ERROR;
        }
    }

    //--------------------获取栈名
    public synchronized static void initTrace(String msg, int... isPrintStack) {
        int isPrintStackOne = isPrintStack.length > 0 ? isPrintStack[0] : 10;
        currentThread = Thread.currentThread().getStackTrace();
        // vm调用栈中此方法所在index：2：VMStack.java:-2:getThreadStackTrace()<--Thread.java:737:getStackTrace()<--
        int curentIndex = 4;
        String className = currentThread[curentIndex].getFileName();
        int endIndex = className.lastIndexOf(".");
        tagName = endIndex < 0 ? className : className.substring(0, endIndex);
        msgT = "[" + className + ":" + currentThread[curentIndex].getLineNumber() + ":"
                + currentThread[curentIndex].getMethodName() + "()]---";
        msgC = "msg:[" + msg + "]";
        if (isPrintStackOne > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("callTraceStack:[");
            for (int i = curentIndex; i < curentIndex + isPrintStackOne && i < currentThread.length; i++) {
                sb.append(currentThread[i].getFileName() + ":" + currentThread[i].getLineNumber() + ":"
                        + currentThread[i].getMethodName() + "()" + "<--");
            }
            sb.append("]");
            callTraceStack = sb.toString();
            msgC += callTraceStack;
        }
    }

    //--------------------函数
    public static void e(String msg, boolean printStack) {
        e(msg, printStack ? 105 : 0);
    }

    public static void w(String msg, boolean printStackNum) {
        w(msg, printStackNum ? 105 : 0);
    }

    public static void d(String msg, boolean printStackNum) {
        d(msg, printStackNum ? 105 : 0);
    }

    public static void v(String msg, boolean printStackNum) {
        v(msg, printStackNum ? 105 : 0);
    }

    public static void i(String msg, boolean printStackNum) {
        i(msg, printStackNum ? 105 : 0);
    }

    //--------------------函数
    public static void e(String msg, int... printStackNum) {
        if (curLogLevel > Log.ERROR) {
            return;
        }
        initTrace(msg, printStackNum.length > 0 ? printStackNum[0] : 0);
        Log.e(tagName, msgT + msgC);
    }

    public static void w(String msg, int... printStackNum) {
        if (curLogLevel > Log.WARN) {
            return;
        }
        initTrace(msg, printStackNum.length > 0 ? printStackNum[0] : 0);
        Log.w(tagName, msgT + msgC);
    }

    public static void d(String msg, int... printStackNum) {
        if (curLogLevel > Log.DEBUG) {
            return;
        }
        initTrace(msg, printStackNum.length > 0 ? printStackNum[0] : 0);
        Log.d(tagName, msgT + msgC);
    }

    public static void v(String msg, int... printStackNum) {
        if (curLogLevel > Log.VERBOSE) {
            return;
        }
        initTrace(msg, printStackNum.length > 0 ? printStackNum[0] : 0);
        Log.v(tagName, msgT + msgC);
    }

    public static void i(String msg, int... printStackNum) {
        if (curLogLevel > Log.INFO) {
            return;
        }
        initTrace(msg, printStackNum.length > 0 ? printStackNum[0] : 0);
        Log.i(tagName, msgT + msgC);
    }
}
