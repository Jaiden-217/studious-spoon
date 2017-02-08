package com.airtops.carbon.plane.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by dingjie on 2016-5-16
 * */
public class AcceView extends View{
	/** 分段颜色 */
	private static final int[] SECTION_COLORS = { Color.GREEN, Color.YELLOW,
			Color.RED };
	/** 进度条最大值 */
	private float maxCount = 1000;
	/** 进度条当前值 */
	private float currentCount;
	/** 画笔 */
	private Paint mPaint;
	private int mWidth, mHeight;

	public AcceView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(context);
	}

	public AcceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public AcceView(Context context) {
		super(context);
		initView(context);
	}

	private void initView(Context context) { }

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		int round = 0;
		mPaint.setColor(Color.rgb(71, 76, 80));
		RectF rectBg = new RectF(0, 0, mWidth, mHeight);
		canvas.drawRoundRect(rectBg, round, round, mPaint);
		mPaint.setColor(Color.BLACK);
		RectF rectBlackBg = new RectF(2, 2, mWidth - 2, mHeight - 2);
		canvas.drawRoundRect(rectBlackBg, round, round, mPaint);

		float section = currentCount / maxCount;
		RectF rectProgressBg = new RectF(3, 3, mWidth - 3, (mHeight - 3)
				* section);
		if (section <= 1.0f / 3.0f) {
			if (section != 0.0f) {
				mPaint.setColor(SECTION_COLORS[0]);
			} else {
				mPaint.setColor(Color.TRANSPARENT);
			}
		} else {
			int count = (section <= 1.0f / 3.0f * 2) ? 2 : 3;
			int[] colors = new int[count];
			System.arraycopy(SECTION_COLORS, 0, colors, 0, count);
			float[] positions = new float[count];
			if (count == 2) {
				positions[1] = 0.0f;
				positions[0] = 1.0f - positions[0];
			} else {
				positions[2] = 0.0f;
				positions[1] = (maxCount / 3) / currentCount;
				positions[0] = 1.0f - positions[0] * 2;
			}
			positions[positions.length - 1] = 1.0f;
			LinearGradient shader = new LinearGradient(3, 3, mWidth - 3,
					(mHeight - 3) * section, colors, null,
					Shader.TileMode.MIRROR);
			mPaint.setShader(shader);
		}
		canvas.drawRoundRect(rectProgressBg, round, round, mPaint);
	}

	private int dipToPx(int dip) {
		float scale = getContext().getResources().getDisplayMetrics().density;
		return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
	}

	/***
	 * 设置最大的进度值
	 */
	public void setMaxCount(float maxCount) {
		this.maxCount = maxCount;
	}

	/***
	 * 设置当前的进度值
	 */
	public void setCurrentCount(float currentCount) {
		this.currentCount = currentCount > maxCount ? maxCount : currentCount;
		invalidate();
	}

	public float getMaxCount() {
		return maxCount;
	}

	public float getCurrentCount() {
		return currentCount;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthSpecMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSpecSize = MeasureSpec.getSize(heightMeasureSpec);
		int heightSpecMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightSpecSize = MeasureSpec.getSize(widthMeasureSpec);
		if (widthSpecMode == MeasureSpec.EXACTLY
				|| widthSpecMode == MeasureSpec.AT_MOST) {
			mHeight = widthSpecSize;
		} else {
			mHeight = 0;
		}
		if (heightSpecMode == MeasureSpec.AT_MOST
				|| heightSpecMode == MeasureSpec.UNSPECIFIED) {
			mWidth = dipToPx(15);
		} else {
			mWidth = heightSpecSize;
		}
		setMeasuredDimension(mWidth, mHeight);
	}
}