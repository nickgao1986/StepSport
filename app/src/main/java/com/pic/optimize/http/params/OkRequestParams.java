package com.pic.optimize.http.params;

import java.io.File;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by ldy on 2015/12/30.
 */
public class OkRequestParams {

    public final static String APPLICATION_OCTET_STREAM =
            "application/octet-stream";

    protected final ConcurrentHashMap<String, String> mUrlParams = new ConcurrentHashMap<>();
    protected final ConcurrentHashMap<String, FileWrapper> mFileParams = new ConcurrentHashMap<>();
    protected final ConcurrentHashMap<String, File[]> mFileArrays = new ConcurrentHashMap<>();
    protected final ConcurrentHashMap<String, String> mHeaderMap = new ConcurrentHashMap<>();

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

    public void put(String key, int value) {
        put(key, String.valueOf(value));
    }

    public void put(String key, float value) {
        put(key, String.valueOf(value));
    }

    public void put(String key, long value) {
        put(key, String.valueOf(value));
    }

    public void put(String key, double value) {
        put(key, String.valueOf(value));
    }

    public void put(String key, boolean value) {
        put(key, String.valueOf(value));
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

    public void put(String key, File file) {
        put(key, file, APPLICATION_OCTET_STREAM);
    }

    public void put(String key, File file, String contentType) {
        if (file != null && file.exists() && file.length() != 0) {
            put(key, new FileWrapper(file, MediaType.parse(contentType)));
        }
    }

    public void put(String key, FileWrapper fileWrapper) {
        try {
            if (key != null && fileWrapper != null) {
                mFileParams.put(key, fileWrapper);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void put(String key, File... files) {
        try {
            if (key != null && files != null) {
                mFileArrays.put(key, files);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void putHeader(String key, int value) {
        putHeader(key, String.valueOf(value));
    }

    public void putHeader(String key, float value) {
        putHeader(key, String.valueOf(value));
    }

    public void putHeader(String key, long value) {
        putHeader(key, String.valueOf(value));
    }

    public void putHeader(String key, double value) {
        putHeader(key, String.valueOf(value));
    }

    public void putHeader(String key, boolean value) {
        putHeader(key, String.valueOf(value));
    }

    public void putHeader(String key, String value) {
        if (key != null && value != null) {
            mHeaderMap.put(key, value);
        }
    }

    public void putHeader(Map<String, String> params) {
        if (params != null && params.size() > 0 ) {
            mHeaderMap.putAll(params);
        }
    }

    public Map<String, String> getUrlParams() {
        return mUrlParams;
    }

    public Map<String, String> getHeaderParams() {
        return mHeaderMap;
    }

    public Map<String, FileWrapper> getFileParams() {
        return mFileParams;
    }

    public Headers getRequestHeaders() {
        try {
            return Headers.of(mHeaderMap);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public void remove(String key) {
        mUrlParams.remove(key);
        mFileParams.remove(key);
        mFileArrays.remove(key);
        mHeaderMap.remove(key);
    }

    public static class FileWrapper {

        public final File file;
        public final String fileName;
        public final MediaType mediaType;

        public FileWrapper(File file, MediaType mediaType) {
            this.file = file;
            this.mediaType = mediaType;
            this.fileName = (file == null) ? "FILE" : file.getName();
        }
    }

    public RequestBody getRequestBody() {
        try {
            if (mFileParams.size() > 0 || mFileArrays.size() > 0) {
                return createMultipartBuilderBody();
            } else {
                return createEncodingBuilderBody();
            }
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    private RequestBody createMultipartBuilderBody() throws Throwable {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        for (ConcurrentHashMap.Entry<String, String> entry : mUrlParams.entrySet()) {
            builder.addFormDataPart(entry.getKey(), entry.getValue());
        }

        for (ConcurrentHashMap.Entry<String, FileWrapper> entry : mFileParams.entrySet()) {
            FileWrapper fileWrapper = entry.getValue();
            builder.addFormDataPart(entry.getKey(), fileWrapper.fileName, RequestBody.create(fileWrapper.mediaType, fileWrapper.file));
        }

        for (ConcurrentHashMap.Entry<String, File[]> entry : mFileArrays.entrySet()) {
            File[] files = entry.getValue();
            for (File file : files) {
                if (file != null && file.exists())
                    builder.addFormDataPart(entry.getKey(), file.getName(),
                            RequestBody.create(MediaType.parse(APPLICATION_OCTET_STREAM), file));
            }
        }
        return builder.build();
    }

    private RequestBody createEncodingBuilderBody() throws Throwable {
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

        for (ConcurrentHashMap.Entry<String, FileWrapper> entry : mFileParams.entrySet()) {
            if (result.length() > 0)
                result.append("&");

            result.append(entry.getKey());
            result.append("=");
            result.append("FILE");
        }

        for (ConcurrentHashMap.Entry<String, File[]> entry : mFileArrays.entrySet()) {
            if (result.length() > 0)
                result.append("&");

            result.append(entry.getKey());
            result.append("=");
            result.append(Arrays.toString(entry.getValue()));
        }

        return result.toString();
    }
}
