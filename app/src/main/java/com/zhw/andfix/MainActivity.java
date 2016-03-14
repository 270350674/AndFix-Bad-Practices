package com.zhw.andfix;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import com.umeng.onlineconfig.OnlineConfigAgent;
import com.zhw.andfix.model.PatchBean;
import com.zhw.andfix.util.GsonUtils;
import com.zhw.andfix.util.RepairBugUtil;
import com.zhw.andfix.util.WeakHandler;


public class MainActivity extends Activity {

    public static final int MSG_WHAT_DOWNLOAD = 0x111;
    public static final String UMENG_ONLINE_PARAM =  "pathInfo";    //友盟后台定义的参数名称


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerMessageReceiver();  // used for receive msg
        initUmeng();    //初始化友盟在线参数
        /*boolean haveDownload = mLocalPreferencesHelper.getBooleanDefaultFalse(
                getString(R.string.spp_app_have_download));
        if (haveDownload){
            RepairBugUtil.getInstance().fixBug(this);
        }*/

        getUmengParamAndFix();

    }

    private void getUmengParamAndFix() {
        //获取友盟在线参数对应key的values
        String pathInfo = OnlineConfigAgent.getInstance().getConfigParams(this, UMENG_ONLINE_PARAM);
        if (!TextUtils.isEmpty(pathInfo)){
            PatchBean onLineBean = GsonUtils.getInstance().parseIfNull(PatchBean.class , pathInfo);
            try {
                //进行判断当前版本是否有补丁需要下载更新
                RepairBugUtil.getInstance().comparePath(this, onLineBean);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 友盟在线的初始化工作
     */
    private void initUmeng() {
        OnlineConfigAgent.getInstance().updateOnlineConfig(this);
        OnlineConfigAgent.getInstance().setDebugMode(true); //设置在线参数的调试模式
    }


    public void onClick(View view) {
        TestActivity.launch(this);
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(mMessageReceiver);
        RepairBugUtil.getInstance().release();
        super.onDestroy();
    }



    private WeakHandler mHandler = new WeakHandler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == MSG_WHAT_DOWNLOAD){
                String message = (String) msg.obj;
                if (TextUtils.isEmpty(message)) return false;
                try {
                    PatchBean bean = GsonUtils.getInstance().parse(PatchBean.class, message);
                    RepairBugUtil.getInstance().comparePath(MainActivity.this, bean);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
    });



    //for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.zhw.andfix.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_MESSAGE = "message";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String message = intent.getStringExtra(KEY_MESSAGE);
                Message msg = new Message();
                msg.what = MSG_WHAT_DOWNLOAD;
                msg.obj = message;
                mHandler.sendMessage(msg);
            }
        }
    }


}
