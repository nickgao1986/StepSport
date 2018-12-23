package com.pic.optimize.expandabletext;

import java.util.List;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import com.example.shoplistdownload.Projections;
import com.example.shoplistdownload.Projections.*;
import com.pic.optimize.R;

public class ContactSelectorActivity extends ContactsListActivity {

	private static final String TAG = "ContactSelector";
	private String mContactName = "";
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		try {
			Cursor cursor = (Cursor) getListAdapter().getItem(position);
			long peopleId = cursor.getLong(Projections.Personal.ID_COLUMN_INDEX);
			mContactName = cursor.getString(Personal.NAME_COLUMN_INDEX);
			int hasPhone = cursor.getInt(Personal.HAS_PHONE_COLUMN_INDEX);
			
			if (hasPhone != 0) {
				List<TaggedContactPhoneNumber> list 
					= DataBaseUtil.getPersonalContactPhoneNumbers(this, peopleId, false);
				choosePhoneNumber(cursor, list);
				return;
			} else {
				signalError(mToast, "号码不正确");
			}
		} catch (Throwable error) {
		}
	}
	
	/**
	 * Signal an error to the user.
	 */
	protected void signalError(Toast toast, String text) {
		toast.setText(text);
		toast.show();
	}
	
	protected void choosePhoneNumber(Cursor cursor, List<TaggedContactPhoneNumber> list) {
		if (list == null) {
			if (cursor == null) {
				return;
			}
			boolean hasPhone = cursor.getInt(Personal.HAS_PHONE_COLUMN_INDEX) != 0;
			if (!hasPhone) {
				signalError(mToast, "号码不正确");
				return;
			}
			list = DataBaseUtil.getPersonalContactPhoneNumbers(this, cursor.getLong(Personal.ID_COLUMN_INDEX), false);
		}
		
		if (list.isEmpty()) {
			signalError(mToast, "号码不正确");
		} else {
			onPersonalContactClicked(cursor, list);
			
		}
	}
	
	public void onPersonalContactClicked(Cursor cursor, List<TaggedContactPhoneNumber> list){
		if (list == null) {
			return;
		}

		if (list.size() == 1) {
			onPhoneSelectedAction(this, list.get(0));
		} else {
			PhoneSelectionDialog.OnItemClickListener listener = new PhoneSelectionDialog.OnItemClickListener() {
				public void onPhoneNumberSelected(Activity activity, TaggedContactPhoneNumber phoneNumber){
					onPhoneSelectedAction(ContactSelectorActivity.this, phoneNumber);
				}
			};
			PhoneSelectionDialog dialog = new PhoneSelectionDialog(this, list, listener);
			dialog.show();
		}
	}
	
	protected void onPhoneSelectedAction(Activity activity, TaggedContactPhoneNumber phoneNumber) {
		Intent resultIntent = new Intent();
		resultIntent.putExtra(RCMConstants.EXTRA_CONTACT_NAME, 			mContactName);
		resultIntent.putExtra(RCMConstants.EXTRA_CONTACT_NUMBER, 		phoneNumber.phoneNumber);
		resultIntent.putExtra(RCMConstants.EXTRA_CONTACT_PHONE_ID, 		phoneNumber.id);
		resultIntent.putExtra(RCMConstants.EXTRA_CONTACT_ID, 			phoneNumber.contact_id);
		resultIntent.putExtra(RCMConstants.EXTRA_CONTACT_LOOKUP_KEY, 	phoneNumber.lookup_key);

		this.setResult(ExpandableEditActivity.CONTACT_REQUEST_CODE, resultIntent);
		this.finish();
	}
}
