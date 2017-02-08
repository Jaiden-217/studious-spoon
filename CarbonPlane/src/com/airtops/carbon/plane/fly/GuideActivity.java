package com.airtops.carbon.plane.fly;
import java.util.ArrayList;
import java.util.List;

import com.airtops.carbon.plane.adapter.ViewPageAdapter;
import com.airtops.carbon.plane.base.BaseActivity;
import com.airtops.carbon.plane.R;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class GuideActivity extends BaseActivity implements OnPageChangeListener,View.OnClickListener {

    private List<View> views;
    private LinearLayout ll;

    // 底部小点图片
    private ImageView[] dots;

    // 记录当前选中位置
    private int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
//        editor = getSharedPreferences().edit();
//        editor.putLong("Total_Time", 0).commit();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 初始化页面
        initViews();

        // 初始化底部小点
        initDots();
    }


    private void initViews() {
        ViewPager vp;
        ViewPageAdapter vpAdapter;
        LayoutInflater inflater = LayoutInflater.from(this);

        views = new ArrayList<View>();
        // 初始化引导图片列表
        views.add(inflater.inflate(R.layout.page_one, null));
        views.add(inflater.inflate(R.layout.page_two, null));
        views.add(inflater.inflate(R.layout.page_three, null));

        // 初始化Adapter
        vpAdapter = new ViewPageAdapter(views, this);

        vp = (ViewPager) findViewById(R.id.view_pager);
        vp.setAdapter(vpAdapter);
        // 绑定回调
        vp.setOnPageChangeListener(this);
    }

    private void initDots() {
        ll = (LinearLayout) findViewById(R.id.ll);

        dots = new ImageView[views.size()];

        // 循环取得小点图片
        for (int i = 0; i < views.size(); i++) {
            dots[i] = (ImageView) ll.getChildAt(i);
            dots[i].setEnabled(true);// 都设为灰色
        }

        currentIndex = 0;
        dots[currentIndex].setEnabled(false);// 设置为白色，即选中状态
    }

    private void setCurrentDot(int position) {
        if (position < 0 || position > views.size() - 1
                || currentIndex == position) {
            return;
        }

        dots[position].setEnabled(false);
        dots[currentIndex].setEnabled(true);

        currentIndex = position;
    }

    // 当滑动状态改变时调用
    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    // 当当前页面被滑动时调用
    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    // 当新的页面被选中时调用
    @Override
    public void onPageSelected(int arg0) {
        // 设置底部小点选中状态
        setCurrentDot(arg0);
        if (arg0 == 2) {
			ll.setVisibility(View.GONE);
		} else {
			ll.setVisibility(View.VISIBLE);
		}
    }

    @Override
    public void onClick(View v) {
        setGuided();
        Intent mIntent = new Intent();
        mIntent.setClass(GuideActivity.this, DeviceScanActivity.class);
//        mIntent.setClass(GuideActivity.this, ControllerActivity.class);
        GuideActivity.this.startActivity(mIntent);
        GuideActivity.this.finish();
    }


    private static final String SHAREDPREFERENCES_NAME = "my_pref";
    private static final String KEY_GUIDE_ACTIVITY = "guide_activity";
    private void setGuided(){
        SharedPreferences settings = getSharedPreferences(SHAREDPREFERENCES_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(KEY_GUIDE_ACTIVITY, "false");
        editor.apply();
    }
}
