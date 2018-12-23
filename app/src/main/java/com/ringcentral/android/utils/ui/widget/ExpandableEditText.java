/** 
 * Copyright (C) 2010-2013, RingCentral, Inc. 
 * All Rights Reserved.
 */
package com.ringcentral.android.utils.ui.widget;
import java.util.ArrayList;



import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.pic.optimize.R;


/**
 * ExpandableEditText base requirement http://jira.ringcentral.com/browse/AB-7286 
 * User can select text value or input text, its height is automatic increase.
 * @author jerry.cai
 *
 */
public class ExpandableEditText extends RelativeLayout implements View.OnClickListener {
	
	private static final String TAG = "ExpandableEditText";

	private static final String STRING_AND_MORE_SAMPLE = " & % more...";
	private static final String STRING_AND_MORE_ELLIPSIS = " & %d more...";
	private static final String STRING_ELLIPSIS_AND_MORE = "... & %d more";
	private static final String STRING_ELLIPSIS = "...";
	private static final String BOLDSTYLE = "bold";
	
	private TextView mTitleText;
	private TextView mSingleLineText;
	private InnerEditText mInnerEditText;
	private ImageView mAddButton;
	
	private static int MIN_EDIT_WIDTH = 100;
	private static int MIN_BUTTON_WIDTH = 80;
	private static int MIN_TEXT_WIDTH = (MIN_EDIT_WIDTH + MIN_BUTTON_WIDTH) / 3;
	private static int PADDING = 10;
	private static int PADDING_LEFT = 20;
	private static int MAX_RECIPIENT_COUNT = 20;
	
	private int mLines = 0;
	ArrayList<InnerTextView> mTotalTexts = new ArrayList<InnerTextView>();
	ArrayList<ExpandableEditTextContact> mTotalContact = new ArrayList<ExpandableEditTextContact>();
	
	private int mCurrentSelect = -1;
	
	int mWidthMeasureSpec, mHeightMeasureSpec;
	boolean mIsShowTextsSingleLine = false;
	
	private OnDispatchKeyBackListener mOnDispatchKeyBackListener;
	private OnBeforeHideAllItemsListener mOnBeforeHideAllItemsListener;
	private OnAfterShowAllItemsListener mOnAfterShowAllItemsListener;

	private OnClickNextListener mOnClickNextListener;
	private OnDeleteContactListener mOnDeleteContactListener;
	private OnFocusChangeListener mOuterFocusListener;
	private OnContactEditClickListener mOnContactEditClickListener;
	private OnAfterToTextChangedListener mOnAfterToTextChangedListener;
	private OnHeightChangedListener mOnHeightChangedListener;
	OnClickListener mAddButtonOnClickListener;
	private TextWatcher mOuterTextWatcher;
	
	//ui setting
	private String mTitleTextStr;
	private String mTitleStyle;
	private float mTitleFontSize;
	private int	   mTitleFontColor;
	private String mItemStyle;
	private float mItemFontSize;
	private int   mItemFontColor;
	private final int TEXT_DEFAULT_COLOR = Color.BLACK;
	private final int TEXT_DEFAULT_SIZE = 16;
	private boolean mIsAllItemsHide = false, mKeyboardShowing = false, mIsFirstLoad = false;
	
	private AlertDialog mDialog = null;
	private int mOneLineHeight = 0;
	
	private static String _empty = "  ";
	
	public ExpandableEditText(Context context){
		super(context);
		initParams();
		intSubView();
	}
	
