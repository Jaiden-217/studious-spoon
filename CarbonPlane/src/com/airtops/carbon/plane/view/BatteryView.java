package com.airtops.carbon.plane.view;

import com.airtops.carbon.plane.util.DataValue;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by wangning on 15/10/27.
 */
@SuppressLint("DrawAllocation")
public class BatteryView extends View {

	private int mPower = 0;

	public BatteryView(Context context) {
		super(context);
	}

	public BatteryView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int mColor = Color.BLACK;
		int battery_left = 10;
		int battery_top = 10;
		int battery_width = 200;
		int battery_height = 45;

		int battery_inside_margin = 3;

		// 先画外框
		Paint paint = new Paint();
		paint.setColor(mColor);
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);

		float power_percent = mPower / 100.0f;

		Paint paint2 = new Paint(paint);
		paint2.setStyle(Paint.Style.FILL);
		if (mPower < 20) {
			Shader mShader = new LinearGradient(0, 0, 0, 400, new int[] {
					Color.rgb(255, 0, 0), Color.rgb(85, 212, 235) }, null,
					Shader.TileMode.MIRROR);
			paint2.setShader(mShader);
		}
		if (mPower >= 20 && mPower < 40) {
			Shader mShader = new LinearGradient(0, 0, 0, 400, new int[] {
					Color.rgb(209, 102, 20), Color.rgb(85, 212, 235) }, null,
					Shader.TileMode.MIRROR);
			paint2.setShader(mShader);
		}
		if (mPower >= 40 && mPower < 60) {
			Shader mShader = new LinearGradient(0, 0, 0, 400, new int[] {
					Color.rgb(255, 255, 0), Color.rgb(85, 212, 235) }, null,
					Shader.TileMode.MIRROR);
			paint2.setShader(mShader);
		}
		if (mPower >= 60 && mPower < 80) {
			Shader mShader = new LinearGradient(0, 0, 0, 400, new int[] {
					Color.rgb(0, 255, 255), Color.rgb(85, 212, 235) }, null,
					Shader.TileMode.MIRROR);
			paint2.setShader(mShader);
		}
		if (mPower >= 80) {
			Shader mShader = new LinearGradient(0, 0, 0, 400, new int[] {
					Color.rgb(0, 255, 0), Color.rgb(85, 212, 235) }, null,
					Shader.TileMode.MIRROR);
			paint2.setShader(mShader);
		}
		// 画电量
		if (power_percent != 0) {
			int p_left = battery_left + battery_inside_margin;
			int p_top = -178;
			int p_right = battery_left + battery_inside_margin +
					(int)((battery_width - 2 * battery_inside_margin) * power_percent);
			int p_bottom = battery_top + battery_height + battery_inside_margin;
			Rect rect2 = new Rect(p_left, p_top, p_right, p_bottom);
			canvas.drawRect(rect2, paint2);
		}

		Paint paint3 = new Paint(paint);
		paint3.setStyle(Paint.Style.FILL);
		paint3.setColor(Color.rgb(236, 236, 236));
		if (power_percent != 0) {
			int p_left = battery_left + battery_inside_margin + 
					(int) ((battery_width - 2 * battery_inside_margin) * power_percent);
			int p_top = battery_top + battery_inside_margin;
			int p_right = p_left + battery_width - 2 * battery_inside_margin;
			int p_bottom = -178 ;
			Rect rect2 = new Rect(p_left, p_top, p_right, p_bottom);
			canvas.drawRect(rect2, paint3);
		}
	}

	public void setPower(int power) {
		mPower = power;
		Log.e(DataValue.TAG, "power" + mPower);
		invalidate();
	}
}