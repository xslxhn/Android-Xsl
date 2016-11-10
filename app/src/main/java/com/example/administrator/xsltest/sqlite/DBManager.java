/*
 * 	SQLiteDatabase说明:
 * 		1、db.insert(table, nullColumnHack, values)
			第一个参数是表名称，第二个参数是空列的默认值，第三个参数是ContentValues类型的一个封装了列名称和列值的Map；
		2、db.delete(table, whereClause, whereArgs)
			第一个参数是表名称，第二个参数是删除条件，第三个参数是删除条件值数组
		3、db.update(table, values, whereClause, whereArgs)
			第一个参数是表名称，第二个参数是更行列ContentValues类型的键值对（Map），第三个参数是更新条件（where字句），第四个参数是更新条件数组
		4、db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy)（下面有对该方法详细讲解）
		5、db.execSQL(sql) 
			执行任何SQL语句
		6、db.rawQuery(sql, selectionArgs)
	Cursor：
		Cursor是一个游标接口，提供了遍历查询结果的方法，如移动指针方法move()，获得列值方法getString()等.
		Cursor游标常用方法：
		1、getCount()   
			总记录条数
		2、isFirst()     
			判断是否第一条记录
		3、isLast()      
			判断是否最后一条记录
		4、moveToFirst()    
			移动到第一条记录 
		5、moveToLast()    
			移动到最后一条记录
		6、move(int offset)   
			移动到指定记录
		7、moveToNext()    
			移动到下一条记录
		8、moveToPrevious()    
			移动到上一条记录
		9、getColumnIndexOrThrow(String columnName)  
			根据列名称获得列索引
		10、getInt(int columnIndex)   
			获得指定列索引的int类型值
		11、getString(int columnIndex)   
			获得指定列缩影的String类型值		
 */
package com.example.administrator.xsltest.sqlite;

import java.util.ArrayList;  
import java.util.List;  


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
//import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//import android.database.sqlite.SQLiteQueryBuilder;
//import android.provider.BaseColumns;
//import android.text.TextUtils;
//import android.util.Log;

public class DBManager {
	
	private DBHelper helper;
	private SQLiteDatabase db;
	/**
     * 构造函数: 实例化DBHelper,并获取一个SQLiteDatabase对象
     */
    public DBManager(Context context) {
    	helper = new DBHelper(context);
    	//因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);  
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里 
    	db = helper.getWritableDatabase();
    }
    /** 
     * 加人列表 
     */  
    public void add(List<Person> persons) {  
        db.beginTransaction();  //开始事务  
        try {  
            for (Person person : persons) {  
                db.execSQL("INSERT INTO person VALUES(null, ?, ?, ?)", new Object[]{person.name, person.age, person.info});  
            }  
            db.setTransactionSuccessful();  //设置事务成功完成  
        } finally {  
            db.endTransaction();    //结束事务  
        }  
    }
    /** 
     * 更新人的年龄
     */  
    public void updateAge(Person person) {  
        ContentValues cv = new ContentValues();  
        cv.put("age", person.age);  
        db.update("person", cv, "name = ?", new String[]{person.name});  
    }  
    /** 
     * delete old person 
     * @param person 
     */  
    public void deleteOldPerson(Person person) {  
        db.delete("person", "age >= ?", new String[]{String.valueOf(person.age)});  
    }  
    /** 
     * query all persons, return list 
     * @return List<Person> 
     */  
    public List<Person> query() {  
        ArrayList<Person> persons = new ArrayList<Person>();  
        Cursor c = queryTheCursor();  
        while (c.moveToNext()) {  
            Person person = new Person();  
            person._id = c.getInt(c.getColumnIndex("_id"));  
            person.name = c.getString(c.getColumnIndex("name"));  
            person.age = c.getInt(c.getColumnIndex("age"));  
            person.info = c.getString(c.getColumnIndex("info"));  
            persons.add(person);  
        }  
        c.close();  
        return persons;  
    } 
    /** 
     * query all persons, return cursor 
     * @return  Cursor 
     */  
    public Cursor queryTheCursor() {  
        Cursor c = db.rawQuery("SELECT * FROM person", null);  
        return c;  
    }  
      
    /** 
     * close database 
     */  
    public void closeDB() {  
        db.close();  
    }  
    
    /**
     * 删除数据
     */
    /*
    public int delete(long id, String selection, String[] selectionArgs) {
        int count = 0;
        count = db.delete(DATABASE_TABLE, 
        		               DataColumns._ID + " = " + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), 
        		               selectionArgs);
        return count;
    }
    */
    /**
     * 添加记录
     */
    /*
    public long insert(ContentValues values) {
        // ---add a new book---
        long rowID = db.insert(DATABASE_TABLE, "", values);

        // ---if added successfully---
        if (rowID > 0) {
            return rowID;
        }
        throw new SQLException("Failed to insert row");
    }
    */
    

    /**
     * 直接执行查询的方法
     */
    /*
    public Cursor query(String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
        sqlBuilder.setTables(DATABASE_TABLE);

        if (sortOrder == null || sortOrder == "")
            sortOrder = DataColumns.TITLE;

        Cursor c = sqlBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);

        return c;
    }
    */

    /**
     * 更新记录的方法
     */
    /*
    public int update(long id, ContentValues values, String selection,
            String[] selectionArgs) {
        int count = 0;

        count = db.update(DATABASE_TABLE, values, DataColumns._ID
                + " = "
                + id
                + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')'
                        : ""), selectionArgs);

        return count;
    }
    */
}
