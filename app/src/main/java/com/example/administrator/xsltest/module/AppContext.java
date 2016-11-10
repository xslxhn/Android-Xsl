package com.example.administrator.xsltest.module;

import android.app.Application;
import android.content.Context;

/**
 * Created by Administrator on 2016/10/31.
 */

/*
 * 用于全局提取Context
 */

public class AppContext extends Application {
    private static Context instance;
    @Override
    public void onCreate()
    {
        instance = getApplicationContext();
    }
    public static Context getContext()
    {
        return instance;
    }
}
