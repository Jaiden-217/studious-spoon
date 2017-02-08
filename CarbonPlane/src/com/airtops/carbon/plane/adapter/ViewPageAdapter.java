package com.airtops.carbon.plane.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;
import com.airtops.carbon.plane.R;
public class ViewPageAdapter extends PagerAdapter {

    private Context mContext;
    private List<View> mViews ;

    public ViewPageAdapter(List<View> views,Context context){
        this.mContext = context;
        this.mViews = views;
    }

    @Override
    public int getCount() {
        return mViews.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view ==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mViews.get(position));
        if(position==2){
            ImageView btn = (ImageView) container.findViewById(R.id.btn_enter);
            btn.setOnClickListener((View.OnClickListener) mContext);
        }
        return mViews.get(position);

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViews.get(position));
    }

}