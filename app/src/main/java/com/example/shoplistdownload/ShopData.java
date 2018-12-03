package com.example.shoplistdownload;


import android.os.Parcel;
import android.os.Parcelable;

public class ShopData implements Parcelable{

	public String shopId;
	public String name;
	public String url;
	public String info;

	public ShopData(Parcel in) {
		readFromParcel(in);
	}
	
	public ShopData() {
	}
	

	public static final Parcelable.Creator<ShopData> CREATOR = new Parcelable.Creator<ShopData>() {
		
		public ShopData createFromParcel( Parcel in ){
			return new ShopData(in);
		}
		
		public ShopData[] newArray( int size){
			return new ShopData[size];
		}
	};
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(shopId);
		dest.writeString(name);
		dest.writeString(url);
		dest.writeString(info);
		
	}
	public void readFromParcel( Parcel in ){
		shopId = in.readString();
		name = in.readString();
		url = in.readString();
		info = in.readString();
	}
	
}

