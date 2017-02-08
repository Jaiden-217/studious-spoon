/**
 * @ClassName CarbonPlaneService
 * @Description 处理数据发送的服务
 * @author DingJie
 * @date 2016-05-26
 * */
package com.airtops.carbon.plane.service;

import com.airtops.carbon.plane.base.CARBONAPP;
import com.airtops.carbon.plane.util.DataValue;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.Random;

public class CarbonPlaneService extends Service{
	private String TAG = DataValue.TAG;
	private Random random = new Random();
	private Intent mIntent = new Intent();
	private SendCommandThread sendCommandThread = null;
	private HeartBeatThread heartBeatThread = null;
	private HandModeCommandSend handModeCommandSend = null;
	public static final int COMMAND_RATE = 100;
	public static final int START_COMMAND_THREAD = 101;
	public static final int STOP_COMMAND_THREAD = 102;
	public static final int TAKE_PHOTO = 103;
	public static final int START_TAKING_VIDEO = 104;
	public static final int STOP_TAKING_VIDEO = 105;
	public static final int CMD_SEND_PARAMS = 106;
	public static final int START_HAND_MODE = 108;
	public static final int STOP_HAND_MODE = 109;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		mIntent = new Intent(DataValue.DATA_ACTION);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		stopCommandThread();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			int cmd = intent.getIntExtra("cmd", 0);
			switch (cmd) {
			case START_COMMAND_THREAD:
				send_unlock();
				break;
			case STOP_COMMAND_THREAD:
				send_lock();
				break;
			case TAKE_PHOTO:
				take_photo();
				break;
			case START_TAKING_VIDEO:
				start_taking_video();
				break;
			case STOP_TAKING_VIDEO:
				stop_taking_video();
				break;
			case CMD_SEND_PARAMS:
				send_params();
				break;
			case START_HAND_MODE:
				start_hand_mode();
				break;
			case STOP_HAND_MODE:
				stop_hand_mode();
				break;
			default:
				break;
			}
		}
		return START_NOT_STICKY;
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case START_COMMAND_THREAD:
				startCommandThread();
//				start_heart_beat_thread();
				break;
			case STOP_COMMAND_THREAD:
				stopCommandThread();
