package com.pic.optimize.expandabletext;

import com.example.shoplistdownload.ContactsList;
import com.example.shoplistdownload.TestDropdownHideActivity;
import com.pic.optimize.R;
import com.ringcentral.android.utils.ui.widget.ExpandableEditText;
import com.ringcentral.android.utils.ui.widget.ExpandableEditTextContact;
import com.ringcentral.android.utils.ui.widget.ExpandableEditText.OnContactEditClickListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

public class ExpandableEditActivity extends Activity implements OnContactEditClickListener{

	private ExpandableEditText mEditContactArea;
	private int mIsKeyBoardShow = 0;
	private InputMethodManager mInputMethodManager;
	
	public static final int SOFT_KEY_BOARD_HIDE = 0;
	public static final int SOFT_KEY_BOARD_SHOW = 1;
	public static final int SOFT_KEY_BOARD_MSG = -999;
	
	private Handler hideKeyboardHandler = new Handler();
	public static final int CONTACT_REQUEST_CODE = 0;

	public static void startActivity(Context context) {
		Intent intent = new Intent();
		intent.setClass(context,ExpandableEditActivity.class);
		context.startActivity(intent);
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.expandedit_activity_main);
		
		mEditContactArea = (ExpandableEditText) findViewById(R.id.edit_contact_area);
		mEditContactArea.setEditTextImeOptionsNext();
		mEditContactArea.setOnContactEditClickListener(this);
		
		mEditContactArea.setAddButtonClickListener(new OnClickListener() {

			public void onClick(View v) {
				if(mIsKeyBoardShow == SOFT_KEY_BOARD_SHOW){
					hideKeyboard();
				}
				hideKeyboardHandler.postDelayed(new Runnable(){

					@Override
					public void run() {
						Intent intent = new Intent(RCMConstants.ACTION_LIST_CONTACTS);
						intent.setClass(ExpandableEditActivity.this, ContactSelectorActivity.class);
						startActivityForResult(intent,CONTACT_REQUEST_CODE);
					}
					
				}, 150);
				
			}
		});
		
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (requestCode == CONTACT_REQUEST_CODE && data != null) {
			String name 	= data.getStringExtra(RCMConstants.EXTRA_CONTACT_NAME);
			String number 	= data.getStringExtra(RCMConstants.EXTRA_CONTACT_NUMBER);
			
			ExpandableEditTextContact contact = new ExpandableEditTextContact(this, name, number, true);
			mEditContactArea.setContact(contact);
		}

		if (mEditContactArea != null 
				&& mEditContactArea.isEditFocus() && mInputMethodManager != null) {
			mInputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		}
		
	}
	
	
	private void hideKeyboard() {
		if (mInputMethodManager != null && getCurrentFocus() != null) {
			mInputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
			mIsKeyBoardShow = SOFT_KEY_BOARD_HIDE;
		}
		mEditContactArea.setKeyboardVisible(false);
	}
	



	@Override
	public void onContactEditClick() {
		
	}
}
