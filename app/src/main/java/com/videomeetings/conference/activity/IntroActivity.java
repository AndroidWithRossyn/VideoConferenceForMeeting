package com.videomeetings.conference.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.videomeetings.conference.R;
import com.videomeetings.conference.base.BaseActivity;
import com.videomeetings.conference.utils.Global;

import butterknife.BindView;
import butterknife.OnClick;

public class IntroActivity extends BaseActivity {

    @BindView(R.id.mViewPager)
    ViewPager mViewPager;
    @BindView(R.id.mIvNext)
    TextView mIvNext;
    @BindView(R.id.mIVDots)
    ImageView mIVDots;
    @BindView(R.id.mTxtIntro1)
    TextView mTxtIntro1;
    @BindView(R.id.mTxtIntro2)
    TextView mTxtIntro2;
    private int[] layouts;
    private MyViewPagerAdapter myViewPagerAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_intro;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }

        layouts = new int[]{
                R.layout.layout_into1,
                R.layout.layout_into2};

        myViewPagerAdapter = new MyViewPagerAdapter();
        mViewPager.setAdapter(myViewPagerAdapter);
        mViewPager.addOnPageChangeListener(viewPagerPageChangeListener);
    }

    @OnClick({R.id.mIvNext})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.mIvNext:
                int current = getItem(+1);
                if (current < layouts.length) {
                    // move to next screen
                    mViewPager.setCurrentItem(current);
                } else {
                    launchHomeScreen();
                }
                break;
        }
    }

    private int getItem(int i) {
        return mViewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        prefManager.setBoolean(Global.ISPRIVACYFIRST,false);
        startActivity(new Intent(IntroActivity.this, AnimSelectionActivity.class));
        finish();
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {

            if (position == layouts.length - 1) {
                mTxtIntro1.setText(getResources().getText(R.string.title2));
                mTxtIntro2.setText(getResources().getText(R.string.titleintro2));
                mIVDots.setImageDrawable(ContextCompat.getDrawable(IntroActivity.this,R.drawable.ic_dot1));
                mIvNext.setText(getResources().getText(R.string.letstart));
            } else {
                mTxtIntro1.setText(getResources().getText(R.string.title1));
                mTxtIntro2.setText(getResources().getText(R.string.titleintro1));
                mIVDots.setImageDrawable(ContextCompat.getDrawable(IntroActivity.this,R.drawable.ic_dot2));
                mIvNext.setText(getResources().getText(R.string.next));
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}