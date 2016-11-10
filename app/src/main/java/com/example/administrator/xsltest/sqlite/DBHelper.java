/*
 * 创建了继承自SQLiteOpenHelper的用于访问数据库的子类
 * 作为维护和管理数据库的基类
 */
package com.example.administrator.xsltest.sqlite;

import android.content.Context;  
import android.database.sqlite.SQLiteDatabase;  
import android.database.sqlite.SQLiteOpenHelper;  

public class DBHelper extends SQLiteOpenHelper {  
	//数据库名称
    private static final String DATABASE_NAME = "test.db";  
    //数据库版本     
    private static final int DATABASE_VERSION = 1;  
    //构造函数
    public DBHelper(Context context) {  
        //CursorFactory设置为null,使用默认值  
        super(context, DATABASE_NAME, null, DATABASE_VERSION);  
    }  
    //数据库第一次被创建时onCreate会被调用  
    @Override  
    public void onCreate(SQLiteDatabase db) {  
        db.execSQL("CREATE TABLE IF NOT EXISTS person" +  
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, age INTEGER, info TEXT)");  
    }  
    //如果DATABASE_VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade  
    @Override  
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  
        db.execSQL("ALTER TABLE person ADD COLUMN other STRING");  
    }  
}  
