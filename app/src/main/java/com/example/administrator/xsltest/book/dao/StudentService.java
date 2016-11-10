/*
 * 数据库底层类
 */
package com.example.administrator.xsltest.book.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.administrator.xsltest.book.model.Student;
import com.example.administrator.xsltest.book.tool.CommonUtil;

/*
import xslPackage.XslTest.book.model.Student;
import xslPackage.XslTest.book.tool.CommonUtil;
*/
public class StudentService {

	private SQLiteHelper db = null;

	// 构造函数
	public StudentService(Context context) {
		db = new SQLiteHelper(context);
	}

	// 备份
	public void copy() {

		Cursor cursor = null;
		Student student = new Student();
		try {
			cursor = Select("1=1");
		} catch (SQLException ex) {
		}
		while (cursor.moveToNext()) {

			student.setPicture(cursor.getInt(cursor
					.getColumnIndex(DB.TABLES.STUDENT.FIELD.PICTURE)));
			student.setName(cursor.getString(cursor
					.getColumnIndex(DB.TABLES.STUDENT.FIELD.NAME)));
			student.setBirthday(cursor.getString(cursor
					.getColumnIndex(DB.TABLES.STUDENT.FIELD.BIRTHDAY)));
			student.setMobilePhone(cursor.getString(cursor
					.getColumnIndex(DB.TABLES.STUDENT.FIELD.MOBILEPHONE)));
			student.setOfficePhone(cursor.getString(cursor
					.getColumnIndex(DB.TABLES.STUDENT.FIELD.OFFICEPHONE)));
			student.setHomePhone(cursor.getString(cursor
					.getColumnIndex(DB.TABLES.STUDENT.FIELD.HOMEPHONE)));
			student.setJob(cursor.getString(cursor
					.getColumnIndex(DB.TABLES.STUDENT.FIELD.JOB)));
			student.setCompany(cursor.getString(cursor
					.getColumnIndex(DB.TABLES.STUDENT.FIELD.COMPANY)));

			String sql = String.format(DB.TABLES.STUDENT.SQL2.INSERT,
					student.getPicture(), student.getName(),
					student.getBirthday(), student.getMobilePhone(),
					student.getOfficePhone(), student.getHomePhone(),
					student.getJob(), student.getCompany());
			db.ExecuteSQL(sql);
			CommonUtil.Log_Info("book", "copy:" + sql);
		}
		cursor.close();
	}

	// 还原
	public void back() {

		Cursor cursor = null;
		Student student = new Student();
		try {
			cursor = SelectCopy("1=1");
		} catch (SQLException ex) {
		}
		while (cursor.moveToNext()) {

			student.setPicture(cursor.getInt(cursor
					.getColumnIndex(DB.TABLES.STUDENT.FIELD.PICTURE)));
			student.setName(cursor.getString(cursor
					.getColumnIndex(DB.TABLES.STUDENT.FIELD.NAME)));
			student.setBirthday(cursor.getString(cursor
					.getColumnIndex(DB.TABLES.STUDENT.FIELD.BIRTHDAY)));
			student.setMobilePhone(cursor.getString(cursor
					.getColumnIndex(DB.TABLES.STUDENT.FIELD.MOBILEPHONE)));
			student.setOfficePhone(cursor.getString(cursor
					.getColumnIndex(DB.TABLES.STUDENT.FIELD.OFFICEPHONE)));
			student.setHomePhone(cursor.getString(cursor
					.getColumnIndex(DB.TABLES.STUDENT.FIELD.HOMEPHONE)));
			student.setJob(cursor.getString(cursor
					.getColumnIndex(DB.TABLES.STUDENT.FIELD.JOB)));
			student.setCompany(cursor.getString(cursor
					.getColumnIndex(DB.TABLES.STUDENT.FIELD.COMPANY)));

			String sql = String.format(DB.TABLES.STUDENT.SQL.INSERT,
					student.getPicture(), student.getName(),
					student.getBirthday(), student.getMobilePhone(),
					student.getOfficePhone(), student.getHomePhone(),
					student.getJob(), student.getCompany());
			db.ExecuteSQL(sql);
			CommonUtil.Log_Info("book", "back:" + sql);
		}
		cursor.close();

	}

