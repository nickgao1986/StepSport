package com.pic.optimize.expandabletext;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;

public class RcAlertDialog extends AlertDialog {
	
	protected RcAlertDialog(Context context) {
		super(context);
	}
	
	public static Builder getBuilder(Context context){
		return new Builder(context)
			.setOnKeyListener( new OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					if(keyCode == KeyEvent.KEYCODE_SEARCH){ // otherwise, dialog can be skipped with search button
						return true;
					}
					return false;
				}
			});
	}
	
}
