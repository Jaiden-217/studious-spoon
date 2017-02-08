package com.airtops.carbon.plane.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by wangning on 15/10/29.
 */
@SuppressLint("DrawAllocation")
public class DottedLineView extends View{

    private Paint mPaint;

    private Context mContext;

    public DottedLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        mPaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int height = getHeight()/5;
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.DKGRAY);
        mPaint.setStrokeWidth(4);
        Path path = new Path();
        path.moveTo(0, 2);
        path.lineTo(getWidth(),2);
        PathEffect effects = new DashPathEffect(new float[]{10,5,10,5},1);
        mPaint.setPathEffect(effects);
        canvas.drawPath(path, mPaint);

        path.moveTo(0, height+2);
        path.lineTo(getWidth(),height+2);
        canvas.drawPath(path, mPaint);

        path.moveTo(0, 2*height+2);
        path.lineTo(getWidth(),2*height+2);
        canvas.drawPath(path, mPaint);

        path.moveTo(0, 3*height+2);
        path.lineTo(getWidth(),3*height+2);
        canvas.drawPath(path, mPaint);

        path.moveTo(0, 4*height+2);
        path.lineTo(getWidth(),4*height+2);
        canvas.drawPath(path, mPaint);

    }
}
