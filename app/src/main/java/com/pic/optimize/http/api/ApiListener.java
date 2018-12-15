package com.pic.optimize.http.api;


public interface ApiListener {
	/**
	 * 发送成功监听
	 * 
	 * @param api
	 */
	void success(ApiBase api);

	/**
	 * 发送失败监听
	 * 
	 * @param api
	 */
	void failure(ApiBase api);
}
