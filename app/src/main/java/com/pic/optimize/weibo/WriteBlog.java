package com.pic.optimize.weibo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pic.optimize.BookDetail;
import com.pic.optimize.R;
import com.pic.optimize.http.Book;
import com.pic.optimize.weibo.view.EmotionView;

public class WriteBlog extends Activity implements OnClickListener,TextWatcher{

	ImageButton ibInsertLocation;
	private LocationHelper mLocationHelper;
	ProgressBar pgLoadingLocation;
	ImageView insertPicView, insertLocationView;
	EmotionView mEmotionView;
	EditText etMblog;
	TextView tvTextLimit;
	ImageButton ibInsertPic;
	ImageButton ibFace;
	InputMethodManager mInputMethodManager;
	private FaceClickHealper mFaceClickHealper;
	static final int DIALOG_ALERT_DELETE_LOCATION = 1012;
	static final int DIALOG_ALERT_IMPORT = 1001;
	static final int DIALOG_ALERT_DETELE_ALL_TEXT = 1010;
	private File sdcardTempFile;
	private static final int REQUEST_CODE_CAMERA = 101;
	private static final int REQUEST_CODE_GALLERY = 100;
	private ImageLoadingHelper mImageLoadingHelper;
	protected static final int REQUEST_CODE_IMAGE_EDIT = 102;
	private TopicHelper mTopicHelper;
	private AtEditHelper mAtEditHelper;
	// 默认文字缓存的保存路径
	private String DEFAULT_DRAFT_TEXT_PATH;
	static final int DIALOG_ALERT_OVERRIDE_DRAFT = 1008;
	static final int DIALOG_ALERT_LOAD_DRAFT_CACHE = 1009;

	public static void startActivity(Context context) {
		Intent intent = new Intent();
		intent.setClass(context,WriteBlog.class);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.write_blog);
		
		mFaceClickHealper = new FaceClickHealper(this);
		
		mLocationHelper = new LocationHelper(this);
		ibInsertLocation = (ImageButton) findViewById(R.id.ib_insert_location);
		ibInsertLocation.setOnClickListener(this);
		
		ibFace = (ImageButton) findViewById(R.id.ib_face_keyboard);
		ibFace.setOnClickListener(this);
		
		etMblog = (EditText) findViewById(R.id.et_mblog);
		etMblog.setOnClickListener(this);
		etMblog.addTextChangedListener(this);
		
		mAtEditHelper = new AtEditHelper(this);
		mTopicHelper = new TopicHelper(this);
		
		mEmotionView = (EmotionView) findViewById(R.id.emotion_view);
		mEmotionView.setEmotionAdapter(mEmotionAdapter);
		mImageLoadingHelper = new ImageLoadingHelper(this);
		insertPicView = (ImageView) findViewById(R.id.iv_insertpic);
		insertLocationView = (ImageView) findViewById(R.id.iv_location);
		pgLoadingLocation = (ProgressBar) findViewById(R.id.pg_loadlocation);
		tvTextLimit = (TextView) findViewById(R.id.tv_text_limit);
		ibInsertPic = (ImageButton) findViewById(R.id.ib_insert_pic);
		ibInsertPic.setOnClickListener(this);
		
		findViewById(R.id.ll_text_limit_unit).setOnClickListener(this);
		findViewById(R.id.ib_insert_topic).setOnClickListener(this);
		findViewById(R.id.ib_insert_at).setOnClickListener(this);
		
		mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		sdcardTempFile = new File(Environment.getExternalStorageDirectory(),
				Utils.CAMERA_FILE_CACHE_PREX
						+ SystemClock.currentThreadTimeMillis() + ".jpg");
		
