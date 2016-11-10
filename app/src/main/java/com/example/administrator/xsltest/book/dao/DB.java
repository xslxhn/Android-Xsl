/*
 * 数据库接口定义: 有点像C语言的宏定义或枚举的结合
 */
package com.example.administrator.xsltest.book.dao;

import android.database.Cursor;

public interface DB {
	// 数据库名称
	public static final String DATABASE_NAME = "xslbook.db";
	// 数据库版本
	public static final int DATABASE_VERSION = 2;

	// 附录
	public interface TABLES {
		// 学生
		public interface STUDENT {
			// 表名称(1)
			public static final String TABLENAME = "tblStudent";

			// 域(1)
			public interface FIELD {
				// ID
				public static final String ID = "id";
				// 图片
				public static final String PICTURE = "Picture";
				// 名称
				public static final String NAME = "Name";
				// 出生年月
				public static final String BIRTHDAY = "Birthday";
				// 移动电话
				public static final String MOBILEPHONE = "MobilePhone";
				// 办公电话
				public static final String OFFICEPHONE = "OfficePhone";
				// 家庭电话
				public static final String HOMEPHONE = "HomePhone";
				// 职务职称
				public static final String JOB = "Job";
				// 公司名称
				public static final String COMPANY = "Company";
			}

			// SQL语言(1)
			public interface SQL {
				// 创建表: create table 表名称(列名称1 数据类型,列名称2 数据类型,...)
				public static final String CREATETABLE = "create table "
						+ TABLENAME
						+ "(id integer primary key autoincrement,Picture int,Name string,Birthday string,MobilePhone string,OfficePhone string,HomePhone string,Job string,Company string)";
				// 删除表: drop table if exists 表名称
				public static final String DROPTABLE = "drop table if exists "
						+ TABLENAME;
				// 插入行: insert into 表名称(列1,列2...)values(值1,值2...)
				public static final String INSERT = "insert into tblStudent(Picture,Name,Birthday,MobilePhone,OfficePhone,HomePhone,Job,Company) values('%s','%s','%s','%s','%s','%s','%s','%s')";
				// 修改(指定行): update 表名称 set 列名称=新值,...,where 列名称=指定行值
				public static final String UPDATE = "update tblStudent set picture='%s',name='%s',Birthday='%s',mobilephone='%s',officephone='%s',homephone='%s',job='%s',company='%s' where id=%s";
				// 删除(指定行): delete from 表名称 where 列名称=指定行值
				public static final String DELETE = "delete from tblStudent where id=%s";
				// 删除所有: delete from 表名称 where 1=1
				public static final String DELETEALL = "delete from tblStudent where 1=1";
				// 选取数据: select * from 表名称 where 条件
				public static final String SELECT = "select * from tblStudent where %s";
			}

			// 表名称(2)
			public static final String TABLENAME2 = "tblStudentCopy";

			// 域(2)
			public interface FIELD2 {
				public static final String ID = "id";
				public static final String PICTURE = "Picture";
				public static final String NAME = "Name";
				public static final String BIRTHDAY = "Birthday";
				public static final String MOBILEPHONE = "MobilePhone";
				public static final String OFFICEPHONE = "OfficePhone";
				public static final String HOMEPHONE = "HomePhone";
				public static final String JOB = "Job";
				public static final String COMPANY = "Company";
			}

			// SQL语言(2)
			public interface SQL2 {
				// 创建
				public static final String CREATETABLE = "create table "
						+ TABLENAME2
						+ "(id integer primary key autoincrement,Picture int,Name string,Birthday string,MobilePhone string,OfficePhone string,HomePhone string,Job string,Company string)";
				//
				public static final String DROPTABLE = "drop table if exists "
						+ TABLENAME2;
				// 插入
				public static final String INSERT = "insert into tblStudentCopy(Picture,Name,Birthday,MobilePhone,OfficePhone,HomePhone,Job,Company) values('%s','%s','%s','%s','%s','%s','%s','%s')";
				// 更新
				public static final String UPDATE = "update tblStudentCopy set picture='%s',name='%s',Birthday='%s',mobilephone='%s',officephone='%s',homephone='%s',job='%s',company='%s' where id=%s";
				// 删除
				public static final String DELETE = "delete from tblStudentCopy where id=%s";
				// 删除所有
				public static final String DELETEALL = "delete from tblStudentCopy where 1=1";
				// 选择
				public static final String SELECT = "select * from tblStudentCopy where %s";
			}
		}
	}

	public Cursor select();

}
