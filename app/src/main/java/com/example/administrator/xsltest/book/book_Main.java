package com.example.administrator.xsltest.book;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.xsltest.R;
import com.example.administrator.xsltest.book.biz.StudentManager;
import com.example.administrator.xsltest.book.model.Student;
import com.example.administrator.xsltest.module.LogUtils;

public class book_Main extends Activity {
    boolean notSetListener = true;
    private StudentManager studentManager = null;
    private ListView lvStudent = null;
    List<Student> list = null;
    LinearLayout searchLinearout;// 装搜索框的linearlayout,默认情况下visibility=gone
    EditText et_search;// 搜索框
    ArrayList<Integer> seleteId;// 存储标记条目的id号
    ArrayList<Integer> deleteIdAll;// 存储所有ID号用于全部删除
    TextView tv = null;
    // ----------主菜单(屏幕下方的工具栏)----------Begin
    // 指针
    GridView bottomMenuGrid;
    // 菜单文字
    String[] bottom_menu_itemName = {"增加", "查找", "删除", "菜单", "退出"};
    // 菜单图片
    int[] bottom_menu_itemSource = {
            R.drawable.book_menu_new_user, // 主菜单图片
            R.drawable.book_menu_search,
            R.drawable.book_menu_delete,
            R.drawable.book_reserve12,
            R.drawable.book_menu_exit};
    // ----------主菜单(屏幕下方的工具栏)----------End
    // ----------子菜单对话框---------------------Begin
    // 指针
    GridView mainMenuGrid;
    // 子对话框的定义
    AlertDialog mainMenuDialog;
    // 子对话框视图
    View mainMenuView;
    // 菜单文字
    String[] main_menu_itemName = {"显示所有", "删除所有", "备份数据", "还原数据", "更新", "后退",
            "测试"};
    // 菜单图片
    int[] main_menu_itemSource = {
            R.drawable.book_showall,
            R.drawable.book_menu_delete,// 子菜单图片
            R.drawable.book_menu_backup,
            R.drawable.book_menu_restore,
            R.drawable.book_menu_fresh,
            R.drawable.book_menu_return,
            R.drawable.book_menu_return};

