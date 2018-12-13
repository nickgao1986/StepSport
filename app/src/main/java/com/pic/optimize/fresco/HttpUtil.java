package com.pic.optimize.fresco;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpUtil {

    public static final int HTTP_ARGUMENT_ERR = -1001;
    public static final int HTTP_URL_ERR = -1003;
    public static final int TIMEOUT = 30000;
    private static final int BUF_LEN = 512;

    HttpURLConnection connection = null;
    byte[] tmpBuf = new byte[BUF_LEN];

    private Handler mHandler = null;
    public static final int POST_PROGRESS_NOTIFY = 101;

    public HttpUtil(){
    }

    public String get(Context context, final String strUrl) {
        URL getUrl = null;

        try {
            getUrl = new URL(strUrl);
        } catch (MalformedURLException ex) {
            Log.e("HttpUtil", "get MalformedURL", ex);
            return null;
        }
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


            conn.connect();

            input = conn.getInputStream();

            String webcontent = null;

            byteOutStream = new ByteArrayOutputStream();
            //byte[] buf = new byte[BUF_LEN];
            int i = 0;
            while((i = input.read(tmpBuf)) != -1){
                byteOutStream.write(tmpBuf, 0, i);
            }

            outData = byteOutStream.toByteArray();
            if(outData != null && outData.length > 0){
                webcontent = new String(outData);
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

        }

        return null;
    }

    public void doDelete(String urlStr,String params){
        try{
            System.out.println(urlStr);
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("DELETE");
            DataOutputStream ds = new DataOutputStream(conn.getOutputStream());
            conn.getOutputStream().write(params.getBytes());
            ds.flush();

            conn.getInputStream();
            if(conn.getResponseCode() ==200){
                System.out.println(">>>>>>成功");
            }else{
                System.out.println(conn.getResponseCode());
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        }

    }




    public String post(Context context, final String strUrl, String params) {

        URL postUrl = null;
        try {
            postUrl = new URL(strUrl);
        } catch (MalformedURLException ex) {
            Log.e("HttpUtil", "get MalformedURL", ex);
            return null;
        }
        InputStream input = null;
        DataOutputStream ds = null;
        ByteArrayOutputStream byteOutStream = null;
        HttpURLConnection conn = null;
        byte[] outData = null;
        try {

            conn = getConnection(context, postUrl);
            connection = conn;
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setConnectTimeout(TIMEOUT * 2);
            conn.setReadTimeout(TIMEOUT * 2);

            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length",String.valueOf(params.getBytes().length));

            //byte[] data = new byte[BUF_LEN];
            ds = new DataOutputStream(conn.getOutputStream());

            conn.getOutputStream().write(params.getBytes());

            ds.flush();

            input = conn.getInputStream();


            byteOutStream = new ByteArrayOutputStream();

            outData = getResponse(input, byteOutStream, tmpBuf);

            if(mHandler != null){
                Message msg = new Message();
                msg.what = POST_PROGRESS_NOTIFY;
                msg.arg1 = 90;
                mHandler.sendMessage(msg);
            }

            String webcontent = null;
            if(outData != null && outData.length > 0){
                webcontent = new String(outData);
            }

            return webcontent;
        } catch (Exception ex) {
            Log.e("HttpUtil", "post", ex);
        } catch (OutOfMemoryError ex){
            Log.e("HttpUtil", "post OutOfMemoryError", ex);
        } finally {
            try {
                outData = null;
                if (input != null){
                    input.close();
                    input = null;
                }
                if (ds != null){
                    ds.close();
                    ds = null;
                }
                if (conn != null){
                    conn.disconnect();
                    conn = null;
                }
                if (byteOutStream != null){
                    byteOutStream.close();
                    byteOutStream = null;
                }
                if(mHandler != null){
                    Message msg = new Message();
                    msg.what = POST_PROGRESS_NOTIFY;
                    msg.arg1 = 100;
                    mHandler.sendMessage(msg);
                }
            } catch (Exception ex) {
                Log.e("HttpUtil", "post finally", ex);
            }
        }
        return null;
    }

    private byte[] getResponse(InputStream input, ByteArrayOutputStream byteOutStream, byte[] data) throws IOException {
        if(input == null || byteOutStream == null || data == null){
            return null;
        }
        int i = 0;
        while((i = input.read(data)) != -1){
            byteOutStream.write(data, 0, i);
        }

        byte[] bmpData = byteOutStream.toByteArray();
        return bmpData;
    }

    private HttpURLConnection getConnection(Context context, URL url) throws Exception{
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        return conn;
    }

}
