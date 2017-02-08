package com.airtops.carbon.plane.view;

import com.airtops.carbon.plane.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by wangning on 15/10/27.
 */
public class PlaneView extends View{

    private Context mContext;

    public PlaneView(Context context) {
        super(context);
        mContext=context;
    }

    public PlaneView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @SuppressLint("DrawAllocation")
	@Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        Bitmap mBitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.cloud);
        canvas.drawBitmap(mBitmap,0,0,paint);
    }
}