	public ExpandableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initParams();
        
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ExpandableEditText, 0, 0);
		int n = a.getIndexCount();

		for (int i = 0; i < n; i++) {
			int attr = a.getIndex(i);
			switch (attr) {
			case R.styleable.ExpandableEditText_titleText:
				mTitleTextStr = a.getString(attr);
				break;
			case R.styleable.ExpandableEditText_titleTextSize:
				mTitleFontSize = a.getDimensionPixelSize(attr, TEXT_DEFAULT_SIZE);
				break;
			case R.styleable.ExpandableEditText_titleTextStyle1:
				mTitleStyle = a.getString(attr);
				break;
			case R.styleable.ExpandableEditText_itemTextStyle:
				mItemStyle = a.getString(attr);
				break;
			case R.styleable.ExpandableEditText_itemTextSize:
				mItemFontSize = a.getDimensionPixelSize(attr, TEXT_DEFAULT_SIZE);
				break;
			case R.styleable.ExpandableEditText_itemTextColor:
				mItemFontColor = a.getColor(attr, TEXT_DEFAULT_COLOR);
				break;
			}
		}
		a.recycle();
        
        intSubView();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		ViewGroup.LayoutParams layoutParams = this.getLayoutParams();
		
		mWidthMeasureSpec = widthMeasureSpec;
		mHeightMeasureSpec = heightMeasureSpec;
		
		setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
		
		//try to measure
		/*
		{
			int itemHeight = mTitleText.getMeasuredHeight();
			if(mInnerEditText.getMeasuredHeight() > itemHeight){
				itemHeight = mInnerEditText.getMeasuredHeight();
			}
			
			if(mAddButton.getMeasuredHeight() > itemHeight){
				itemHeight = mAddButton.getMeasuredHeight();
			}
			
		}*/
		
		// Children are just made to fill our space.
		int paddingHeight = getPaddingTop() + getPaddingBottom();
		int childWidthSize = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
		PADDING_LEFT = getPaddingLeft();
		int childHeightSize = getMeasuredHeight() - paddingHeight;
		int currentWidth = childWidthSize;
		int currentHeight = childHeightSize;
		int totalHeight = 0;
		
		if (mTitleText.getVisibility() != GONE) {
			final LayoutParams lp = (LayoutParams) mTitleText.getLayoutParams();
			if (lp != null) {
				int widthMode = MeasureSpec.UNSPECIFIED;
				int heightMode = MeasureSpec.UNSPECIFIED;
				final int widthSpec = MeasureSpec.makeMeasureSpec(childWidthSize, widthMode);
				final int heightSpec = MeasureSpec.makeMeasureSpec(childHeightSize, heightMode);
				mTitleText.measure(widthSpec, heightSpec);
				currentWidth -= (mTitleText.getMeasuredWidth() + PADDING_LEFT);
				totalHeight += (mTitleText.getMeasuredHeight() + PADDING);
			}
		} else {
			totalHeight += (mTitleText.getMeasuredHeight() + PADDING);
		}
		
		if (false == mIsShowTextsSingleLine) {
			mLines = 1;
			int textHeight = 0;
			boolean isNeedIncreaseHeight = false;
			for (int i= 0; i< mTotalTexts.size(); i++) {
				InnerTextView tv = mTotalTexts.get(i);
				if (tv != null && tv.getVisibility() != GONE) {
					final LayoutParams lp = (LayoutParams)tv.getLayoutParams();
					if (lp == null) {
						continue;
					}
					tv.setHeight(mTitleText.getMeasuredHeight());
					
					int widthMode = MeasureSpec.UNSPECIFIED;
					int heightMode = MeasureSpec.UNSPECIFIED;
					if (i != 0 && currentWidth >= MIN_TEXT_WIDTH) {
						if (tv.getPaint().measureText(tv.getText().toString()) + tv.getPaddingLeft() + tv.getPaddingRight() > currentWidth) {
							currentWidth = childWidthSize;
							tv.setMaxWidth(childWidthSize);
							int widthSpec = MeasureSpec.makeMeasureSpec(childWidthSize, widthMode);
							int heightSpec = MeasureSpec.makeMeasureSpec(currentHeight, heightMode);
							tv.measure(widthSpec, heightSpec);

							tv.setLineFirstItem(true);
							currentHeight -= (tv.getMeasuredHeight() + PADDING);
							totalHeight += (tv.getMeasuredHeight() + PADDING);
							mLines ++;
						} else {
							tv.setLineFirstItem(false);
							tv.setMaxWidth(currentWidth);
							int widthSpec = MeasureSpec.makeMeasureSpec(currentWidth, widthMode);
							int heightSpec = MeasureSpec.makeMeasureSpec(currentHeight, heightMode);
							tv.measure(widthSpec, heightSpec);
						}
					} else {
						tv.setLineFirstItem(true);
						if (i != 0) {
							currentWidth = childWidthSize;
						}

						tv.setMaxWidth(currentWidth);
						int widthSpec = MeasureSpec.makeMeasureSpec(currentWidth, widthMode);
						int heightSpec = MeasureSpec.makeMeasureSpec(currentHeight, heightMode);
						tv.measure(widthSpec, heightSpec);
						if (i != 0) {
							currentHeight -= (tv.getMeasuredHeight() + PADDING);
							totalHeight += (tv.getMeasuredHeight() + PADDING);
							mLines ++;
						}
					}
					currentWidth -= (tv.getMeasuredWidth() + PADDING);
					textHeight = tv.getMeasuredHeight();
					tv.setLineNumber(mLines);
				}

			}
			
			if (mInnerEditText.getVisibility() != GONE) {
				final LayoutParams lp = (LayoutParams)mInnerEditText.getLayoutParams();
				if (lp != null) {
					int widthMode = MeasureSpec.UNSPECIFIED;
					int heightMode = MeasureSpec.UNSPECIFIED;
					
					if (currentWidth < PADDING + MIN_EDIT_WIDTH + MIN_BUTTON_WIDTH) {
						currentHeight -= (((textHeight == 0) ? mTitleText.getMeasuredHeight() : textHeight) + PADDING);
						//totalHeight += (((textHeight == 0)?mTitleText.getMeasuredHeight():textHeight) + PADDING);
						mLines +=1;
						currentWidth = childWidthSize;
						isNeedIncreaseHeight = true;
					}
					
					mInnerEditText.setMinimumWidth(currentWidth - MIN_BUTTON_WIDTH - PADDING);
					mInnerEditText.setMaxWidth(currentWidth - MIN_BUTTON_WIDTH - PADDING);
					final int widthSpec = MeasureSpec.makeMeasureSpec(currentWidth - MIN_BUTTON_WIDTH - PADDING, widthMode);
					final int heightSpec = MeasureSpec.makeMeasureSpec(currentHeight, heightMode);
					mInnerEditText.measure(widthSpec, heightSpec);
					currentWidth -= (mInnerEditText.getMeasuredWidth() + PADDING);
					
					if (isNeedIncreaseHeight) {
						totalHeight += mInnerEditText.getMeasuredHeight();
					}
					mInnerEditText.setLineNumber(mLines);
				}
			}
			
			if (mAddButton.getVisibility() != GONE) {
				final LayoutParams lp = (LayoutParams) mAddButton.getLayoutParams();
				if (lp != null) {
					mAddButton.setMinimumHeight(mTitleText.getMeasuredHeight());
					int widthMode = MeasureSpec.UNSPECIFIED;
					int heightMode = MeasureSpec.UNSPECIFIED;
					final int widthSpec = MeasureSpec.makeMeasureSpec(currentWidth, widthMode);
					final int heightSpec = MeasureSpec.makeMeasureSpec(currentHeight, heightMode);
					mAddButton.measure(widthSpec, heightSpec);
					
				}
			}
			
			if (mLines == 1) {
				if (mInnerEditText.getMeasuredHeight() > textHeight)
					textHeight = mInnerEditText.getMeasuredHeight();
				//if (mAddButton.getMeasuredHeight() > textHeight)
				//	textHeight = mAddButton.getMeasuredHeight();

				layoutParams.height = textHeight + paddingHeight;
			} else {
				if(isNeedIncreaseHeight){
					layoutParams.height = totalHeight + paddingHeight;
				}else{
					layoutParams.height = totalHeight + paddingHeight - PADDING;
				}
				
			}
			
		} else {
			if (mSingleLineText != null && mSingleLineText.getVisibility() != GONE) {
				final LayoutParams lp = (LayoutParams) mSingleLineText.getLayoutParams();
				if (lp != null) {
					int widthMode = MeasureSpec.UNSPECIFIED;
					int heightMode = MeasureSpec.UNSPECIFIED;
					if (currentWidth > MIN_TEXT_WIDTH) {
						mSingleLineText.setMaxWidth(currentWidth);
					}
					
					final int widthSpec = MeasureSpec.makeMeasureSpec(currentWidth, widthMode);
					final int heightSpec = MeasureSpec.makeMeasureSpec(childHeightSize, heightMode);
					mSingleLineText.measure(widthSpec, heightSpec);
					totalHeight = mSingleLineText.getMeasuredHeight();
				}
			}

			layoutParams.height = totalHeight + paddingHeight;
		}
		
		mOneLineHeight = mTitleText.getMeasuredHeight();
	   
	    this.setLayoutParams(layoutParams);

	    if (mOnHeightChangedListener != null) {
	    	mOnHeightChangedListener.onHeightChanged(layoutParams.height, mOneLineHeight,PADDING, paddingHeight);
	    }
	}
	
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (this.getChildCount() > 0) {
			l = l + getPaddingLeft();
			r = r - getPaddingRight();

			int currentLeft = l;
			int currentTop = getPaddingTop();
			if (mTitleText.getVisibility() != GONE) {
				final LayoutParams lp = (LayoutParams) mTitleText.getLayoutParams();
				if (lp != null) {
					mTitleText.layout(currentLeft, currentTop, currentLeft + mTitleText.getMeasuredWidth(), 
							currentTop + mTitleText.getMeasuredHeight());
					currentLeft += (mTitleText.getMeasuredWidth() + PADDING_LEFT);
					if (currentLeft > r) {
						currentTop = (mTitleText.getMeasuredHeight() + PADDING);
						currentLeft = l;
					}
				}
			}
			if (false == mIsShowTextsSingleLine) {
				int textHeight = 0;
				
				for (int i = 0; i < mTotalTexts.size(); i++) {
					InnerTextView tv = mTotalTexts.get(i);
					if (tv != null && tv.getVisibility() != GONE) {
						
						final LayoutParams lp = (LayoutParams) tv.getLayoutParams();
						
						if (lp != null) {
							if (tv.isLineFirstItem()) {
								if (i != 0) {
									currentTop += (tv.getMeasuredHeight() + PADDING);
									currentLeft = l;
								}
							}
							
							tv.layout(currentLeft, currentTop, 
									currentLeft + tv.getMeasuredWidth(),
									currentTop + tv.getMeasuredHeight());
							
							currentLeft+= tv.getMeasuredWidth() + PADDING;
							textHeight = tv.getMeasuredHeight();
							
							tv.invalidate();
						}
					}
				}
				
				if (mInnerEditText.getVisibility() != GONE) {
					final LayoutParams lp = (LayoutParams) mInnerEditText.getLayoutParams();
					if (lp != null) {
						if (r - currentLeft < PADDING + MIN_EDIT_WIDTH + MIN_BUTTON_WIDTH) {
							currentTop += (((textHeight == 0) ? mTitleText.getMeasuredHeight() : textHeight) + PADDING);
							currentLeft = getPaddingLeft();
						}
						
						
						mInnerEditText.layout(currentLeft, currentTop, 
								currentLeft + mInnerEditText.getMeasuredWidth(),
								currentTop  + mInnerEditText.getMeasuredHeight());
						currentLeft += (mInnerEditText.getMeasuredWidth() + PADDING);
					}
				}
				
				if (mAddButton.getVisibility() != GONE) {
					final LayoutParams lp = (LayoutParams) mAddButton.getLayoutParams();
					if (lp != null) {
						mAddButton.layout(currentLeft, currentTop, currentLeft + mAddButton.getMeasuredWidth(), 
								currentTop + mAddButton.getMeasuredHeight());
					}
				}
			} else {

				if (mSingleLineText != null && mSingleLineText.getVisibility() != GONE) {
					final LayoutParams lp = (LayoutParams) mSingleLineText.getLayoutParams();
					if (lp != null) {
						mSingleLineText.layout(currentLeft, currentTop, currentLeft + mSingleLineText.getMeasuredWidth(), 
								currentTop + mSingleLineText.getMeasuredHeight());
					}
				}
			}
		}
	}
	
	private void initParams(){
		mTitleTextStr = "";
		mTitleStyle = mItemStyle = "";
		mTitleFontSize = mItemFontSize = TEXT_DEFAULT_SIZE;
		mTitleFontColor = mItemFontColor = TEXT_DEFAULT_COLOR;
		mIsFirstLoad = false;
	}
	
	private void intSubView(){
		this.setOnClickListener(this);
		
		Bitmap btnBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.add_light);
		MIN_EDIT_WIDTH = 100;
		MIN_BUTTON_WIDTH = btnBitmap.getWidth();
		MIN_TEXT_WIDTH = (MIN_EDIT_WIDTH + MIN_BUTTON_WIDTH)/3;
		btnBitmap.recycle();
		
		mTitleText = new TextView(getContext());
		mTitleText.setText(mTitleTextStr);