    // ----------子菜单对话框---------------------End
    // ----------自动生成联系人进度条-------------Begin
    private ProgressDialog mProgressDialog;
    final int maxprogress = 100;
    final int ProgressTag = 0x0001;
    public boolean loop = true;
    // ----------自动生成联系人进度条-------------End
    // 屏蔽指定错误警告
    @SuppressLint("HandlerLeak")
    private Handler mProgressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ProgressTag:
                    if (mProgressDialog.getProgress() >= maxprogress - 1) {
                        mProgressDialog.dismiss();
                    } else {
                        mProgressDialog.incrementProgressBy(1);
                    }
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置主页面
        setContentView(R.layout.book_main);
        // 新建数据库管理类
        studentManager = new StudentManager(this);
        // 列表显示器实例化
        lvStudent = (ListView) findViewById(R.id.lvStudent);
        // 列表实例化
        list = studentManager.GetAllStudent();
        // 信息实例化
        tv = (TextView) findViewById(R.id.Tv_BookInfo);
        // 搜索框初始化
        searchLinearout = (LinearLayout) findViewById(R.id.ll_search);
        // 刷新列表
        if (list.size() == 0) {
            setTitle("没有数据");
        } else
            bindListView(list);
        // 底部菜单初始化
        // ---实例化 bottomMenuGrid
        bottomMenuGrid = (GridView) findViewById(R.id.gv_buttom_menu);
        // ---设置背景
        bottomMenuGrid.setBackgroundResource(R.drawable.book_channelgallery_bg);
        // ---设置每行列数
        bottomMenuGrid.setNumColumns(5);
        // ---垂直间隔
        bottomMenuGrid.setVerticalSpacing(10);
        // ---水平间隔
        bottomMenuGrid.setHorizontalSpacing(10);
        // ---设置菜单Adapter(图形和文字)
        bottomMenuGrid.setAdapter(getMenuAdapter(bottom_menu_itemName,
                bottom_menu_itemSource));
        // ---监听底部菜单选项
        bottomMenuGrid.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                switch (arg2) {
                    case 0: // 添加操作
                        startActivityForResult(new Intent(book_Main.this,
                                StudentManagerActvity.class), 1);
                        break;
                    case 1:// 查找操作(模糊查找)
                        // 实例化searchLinearout
                        // searchLinearout = (LinearLayout)
                        // findViewById(R.id.ll_search);
                        // 实例化et_search
                        et_search = (EditText) findViewById(R.id.et_search);
                        // 监听按键
                        et_search.setOnKeyListener(new OnKeyListener() {
                            @Override
                            public boolean onKey(View v, int keyCode, KeyEvent event) {
                                // 获取用户输入的字符串
                                String condition = et_search.getText().toString();
                                // 如果字符串为空,则显示所有信息
                                if (condition.equals("")) {
                                    bindListView(studentManager.GetAllStudent());
                                }
                                // 如果字符串不为空,则显示满足条件的
                                else {
                                    List<Student> result = studentManager
                                            .GetStudentByNameOrMobilephone(condition);
                                    bindListView(result);
                                }
                                return false;
                            }
                        });
                        // 如果搜索框可见,则隐藏
                        if (searchLinearout.getVisibility() == View.VISIBLE) {
                            searchLinearout.setVisibility(View.GONE);
                        }
                        // 如果搜索框隐藏,则可见
                        else {
                            searchLinearout.setVisibility(View.VISIBLE);
                            et_search.requestFocus();
                            et_search.selectAll();
                        }
                        break;
                    case 2:// 删除操作
                        delete(false, seleteId, "没有标记任何记录\n长按一条记录即可标记", "确定要删除标记的");
                        break;
                    case 3:// 子菜单弹出
                        // 隐藏搜索框
                        searchLinearout.setVisibility(View.GONE);
                        // 装载菜单项
                        mainMenuDialog.show();
                        break;
                    case 4:// 退出操作(系统函数)
                        finish();
                        break;
                }
            }
        });
        // 主菜单初始化
        // ---作用类似于 findViewById(),用来找layout文件夹下的xml布局文件，并且实例化
        LayoutInflater li = LayoutInflater.from(this);
        // ---创建主菜单的视图
        mainMenuView = li.inflate(R.layout.book_main_menu_grid, null);
        // ---创建主菜单对话框
        mainMenuDialog = new AlertDialog.Builder(this).setView(mainMenuView)
                .create();
        // ---根据主菜单视图，拿到视图文件中的GridView，然后再往里面放Adapter
        mainMenuGrid = (GridView) mainMenuView.findViewById(R.id.gridview);
        // ---设置菜单Adapter(图形和文字)
        mainMenuGrid.setAdapter(getMenuAdapter(main_menu_itemName,
                main_menu_itemSource));
        // ---监听菜单选项
        mainMenuGrid.setOnItemClickListener(new OnItemClickListener() {
            @SuppressWarnings("deprecation")
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                switch (arg2) {
                    case 0:// 显示所有操作
                        list = studentManager.GetAllStudent();
                        bindListView(list);
                        break;
                    case 1:// 删除所有操作
                        deleteIdAll = studentManager.GetAllStudent_id();
                        delete(true, deleteIdAll, "没有任何记录", "确定要删除全部");
                        break;
                    case 2: // 备份数据
                        BackUp();
                        break;
                    case 3:// 还原数据
                        Restore();
                        break;
                    case 4:// 更新
                        break;
                    case 5:// 后退操作
                        list = studentManager.GetAllStudent();
                        bindListView(list);
                        mainMenuDialog.dismiss();
                        break;
                    case 6:// 测试
                        LogUtils.i("测试-删除表");
                        // 新建100个联系人表
                        // ---删除表
                        studentManager.DeleteAll();
                        // ---新建表
                        mProgressDialog = new ProgressDialog(book_Main.this);
                        mProgressDialog.setTitle("生成联系人进度条");
                        mProgressDialog
                                .setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        mProgressDialog.setMax(maxprogress);

                        mProgressDialog.setButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        loop = false;
                                    }
                                });
                        mProgressDialog.setButton2("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        loop = false;
                                    }
                                });
                        mProgressDialog.show();
                        mProgressDialog.setProgress(0);
                        LogUtils.i("测试:新建表开始");
                        new Thread() {
                            String str;

                            public void run() {
                                loop = true;
                                for (int i = 1; (i <= maxprogress)
                                        && (loop); i++) {
                                    str = String.valueOf(i);
                                    studentManager.Add(R.drawable.book_image3, "Name" + str, "", str,
                                            str, str, "Job" + str, "Company" + str);
                                    LogUtils.i("测试:" + str);
                                    // 弹出进度条
                                    mProgressHandler.sendEmptyMessage(ProgressTag);
                                }
                                LogUtils.i("测试:新建表结束");
                            }
                        }.start();
                        // 显示所有(不知为什么,用了进度条就不显示,而且程序出错)
                        // bindListView(studentManager.GetAllStudent());
                        break;
                }
            }
        });
    }

    /**
     * 菜单第一层: 响应点击Menu按钮时的事件(由于菜单键在高版本中已删除,在此也不使用啦)
     */
    /*
	 * @Override public boolean onKeyDown(int keyCode, KeyEvent event) { //
	 * 如果按下的是底部菜单键 if (keyCode == KeyEvent.KEYCODE_MENU) { // 装载底部菜单
	 * loadBottomMenu(); // 如果底部菜单可见,则设为不可见 if (bottomMenuGrid.getVisibility()
	 * == View.VISIBLE) { // 如果搜索框可见,则搜索框设为不可见 if (searchLinearout != null &&
	 * searchLinearout.getVisibility() == View.VISIBLE) {
	 * searchLinearout.setVisibility(View.GONE); }
	 * bottomMenuGrid.setVisibility(View.GONE); } // 如果底部菜单不可见,则设为可见 else {
	 * bottomMenuGrid.setVisibility(View.VISIBLE); } } return
	 * super.onKeyDown(keyCode, event); }
	 */
	/*
	 * 按钮单击执行句柄-->主菜单
	 */
    public void Btn_BookMenu(View v) {
        // 如果底部菜单可见,则设为不可见
        if (bottomMenuGrid.getVisibility() == View.VISIBLE) {
            // 搜索框设为不可见
            searchLinearout.setVisibility(View.GONE);
            // 主菜单不可见
            bottomMenuGrid.setVisibility(View.GONE);
        }
        // 如果底部菜单不可见,则设为可见
        else {
            bottomMenuGrid.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 用户函数: 删除功能的封装
     */
    protected void delete(final boolean Bz_DeleteAll,
                          final ArrayList<Integer> deleteid, String text1, String text2) {
        if (deleteid == null || deleteid.size() == 0) {
            Toast.makeText(book_Main.this, text1, Toast.LENGTH_LONG).show();
        } else {
            new AlertDialog.Builder(book_Main.this)
                    .setTitle(text2 + deleteid.size() + "条记录吗?")
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    if (!Bz_DeleteAll)
                                        DelStudent(deleteid);
                                    else
                                        studentManager.DeleteAll();
                                }

                                private void DelStudent(ArrayList<Integer> index) {
                                    boolean isOk = false;
                                    for (int i = 0; i < deleteid.size(); i++) {
                                        isOk = studentManager.Delete(index
                                                .get(i));
                                    }

                                    if (isOk) {
                                        Toast.makeText(book_Main.this, "删除成功!", Toast.LENGTH_LONG).show();
                                        bindListView(studentManager
                                                .GetAllStudent());
                                        seleteId = null;
                                    } else {
                                        Toast.makeText(book_Main.this, "删除失败!", Toast.LENGTH_LONG).show();
                                    }

                                }
                            }).setNegativeButton("取消", null).create().show();
        }

    }

    /**
     * 用户函数: 备份功能的封装
     */
    protected void BackUp() {
        list = studentManager.GetAllStudent();
        if (list == null || list.size() == 0) {
            Toast.makeText(book_Main.this, "没有任何数据！", Toast.LENGTH_LONG).show();
        } else {
            new AlertDialog.Builder(book_Main.this)
                    .setTitle("确定要备份这" + list.size() + "条记录吗?")
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    studentManager.DeleteAllCopy();
                                    boolean isOk = studentManager.copy();
                                    if (isOk) {
                                        Toast.makeText(book_Main.this,
                                                "备份成功!共备份了"+ list.size()+ "条记录",
                                                Toast.LENGTH_LONG).show();

                                    } else {
                                        Toast.makeText(book_Main.this, "备份失败!", Toast.LENGTH_LONG).show();
                                    }

                                }
                            }).setNegativeButton("取消", null).create().show();
        }
    }

    /**
     * 用户函数: 还原功能的封装
     */
    protected void Restore() {
        new AlertDialog.Builder(book_Main.this).setTitle("确定要还原吗?还原将覆盖现有记录！")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        studentManager.DeleteAll();
                        boolean isOk = studentManager.back();
                        list = studentManager.GetAllStudent();
                        bindListView(list);
                        if (isOk) {
                            Toast.makeText(book_Main.this,
                                    "还原成功!共还原了" + list.size() + "条记录",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(book_Main.this, "还原失败!", Toast.LENGTH_LONG).show();
                        }

                    }
                }).setNegativeButton("取消", null).create().show();
    }

    // 固定模式的代码: 不用深究,将文本和图片整合为SimpleAdapter
    private SimpleAdapter getMenuAdapter(String[] menuNameArray,
                                         int[] imageResourceArray) {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < menuNameArray.length; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("itemImage", imageResourceArray[i]);
            map.put("itemText", menuNameArray[i]);
            data.add(map);
        }
        SimpleAdapter simperAdapter = new SimpleAdapter(this, data,
                R.layout.book_item_menu,
                new String[]{"itemImage", "itemText"}, new int[]{
                R.id.item_image, R.id.item_text});
        return simperAdapter;
    }

    /**
     * 用户函数: 绑定数据到主界面，即刷新界面
     */
    private void bindListView(List<Student> list) {
        final MyAdapter adapter = new MyAdapter(this, list);

        lvStudent.setAdapter(adapter);
        setTitle("共有" + list.size() + "条记录");
        // 设置选择的背景
        lvStudent.setSelector(R.drawable.book_channelgallery_bg);
        // 设置监听(只执行一次)
        if (notSetListener) {
            // 条目单击监听（短点击）
            lvStudent
                    .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> l, View v,
                                                int position, long id) {

                            Student student = (Student) l
                                    .getItemAtPosition(position);
                            int ok = student.getId();

                            Intent intent = new Intent(book_Main.this,
                                    StudentManagerActvity.class);
                            intent.putExtra("id", ok);
                            startActivityForResult(intent, 2);
                        }
                    });
            // 条目的长点击监听
            lvStudent.setOnItemLongClickListener(new OnItemLongClickListener() {

                @Override
                public boolean onItemLongClick(AdapterView<?> arg0, View v,
                                               int position, long arg3) {
                    // 实例化列表
                    if (seleteId == null) {
                        seleteId = new ArrayList<Integer>();
                    }
                    // 获取联系人指针
                    Student student = (Student) arg0
                            .getItemAtPosition(position);
                    // 获取联系人ID
                    Integer ok = student.getId();
                    // 获取容器
                    RelativeLayout r = (RelativeLayout) v;
                    // 获取图标
                    ImageView markedView = (ImageView) r.getChildAt(2);
                    //如果图标隐藏,则设为可见,并选择
                    if (markedView.getVisibility() == View.GONE) {
                        markedView.setVisibility(View.VISIBLE);
                        seleteId.add(ok);
                    }
                    //如果图标可见,则设为隐藏,并删除选择
                    else {
                        markedView.setVisibility(View.GONE);
                        seleteId.remove(ok);
                    }

                    return true;
                }
            });
            notSetListener = false;
        }

    }

    // Activity的回

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        bindListView(studentManager.GetAllStudent());
        seleteId = null;

    }

    /*
     *
     */
    public class MyAdapter extends BaseAdapter {

        private LayoutInflater mInflater;
        private List<Student> list = null;

        public MyAdapter(Context context) {
            list = studentManager.GetAllStudent();
            this.mInflater = LayoutInflater.from(context);
        }

        public MyAdapter(Context context, List<Student> list) {
            this.list = list;
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return list.get(position).getId();
        }

        // 得到显示的列表，有头像，姓名和手机号
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            try {
                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.book_studentlist,
                            null);
                }
                ImageView faceImg = (ImageView) convertView
                        .findViewById(R.id.faceImg);
                TextView lblName = (TextView) convertView
                        .findViewById(R.id.lblName);
                TextView lblMobilePhone = (TextView) convertView
                        .findViewById(R.id.lblMobilePhone);

                Student student = list.get(position);
                lblName.setText("姓名：" + student.getName());
                lblMobilePhone.setText("手机：" + student.getMobilePhone());
                faceImg.setImageResource(student.getPicture());

                return convertView;
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;

            }

        }

    }

}