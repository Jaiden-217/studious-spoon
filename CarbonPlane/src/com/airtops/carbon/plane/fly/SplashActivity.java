package com.airtops.carbon.plane.fly;

import com.airtops.carbon.plane.base.BaseActivity;
import com.airtops.carbon.plane.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class SplashActivity extends BaseActivity {

    //*************************************************
    // Handler:跳转至不同页面
    //*************************************************
    private final static int SWITCH_MAIN_ACTIVITY = 1000;
    private final static int SWITCH_GUIDE_ACTIVITY = 1001;
    private SharedPreferences sharedPreferences;

    /**
     * Handler:跳转到不同界面
     */
    @SuppressLint("HandlerLeak")
    public Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch(msg.what){
                case SWITCH_MAIN_ACTIVITY:
                    Intent mIntent = new Intent();
                    mIntent.setClass(SplashActivity.this, DeviceScanActivity.class);
                    SplashActivity.this.startActivity(mIntent);
                    SplashActivity.this.finish();
                    break;
                case SWITCH_GUIDE_ACTIVITY:
                    mIntent = new Intent();
                    mIntent.setClass(SplashActivity.this, GuideActivity.class);
                    SplashActivity.this.startActivity(mIntent);
                    SplashActivity.this.finish();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sharedPreferences = getApplication().getSharedPreferences("isFirstRun", Context.MODE_PRIVATE);
        init();
    }

    private void init() {
    	String str = sharedPreferences.getString("isfirst", "yes");
    	if (str.equals("yes")) {
    		Editor editor = sharedPreferences.edit();
    		editor.putString("isfirst", "no").apply();
			mHandler.sendEmptyMessageDelayed(SWITCH_GUIDE_ACTIVITY, 3000);
		} else {
			mHandler.sendEmptyMessageDelayed(SWITCH_MAIN_ACTIVITY, 3000);
//			mHandler.sendEmptyMessageDelayed(SWITCH_GUIDE_ACTIVITY, 3000);
		}
    }


    //****************************************************************
    // 判断应用是否初次加载，读取SharedPreferences中的guide_activity字段
    //****************************************************************
//    private static final String SHAREDPREFERENCES_NAME = "my_pref";
//    private static final String KEY_GUIDE_ACTIVITY = "guide_activity";
//    private boolean isFirstEnter(Context context,String className){
//        if(context==null || className==null||"".equalsIgnoreCase(className))return false;
//        String mResultStr = context.getSharedPreferences(SHAREDPREFERENCES_NAME, Context.MODE_WORLD_READABLE)
//                .getString(KEY_GUIDE_ACTIVITY, "");//取得所有类名 如 com.my.MainActivity
//        if(mResultStr.equalsIgnoreCase("false"))
//            return false;
//        else
//            return true;
//    }
}