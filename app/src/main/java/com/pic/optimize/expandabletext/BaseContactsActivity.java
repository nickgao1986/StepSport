package com.pic.optimize.expandabletext;


import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import com.pic.optimize.R;


import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.ResourceCursorAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;


public abstract class BaseContactsActivity extends ListActivity{

	/**
	 * Keeps the cursor adapter.
	 */
	protected BaseContactsAdapter mAdapter;
	protected int mScrollState;
	private static final String TAG = "[RC] BaseContactsActivity";
	protected static ExecutorService sImageFetchThreadPool;
	protected Toast mToast;
	/**
	 * Mask for indicate work with contacts
	 */
	protected static final int MODE_MASK_CONTACTS = 0x00000000;
	/**
	 * Mask for extension mode
	 */
	protected static final int MODE_MASK_EXTENSION = 0x80000000;
	/**
	 * Mask for photo Show mode
	 */
	protected static final int MODE_MASK_SHOW_PHOTOS = 0x40000000;

	protected int mMode = MODE_DEFAULT;
	/**
	 * Default mode
	 */
	protected static final int MODE_DEFAULT = MODE_MASK_CONTACTS;
	/**
	 * Item Photo info class
	 */
	final static class PhotoInfo {
		public int position;
		public long photoId;

		public PhotoInfo(int position, long photoId) {
			this.position = position;
			this.photoId = photoId;
		}

		public ImageView photoView;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
	}
	// ==================================================================================================
	// ContactsAdapter
	// ==================================================================================================

	/**
	 * Cursor adapter.
	 */
	protected abstract class BaseContactsAdapter extends ResourceCursorAdapter implements OnScrollListener {

		protected HashMap<Long, SoftReference<Bitmap>> mBitmapCache = new HashMap<Long, SoftReference<Bitmap>>();
		protected HashSet<ImageView> mItemsMissingImages = new HashSet<ImageView>();
		protected ImageLoaderHandler mHandler;
		protected ImageLoader mImageFetcher;
		ViewTreeObserver.OnPreDrawListener mPreDrawListener;
		protected static final int FETCH_IMAGE_MSG = 1;
		protected boolean mDisplayPhotos = false;

		/**
		 * Defines id data are loading.
		 */
		protected boolean mLoading = true;
		

		/**
		 * Default constructor.
		 */
		public BaseContactsAdapter(Context context, int resourceId) {
			super(context, resourceId, null);
			mHandler = new ImageLoaderHandler();
			mDisplayPhotos = true;
		}
		
		/**
		 * Sets data loading state.
		 * 
		 * @param loading
		 *            the loading state
		 */
		protected void setLoading(boolean loading) {
			mLoading = loading;
		}

		
		/**
		 * Defines if there are data.
		 */
		@Override
		public boolean isEmpty() {
			if (mLoading) {
				return false;
			} else {
				return super.isEmpty();
			}
		}
	
		// =====Photo Loader Helper ==========================================
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
						final PhotoInfo updatedInfo = (PhotoInfo) imageView.getTag();
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

				Bitmap photo = DataBaseUtil.getContactPhoto(getBaseContext(), mPhotoId, null);
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

		public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {

		}

		public void onScrollStateChanged(AbsListView view, int scrollState) {
			
			if (SCROLL_STATE_TOUCH_SCROLL == scrollState) {
                View currentFocus = getCurrentFocus();
                if (currentFocus != null) {
                    currentFocus.clearFocus();
                }
            }
			
			mScrollState = scrollState;
			if ((mMode & MODE_MASK_EXTENSION) != MODE_MASK_EXTENSION) {
				if (scrollState == OnScrollListener.SCROLL_STATE_FLING) {
					clearImageFetching();
				} else if (mDisplayPhotos) {
					processMissingImageItems(view);
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
			synchronized (BaseContactsActivity.this) {
				if (sImageFetchThreadPool == null) {
					sImageFetchThreadPool = Executors.newFixedThreadPool(3);
				}
				sImageFetchThreadPool.execute(mImageFetcher);
			}
		}

		public void clearImageFetching() {
			synchronized (BaseContactsActivity.this) {
				if (sImageFetchThreadPool != null) {
					sImageFetchThreadPool.shutdownNow();
					sImageFetchThreadPool = null;
				}
			}

			mHandler.clearImageFecthing();
		}

		protected void setContactPhoto(Cursor cursor, final ImageView viewToUse, int column) {
			long photoId = 0;

			if (!cursor.isNull(column)) {
				photoId = cursor.getLong(column);
			}

			final int position = cursor.getPosition();
			viewToUse.setTag(new PhotoInfo(position, photoId));

			if (photoId == 0) {
				viewToUse.setImageResource(R.drawable.ic_contact_list_picture);
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
					viewToUse.setImageResource(R.drawable.ic_contact_list_picture);
					mItemsMissingImages.add(viewToUse);
					if (mScrollState != OnScrollListener.SCROLL_STATE_FLING) {
						sendFetchImageMessage(viewToUse);
					}
				}
			}
		}
	}
}
