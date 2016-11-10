/*
 * 类说明: 学生档案
 */
package com.example.administrator.xsltest.book.model;

public class Student {
	// ID
	private int id;
	// 照片
	private int picture;
	// 姓名
	private String Name;
	// 生日
	private String Birthday;
	// 手机
	private String MobilePhone;
	// 办公室电话
	private String OfficePhone;
	// 家庭电话
	private String HomePhone;
	// 职务职称
	private String Job;
	// 单位名称
	private String Company;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPicture() {
		return picture;
	}

	public void setPicture(int picture) {
		this.picture = picture;
	}

	public String getName() {
		return Name;
	}

	public void setName(String Name) {
		this.Name = Name;
	}

	public String getBirthday() {
		return Birthday;
	}

	public void setBirthday(String Birthday) {
		this.Birthday = Birthday;
	}

	public String getMobilePhone() {
		return MobilePhone;
	}

	public void setMobilePhone(String MobilePhone) {
		this.MobilePhone = MobilePhone;
	}

	public String getOfficePhone() {
		return OfficePhone;
	}

	public void setOfficePhone(String OfficePhone) {
		this.OfficePhone = OfficePhone;
	}

	public String getHomePhone() {
		return HomePhone;
	}

	public void setHomePhone(String HomePhone) {
		this.HomePhone = HomePhone;
	}

	public String getJob() {
		return Job;
	}

	public void setJob(String Job) {
		this.Job = Job;
	}

	public String getCompany() {
		return Company;
	}

	public void setCompany(String Company) {
		this.Company = Company;
	}

	// 构造函数1
	public Student() {
	}

	// 构造函数2
	public Student(int id, int picture, String name, String Birthday,
			String mobilephone, String officephone, String homephone,
			String job, String company) {
		this.id = id;
		this.picture = picture;
		this.Name = name;
		this.Birthday = Birthday;
		this.MobilePhone = mobilephone;
		this.OfficePhone = officephone;
		this.HomePhone = homephone;
		this.Job = job;
		this.Company = company;
	}

	// 构造函数3
	public Student(int picture, String name, String Birthday,
			String mobilephone, String officephone, String homephone,
			String job, String company) {
		this.picture = picture;
		this.Name = name;
		this.Birthday = Birthday;
		this.MobilePhone = mobilephone;
		this.OfficePhone = officephone;
		this.HomePhone = homephone;
		this.Job = job;
		this.Company = company;
	}

}
