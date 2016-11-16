package com.example.administrator.xsltest.book;
/*
import xslPackage.XslTest.MainActivity;
import xslPackage.XslTest.R;
import xslPackage.XslTest.book.biz.StudentManager;
import xslPackage.XslTest.book.model.Student;
import xslPackage.XslTest.book.tool.CommonUtil;
*/
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

import com.example.administrator.xsltest.R;
import com.example.administrator.xsltest.book.biz.StudentManager;
import com.example.administrator.xsltest.book.model.Student;

public class StudentManagerActvity extends Activity implements ViewFactory {

	private ImageButton imageButton = null; // 头像按钮
	private EditText edtName = null; // 姓名
	private EditText edtBirthday = null;
	private EditText edtMobilePhone = null; // 手机
	private EditText edtOfficePhone = null; // 办公室电话
	private EditText edtHomePhone = null; // 家庭电话
	private EditText edtJob = null;// 职位职称
	private EditText edtCompany = null;// 单位名称
	//
	private Button btnSave = null; // 添加按钮
	private Button btnDelete = null; // 删除按钮
	private Button btnStudentList = null; // 返回学生列表
	//
	private View imageChooseView;// 头像选择视图
	private AlertDialog imageChooseDialog;// 头像选择对话框
	private Gallery gallery;// 头像的Gallery
	private ImageSwitcher is;// 头像的ImageSwitcher
	int currentImagePosition;// 用于记录当前选中图像在图像数组中的位置
	int previousImagePosition;// 用于记录上一次图片的位置
	boolean imageChanged;// 判断头像有没有变化

