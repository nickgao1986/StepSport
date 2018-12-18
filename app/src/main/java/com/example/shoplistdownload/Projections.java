package com.example.shoplistdownload;

import android.provider.ContactsContract;
import android.provider.Contacts.People;
import android.provider.ContactsContract.Data;


public final class Projections {
	
	 /**
	  * This class help us to build projection for ListActivity
	  * to display summary information about personal contacts.
	  * Help to parseData Cursor
	  */
	 public static final class Personal {
	        
	        private Personal() {};
	        
	        /**
	         * Contacts projection.
	         */
	        public static final String[] CONTACTS_SUMMARY_PROJECTION = new String[] {
	        	People._ID, 								// 0
	        	People.DISPLAY_NAME, 						// 1
	        	People.STARRED,								// 2
	        	ContactsUtils.People_Has_Phone_Number,		// 3
	        	ContactsUtils.People_PhotoID,				// 4
	        };
	    	
	        /**
	         * Contacts group projection.
	         */
	        public static final String[] CONTACTS_GROUP_SUMMARY_PROJECTION = new String[] {
	            	People._ID,
	            	People.DISPLAY_NAME, 						// 1
	            	People.STARRED,								// 2
	            	Data.CONTACT_ID,						    // 3
	            	ContactsUtils.People_PhotoID,				// 4
	            	Data.RAW_CONTACT_ID, 						// 5
	            };
	        
	        /**
	         * Person ID column index.
	         */
	        public static final int ID_COLUMN_INDEX = 0;
	        
	        /**
	         * Person name column index.
	         */
	        public static final int NAME_COLUMN_INDEX = 1;
	        
	        /**
	         * Person stared column index.
	         */
	        public static final int STARRED_COLUMN_INDEX = 2;

	        /**
	         * Person stared column index.
	         */
	        public static final int HAS_PHONE_COLUMN_INDEX = 3;
	        
	        /**
	         * Person photo data column index.
	         */
	        public static final int PHOTO_COLUMN_INDEX = 4;

	        /**
	         * Person phone number column index.
	         */
	        public static final int NUMBER_COLUMN_INDEX = 5;	 
	       
	 }
	 
	 public static final class PersonalFilter{

	        private PersonalFilter() {};
	        
	        public static final String[] CONTACTS_FILTER_PROJECTION = new String[] {
	        	People._ID,
	            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
	            ContactsContract.CommonDataKinds.Phone.STARRED,
	        	ContactsUtils.People_Has_Phone_Number,
	            ContactsContract.CommonDataKinds.Phone.PHOTO_ID,};

	    	/**
	         * Person ID column index.
	         */
	        public static final int ID_COLUMN_INDEX = 0;

	        /**
	         * Person name column index.
	         */
	        public static final int NAME_COLUMN_INDEX = 1;
	        
	        /**
	         * Person stared column index.
	         */
	        public static final int STARRED_COLUMN_INDEX = 2;

	        /**
	         * Person photo data column index.
	         */
	        public static final int HAS_PHONE_COLUMN_INDEX = 3;

	        /**
	         * Person photo data column index.
	         */
	        public static final int PHOTO_COLUMN_INDEX = 4;

	        /**
	         * Person phone number column index.
	         */
	        public static final int NUMBER_COLUMN_INDEX = 5;	 

	        /**
	         * Person phone number column index.
	         */
	        public static final int TYPE_COLUMN_INDEX = 6;	 
	 }
	 
	 public static final class PersonalPhones {
		 
		   private PersonalPhones() {};
		  
		   /** The projection to use when querying the phones table */
		    public static final String[] PHONES_PROJECTION = new String[] {
		        ContactsContract.CommonDataKinds.Phone._ID,
		        ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
		        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
		        ContactsContract.CommonDataKinds.Phone.STARRED,
		        ContactsContract.CommonDataKinds.Phone.PHOTO_ID,
		        ContactsContract.CommonDataKinds.Phone.NUMBER,
		        ContactsContract.CommonDataKinds.Phone.TYPE };

