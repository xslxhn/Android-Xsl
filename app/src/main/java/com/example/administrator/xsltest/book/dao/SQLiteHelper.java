/*
 * 创建了继承自SQLiteOpenHelper的用于访问数据库的子类
 * 作为维护和管理数据库的基类
 */
package com.example.administrator.xsltest.book.dao;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {
	// 构造函数
	public SQLiteHelper(Context context) {
		super(context, DB.DATABASE_NAME, null, DB.DATABASE_VERSION);
	}

	// 数据库第一次被创建时onCreate会被调用
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DB.TABLES.STUDENT.SQL.CREATETABLE);
		db.execSQL(DB.TABLES.STUDENT.SQL2.CREATETABLE);
	}

	// 如果DATABASE_VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// 终止当前数据库表
		db.execSQL(DB.TABLES.STUDENT.SQL.DROPTABLE);
		// 重新创建数据库表
		this.onCreate(db);
	}

	// 用于执行增删改操作(以insert,update,delete打头的sql语句)
	public void ExecuteSQL(String sql) {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.execSQL(sql);
		} catch (SQLException ex) {
			throw ex;
		} finally {
			db.close();
		}
	}
}
