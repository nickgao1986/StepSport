package com.example.shoplistdownload;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import com.pic.optimize.R;

public class EditTextExample extends Activity {

	private EditText mCustom_edittext;
	private TextView left_word_num;
	private static final int MAX_INPUT_NUM = 200;
	private TextView other_char_hint;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.edittext);
		mCustom_edittext = (EditText)findViewById(R.id.custom_edittext);
		other_char_hint = (TextView)findViewById(R.id.other_char_hint);
		other_char_hint.setText("only enlish accepted");
		left_word_num = (TextView) findViewById(R.id.left_word_num);
		mCustom_edittext.addTextChangedListener(new TextWatcher() {

			private CharSequence temp;
			private int selectionStart;
			private int selectionEnd;

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				temp = s;
			}

			@Override
			public void afterTextChanged(Editable s) {
				selectionStart = mCustom_edittext.getSelectionStart();
				selectionEnd = mCustom_edittext.getSelectionEnd();
				
				String lastword = s.toString().trim();
				if (lastword.length() > 0) {
					lastword = lastword.substring(lastword.length() - 1, lastword.length());
					if(!isContentValid(temp.toString())) {
						other_char_hint.setVisibility(View.VISIBLE);
						for(int i=0;i < temp.toString().length();i++) {
							String temp1 = temp.toString().substring(i, i+1);
							if(!isInputValid(temp1)) {
								s.setSpan(new UnderlineSpan(), i, i+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							}
						}
					}else{
						other_char_hint.setVisibility(View.GONE);
					}
					
				}else{
					other_char_hint.setVisibility(View.GONE);
				}
				left_word_num.setText(String.valueOf(MAX_INPUT_NUM - temp.length()));
				
			}

		});
		
	}
	
	private boolean isContentValid(String content) {
		for(int i=0;i < content.length(); i++) {
			String value = content.substring(i,i+1);
			if(!isInputValid(value)) {
				return false;
			}
		}
		return true;
	}

	private boolean isInputValid(String s) {
		byte[] ch_array = s.getBytes();
		if (ch_array.length == 1) {
			return true;
		} else {
			return false;
		}
	}

}
