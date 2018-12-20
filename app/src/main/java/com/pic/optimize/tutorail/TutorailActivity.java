package com.pic.optimize.tutorail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.CycleInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pic.optimize.R;

import java.util.ArrayList;
import java.util.List;

public class TutorailActivity extends Activity {

	private int mImageWidth = 0;
	private int offset = 0;
	private int currIndex = 0;
	private ViewPager viewPager;
	private View view1,view2,view3,view4;
	private List<View> views;
	private ImageView img;
	private TextView textView1,textView2,textView3,textView4;
	ImageView t1_fixed,t1_icon1,t1_icon2,t1_next;
	
	ImageView t2_fixed,t2_icon1,t2_next;
	ImageView t4_fixed,t4_icon1;
	ImageView t3_fixed,t3_next,t3_icon6;
	RelativeLayout center_layout_3;
	
	AnimationDrawable t1_icon1_animationDrawable;

	public static void startActivity(Context context) {
		Intent intent = new Intent();
		intent.setClass(context,TutorailActivity.class);
		context.startActivity(intent);
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pager_layout);
		mImageWidth = this.getResources().getDimensionPixelSize(R.dimen.image_width);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;
		offset = (screenW / 4 - mImageWidth) / 2;
		
		img = (ImageView)findViewById(R.id.img);
		LayoutParams layout = (LayoutParams) img.getLayoutParams();
		layout.leftMargin = offset;
		InitViewPager();
		initAnimationForEachView(0);
	}

	private void InitViewPager() {
		viewPager=(ViewPager) findViewById(R.id.vPager);
		views=new ArrayList<View>();
		LayoutInflater inflater=getLayoutInflater();
		view1=inflater.inflate(R.layout.layout_tutorial_1, null);
		view2=inflater.inflate(R.layout.layout_tutorial_2, null);
		view3=inflater.inflate(R.layout.layout_tutorial_3, null);
		view4=inflater.inflate(R.layout.layout_tutorial_4, null);
		views.add(view1);
		views.add(view2);
		views.add(view3);
		views.add(view4);
		viewPager.setAdapter(new MyViewPagerAdapter(views));
		viewPager.setCurrentItem(0);
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		InitTextView();
	}
	
	private void InitTextView() {
		textView1 = (TextView) findViewById(R.id.tab_name);
		textView2 = (TextView) findViewById(R.id.tab_name1);
		textView3 = (TextView) findViewById(R.id.tab_name2);
		textView4 =  (TextView) findViewById(R.id.tab_name3);

		textView1.setOnClickListener(new MyOnClickListener(0));
		textView2.setOnClickListener(new MyOnClickListener(1));
		textView3.setOnClickListener(new MyOnClickListener(2));
		textView4.setOnClickListener(new MyOnClickListener(3));
		
		t1_fixed = (ImageView)view1.findViewById(R.id.t1_fixed);
	    t1_icon1 = (ImageView)view1.findViewById(R.id.t1_icon1);
	    t1_icon2 = (ImageView)view1.findViewById(R.id.t1_icon2);
	    t1_next = (ImageView)view1.findViewById(R.id.t1_next);
	  
	    t2_fixed = (ImageView)view2.findViewById(R.id.t2_fixed);
	    t2_icon1 = (ImageView)view2.findViewById(R.id.t2_icon1);
	    t2_next = (ImageView)view2.findViewById(R.id.t2_next);
	    
	    t3_fixed = (ImageView)view3.findViewById(R.id.t3_fixed);
	    t3_next = (ImageView)view3.findViewById(R.id.t3_next);
	    center_layout_3 = (RelativeLayout)view3.findViewById(R.id.center_layout_3);
	    t3_icon6 = (ImageView)view3.findViewById(R.id.t3_icon6);
	    
	    t4_fixed = (ImageView)view4.findViewById(R.id.t4_fixed);
	    t4_icon1 = (ImageView)view4.findViewById(R.id.t4_icon1);
	}

	
	private void initAnimationForEachView(int page) {
		if(page == 0) {
			t1_icon1.setImageResource(R.drawable.t1_frame_animation);  
            t1_icon1_animationDrawable = (AnimationDrawable) t1_icon1  
                    .getDrawable();  
            t1_icon1_animationDrawable.start();  
            
            Animation tutorail_rotate = AnimationUtils.loadAnimation(TutorailActivity.this, R.anim.tutorail_rotate);
            t1_icon2.startAnimation(tutorail_rotate);
            Animation tutorail_scalate = AnimationUtils.loadAnimation(TutorailActivity.this, R.anim.tutorail_scalate);
            t1_fixed.startAnimation(tutorail_scalate);
           
            Animation tutorail_bottom = AnimationUtils.loadAnimation(TutorailActivity.this, R.anim.tutorail_bottom);
            t1_next.startAnimation(tutorail_bottom);
            
		}else if(page == 1) {
		    Animation tutorail_rotate = AnimationUtils.loadAnimation(TutorailActivity.this, R.anim.tutorail_scalate);
		    t2_fixed.startAnimation(tutorail_rotate);
            
            Animation tutorail_scalate = AnimationUtils.loadAnimation(TutorailActivity.this, R.anim.tutorail_scalate);
            tutorail_scalate.setFillAfter(true);
            t2_icon1.startAnimation(tutorail_scalate);
            
            Animation tutorail_bottom = AnimationUtils.loadAnimation(TutorailActivity.this, R.anim.tutorail_bottom);
            t2_next.startAnimation(tutorail_bottom);
            
		}else if(page == 2){
			 Animation tutorail_rotate = AnimationUtils.loadAnimation(TutorailActivity.this, R.anim.tutorail_scalate);
			 t3_fixed.startAnimation(tutorail_rotate);
			
			t3_icon6.setImageResource(R.drawable.t3_frame_animation);
			AnimationDrawable t3_icon6_animationDrawable = (AnimationDrawable) t3_icon6.getDrawable();
			t3_icon6_animationDrawable.start();
	            
			 Animation tutorail_bottom = AnimationUtils.loadAnimation(TutorailActivity.this, R.anim.tutorail_bottom);
	         t3_next.startAnimation(tutorail_bottom);
		}else if(page == 3){
			int pivot = Animation.RELATIVE_TO_SELF;
			CycleInterpolator interpolator = new CycleInterpolator(3.0f);
			RotateAnimation animation = new RotateAnimation(0, 10, pivot, 0.47f, pivot, 0.05f);
			animation.setStartOffset(500);
			animation.setDuration(3000);
			animation.setRepeatCount(1);// Animation.INFINITE
			animation.setInterpolator(interpolator);
			t4_icon1.startAnimation(animation);
			  
			Animation tutorail_scalate = AnimationUtils.loadAnimation(TutorailActivity.this, R.anim.tutorail_scalate);
			t4_fixed.startAnimation(tutorail_scalate);
		}
	}
	
	
	private void performSocketAnimation() {

	}


	private class MyOnClickListener implements OnClickListener{
        private int index=0;
        public MyOnClickListener(int i){
        	index=i;
        }
		public void onClick(View v) {
			viewPager.setCurrentItem(index);			
		}
		
	}
	
	

	public class MyViewPagerAdapter extends PagerAdapter{
		private List<View> mListViews;
		
		public MyViewPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) 	{	
			container.removeView(mListViews.get(position));
		}


		@Override
		public Object instantiateItem(ViewGroup container, int position) {			
			 container.addView(mListViews.get(position), 0);
			 return mListViews.get(position);
		}

		@Override
		public int getCount() {			
			return  mListViews.size();
		}
		
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {			
			return arg0==arg1;
		}
	}

    public class MyOnPageChangeListener implements OnPageChangeListener{

    	int one = offset * 2 + mImageWidth;
		int two = one * 2;
		public void onPageScrollStateChanged(int arg0) {
			
			
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
			
		}

		public void onPageSelected(final int arg0) {
			
			
			Animation animation = new TranslateAnimation(one * currIndex, one * arg0, 0, 0);// ��Ȼ����Ƚϼ�ֻ࣬��һ�д��롣
			currIndex = arg0;
			animation.setFillAfter(true);
			animation.setDuration(300);
			img.startAnimation(animation);
			initAnimationForEachView(arg0);
			
		}
    	
    }
    
    public void slideview(final float p1, final float p2) {
	    TranslateAnimation animation = new TranslateAnimation(p1, p2, 0, 0);
	    animation.setInterpolator(new OvershootInterpolator());
	    animation.setDuration(300);
	    animation.setAnimationListener(new Animation.AnimationListener() {
	        @Override
	        public void onAnimationStart(Animation animation) {
	        }
	        
	        @Override
	        public void onAnimationRepeat(Animation animation) {
	        }
	        
	        @Override
	        public void onAnimationEnd(Animation animation) {
	            int left = img.getLeft()+(int)(p2-p1);
	            int top = img.getTop();
	            int width = img.getWidth();
	            int height = img.getHeight();
	            img.clearAnimation();
	            img.layout(left, top, left+width, top+height);
	        }
	    });
	    img.startAnimation(animation);
	}
}
