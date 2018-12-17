package com.pic.optimize.http.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.pic.optimize.R;
import com.pic.optimize.fresco.PicApplication;
import com.pic.optimize.http.OkHttpUtil;
import com.pic.optimize.http.Util.Util;
import com.pic.optimize.http.params.OkRequestParams;
import com.pic.optimize.http.response.OkHttpCallback;

import org.json.JSONObject;

import java.io.File;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.Response;

import static com.pic.optimize.http.api.ApiKey.DATA;
import static com.pic.optimize.http.api.ApiKey.MESSAGE;
import static com.pic.optimize.http.api.ApiKey.STATUS;
import static com.pic.optimize.http.constant.Url.PROTOCOL_HTTP;
import static com.pic.optimize.http.constant.Url.PROTOCOL_HTTPS;


public abstract class ApiUtil {

    private static final String TAG = ApiUtil.class.getSimpleName();

    public static final String API_STATUS_NO_NETWORK = "NoNetwork";
    public static final String API_STATUS_REJECT_ERROR = "RejectError";
    public static final String API_STATUS_NET_ERROR = "NetError";
    public static final String API_STATUS_PARSE_ERROR = "ParseError";
    public static final String API_STATUS_NONLOGIN = "nonLogin";
    public static final String API_STATUS_TIMEOUT = "timeout";
    public static final String API_STATUS_CANCELED = "canceled";

    private static final int STATUS_CODE_NO_NETWORK_PRE_HTTP = -1001; // 无网络状态码（不发送联网请求）
    private static final int STATUS_CODE_EMULATOR_PRE_HTTP = -1002; // 模拟器状态码（不发送联网请求）

    public String newRsa = null;
    /**
     * 请求参数列表
     */
    private OkRequestParams mParams = new OkRequestParams();

