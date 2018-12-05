package com.pic.optimize.helper;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.pic.optimize.R;
import com.pic.optimize.helper.view.PagerSlidingTabStrip;

import java.util.ArrayList;

/**
 * Created by tangh on 14-5-29.
 */
public abstract class ViewPagerActivity extends FragmentActivity implements View.OnClickListener {
    public ViewPager mViewpager;
    String[] mTitles;
    MyAdapter adapter;
    protected FragmentManager mFragmentManager;
    protected PagerSlidingTabStrip indicator;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tp_paper_helper);
        mTitles = getTabText();
        mViewpager = (ViewPager) findViewById(R.id.pager);
        mFragmentManager = getSupportFragmentManager();
        adapter = new MyAdapter(mFragmentManager);
        mViewpager.setAdapter(adapter);

        indicator = (PagerSlidingTabStrip)findViewById(R.id.indicator);
        findViewById(R.id.ib_back).setOnClickListener(this);
        setIndicatorArr();
        indicator.setViewPager(mViewpager);
    }

    private void setIndicatorArr() {
        indicator.setDividerColorResource(android.R.color.transparent);
        indicator.setIndicatorColorResource(R.color.mt_h_text_blue);
        indicator.setIndicatorHeight(4);
        indicator.setUnderlineColorResource(android.R.color.transparent);
        indicator.setShouldExpand(true);
        indicator.setDividerPadding(24);
        indicator.setTextColorResource(R.color.mt_h_text_black);
        indicator.setIndicatorSelectTextColorResource(R.color.mt_h_text_blue);
        indicator.setTextSize(14);
        indicator.setTabPaddingLeftRight(12);
    }

    public void setCurrentItem(int index) {
        indicator.setCurrentItem(index);
    }

    public abstract ArrayList<Fragment> getFragmentList();

    public abstract String[] getTabText();


    class MyAdapter extends FragmentPagerAdapter {
        ArrayList<Fragment> fragment = null;

        public MyAdapter(FragmentManager fm) {
            super(fm);
            fragment = getFragmentList();
        }

        @Override
        public Fragment getItem(int arg0) {
            return fragment.get(arg0);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public int getCount() {
            return fragment.size();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ib_back) {
            finish();
        }
    }
}
