package com.pic.optimize.database;


import android.net.Uri;

public class UriHelper {

	static Uri removeQuery(Uri uri) {
		Uri newUri = uri.buildUpon().query("").build();
		return newUri;
	}

	public static Uri getUri(String path) {

		Uri uri = prepare(path).build();
		return uri;
	}

	private static Uri.Builder prepare(String path) {
		return new Uri.Builder().scheme("content")
				.authority(MyProvider.AUTHORITY).path(path).query("") 
				.fragment("");

	}
}
