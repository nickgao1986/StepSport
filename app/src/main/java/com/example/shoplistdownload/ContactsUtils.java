package com.example.shoplistdownload;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Contacts.ContactMethods;
import android.provider.Contacts.People;
import android.provider.Contacts.Phones;
import android.provider.Contacts.Photos;
import android.provider.ContactsContract.CommonDataKinds.Photo;

public class ContactsUtils {

	private static String buildVersion = null;
	private static boolean isInitialized = false;
	
	/** String value. In SDK 3 it = People._ID;*/
	public static String People_ID					= People._ID;//"photo_data";
	/** String value. In SDK 3 it = People.DEFAULT_SORT_ORDER*/
	public static String People_SortOrder			= People.DEFAULT_SORT_ORDER;
	/** String value. In SDK 3 it = People.DISPLAY_NAME*/
	public static String People_DisplayName			= People.DISPLAY_NAME;
	/** String value. In SDK 3 it = People.PRIMARY_PHONE_ID*/
	public static String People_Has_Phone_Number	= People.PRIMARY_PHONE_ID;
	/** String value. In SDK 3 it = People._ID;*/
	public static String People_PhotoID				= People._ID;
	
	/** String value. In SDK 3 it = Phones._ID*/
	public static String Phone_ID					= Phones._ID;
	/** String value. In SDK 3 it = Phones.NUMBER*/
	public static String Phone_Number 				= Phones.NUMBER;
	/** String value. In SDK 3 it = Phones.TYPE*/
	public static String Phone_Type 				= Phones.TYPE;
	/** String value. In SDK 3 it = Phones.ISPRIMARY*/
	public static String Phone_IsPrimary 			= Phones.ISPRIMARY;
	/** String value. In SDK 3 it = Phones.PERSON_ID*/
	public static String Phone_PersonID 			= Phones.PERSON_ID;
	/** String value. In SDK 3 it = Photos.DATA*/
	public static String Photo_Photo				= Photos.DATA;
	
	/** String value. In SDK 3 it = ContactMethods._ID*/
	public static String Email_Person_ID			= ContactMethods.PERSON_ID;
	/** String value. In SDK 3 it = ContactMethods._ID*/
	public static String Email_Data					= ContactMethods.DATA;
	/** String value. In SDK 3 it = ContactMethods._ID*/
	public static String Email_Type					= ContactMethods.TYPE;
	/** String value. In SDK 3 it = Phones.PERSON_ID*/
	public static String PhoneLookup_ID				= Phones.PERSON_ID;
	/** String value. In SDK 3 it = Not supported in SDK3. people ID instead of photo id*/
	public static String PhoneLookup_Photo_ID		= Phones.PERSON_ID; 
	
	// --- URI --------------
	/** URI value. In SDK 3 it = People.CONTENT_URI*/
	public static Uri Uri_People 					= People.CONTENT_URI;
	/** URI value. In SDK 3 it = People.CONTENT_FILTER_URI*/
	public static Uri Uri_People_Filter				= People.CONTENT_FILTER_URI;
	/** URI value. In SDK 3 it = Phones.CONTENT_URI*/
	public static Uri Uri_Phone 					= Phones.CONTENT_URI;
	/** URI value. In SDK 3 it = Phones.CONTENT_URI*/
	public static Uri Uri_Phone_lookup 				= Phones.CONTENT_FILTER_URL;
	/** URI value. In SDK 3 it = Photos.CONTENT_URI*/
	public static Uri Uri_Photo 					= Photos.CONTENT_URI;	
	/** URI value. In SDK 3 it = ContactMethods.CONTENT_EMAIL_URI*/
	public static Uri Uri_Email 					= ContactMethods.CONTENT_EMAIL_URI;

	@SuppressWarnings("static-access")
	public static String getBuildVersion(){
		if (buildVersion == null){
			buildVersion = new Build.VERSION().SDK;
		}
		return buildVersion;
	}
	
	/**
     * Initialize all Contacts variables
     */
	static {
	    if (!isInitialized){
    	    	// People
    	    	People_DisplayName 		= "display_name"; 
    	    	People_SortOrder 		= People_DisplayName  + " COLLATE LOCALIZED ASC";
    	    	People_Has_Phone_Number	= "has_phone_number";
    	    	People_PhotoID			= "photo_id";

    	    	// Phones
    	    	Phone_Number 			= "data1"; 		
    	    	Phone_Type 				= "data2";
        		Phone_IsPrimary 		= "is_super_primary"; 											// DATA.IS_SUPER_PRIMARY = Phone.IS_SUPER_PRIMARY = "is_super_primary"
        		Phone_PersonID 			= "raw_contact_id";
    
     			//Email
     			Email_Person_ID			= "raw_contact_id";
     			Email_Data				= "data1";
     			Email_Type				= "data2";
     			
     			//PhoneLookup
     			PhoneLookup_ID			= "_id";
     			PhoneLookup_Photo_ID	= "photo_id";
     			
 			
        		// Uri
     			Uri_People 				= Uri.parse("content://com.android.contacts/contacts");	
     			Uri_People_Filter		= Uri.parse("content://com.android.contacts/contacts/filter");																						// ContactContracts.Data 	| Data.CONTENT_URI = content://com.android.contacts/data
     			Uri_Phone 				= Uri.parse("content://com.android.contacts/data/phones");		//							| Phone.CONTENT_URI =content://com.android.contacts/data/phones
     			Uri_Phone_lookup 		= Uri.parse("content://com.android.contacts/phone_lookup");	// ContactsContract.PhoneLookup.CONTENT_FILTER_URI = content://com.android.contacts/phone_lookup
    
     			Uri_Photo 				= Uri.parse("content://com.android.contacts/data");				//
     			Uri_Email 				= Uri.parse("content://com.android.contacts/data");				// 
            }
	        isInitialized = true;
	}

	
	public static Bitmap getContactPhoto(Context context, long photoId, BitmapFactory.Options options) {
        if (photoId < 0) {
            return null;
        }

        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(
                    ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, photoId),
                    new String[] { Photo.PHOTO }, null, null, null);

            if (cursor != null && cursor.moveToFirst() && !cursor.isNull(0)) {
                byte[] photoData = cursor.getBlob(0);
                // Workaround for Android Issue 8488 http://code.google.com/p/android/issues/detail?id=8488
                if (options == null) {
                    options = new BitmapFactory.Options();
                }
                options.inTempStorage = new byte[16 * 1024];
                options.inSampleSize = 2;
                return BitmapFactory.decodeByteArray(photoData, 0, photoData.length, options);
            }
        } catch (java.lang.Throwable error) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

}