//		mTitleText.setFocusable(true);
		if (BOLDSTYLE.equals(mTitleStyle)) {
			mTitleText.getPaint().setFakeBoldText(true);
		}
		mTitleText.setTextColor(mTitleFontColor);
		mTitleText.setTextSize(mTitleFontSize);
		mTitleText.getPaint().setTextSize(mTitleFontSize);
		mTitleText.setGravity(Gravity.CENTER_VERTICAL);
		mTitleText.setBackgroundResource(R.drawable.bg_new_message_to_bg);
		this.addView(mTitleText);
		
		mSingleLineText = new TextView(getContext());
		mSingleLineText.setSingleLine(true);
		mSingleLineText.setBackgroundResource(R.drawable.bg_new_message_to_bg);
		mSingleLineText.setGravity(Gravity.CENTER_VERTICAL);
		mSingleLineText.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
		mSingleLineText.setTextSize(mItemFontSize);
		mSingleLineText.getPaint().setTextSize(mItemFontSize);
		mSingleLineText.setTextColor(mItemFontColor);
		mSingleLineText.setGravity(Gravity.CENTER);
		
		if (BOLDSTYLE.equals(mItemStyle)) {
			mSingleLineText.getPaint().setFakeBoldText(true);
		}
		this.addView(mSingleLineText);
		
		mInnerEditText = new InnerEditText(getContext());
		mInnerEditText.setMinimumWidth(MIN_EDIT_WIDTH);
		mInnerEditText.setSingleLine(true);
		mInnerEditText.setGravity(Gravity.CENTER_VERTICAL);
		mInnerEditText.getPaint().setTextSize(mItemFontSize);
		mInnerEditText.setBackgroundResource(R.drawable.bg_new_message_to_bg);
        mInnerEditText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//when user click the edit control,set items to normal.
				setToNormalStateTextItemView();

				if (mInnerEditText.getText().toString().length() > 0) {
					// if this situation , maybe user want to continue to input and all items who are needed to hide should be hide.
					hideAllItems();
				}
				
				mInnerEditText.setCursorVisible(true);
				mInnerEditText.requestFocus();

				if (mOnContactEditClickListener != null) {
					mOnContactEditClickListener.onContactEditClick();
				}

				
			}
		});
		
		mInnerEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			
			}

			@Override
			public void afterTextChanged(Editable s) {
				onAfterTextChanged(s);
				if (mOnAfterToTextChangedListener != null) {
					mOnAfterToTextChangedListener.onAfterToTextChanged(s);
				}

			}
		});
		
		mInnerEditText.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE 
						|| actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_NULL) {
					mOnClickNextListener.onClickNext();
					return true;
				}
				return false;
			}
		});

		mInnerEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {

				if (mOuterFocusListener != null) {
					mOuterFocusListener.onFocusChange(v, hasFocus);
				}

				if (!hasFocus) {
					setSingleLineTextVisiable();
				}

			}
		});
		
		this.addView(mInnerEditText);
		
		mAddButton = new ImageView(getContext());


		mAddButton.setScaleType(ScaleType.FIT_CENTER);
		mAddButton.setBackgroundResource(R.drawable.transparent);
		mAddButton.setImageResource(R.drawable.add_btn_selector);
		mAddButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mAddButtonOnClickListener != null) {
					mAddButtonOnClickListener.onClick(v);
				}
			}
		});
		
		this.addView(mAddButton);
	}

    /**
	 * interface for TA
	 * @return
	 */
	public TextView getSingleTextView(){
		return mSingleLineText;
	}
	
	public void hideAllItems() {
		if (mLines == 1) {
			return;
		}
		
		if (!mIsAllItemsHide) {
			
			if (mOnBeforeHideAllItemsListener != null) {
				mOnBeforeHideAllItemsListener.onBeforeHideAllItem();
			}

			/**
			 * if mLines > 1 then set mTitleText view gone.
			 */
			boolean isOneLine = (mLines == 1)?true:false;
			
			for (int i = 0; i < mTotalTexts.size(); i++) {
				InnerTextView v = mTotalTexts.get(i);
				if (v.getLineNumber() != mInnerEditText.getLineNumber()) {
					v.setVisibility(View.GONE);
				} else {
					v.setVisibility(View.VISIBLE);
				}
			}
			if (isOneLine == false) {
				mTitleText.setVisibility(View.GONE);
			} else {
				mTitleText.setVisibility(View.VISIBLE);
			}
			
			this.measure(mWidthMeasureSpec, mHeightMeasureSpec);
			this.invalidate();
		}
		mIsAllItemsHide = true;
	}
	
	
	/**
	 * Show all text items but the ones the same line number with edit.
	 */
	public void showAllItems() {
		if (mIsAllItemsHide) {

			for (int i = 0; i < mTotalTexts.size(); i++) {
				mTotalTexts.get(i).setVisibility(View.VISIBLE);
			}
			mTitleText.setVisibility(View.VISIBLE);
			mInnerEditText.setVisibility(View.VISIBLE);
			this.measure(mWidthMeasureSpec, mHeightMeasureSpec);

			if (mOnAfterShowAllItemsListener != null) {
				mOnAfterShowAllItemsListener.onAfterShowAllItems();
			}
		}
		mIsAllItemsHide = false;
		mInnerEditText.requestFocus();
	}
	
	/**
	 * Create a new text item
	 */
	private InnerTextView newAItem() {
		InnerTextView newTextView = null;
		try {
			newTextView = new InnerTextView(getContext());
			newTextView.setMinWidth(MIN_TEXT_WIDTH);
			newTextView.setSingleLine(true);
			newTextView.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
			newTextView.setTextSize(mItemFontSize);
			newTextView.getPaint().setTextSize(mItemFontSize);
			newTextView.setTextColor(mItemFontColor);
			newTextView.setGravity(Gravity.CENTER);
			newTextView.setBackgroundResource(R.drawable.bg_new_message_to_bg);
			
			if (BOLDSTYLE.equals(mItemStyle)) {
				newTextView.getPaint().setFakeBoldText(true);
			}
			newTextView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (!mInnerEditText.hasFocus()) {
						mInnerEditText.requestFocus();
					}
					/*
					InnerTextView textItem = (InnerTextView)v;
					if(textItem != null && textItem.isBubbleSelected() 
							&& DeviceUtils.isAboveAndroidOS4_4()){
						onTextItemDelete();
					}else{
						onTextItemViewSelect(v);
					} 
					*/
					
					onTextItemViewSelect(v);
					showKeyboard();
				}
			});
		} catch(Exception err) {
			newTextView = null;
		}
		
		return newTextView;
	}

	/**
	 * for test
	 * @param text
	 */
	public void addText(String text) {
		if (text == null || text.trim().length() == 0) {
			return;
		}
		
		InnerTextView newTextView = newAItem();
		if (newTextView != null) {
			addView(newTextView);
			mTotalTexts.add(newTextView);
			this.measure(mWidthMeasureSpec, mHeightMeasureSpec);
		}
	}
	
	/**
	 * Get the selected item view
	 * @return InnerTextView
	 */
	private InnerTextView getSelectItem(){
		InnerTextView result = null;
		if (mCurrentSelect != -1) {
			if (mCurrentSelect < mTotalTexts.size() && mCurrentSelect >= 0) {
				result = mTotalTexts.get(mCurrentSelect);
			} else {
				mCurrentSelect = -1;
			}
		}

		return result;
	}
	
	private boolean cancelSelectedItem(){
		InnerTextView result = getSelectItem();
		mCurrentSelect = -1;
		if(result != null){
			result.setBackground(false);
			return true;
		}
		return false;
	}
	
	public void cancelSelected(){
		setToNormalStateTextItemView();
	}
	
	/**
	 * Handle when the text of edit-control changed, it's cursor should be visible
	 * @param s
	 */
	private void onAfterTextChanged(Editable s){
		//make sure the cursor is visible
		mInnerEditText.setCursorVisible(true);
		//when user input str make sure there is no select item
		InnerTextView selectItem = getSelectItem();
		if (selectItem != null) {
			//selectItem.setBackground(false);
			removeItem(selectItem);
			mCurrentSelect = -1;
			setAddButtonVisible();
		}

		// if the recipients is max then show alert dialog.
		if (!TextUtils.isEmpty(s) && isFull()) {
			showTooManyRecipientsDialog(getContext());
			mInnerEditText.setText("");
			return;
		}

		if (mOuterTextWatcher != null) {
			mOuterTextWatcher.afterTextChanged(mInnerEditText.getText());
		}

		if (s == null || s.length() == 0) {
			if (mInnerEditText.hasFocus()) {
				showAllItems();
			} else {
				mIsAllItemsHide = false;
			}
			return;
		}

		hideAllItems();
		
	}
	
	/**
	 * make sure every item is unselected.
	 */
	private void setToNormalStateTextItemView(){
		
		mCurrentSelect = -1;
		for (int i = 0; i < mTotalTexts.size(); i++) {
			InnerTextView contactView = mTotalTexts.get(i);
			if (contactView != null) {
				contactView.setBackground(false);
			}
		}
	}
	
	/**
	 * @warning will hide the edit cursor.
	 * @param v
	 */
	private void onTextItemViewSelect(View v){
		mCurrentSelect = -1;
		if (v == null) {
			return;
		}
		
		InnerTextView contactView = null;
		for (int i = 0; i < mTotalTexts.size(); i++) {
			contactView = mTotalTexts.get(i);
			if (v.equals(contactView)) {
				mCurrentSelect = i;
				contactView.setBackground(true);
			} else {
				contactView.setBackground(false);
			}
		}
		
		//Then set the editor cursor is invisible.
		mInnerEditText.setCursorVisible(false);
		

	}
	
	public InnerTextView getTextItem(int index){
		if (index < 0 || index > mTotalTexts.size() - 1) {
			return null;
		}
		return mTotalTexts.get(index);
	}
	
	/**
	 * @warning will show the edit cursor.
	 */
	private void onTextItemDelete() {
	
		if (mCurrentSelect != -1) {
			if (mCurrentSelect >= 0 && mCurrentSelect <= mTotalTexts.size() - 1) {
				InnerTextView v = mTotalTexts.get(mCurrentSelect);
				removeItem(v);
				measure(mWidthMeasureSpec, mHeightMeasureSpec);
				setAddButtonVisible();
				
				if(mOnDeleteContactListener != null){
					mOnDeleteContactListener.onDeleteContact();
				}
			}
		}
		mCurrentSelect = -1;
		mInnerEditText.setCursorVisible(true);
	}
	
	private void removeItem(InnerTextView v){
		mTotalTexts.remove(v);
		mTotalContact.remove(v.getContactData());
		ExpandableEditText.this.removeView(v);
	}
	

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		
		
		if (event.getAction() != KeyEvent.ACTION_DOWN) {
			return super.dispatchKeyEvent(event);
		}
		
		if (event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
			
		} else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
			if (mTotalContact.size() > 0) {
				int index = mTotalContact.size() - 1;
				if (mCurrentSelect != - 1) {
					index = mCurrentSelect - 1;
					if (index < 0) {
						setToNormalStateTextItemView();
						mInnerEditText.setCursorVisible(true);
						return true;
					}
				} 
				onTextItemViewSelect(getTextItem(index));
				return true;
			}
		} else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
			if (mTotalContact.size() > 0) {
				int index = 0;
				if (mCurrentSelect >= 0) {
					index = mCurrentSelect + 1;
					if (index >= mTotalContact.size()) {
						setToNormalStateTextItemView();
						mInnerEditText.setCursorVisible(true);
						return true;
					}
				}
				onTextItemViewSelect(getTextItem(index));
				return true;
			}
		}
		
		return super.dispatchKeyEvent(event);
	}
	
	@Override
	public boolean dispatchKeyEventPreIme(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			if (mOnDispatchKeyBackListener != null) {
				mOnDispatchKeyBackListener.onDispatchKeyBack();
			}
			return true;
		}
		return super.dispatchKeyEventPreIme(event);
	}

	public String getText() {
		return mInnerEditText.getText().toString();

	}
	
	public boolean isEditTextVerf(){
		return !mInnerEditText.getText().toString().equals(_empty);
	}
	
	public void setFocusOn(boolean needVK) {
		this.mInnerEditText.requestFocus();
		this.setSingleLineTextGone();

		if (needVK) {
			showKeyboard();
		} else {
			mKeyboardShowing = true;
		}
	}

	public void clearContactEditTextFocus() {
		setSingleLineTextVisiable();
	}

	public void hideOrShowEditTextCursor(boolean isShow) {
		if (isShow) {
			mInnerEditText.requestFocus();
		}
		mInnerEditText.setCursorVisible(isShow);
	}
	
	private void showTooManyRecipientsDialog(Context context) {
//		if (mDialog == null) {
//			String title = context.getResources().getString(R.string.too_many_recipients_title);
//			String errorMessage = context.getResources().getString(R.string.too_many_recipients_content, MAX_RECIPIENT_COUNT);
//
//			mDialog = RcAlertDialog.getBuilder(context)
//						 .setTitle(title)
//						 .setMessage(errorMessage)
//						 .setIcon(R.drawable.symbol_exclamation)
//						 .setPositiveButton(R.string.dialog_btn_ok, new DialogInterface.OnClickListener() {
//							 public void onClick(DialogInterface dialog, int which) {
//								 dialog.dismiss();
//							 }
//						 }).create();
//		}
//		if (!mDialog.isShowing()) {
//			mDialog.show();
//		}
	}
	
	///////////////////////////////////////////////////////////////adpater RecipientsEditor/////////////////
	private void addContact(ExpandableEditTextContact contact) {
		if (contact == null) {
			return;
		}

		InnerTextView newTextView = newAItem();
		if (newTextView == null) {
			return;
		}
		newTextView.setContactData(contact);
		newTextView.setBackground(false);
		addView(newTextView);
		mTotalTexts.add(newTextView);
		mTotalContact.add(contact);
		
		if (mIsShowTextsSingleLine) {
			if (!mInnerEditText.hasFocus()) {
				newTextView.setVisibility(View.GONE);
				updateSingleLineText();
			} else {
				mIsShowTextsSingleLine = false;
			}
		}
		
		this.measure(mWidthMeasureSpec, mHeightMeasureSpec);
	}
	
	public void setContacts(ArrayList<ExpandableEditTextContact> list){
		if (list == null) {
			return;
		}
//		int size = list.size();
		for (ExpandableEditTextContact contact : list) {
//			Contact contact = list.get(i);
			InnerTextView newTextView = newAItem();
			if (newTextView == null || contact == null) {
				continue;
			}
			newTextView.setContactData(contact);
			newTextView.setBackground(false);
			mTotalTexts.add(newTextView);
			mTotalContact.add(contact);
			addView(newTextView);
		}
		
		if (isFull()) {
			setAddButtonVisible();
		}
		
		this.measure(mWidthMeasureSpec, mHeightMeasureSpec);
	}
	
	public void setContact(ExpandableEditTextContact contact) {
		
		if (contact == null || TextUtils.isEmpty(contact.name) && TextUtils.isEmpty(contact.number)) {
			return;
		}
		
		boolean isExistContact = isExist(contact);
		
		if (!isExistContact && !isFull()) {
			addContact(contact);
			setAddButtonVisible();
		} else if (isExistContact) {
			cancelSelectedItem();
		}
		
		mInnerEditText.setText("");

	}
	
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// to handle when view is not available, but its functions has been called.
		if (w != oldw && mIsFirstLoad == false) {
			mIsFirstLoad = false;
			if (mIsShowTextsSingleLine) {
				updateSingleLineTextVisiable();
			}
		}
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	private void setAddButtonVisible(){
		if (isFull() && this.mAddButton.getVisibility() != View.GONE) {
			this.mAddButton.setVisibility(View.GONE);
		} else {
			if (isEditFocus() || !mIsShowTextsSingleLine) {
				this.mAddButton.setVisibility(View.VISIBLE);
			} else {
				this.mAddButton.setVisibility(View.GONE);
			}
		}
	}

	public View getInnerEditText(){
		return this.mInnerEditText;
	}
	
	public ArrayList<ExpandableEditTextContact> getContact(){
		return mTotalContact;
	}
	
	public void setAddButtonClickListener(OnClickListener clickListener) {
		mAddButtonOnClickListener = clickListener;
	}
	
	public void setTextChangedListener(TextWatcher textWatcher) {
//		mInnerEditText.addTextChangedListener(textWatcher);
		mOuterTextWatcher = textWatcher;
	}

	public void setOnDeleteContactListener(OnDeleteContactListener deleteContactListener) {
//		mInnerEditText.addTextChangedListener(textWatcher);
		mOnDeleteContactListener = deleteContactListener;
	}
	
	public void setFocusChangedListener(OnFocusChangeListener focusListener) {
		mOuterFocusListener = focusListener;
	}
	
	public void setOnClickNextListener(OnClickNextListener listener){
		mOnClickNextListener = listener;
	}
	
	public void setOnDispatchKeyBackListener(OnDispatchKeyBackListener listener){
		mOnDispatchKeyBackListener = listener;
	}

	public void setOnBeforeHideAllItemsListener(OnBeforeHideAllItemsListener listener){
		mOnBeforeHideAllItemsListener = listener;
	}
	
	public void setOnAfterShowAllItemsListener(OnAfterShowAllItemsListener listener){
		mOnAfterShowAllItemsListener = listener;
	}

	public void setOnContactEditClickListener(OnContactEditClickListener listener){
		mOnContactEditClickListener = listener;
	}
	
	public void setOnAfterToTextChangedListener(OnAfterToTextChangedListener listener){
		mOnAfterToTextChangedListener = listener;
	}
	
	public void setOnHeightChangedListener(OnHeightChangedListener listener){
		mOnHeightChangedListener = listener;
	}
	
	public void setEditTextImeOptionsNext(){
		mInnerEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
	}
	
	public void setEditTextImeOptionsDone(){
		mInnerEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
	}
	
	public void setText(CharSequence text) {
		mInnerEditText.setText(text);
	}
	
	public void setEditTextCursorLastPosition(){
		String text = mInnerEditText.getText().toString();
		if (!TextUtils.isEmpty(text)) {
			int position = text.length();
			mInnerEditText.requestFocus();
			mInnerEditText.setCursorVisible(true);
			mInnerEditText.setSelection(position);
		}
	}
	
	public void updateSingleLineText(){
		if (mOnBeforeHideAllItemsListener != null) {
			mOnBeforeHideAllItemsListener.onBeforeHideAllItem();
		}
		
		for (int i = 0; i < this.mTotalTexts.size(); i++) {
			mTotalTexts.get(i).setVisibility(View.GONE);
		}
		
		int lineWidth = this.getWidth() - mTitleText.getWidth() - this.getPaddingLeft() - this.getPaddingRight() - PADDING_LEFT;
		
		if (lineWidth <= 0) {
			return;
		}

		ArrayList<String> names = new ArrayList<String>();
		for (ExpandableEditTextContact contact : mTotalContact) {
			names.add(contact.name);
		}

		String displayText = adjustText(lineWidth, mSingleLineText.getPaint(), names);
		mSingleLineText.setText(displayText);
	}
	
	private String adjustText(int fullwidth, Paint paint, ArrayList<String> names){
		if (fullwidth <= 0) {
			return "";
		}
		
		ArrayList<Integer> sizeList = new ArrayList<Integer>();
		StringBuilder sb = new StringBuilder();
		
		int curLength = 0;
		String displayName = "";
		boolean canShowAll = true;
		for (int i = 0; i < names.size(); i++) {
			displayName = names.get(i);
			if (i > 0) {
				displayName = ", " + displayName;
			}
			sb.append(displayName);
			
			int contactWidth = (int)paint.measureText(displayName);
			sizeList.add(contactWidth);
			
			curLength += contactWidth;
			if (curLength > fullwidth) {
				canShowAll = false;
				break;
			}
		}
		
		if (canShowAll) {
			return sb.toString();
		}

		float morePartWidth = paint.measureText(STRING_AND_MORE_SAMPLE);
		float ellipsisWidth = paint.measureText(STRING_ELLIPSIS);
		
		curLength = 0;
		sb.delete(0, sb.length());

		for (int i = 0; i < sizeList.size(); i++) {
			displayName = names.get(i);
			curLength += sizeList.get(i);

			if (curLength + morePartWidth <= fullwidth) {
				if (i == 0) {
					sb.append(displayName);
				} else {
					sb.append(", " + displayName);
				}
				continue;
			}

			int remainingCount = names.size() - i;

			if (i == 0) {
				if (names.size() == 1) {
					String subName = getDisplaySubString(paint, names.get(i), fullwidth - ellipsisWidth);
					sb.append(subName).append(STRING_ELLIPSIS);
				} else if (names.size() > 1) {
					String subName = getDisplaySubString(paint, names.get(i), fullwidth - morePartWidth);
					sb.append(subName).append(String.format(STRING_ELLIPSIS_AND_MORE, --remainingCount));
				}
			} else {
				sb.append(String.format(STRING_AND_MORE_ELLIPSIS, remainingCount));
			}
			break;
		}
		
		return sb.toString();
	}

	private String getDisplaySubString(Paint paint, String strName, float remainingWidth){
		String subString = "";
		
		for (int charIndex = 1; charIndex < strName.length(); charIndex++) {
			subString = strName.substring(0, charIndex);
			float width = paint.measureText(subString);

			if (width >= remainingWidth) {
				subString = strName.substring(0, charIndex - 1);
				break;
			}
		}
		
		return subString;
	}
	
	public void setSingleLineTextVisiable() {
		if (mIsShowTextsSingleLine == false) {
			this.mIsShowTextsSingleLine = true;
			try {
				updateSingleLineTextVisiable();
			} catch (Exception e) {
			}
		}
	}
	
	private void updateSingleLineTextVisiable() {
		updateSingleLineText();
		mInnerEditText.setVisibility(View.GONE);
		mAddButton.setVisibility(View.GONE);
		mTitleText.setVisibility(View.VISIBLE);
		
		this.measure(mWidthMeasureSpec, mHeightMeasureSpec);
		this.invalidate();
		mTitleText.setVisibility(View.VISIBLE);
		mSingleLineText.setVisibility(View.VISIBLE);
	}
	
	public int getOneLineHeight() {
		return this.mOneLineHeight;
	}
	
	public int getItemPaddingHeight(){
		return PADDING;
	}
	
	
	public void setSingleLineTextGone() {
		if (mIsShowTextsSingleLine) {
			this.mIsShowTextsSingleLine = false;
			
			for (int i = 0; i < this.mTotalTexts.size(); i++) {
				mTotalTexts.get(i).setBackground(false);
				mTotalTexts.get(i).setVisibility(View.VISIBLE);
			}
			mInnerEditText.setVisibility(View.VISIBLE);
			mTitleText.setVisibility(View.VISIBLE);
			if (isFull()) {
				this.mAddButton.setVisibility(View.GONE);
			} else {
				this.mAddButton.setVisibility(View.VISIBLE);
			}
			
			this.measure(mWidthMeasureSpec, mHeightMeasureSpec);
			this.invalidate();
			
			mSingleLineText.setVisibility(View.GONE);
		}
		
		mInnerEditText.requestFocus();
		mInnerEditText.setCursorVisible(true);
	}
	
	public void setKeyboardVisible(boolean isShowing) {
		mKeyboardShowing = isShowing;
	}
	
	public boolean requestEditFocus(){
		if (mInnerEditText != null) {
			return mInnerEditText.requestFocus();
		}
		
		return false;
	}

	public boolean isEditFocus(){
		if (mInnerEditText != null && mInnerEditText.hasFocus()) {
			return true;
		}
		return false;
	}
	
	public boolean isEmpty() {
		return mTotalContact.size() == 0 && TextUtils.isEmpty(mInnerEditText.getText());
	}

	private boolean isFull(){
		if (mTotalContact.size() >= MAX_RECIPIENT_COUNT) {
			return true;
		}
		return false;
	}
	
	private boolean isExist(ExpandableEditTextContact newContact){
		ExpandableEditTextContact contact = null;
		for (int i = 0; i < mTotalContact.size(); i++) {
			contact = mTotalContact.get(i);
			if (TextUtils.equals(contact.name, newContact.name) 
					&& TextUtils.equals(contact.number, newContact.number)) {
				return true;
			}
		}
		return false;
	}

	private void showKeyboard() {
		if (mKeyboardShowing == false) {
			InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm != null) {
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				mKeyboardShowing = true;
			}
		}
	}
	
	public interface OnClickNextListener{
		public void onClickNext();
	}

	public interface OnContactEditClickListener{
		void onContactEditClick();
	}
	
	public interface OnDispatchKeyBackListener{
		void onDispatchKeyBack();
	}
	
	public interface OnBeforeHideAllItemsListener{
		public void onBeforeHideAllItem();
	}
	
	public interface OnAfterShowAllItemsListener{
		public void onAfterShowAllItems();
	}

	public interface OnDeleteContactListener{
		public void onDeleteContact();
	}
	
	public interface OnAfterToTextChangedListener{
		public void onAfterToTextChanged(Editable s);
	}

	public interface OnHeightChangedListener{
		public void onHeightChanged(int height, int lineHeight,  int linePadding, int verticalPadding);
	}
	
	/**
	 * @author jerry.cai
	 *
	 */
	public class InnerEditText extends EditText {
		int mLineNumber = 1;
		boolean mDeleteStatus= false;
		public InnerEditText(Context context) {
	        super(context);
		}
		
		public InnerEditText(Context context, AttributeSet attrs) {
	        super(context, attrs);
		}
		
		public int getLineNumber(){
			return mLineNumber;
		}
		
		public void setLineNumber(int line){
			mLineNumber = line;
		}
		
		@Override
		public void setText(CharSequence text, BufferType type) {
			// TODO Auto-generated method stub
			super.setText(text, type);
			mDeleteStatus = false;
		}
		
		public void setDeleteStatus(boolean value){
			mDeleteStatus = value;
		}
		
		public boolean getDeleteStatus(){
			return mDeleteStatus;
		}
	}
	
	/**
	 * @author jerry.cai
	 *
	 */
	public class InnerTextView extends TextView {
		ExpandableEditTextContact mContactData;
		boolean mIsLineFirstItem = false;
		int mLineNumber = 1;
		//boolean mIsSelect = false;
		
		public InnerTextView(Context context) {
	        super(context);
	        
		}
		
		public InnerTextView(Context context, AttributeSet attrs) {
	        super(context, attrs);
		}
		
		public void setContactData(ExpandableEditTextContact contact){
			mContactData = contact;
			
			String displayName = "";
			if (!TextUtils.isEmpty(contact.name)) {
				displayName = contact.name;
			} else {
				displayName = contact.number;
			}
			setText(displayName);
			
		}
		
		public void setBackground(boolean isSelected){
			if (isSelected) {
				if (mContactData.isValid) {
					setBackgroundResource(R.drawable.bg_chooes_pressed);
				} else {
					setBackgroundResource(R.drawable.bg_contact_validation_selected);
				}
                setTextColor(getResources().getColor(R.color.palette_white));
			} else {
				if (mContactData.isValid) {
					setBackgroundResource(R.drawable.bg_chooes_nor);
				} else {
					setBackgroundResource(R.drawable.bg_contact_validation_nor);
				}
                setTextColor(getResources().getColor(R.color.black));
			}
			//mIsSelect = isSelected;
		}
		
		public ExpandableEditTextContact getContactData(){
			return mContactData;
		}
		
		public boolean isLineFirstItem(){
			return mIsLineFirstItem;
		}
		
		public void setLineFirstItem(boolean value){
			mIsLineFirstItem = value;
		}
		
		public int getLineNumber(){
			return mLineNumber;
		}
		
		public void setLineNumber(int line){
			mLineNumber = line;
		}
		
		//public boolean isBubbleSelected(){
		//	return mIsSelect;
		//}
		
	}
	
	@Override
	public void onClick(View v) {
		setSingleLineTextGone();
		if (false == mIsShowTextsSingleLine) {
			showKeyboard();
		}
		setToNormalStateTextItemView();
		mCurrentSelect = -1;
	
	}
}