	        /**
	         * Person ID column index.
	         */
	        public static final int ID_COLUMN_INDEX = 0;
	        
	        /**
	         * Person ID column index.
	         */
	        public static final int CONTACT_ID_COLUMN_INDEX = 1;
	        
	        /**
	         * Person name column index.
	         */
	        public static final int NAME_COLUMN_INDEX = 2;
	        
	        /**
	         * Person stared column index.
	         */
	        public static final int STARRED_COLUMN_INDEX = 3;

	        /**
	         * Person photo data column index.
	         */
	        public static final int PHOTO_COLUMN_INDEX = 4;

	        /**
	         * Person phone number column index.
	         */
	        public static final int NUMBER_COLUMN_INDEX = 5;

	        /**
	         * Person phone number type column index.
	         */
	        public static final int TYPE_COLUMN_INDEX = 6;
	}
	 		 
	 
	 
	public static final class PersonalContacts {
		private PersonalContacts() {
		};

		/**
		 * Extension projection.
		 */
		static final String[] PERSONAL_CONTACTS_SUMMARY_PROJECTION = new String[] {
			ContactsContract.Contacts._ID,				// 0
			ContactsContract.Contacts.DISPLAY_NAME,		// 1
			ContactsContract.Contacts.STARRED,			// 2
			ContactsContract.Contacts.HAS_PHONE_NUMBER,	// 3
			ContactsContract.Contacts.PHOTO_ID,			// 4
			ContactsContract.Contacts.LOOKUP_KEY		// 5
		};

		static final int ID_COLUMN_INDEX 				= 0;
		static final int DISPLAY_NAME_COLUMN_INDEX 		= 1;
		static final int STARRED_COLUMN_INDEX 			= 2;
		static final int HAS_PHONE_NUMBER_COLUMN_INDEX 	= 3;
		static final int PHOTO_ID_COLUMN_INDEX 			= 4;
		static final int LOOKUP_KEY_COLUMN_INDEX 		= 5;
	}
	
	public static final class PersonalGroupContacts {
		private PersonalGroupContacts() {
		};

		/**
		 * Extension Group projection.
		 */
		static final String[] PERSONAL_CONTACTS_SUMMARY_PROJECTION = new String[] {
			ContactsContract.Contacts._ID,				// 0
			ContactsContract.Contacts.DISPLAY_NAME,		// 1
			ContactsContract.Contacts.STARRED,			// 2
			Data.CONTACT_ID,							// 3
			ContactsContract.Contacts.PHOTO_ID,			// 4
			ContactsContract.Contacts.LOOKUP_KEY,		// 5
			Data.RAW_CONTACT_ID							// 6
		};

		static final int ID_COLUMN_INDEX 				= 0;
		static final int DISPLAY_NAME_COLUMN_INDEX 		= 1;
		static final int STARRED_COLUMN_INDEX 			= 2;
		static final int HAS_PHONE_NUMBER_COLUMN_INDEX 	= 3;
		static final int PHOTO_ID_COLUMN_INDEX 			= 4;
		static final int LOOKUP_KEY_COLUMN_INDEX 		= 5;
		static final int RAW_CONTACT_ID_COLUMN_INDEX 	= 6;
	}
	
	public static final String[] MATRIX_COLUMN_NAMES = new String[] {
		ContactsContract.Contacts._ID,				// 0
		ContactsContract.Contacts.DISPLAY_NAME,		// 1
		ContactsContract.Contacts.STARRED,			// 2
		ContactsContract.Contacts.HAS_PHONE_NUMBER,	// 3
		ContactsContract.Contacts.PHOTO_ID,			// 4
		ContactsContract.Contacts.LOOKUP_KEY,		// 5
		ContactsContract.CommonDataKinds.Phone._ID	// 6
	};
	
