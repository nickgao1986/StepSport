package com.pic.optimize.http;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.pic.optimize.http.params.OkRequestParams;
import com.pic.optimize.http.response.OkHttpCallback;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class OkHttpUtil {

    private static final String TAG = "[RC]OkHttpUtil";
    public static final int DEFAULT_CONNECT_TIMEOUT = 30 * 1000;
    public static final int DEFAULT_WRITE_TIMEOUT = 60 * 1000;
    public static final int DEFAULT_READ_TIMEOUT = 60 * 1000;

    private static OkHttpClient mOkHttpClient = null;
    private static OkHttpClient.Builder mClientBuilder = null;
    private static Handler mOkHandler = null;

    public static void init(Context context) {
        if (mOkHttpClient == null) {
            mClientBuilder = new OkHttpClient()
                    .newBuilder()
                    .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                    .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.MILLISECONDS)
                    .writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.MILLISECONDS);
            mOkHttpClient = mClientBuilder.build();
        }
    }

    public static <T> void get(String url, OkRequestParams params, Object tag, OkHttpCallback<T> okHttpCallback) {
        get(url, params, tag, true, okHttpCallback);
    }


    public static <T> void get(String url, OkRequestParams params, Object tag, boolean commonParams, OkHttpCallback<T> okHttpCallback) {
        if (commonParams) {
            ApiCommonParams.appendCommonParams(params);
        }
        url = getFinalUrl(url, params);
        Log.d(TAG, url);

        Call call = null;
        Callback callback = getCallBack(okHttpCallback);
        try {
            Headers headers = getRequestHeaders(params);
            Request request = getRequest(url, null, headers, tag);

            call = mOkHttpClient.newCall(request);
            call.enqueue(callback);
        } catch (Throwable e) {
            e.printStackTrace();
            callback.onFailure(call, new IOException("get", e));
        }
    }

    private static Headers getRequestHeaders(OkRequestParams params) {
        return params == null ? null : params.getRequestHeaders();
    }

    public static Request getRequest(String url, RequestBody requestBody, Headers headers, Object tag) {
        Request.Builder builder = new Request.Builder();
        if (requestBody != null) {
            builder.post(requestBody);
        }
        if (headers != null) {
            builder.headers(headers);
        }
        if (tag != null) {
            builder.tag(tag);
        }
        builder.url(url);
        return builder.build();
    }


    public static <T> Callback getCallBack(OkHttpCallback<T> okHttpCallBack) {
        if (okHttpCallBack == null) {
            return OkHttpCallback.DEFAULT_CALLBACK;
        } else {
            okHttpCallBack.setHandler(getOkHandler());
            return okHttpCallBack;
        }
    }

    private static Handler getOkHandler() {
        if (mOkHandler == null) {
            mOkHandler = new Handler(Looper.getMainLooper());
        }
        return mOkHandler;
    }

    public static String getFinalUrl(String url, OkRequestParams params) {
        if (params != null) {
            String paramString = params.getParamString().trim();
            if (!paramString.equals("") && !paramString.equals("?")) {
                url += url.contains("?") ? "&" : "?";
                url += paramString;
            }
        }
        return url;
    }

    public static <T> void post(String url, OkRequestParams params, Object tag, OkHttpCallback<T> okHttpCallback) {
        post(url, params, tag, false, true, okHttpCallback);
    }

    /**
     * post请求
     *
     * @param url           请求连接
     * @param postBodyParams        请求参数
     * @param tag           标记
     * @param isProgress    是否显示上传进度
     * @param commonParams  是否附带app公共参数
     * @param okHttpCallback 上传回调
     * @param <T>
     */
    public static <T> void post(String url, OkRequestParams postBodyParams, Object tag, boolean isProgress, boolean commonParams, OkHttpCallback<T> okHttpCallback) {
        if (commonParams) {
            OkRequestParams urlComParams = new OkRequestParams();
            url = getFinalUrl(url, urlComParams);
        } else {
            url = getFinalUrl(url, postBodyParams);
        }

        Call call = null;
        Callback callback = getCallBack(okHttpCallback);
        try {
            RequestBody requestBody = getRequestBody(postBodyParams, isProgress, okHttpCallback);
            Headers headers = getRequestHeaders(postBodyParams);
            Request request = getRequest(url, requestBody, headers, tag);

            call = mOkHttpClient.newCall(request);
            call.enqueue(callback);
        } catch (Throwable e) {
            e.printStackTrace();
            callback.onFailure(call, new IOException("post", e));
        }
    }

    private static <T> RequestBody getRequestBody(OkRequestParams params, boolean isProgress, OkHttpCallback<T> okHttpCallback) {
        RequestBody requestBody = getRequestBody(params);
        return requestBody;
    }

    private static RequestBody getRequestBody(OkRequestParams params) {
        return params == null ? null : params.getRequestBody();
    }


}
