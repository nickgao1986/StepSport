package com.pic.optimize.http.api;


public interface ApiListener {
	/**
	 * 发送成功监听
	 * 
	 * @param api
	 */
	void success(ApiUtil api);

	/**
	 * 发送失败监听
	 * 
	 * @param api
	 */
	void failure(ApiUtil api);
}
