package com.example.shoplistdownload;


import android.app.ListActivity;
import android.content.AsyncQueryHandler;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ResourceCursorAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.example.shoplistdownload.Projections.Personal;
import com.example.view.SectionIndexerView;
import com.pic.optimize.R;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ContactsList extends ListActivity{
	/**
	 * The query token.
	 */
	protected static final int QUERY_TOKEN = 54;
	protected TextView mEmtytext;
	protected ProgressBar mLoadingBar;
	/**
	 * Keeps the cursor adapter.
	 */
	protected ContactsAdapter mAdapter;
	protected boolean mShowLoading = false;
	private static final String NONE_ENGLISH_LETTER_TITLE = "#";

	private Handler showLoadingHandler;
	protected static final int SHOW_PROGRESS_DELAY = 300;
	protected static final int EMPTY_MSG = 0;
	/**
	 * Keeps query handler.
	 */
	protected AsyncQueryHandler mQueryHandler;
	private static final String FIRST_ENGLISH_LETTER_PATTERN = "^[A-Za-z]";

	/** Regular expression object */
	private static final Pattern mPattern = Pattern.compile(FIRST_ENGLISH_LETTER_PATTERN);
	protected int mScrollState;
	protected static ExecutorService sImageFetchThreadPool;
	
	private SectionIndexerView mSectionIndexer;

	public static void startActivity(Context context) {
		Intent intent = new Intent();
		intent.setClass(context,ContactsList.class);
		context.startActivity(intent);
	}
	
	protected final class MyHandler extends AsyncQueryHandler {
		/**
		 * Asynchronous query handler constructor.
		 */
		public MyHandler(Context context) {
			super(context.getContentResolver());
		}
		
		/**
		 * On query completion.
		 */
		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			if (cursor == null || cursor.isClosed()) {
				return;
			}
			if (!isFinishing()) {
				setLoading(false);
				if (mAdapter != null) {
					mAdapter.setLoading(false);
					mAdapter.changeCursor(cursor);
				} 
				
				if (cursor.getCount() == 0) {
					mEmtytext.setVisibility(View.VISIBLE);
				} else {
					mEmtytext.setVisibility(View.INVISIBLE);
				}
			} else {
				if (cursor != null && !cursor.isClosed()) {
					cursor.close();
				}
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (mQueryHandler != null) {
			mQueryHandler.cancelOperation(QUERY_TOKEN);
			mQueryHandler = null;
		}
		
		if (mAdapter != null && mAdapter.mItemsMissingImages != null) {
			mAdapter.mItemsMissingImages.clear();
			mAdapter.clearMessages();
		}
		
		// Workaround for Android Issue 8488
		// http://code.google.com/p/android/issues/detail?id=8488
		if (mAdapter != null && mAdapter.mBitmapCache != null) {
			for (SoftReference<Bitmap> bitmap : mAdapter.mBitmapCache.values()) {
				if (bitmap != null && bitmap.get() != null) {
					bitmap.get().recycle();
					bitmap = null;
				}
			}
			mAdapter.mBitmapCache.clear();
		}

	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contacts_list_content);
		mEmtytext = (TextView) findViewById(R.id.emptyListText);
		mLoadingBar = (ProgressBar) findViewById(R.id.loading);
		
		mQueryHandler = new MyHandler(this);
		mAdapter = new ContactsAdapter(this);
		
		
		mSectionIndexer = (SectionIndexerView)findViewById(R.id.section_indexer_view);
		TextView textView = (TextView)findViewById(R.id.section_text);
		
		getListView().setOnScrollListener(mAdapter);
		setListAdapter(mAdapter);
		getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				try {
					Cursor cursor = (Cursor) getListAdapter().getItem(position);
					if (cursor == null) {
						return;
					}

					int contactId = cursor.getInt(Personal.ID_COLUMN_INDEX);
					Intent intent = new Intent();
					intent.setClass(ContactsList.this, ContactDetail.class);
					intent.putExtra("contactId",contactId);
					startActivity(intent);
				}catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		mSectionIndexer.init(getListView(), mAdapter, textView);
		startQuery();
	}

	
	protected void startQuery() {
		if (mAdapter != null) {
			mAdapter.setLoading(true);
		}

		setLoading(true);
		if (mQueryHandler == null) {
			return;
		}
		mQueryHandler.cancelOperation(QUERY_TOKEN);
		queryPersonal();
	}

	protected void setLoading(boolean enable) {
		if (enable) {
			mShowLoading = true;
			mEmtytext.setVisibility(View.GONE);
			new Thread("ContactsListAct:setLoading") {
				public void run() {
					try {
						sleep(SHOW_PROGRESS_DELAY);
					} catch (InterruptedException e) {
					}
					if (mShowLoading) {
						showLoadingHandler.sendEmptyMessage(EMPTY_MSG);
					}
				}

			}.start();
		} else {
			mShowLoading = false;
			mEmtytext.setVisibility(View.VISIBLE);
			mLoadingBar.setVisibility(View.GONE);
		}
	}
	
	protected final void queryPersonal() {
		mQueryHandler.startQuery(QUERY_TOKEN, null, ContactsContract.Contacts.CONTENT_URI, 
				Personal.CONTACTS_SUMMARY_PROJECTION, null, null, getSortOrder(ContactsContract.Contacts.DISPLAY_NAME));
	}

	
	protected static String getSortOrder(String fieldName) {

		return "CASE WHEN substr(UPPER(" + fieldName + "), 1, 1) BETWEEN 'A' AND 'Z' THEN 1 else 10 END," +
				fieldName + " COLLATE LOCALIZED ASC";
	}
	
	protected static final class SectionedContactListItemCache {
		/** Grouped section title.*/
		public TextView sectionHeader;
		/** Person name view.*/
		public TextView nameView;
		/** Person phone number view.*/
		public TextView typeView;
		/** Person photo cache holder.*/
		public ImageView photoView;
		/** Person details icon.*/
		public ImageView detailItemIcon;
        /** Container for name and type views **/
        public View nameTypeWrapper;
	}
	
	
	protected final class ContactsAdapter extends ResourceCursorAdapter implements SectionIndexer,OnScrollListener {

		protected boolean mLoading = true;
		private ContactSectionMapper mSectionMapper = null;
		private SectionTitle[] mSectionDatas = null;
		private static final int SECTION_COUNT = 27;
		protected HashMap<Long, SoftReference<Bitmap>> mBitmapCache = new HashMap<Long, SoftReference<Bitmap>>();
		protected HashSet<ImageView> mItemsMissingImages = new HashSet<ImageView>();
		protected ImageLoaderHandler mHandler;
		protected ImageLoader mImageFetcher;
		ViewTreeObserver.OnPreDrawListener mPreDrawListener;
		protected static final int FETCH_IMAGE_MSG = 1;
		
		public ContactsAdapter(Context context) {
			super(context, R.layout.contacts_list_item_photo,null);
			mHandler = new ImageLoaderHandler();

			mSectionDatas = new SectionTitle[SECTION_COUNT];
			for (int i = 0; i < SECTION_COUNT; i++) {
				mSectionDatas[i] = new SectionTitle();
			}
			mSectionMapper = new ContactSectionMapper(mSectionDatas);
		}
		
		
		protected void setLoading(boolean loading) {
			mLoading = loading;
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (SCROLL_STATE_TOUCH_SCROLL == scrollState) {
                View currentFocus = getCurrentFocus();
                if (currentFocus != null) {
                    currentFocus.clearFocus();
                }
            }
			
			mScrollState = scrollState;
			if (scrollState == OnScrollListener.SCROLL_STATE_FLING) {
				clearImageFetching();
			} else {
				processMissingImageItems(view);
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			
		}
		
		@Override
		public void changeCursor(Cursor c) {
			processCursor(c);
			super.changeCursor(c);
		}
		
		private void processCursor(Cursor c) {

			/** define some variables */
			SparseIntArray sectionPositionMap = new SparseIntArray();
			SparseIntArray positionSectionMap = new SparseIntArray();

			for(int i = 0; i < mSectionDatas.length; i++){
				mSectionDatas[i].title = "";
			}

			if (c == null || c.getCount() == 0 || c.isClosed()){
				mSectionMapper.changeData(sectionPositionMap, positionSectionMap);
				return;
			}
			
			String curtitle = "";

			int i = 0;
			int position = 0;
			while (c.moveToNext()) {
				position = c.getPosition();

				String curLetter = getTitle(getDisplayName(c));

				if (TextUtils.isEmpty(curtitle) || !TextUtils.equals(curLetter, curtitle)) {
					mSectionDatas[i].title = curLetter;
					sectionPositionMap.put(i, position);
					curtitle = curLetter;
					i++;
				}
				positionSectionMap.put(position, i - 1);
			}

			for(; i < mSectionDatas.length; i++){
				mSectionDatas[i].title = curtitle;
				sectionPositionMap.put(i, position);
			}
			mSectionMapper.changeData(sectionPositionMap, positionSectionMap);
		}
		
		protected String getTitle(String displayName) {
			String title;
			/** check if the first letter is English letter */
			Matcher matcher = mPattern.matcher(displayName);
			if (!matcher.find()) {
				title = NONE_ENGLISH_LETTER_TITLE;
			} else {
				title = displayName.trim().substring(0, 1).toUpperCase(Locale.US);
			}
			return title;
		}
		
		protected String getDisplayName(Cursor c) {
			
			String displayName =  c.getString(Personal.NAME_COLUMN_INDEX);
					
			if(TextUtils.isEmpty(displayName)) {
				return "";
			}
			
			return displayName;
		}
		

		@Override
		public Object[] getSections() {
			return mSectionMapper.getSections();
		}

		@Override
		public int getPositionForSection(int section) {
			return mSectionMapper.getPositionForSection(section);
		}

		@Override
		public int getSectionForPosition(int position) {
			return mSectionMapper.getSectionForPosition(position);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			final SectionedContactListItemCache cache = (SectionedContactListItemCache) view.getTag();
			cache.typeView.setVisibility(View.GONE);
			cache.photoView.setVisibility(View.VISIBLE);
			String name = cursor.getString(Personal.NAME_COLUMN_INDEX);
			if (TextUtils.isEmpty(name)) {
				cache.nameView.setText(R.string.contact_no_name);
			} else {
				cache.nameView.setText(name);
			}
			if(mSectionMapper != null  && mSectionMapper.isSection(cursor.getPosition())) {
				cache.sectionHeader.setVisibility(View.VISIBLE);
				cache.sectionHeader.setText(mSectionMapper.getSection(cursor.getPosition()));
				// Set section header unclickable
				cache.sectionHeader.setOnClickListener(null);
			}else{
				cache.sectionHeader.setVisibility(View.GONE);
			}
			setContactPhoto(cursor, cache.photoView, Personal.PHOTO_COLUMN_INDEX);
		}
		
		
		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			
			View view = super.newView(context, cursor, parent);
			final SectionedContactListItemCache cache = new SectionedContactListItemCache();
            cache.nameTypeWrapper = view.findViewById(R.id.name_type);
			cache.sectionHeader = (TextView) view.findViewById(R.id.txtSectionHeader);
			cache.nameView = (TextView) view.findViewById(R.id.name);
			cache.typeView = (TextView) view.findViewById(R.id.type);
			cache.photoView = (ImageView) view.findViewById(R.id.photo);
			cache.detailItemIcon = (ImageView) view.findViewById(R.id.contacts_detail_item_icon);
			view.setTag(cache);
			
			return view;
		}
		
		
		protected void setContactPhoto(Cursor cursor, final ImageView viewToUse, int column) {
			long photoId = 0;

			if (!cursor.isNull(column)) {
				photoId = cursor.getLong(column);
			}

			final int position = cursor.getPosition();
			viewToUse.setTag(new PhotoInfo(position, photoId));

			if (photoId == 0) {
				viewToUse.setImageResource(R.drawable.avatar);
			} else {

				Bitmap photo = null;

				SoftReference<Bitmap> ref = mBitmapCache.get(photoId);
				if (ref != null) {

					photo = ref.get();
					if (photo == null) {
						mBitmapCache.remove(photoId);
					}
				}

				if (photo != null) {
					viewToUse.setImageBitmap(photo);
				} else {
					viewToUse.setImageResource(R.drawable.avatar);
					mItemsMissingImages.add(viewToUse);
					if (mScrollState != OnScrollListener.SCROLL_STATE_FLING) {
						sendFetchImageMessage(viewToUse);
					}
				}
			}
		}
		
		private void processMissingImageItems(AbsListView view) {
			for (ImageView iv : mItemsMissingImages) {
				sendFetchImageMessage(iv);
			}
		}

		protected void sendFetchImageMessage(ImageView view) {
			final PhotoInfo info = (PhotoInfo) view.getTag();
			if (info == null) {
				return;
			}
			final long photoId = info.photoId;
			if (photoId == 0) {
				return;
			}
			mImageFetcher = new ImageLoader(photoId, view);
			synchronized (ContactsList.this) {
				if (sImageFetchThreadPool == null) {
					sImageFetchThreadPool = Executors.newFixedThreadPool(3);
				}
				sImageFetchThreadPool.execute(mImageFetcher);
			}
		}

		public void clearImageFetching() {
			synchronized (ContactsList.this) {
				if (sImageFetchThreadPool != null) {
					sImageFetchThreadPool.shutdownNow();
					sImageFetchThreadPool = null;
				}
			}

			mHandler.clearImageFecthing();
		}

		public void clearMessages() {
			if (mHandler != null) {
				try {
					mHandler.removeCallbacksAndMessages(null);
				} catch (java.lang.Throwable th) {
				}
				mHandler = null;
			}
		}
		
		//image downloader
		private class ImageLoaderHandler extends Handler {
			@Override
			public void handleMessage(Message message) {
				if (isFinishing()) {
					return;
				}
				switch (message.what) {
				case FETCH_IMAGE_MSG: {
					final ImageView imageView = (ImageView) message.obj;
					if (imageView == null) {
						break;
					}

					final PhotoInfo info = (PhotoInfo) imageView.getTag();
					if (info == null) {
						break;
					}

					final long photoId = info.photoId;
					if (photoId == 0) {
						break;
					}

					SoftReference<Bitmap> photoRef = mBitmapCache.get(photoId);
					if (photoRef == null) {
						break;
					}
					Bitmap photo = photoRef.get();
					if (photo == null) {
						mBitmapCache.remove(photoId);
						break;
					}

					synchronized (imageView) {
						final PhotoInfo updatedInfo = (PhotoInfo) imageView
								.getTag();
						long currentPhotoId = updatedInfo.photoId;
						if (currentPhotoId == photoId) {
							imageView.setImageBitmap(photo);
							mItemsMissingImages.remove(imageView);
						} else {
						}
					}
					break;
				}
				}
			}

			public void clearImageFecthing() {
				removeMessages(FETCH_IMAGE_MSG);
			}
		}

		private class ImageLoader implements Runnable {
			long mPhotoId;
			private ImageView mImageView;

			public ImageLoader(long photoId, ImageView imageView) {
				this.mPhotoId = photoId;
				this.mImageView = imageView;
			}

			public void run() {
				if (isFinishing()) {
					return;
				}

				if (Thread.interrupted()) {
					return;
				}

				if (mPhotoId < 0) {
					return;
				}

				Bitmap photo = ContactsUtils.getContactPhoto(getBaseContext(),
						mPhotoId, null);
				if (photo == null) {
					return;
				}

				mBitmapCache.put(mPhotoId, new SoftReference<Bitmap>(photo));

				if (Thread.interrupted()) {
					return;
				}

				Message msg = new Message();
				msg.what = FETCH_IMAGE_MSG;
				msg.obj = mImageView;
				mHandler.sendMessage(msg);
			}
		}
		
	}//end of adapter
	
	
	
	private static class ShowLoadingHandler extends Handler{
		private final SoftReference<ContactsList> mActivityRef;

        public ShowLoadingHandler(ContactsList activity){
			mActivityRef = new SoftReference<ContactsList>(activity);
        }
        
		public void handleMessage(Message msg) {
			ContactsList activity = mActivityRef.get();
			if (activity == null) {
				return;
			}
			
			ContactsAdapter adapter = activity.mAdapter;
			if (adapter == null) {
				return;
			}
			
			if (activity.mShowLoading) {
				activity.mEmtytext.setVisibility(View.GONE);
				if (adapter != null && (adapter.getCursor() == null 
						|| adapter.getCursor().isClosed()
						|| adapter.getCursor().getCount() <= 0)) {
					activity.mLoadingBar.setVisibility(View.VISIBLE);
				}
			}
		}
	};
	
    public class SectionTitle{
    	public String title;

    	public SectionTitle(){
    		title = "";
    	}
    	
    	public String toString(){
    		return title;
    	}
    }
	
    final static class PhotoInfo {
		public int position;
		public long photoId;

		public PhotoInfo(int position, long photoId) {
			this.position = position;
			this.photoId = photoId;
		}

		public ImageView photoView;
	}
    
	private class ContactSectionMapper implements SectionIndexer {

		private SectionTitle[] mSections = null;
		private SparseIntArray mSectionPositionMap = null;
		private SparseIntArray mPositionSectionMap = null;

		public ContactSectionMapper(SectionTitle[] sectionDatas) {
			mSections = sectionDatas;
		}
		
		public void changeData(SparseIntArray sectionPositionMap, SparseIntArray positionSectionMap) {
			mSectionPositionMap = sectionPositionMap;
			mPositionSectionMap = positionSectionMap;
		}
		
		@Override
		public Object[] getSections() {
			return mSections;
		}


		@Override
		public int getPositionForSection(int section) {
			if (mSectionPositionMap == null)
				return -1;

			
			if (section == 0)
				return -1;
			
			return mSectionPositionMap.get(section, -1);
		}


		@Override
		public int getSectionForPosition(int position) {
			if (mPositionSectionMap == null)
				return -1;

			if (position <= 0)
				return 0;
			
			return mPositionSectionMap.get(position, -1);
		}

		
		/**
		 * @param position
		 * @return
		 */
		public boolean isSection(int position) {
			if (position == 0)
				return true;
			
			int sectionIdx = getSectionForPosition(position);
			int sectionPosition = getPositionForSection(sectionIdx);

			if (sectionIdx == -1 && sectionPosition == -1)
				return false;
			
			return (position == sectionPosition);
		}

		public String getSection(int position) {
			if (mSections == null)
				return NONE_ENGLISH_LETTER_TITLE;

			int sectionIndex = getSectionForPosition(position);
			if (sectionIndex < 0 || sectionIndex >= mSections.length)
				return NONE_ENGLISH_LETTER_TITLE;
			
			return mSections[sectionIndex].toString();
		}

	}
	
	

}
