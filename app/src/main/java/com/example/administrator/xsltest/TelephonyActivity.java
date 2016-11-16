package com.example.administrator.xsltest;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.administrator.xsltest.module.AppContext;
import com.example.administrator.xsltest.module.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by XSL on 2016/11/14.
 */

public class TelephonyActivity extends AppCompatActivity {
    ListView showView;
    String[] statusNames;
    ArrayList<String> statusValues = new ArrayList<String>();

    // 记录需要群发的短信号码
    ArrayList<String> sendList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        setContentView(R.layout.phone_sms);
        TelephonyManager tManager = (TelephonyManager)
                getSystemService(Context.TELEPHONY_SERVICE);
        final SmsManager smsManager = SmsManager.getDefault();
        //------------------------------------------------------------------------------------------电话测试
        Button btn_phone1 = (Button) findViewById(R.id.list_view_btn3);
        btn_phone1.setOnClickListener(new View.OnClickListener() {
            Intent intent = new Intent();
            @Override
            public void onClick(View v) {
                // 直接拨号
                intent.setAction(Intent.ACTION_CALL);
                // 用户启动拨号
                //intent.setAction(Intent.ACTION_DIAL);
                // 需要发短信的号码 电话号码之间用";"隔开
                intent.setData(Uri.parse("tel:"+"10086"));
                startActivity(intent);
            }
        });
        //------------------------------------------------------------------------------------------短信测试
        // 接口方式
        Button btn_sendSms1 = (Button) findViewById(R.id.list_view_btn1);
        btn_sendSms1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建一个PendingIntent
                PendingIntent pi = PendingIntent.getActivity(AppContext.getContext(), 0, new Intent(), 0);
                // 发送短信
                smsManager.sendTextMessage("13234020721", null, "SmsTest", pi, null);
                // 提示短信发送完成
                Toast.makeText(AppContext.getContext(), "短信单发完成", Toast.LENGTH_LONG).show();
            }
        });
        // 系统方式
        Button btn_sendSms2 = (Button) findViewById(R.id.list_view_btn2);
        btn_sendSms2.setOnClickListener(new View.OnClickListener() {
            Intent intent = new Intent();
            @Override
            public void onClick(View v) {
                // 系统默认的active,用来打开默认的短信界面
                intent.setAction(Intent.ACTION_SENDTO);
                // 需要发短信的号码 电话号码之间用";"隔开
                intent.setData(Uri.parse("smsto:" + ";" +"13234020721"));
                intent.putExtra("sms_body","别紧张，这仅仅是一个测试！OY");
                startActivity(intent);
            }
        });
        /*
        // 群发
        Button btn_sendMassSms = (Button) findViewById(R.id.list_view_btn2);
        btn_sendMassSms.setEnabled(false);
        btn_sendMassSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (String number:sendList) {
                    // 创建一个PendingIntent
                    PendingIntent pi = PendingIntent.getActivity(AppContext.getContext(), 0, new Intent(), 0);
                    // 发送短信
                    smsManager.sendTextMessage(number, null, "MassSmsTest", pi, null);
                }
                // 提示短信发送完成
                Toast.makeText(AppContext.getContext(), "短信群发完成", Toast.LENGTH_LONG).show();
            }
        });
        // 号码选择
        Button btn_smsSelectPhone = (Button) findViewById(R.id.list_view_btn3);
        btn_smsSelectPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 查询联系人的电话号码
                final Cursor cursor = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null
                );
                BaseAdapter adapter = new BaseAdapter() {
                    @Override
                    public int getCount() {
                        return cursor.getCount();
                    }

                    @Override
                    public Object getItem(int position) {
                        return position;
                    }

                    @Override
                    public long getItemId(int position) {
                        return position;
                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        cursor.moveToPosition(position);
                        CheckBox rb = new CheckBox(AppContext.getContext());
                        // 获取联系人的电话号码，并去掉中间的中划线，空格
                        String number = cursor
                                .getString(cursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.Phone.NUMBER))
                                .replace("-","")
                                .replace(" ","");
                        rb.getText(number);
                        if (isChecked(number)){

                        }
                    }
                }
            }
        });
        */
        //------------------------------------------------------------------------------------------注册通话状态接收器
        PhoneStateListener listener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String number) {
                super.onCallStateChanged(state, number);
                //
                switch (state) {
                    // 无任何状态
                    case TelephonyManager.CALL_STATE_IDLE:
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        break;
                    // 来电响铃时
                    case TelephonyManager.CALL_STATE_RINGING:
                        //处理滑动清除和点击删除事件
                        //Toast.makeText(AppContext.getContext(), "来电", Toast.LENGTH_SHORT).show();
                        LogUtils.i("来电" + number);
                        break;
                    default:
                        break;
                }
            }
        };
        tManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        //------------------------------------------------------------------------------------------显示信息
        // 获取各种状态名称数组
        statusNames = getResources().getStringArray(R.array.telephone_status_names);
        //--------------------ArrayList<>赋值
        // 获取设备编号
        statusValues.add(tManager.getDeviceId());
        // 获取系统平台版本
        statusValues.add(tManager.getDeviceSoftwareVersion() != null ? tManager.getDeviceSoftwareVersion() : "未知");
        // 获取网络运营商代号
        statusValues.add(tManager.getNetworkOperator());
        // 获取网络运营商名称
        statusValues.add(tManager.getNetworkOperatorName());
        // 获取手机网络类型
        String[] phoneType = getResources().getStringArray(R.array.phone_type);
        statusValues.add(phoneType[tManager.getPhoneType()]);
        // 获取设备所在位置
        statusValues.add(tManager.getCellLocation() != null ? tManager.getCellLocation().toString() : "未知未知");
        // 获取SIM卡的国别
        statusValues.add(tManager.getSimCountryIso());
        // 获取SIM卡序列号
        statusValues.add(tManager.getSimSerialNumber());
        // 获取SIM卡状态
        String[] simState = getResources().getStringArray(R.array.sim_state);
        statusValues.add(simState[tManager.getSimState()]);
        //--------------------ArrayList<> -->  ArrayList<Map<String,String>>
        // 获得ListView对象
        ArrayList<Map<String, String>> status = new ArrayList<>();
        // 遍历statusValues集合，将statusNames与statusValues的数据封装到status集合中
        for (int i = 0; i < statusValues.size(); i++) {
            HashMap<String, String> map = new HashMap<>();
            map.put("name", statusNames[i]);
            map.put("value", statusValues.get(i));
            status.add(map);
        }
        //--------------------SimpleAdapter
        // 使用SimpleAdapter封装List数据
        SimpleAdapter adapter = new SimpleAdapter(
                this,
                status,
                R.layout.phone_sms_listview_unit,
                new String[]{"name", "value"},
                new int[]{R.id.list_view_line1_textview1, R.id.list_view_line1_textview2});
        // 为listView设置Adapter
        showView = (ListView) findViewById(R.id.list_view_lv1);
        showView.setAdapter(adapter);
    }
    /*
    // 判断某个电话号码是否已经在群发范围内
    private boolean isChecked(String phone){
        for(String s1:sendList){
            if(s1.equals(phone)){
                return true;
            }
        }
        return false;
    }
    */
}
