package com.airtops.carbon.plane.fly;

import java.util.Random;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airtops.carbon.plane.R;
import com.airtops.carbon.plane.base.BaseActivity;
import com.airtops.carbon.plane.base.CARBONAPP;
import com.airtops.carbon.plane.bluetooth.BluetoothLeClass;
import com.airtops.carbon.plane.service.CarbonPlaneService;
import com.airtops.carbon.plane.util.DataValue;
import com.airtops.carbon.plane.view.AcceView;
import com.airtops.carbon.plane.view.SpringProgressView;

/**
 * Created by wangning; Revised by dingjie.
 * */

public class ControllerActivity extends BaseActivity implements
		View.OnClickListener {
	private boolean is_video_taking = false;
	private boolean isClockRun = false;
	private boolean isTimeCounted = false;
	private boolean flag = true;
	private Vibrator vibrator;
	private int acce = 1000;
	private int speed = 1000;
	private int rotateRate = 1100;
	private int power, swerving, time, rssi, l_power, l_rssi, begin, height;
	private SensorManager mSensorManager;
	private Intent mIntent = new Intent();
	protected static BluetoothLeClass mBLE;
	private ImageView mButtonVideo, btnPower, left_propeller,
			right_propeller, mSurfaceView, power_indicator, rssi_indicator,
			video_dot, imgPlane, imgPlaneUp, imgPlaneDown, fly_height_icon_left, fly_height_icon_right;
	private TextView acceTv;
	private GestureDetector gDetector;
	private SpringProgressView timePro;
	private Chronometer timeCounter, videoTimer;
	private AcceView acceView;
	private RelativeLayout planeLayout, videoLayout;
	private RotateAnimation animationL, animationR;
	private AnimationDrawable mAnim;
	private static final int UPDATE_POWER = 1003;
	private static final int UPDATE_RSSI = 1006;
	private static final int VIDEO_DOT_VISIBLE = 1010;
	private static final int VIDEO_DOT_GONE = 1011;
	private static final int UPDATE_HEIGHT = 1012;
	private static final int ALL_FINISH = 1013;

	private static final String items[] = { "方向微调",  "飞行时间", "参数调整", "版本更新"};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_controller);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	@Override
	protected void onStart() {
		super.onStart();
		initUI();
		getHeight();
		initSensor();
		startSendCommand();
	}

    @Override
    public boolean onTouchEvent(MotionEvent event) {
            return gDetector.onTouchEvent(event);
    }

	/**
	 * 初始化控件
	 * */
	public void initUI() {
		fly_height_icon_left = (ImageView) findViewById(R.id.f_h_i);
		fly_height_icon_right = (ImageView) findViewById(R.id.f_h_i_r);
		imgPlane = (ImageView) findViewById(R.id.imgPlane);
		imgPlaneUp = (ImageView) findViewById(R.id.imgPlaneUp);
		imgPlaneDown = (ImageView) findViewById(R.id.imgPlaneDown);
		videoTimer = (Chronometer) findViewById(R.id.video_timer);
		video_dot = (ImageView) findViewById(R.id.video_dot);
		videoLayout = (RelativeLayout) findViewById(R.id.videoLayout);
		power_indicator = (ImageView) findViewById(R.id.power_indicator);
		rssi_indicator = (ImageView) findViewById(R.id.rssi_indicator);
		planeLayout = (RelativeLayout) findViewById(R.id.planeLayout);
		acceTv = (TextView) findViewById(R.id.acceTv);
		mBLE = new BluetoothLeClass(this);
		btnPower = (ImageView) findViewById(R.id.btn_power);
		btnPower.setOnClickListener(this);
		// right data
		findViewById(R.id.btn_photo).setOnClickListener(this);
		mButtonVideo = (ImageView) findViewById(R.id.btn_video);
		mButtonVideo.setOnClickListener(this);
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		timePro = (SpringProgressView) findViewById(R.id.timePro);
		timePro.setMaxCount(10800);
		timeCounter = (Chronometer) findViewById(R.id.timeCounter);
		acceView = (AcceView) findViewById(R.id.accePro);
		left_propeller = (ImageView) findViewById(R.id.imgLeftPropeller);
		right_propeller = (ImageView) findViewById(R.id.imgRightPropeller);
		mSurfaceView = (ImageView) findViewById(R.id.surfaceView);
		mIntent.setAction(DataValue.DATA_ACTION);
		register_receiver();
        gDetector = new GestureDetector(this, new Detection());
        gDetector.setOnDoubleTapListener(dTapListener);
	}

	/**
	 * 电量指针的动画
	 * */
	protected void startAnimation(double d) {
		AnimationSet animationSet = new AnimationSet(true);
		/**
		 * 前两个参数定义旋转的起始和结束的度数，后两个参数定义圆心的位置
		 */
		int end = getDuShu(d);

		RotateAnimation rotateAnimation = new RotateAnimation(begin, end,
				Animation.RELATIVE_TO_SELF, 0.93f, Animation.RELATIVE_TO_SELF,
				0.5f);
		rotateAnimation.setDuration(1000);
		rotateAnimation.setFillAfter(true);
		animationSet.addAnimation(rotateAnimation);
		animationSet.setFillAfter(true);
		power_indicator.startAnimation(animationSet);
		begin = end;
	}

	/**
	 * 蓝牙指针的动画
	 * */
	protected void startRssiAnimation(double d) {
		AnimationSet animationSet = new AnimationSet(true);
		/**
		 * 前两个参数定义旋转的起始和结束的度数，后两个参数定义圆心的位置
		 */
		int end = getDuShu(d);

		RotateAnimation rotateAnimation = new RotateAnimation(begin, end,
				Animation.RELATIVE_TO_SELF, 0.93f, Animation.RELATIVE_TO_SELF,
				0.5f);
		rotateAnimation.setDuration(1000);
		rotateAnimation.setFillAfter(true);
		animationSet.addAnimation(rotateAnimation);
		animationSet.setFillAfter(true);
		rssi_indicator.startAnimation(animationSet);
		begin = end;
	}

	/**
	 * 把蓝牙信号值和电量值转换成仪表盘上的度数
	 * */
	public int getDuShu(double number) {
		double a;
		a = number * 1.8;
		return (int) a;
	}

	/**
	 * handler
	 * */
    @SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int value = msg.what;
			switch (value) {
			case UPDATE_POWER:
				startAnimation(Double.parseDouble(power + ""));
				break;
			case UPDATE_RSSI:
				startRssiAnimation(Double.parseDouble(rssi + ""));
				break;
			case VIDEO_DOT_VISIBLE:
				dot_video_visible();
				break;
			case VIDEO_DOT_GONE:
				dot_video_gone();
				break;
			case UPDATE_HEIGHT:
				updateHeight();
				break;
			case ALL_FINISH:
				sendBroadcast(new Intent(DataValue.BROAD_FINISH));
				break;
			default:
				break;
			}
		}
	};


	/**
	 * 设置显示高度的圆点的位置
	 * */
	private void updateHeight() {
		Random x = new Random(1);
		float mX = x.nextFloat();
		left_delta_Y = 0 - (int) (mX * height);
		right_delta_Y = left_delta_Y;
		getMovePositionL(fly_height_icon_left);
		getMovePositionR(fly_height_icon_right);
		mHandler.sendEmptyMessageDelayed(UPDATE_HEIGHT, 1000);
	}

	/**
	 * 设置摄像状态指示灯关闭
	 * */
	private void dot_video_gone() {
		video_dot.setVisibility(View.GONE);
		mHandler.sendEmptyMessageDelayed(VIDEO_DOT_VISIBLE, 1000);
	}

	/**
	 * 设置摄像状态指示灯打开
	 * */
	private void dot_video_visible() {
		video_dot.setVisibility(View.VISIBLE);
		mHandler.sendEmptyMessageDelayed(VIDEO_DOT_GONE, 1000);
	}

	@Override
	protected void onResume() {
		super.onResume();
		register_receiver();
	}

	/**
	 * 注册广播
	 * */
	private void register_receiver() {
		// 接受电池广播
		IntentFilter batFilter = new IntentFilter();
		batFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(batteryReceiver, batFilter);
		// 接受rssi广播
		IntentFilter rssiFilter = new IntentFilter();
		rssiFilter.addAction(DataValue.RSSI_ACTION);
		registerReceiver(rssiReceiver, rssiFilter);
		// 接受蓝牙状态广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(DataValue.LINK_ACTION);
		filter.addAction(DataValue.BROAD_CONNECTION_BROKEN);
		registerReceiver(linkReceiver, filter);
	}

    /**
	 * 抬头动作
	 * */
	private void planeUp() {
		imgPlane.setVisibility(View.GONE);
		imgPlaneDown.setVisibility(View.GONE);
		imgPlaneUp.setVisibility(View.VISIBLE);
	}

	/**
	 * 低头动作
	 * */
	private void planeDown() {
		imgPlane.setVisibility(View.GONE);
		imgPlaneUp.setVisibility(View.GONE);
		imgPlaneDown.setVisibility(View.VISIBLE);
	}

	/**
	 * 水平动作
	 * */
	private void planeStrait() {
		imgPlaneUp.setVisibility(View.GONE);
		imgPlaneDown.setVisibility(View.GONE);
		imgPlane.setVisibility(View.VISIBLE);
	}

	/**
	 * 手势监听
	 * */
	class Detection implements OnGestureListener {
		@Override
		public boolean onDown(MotionEvent e) {
			swerving = 0;
			return false;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			planeStrait();
			if (!flag) {
				if (acce >= 1900) {
					rotateRate = 100;
				}
				if (acce >= 1800 && acce < 1900) {
					rotateRate = 250;
				}
				if (acce >= 1700 && acce < 1800) {
					rotateRate = 400;
				}
				if (acce >= 1600 && acce < 1700) {
					rotateRate = 500;
				}
				if (acce >= 1500 && acce < 1600) {
					rotateRate = 600;
				}
				if (acce >= 1400 && acce < 1500) {
					rotateRate = 700;
				}
				if (acce >= 1300 && acce < 1400) {
					rotateRate = 800;
				}
				if (acce >= 1200 && acce < 1300) {
					rotateRate = 900;
				}
				if (acce >= 1100 && acce < 1200) {
					rotateRate = 1000;
				}
				if (acce < 1100) {
					rotateRate = 1100;
				}
				startAnim();
				changeSpeed();
			}

			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			set_up();
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			if (flag && swerving == 0) {
				showToast(getString(R.string.press_power));
				swerving++;
			} else if (!flag) {
				int acceRate = (int) distanceY;
				if (acceRate > DataValue.ACCE_RATE_DEFAULT) {
					planeUp();
				}
				if (acceRate < DataValue.ACCE_RATE_DEFAULT) {
					planeDown();
				}
				acce += acceRate;
				if (acce >= DataValue.ACCE_MAX) {
					acce = DataValue.ACCE_MAX;
				} else if (acce < DataValue.ACCE_MIN) {
					acce = DataValue.ACCE_MIN;
				}
				speed = DataValue.ACCE_DENOMINATOR / acce;

				CARBONAPP.CARBON.setLspeed((byte) (acce & 0x000000FF));
				CARBONAPP.CARBON.setHspeed((byte) (acce / 256));

				acceView.setCurrentCount(acce - DataValue.ACCE_COUNT_DEFAULT);
				acceTv.setText(getString(R.string.current_speed,(acce-DataValue.ACCE_COUNT_DEFAULT)/10));
			}
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}

	}
	private void set_up() {
		// dialog参数设置
		AlertDialog.Builder builder = new AlertDialog.Builder(this); // 先得到构造器
		builder.setTitle("设置"); // 设置标题
		// builder.setMessage("是否确认退出?"); //设置内容
		builder.setIcon(R.drawable.dronetab_setting);// 设置图标，图片id即可
		// 设置列表显示，注意设置了列表显示就不要设置builder.setMessage()了，否则列表不起作用。
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				switch (which) {
				    case 0:
					    toTrim();
					    break;
				    case 1:
					    showTotalTime();
					    break;
				    case 2:
					    if (flag) {
						    toParamSettings();
					    } else {
						    showToast("请先关闭电源");
					    }
					    break;
				    case 3:
					    showToast("当前已是最新版本");
					    break;
				default:
					break;
				}
			}
		});
		builder.create().show();
	}

	/**
	 * 获取高度显示条的高度值
	 * */
	private void getHeight() {
		final ImageView fhBg = (ImageView) findViewById(R.id.f_h_l);
		ViewTreeObserver vtoHeight = fhBg.getViewTreeObserver();
		vtoHeight.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {
				fhBg.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				height = fhBg.getHeight();
			}
		});
	}

	private int left_start_left, left_start_right, left_start_top, left_start_bottom, left_delta_Y;
	private int right_start_left, right_start_right, right_start_top, right_start_bottom, right_delta_Y;

	/**
	 * 获取左侧高度指示点的初始坐标
	 * */
	private void getStartPositionL(View v) {
		left_start_left = v.getLeft();
		left_start_right = v.getRight();
		left_start_top = v.getTop();
		left_start_bottom = v.getBottom();
	}

	/**
	 * 移动左侧高度指示点的位置
	 * */
	private void getMovePositionL(View v) {
		v.layout(left_start_left, left_start_top + left_delta_Y, left_start_right, left_start_bottom + left_delta_Y);
	}

	/**
	 * 将左侧高度指示点还原到初始位置
	 * */
	private void setToStartPositionL(View v) {
		v.layout(left_start_left, left_start_top, left_start_right, left_start_bottom);
	}

	/**
	 * 获取右侧高度指示点的初始坐标
	 * */
	private void getStartPositionR(View v) {
		right_start_left = v.getLeft();
		right_start_right = v.getRight();
		right_start_top = v.getTop();
		right_start_bottom = v.getBottom();
	}

	/**
	 * 移动右侧高度指示点的位置
	 * */
	private void getMovePositionR(View v) {
		v.layout(right_start_left, right_start_top + right_delta_Y, right_start_right, right_start_bottom + right_delta_Y);
	}

	/**
	 * 将右侧高度指示点还原到初始位置
	 * */
	private void setToStartPositionR(View v) {
		v.layout(right_start_left, right_start_top, right_start_right, right_start_bottom);
	}

	/**
	 * initSensor
	 */
	public void initSensor() {
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		Sensor mSensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		if (null == mSensorManager) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this)
					.setTitle(R.string.tip).setMessage(
							R.string.unsupport_device);
			builder.setNegativeButton(R.string.yes,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface,
								int i) {
							dialogInterface.dismiss();
							finish();
						}
					}).create().show();
		}
		mSensorManager.registerListener(new SensorEvent(), mSensor,
				SensorManager.SENSOR_DELAY_FASTEST);
	}

	/**
	 * 计时条颜色填充
	 * */
    @SuppressLint("HandlerLeak")
	private Handler timeHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1101) {
				countTime();
			}
		}
	};

	private void countTime() {
		time++;
		timePro.setCurrentCount(time);
		timeHandler.sendEmptyMessageDelayed(1101, 10);
	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {
		case R.id.btn_video:
			take_video();
			break;
		case R.id.btn_photo:
			take_photo();
			break;
		case R.id.btn_power:
			power_clicked();
			break;
		default:
			break;
		}
	}

	private void take_photo() {
        if (!flag) {
            if (!is_video_taking) {
                Intent intent = new Intent(ControllerActivity.this, CarbonPlaneService.class);
                intent.putExtra("cmd", CarbonPlaneService.TAKE_PHOTO);
                startService(intent);
            } else {
                showToast(getString(R.string.stop_video_mode));
            }
        } else {
			showToast(getString(R.string.press_power));
		}
	}

	private void take_video() {
		if (!flag) {
			if (!is_video_taking) {
				setVideoTakingCondition(true);
				Intent intent = new Intent(ControllerActivity.this, CarbonPlaneService.class);
				intent.putExtra("cmd", CarbonPlaneService.START_TAKING_VIDEO);
				startService(intent);
				mButtonVideo.setSelected(true);
				videoLayout.setVisibility(View.VISIBLE);
				video_dot.setVisibility(View.VISIBLE);
				videoTimer.setBase(SystemClock.elapsedRealtime());
				videoTimer.start();
				mHandler.sendEmptyMessageDelayed(VIDEO_DOT_GONE, 1000);
			} else {
				setVideoTakingCondition(false);
				Intent intent = new Intent(ControllerActivity.this, CarbonPlaneService.class);
				intent.putExtra("cmd", CarbonPlaneService.STOP_TAKING_VIDEO);
				startService(intent);
				mButtonVideo.setSelected(false);
				videoTimer.setBase(SystemClock.elapsedRealtime());
				videoTimer.stop();
				mHandler.removeMessages(VIDEO_DOT_GONE);
				mHandler.removeMessages(VIDEO_DOT_VISIBLE);
				videoLayout.setVisibility(View.GONE);
			}

		} else {
			showToast(getString(R.string.press_power));
		}
	}

	/**
	 * 低电量警告
	 * */
	private void show_low_power() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(
				R.string.tip).setMessage(R.string.lowPower);
		builder.setNegativeButton(R.string.yes,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						dialogInterface.dismiss();
					}
				}).create().show();
	}

	/**
	 * 螺旋桨动画
	 * */
	private void startAnim() {
		animationR = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,
				0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		/** 常用方法 */
		animationR.setInterpolator(new LinearInterpolator());// 不停顿
		animationR.setDuration(rotateRate);
		animationR.setRepeatCount(-1);
		right_propeller.setAnimation(animationR);
		animationR.start();

		animationL = new RotateAnimation(360f, 0f, Animation.RELATIVE_TO_SELF,
				0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		/** 常用方法 */
		animationL.setInterpolator(new LinearInterpolator());// 不停顿
		animationL.setDuration(rotateRate);
		animationL.setRepeatCount(-1);
		left_propeller.setAnimation(animationL);
		animationL.start();
	}

    /**
     * 是否正在摄像
     * */
    private void setVideoTakingCondition(boolean s) {
        is_video_taking = s;
    }

	/**
	 * 解锁
	 * */
	private void startSendCommand() {
		Intent intent = new Intent(ControllerActivity.this,
				CarbonPlaneService.class);
		intent.putExtra("cmd", CarbonPlaneService.START_COMMAND_THREAD);
		startService(intent);
	}

	/**
	 * 加锁
	 * */
	private void stopSendCommand() {
		Intent intent = new Intent(ControllerActivity.this,
				CarbonPlaneService.class);
		intent.putExtra("cmd", CarbonPlaneService.STOP_COMMAND_THREAD);
		startService(intent);
	}

	/**
	 * 电源开关
	 * */
	private void power_on_off() {
		vibrator.vibrate(100);
		if (flag) {
			flag = false;
			getStartPositionL(fly_height_icon_left);
			getStartPositionR(fly_height_icon_right);
			startAnim();
			btnPower.setSelected(true);
			CARBONAPP.CARBON.setLock((byte) (0x01));
			runTimeCounter();
			changeSpeed();
			updateHeight();
			START_TIME = System.currentTimeMillis();
		} else {
			mAnim.stop();
			timeCounter.stop();
			animationL.cancel();
			animationR.cancel();
			if (isTimeCounted) {
				timeHandler.removeMessages(1101);
				isTimeCounted = false;
			}
			timeHandler.removeMessages(1101);
			CARBONAPP.CARBON.setLock((byte) (0x00));
			flag = true;
			btnPower.setSelected(false);
			mHandler.removeMessages(UPDATE_HEIGHT);
			setToStartPositionL(fly_height_icon_left);
			setToStartPositionR(fly_height_icon_right);
			setTotalTime();
		}
	}

	/**
	 * 运行计时器
	 * */
	private void runTimeCounter() {
		if (!isClockRun) {
			timeCounter.setBase(SystemClock.elapsedRealtime());
			isClockRun = true;
		}
		if (!isTimeCounted) {
			timeHandler.sendEmptyMessage(1101);
			isTimeCounted = true;
		}
		timeCounter.setBase(convertStrTimeToLong(timeCounter.getText()
				.toString()));
		timeCounter.start();
	}

	/**
	 * 背景动画
	 * */
	private void changeSpeed() {
		mAnim = new AnimationDrawable();
		for (int i = 1; i <= 7; i++) {
			int id = getResources().getIdentifier("step" + i, "drawable",
					getPackageName());
			Drawable drawable = getResources().getDrawable(id);
			if (null != drawable) {
				mAnim.addFrame(drawable, speed);
			}
		}
		mAnim.setOneShot(false);
		mSurfaceView.setBackground(mAnim);
		mAnim.start();
	}

	/**
	 * 将String类型的时间转换成long,如：12:01:08
	 * 
	 * @param strTime
	 *            String类型的时间
	 * @return long类型的时间 *
	 **/
	protected long convertStrTimeToLong(String strTime) {
		String[] timeArray = strTime.split(":");
		long longTime = 0;
		if (timeArray.length == 2) {// 如果时间是MM:SS格式
			longTime = Integer.parseInt(timeArray[0]) * 1000 * 60
					+ Integer.parseInt(timeArray[1]) * 1000;
		} else if (timeArray.length == 3) {// 如果时间是HH:MM:SS格式
			longTime = Integer.parseInt(timeArray[0]) * 1000 * 60 * 60
					+ Integer.parseInt(timeArray[1]) * 1000 * 60
					+ Integer.parseInt(timeArray[0]) * 1000;
		}
		return SystemClock.elapsedRealtime() - longTime;
	}

	/**
	 * 电源按钮的点击事件
	 * */
	private void power_clicked() {
		if (is_video_taking) {
			showToast("请先关闭摄像模式！");
			return;
		}
		if (power < 0) {
			show_low_power();
		} else {
			power_on_off();
		}
	}

	/**
	 * Pause in the life circle of application
	 * */
	@Override
	protected void onPause() {
		if (!flag) {
			acce = 0;
		}
		super.onPause();
	}

	/**
	 * 接收电量信息的广播
	 * */
	private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
				power = intent.getIntExtra("level", 0);
				if (l_power != power) {
					l_power = power;
					mHandler.sendEmptyMessageDelayed(UPDATE_POWER, 100);
				}
			}
		}
	};
	/**
	 * connection state broadcast receiver 检测蓝牙连接状态
	 */
	private BroadcastReceiver linkReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(DataValue.BROAD_CONNECTION_BROKEN) || intent.getAction().equals(DataValue.LINK_ACTION)) {
				if (!ControllerActivity.this.isDestroyed()) {
					int link = intent.getIntExtra("link", 1);
					if (link == 0) {
						show_break_dlg();
					}
				}
			}
		}
	};

	private void show_break_dlg() {
		vibrator.vibrate(500);
		AlertDialog.Builder builder = new AlertDialog.Builder(
				ControllerActivity.this).setTitle(R.string.tip)
				.setMessage(R.string.bluetooth_disconnect);
		builder.setNegativeButton(R.string.yes,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(
							DialogInterface dialogInterface,
							int i) {
						dialogInterface.dismiss();
						ControllerActivity.this.finish();
					}
				}).create().show();
	}

	/**
	 * Bluetooth signal strength broadcast receiver
	 */
	private BroadcastReceiver rssiReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(DataValue.RSSI_ACTION)) {
				rssi = intent.getIntExtra("rssi", 0);
				rssi = (int) ((rssi + 100) * 1.5);
				if (rssi <= 0) {
					rssi = 0;
				}
				if (rssi >= 100) {
					rssi = 100;
				}
				if (l_rssi != rssi) {
					l_rssi = rssi;
					mHandler.sendEmptyMessageDelayed(UPDATE_RSSI, 100);
				}
			}
		}
	};

	/**
	 * 生命周期stop
	 * */
	@Override
	protected void onStop() {
		mSensorManager.unregisterListener(new SensorEvent());
		stopSendCommand();
		stopService(new Intent(ControllerActivity.this,
				CarbonPlaneService.class));
		Log.e(DataValue.TAG, "Paper is onStop()");
		super.onStop();
	}

	/**
	 * 重写返回按钮点击事件
	 * */
	@Override
	public void onBackPressed() {
		if (!flag) {
			showToast("请关闭电源后再退出本页面！");
		} else {
			long currentTime = System.currentTimeMillis();
			if ((currentTime - touchTime) >= waitTime) {
				showToast("再按一次退出");
				touchTime = currentTime;
			} else {
				finish();
			}
		}
	}

	/**
	 * 生命周期onDestroy
	 * */
	@Override
	protected void onDestroy() {
		try {
			DeviceScanActivity.mBLE.disconnect();
		} catch (Exception e) {
			Log.e(DataValue.TAG, "controller destroy failed; "+e.toString());
		}
		Log.e(DataValue.TAG, "paper is onDestroy");
		unregisterReceiver(batteryReceiver);
		unregisterReceiver(rssiReceiver);
		unregisterReceiver(linkReceiver);
		super.onDestroy();
	}

	/**
	 * 双击监听
	 * */
	OnDoubleTapListener dTapListener = new OnDoubleTapListener() {

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			power_clicked();
			return false;
		}
	};
	
	/**
	 * 方向微调
	 * */
	private int trim = 0;
	private void toTrim() {
		final Dialog dialog = new Dialog(this, R.style.HttpDialogStyle);
		dialog.setContentView(R.layout.trim_xml);
		TextView titleTv = (TextView) (dialog.findViewById(R.id.titleTv));
		titleTv.setText(items[0]);
		final TextView messageTv = (TextView) (dialog.findViewById(R.id.msgTv));
		messageTv.setText(getString(R.string.current_trim,(CARBONAPP.CARBON.getTrimming()-1500)));
		Button minus = (Button) (dialog.findViewById(R.id.trimMinus));
		minus.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				trim -= 10;
				messageTv.setText(getString(R.string.current_trim,trim));
			}
		});
		
		Button plus = (Button) (dialog.findViewById(R.id.trimPlus));
		plus.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				trim += 10;
				messageTv.setText(getString(R.string.current_trim,trim));
			}
		});
		
		Button cancel = (Button) (dialog.findViewById(R.id.cancelBtn));
		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		Button confirm = (Button) (dialog.findViewById(R.id.okBtn));
		confirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CARBONAPP.CARBON.setTrimming(1500 + trim);
				dialog.dismiss();
			}
		});
		dialog.show();
	}

    /**
     * 参数调整
     * */
	private void toParamSettings() {
		final Dialog dialog = new Dialog(this, R.style.HttpDialogStyle);
		dialog.setContentView(R.layout.param_setting);
		TextView titleTv = (TextView) (dialog.findViewById(R.id.titleTv));
		titleTv.setText(items[2]);

		final EditText raw_1 = (EditText) (dialog.findViewById(R.id.raw_1));
		final EditText raw_2 = (EditText) (dialog.findViewById(R.id.raw_2));
		final EditText raw_3 = (EditText) (dialog.findViewById(R.id.raw_3));
		final EditText pitch_1 = (EditText) (dialog.findViewById(R.id.pitch_1));
		final EditText pitch_2 = (EditText) (dialog.findViewById(R.id.pitch_2));
		final EditText pitch_3 = (EditText) (dialog.findViewById(R.id.pitch_3));
		final EditText yaw_1 = (EditText) (dialog.findViewById(R.id.yaw_1));
		final EditText yaw_2 = (EditText) (dialog.findViewById(R.id.yaw_2));
		final EditText yaw_3 = (EditText) (dialog.findViewById(R.id.yaw_3));

		raw_1.setText(R.string.default_params);raw_2.setText(R.string.default_params);raw_3.setText(R.string.default_params);
		pitch_1.setText(R.string.default_params);pitch_2.setText(R.string.default_params);pitch_3.setText(R.string.default_params);
		yaw_1.setText(R.string.default_params);yaw_2.setText(R.string.default_params);yaw_3.setText(R.string.default_params);

		Button cancel = (Button) (dialog.findViewById(R.id.cancelBtn));
		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		Button confirm = (Button) (dialog.findViewById(R.id.okBtn));
		confirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					int raw1 = Integer.parseInt(raw_1.getText().toString());
					int raw2 = Integer.parseInt(raw_2.getText().toString());
					int raw3 = Integer.parseInt(raw_3.getText().toString());
					int pitch1 = Integer.parseInt(pitch_1.getText().toString());
					int pitch2 = Integer.parseInt(pitch_2.getText().toString());
					int pitch3 = Integer.parseInt(pitch_3.getText().toString());
					int yaw1 = Integer.parseInt(yaw_1.getText().toString());
					int yaw2 = Integer.parseInt(yaw_2.getText().toString());
					int yaw3 = Integer.parseInt(yaw_3.getText().toString());

					CARBONAPP.CARBON.setRaw_first_high((byte) (raw1 & 0x000000FF));
					CARBONAPP.CARBON.setRaw_first_low((byte) (raw1 / 256));
					CARBONAPP.CARBON.setRaw_second_high((byte) (raw2 & 0x000000FF));
					CARBONAPP.CARBON.setRaw_second_low((byte) (raw2 / 256));
					CARBONAPP.CARBON.setRaw_third_high((byte) (raw3 & 0x000000FF));
					CARBONAPP.CARBON.setRaw_third_low((byte) (raw3 / 256));
					CARBONAPP.CARBON.setPitch_first_high((byte) (pitch1 & 0x000000FF));
					CARBONAPP.CARBON.setPitch_first_low((byte) (pitch1 / 256));
					CARBONAPP.CARBON.setPitch_second_high((byte) (pitch2 & 0x000000FF));
					CARBONAPP.CARBON.setPitch_second_low((byte) (pitch2 / 256));
					CARBONAPP.CARBON.setPitch_third_high((byte) (pitch3 & 0x000000FF));
					CARBONAPP.CARBON.setPitch_third_low((byte) (pitch3 / 256));
					CARBONAPP.CARBON.setYaw_first_high((byte) (yaw1 & 0x000000FF));
					CARBONAPP.CARBON.setYaw_first_low((byte) (yaw1 / 256));
					CARBONAPP.CARBON.setYaw_second_high((byte) (yaw2 & 0x000000FF));
					CARBONAPP.CARBON.setYaw_second_low((byte) (yaw2 / 256));
					CARBONAPP.CARBON.setYaw_third_high((byte) (yaw3 & 0x000000FF));
					CARBONAPP.CARBON.setYaw_third_low((byte) (yaw3 / 256));

					Intent intent = new Intent(ControllerActivity.this, CarbonPlaneService.class);
					intent.putExtra("cmd", CarbonPlaneService.CMD_SEND_PARAMS);
					startService(intent);
				} catch (Throwable e1) {
					showToast("数据格式有误，提交失败");
				}

				dialog.dismiss();
			}
		});
		dialog.show();
	}

	/**
	 * 显示飞行总时长
	 * */
	private void showTotalTime() {
		final Dialog dialog = new Dialog(this, R.style.HttpDialogStyle);
		dialog.setContentView(R.layout.alert);
		TextView titleTv = (TextView) (dialog.findViewById(R.id.titleTv));
		titleTv.setText("飞行时长记录");
		TextView messageTv = (TextView) (dialog.findViewById(R.id.msgTv));
		messageTv.setText("您当前的飞行总时间为：");
		TextView timeTV = (TextView) (dialog.findViewById(R.id.showTotal));
		timeTV.setText(totalTime());
		Button confirm = (Button) (dialog.findViewById(R.id.okBtn));
		confirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	/**
	 * 获取飞行总时长
	 * */
	private String totalTime() {
		long time = getSharedPreferences().getLong("Total_Time", 0) / 1000;
		int hour = (int) (time / 3600);
		int min = (int) (time - hour * 3600) / 60;
		int sec = (int) (time % 60);
		return hour + "小时" + min + "分" + sec + "秒";
	}

	/**
	 * 更新飞行总时长
	 * */
	private void setTotalTime() {
		editor = getSharedPreferences().edit();
		END_TIME = System.currentTimeMillis();
		TIME_LAST = END_TIME - START_TIME;
		CURRENT_TOTAL_TIME = getSharedPreferences().getLong("Total_Time", 0);
		CURRENT_TOTAL_TIME += TIME_LAST;
		editor.putLong("Total_Time", CURRENT_TOTAL_TIME).apply();
	}

    /**
     * 设置转弯动画
     * */
    private void set_direction_anim(int data) {
        RotateAnimation rotateAnimation = new RotateAnimation(
                -data * 3, -data * 3, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setFillAfter(false);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setDuration(5);
        planeLayout.startAnimation(rotateAnimation);
    }

    /**
	 * Gravity Sensor Event Listener
	 * */
	class SensorEvent implements SensorEventListener {
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) { }

		@Override
		public void onSensorChanged(android.hardware.SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && !flag) {
                    @SuppressWarnings("deprecation")
                    int data = (int) (event.values[SensorManager.DATA_X]);

                    if (data > DataValue.SENSOR_DATA_LEVEL_MIN && data < DataValue.SENSOR_DATA_LEVEL_MAX) {
							data = DataValue.SENSOR_DATA_DEFAULT;
                    }
					int direction = CARBONAPP.CARBON.getTrimming() - (DataValue.DIRECTION_FACTORING * data);
					CARBONAPP.CARBON.setLdirection((byte) (direction & 0x000000FF));
					CARBONAPP.CARBON.setHdirection((byte) (direction / 256));
                    set_direction_anim(data);
                }
		    }
	    }
}
