package com.pic.optimize.helper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.pic.optimize.helper.fragment.WebViewFragment;

import java.util.ArrayList;

public class PaperHelpActivity extends ViewPagerActivity {

    private boolean mShouldRecordTracker;

    private String[] mUrls = new String[]{
            "https://www.baidu.com/"
            ,"https://3w.huanqiu.com/a/de583b/7InhuI8vP4Q?agt=8/"
            ,"https://www.hao123.com/"};
    private int mIndex;

    public static void startActivity(Context context,int index) {
        Intent intent = new Intent();
        intent.putExtra("index",index);
        intent.setClass(context,PaperHelpActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getIntent() != null) {
            mIndex = getIntent().getIntExtra("index",0);
            setCurrentItem(mIndex);
        }

        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if(mShouldRecordTracker) {
                    if(i == 0) {
                    }else if(i == 1) {
                    }else if(i == 2) {
                    }
                }
                mShouldRecordTracker = true;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    public ArrayList<Fragment> getFragmentList(){
        return getWebViewFragmentList();
    }

    private ArrayList<Fragment> getWebViewFragmentList() {
        ArrayList<Fragment> list = new ArrayList<>();
        for (int i=0;i<mUrls.length;i++) {
            String url = mUrls[i];
            WebViewFragment webViewFragment =  new WebViewFragment();
            Bundle bundle = new Bundle();
            bundle.putString("url",url);
            webViewFragment.setArguments(bundle);
            list.add(webViewFragment);
        }
        return list;
    }

    @Override
    public String[] getTabText() {
        return new String[]{"百度","新闻","hao123"};
    }

}
