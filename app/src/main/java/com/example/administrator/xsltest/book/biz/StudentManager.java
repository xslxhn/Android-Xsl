/*
 * 数据库应用层类
 */
package com.example.administrator.xsltest.book.biz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.administrator.xsltest.book.dao.DB;
import com.example.administrator.xsltest.book.dao.StudentService;
import com.example.administrator.xsltest.book.model.Student;
import com.example.administrator.xsltest.book.tool.CommonUtil;

public class StudentManager {
	// 与下一层的接口
	private StudentService dal = null;
	// 定义数据库
	public static SQLiteDatabase dbInstance;
	// 定义名称
	public static final String DB_TABLENAME = "tblStudent";

	// 构造函数(实例化接口类)
	public StudentManager(Context context) {
		dal = new StudentService(context);
	}

	// Add()-->1-->增加一条联系人信息
	public boolean Add(Student student) {
		try {
			dal.Insert(student);
			return true;
		} catch (SQLException e) {
			CommonUtil
					.Log_Error("book", "StudentManager-Add1" + e.getMessage());
			return false;
		}
	}

	// Add()-->2-->增加一条联系人信息
	public boolean Add(int Picture, String Name, String Birthday,
			String MobilePhone, String OfficePhone, String HomePhone,
			String Job, String Company) {
		try {
			Student student = new Student(Picture, Name, Birthday, MobilePhone,
					OfficePhone, HomePhone, Job, Company);
			dal.Insert(student);
			return true;
		} catch (SQLException e) {
			CommonUtil
					.Log_Error("book", "StudentManager-Add2" + e.getMessage());
			return false;

		}
	}

	// Modify()-->1-->修改一条联系人
	public boolean Modify(Student student) {
		try {
			dal.Update(student);
			return true;
		} catch (Exception e) {
			CommonUtil.Log_Error("book",
					"StudentManager-Modify" + e.getMessage());
			return false;
		}
	}

	// Modify()-->2-->修改一条联系人
	public boolean Modify(int id, int Picture, String Name, String Birthday,
			String MobilePhone, String OfficePhone, String HomePhone,
			String Job, String Company) {
		try {
			Student student = new Student(id, Picture, Name, Birthday,
					MobilePhone, OfficePhone, HomePhone, Job, Company);
			dal.Update(student);
			return true;
		} catch (SQLException e) {
			CommonUtil.Log_Error("book",
					"StudentManager-Modify" + e.getMessage());
			return false;

		}
	}

	// Delete()-->删除一条联系人
	public boolean Delete(int id) {
		try {
			dal.Delete(id);
			return true;
		} catch (Exception e) {
			CommonUtil.Log_Error("book",
					"StudentManager-Delete" + e.getMessage());
			return false;
		}
	}

	// ----------所有人操作----------Begin
	// 获取所有联系人列表
	public List<Student> GetAllStudent() {
		String condition = "1=1";
		CommonUtil.Log_Info("book", "GetAllStudent");
		return this.GetStudentByCondition(condition);
	}

	// 获取所有联系人的ID
	public ArrayList<Integer> GetAllStudent_id() {

		ArrayList<Integer> AllID = new ArrayList<Integer>();
		Cursor cursor = null;
		try {
			cursor = dal.Select("1=1");
		} catch (SQLException ex) {
			return null;
		}
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor
					.getColumnIndex(DB.TABLES.STUDENT.FIELD.ID));
			AllID.add(id);
		}
		cursor.close();
		return AllID;

	}

	// 获取所有联系人列表的起始光标
	public Cursor GetAllStudent_Cursor() {
		Cursor cursor = null;
		try {
			cursor = dal.Select("1=1");
			return cursor;
		} catch (SQLException ex) {
			return null;
		}
	}
	// ----------所有人操作----------End
	// 通过ID获取联系人信息
	public Student GetStudentById(int id) {
		String condition = DB.TABLES.STUDENT.FIELD.ID + "=" + id;
		try {
			return this.GetStudentByCondition(condition).get(0);
		} catch (Exception ex) {
			return null;
		}
	}

	// 通过名称(电话)获取联系人列表(name like %(name)% or mobilephone like %(name)%)
	public List<Student> GetStudentByNameOrMobilephone(String Name) {
		String condition = DB.TABLES.STUDENT.FIELD.NAME + " like '%" + Name
				+ "%'" + " or " + DB.TABLES.STUDENT.FIELD.MOBILEPHONE
				+ " like '%" + Name + "%'";
		System.out.println(condition);
		return GetStudentByCondition(condition);

	}

	// 局部函数: 通过特定条件获取联系人列表
	private List<Student> GetStudentByCondition(String condition) {
		List<Student> list = new ArrayList<Student>();
		Cursor cursor = null;
		try {
			cursor = dal.Select(condition);
		} catch (SQLException ex) {
			return null;
		}
		while (cursor.moveToNext()) {
			Student student = new Student();
			student.setId(cursor.getInt(cursor
					.getColumnIndex(DB.TABLES.STUDENT.FIELD.ID)));
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
			list.add(student);
		}
		cursor.close();
		return list;
	}

	// 备份
	public boolean copy() {
		dal.copy();
		return true;
	}

	// 恢复
	public boolean back() {
		dal.back();
		return true;
	}

	// 删除所有备份
	public void DeleteAllCopy() {
		dal.DeleteAllCopy();
	}

	// 删除所有
	public void DeleteAll() {
		dal.DeleteAll();
	}

}
