package com.pic.optimize.http.Util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;

import com.pic.optimize.fresco.PicApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

	private static final String TAG = Util.class.getSimpleName();

	/**
	 * 启动新的activity
	 *
	 * @param activity
	 * @param intent
	 * @param forResult
	 *            是否启用startActivityForResult方法
	 * @param requestCode
	 *            请求码:若forResult为false时候，不需要使用startActivityForResult方法，此设置无效
	 */
	public static void launch(Activity activity, Intent intent, boolean forResult, int requestCode) {
		if (forResult) {
			activity.startActivityForResult(intent, requestCode);
		} else {
			activity.startActivity(intent);
		}
	}

	public static void overridePendingTransition(Context context, int enterAnim, int exitAnim) {
		if (context instanceof Activity) {
			((Activity)context).overridePendingTransition(enterAnim, exitAnim);
		}
	}

	/**
	 * 获取IMEI
	 * @param context
	 * @return
	 */
	public static String getImei(Context context) {
		String result = "";
		try {
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			if (null != tm) {
				result = tm.getDeviceId();
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 获取build serial
	 * @param context
	 * @return
	 */
	public static String getBuildSerial(Context context) {
		String result = null;
		try {
			result = android.os.Build.SERIAL;
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return result;
	}


	/**
	 * ANDROID_ID是Android系统第一次启动时产生的一个64bit（16BYTES）数
	 * @param context
	 * @return
	 */
	public static String getAndroidId(Context context) {
		String result = "";
		try {
			result = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return result;
	}


	/**
	 * 判断android设备中是否有相应的应用来处理这个Intent。
	 *
	 * @param context
	 * @param intent
	 * @return
	 */
	public static boolean hasIntentActivities(Context context, Intent intent) {
		PackageManager packageManager = context.getPackageManager();
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent, 0);

		return list.size() > 0;
	}

	/**
	 * 获取AndroidMenifest.xml中Application中的meta data
	 *
	 * @param context
	 *            ：上下文
	 * @param name
	 *            ：meta data的名称
	 * @return：meta data的值
	 */
	public static String getApplicationMetaData(Context context, String name) {
		String result = "";
		try {
			ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			result = appInfo.metaData.get(name) + "";
		} catch (Exception e) {

		}
		return result;
	}



	/**
	 * 返回当前程序版本名
	 */
	public static String getAppVersionName(Context context) {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			return pi.versionName;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 返回当前程序版本号
	 */
	public static String getAppVersionCode(Context context) {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			return String.valueOf(pi.versionCode);
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		}
	}

	/**
	 * 返回当前程序名
	 */
	public static String getAppLabel(Context context) {
		try {
			PackageManager pm = context.getPackageManager();
			return String.valueOf(pm.getApplicationLabel(context.getApplicationInfo()));
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		}
	}



	@SuppressLint({"HardwareIds", "MissingPermission"})
	private static String getMacAddressByWifiInfo() {
		try {
			@SuppressLint("WifiManagerLeak")
            WifiManager wifi = (WifiManager) PicApplication.getContext().getSystemService(Context.WIFI_SERVICE);
			if (wifi != null) {
				WifiInfo info = wifi.getConnectionInfo();
				if (info != null) return info.getMacAddress();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "02:00:00:00:00:00";
	}

	private static String getMacAddressByNetworkInterface() {
		try {
			List<NetworkInterface> nis = Collections.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface ni : nis) {
				if (!ni.getName().equalsIgnoreCase("wlan0")) continue;
				byte[] macBytes = ni.getHardwareAddress();
				if (macBytes != null && macBytes.length > 0) {
					StringBuilder res1 = new StringBuilder();
					for (byte b : macBytes) {
						res1.append(String.format("%02x:", b));
					}
					return res1.deleteCharAt(res1.length() - 1).toString();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "02:00:00:00:00:00";
	}



	/**
	 * 网络状态判断
	 *
	 * @param context
	 * @return
	 */
	public static boolean hasNetwork(Context context) {
		try {
			if (null == context || null == context.getApplicationContext()) {
				return false;
			}
			ConnectivityManager manager = (ConnectivityManager) context.getApplicationContext().getSystemService(
					Context.CONNECTIVITY_SERVICE);
			if (manager == null) {
				return false;
			}
			NetworkInfo networkinfo = manager.getActiveNetworkInfo();
			if (networkinfo == null || !networkinfo.isAvailable()) {
				return false;
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 是否在wifi环境下
	 *
	 * @param context
	 * @return
	 */
	public static boolean isWifiActive(Context context) {
		boolean result = false;
		try {
			ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (null != manager) {
				NetworkInfo networkinfo = manager.getActiveNetworkInfo();
				if (networkinfo != null && (networkinfo.getType() == ConnectivityManager.TYPE_WIFI)) {
					result = true;
				}
			}
		} catch (Exception e) {

		}
		return result;
	}

	/**
	 * 是否在移动环境下，且已连接
	 *
	 * @param context
	 * @return
	 */
	public static boolean isMobileActive(Context context) {
		try {
			ConnectivityManager manager = (ConnectivityManager) context.getSystemService(
					Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = manager.getActiveNetworkInfo();
			return info != null && (info.getType() == ConnectivityManager.TYPE_MOBILE)
					&& info.isConnected();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 获取网络
	 *
	 * @param context
	 * @return
	 */
	public static String getExtraInfo(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo nwInfo = connectivity.getActiveNetworkInfo();
		if (nwInfo == null) {
			return null;
		}
		String extraInfo = nwInfo.getExtraInfo();
		String typeName = nwInfo.getTypeName();
		if (typeName != null && typeName.equalsIgnoreCase("WIFI")) {
			return typeName;
		}
		return extraInfo;
	}

	/**
	 * 返回网络信息
	 *
	 * @param context
	 * @return
	 */
	public static String getNetType(Context context) {
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo nwInfo = connectivity.getActiveNetworkInfo();
			if (nwInfo == null) {
				return "unknow";
			}
			return nwInfo.toString();
		} catch (Exception ex) {
			return "unknow";
		}
	}


	/**
	 * 枚举网络状态
	 * NET_NO：没有网络
	 * NET_2G:2g网络
	 * NET_3G：3g网络
	 * NET_4G：4g网络
	 * NET_WIFI：wifi
	 * NET_UNKNOWN：未知网络
	 */
	public interface NetState{
		Integer NET_NO = 0;
		Integer NET_WIFI = 1;
		Integer NET_2G = 2;
		Integer NET_3G = 3;
		Integer NET_4G = 4;
		Integer NET_UNKNOWN = 5;}

	/**
	 * 判断当前是否网络连接
	 * @param context
	 * @return 状态码
	 */
	public static int isConnected(Context context) {
		int stateCode = NetState.NET_NO;
		try {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo ni = cm.getActiveNetworkInfo();
			if (ni != null && ni.isConnectedOrConnecting()) {
				switch (ni.getType()) {
					case ConnectivityManager.TYPE_WIFI:
						stateCode = NetState.NET_WIFI;
						break;
					case ConnectivityManager.TYPE_MOBILE:
						switch (ni.getSubtype()) {
							case TelephonyManager.NETWORK_TYPE_GPRS: //联通2g
							case TelephonyManager.NETWORK_TYPE_CDMA: //电信2g
							case TelephonyManager.NETWORK_TYPE_EDGE: //移动2g
							case TelephonyManager.NETWORK_TYPE_1xRTT:
							case TelephonyManager. NETWORK_TYPE_IDEN:
								stateCode = NetState.NET_2G;
								break;
							case TelephonyManager.NETWORK_TYPE_EVDO_A: //电信3g
							case TelephonyManager.NETWORK_TYPE_UMTS:
							case TelephonyManager.NETWORK_TYPE_EVDO_0:
							case TelephonyManager.NETWORK_TYPE_HSDPA:
							case TelephonyManager.NETWORK_TYPE_HSUPA:
							case TelephonyManager.NETWORK_TYPE_HSPA:
							case TelephonyManager.NETWORK_TYPE_EVDO_B:
							case TelephonyManager.NETWORK_TYPE_EHRPD:
							case TelephonyManager.NETWORK_TYPE_HSPAP:
								stateCode = NetState.NET_3G;
								break;
							case TelephonyManager.NETWORK_TYPE_LTE:
								stateCode = NetState.NET_4G;
								break;
							default:
								stateCode = NetState.NET_UNKNOWN;
						}
						break;
					default:
						stateCode = NetState.NET_UNKNOWN;
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return stateCode;
	}

    public static boolean isNotWifi(Context context) {
        int netState = isConnected(context);
        return Util.NetState.NET_2G.equals(netState)
                || Util.NetState.NET_3G.equals(netState)
                || Util.NetState.NET_4G.equals(netState);

    }

	/**
	 * 获取手机系统对应的SDK_INT
	 *
	 * @return
	 */
	public static int getSDKINT() {
		return Build.VERSION.SDK_INT;
	}

	/**
	 * 获取手机系统版本
	 *
	 * @return
	 */
	public static String getVersionRelease() {
		return Build.VERSION.RELEASE;
	}

	/**
	 * 获取厂商
	 *
	 * @return
	 */
	public static String getManufacturer() {
		return android.os.Build.MANUFACTURER;
	}

	/**
	 * 手机型号
	 *
	 * @return
	 */
	public static String getPhoneModel() {
		return android.os.Build.MODEL;
	}
	public static String getOSVersionString(){
		return Build.VERSION.RELEASE;
	}


}