    /**
     * 标签，用于扩展保存标识信息
     */
    private Object mTag = null;
    /**
     * 网络相应超时时间
     */
    private int mTimeoutDuration = 20 * 1000;
    /**
     * 状态码
     */
    private String mStatus = "";
    /**
     * 状态码信息
     */
    private String mStatusMessage = "";
    /**
     * 返回结果Json
     */
    private JSONObject mResponse = null;
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
            onFailureErrorLog(call, code, headers, "");
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
                setResponseJson(response);
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
                    // 兼容根字段里含有失败message信息的api 优先级其次
                    if (TextUtils.isEmpty(getStatusMessage())) {
                        if (!"changeRsa".equals(getStatus())) {
                            String message = response.optString(MESSAGE);
                            if (TextUtils.isEmpty(message)) {
                                message = mContext.getString(R.string.req_fail);
                            }
                            failure(status, message);
                        } else {
                            JSONObject data = response.optJSONObject(DATA);
                            if (data != null) {
                                if (data.has("newRsa")) {
                                    newRsa = data.optString("newRsa");
                                    failure(status, getStatusMessage());
                                }
                            }
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
                        } else {
                            // 兼容status里含有失败信息的api 优先级最低
                            failure(status, getStatusMessage());
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
     * 获取标签
     *
     * @return：标签，用于扩展保存标识信息
     */
    public Object getTag() {
        return mTag;
    }

    /**
     * 设置标签
     *
     * @param tag ：标签，用于扩展保存标识信息
     */
    public ApiUtil setTag(Object tag) {
        mTag = tag;
        return this;
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
     * 是否网络错误
     *
     * @return
     */
    public boolean isNetError() {
        return API_STATUS_NO_NETWORK.equalsIgnoreCase(getStatus())
                || API_STATUS_NET_ERROR.equalsIgnoreCase(getStatus());
    }

    /**
     * 是否被取消
     *
     * @return
     */
    public boolean isCanceled() {
        return API_STATUS_CANCELED.equalsIgnoreCase(getStatus());
    }

    /**
     * @return
     */
    public boolean isNonLogin() {
        return API_STATUS_NONLOGIN.equalsIgnoreCase(getStatus());
    }

    public String getResponse() {
        try {
            return mResponse.toString();
        } catch (Throwable e) {
            return "";
        }
    }

    public JSONObject getResponseJson() {
        return mResponse;
    }

    private void setResponseJson(JSONObject response) {
        this.mResponse = response;
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
     * 设置状态信息
     *
     * @param statusMessage ：状态信息
     */
    public void setStatusMessage(String statusMessage) {
        mStatusMessage = statusMessage;
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
     * 获取超时时间
     *
     * @return：超时时间
     */
    public int getTimeoutDuration() {
        return mTimeoutDuration;
    }

    /**
     * 设置超时时间
     *
     * @param timeoutDuration ：新的超时时间
     */
    public void setTimeoutDuration(int timeoutDuration) {
        mTimeoutDuration = timeoutDuration;
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

    public void addParam(String key, int value) {
        mParams.put(key, value);
    }

    /**
     * 添加文件
     *
     * @param key
     * @param file
     */
    public void addParam(String key, File file) {
        try {
            mParams.put(key, file);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void addParam(String key, File... files) {
        try {
            mParams.put(key, files);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void addHeaderParam(String key, String value) {
        mParams.putHeader(key, value);
    }

    public void addHeaderParam(String key, int value) {
        mParams.putHeader(key, value);
    }

    /**
     * 获取url
     * <p>
     * 注：api url 尾标不要加 /
     * 错误：Url.HOST_URL + "/api/mobile_sign/check_in/"
     * 正确：Url.HOST_URL + "/api/mobile_sign/check_in"
     *
     * @return：http链接url
     */
    protected abstract String getUrl();

    /**
     * 获取真正的Url地址
     *
     * @return 真正的Url地址
     */
    private String getRealUrl(@NonNull String url) {
        if (isHttps() && url.contains(PROTOCOL_HTTP))
            return url.replace(PROTOCOL_HTTP, PROTOCOL_HTTPS);
        else
            return url;
    }

    /**
     * 判断是否是执行Https协议
     *
     * @return @return 是：https，否：http
     */
    public boolean isHttps() {
        return false;
    }

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
     * Http 请求失败  错误日志
     *
     * @param statusCode http response status line
     * @param content    errorResponse.toString();  it`s may ""
     */
    protected void onFailureErrorLog(Call call, int statusCode, Headers headers, String content) {
        if (call == null || isStatusCodePreHttp(statusCode)) {
            return;
        }
        try {
            String url = call.request().url().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否是未发联网请求的模拟状态码
     *
     * @param statusCode
     * @return
     */
    protected boolean isStatusCodePreHttp(int statusCode) {
        return STATUS_CODE_NO_NETWORK_PRE_HTTP == statusCode || STATUS_CODE_EMULATOR_PRE_HTTP == statusCode;
    }

    /**
     * http post
     *
     * @param context        ：上下文
     * @param showLoading    ：是否显示加载界面
     * @param dismissLoading ：请求响应后是否销毁加载界面
     * @param listener       ：请求响应监听
     */
    public void post(Context context, boolean showLoading, boolean dismissLoading, ApiListener listener) {
        post(context, showLoading, null, dismissLoading, true, listener);
    }

    @Deprecated
    /**
     * http post 需要尽量避免重复显示失败toast
     *
     * @param context
     *            ：上下文
     * @param loadingString
     *            ：加载界面显示的提示文字, 如果传空将不显示加载界面
     * @param dismissLoading
     *            ：请求响应后是否销毁加载界面
     * @param listener
     *            ：请求响应监听
     */
    public void post(Context context, String loadingString, boolean dismissLoading, ApiListener listener) {
        post(context, !TextUtils.isEmpty(loadingString), loadingString, dismissLoading, true, listener);
    }

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
     * http post，默认显示加载界面，请求响应后销毁加载界面
     *
     * @param context  ：context上下文
     * @param listener ：请求响应监听
     */
    public void post(Context context, ApiListener listener) {
        post(context, true, null, true, true, listener);
    }

    /**
     * http post，默认显示加载界面，请求响应后销毁加载界面
     *
     * @param context           ：context上下文
     * @param showStatusMessage ：是否在出错时显示出错信息
     * @param listener          ：请求响应监听
     */
    public void post(Context context, boolean showStatusMessage, ApiListener listener) {
        post(context, true, null, true, showStatusMessage, listener);
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
     * @param context        ：上下文
     * @param showLoading    ：是否显示加载界面
     * @param dismissLoading ：请求响应后是否销毁加载界面
     * @param listener       ：请求响应监听
     */
    public void get(Context context, boolean showLoading, boolean dismissLoading, ApiListener listener) {
        get(context, showLoading, null, dismissLoading, true, listener);
    }

    /**
     * http get
     *
     * @param context        ：上下文
     * @param loadingString  ：加载界面显示的提示文字, 如果传空将不显示加载界面
     * @param dismissLoading ：请求响应后是否销毁加载界面
     * @param listener       ：请求响应监听
     */
    public void get(Context context, String loadingString, boolean dismissLoading, ApiListener listener) {
        get(context, !TextUtils.isEmpty(loadingString), loadingString, dismissLoading, true, listener);
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
     * http get，默认显示加载界面，请求响应后销毁加载界面
     *
     * @param context  ：context上下文
     * @param listener ：请求响应监听
     */
    public void get(Context context, ApiListener listener) {
        get(context, true, null, true, true, listener);
    }

    /**
     * http get，默认显示加载界面，请求响应后销毁加载界面
     *
     * @param context           ：context上下文
     * @param showStatusMessage ：是否在出错时显示出错信息
     * @param listener          ：请求响应监听
     */
    public void get(Context context, boolean showStatusMessage, ApiListener listener) {
        get(context, true, null, true, showStatusMessage, listener);
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
            OkHttpUtil.post(getRealUrl(getUrl()), mParams, mTag, mSendListener);
        } else if (mSendListener != null) {
            mSendListener.onFailure(null, STATUS_CODE_NO_NETWORK_PRE_HTTP, null, OkHttpCallback.RESPONSE_ERROR_NET, new Throwable("recvfrom failed: ECONNRESET (Connection reset by peer)"));
        }
    }



    /**
     * 无网络时模拟失败发送失败消息
     */
    private void get() {
        if (Util.hasNetwork(mContext)) {
            OkHttpUtil.get(getRealUrl(getUrl()), mParams, mTag, mSendListener);
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
//        if (mShowLoading) {
//            DialogUtil.showLoadingDialog(mContext, getDialogMessage());
//        }
    }

    /**
     * 清除加载界面
     */
    protected void dismissLoading() {
        // 成功时判断是否清除加载界面,失败时不判断是否清除直接清除加载界面
//        if ((!isSuccess()) || mDismissLoadingFinished) {
//            DialogUtil.dismissLoadingDialog(mContext);
//        }
    }

    /**
     * 显示出错信息
     *
     * @param message
     */
    protected void showStatusMessage(String message) {
        if (mShowStatusMessage && !TextUtils.isEmpty(message)) {
           // ToastUtil.show(mContext, message);
        }
    }

    protected String getString(int resId) {
        String result = "";
        if (null != mContext) {
            result = mContext.getString(resId);
        }
        return result;
    }

    /**
     * 是否有添加积分成功弹出  start
     **/
    private boolean isToastScore = false;

    /**
     * 有积分相关 toast或者弹窗，不弹出其他成功文案
     *
     * @return
     */
    public boolean isToastScore() {
        return isToastScore;
    }

    public void setToastScore(boolean isToastScore) {
        this.isToastScore = isToastScore;
    }
    /** 是否弹出加载成功信息(选择性使用) end **/

    /**
     * 是否显示积分Toast
     */
    private boolean isShowScoreToast = true;

    public void setShowScoreToast(boolean isShowScore) {
        this.isShowScoreToast = isShowScore;
    }

    public boolean isShowScoreToast() {
        return isShowScoreToast;
    }

}