	// 插入信息(用于新建)
	public void Insert(Student student) throws SQLException {
		String sql = String.format(DB.TABLES.STUDENT.SQL.INSERT,
				student.getPicture(), student.getName(), student.getBirthday(),
				student.getMobilePhone(), student.getOfficePhone(),
				student.getHomePhone(), student.getJob(), student.getCompany());
		db.ExecuteSQL(sql);
		CommonUtil.Log_Info("book", "insert:" + sql);
	}

	// 更新信息(用于修改)
	public void Update(Student student) throws SQLException {
		String sql = String.format(DB.TABLES.STUDENT.SQL.UPDATE,
				student.getPicture(), student.getName(), student.getBirthday(),
				student.getMobilePhone(), student.getOfficePhone(),
				student.getHomePhone(), student.getJob(), student.getCompany(),
				student.getId());
		db.ExecuteSQL(sql);
		CommonUtil.Log_Info("book", "update:" + sql);
	}

	// 删除信息
	public void Delete(int id) throws SQLException {
		String sql = String.format(DB.TABLES.STUDENT.SQL.DELETE, id);
		db.ExecuteSQL(sql);
		CommonUtil.Log_Info("book", "delete:" + sql);
	}

	// 删除所有信息
	public void DeleteAll() throws SQLException {
		String sql = String.format(DB.TABLES.STUDENT.SQL.DELETEALL);
		db.ExecuteSQL(sql);		
		CommonUtil.Log_Info("book", "DeleteAll:" + sql);
	}
	// 删除所有备份信息
	public void DeleteAllCopy() throws SQLException {

		String sql = String.format(DB.TABLES.STUDENT.SQL2.DELETEALL);
		db.ExecuteSQL(sql);
		CommonUtil.Log_Info("book", "DeleteBackAll:" + sql);
	}

	// 查询主表(表名称,查询列,查询条件 ,查询条件的? ,查询出来的数据是否需要分组,聚合操作,排序 )
	public Cursor Select(String condition) throws SQLException {
		SQLiteDatabase db1 = db.getReadableDatabase();
		Cursor cursor = null;

		cursor = db1.query(DB.TABLES.STUDENT.TABLENAME, new String[] {
				DB.TABLES.STUDENT.FIELD.ID, DB.TABLES.STUDENT.FIELD.PICTURE,
				DB.TABLES.STUDENT.FIELD.NAME, DB.TABLES.STUDENT.FIELD.BIRTHDAY,
				DB.TABLES.STUDENT.FIELD.MOBILEPHONE,
				DB.TABLES.STUDENT.FIELD.OFFICEPHONE,
				DB.TABLES.STUDENT.FIELD.HOMEPHONE, DB.TABLES.STUDENT.FIELD.JOB,
				DB.TABLES.STUDENT.FIELD.COMPANY }, condition, null, null, null,
				null);
		CommonUtil.Log_Info("book", "Select:" + cursor);
		return cursor;
	}

	// 查询副表
	public Cursor SelectCopy(String condition) throws SQLException {
		SQLiteDatabase db1 = db.getReadableDatabase();
		Cursor cursor = null;

		cursor = db1.query(DB.TABLES.STUDENT.TABLENAME2, new String[] {
				DB.TABLES.STUDENT.FIELD.ID, DB.TABLES.STUDENT.FIELD.PICTURE,
				DB.TABLES.STUDENT.FIELD.NAME, DB.TABLES.STUDENT.FIELD.BIRTHDAY,
				DB.TABLES.STUDENT.FIELD.MOBILEPHONE,
				DB.TABLES.STUDENT.FIELD.OFFICEPHONE,
				DB.TABLES.STUDENT.FIELD.HOMEPHONE, DB.TABLES.STUDENT.FIELD.JOB,
				DB.TABLES.STUDENT.FIELD.COMPANY }, condition, null, null, null,
				null);
		CommonUtil.Log_Info("book", "SelectBack:" + cursor);
		return cursor;
	}

}
