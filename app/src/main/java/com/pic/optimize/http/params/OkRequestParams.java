package com.pic.optimize.http.params;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.FormBody;
import okhttp3.RequestBody;


public class OkRequestParams {

    protected final HashMap<String, String> mUrlParams = new HashMap<>();

    public OkRequestParams() {
        this(null);
    }

    public OkRequestParams(Map<String, String> source) {
        if (source != null) {
            for (Map.Entry<String, String> entry : source.entrySet()) {
                put(entry.getKey(), entry.getValue());
            }
        }
    }


    public void put(String key, String value) {
        if (key != null && value != null) {
            mUrlParams.put(key, value);
        }
    }

    public void put(Map<String, String> params) {
        if (params != null && params.size() > 0 ) {
            mUrlParams.putAll(params);
        }
    }


    public void remove(String key) {
        mUrlParams.remove(key);
    }


    public RequestBody getRequestBody() {
        try {
            return createEncodingBuilderBody();
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }



    private RequestBody createEncodingBuilderBody() {
        FormBody.Builder builder = new FormBody.Builder();

        for (ConcurrentHashMap.Entry<String, String> entry : mUrlParams.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }
        return builder.build();
    }

    public String getParamString() {
        StringBuilder result = new StringBuilder();
        for (ConcurrentHashMap.Entry<String, String> entry : mUrlParams.entrySet()) {
            if (result.length() > 0)
                result.append("&");
            try {
                result.append(entry.getKey());
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            } catch (Throwable e) {
                return "";
            }
        }
        return result.toString();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (ConcurrentHashMap.Entry<String, String> entry : mUrlParams.entrySet()) {
            if (result.length() > 0)
                result.append("&");

            result.append(entry.getKey());
            result.append("=");
            result.append(entry.getValue());
        }

        return result.toString();
    }
}
