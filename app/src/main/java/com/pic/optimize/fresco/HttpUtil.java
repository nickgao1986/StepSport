package com.pic.optimize.fresco;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpUtil {

    public static final int HTTP_ARGUMENT_ERR = -1001;
    public static final int HTTP_RESPONSE_EMPTY = -1002;//Http Response is Empty
    public static final int HTTP_URL_ERR = -1003;
    public static final int HTTP_GZIP_ERR = -1004;
    public static final int HTTP_CANCELED = -1005;
    public static final int HTTP_EXCEPTION = -1006;
    public static final int TIMEOUT = 30000;
    private static final int BUF_LEN = 512;

    HttpURLConnection connection = null;
    private boolean bIsStop = false;
    protected Object objAbort = new Object();
    private int lastErrCode = 0;
    private boolean bNeedGzip = true;
    byte[] tmpBuf = new byte[BUF_LEN];
    byte[] tmpBuf2 = new byte[BUF_LEN * 2];

    private Handler mHandler = null;
    public static final int POST_PROGRESS_NOTIFY = 101;

    public HttpUtil(){
    }

    public int getLastErrCode(){
        return lastErrCode;
    }

    public String get(Context context, final String strUrl) {
        if (TextUtils.isEmpty(strUrl) || context == null) {
            lastErrCode = HTTP_ARGUMENT_ERR;
            return null;
        }
        final String fixurl = strUrl;

        URL getUrl = null;

        try {
            getUrl = new URL(fixurl);
        } catch (MalformedURLException ex) {
            Log.e("HttpUtil", "get MalformedURL", ex);
            lastErrCode = HTTP_URL_ERR;
            return null;
        }
        bIsStop = false;
        InputStream input = null;
        ByteArrayOutputStream byteOutStream = null;
        HttpURLConnection conn = null;
        byte[] outData = null;
        try {
            conn = getConnection(context, getUrl);
            connection = conn;
            conn.setConnectTimeout(TIMEOUT);
            conn.setReadTimeout(TIMEOUT);
            conn.setDoInput(true);

            if(bIsStop){
                lastErrCode = HTTP_CANCELED;
                return null;
            }
            conn.connect();
            if(bIsStop){
                lastErrCode = HTTP_CANCELED;
                return null;
            }
            input = conn.getInputStream();
            if(bIsStop){
                lastErrCode = HTTP_CANCELED;
                return null;
            }
            String webcontent = null;

            byteOutStream = new ByteArrayOutputStream();
            //byte[] buf = new byte[BUF_LEN];
            int i = 0;
            while(!bIsStop && (i = input.read(tmpBuf)) != -1){
                byteOutStream.write(tmpBuf, 0, i);
            }
            if(bIsStop){
                lastErrCode = HTTP_CANCELED;
                return null;
            }
            outData = byteOutStream.toByteArray();
            if(outData != null && outData.length > 0){
                webcontent = new String(outData);
            }
            if(bIsStop){
                lastErrCode = HTTP_CANCELED;
                return null;
            }
            return webcontent;
        } catch (Exception ex) {
            Log.e("HttpUtil", "get", ex);
            //return ex.getMessage();
        } finally {
            try{
                outData = null;
                if(input != null){
                    input.close();
                    input = null;
                }

                if(byteOutStream != null){
                    byteOutStream.close();
                    byteOutStream = null;
                }
                if(conn != null){
                    conn.disconnect();
                    conn = null;
                }
            } catch(Exception ex){
                Log.e("HttpUtil", "get finally", ex);
                //return ex.getMessage();
            }
            if(bIsStop){
                synchronized (objAbort) {
                    objAbort.notify();
                }
            }
        }
        if(bIsStop){
            lastErrCode = HTTP_CANCELED;
        }
        else{
            lastErrCode = HTTP_EXCEPTION;
        }
        return null;
    }


    private HttpURLConnection getConnection(Context context, URL url) throws Exception{
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        return conn;
    }

}