//				stop_heart_beat_thread();
				break;
			case TAKE_PHOTO:
				mIntent.putExtra("data", CARBONAPP.CARBON.carbon_take_photo());
				sendBroadcast(mIntent);
				mHandler.sendEmptyMessageDelayed(106, COMMAND_RATE);
				break;
			case 106:
				mIntent.putExtra("data", CARBONAPP.CARBON.carbon_take_photo());
				sendBroadcast(mIntent);
				mHandler.sendEmptyMessageDelayed(START_COMMAND_THREAD, COMMAND_RATE);
				break;
			case START_TAKING_VIDEO:
				mIntent.putExtra("data", CARBONAPP.CARBON.carbon_take_video());
				sendBroadcast(mIntent);
				mHandler.sendEmptyMessageDelayed(107, COMMAND_RATE);
				break;
			case 107:
				mIntent.putExtra("data", CARBONAPP.CARBON.carbon_take_video());
				sendBroadcast(mIntent);
				mHandler.sendEmptyMessageDelayed(START_COMMAND_THREAD, COMMAND_RATE);
				break;
			case STOP_TAKING_VIDEO:
				mIntent.putExtra("data", CARBONAPP.CARBON.carbon_stop_video());
				sendBroadcast(mIntent);
				mHandler.sendEmptyMessageDelayed(108, COMMAND_RATE);
				break;
			case 108:
				mIntent.putExtra("data", CARBONAPP.CARBON.carbon_stop_video());
				sendBroadcast(mIntent);
				mHandler.sendEmptyMessageDelayed(START_COMMAND_THREAD, COMMAND_RATE);
				break;
			default:
				break;
			}
		}
	};

	/**
	 * 发送修改后的参数
	 * */
	private void send_params() {
		stopCommandThread();
		mIntent.putExtra("data", CARBONAPP.CARBON.carbon_param_raw());
		sendBroadcast(mIntent);
		mIntent.putExtra("data", CARBONAPP.CARBON.carbon_param_pitch());
		sendBroadcast(mIntent);
		mIntent.putExtra("data", CARBONAPP.CARBON.carbon_param_yaw());
		sendBroadcast(mIntent);
		startCommandThread();
	}
	
	/**
	 * 发送拍照指令
	 * */
	private void take_photo() {
		stopCommandThread();
		mIntent.putExtra("data", CARBONAPP.CARBON.carbon_take_photo());
		sendBroadcast(mIntent);
		mHandler.sendEmptyMessageDelayed(TAKE_PHOTO, COMMAND_RATE);
		Log.e(TAG, "photo taken");
	}
	
	/**
	 * 发送开始摄像指令
	 * */
	private void start_taking_video() {
		stopCommandThread();
		mIntent.putExtra("data", CARBONAPP.CARBON.carbon_take_video());
		sendBroadcast(mIntent);
		mHandler.sendEmptyMessageDelayed(START_TAKING_VIDEO, COMMAND_RATE);
		Log.e(TAG, "video taking started");
	}
	
	/**
	 * 发送停止摄像指令
	 * */
	private void stop_taking_video() {
		stopCommandThread();
		mIntent.putExtra("data", CARBONAPP.CARBON.carbon_stop_video());
		sendBroadcast(mIntent);
		mHandler.sendEmptyMessageDelayed(STOP_TAKING_VIDEO, COMMAND_RATE);
		Log.e(TAG, "video taking stopped");
	}
	
	/**
	 * 发送指令以开启飞行姿态线程
	 * */
	private void send_unlock() {
		mHandler.sendEmptyMessage(START_COMMAND_THREAD);
		Log.e(TAG, "unlock sent");
	}
	
	/**
	 * 发送指令以关闭飞行姿态线程
	 * */
	private void send_lock() {
		mHandler.sendEmptyMessageDelayed(STOP_COMMAND_THREAD, COMMAND_RATE);
		Log.e(TAG, "lock sent");
	}
	
	/**
	 * 开启发送飞行姿态指令线程
	 * */
	private void startCommandThread() {
		stop_hand_mode();
		if (sendCommandThread == null) {
			sendCommandThread = new SendCommandThread();
		}
		if (!sendCommandThread.isAlive()) {
			sendCommandThread.start();
		}
		Log.e(TAG, "command thread started");
	}
	
	/**
	 * 停止发送飞行姿态指令线程
	 * */
	private void stopCommandThread() {
		if (sendCommandThread != null) {
			sendCommandThread.isRunning = false;
			sendCommandThread = null;
		}
		Log.e(TAG, "command thread stopped");
	}
	
	/**
	 * 发送飞行姿态指令线程
	 * */
	class SendCommandThread extends Thread {
		public volatile boolean isRunning = true;
		@Override
		public void run() {
			super.run();
			while (isRunning) {
				try {
					sleep(COMMAND_RATE);
					int mRandom = random.nextInt(255);
					CARBONAPP.CARBON.setRandoms((byte) (mRandom & 0x000000FF));
					mIntent.putExtra("data", CARBONAPP.CARBON.carbon_plane_normal_command());
					sendBroadcast(mIntent);
				} catch (InterruptedException e1) {
					Log.e(TAG, e1.toString());
				}
			}
		}
	}

	/**
	 * 开启hand mode
	 * */
	private void start_hand_mode() {
		stopCommandThread();
		if (handModeCommandSend == null) {
			handModeCommandSend = new HandModeCommandSend();
		}
		if (!handModeCommandSend.isAlive()) {
			handModeCommandSend.start();
		}
		Log.e(TAG, "hand mode started");
	}

	/**
	 * 关闭hand mode
	 * */
	private void stop_hand_mode() {
		if (handModeCommandSend != null) {
			handModeCommandSend.isRunning = false;
			handModeCommandSend = null;
		}
		Log.e(TAG, "hand mode stopped");
	}

	/**
	 * hand mode 时指令发送线程
	 * */
	class HandModeCommandSend extends Thread {
		public volatile boolean isRunning = true;

		@Override
		public void run() {
			super.run();
			while (isRunning) {
				try {
					sleep(COMMAND_RATE);
					int mRandom = random.nextInt(255);
					CARBONAPP.CARBON.setRandoms((byte) (mRandom & 0x000000FF));
					mIntent.putExtra("data", CARBONAPP.CARBON.carbon_hand_mode());
					sendBroadcast(mIntent);
				} catch (InterruptedException e1) {
					Log.e(TAG, e1.toString());
				}
			}
		}
	}

	/**
	 * 开启心跳包发送线程
	 * */
	private void start_heart_beat_thread() {
		if (heartBeatThread == null) {
			heartBeatThread = new HeartBeatThread();
		}
		if (!heartBeatThread.isAlive()) {
			heartBeatThread.start();
		}
		Log.e(TAG, "heart beat thread started");
	}

	/**
	 * 关闭心跳包发送线程
	 * */
	private void stop_heart_beat_thread() {
		if (heartBeatThread != null) {
			heartBeatThread.isRunning = false;
			heartBeatThread = null;
		}
		Log.e(TAG, "heart beat thread stopped");
	}

	/**
	 * 打开连接断开提示框
	 * */
	private void show_break_dlg() {
		Intent intent = new Intent();
		intent.setAction(DataValue.BROAD_CONNECTION_BROKEN);
		intent.putExtra("link", 0);
		sendBroadcast(intent);
		stop_heart_beat_thread();
		Log.e(TAG, "bluetooth connection broken");
	}

	/**
	 * 心跳包发送线程
	 * */
	class HeartBeatThread extends Thread {
		public volatile boolean isRunning = true;
		@Override
		public void run() {
			super.run();
			while (isRunning) {
				try {
					sleep(1000);
					CARBONAPP.CARBON.setHeartBeat(CARBONAPP.CARBON.getHeartBeat()+1);
					if (CARBONAPP.CARBON.getHeartBeat() == 2) {
						CARBONAPP.CARBON.setHeartBeat(0);
						show_break_dlg();
					}
				} catch (InterruptedException e1) {
					Log.e(TAG, e1.toString());
				}
			}
		}
	}

}
