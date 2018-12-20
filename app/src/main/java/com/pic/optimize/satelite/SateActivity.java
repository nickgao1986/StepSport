package com.pic.optimize.satelite;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.pic.optimize.R;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class SateActivity extends Activity {

	
	private static final int DEFAULT_SATELLITE_DISTANCE = 200;
	private int satelliteDistance = DEFAULT_SATELLITE_DISTANCE;	
	private static final int DEFAULT_EXPAND_DURATION = 400;
	private int distance = 0;
	private int mAngle = 50;
	private Animation mainRotateLeft;
	private Map<View, SatelliteMenuItem> viewToItemMap = new HashMap<View, SatelliteMenuItem>();
	private  Animation mainRotateRight;
	private  ImageView sat_main;

	public static void startActivity(Context context) {
		Intent intent = new Intent();
		intent.setClass(context,SateActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.satetext);
	    sat_main = (ImageView)findViewById(R.id.sat_main);
		mainRotateLeft = createMainButtonAnimation(this);
		mainRotateRight = createMainButtonInverseAnimation(this);
		distance = this.getResources().getDimensionPixelSize(R.dimen.distance);

		int x = getTranslateX(mAngle,distance);
		int y = getTranslateY(mAngle, distance);

		final Animation itemClick = createItemClickAnimation(this);
		final ImageView itemView = (ImageView)findViewById(R.id.sat_item);
		
		final ImageView cloneView = (ImageView)findViewById(R.id.clone_item);
		cloneView.setImageResource(R.drawable.searchable_web);
		
		RelativeLayout.LayoutParams layoutParams =(RelativeLayout.LayoutParams) cloneView.getLayoutParams();
		layoutParams.bottomMargin = Math.abs(y);
		layoutParams.leftMargin = Math.abs(x);
		cloneView.setLayoutParams(layoutParams);
		
		cloneView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				v.startAnimation(itemClick);
			}
		});
		
		itemView.setVisibility(View.GONE);
		
		SatelliteMenuItem menuItem = new SatelliteMenuItem(1, R.drawable.searchable_web);
		final Animation itemOut = createItemOutAnimation(this, 0,DEFAULT_EXPAND_DURATION, x, y);
		Animation itemIn = createItemInAnimation(this, 0, DEFAULT_EXPAND_DURATION, x, y);

		
		menuItem.setView(itemView);
		menuItem.setInAnimation(itemIn);
		menuItem.setOutAnimation(itemOut);
		menuItem.setCloneView(cloneView);
		menuItem.setClickAnimation(itemClick);
		menuItem.setFinalX(x);
		menuItem.setFinalY(y);

		itemView.setImageResource(R.drawable.searchable_web);
		viewToItemMap.put(itemView, menuItem);
		itemOut.setAnimationListener(new SatelliteAnimationListener(itemView, false, viewToItemMap));
		itemIn.setAnimationListener(new SatelliteAnimationListener(itemView, true, viewToItemMap));

		itemClick.setAnimationListener(new SatelliteItemClickAnimationListener(menuItem));
		
		sat_main.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				itemView.setVisibility(View.VISIBLE);
				sat_main.startAnimation(mainRotateLeft);
				itemView.startAnimation(itemOut);
			}
		});
		
	}
	
	 public static Animation createMainButtonInverseAnimation(Context context){
	    	return AnimationUtils.loadAnimation(context, R.anim.sat_main_rotate_right);
	 }
	
    
    public static Animation createItemClickAnimation(Context context){
    	return AnimationUtils.loadAnimation(context, R.anim.sat_item_anim_click);
    }
    
	
	public static Animation createMainButtonAnimation(Context context){
    	return AnimationUtils.loadAnimation(context, R.anim.sat_main_rotate_left);
    }
	
	
	private class SatelliteItemClickAnimationListener implements Animation.AnimationListener {
		
		private SatelliteMenuItem menuItem;
		
		public SatelliteItemClickAnimationListener(SatelliteMenuItem menuItem) {
			this.menuItem = menuItem;
		}
		
		@Override
		public void onAnimationEnd(Animation animation) {
		}
		
		@Override
		public void onAnimationRepeat(Animation animation) {
		}
		
		@Override
		public void onAnimationStart(Animation animation) {
			sat_main.startAnimation(mainRotateRight);
			System.out.println("====menuItem="+menuItem);
			menuItem.getView().startAnimation(menuItem.getInAnimation());
		}		
	}
    
	
	private  class SatelliteAnimationListener implements Animation.AnimationListener {
		private WeakReference<View> viewRef;
		private boolean isInAnimation;
		private Map<View, SatelliteMenuItem> viewToItemMap;

		public SatelliteAnimationListener(View view, boolean isIn, Map<View, SatelliteMenuItem> viewToItemMap) {
			this.viewRef = new WeakReference<View>(view);
			this.isInAnimation = isIn;
			this.viewToItemMap = viewToItemMap;
		}

		@Override
		public void onAnimationStart(Animation animation) {
			if (viewRef != null) {
				View view = viewRef.get();
				if (view != null) {
					SatelliteMenuItem menuItem = viewToItemMap.get(view);
					if (isInAnimation) {
						menuItem.getView().setVisibility(View.VISIBLE);
						menuItem.getCloneView().setVisibility(View.GONE);
					} else {
						menuItem.getCloneView().setVisibility(View.GONE);
						menuItem.getView().setVisibility(View.VISIBLE);
					}
				}
			}
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			if (viewRef != null) {
				View view = viewRef.get();
				if (view != null) {
					SatelliteMenuItem menuItem = viewToItemMap.get(view);

					if (isInAnimation) {
						menuItem.getView().setVisibility(View.GONE);
						menuItem.getCloneView().setVisibility(View.GONE);
					} else {
						menuItem.getCloneView().setVisibility(View.VISIBLE);
						menuItem.getView().setVisibility(View.GONE);
					}
				}
			}
		}
	}
	
	
	public static Animation createItemInAnimation(Context context, int index, long expandDuration, int x, int y){        
        RotateAnimation rotate = new RotateAnimation(720, 0, 
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        
        rotate.setInterpolator(context, R.anim.sat_item_in_rotate_interpolator);
        rotate.setDuration(expandDuration);
        
        TranslateAnimation translate = new TranslateAnimation(x, 0, y, 0);
        
        
        long delay = 250;
        if(expandDuration <= 250){
            delay = expandDuration / 3;
        }         
        
        long duration = 400;
        if((expandDuration-delay) > duration){
        	duration = expandDuration-delay; 
        }
        
        translate.setDuration(duration);
        translate.setStartOffset(delay);        
        
        translate.setInterpolator(context, R.anim.sat_item_anticipate_interpolator);
        
        AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0f);
        long alphaDuration = 10;
        if(expandDuration < 10){
        	alphaDuration = expandDuration / 10;
        }
        alphaAnimation.setDuration(alphaDuration);
        alphaAnimation.setStartOffset((delay + duration) - alphaDuration);
        
        AnimationSet animationSet = new AnimationSet(false);
        animationSet.setFillAfter(false);
        animationSet.setFillBefore(true);
        animationSet.setFillEnabled(true);
        
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(rotate);
        animationSet.addAnimation(translate);
        
        
        animationSet.setStartOffset(30*index);
        animationSet.start();
        animationSet.startNow();
        return animationSet;
    }
	
	public static Animation createItemOutAnimation(Context context, int index, long expandDuration, int x, int y){
    	
        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
        long alphaDuration = 60;
        if(expandDuration < 60){
        	alphaDuration = expandDuration / 4;
        }
        alphaAnimation.setDuration(alphaDuration);
        alphaAnimation.setStartOffset(0);

        
        TranslateAnimation translate = new TranslateAnimation(0, x, 0, y);
         
        translate.setStartOffset(0);
        translate.setDuration(expandDuration); 
        translate.setInterpolator(context, R.anim.sat_item_overshoot_interpolator);
        
        RotateAnimation rotate = new RotateAnimation(0f, 360f, 
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        

        rotate.setInterpolator(context, R.anim.sat_item_out_rotate_interpolator);
        
        long duration = 100;
        if(expandDuration <= 150){
            duration = expandDuration / 3;
        }
        
        rotate.setDuration(expandDuration-duration);
        rotate.setStartOffset(duration);        
        
        AnimationSet animationSet = new AnimationSet(false);
        animationSet.setFillAfter(false);
        animationSet.setFillBefore(true);
        animationSet.setFillEnabled(true);
                
        //animationSet.addAnimation(alphaAnimation);
        //animationSet.addAnimation(rotate);
        animationSet.addAnimation(translate);
        
        animationSet.setStartOffset(30*index);
        
        return animationSet;
    }

	
	public static int getTranslateX(float degree, int distance) {
		return Double.valueOf(distance * Math.cos(Math.toRadians(degree))).intValue();
	}
	
	public static int getTranslateY(float degree, int distance){
        return Double.valueOf(-1 * distance * Math.sin(Math.toRadians(degree))).intValue();
    }
	 
}
