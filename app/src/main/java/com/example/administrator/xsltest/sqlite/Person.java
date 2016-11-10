/*
 * 为了方便我们面向对象的使用数据，我们建一个Person类，对应person表中的字段
 */
package com.example.administrator.xsltest.sqlite;

public class Person {
	public int _id;
	public String name;
	public int age;
	public String info;
	
	public Person(){		
	}
	public Person(String name, int age, String info) {  
		this.name = name;  
	    this.age = age;  
	    this.info = info;  
	} 	
}
