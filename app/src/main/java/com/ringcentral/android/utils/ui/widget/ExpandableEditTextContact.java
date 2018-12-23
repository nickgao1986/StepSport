package com.ringcentral.android.utils.ui.widget;

import android.content.Context;


public class ExpandableEditTextContact {
	
	public String name;
	public String number;
	public long phone_id;
	public long contact_id;
	public String lookup_key;
	public boolean isValid;
	public boolean isPersonal;
	public int phoneType;
	
    /**
     * Only for Flurry in sending text message
     */
	public int number_type;		// 1, department; 2, personal; 3, Long Number

	public static final int UNKNOW 				= 0;
	public static final int DEPARTMENT_NUMBER 	= 1;	//department pager
	public static final int PERSONAL_NUMBER 	= 2;	//personal pager
	public static final int LONG_NUMBER			= 3;	//sms

	public ExpandableEditTextContact(){
	}
	
	public ExpandableEditTextContact(Context context, String name, String number, boolean isValid) {
		this.name 		= name;
		this.number  	= number;
		this.isValid  	= isValid;
		
		if (isValid) {
			initNumberType(context);
		}
	}
	
	private void initNumberType(Context context) {
//		String number = this.number;
//		PhoneNumber phoneNumber = PhoneUtils.getParser().parse(number);
//		
//		if (phoneNumber == null) {
//			this.number_type = UNKNOW;
//			return;
//		}
//
//		if (phoneNumber.isShortNumber) {
//			if (context != null && RCMProviderHelper.isDeptExtension(context, phoneNumber.number)) {
//				this.number_type = DEPARTMENT_NUMBER;
//			} else {
//				this.number_type = PERSONAL_NUMBER;
//			}
//		} else {
//			this.number_type = LONG_NUMBER;
//		}
	}
	
	public void setContactIDs(long phone_id, long contact_id, String lookup_key) {
		this.phone_id 	= phone_id;
		this.contact_id = contact_id;
		this.lookup_key = lookup_key;
		
		if (phone_id > 0 && contact_id > 0) {
			isPersonal = true;
		}
	}
}
