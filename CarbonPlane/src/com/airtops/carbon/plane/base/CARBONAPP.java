package com.airtops.carbon.plane.base;

import com.airtops.carbon.plane.util.CarbonCommand;

import android.app.Application;

/**
 * @ClassName CARBONAPP
 * @Description 自定义Application父类，定义全局变量。
 * @author DingJie
 * @date 2016-5-26
 * */
public class CARBONAPP extends Application{
	/**
	 * 飞机姿态指令类
	 * */
	public static CarbonCommand CARBON = null;
	@Override
	public void onCreate() {
		super.onCreate();
		initAppEnv();
	}
	
	private void initAppEnv() {
		CARBON = CarbonCommand.getInstance();
	}
}
