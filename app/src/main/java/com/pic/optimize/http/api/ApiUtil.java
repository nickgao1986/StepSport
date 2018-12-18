package com.pic.optimize.http.api;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import com.pic.optimize.R;
import com.pic.optimize.fresco.PicApplication;
import com.pic.optimize.http.OkHttpUtil;
import com.pic.optimize.http.Util.DialogUtil;
import com.pic.optimize.http.Util.Util;
import com.pic.optimize.http.params.OkRequestParams;
import com.pic.optimize.http.response.OkHttpCallback;

import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.Response;

import static com.pic.optimize.http.api.ApiKey.DATA;
import static com.pic.optimize.http.api.ApiKey.MESSAGE;
import static com.pic.optimize.http.api.ApiKey.STATUS;


public abstract class ApiUtil {

    public static final String API_STATUS_REJECT_ERROR = "RejectError";
    public static final String API_STATUS_NET_ERROR = "NetError";
    public static final String API_STATUS_PARSE_ERROR = "ParseError";
    public static final String API_STATUS_NONLOGIN = "nonLogin";
    public static final String API_STATUS_TIMEOUT = "timeout";
    public static final String API_STATUS_CANCELED = "canceled";

    private static final int STATUS_CODE_NO_NETWORK_PRE_HTTP = -1001; // 无网络状态码（不发送联网请求）

    /**
     * 请求参数列表
     */
    private OkRequestParams mParams = new OkRequestParams();

    /**
     * 标签，用于扩展保存标识信息
     */
    private Object mTag = null;

    /**
     * 状态码
     */
    private String mStatus = "";
    /**
     * 状态码信息
     */
    private String mStatusMessage = "";

    /**
     * 上下文
     */
    private Context mContext = null;
    /**
     * 是否显示加载界面
     */
    private boolean mShowLoading = true;
    /**
     * 数据返回后是否清除加载界面
     */
    private boolean mDismissLoadingFinished = true;
    /**
     * 加载界面字符串
     */
    private String mLoadingString = null;
    /**
     * 是否显示状态信息
     */
    private boolean mShowStatusMessage = true;
    /**
     * API发送监听
     */
    private ApiListener mApiListener = null;

    /**
     * 是否返回主线程
     *
     * @return 默认为true; 如果子类重写返回false,则不允许更新ui,且不能Toast
     */
    protected boolean isBackMainThread() {
        return true;
    }

