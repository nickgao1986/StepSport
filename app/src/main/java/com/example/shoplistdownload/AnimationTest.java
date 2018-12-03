package com.example.shoplistdownload;


import com.example.location.LocationObsever;
import com.example.location.LocationUtil;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.pic.optimize.R;


public class AnimationTest extends Activity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.animation_test);
		
		final LayoutInflater inflater = getLayoutInflater();
		final ImageButton button = (ImageButton) findViewById(R.id.btn);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Animation shake = AnimationUtils.loadAnimation(AnimationTest.this, R.anim.button_shake);
				shake.reset();
				shake.setFillAfter(true);
				button.startAnimation(shake);
			}
		});
		
		
		final int btn_margin_left_right = this.getResources().getDimensionPixelSize(R.dimen.btn_margin_left_right);
		final EditText custom_edittext = (EditText) findViewById(R.id.custom_edittext);
		custom_edittext.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

				if (s.length() > 0) {
					custom_edittext.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.search_bar_close_btn, 0);
				} else {
					custom_edittext.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				}

			}
		});
		
		final Button confirm = (Button) findViewById(R.id.btn_confirm);
		confirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(custom_edittext.getText().toString().equals("jake")){
					Toast.makeText(AnimationTest.this, "welcome", Toast.LENGTH_LONG).show();
				}else{
//					Animation shake = AnimationUtils.loadAnimation(AnimationTest.this, R.anim.shake_x);
//					custom_edittext.startAnimation(shake);
					AlertDialog dialog = getOneButtonDialog();
					dialog.show();
					
					
//					WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
//					lp.width = AnimationTest.this.getResources().getDimensionPixelSize(R.dimen.dialog_width);
//					lp.height = AnimationTest.this.getResources().getDimensionPixelSize(R.dimen.dialog_height);
//					dialog.getWindow().setAttributes(lp);
					
				}
			}
		});
		
		
		custom_edittext.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    Drawable[] compoundDrawables = custom_edittext.getCompoundDrawables();
                    if(compoundDrawables != null) {
                        Drawable image = compoundDrawables[DRAWABLE_RIGHT];

                        if(image != null) {
                            int leftMargin = custom_edittext.getRight() - custom_edittext.getPaddingRight() - image.getBounds().width() - btn_margin_left_right;
                            if(event.getX() >= leftMargin) {
                                if (custom_edittext != null && custom_edittext.getEditableText() != null) {
                                	custom_edittext.getEditableText().clear();
                                }
                            }
                        }
                    }
                }
                return false;
            }
        });

	}
	
	
	
	
	public AlertDialog getCustomDialog() {
		LayoutInflater inflater = getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_content, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(AnimationTest.this);
		builder.setView(view);
		builder.setTitle("A New Version is Available");
		builder.setPositiveButton("Upgrade Now",null).setNeutralButton("Cancel", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		return builder.create();
	}
	
	
	public AlertDialog getEditCustomDialog() {
		LayoutInflater inflater = getLayoutInflater();
		View view = inflater.inflate(R.layout.custom_message_rename, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(AnimationTest.this);
		builder.setView(view);
		builder.setTitle("A New Version is Available");
		return builder.create();
	}
	
	public AlertDialog getThreeButtonDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(AnimationTest.this);
		builder.setTitle("A New Version is Available").setMessage("6.4.1 is now available (you have 6.4.0). Would you like to upgrade now?");
		builder.setPositiveButton("Upgrade Now",null).setNeutralButton("Remind Me", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).setNegativeButton("Skip", null);
		return builder.create();
	}
	
	
	public AlertDialog getOneButtonDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(AnimationTest.this);
		builder.setTitle("A New Version is Available").setMessage("6.4.1 is now available (you have 6.4.0). To continue using this app, please upgrade now.");
		builder.setPositiveButton("Upgrade Now",null);
		return builder.create();
	}


}
