package com.pic.optimize.http.response;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;

import org.json.JSONTokener;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Response;
import okhttp3.ResponseBody;


public abstract class OkHttpCallback<T> implements Callback {

    private static final String TAG = OkHttpCallback.class.getSimpleName();
    /** 网络错误 **/
    public static final int RESPONSE_ERROR_NET = 1;
    /** 服务器错误 **/
    public static final int RESPONSE_ERROR_SERVER = 2;
    /** 连接超时 **/
    public static final int RESPONSE_ERROR_TIMEOUT = 3;
    /** 取消请求 **/
    public static final int RESPONSE_CANCELED = 4;

    protected Handler mOkHandler = null;

    public abstract void onSuccess(final Call call, int code, Headers headers, T response);

    public abstract void onFailure(final Call call, int code, Headers headers, int error, Throwable t);

    public void onProgress(long curSize, long totalSize, float progress, boolean done) {}

    protected abstract T parseResponse(Response response) throws Throwable;

    protected final void onSuccess(final Call call, Response response) throws Throwable {
        T responseObject = parseResponse(response);
        if (responseObject != null) {
            onSuccessRequest(call, response, response.code(), response.headers(), responseObject);
        } else {
            onFailureRequest(call, response, response.code(), response.headers(), RESPONSE_ERROR_SERVER, new Throwable("responseObject == null"));
        }
    }

    protected boolean isPostMainThread() {
        return true;
    }

    protected void onSuccessRequest(final Call call, @Nullable final Response res, final int code, final Headers headers, final T response) {
        if (isPostMainThread()) {
            getOkHandler().post(new Runnable() {
                @Override
                public void run() {
                    onSuccess(call, code, headers, response);
                }
            });
        } else {
            onSuccess(call, code, headers, response);
        }
    }

    protected void onFailureRequest(@Nullable final Call call, @Nullable final Response res, final int code, final Headers headers, final int error, final Throwable t) {
        if (isPostMainThread()) {
                getOkHandler().post(new Runnable() {
                @Override
                public void run() {
                    onFailure(call, code, headers, code == 504 ? RESPONSE_ERROR_TIMEOUT : error, t);
                }
            });
        } else {
            onFailure(call, code, headers, code == 504 ? RESPONSE_ERROR_TIMEOUT : error, t);
        }
    }



    @Override
    public final void onResponse(Call call, Response response) throws IOException {
        if (response != null) {
            try {
                if (response.isSuccessful()) {
                    onSuccess(call, response);
                } else {
                    onFailureRequest(call, response, response.code(), response.headers(), RESPONSE_ERROR_NET, new Throwable("!response.isSuccessful()"));
                }
            } catch (Throwable e) {
                onFailureRequest(call, response, response.code(), response.headers(), RESPONSE_ERROR_NET, e);
            } finally {
                close(response);
            }
        } else {
            onFailureRequest(call, null, 0, null, RESPONSE_ERROR_NET, new Throwable("response == null"));
        }
    }

    @Override
    public final void onFailure(Call call, IOException e) {
        if (call != null && call.isCanceled()) {
            onFailureRequest(call, null, 0, null, RESPONSE_CANCELED, e);
        } else {
            onFailureRequest(call, null, 0, null, RESPONSE_ERROR_NET, e);
        }
    }

    protected final void close(Response response) {
        if (response == null)
            return;

        ResponseBody body = response.body();
        if (body == null)
            return;

        try {
            body.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    protected final T responseToJson(Response response) throws Throwable {
        String responseStr = responseToString(response).trim();
        if (responseStr.startsWith("{") || responseStr.startsWith("[")) {
            return (T) new JSONTokener(responseStr).nextValue();
        }
        return null;
    }

    protected final String responseToString(Response response) throws Throwable {
        String responseStr = response.body().string().trim();
        if (responseStr.startsWith("\ufeff")) {
            responseStr = responseStr.substring(1);
        }
        return responseStr;
    }

    /**
     * 返回泛型 .class
     * @return
     */
    protected final Type getGenericType() {
        Type superclass = getClass().getGenericSuperclass();
        if (superclass instanceof ParameterizedType) {
            Type[] params = ((ParameterizedType) superclass).getActualTypeArguments();
            if (params.length > 0)
                return params[0];
        }
        return Object.class;
    }

    public final void setHandler(Handler okHandler) {
        this.mOkHandler = okHandler;
    }

    public final Handler getOkHandler() {
        return mOkHandler == null ? new Handler(Looper.getMainLooper()) : mOkHandler;
    }

    public static final Callback DEFAULT_CALLBACK = new Callback() {

        @Override
        public void onFailure(Call call, IOException e) {
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
        }
    };
}