    /**
     * 发送监听
     */
    private OkHttpResJsonHandler mSendListener = new OkHttpResJsonHandler() {

        @Override
        protected void onSuccessRequest(Call call, @Nullable final Response res, int code, Headers headers, JSONObject response) {
            super.onSuccessRequest(call, res, code, headers, response);
        }

        @Override
        protected void onFailureRequest(Call call, @Nullable final Response res, int code, Headers headers, int error, Throwable t) {
            super.onFailureRequest(call, res, code, headers, error, t);
        }

        @Override
        protected boolean isPostMainThread() {
            return isBackMainThread();
        }

        @Override
        public void onSuccess(Call call, int statusCode, Headers headers, JSONObject response) {
            if (null != response) {
                String status = response.optString(STATUS);
                String statusMessage = response.optString(MESSAGE);

                if (TextUtils.isEmpty(statusMessage)) {
                    setStatus(status, ApiStatus.getStatusMessage(status));
                } else {
                    setStatus(status, statusMessage);
                }

                if (isSuccess()) {
                    try {
                        dismissLoading();
                        onParse(response);
                        if (null != mApiListener) {
                            mApiListener.success(ApiUtil.this);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        failure(API_STATUS_PARSE_ERROR, mContext.getString(R.string.parse_error));
                    }
                } else {
                    if ("failed".equals(getStatus())) {
                        // 兼容data里含有message字段的api 优先级最高
                        JSONObject data = response.optJSONObject(DATA);
                        if (data != null) {
                            String message = data.optString(MESSAGE);
                            if (!TextUtils.isEmpty(message)) {
                                failure(status, message);
                            } else {
                                message = response.optString(MESSAGE);
                                if (!TextUtils.isEmpty(message)) {
                                    failure(status, message);
                                } else {
                                    failure(status, getStatusMessage());
                                }
                            }
                        }
                    }
                }
            } else {
                failure(API_STATUS_PARSE_ERROR, mContext.getString(R.string.parse_error));
            }
        }

        @Override
        public void onFailure(Call call, int code, Headers headers, int error, Throwable t) {
            if (code == 403) {
                failure(API_STATUS_REJECT_ERROR, "系统繁忙，请稍后再试");
            } else if (error == OkHttpCallback.RESPONSE_ERROR_SERVER) {
                failure(API_STATUS_NET_ERROR, "服务器处理错误");
            } else if (error == OkHttpCallback.RESPONSE_ERROR_TIMEOUT) {
                failure(API_STATUS_TIMEOUT, "服务器处理错误");
            } else if (error == OkHttpCallback.RESPONSE_CANCELED) {
                failure(API_STATUS_CANCELED, "请求取消");
            } else {
                failure(API_STATUS_NET_ERROR, "亲,您的网络不给力！");
            }
        }
    };

    private void failure(String status, String statusMessage) {
        dismissLoading();
        setStatus(status, statusMessage);
        // 特殊处理，1、当提示信息为未登陆时不弹出toast提示;  2、请求被取消
        if (!status.equals(API_STATUS_NONLOGIN) && !status.equals(API_STATUS_CANCELED)) {
            showStatusMessage(statusMessage);
        }
        if (null != mApiListener) {
            try {
                mApiListener.failure(ApiUtil.this);
            } catch (Exception e) {
            }
        }
    }


    protected ApiUtil() {
        super();
    }

    public Context getContext() {
        if (mContext == null) {
            return PicApplication.getContext();
        }
        return mContext;
    }


    /**
     * 判断状态码是否成功
     *
     * @return
     */
    public boolean isSuccess() {
        return ApiStatus.isSuccess(getStatus());
    }




    /**
     * 获取状态码
     *
     * @return：状态码
     */
    public String getStatus() {
        return mStatus;
    }

    /**
     * 设置状态码
     *
     * @param status ：状态码
     */
    public void setStatus(String status) {
        mStatus = status;
    }

    /**
     * 获取状态信息
     *
     * @return：状态信息
     */
    public String getStatusMessage() {
        return mStatusMessage;
    }


    /**
     * 设置状态码及错误信息
     *
     * @param status        ：状态码
     * @param statusMessage ：状态信息
     */
    public void setStatus(String status, String statusMessage) {
        mStatus = status;
        mStatusMessage = statusMessage;
    }


    /**
     * 添加参数
     *
     * @param key
     * @param value
     */
    public void addParam(String key, String value) {
        mParams.put(key, value);
    }


    /**
     * 获取url
     * @return：http链接url
     */
    protected abstract String getUrl();



    /**
     * 解析数据
     *
     * @param jsonObject
     */
    private void onParse(JSONObject jsonObject) throws Exception {
        parse(jsonObject);
    }

    /**
     * 解析数据
     *
     * @param jsonObject
     */
    protected abstract void parse(JSONObject jsonObject) throws Exception;




    /**
     * http post
     *
     * @param context           ：上下文
     * @param loadingString     ：加载界面显示的提示文字, 如果传空将不显示加载界面
     * @param dismissLoading    ：请求响应后是否销毁加载界面
     * @param showStatusMessage ：是否在出错时显示出错信息
     * @param listener          ：请求响应监听
     */
    public void post(Context context, String loadingString, boolean dismissLoading, boolean showStatusMessage,
                     ApiListener listener) {
        post(context, !TextUtils.isEmpty(loadingString), loadingString, dismissLoading, showStatusMessage, listener);
    }

    /**
     * http post
     *
     * @param context           ：上下文
     * @param showLoading       ：是否显示加载界面
     * @param loadingString     ：加载界面文字， 如果为空将显示默认文字
     * @param dismissLoading    ：请求完成后是否清除加载界面
     * @param showStatusMessage ：是否显示出错信息
     * @param listener          ：发送回调
     */
    public void post(Context context, boolean showLoading, String loadingString, boolean dismissLoading,
                     boolean showStatusMessage, ApiListener listener) {
        initSend(context, showLoading, loadingString, dismissLoading, showStatusMessage, listener);
        post();
    }




    /**
     * http get
     *
     * @param context           ：上下文
     * @param loadingString     ：加载界面显示的提示文字, 如果传空将不显示加载界面
     * @param dismissLoading    ：请求响应后是否销毁加载界面
     * @param showStatusMessage ：是否在出错时显示出错信息
     * @param listener          ：请求响应监听
     */
    public void get(Context context, String loadingString, boolean dismissLoading, boolean showStatusMessage,
                    ApiListener listener) {
        get(context, !TextUtils.isEmpty(loadingString), loadingString, dismissLoading, showStatusMessage, listener);
    }


    /**
     * http get
     *
     * @param context           ：上下文
     * @param showLoading       ：是否显示加载界面
     * @param loadingString     ：加载界面文字， 如果为空将显示默认文字
     * @param dismissLoading    ：请求完成后是否清除加载界面
     * @param showStatusMessage ：是否显示出错信息
     * @param listener          ：发送回调
     */
    public void get(Context context, boolean showLoading, String loadingString, boolean dismissLoading,
                    boolean showStatusMessage, ApiListener listener) {
        initSend(context, showLoading, loadingString, dismissLoading, showStatusMessage, listener);
        get();
    }

    /**
     * 无网络时模拟失败发送失败消息
     */
    private void post() {

        if (Util.hasNetwork(mContext)) {
            OkHttpUtil.post(getUrl(), mParams, mTag, mSendListener);
        } else if (mSendListener != null) {
            mSendListener.onFailure(null, STATUS_CODE_NO_NETWORK_PRE_HTTP, null, OkHttpCallback.RESPONSE_ERROR_NET, new Throwable("recvfrom failed: ECONNRESET (Connection reset by peer)"));
        }
    }



    /**
     * 无网络时模拟失败发送失败消息
     */
    private void get() {
        if (Util.hasNetwork(mContext)) {
            OkHttpUtil.get(getUrl(), mParams, mTag, mSendListener);
        } else if (mSendListener != null) {
            mSendListener.onFailure(null, STATUS_CODE_NO_NETWORK_PRE_HTTP, null, OkHttpCallback.RESPONSE_ERROR_NET, new Throwable("recvfrom failed: ECONNRESET (Connection reset by peer)"));
        }
    }

    /**
     * 初始化发送请求
     *
     * @param context           ：上下文
     * @param showLoading       ：是否显示加载界面
     * @param loadingString     ：加载界面文字， 如果为空将显示默认文字
     * @param dismissLoading    ：请求完成后是否清除加载界面
     * @param showStatusMessage ：是否显示出错信息
     * @param listener          ：发送回调
     */
    private void initSend(Context context, boolean showLoading, String loadingString, boolean dismissLoading,
                          boolean showStatusMessage, ApiListener listener) {
        if (null == context) {
            context = PicApplication.getContext();
        }
        mContext = context;
        mApiListener = listener;
        mShowLoading = showLoading;
        mLoadingString = loadingString;
        mDismissLoadingFinished = dismissLoading;
        mShowStatusMessage = showStatusMessage;

        showLoading();
    }

    /**
     * 设置loading对话栏显示信息
     *
     * @return
     */
    protected String getDialogMessage() {
        if (TextUtils.isEmpty(mLoadingString)) {
            return mContext.getString(R.string.loading);
        } else {
            return mLoadingString;
        }
    }

    /**
     * 显示加载界面
     */
    protected void showLoading() {
        if (mShowLoading) {
            DialogUtil.showLoadingDialog(mContext, getDialogMessage());
        }
    }

    /**
     * 清除加载界面
     */
    protected void dismissLoading() {
        // 成功时判断是否清除加载界面,失败时不判断是否清除直接清除加载界面
        if ((!isSuccess()) || mDismissLoadingFinished) {
            DialogUtil.dismissLoadingDialog(mContext);
        }
    }

    /**
     * 显示出错信息
     *
     * @param message
     */
    protected void showStatusMessage(String message) {
        if (mShowStatusMessage && !TextUtils.isEmpty(message)) {
            Toast.makeText(mContext,message,Toast.LENGTH_SHORT).show();
        }
    }

    protected String getString(int resId) {
        String result = "";
        if (null != mContext) {
            result = mContext.getString(resId);
        }
        return result;
    }



}