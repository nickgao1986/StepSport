package com.pic.optimize.expandabletext;

import java.util.ArrayList;
import java.util.List;

import com.pic.optimize.R;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.text.TextUtils;


public class DataBaseUtil {

	/**
	 * Define logging tag.
	 */
	private static final String LOG_TAG = "[RC] DataBaseUtil";

	public static Bitmap getContactPhoto(Context context, long photoId,
			BitmapFactory.Options options) {
		if (photoId < 0) {
			return null;
		}

		Cursor cursor = null;
		try {
			cursor = context.getContentResolver().query(
					ContentUris.withAppendedId(
							ContactsContract.Data.CONTENT_URI, photoId),
					new String[] { Photo.PHOTO }, null, null, null);

			if (cursor != null && cursor.moveToFirst() && !cursor.isNull(0)) {
				byte[] photoData = cursor.getBlob(0);
				// Workaround for Android Issue 8488
				// http://code.google.com/p/android/issues/detail?id=8488
				if (options == null) {
					options = new BitmapFactory.Options();
				}
				options.inTempStorage = new byte[16 * 1024];
				options.inSampleSize = 2;
				return BitmapFactory.decodeByteArray(photoData, 0,
						photoData.length, options);
			}
		} catch (Throwable error) {
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
	}

	private static final String[] PROJECTION7 = {
			ContactsContract.CommonDataKinds.Phone.TYPE,
			ContactsContract.CommonDataKinds.Phone.NUMBER,
			ContactsContract.CommonDataKinds.Phone.IS_SUPER_PRIMARY,
			ContactsContract.CommonDataKinds.Phone.LABEL,
			ContactsContract.CommonDataKinds.Phone._ID,
			ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
			ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY };

	private static final int TYPE_INDX7 = 0;
	private static final int NUMBER_INDX7 = 1;
	private static final int IS_PRIMARY_INDX7 = 2;
	private static final int LABEL_INDX7 = 3;
	private static final int ID_INDX7 = 4;
	private static final int CONTACT_ID_INDX7 = 5;
	private static final int LOOKUP_KEY_INDX7 = 6;

	public static List<TaggedContactPhoneNumber> getPersonalContactPhoneNumbers(
			Context context, long contactId, boolean onlyFullValidNumbers) {
		ArrayList<TaggedContactPhoneNumber> list = new ArrayList<TaggedContactPhoneNumber>();
		if (contactId < 0) {
			return list;
		}

		Cursor cursor = null;

		try {
			String[] whereArgs = new String[] { String.valueOf(contactId) };
			cursor = context.getContentResolver().query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
					PROJECTION7,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
					whereArgs, null);

			if (cursor != null) {
				cursor.moveToPosition(-1);
				while (cursor.moveToNext()) {
					try {
						String number = cursor.getString(NUMBER_INDX7);
						if (TextUtils.isEmpty(number)) {
							continue;
						}
						TaggedContactPhoneNumber num = new TaggedContactPhoneNumber();

						int type = cursor.getInt(TYPE_INDX7);
						num.numberTag = getPhoneNumberTag(context, type);
						num.phoneNumber = number;
						if (cursor.getInt(IS_PRIMARY_INDX7) != 0) {
							num.isDefault = true;
						}
						num.id = cursor.getLong(ID_INDX7);
						num.contact_id = cursor.getLong(CONTACT_ID_INDX7);
						num.lookup_key = cursor.getString(LOOKUP_KEY_INDX7);
						num.phone_id = num.id;

						list.add(num);
					} catch (Throwable error) {
					}
				}
			}
		} catch (Throwable ex) {
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return list;
	}

	public static String getPhoneNumberTag(Context context, long tag) {

		return "号码";
	}
}
