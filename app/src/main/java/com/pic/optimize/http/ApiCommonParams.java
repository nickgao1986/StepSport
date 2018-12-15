package com.pic.optimize.http;

import android.content.Context;
import android.text.TextUtils;

import com.pic.optimize.fresco.PicApplication;
import com.pic.optimize.http.Util.Util;
import com.pic.optimize.http.params.OkRequestParams;

import java.util.concurrent.ConcurrentHashMap;


public class ApiCommonParams {

    /**
	 * 上下文
	 */
	private static Context mContext = null;
	/**
	 * 应用标识(每个接口调用都需要传)
	 */
	private static String mAppName = null;
	/**
	 * mac地址(每个接口调用都需要传)
	 */
	private static String mMac = null;
	/**
	 * 版本号(每个接口调用都需要传)
	 */
	private static String mVersion = null;

	/**
	 * 网络状态(每个接口调用都需要传)
	 */
	private static int mNetworkState = 7;

	/**
	 * 用户id
	 */
	private static String mUserId = null;


    /**
     * IMEI
     */
    private static String mImei = null;
    /**
     * android id
     */
    private static String mAndroidId = null;
    /**
     * build_serial
     */
    private static String mBuildSerial = null;
    /**
     * 渠道号
     */
    private static String mChannel = null;
	/**
	 * 手机型号
	 */
	private static String mPhoneModel = null;
	/**
	 * 手机厂商
	 */
	private static String mManufacturer = null;
	/**
     * 用户下载app时间
     */
	private static long mDownloadTs;


	static {
		mContext = PicApplication.getContext();
		// 获取版本号
		mVersion = Util.getAppVersionName(mContext);
		// 获取mac
        mImei = Util.getImei(mContext);
        mAndroidId = Util.getAndroidId(mContext);
        mBuildSerial = Util.getBuildSerial(mContext);
        mPhoneModel = Util.getPhoneModel();
		mManufacturer = Util.getManufacturer();
	}


	/**
	 * 获取通用公共参数
	 */
	public static ConcurrentHashMap<String, String> fetchCommonsParams() {
		ConcurrentHashMap<String, String> params = new ConcurrentHashMap<>();

		params.put("client_type", "android");

		if (TextUtils.isEmpty(mImei)) {
			mImei = Util.getImei(mContext);
		}
		if (TextUtils.isEmpty(mAndroidId)) {
			mAndroidId = Util.getAndroidId(mContext);
		}
		if (TextUtils.isEmpty(mBuildSerial)) {
			mBuildSerial = Util.getBuildSerial(mContext);
		}


		// 关键校验参数，无论是否为空（非null）都将其拼入api
		params.put("mac", mMac == null ? "" : mMac);
		params.put("imei", mImei == null ? "" : mImei);

		if (!TextUtils.isEmpty(mAndroidId)) {
			params.put("android_id", mAndroidId);
		}
		if (!TextUtils.isEmpty(mBuildSerial)) {
			params.put("build_serial", mBuildSerial);
		}
		if (!TextUtils.isEmpty(mAppName)) {
			params.put("app_id", mAppName);
		}
		if (!TextUtils.isEmpty(mVersion)) {
			params.put("version", mVersion);
		}

		if (!TextUtils.isEmpty(mManufacturer)) {
			params.put("device_brand", mManufacturer);//厂商
		}
		if (!TextUtils.isEmpty(mPhoneModel)) {
			params.put("device_model", mPhoneModel);//型号
		}
		return params;
	}


	/**
	 * 追加通用公共参数
	 * @param params
	 */
	public static void appendCommonParams(OkRequestParams params) {
		if (params == null) { return; }
		params.put(fetchCommonsParams());
	}


	public static int getNetworkState() {
		return mNetworkState;
	}

	public static void setNetworkState(int networkState) {
		mNetworkState = networkState;
	}

	/**
	 * 获取渠道
	 * @return
	 */
	public static String getChannel() {
		return mChannel;
	}

	/**
	 * 获取mac地址
	 * @return
	 */
	public static String getMacAddress() {
		return mMac;
	}

    /**
     * 获取imei
     * @return
     */
    public static String getImei() {
        return mImei;
    }

    /**
     * 获取手机型号
     * @return
     */
    public static String getDeivceModel() {
        return mPhoneModel;
    }

	/**
	 * 获取 android id
	 * @return
	 */
	public static String getAndroidId() {
		return mAndroidId;
	}

	/**
	 * 获取版本号
	 * @return
	 */
	public static String getAppVersionName() {
		return mVersion;
	}


	public static void setUserID(String userId) {
		ApiCommonParams.mUserId = userId;
	}
	
	public static String getUserId() {
		return mUserId;
	}


}
