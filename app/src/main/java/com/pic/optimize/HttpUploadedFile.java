package com.pic.optimize;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpUploadedFile {


	/** single instance of this class */
	private static HttpUploadedFile instance = null;
	
	/**
	 * Constructor
	 */
	private HttpUploadedFile(){
		
	}
	
	/**
	 * Factory method
	 */
	public static synchronized HttpUploadedFile getInstance(){
		if(instance == null){
			instance = new HttpUploadedFile();
		}
		return instance;
	}
	
	
	String url = "http://2.novelread.sinaapp.com/framework-sae/index.php?c=main&a=getPostBody";
	private int lastErrCode = 0;
	byte[] tmpBuf = new byte[BUF_LEN];
	byte[] tmpBuf2 = new byte[BUF_LEN * 2];
	public static final int POST_PROGRESS_NOTIFY = 101;
	HttpURLConnection connection = null;
	public static final int HTTP_ARGUMENT_ERR = -1001;
	public static final int HTTP_RESPONSE_EMPTY = -1002;// Http Response is
														// Empty
	public static final int HTTP_URL_ERR = -1003;
	public static final int HTTP_GZIP_ERR = -1004;
	public static final int HTTP_CANCELED = -1005;
	public static final int HTTP_EXCEPTION = -1006;
	private boolean bIsStop = false;
	protected Object objAbort = new Object();

	private Handler mHandler = null;
	public static final int TIMEOUT = 30000;
	private static final int BUF_LEN = 512;

	public boolean doUploadPhoto(Context context, String filePathName,
			Handler handler) {
		boolean ret = false;

		File file = new File(filePathName);
		if (!file.exists()) {
			return false;
		}
		FileInputStream fs = null;
		if (file != null) {
			try {
				fs = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (file == null || fs == null) {
			return false;
		}
		mHandler = handler;
		ret = postWithoutResponse(context, url, fs, (int) file.length());
		if (fs != null) {
			try {
				fs.close();
				fs = null;
			} catch (Exception e) {
			}
		}
		return ret;
	}

	public Boolean postWithoutResponse(Context context, final String strUrl,
			InputStream dataStream, int iStreamLen) {

		if (TextUtils.isEmpty(strUrl) || dataStream == null || iStreamLen <= 0) {
			lastErrCode = HTTP_ARGUMENT_ERR;
			return false;
		}

		URL postUrl = null;
		try {
			postUrl = new URL(strUrl);
		} catch (MalformedURLException ex) {
			Log.e("HttpUtil", "get MalformedURL", ex);
			lastErrCode = HTTP_URL_ERR;
			return false;
		}
		bIsStop = false;
		InputStream input = null;
		DataOutputStream ds = null;
		ByteArrayOutputStream byteOutStream = null;
		HttpURLConnection conn = null;
		byte[] outData = null;
		try {
			if (bIsStop) {
				lastErrCode = HTTP_CANCELED;
				return false;
			}
			conn = getConnection(context, postUrl);
			connection = conn;
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setConnectTimeout(TIMEOUT * 3);
			conn.setReadTimeout(TIMEOUT * 3);

			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "application/octet-stream");
			conn.setRequestProperty("Content-Length",
					String.valueOf(iStreamLen));
			if (bIsStop) {
				lastErrCode = HTTP_CANCELED;
				return false;
			}

			// get the wifi status
			WifiManager wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			boolean bWifiEnable = (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED);

			// byte[] data = new byte[BUF_LEN];
			ds = new DataOutputStream(conn.getOutputStream());
			int len = 0;
			int postLen = 0;
			int nMaxProgress = bWifiEnable ? 80 : 40;
			while (!bIsStop && ((len = dataStream.read(tmpBuf2)) != -1)) {
				ds.write(tmpBuf2, 0, len);
				ds.flush();
				// waiting for uploading the image file to optimize the progress
				// bar when using GPRS
				if (!bWifiEnable) {
					Thread.sleep(30);
				}

				// notify post progress
				postLen += len;
				if (mHandler != null) {
					Message msg = new Message();
					msg.what = POST_PROGRESS_NOTIFY;
					msg.arg1 = (postLen * nMaxProgress) / iStreamLen;
					mHandler.sendMessage(msg);
				}
			}
			if (bIsStop) {
				lastErrCode = HTTP_CANCELED;
				return false;
			}
			ds.flush();

			// waiting for uploading the image file to optimize the progress bar
			// when using GPRS
			if (!bWifiEnable) {
				postLen = 0;
				while (postLen < iStreamLen) {
					Thread.sleep(30);

					// notify post progress
					postLen += tmpBuf2.length;
					if (mHandler != null) {
						Message msg = new Message();
						msg.what = POST_PROGRESS_NOTIFY;
						msg.arg1 = (postLen * 35) / iStreamLen + 50;
						mHandler.sendMessage(msg);
					}
				}
			}

			// waiting for the server's response
			InputStream is = conn.getInputStream();
			int ch;
			StringBuffer res = new StringBuffer();
			while ((ch = is.read()) != -1) {
				res.append((char) ch);
			}

			if (mHandler != null) {
				Message msg = new Message();
				msg.what = POST_PROGRESS_NOTIFY;
				msg.arg1 = 90;
				mHandler.sendMessage(msg);
			}
			return true;
		} catch (Exception ex) {
			Log.e("HttpUtil", "post", ex);
			if (bIsStop) {
				lastErrCode = HTTP_CANCELED;
			} else {
				lastErrCode = HTTP_EXCEPTION;
			}
			return false;
		} finally {
			try {
				outData = null;
				if (input != null) {
					input.close();
					input = null;
				}
				// if (ds != null){
				// ds.close();
				// ds = null;
				// }
				try {
					ds.close();
					ds = null;
				} catch (Exception e) {
					// TODO: handle exception
				}

				if (conn != null) {
					conn.disconnect();
					conn = null;
				}
				if (byteOutStream != null) {
					byteOutStream.close();
					byteOutStream = null;
				}
				if (bIsStop) {
					synchronized (objAbort) {
						objAbort.notify();
					}
				}
				if (mHandler != null) {
					Message msg = new Message();
					msg.what = POST_PROGRESS_NOTIFY;
					msg.arg1 = 100;
					mHandler.sendMessage(msg);
				}
			} catch (Exception ex) {
				Log.e("HttpUtil", "post finally", ex);
			}
		}

	}

	public synchronized void cancel() {
		try {
			bIsStop = true;
			if (connection != null) {
				connection.disconnect();
				connection = null;
			}
			synchronized (objAbort) {
				objAbort.wait(50);
			}
		} catch (Exception ex) {
			Log.v("HttpUtil", "canel", ex);
		}
	}

	private HttpURLConnection getConnection(Context context, URL url)
			throws Exception {
		String[] apnInfo = null;
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		int wifiState = wifiManager.getWifiState();

		HttpURLConnection conn = null;
		conn = (HttpURLConnection) url.openConnection();

		return conn;
	}
}