		DEFAULT_DRAFT_TEXT_PATH = getFilesDir().getAbsolutePath()
				+ "/draft/text.dat";
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (doesDraftExist()) {
			showDialog(DIALOG_ALERT_LOAD_DRAFT_CACHE);
		}
	}

	final class ImageLoadingHelper {
		
		private File picfile;
		private WriteBlog activity;
		private String defaultTempPath;
		private String defaultDraftPath;
		
		Bitmap createThumbnail() {
			Options thumbnailOpts = new Options();
			if (!hasBitmap()) { throw new IllegalStateException("There is no pic!"); }
			return BitmapFactory.decodeFile(getTempPath(),
					thumbnailOpts);
		}
		
		synchronized boolean hasBitmap() {
			String picfileName = picfile.getName();
			if(picfileName.matches(".avi")|| picfileName.matches(".rm")||picfileName.matches(".mp4")
					||picfileName.matches(".rmvb")||picfileName.matches(".wmv")||picfileName.matches(".flv"))
				{
				return false;
				}
			else{
				return FileUtils.doesExisted(picfile);
			}
			
		}
		
		synchronized void importBitmapFile(Uri uri) {
			if (uri.getScheme().equals("content")) {
				InputStream inputStream = null;
				try {
					inputStream = activity.getContentResolver()
							.openInputStream(uri);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				FileUtils.file_put_contents(inputStream,picfile);
			}
		}
		
		
		boolean doesDraftExist() {
			return FileUtils.doesExisted(defaultDraftPath);
		}
		

		String getTempPath() {
			return picfile == null ? "" : picfile.getAbsolutePath();
		}
		
		
		void loadDraft() {
			FileUtils.copy(defaultDraftPath, defaultTempPath);
			activity.displayInsertBitmap();
			
		}
		
		void saveDraft() {
			FileUtils.copy(defaultTempPath, defaultDraftPath);
		}
		
		
		ImageLoadingHelper(WriteBlog activity) {
			this.activity = activity;
			if (FileUtils.isSdCardAvailable()) {
				defaultTempPath = FileUtils.getSDPath() + "/temp/"
						+ System.currentTimeMillis() + ".jpg";
			}
			else {
				defaultTempPath = activity.getFilesDir().getAbsolutePath()
						+ "/pic/" + System.currentTimeMillis() + ".jpg";
			}
			defaultDraftPath = activity.getFilesDir().getAbsolutePath()
					+ "/draft/bitmap_temp.jpg";
			if (FileUtils.doesExisted(defaultTempPath)) {
				defaultTempPath.replace(".jpg", "(1).jpg");
			}
			picfile = new File(defaultTempPath);
		}
		
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			showDialog(DIALOG_ALERT_OVERRIDE_DRAFT);
			return true;
		} else {
			return super.dispatchKeyEvent(event);
		}
	}
	
	private boolean saveDraft(String text) {
		try {
			File f = new File(DEFAULT_DRAFT_TEXT_PATH);
			System.out.println("save draft path="+DEFAULT_DRAFT_TEXT_PATH+"exsit="+f.exists());
			FileUtils.makesureFileExist(DEFAULT_DRAFT_TEXT_PATH);
			FileUtils.saveObject(text, new FileOutputStream(
					DEFAULT_DRAFT_TEXT_PATH));
			
			
			
		}
		catch (final Exception e) {
		}
		return false;
	}

	
	
	private void saveDraft() {
		final String text = etMblog.getText().toString();
		if (!TextUtils.isEmpty(text)) {
			saveDraft(text);
		}
		mImageLoadingHelper.saveDraft();
		finish();
	}
	
	
	private EmotionView.EmotionAdapter mEmotionAdapter = new EmotionView.EmotionAdapter() {
		public void doAction(int resId, String desc) {
			final int start = etMblog.getSelectionStart();
			final int end = etMblog.getSelectionEnd();
			final String preText = etMblog.getText().toString();
			final String addText = "[" + desc + "]";
			final SpannableStringBuilder sb = new SpannableStringBuilder();
			sb.append(preText, 0, start);
			sb.append(addText);
			sb.append(preText, end, preText.length());
			Utils.highlightContent(WriteBlog.this,sb);
			etMblog.setText(sb, TextView.BufferType.SPANNABLE);
			etMblog.setSelection(start + addText.length());
		}
	};
	
	void onLocatedError() {
		insertLocationView.setVisibility(View.GONE);
		pgLoadingLocation.setVisibility(View.GONE);
		Toast.makeText(this, R.string.main_gps_fail, Toast.LENGTH_SHORT).show();
	}
	

	void onLocatedSuccess() {
		pgLoadingLocation.setVisibility(View.GONE);
		insertLocationView.setVisibility(View.VISIBLE);
	}

	
	
	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.ib_face_keyboard:
			mFaceClickHealper.onClick(v);
			break;
		case R.id.ib_insert_location:
			if (mLocationHelper.isFetching()
					|| mLocationHelper.isLocatedSucess()) {
				showDialog(DIALOG_ALERT_DELETE_LOCATION);
			}
			else {
				mLocationHelper.openAllProviders();
			}
			break;
		case R.id.ib_insert_pic:
			showDialog(WriteBlog.DIALOG_ALERT_IMPORT);
			break;
		case R.id.ib_insert_topic:
			mTopicHelper.insertText();
			break;

		case R.id.ib_insert_at:
			mAtEditHelper.insertText();
			break;
		case R.id.ll_text_limit_unit:
			if (!TextUtils.isEmpty(etMblog.getText().toString())) {
				showDialog(WriteBlog.DIALOG_ALERT_DETELE_ALL_TEXT);
			}
			break;
		default:
			break;
	}
	}
	
	final class AtEditHelper {
		private static final String AT = "@请输入用户名 ";
		private WriteBlog activity;

		public void insertText() {
			final EditText editText = activity.etMblog;
			final int selectionStart = editText.getSelectionStart();//获取光标所在位置
			final int secletionEnd = editText.getSelectionEnd();
			final String text = editText.getText().toString();
			if (selectionStart == -1 || secletionEnd == -1
					|| selectionStart > secletionEnd) {
				activity.etMblog.append(AtEditHelper.AT);
				final int totalLength = activity.etMblog.getText().toString()
						.length();
				activity.etMblog
						.setSelection(totalLength - AtEditHelper.AT.length() + 1,
								totalLength - 1);
			}else {
				final StringBuilder sb = new StringBuilder();
				sb.append(text.substring(0, selectionStart));
				sb.append(AtEditHelper.AT);
				sb.append(text.substring(secletionEnd));
				editText.setText(sb.toString());
				Selection.setSelection(editText.getText(), selectionStart + 1,
						selectionStart + AtEditHelper.AT.length() - 1);
				
			}

		}

		AtEditHelper(WriteBlog activity) {
			super();
			this.activity = activity;
		}
	}
	
	final class TopicHelper {
		private static final String TOPIC = "#请插入话题名称#";
		private WriteBlog activity;

		public void insertText() {
			final EditText editText = activity.etMblog;
			final int selectionStart = editText.getSelectionStart();
			final int secletionEnd = editText.getSelectionEnd();
			final String text = editText.getText().toString();
			if (selectionStart == -1 || secletionEnd == -1
					|| selectionStart > secletionEnd) {
				activity.etMblog.append(TopicHelper.TOPIC);
				final int totalLength = activity.etMblog.getText().toString()
						.length();
				activity.etMblog.setSelection(
						totalLength - TopicHelper.TOPIC.length() + 1,
						totalLength - 1);
			}else {
				final StringBuilder sb = new StringBuilder();
				sb.append(text.substring(0, selectionStart));
				sb.append(TOPIC);
				sb.append(text.substring(secletionEnd));
				editText.setText(sb.toString());
				Selection.setSelection(editText.getText(), selectionStart + 1,
						selectionStart + TOPIC.length() - 1);
			}
		}

		TopicHelper(WriteBlog activity) {
			super();
			this.activity = activity;
		}
	}

	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if ((requestCode == WriteBlog.REQUEST_CODE_CAMERA)
				|| (WriteBlog.REQUEST_CODE_GALLERY == requestCode)) {
			if (resultCode != Activity.RESULT_OK) { return; }
			Uri uri = null;
			if ((requestCode == WriteBlog.REQUEST_CODE_CAMERA)
					&& FileUtils.doesExisted(sdcardTempFile)) {
				uri = Uri.fromFile(sdcardTempFile);
			}
			else if (WriteBlog.REQUEST_CODE_GALLERY == requestCode) {
				uri = data.getData();
			}

			if (uri != null) {
				displayImageUri(uri);
			}
		}
	}
	
	private void displayImageUri(Uri uri) {
		mImageLoadingHelper.importBitmapFile(uri);
		displayInsertBitmap();
	}
	
	void displayInsertBitmap() {
		insertPicView.setImageBitmap(mImageLoadingHelper
				.createThumbnail());
		insertPicView.setVisibility(View.VISIBLE);
	}
	
	void cancleLocated() {
		mLocationHelper.dispose();
		insertLocationView.setVisibility(View.GONE);
		pgLoadingLocation.setVisibility(View.GONE);
	}

	private void startToCameraActivity() {
		if (FileUtils.isSdCardAvailable()) {
			final Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			final Uri picUri = Uri.fromFile(sdcardTempFile);
			i.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
			startActivityForResult(i, WriteBlog.REQUEST_CODE_CAMERA);
		}
		else {
			Toast.makeText(this, R.string.pls_insert_sdcard, Toast.LENGTH_SHORT)
					.show();
		}
	}
	
	private void loadDraft() {
		String str = FileUtils.loadObject(DEFAULT_DRAFT_TEXT_PATH);
		etMblog.setText(str);
		etMblog.setSelection(str.length());
		
		if (mImageLoadingHelper.doesDraftExist()) {
			mImageLoadingHelper.loadDraft();
		}
		
	}
	
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		if (id == WriteBlog.DIALOG_ALERT_IMPORT) {
			dialog = new AlertDialog.Builder(this)
					.setTitle(R.string.menu_settings)
					.setItems(
							new CharSequence[] {
									getString(R.string.menu_camera),
									getString(R.string.menu_gallery) },
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									switch (which) {
										case 0:
											startToCameraActivity();
											break;

										case 1:
											startToMediaActivity();
											break;
									}
								}

							}).create();
		} else if (id == WriteBlog.DIALOG_ALERT_DELETE_LOCATION) {
			dialog = new AlertDialog.Builder(this)
					.setTitle(R.string.attention_all)
					.setMessage(R.string.delete_location_or_not)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									cancleLocated();
								}
							}).setNegativeButton(R.string.cancel, null)
					.create();
		}else if (id == WriteBlog.DIALOG_ALERT_DETELE_ALL_TEXT) {
			dialog = new AlertDialog.Builder(this)
			.setTitle(R.string.attention_all)
			.setMessage(R.string.delete_text)
			.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							etMblog.setText("");
						}
					}).setNegativeButton(R.string.cancel, null)
			.create();
		}	else if (id == WriteBlog.DIALOG_ALERT_OVERRIDE_DRAFT) {
			dialog = new AlertDialog.Builder(this)
			.setTitle(R.string.attention_all)
			.setMessage(R.string.override_draft_or_not)
			.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							saveDraft();
						}
					})
			.setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog,
								int which) {
							finish();
						}
					}).create();
		}else if (id == WriteBlog.DIALOG_ALERT_LOAD_DRAFT_CACHE) {
			dialog = new AlertDialog.Builder(this)
			.setTitle(R.string.attention_all)
			.setMessage(R.string.load_draft_or_not)
			.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							if (doesDraftExist()) {
								loadDraft();
							}
							// else {
							// loadShareImage();
							// }
						}
					})
			.setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog,
								int which) {
						}

					})
			.setNeutralButton(R.string.itemmenu_delete,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							Toast.makeText(getApplicationContext(),
									R.string.delete_draft,
									Toast.LENGTH_SHORT).show();
							deleteDraft();
						}

					}).setCancelable(false).create();
		}
		return dialog;
		
	}
	
	private boolean doesDraftExist() {
		return FileUtils.doesExisted(DEFAULT_DRAFT_TEXT_PATH);
	}
	
	boolean deleteDraft() {
		return FileUtils.deleteFile(DEFAULT_DRAFT_TEXT_PATH);
	}

	
	private void startToMediaActivity() {
		startActivityForResult(new Intent(Intent.ACTION_PICK,
				MediaStore.Images.Media.INTERNAL_CONTENT_URI),
				WriteBlog.REQUEST_CODE_GALLERY);
	}
	
	
	void setEmotionViewVisibility(boolean visibility) {
		if (mEmotionView != null) {
			mEmotionView.setVisibility(visibility ? View.VISIBLE : View.GONE);
		}
	}
	
	void displayFaceImageSrc() {
		if (ibFace != null) {
			ibFace.setImageResource(R.drawable.btn_insert_face);
		}
	}
	
	void displayKeyboardImageSrc() {
		if (ibFace != null) {
			ibFace.setImageResource(R.drawable.btn_insert_keyboard);
		}
	}
	
	boolean setInputMethodVisibility(boolean visibility) {
		if ((mInputMethodManager != null) && (etMblog != null)) {
			if (visibility) {
				mInputMethodManager.showSoftInput(etMblog, 0);
			}
			else {
				if (mInputMethodManager.isActive(etMblog)) {
					mInputMethodManager.hideSoftInputFromWindow(
							etMblog.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
					
				}
			}
		}
		return false;
	}
	
	
	final class LocationHelper {
		private LocationListener mLocationListener;
		private WriteBlog activity;
		private LocationManager mLocationManager;
		// 默认延迟。单位：秒
		private int timeout;
		private Handler uiHandler;
		private boolean isLocated;
		private LocationHolder holder;
		private boolean startFetching;
		private String defaultDraftPath;
		
		LocationHelper(WriteBlog activity) {
			this.activity = activity;
			mLocationManager = (LocationManager) activity
					.getSystemService(Context.LOCATION_SERVICE);
			uiHandler = new Handler();
			timeout = 60;
			defaultDraftPath = activity.getFilesDir().getAbsolutePath()
					+ "/draft/location.dat";
		}
		
		void dispose() {
			closeAll();
			isLocated = false;
			startFetching = false;
			holder = null;
		}
		
		
		void openAllProviders() {
			if (startFetching || isLocated) { return; }

		}
		
		
		boolean isFetching() {
			return startFetching;
		}
		
		boolean isLocatedSucess() {
			return (holder != null) && holder.isUseful();
		}
		
		void closeAll() {
			if (mLocationListener != null) {
				mLocationManager.removeUpdates(getLocationListener());
				mLocationListener = null;
			}
		}
		
		
		private LocationListener getLocationListener() {
			if (mLocationListener == null) {
				mLocationListener = new LocationListener() {

					public void onLocationChanged(Location location) {
						final LocationHolder holder = new LocationHolder();
						holder.lat = location.getLatitude();
						holder.lon = location.getLongitude();
						System.out.println("====holder.lat="+holder.lat+"lon="+holder.lon);
						if (holder.isUseful()) {
							isLocated = true;
							LocationHelper.this.holder = holder;
							activity.onLocatedSuccess();
							closeAll();
							startFetching = false;
						}
					}

					public void onProviderDisabled(String provider) {
					}

					public void onProviderEnabled(String provider) {
					}

					public void onStatusChanged(String provider, int status,
							Bundle extras) {
					}
				};
			}
			return mLocationListener;
		}
		
		

	}


	
	final class LocationHolder implements Serializable {
		private static final long serialVersionUID = -8205421689204807445L;
		/* package */double lat;
		/* package */double lon;

		public boolean equals(Object o) {
			if (o == null) { return false; }

			if (o == this) { return true; }

			if (o.getClass() == getClass()) {
				final LocationHolder holder = (LocationHolder) o;
				if ((Math.abs(holder.lat - lat) < .001)
						&& (Math.abs(holder.lon - lon) < .001)) { return true; }
			}
			return false;
		}

		public int hashCode() {
			final StringBuffer builder = new StringBuffer();
			builder.append(lat).append(lon);
			return builder.hashCode();
		}

		public String toString() {
			return ">>>>>>>>>> LocationHolder <<<<<<<<<<" + "\tlat:" + lat
					+ "\tlon:" + lon;
		}

		/* package */boolean isUseful() {
			return isIllegle(lat) && isIllegle(lon);
		}

		private boolean isIllegle(double pos) {
			if ((pos > 1.) || (pos < -1.)) { return true; }
			return false;
		}
	}


	final class FaceClickHealper {
		private boolean isFaceDiaplay = false;
		private WriteBlog activity;

		boolean isFaceDiaplay() {
			return isFaceDiaplay;
		}

		void onClick(View v) {
			activity.setEmotionViewVisibility(!isFaceDiaplay);
			if (isFaceDiaplay) {
				activity.displayFaceImageSrc();
			}
			else {
				activity.displayKeyboardImageSrc();
			}
			activity.setInputMethodVisibility(isFaceDiaplay);
			changeDiaplayFlag();
		}

		boolean onFinish() {
			if (isFaceDiaplay) {
				activity.displayFaceImageSrc();
				activity.setEmotionViewVisibility(false);
				changeDiaplayFlag();
				return true;
			}
			return false;
		}

		private void changeDiaplayFlag() {
			isFaceDiaplay = !isFaceDiaplay;
		}

		public FaceClickHealper(WriteBlog activity) {
			super();
			this.activity = activity;
		}
	}
	
	@Override
	public void afterTextChanged(Editable s) {
		
	}



	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		
	}



	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		
	}
}