	private boolean isAdd = false;// 是否是来自增加的数据
	private int id = -1;
	private int flag = 0;// 0，表示修改来的保存
	private StudentManager studentManager = null;
	private int flaggg = 0;
	// 头像数组
	private int[] images = new int[] { R.drawable.book_icon,
			R.drawable.book_image1, R.drawable.book_image2,
			R.drawable.book_image3, R.drawable.book_image4,
			R.drawable.book_image5, R.drawable.book_image6,
			R.drawable.book_image7, R.drawable.book_image8,
			R.drawable.book_image9, R.drawable.book_image10,
			R.drawable.book_image11, R.drawable.book_image12,
			R.drawable.book_image13, R.drawable.book_image14,
			R.drawable.book_image15, R.drawable.book_image16,
			R.drawable.book_image17, R.drawable.book_image18,
			R.drawable.book_image19, R.drawable.book_image20,
			R.drawable.book_image21, R.drawable.book_image22,
			R.drawable.book_image23, R.drawable.book_image24,
			R.drawable.book_image25, R.drawable.book_image26,
			R.drawable.book_image27, R.drawable.book_image28,
			R.drawable.book_image29, R.drawable.book_image30 };

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.book_studentmanager);
		studentManager = new StudentManager(this);
		// ------实例化界面相关
		edtName = (EditText) findViewById(R.id.edtName);
		edtBirthday = (EditText) findViewById(R.id.edtBirthday);
		edtMobilePhone = (EditText) findViewById(R.id.edtMobilePhone);
		edtOfficePhone = (EditText) findViewById(R.id.edtOfficePhone);
		edtHomePhone = (EditText) findViewById(R.id.edtHomePhone);
		edtJob = (EditText) findViewById(R.id.edtJob);
		edtCompany = (EditText) findViewById(R.id.edtCompany);

		btnSave = (Button) findViewById(R.id.btnSave);
		btnDelete = (Button) findViewById(R.id.btnDelete);
		btnStudentList = (Button) findViewById(R.id.btnStudentList);

		imageButton = (ImageButton) findViewById(R.id.imageBotton);
		// ImageView faceImg = (ImageView) findViewById(R.id.faceImg);
		// ------
		id = this.getIntent().getIntExtra("id", -1);
		// 说明是修改，因为id从0开始，说明原本有对应的值
		if (id != -1) {
			StudentManager studentManager = new StudentManager(this);
			Student student = studentManager.GetStudentById(id);
			btnDelete.setVisibility(View.VISIBLE);

			imageButton.setImageResource(student.getPicture());
			edtName.setText(student.getName());
			edtBirthday.setText(student.getBirthday());
			edtMobilePhone.setText(student.getMobilePhone());
			edtOfficePhone.setText(student.getOfficePhone());
			edtHomePhone.setText(student.getHomePhone());
			edtJob.setText(student.getJob());
			edtCompany.setText(student.getCompany());

			initEidt(false); // 锁定编辑框

			isAdd = false;
			this.setTitle("修改联系人信息");
		}
		// 通过点击添加过来的
		else {
			isAdd = true;
			flag = 1;
			btnSave.setText("保存");
			this.setTitle("添加联系人信息");
		}

		// 返回联系人列表键的监听
		btnStudentList.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setResult(100); // 代表返回码为100
				finish();
			}
		});

		// 删除键监听
		btnDelete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(StudentManagerActvity.this)
						.setTitle("确定要删除吗？")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										boolean isOk = studentManager
												.Delete(id);
										if (isOk) {
											Toast.makeText(StudentManagerActvity.this,
													"删除成功!", Toast.LENGTH_LONG).show();
										} else {
											Toast.makeText(StudentManagerActvity.this,
													"删除失败!", Toast.LENGTH_LONG).show();
										}
									}
								}).setNegativeButton("取消", null).create()
						.show();
			}
		});

		// 保存按键监听
		btnSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (flag == 0) {
					// 解除编辑框锁定
					initEidt(true);
					btnSave.setText("保存");
					flag++;// 点一下能编辑，点一下不能编辑
				} else {

					String Name = edtName.getText().toString();
					String Birthday = edtBirthday.getText().toString();
					String MobilePhone = edtMobilePhone.getText().toString();
					String OfficePhone = edtOfficePhone.getText().toString();
					String HomePhone = edtHomePhone.getText().toString();
					if (MobilePhone.trim().equals("")
							&& OfficePhone.trim().equals("")
							&& HomePhone.trim().equals("")) {
						Toast.makeText(StudentManagerActvity.this,
								"请至少填写一个联系方式!", Toast.LENGTH_LONG).show();
						return;
					}
					String Job = edtJob.getText().toString();
					String Company = edtCompany.getText().toString();
					if (Name.trim().equals("")) {
						Name = MobilePhone;
						if (MobilePhone.trim().equals("")) {
							Name = OfficePhone;
						}
						if (OfficePhone.trim().equals("")) {
							Name = HomePhone;
						}
					}

					// 修改头像的，
					int Picture;
					if (imageChanged) {
						Picture = images[currentImagePosition % images.length];
					} else {
						Picture = images[previousImagePosition % images.length];
					}

					StudentManager studentManager = new StudentManager(
							StudentManagerActvity.this);
					boolean isok = false;
					if (isAdd) {
						isok = studentManager.Add(Picture, Name, Birthday,
								MobilePhone, OfficePhone, HomePhone, Job,
								Company);
					} else {
						isok = studentManager.Modify(id, Picture, Name,
								Birthday, MobilePhone, OfficePhone, HomePhone,
								Job, Company);
					}
					if (isok) {
						Toast.makeText(StudentManagerActvity.this,
								"保存联系人信息成功!", Toast.LENGTH_LONG).show();
						initUI();
					} else {
						Toast.makeText(StudentManagerActvity.this,
								"保存联系人信息失败!", Toast.LENGTH_LONG).show();
					}
				}
			}

		});

		// 点击头像监听
		imageButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				if (flaggg == 0) {

					initImageChooseDialog();// s 初始化imageChooseDialog
					flaggg = 1;

				}
				imageChooseDialog.show();
			}
		});
	}

	// 清空编辑框
	private void initUI() {
		edtName.setText("");
		edtBirthday.setText("");
		edtMobilePhone.setText("");
		edtOfficePhone.setText("");
		edtHomePhone.setText("");
		edtJob.setText("");
		edtCompany.setText("");
		imageButton.setImageResource(images[0]);
	}

	// 判断编辑框是否允许编辑
	private void initEidt(boolean isok) {

		imageButton.setEnabled(isok);
		edtName.setEnabled(isok);
		edtBirthday.setEnabled(isok);
		edtMobilePhone.setEnabled(isok);
		edtOfficePhone.setEnabled(isok);
		edtHomePhone.setEnabled(isok);
		edtJob.setEnabled(isok);
		edtCompany.setEnabled(isok);

	}

	public void initImageChooseDialog() {

		if (imageChooseView == null) { // 为gallery加图片，做出ImageChooseView的视图
			LayoutInflater li = LayoutInflater.from(StudentManagerActvity.this);
			imageChooseView = li.inflate(R.layout.book_imagechoose, null);

			gallery = (Gallery) imageChooseView.findViewById(R.id.imggallery);// 得到视图中gallery的位置
			gallery.setAdapter(new ImageAdapter(this));// 为Gallery装载图片
			gallery.setSelection(images.length / 2);// 初始显示中间位置的头像

			is = (ImageSwitcher) imageChooseView.findViewById(R.id.imgswitch);
			is.setFactory(this);
			is.setInAnimation(AnimationUtils.loadAnimation(this,// 加载动画效果（效果可以自己写）
					android.R.anim.fade_in));
			is.setOutAnimation(AnimationUtils.loadAnimation(this,// 卸载图片的动画效果
					android.R.anim.fade_out));

			// 为头像gallery设置监听，记录选中的头像
			gallery.setOnItemSelectedListener(new OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {

					currentImagePosition = arg2;// 当前的头像位置为选中的位置
					is.setImageResource(images[arg2 % images.length]);// 为ImageSwitcher设置图像

				}

				public void onNothingSelected(AdapterView<?> arg0) {

				}
			});
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this); // 点击头像，弹出一个头像选择的对话框
		builder.setTitle("请选择图像");
		builder.setView(imageChooseView);// 加载供选择的头像选择视图
		builder.setPositiveButton("确定",// 点击确定，则把当前的图片赋值给头像按钮显示
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						imageChanged = true;
						previousImagePosition = currentImagePosition;
						imageButton
								.setImageResource(images[currentImagePosition
										% images.length]);
					}
				});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				currentImagePosition = previousImagePosition;

			}
		});
		imageChooseDialog = builder.create();

	}

	// Gallery的适配器,内部类
	class ImageAdapter extends BaseAdapter {

		private Context context;

		public ImageAdapter(Context context) {
			this.context = context;
		}

		public int getCount() {
			return Integer.MAX_VALUE;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		// 得到选中的图片，放到view视图中
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imgv = new ImageView(context);
			imgv.setImageResource(images[position % images.length]);
			imgv.setAdjustViewBounds(true);
			imgv.setLayoutParams(new Gallery.LayoutParams(80, 80));
			imgv.setPadding(15, 10, 15, 10);
			return imgv;
		}
	}

	@Override
	public View makeView() {
		ImageView view = new ImageView(this);
		view.setBackgroundColor(0xff000000);
		view.setScaleType(ScaleType.FIT_CENTER);
		view.setLayoutParams(new ImageSwitcher.LayoutParams(90, 90));
		return view;
	}

	// 单击句柄(出生日期文本框)
	public void ClickHandler(View v) {
		switch (v.getId()) {
		case R.id.edtBirthday:
			edtBirthday = (EditText) findViewById(R.id.edtBirthday);
			// 设置键解析
			DatePickerDialog datePickerDialog = new DatePickerDialog(this,
					new DatePickerDialog.OnDateSetListener() {
						public void onDateSet(DatePicker view, int year,
								int monthOfYear, int dayOfMonth) {
							edtBirthday.setText(String.format("%04d-%02d-%02d",
									year, monthOfYear + 1, dayOfMonth));
						}
					}, 2009, 11, 1);
			// 取消键解析
			datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
					"Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							edtBirthday.setText("1982-08-24");
						}
					});
			datePickerDialog.show();
			break;
		}
	}
}
