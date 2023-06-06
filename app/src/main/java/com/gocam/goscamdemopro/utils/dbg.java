package com.gocam.goscamdemopro.utils;

import android.text.TextUtils;
import android.util.Log;

public class dbg {
    private static final String TAG = dbg.class.getSimpleName();
    public static boolean debuggable = true;
    static String className;//类名
    static String methodName;//方法名
    static int lineNumber;//行数

    public static void D(String tag,String msg){
        if (!debuggable) {
            return;
        }
        Log.d(tag, msg);
    }

    public static void E(String tag,String msg){
        if (!debuggable) {
            return;
        }
        Log.e(tag, msg);
    }

    public static void I(String tag,String msg){
        if (!debuggable) {
            return;
        }
        Log.d(tag, msg);
    }

    public static void d(Object... params) {
        if (!debuggable) {
            return;
        }
        getMethodNames(new Throwable().getStackTrace());
        String log = createLog(getMsg(params));
        Log.d(className, log);
    }

    public static void i(Object... params) {
        if (!debuggable) {
            return;
        }
        getMethodNames(new Throwable().getStackTrace());
        String log = createLog(getMsg(params));
        Log.i(className, log);
    }

    public static void e(Object... params) {
        if (!debuggable) {
            return;
        }
        getMethodNames(new Throwable().getStackTrace());
        String log = createLog(getMsg(params));
        Log.e(className, log);
    }

    public static void w(Object... params) {
        if (!debuggable) {
            return;
        }
        getMethodNames(new Throwable().getStackTrace());
        String log = createLog(getMsg(params));
        Log.w(className, log);
    }

    public static void v(Object... params) {
        if (!debuggable) {
            return;
        }
        getMethodNames(new Throwable().getStackTrace());
        String log = createLog(getMsg(params));
        Log.v(className, log);
    }

    public static String[] format(StackTraceElement e, Object... params) {
        String tag;
        StringBuilder msg;
        if (e == null) {
            tag = TAG;
        } else {
            tag = String.format("%s.%s.[%4d]",
                    e.getFileName().replace(".java", ""), e.getMethodName(),
                    e.getLineNumber());
        }
        msg = new StringBuilder();
        if (params != null) {
            for (Object o : params) {
                if (o == null) {
                    msg.append("null, ");
                } else {
                    msg.append(o.toString() + ", ");
                }
            }
        }
        if (!TextUtils.isEmpty(msg.toString().trim())) {
            msg.append("\r\n");
        }
        return new String[]{tag, msg.toString()};
    }

    private static String getMsg(Object[] params) {
        StringBuilder builder = new StringBuilder();
        if (params == null || params.length <= 0) {
            return "not has params";
        } else {
            for (Object o : params) {
                if (o == null) {
                    builder.append(" this param is null ,");
                } else {
                    builder.append(o.toString() + ",");
                }
            }
        }
        return builder.toString();
    }

    private static String createLog(String log) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(methodName);
        buffer.append("(").append(className).append(":").append(lineNumber).append(")");
        buffer.append(log);
        return buffer.toString();
    }

    private static void getMethodNames(StackTraceElement[] sElements) {
        className = sElements[1].getFileName();
        methodName = sElements[1].getMethodName();
        lineNumber = sElements[1].getLineNumber();
    }
}