	public static final String[] MATRIX_GROUP_COLUMN_NAMES = new String[] {
			Data.RAW_CONTACT_ID,				        // 0
			ContactsContract.Contacts.DISPLAY_NAME,		// 1
			ContactsContract.Contacts.STARRED,			// 2
			ContactsContract.Contacts.HAS_PHONE_NUMBER,	// 3
			ContactsContract.Contacts.PHOTO_ID,			// 4
			ContactsContract.Contacts.LOOKUP_KEY,		// 5
			ContactsContract.CommonDataKinds.Phone._ID	// 6
		};
	
	public static final String[] MATRIX_GROUP_CONTACT_COLUMN_NAMES = new String[] {
		Data.RAW_CONTACT_ID,				        // 0
		ContactsContract.Contacts.DISPLAY_NAME,		// 1
		ContactsContract.Contacts.STARRED,			// 2
		ContactsContract.Contacts.HAS_PHONE_NUMBER,	// 3
		ContactsContract.Contacts.PHOTO_ID,			// 4
		People._ID									// 6
		
	};
	
	public static final class PhoneContacts {
		private PhoneContacts() {
		};

		/**
		 * Extension projection.
		 */
		static final String[] PHONE_CONTACTS_SUMMARY_PROJECTION = new String[] {
			ContactsContract.CommonDataKinds.Phone.CONTACT_ID,			// 0
			ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,		// 1
			ContactsContract.CommonDataKinds.Phone.STARRED,				// 2
			ContactsContract.CommonDataKinds.Phone.PHOTO_ID,			// 3
			ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY,			// 4
			ContactsContract.CommonDataKinds.Phone._ID					// 5
		};

		static final int CONTACT_ID_COLUMN_INDEX 	= 0;
		static final int DISPLAY_NAME_COLUMN_INDEX 	= 1;
		static final int STARRED_COLUMN_INDEX 		= 2;
		static final int PHOTO_ID_COLUMN_INDEX 		= 3;
		static final int LOOKUP_KEY_COLUMN_INDEX 	= 4;
		static final int PHONE_ID_COLUMN_INDEX 		= 5;
	}
	
	public static final class PhoneGroupContacts {
		private PhoneGroupContacts() {
		};

		/**
		 * Extension projection.
		 */
		static final String[] PHONE_CONTACTS_SUMMARY_PROJECTION = new String[] {
			ContactsContract.CommonDataKinds.Phone.CONTACT_ID,			// 0
			ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,		// 1
			ContactsContract.CommonDataKinds.Phone.STARRED,				// 2
			ContactsContract.CommonDataKinds.Phone.PHOTO_ID,			// 3
			ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY,			// 4
			ContactsContract.CommonDataKinds.Phone._ID,					// 5
			ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID,										// 6
		};

		static final int CONTACT_ID_COLUMN_INDEX 	= 0;
		static final int DISPLAY_NAME_COLUMN_INDEX 	= 1;
		static final int STARRED_COLUMN_INDEX 		= 2;
		static final int PHOTO_ID_COLUMN_INDEX 		= 3;
		static final int LOOKUP_KEY_COLUMN_INDEX 	= 4;
		static final int PHONE_ID_COLUMN_INDEX 		= 5;
		static final int RAW_CONTACT_ID_COLUMN_INDEX = 6;
	}
	
	public static final class SelectContact {

		public static final int ID_COLUMN_INDEX                 = 0;
		public static final int DISPLAY_NAME_COLUMN_INDEX       = 1;
		public static final int PHONE_NUMBER_COLUMN_INDEX       = 2;
		public static final int PHONE_TYPE_COLUMN_INDEX         = 3;
		public static final int PHONE_TYPE_NUMBER_COLUMN_INDEX  = 4;
		public static final int PHONE_ID_COLUMN_INDEX           = 5;
		public static final int CONTACT_ID_COLUMN_INDEX         = 6;
		public static final int LOOKUP_KEY_COLUMN_INDEX         = 7;
	}
	
}
