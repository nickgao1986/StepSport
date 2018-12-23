package com.pic.optimize.expandabletext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.pic.optimize.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.SimpleAdapter;

public class PhoneSelectionDialog implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener{

	public interface OnItemClickListener {
		public void onPhoneNumberSelected(Activity activity, TaggedContactPhoneNumber phoneNumber);
	}

	private static final String TAG = "[RC]PhoneNumberSelectDialog";
	
	private static final String DISPLAY_NUMBER = "DISPLAY_NUMBER";
	private static final String TYPE = "TYPE";
	
	private Activity mActivity;
	private AlertDialog mDialog;
	private SimpleAdapter mPhonesAdapter;
	private List<HashMap<String, String>> mFillMaps;
	private List<TaggedContactPhoneNumber> mList;
	private OnItemClickListener mItemClickListener;
	
	public PhoneSelectionDialog(Activity activity, List<TaggedContactPhoneNumber> list, OnItemClickListener listener) {
		if (isIllegalArgument(activity, list, listener)) {
			throw new IllegalArgumentException(TAG);
		}
		
		if (list.size() == 1) {
			listener.onPhoneNumberSelected(activity, list.get(0));
			return;
		}
		
		mActivity = activity;
		mList = list;
		mItemClickListener = listener;
		
		String[] from = new String[] { DISPLAY_NUMBER, TYPE };
		int[] to = new int[] { android.R.id.text1, android.R.id.text2 };

		mFillMaps = new ArrayList<HashMap<String, String>>();
		for (TaggedContactPhoneNumber num : list) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(DISPLAY_NUMBER, num.phoneNumber);
			map.put(TYPE, 			num.numberTag);
			mFillMaps.add(map);
		}
		
		mPhonesAdapter = new SimpleAdapter(mActivity, mFillMaps, R.layout.simple_list_item_2, from, to);
		mDialog = RcAlertDialog.getBuilder(mActivity)
				.setAdapter(mPhonesAdapter, this)
				.setTitle("号码")
				.create();
	}
	
	@Override
	public void onDismiss(DialogInterface dialog) {
		mActivity = null;
		mFillMaps = null;
		mList = null;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (mItemClickListener == null) {
			return;
		}
		
		try {
			mItemClickListener.onPhoneNumberSelected(mActivity, mList.get(which));
		} catch (Exception ex) {
		}
	}

	public void show() {
		if (mFillMaps == null || mFillMaps.isEmpty()) {
			return;
		}
		mDialog.show();
	}

	private boolean isIllegalArgument(Activity activity, List<TaggedContactPhoneNumber> list, OnItemClickListener listener) {
		return list == null || list.isEmpty() || activity == null || listener == null;
	}

}
