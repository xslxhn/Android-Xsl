/*
* SQLite实例说明:
* SQLiteActivity-->DBManager(类-->管理数据库)-->DBHelper(类)
* 				-->Person(类-->用户数据结构)
*/
package com.example.administrator.xsltest.sqlite;

import java.util.ArrayList;  
import java.util.HashMap;  
import java.util.List;  
import java.util.Map;  

//import xslPackage.XslTest.R;
//import xslPackage.XslTest.R.id;
//import xslPackage.XslTest.R.layout;

import android.app.Activity;  
import android.database.Cursor;  
import android.database.CursorWrapper;  
import android.os.Bundle;  
import android.view.View;  
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;  
import android.widget.SimpleAdapter;  
import android.widget.SimpleCursorAdapter;  
import android.widget.TextView;

import com.example.administrator.xsltest.R;

public class SQLiteActivity extends Activity {
	EditText et_input1,et_input2,et_input3,et_output;
	TextView txtV1;
	Button button;
    private DBManager mgr;  
    private ListView listView;     
    @Override
    public void onCreate(Bundle savedInstanceState) {        
    	super.onCreate(savedInstanceState);  
        setContentView(R.layout.test1);           
        //------------------------输入界面初始化
        txtV1 = (TextView)findViewById(R.id.Tv_Test1_1);
        txtV1.setText("输入名称:");
        txtV1 = (TextView)findViewById(R.id.Tv_Test1_2);
        txtV1.setText("输入年龄:");
        txtV1 = (TextView)findViewById(R.id.Tv_Test1_3);
        txtV1.setText("输入性别:");
        //输入文本框初始化		
        et_input1 = (EditText)findViewById(R.id.Et_Test1_Input_1);
        et_input1.setText("");
        et_input1 = (EditText)findViewById(R.id.Et_Test1_Input_2);
        et_input1.setText("");
        et_input1 = (EditText)findViewById(R.id.Et_Test1_Input_3);
        et_input1.setText("");
        //------------------------
        //------------------------第一行按键
        //文本		
  		txtV1 = (TextView)findViewById(R.id.Tv_Test1_11);
  		txtV1.setText("有效按键");
  		//监听按键---Add		
  		button = (Button)findViewById(R.id.Btn_Test1_11);
  		button.setText("增加成员");	
  		button.setOnClickListener(new OnClickListener() {
  			public void onClick(View v) {
  				ArrayList<Person> persons = new ArrayList<Person>();    	          
  		        Person person1 = new Person();//("徐松亮", 30, "Boy");   
  		        et_input1 = (EditText)findViewById(R.id.Et_Test1_Input_1);
  		        person1.name=et_input1.getText().toString();
				et_input1 = (EditText)findViewById(R.id.Et_Test1_Input_2);
				person1.age = Integer.parseInt(et_input1.getText().toString());  
		        et_input1 = (EditText)findViewById(R.id.Et_Test1_Input_3);
		        person1.info=et_input1.getText().toString();
  		        persons.add(person1);  
  		        mgr.add(persons);
  			}
  		});
  	    //监听按键---update		
  		button = (Button)findViewById(R.id.Btn_Test1_12);
  		button.setText("更新成员");	
  		button.setOnClickListener(new OnClickListener() {
  			public void onClick(View v) {
  				Person person = new Person();  
  		        person.name = "徐电航";  
  		        person.age = 3;  
  		        mgr.updateAge(person);  
  			}
  		});
  		//监听按键---delete		
  		button = (Button)findViewById(R.id.Btn_Test1_13);
  		button.setText("指定删除");	
  		button.setOnClickListener(new OnClickListener() {
  			public void onClick(View v) {  				
  				Person person = new Person();  				
  				et_input1 = (EditText)findViewById(R.id.Et_Test1_Input_1);
  				person.name=et_input1.getText().toString();
  				et_input1 = (EditText)findViewById(R.id.Et_Test1_Input_2);
  		        person.age = Integer.parseInt(et_input1.getText().toString());  
  		        et_input1 = (EditText)findViewById(R.id.Et_Test1_Input_3);
  		        person.info=et_input1.getText().toString();  		        
  		        mgr.deleteOldPerson(person);  
  			}
  		});
  		//------------------------第二行按键
        //文本		
  		txtV1 = (TextView)findViewById(R.id.Tv_Test1_21);
  		txtV1.setText("有效按键");
  		//监听按键---query		
  		button = (Button)findViewById(R.id.Btn_Test1_21);
  		button.setText("更新显示");	
  		button.setOnClickListener(new OnClickListener() {
  			public void onClick(View v) {
  				query(v);
  			}
  		});
  		//监听按键---queryTheCursor		
  		button = (Button)findViewById(R.id.Btn_Test1_22);
  		button.setText("只显名称");	
  		button.setOnClickListener(new OnClickListener() {
  			public void onClick(View v) {
  				queryTheCursor(v);
  			}
  		});
  	    //监听按键---删除所有		
  		button = (Button)findViewById(R.id.Btn_test1_23);
  		button.setText("删除所有");	
  		button.setOnClickListener(new OnClickListener() {
  			public void onClick(View v) {
  				mgr.deleteOldPerson(null);
  			}
  		});
        //------------------------
        listView = (ListView) findViewById(R.id.LV_Test1_1); 
        //初始化DBManager  
        mgr = new DBManager(this);
    }
    @Override  
    protected void onDestroy() {  
        super.onDestroy();  
        //应用的最后一个Activity关闭时应释放DB  
        mgr.closeDB();  
    } 
 
    /*
     * 按键query的单击的句柄  (询问: 更新显示)
     * 将数据库中的数据显示到Listview
     */  
    public void query(View view) {  
    	//装载persons列表
        List<Person> persons = mgr.query();  
        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();  
        for (Person person : persons) {  
            HashMap<String, String> map = new HashMap<String, String>();  
            map.put("name", person.name);  
            map.put("info", person.age + " years old, " + person.info);  
            list.add(map);  
        }  
        SimpleAdapter adapter = new SimpleAdapter(this, list, android.R.layout.simple_list_item_2,  
                    new String[]{"name", "info"}, new int[]{android.R.id.text1, android.R.id.text2});  
        listView.setAdapter(adapter);  
    }
    /*
     * 按键queryTheCursor的单击的句柄  
     */
    public void queryTheCursor(View view) {  
        Cursor c = mgr.queryTheCursor(); 
        //托付给activity根据自己的生命周期去管理Cursor的生命周期  
        startManagingCursor(c); 
        //数据源加载
        CursorWrapper cursorWrapper = new CursorWrapper(c) {  
            @Override  
            public String getString(int columnIndex) {  
                //将简介前加上年龄  
                if (getColumnName(columnIndex).equals("info")) {  
                    int age = getInt(getColumnIndex("age"));  
                    return age + " years old, " + super.getString(columnIndex);  
                }  
                return super.getString(columnIndex);  
            }  
        };  
        //确保查询结果中有"_id"列  
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
        		                                              //布局文件(列表的每一行布局,下面是系统定义好的布局,只显示一行文字)
        		                                              android.R.layout.simple_list_item_1,
        		                                              //数据源
                                                              cursorWrapper, 
                                                              //包含数据库的列的String型数组
                                                              new String[]{"name"}, 
                                                              //包含布局文件中对应组件id的int型数组。
                                                              new int[]{android.R.id.text1});  
        listView.setAdapter(adapter);  
    }  
}