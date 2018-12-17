package com.pic.optimize.http.api;

import android.text.TextUtils;

import com.pic.optimize.R;
import com.pic.optimize.fresco.PicApplication;

/**
 * 
 * API状态码
 * 
 * @author guozhiqing
 * 
 */
public class ApiStatus {

	protected static StatusMap mStatusMapList[] = {
		new StatusMap("0", R.string.success),
		new StatusMap("success", R.string.success),
		new StatusMap("failed", R.string.operation_fail),
		new StatusMap("auth_code_error", R.string.auth_code_error),
	};

	/**
	 * 解析服务器返回状态码。提供通用的错误码转换和错误信息
	 * 
	 * @param status
	 *            ：服务器返回状态码
	 */
	public static String getStatusMessage(String status) {
		String result = "";
		if (!TextUtils.isEmpty(status)) {
			for (StatusMap err : mStatusMapList) {
				if (status.equals(err.mStatus)) {
					result = PicApplication.getResString(err.mErrorMessageId);
					break;
				}
			}
		}
		return result;
	}

	/**
	 * 判断状态码是否成功
	 * 
	 * @param status
	 * @return
	 */
	public static boolean isSuccess(String status) {
		boolean result = false;
		if (("success".equalsIgnoreCase(status)) || ("0".equals(status)) || ("200".equals(status))) {
			result = true;
		}
		return result;
	}

	protected static class StatusMap {
		public String mStatus = null;
		public int mErrorMessageId = 0;

		public StatusMap(String status, int errorMessageId) {
			mStatus = status;
			mErrorMessageId = errorMessageId;
		}
	}
}
