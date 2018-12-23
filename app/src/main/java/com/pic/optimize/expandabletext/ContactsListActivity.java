package com.pic.optimize.expandabletext;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.AsyncQueryHandler;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.example.shoplistdownload.Projections;
import com.pic.optimize.R;


public class ContactsListActivity extends BaseContactsActivity {

	/**
	 * Keeps query handler.
	 */
	protected AsyncQueryHandler mQueryHandler;
	private static final String TAG = "[RC]ContactsListActivity";
	/** Title for names which first letter is none English letter */
	private static final String NONE_ENGLISH_LETTER_TITLE = "#";

	/** Show all contacts */
	protected static final int MODE_CONTACTS = 1 | MODE_MASK_CONTACTS
			| MODE_MASK_SHOW_PHOTOS;
	/**
	 * The query token.
	 */
	protected static final int QUERY_TOKEN = 54;


	/**
	 * Regular expression to detect if contact name's first letter is English
	 * letter
	 */
	private static final String FIRST_ENGLISH_LETTER_PATTERN = "^[A-Za-z]";

	/** Regular expression object */
	private static final Pattern mPattern = Pattern
			.compile(FIRST_ENGLISH_LETTER_PATTERN);

	public final class QueryHandler extends AsyncQueryHandler {

		public QueryHandler(Context context) {
			super(context.getContentResolver());
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {

			if (cursor == null) {
				return;
			}

			if (!isFinishing()) {
				if (mAdapter != null) {
					mAdapter.changeCursor(cursor);
				}
			} else {
				if (cursor != null && !cursor.isClosed()) {
					cursor.close();
				}
			}

		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.contacts_list_content);
		mQueryHandler = new QueryHandler(this);
		/**
		 * Set Contacts adapter
		 */
		mAdapter = new ContactsAdapter(this);
	    getListView().setOnScrollListener(mAdapter);
	    setListAdapter(mAdapter);
		queryPersonal();
	}

	/**
	 * Query under personal mode
	 */
	protected final void queryPersonal() {
		mQueryHandler.cancelOperation(QUERY_TOKEN);
		mQueryHandler.startQuery(QUERY_TOKEN, null,
				ContactsContract.Contacts.CONTENT_URI,
				Projections.Personal.CONTACTS_SUMMARY_PROJECTION, null, null,
				getSortOrder(ContactsContract.Contacts.DISPLAY_NAME));
	}

	/**
	 * Get sorting String for Contacts display
	 */
	protected static String getSortOrder(String fieldName) {
		return "CASE WHEN substr(UPPER(" + fieldName
				+ "), 1, 1) BETWEEN 'A' AND 'Z' THEN 1 else 10 END,"
				+ fieldName + " COLLATE LOCALIZED ASC";
	}

	/**
	 * Define the common action of grouped mode and ungrouped mode.
	 */
	public interface ListDisplayMode {
		public int getItemViewType(int position);

		public int getViewTypeCount();

		public boolean isSection(int position);
	}

	/**
	 * Define the single section data. This is to avoid the section data not
	 * refreshing bug in android(Fixed in above 4.0 version)
	 */
	private class SectionTitle {
		public String title;

		public SectionTitle() {
			title = "";
		}

		public String toString() {
			return title;
		}
	}

	/**
	 * Item view cache holder.
	 */
	protected static final class SectionedContactListItemCache {
		/** Grouped section title. */
		public TextView sectionHeader;
		/** Person name view. */
		public TextView nameView;
		/** Person phone number view. */
		public TextView typeView;
		/** Person photo cache holder. */
		public ImageView photoView;
		/** Person details icon. */
		public ImageView detailItemIcon;
	}

	private class ContactSectionMapper implements SectionIndexer {

		private SectionTitle[] mSections = null;
		private SparseIntArray mSectionPositionMap = null;
		private SparseIntArray mPositionSectionMap = null;

		public ContactSectionMapper(SectionTitle[] sectionDatas) {
			mSections = sectionDatas;
		}

		public void changeData(SparseIntArray sectionPositionMap,
				SparseIntArray positionSectionMap) {
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

	/**
	 * Cursor adapter.
	 */
	protected final class ContactsAdapter extends BaseContactsAdapter implements
			SectionIndexer {

		/**
		 * Implement related function when the list need to be displayed in
		 * group.
		 */
		private class GroupMode implements ListDisplayMode {
			/** Item view type of personal without section header */
			private static final int VIEW_TYPE_PERSONAL = 0;
			/** Item view type of company without section header */
			private static final int VIEW_TYPE_EXTENSION = 1;
			/** Item view type of personal with section header */
			private static final int VIEW_TYPE_PERSONAL_SECTION = 2;
			/** Item view type of company with section */
			private static final int VIEW_TYPE_EXTENSION_SECTION = 3;
			/** Total numbers of modes */
			private static final int MODE_NUMBERS = 4;

			@Override
			public int getItemViewType(int position) {
				if (mSectionMapper == null) {
					return -1;
				}
				if (mSectionMapper.isSection(position)) {
					if (mMode == MODE_CONTACTS) {
						return VIEW_TYPE_PERSONAL_SECTION;
					}
				} else {
					if (mMode == MODE_CONTACTS) {
						return VIEW_TYPE_PERSONAL;
					}
				}

				return -1;
			}

			@Override
			public int getViewTypeCount() {
				return MODE_NUMBERS;
			}

			@Override
			public boolean isSection(int position) {
				return mSectionMapper != null
						&& mSectionMapper.isSection(position);
			}

		}

		/**
		 * Implement related function when the list need't to be displayed in
		 * group.
		 */
		private class UngroupMode implements ListDisplayMode {
			/** Item view type of personal without section header*/
			private static final int VIEW_TYPE_PERSONAL = 0;
			/** Item view type of company without section header*/
			private static final int VIEW_TYPE_EXTENSION = 1;
			/** Total numbers of modes */
			private static final int MODE_NUMBERS = 2;

			@Override
			public int getItemViewType(int position) {
				if (mMode == MODE_CONTACTS) {
					return VIEW_TYPE_PERSONAL;
				}

				return -1;
			}

			@Override
			public int getViewTypeCount() {
				return MODE_NUMBERS;
			}

			@Override
			public boolean isSection(int position) {
				return false;
			}

		}
		
		private ContactSectionMapper mSectionMapper = null;
		private final ListDisplayMode mGroupMode = new GroupMode();
		private ListDisplayMode mDisplayMode = null;
		private SectionTitle[] mSectionDatas = null;
		private static final int SECTION_COUNT = 27;
		private final ListDisplayMode mUngroupMode = new UngroupMode();
		/**
		 * Default constructor.
		 */
		public ContactsAdapter(Context context) {
			super(context, R.layout.contacts_list_item_photo);

			mDisplayMode = mGroupMode;
			mSectionDatas = new SectionTitle[SECTION_COUNT];
			for (int i = 0; i < SECTION_COUNT; i++) {
				mSectionDatas[i] = new SectionTitle();
			}
			mSectionMapper = new ContactSectionMapper(mSectionDatas);
		}

		@Override
		public int getItemViewType(int position) {
			return mDisplayMode.getItemViewType(position);
		}

		@Override
		public int getViewTypeCount() {
			return mDisplayMode.getViewTypeCount();
		}

		@Override
		public Object getItem(int position) {
			return super.getItem(position);
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
		public View newView(Context context, Cursor cursor, ViewGroup parent) {

			View view = super.newView(context, cursor, parent);
			final SectionedContactListItemCache cache = new SectionedContactListItemCache();
			cache.sectionHeader = (TextView) view
					.findViewById(R.id.txtSectionHeader);
			cache.nameView = (TextView) view.findViewById(R.id.name);
			cache.typeView = (TextView) view.findViewById(R.id.type);
			cache.photoView = (ImageView) view.findViewById(R.id.photo);
			cache.detailItemIcon = (ImageView) view
					.findViewById(R.id.contacts_detail_item_icon);
			view.setTag(cache);

			return view;
		}

		@Override
		public void bindView(View view, Context context, final Cursor cursor) {
			final SectionedContactListItemCache cache = (SectionedContactListItemCache) view
					.getTag();

			if (mDisplayMode.isSection(cursor.getPosition())) {
				cache.sectionHeader.setVisibility(View.VISIBLE);
				cache.sectionHeader.setText(mSectionMapper.getSection(cursor
						.getPosition()));
				// Set section header unclickable
				cache.sectionHeader.setOnClickListener(null);
			} else {
				cache.sectionHeader.setVisibility(View.GONE);
			}
			cache.typeView.setVisibility(View.GONE);
			cache.photoView.setVisibility(View.VISIBLE);

			String name = cursor.getString(Projections.Personal.NAME_COLUMN_INDEX);
			if (TextUtils.isEmpty(name)) {
				cache.nameView.setText(R.string.contact_no_name);
			} else {
				cache.nameView.setText(name);
			}

			// Set the photo, if requested
			if (mDisplayPhotos) {
				setContactPhoto(cursor, cache.photoView, Projections.Personal.PHOTO_COLUMN_INDEX);
			}
		}

		@Override
		public void changeCursor(Cursor c) {
			processCursor(c);
			changeDisplayMode(false);
			super.changeCursor(c);
		}

		protected String getDisplayName(Cursor c) {

			String displayName = c.getString(Projections.Personal.NAME_COLUMN_INDEX);

			if (TextUtils.isEmpty(displayName)) {
				return "";
			}

			return displayName;
		}
		
		public void changeDisplayMode(boolean isGroup) {
			if (isGroup)
				mDisplayMode = mGroupMode;
			else
				mDisplayMode = mUngroupMode;
		}

		protected String getTitle(String displayName) {
			String title;
			/** check if the first letter is English letter */
			Matcher matcher = mPattern.matcher(displayName);
			if (!matcher.find()) {
				title = NONE_ENGLISH_LETTER_TITLE;
			} else {
				title = displayName.trim().substring(0, 1)
						.toUpperCase(Locale.US);
			}
			return title;
		}

		/**
		 * Process cursor to extract useful information into some data structure
		 */
		private void processCursor(Cursor c) {

			/** define some variables */
			SparseIntArray sectionPositionMap = new SparseIntArray();
			SparseIntArray positionSectionMap = new SparseIntArray();

			for (int i = 0; i < mSectionDatas.length; i++) {
				mSectionDatas[i].title = "";
			}

			if (c == null || c.getCount() == 0 || c.isClosed()) {
				mSectionMapper.changeData(sectionPositionMap,
						positionSectionMap);
				return;
			}

			String curtitle = "";

			int i = 0;
			int position = 0;
			while (c.moveToNext()) {
				position = c.getPosition();

				String curLetter = getTitle(getDisplayName(c));

				/** check if it is needed to insert a section title */
				if (TextUtils.isEmpty(curtitle)
						|| !TextUtils.equals(curLetter, curtitle)) {
					mSectionDatas[i].title = curLetter;
					sectionPositionMap.put(i, position);
					curtitle = curLetter;
					i++;
				}
				positionSectionMap.put(position, i - 1);
			}

			for (; i < mSectionDatas.length; i++) {
				mSectionDatas[i].title = curtitle;
				sectionPositionMap.put(i, position);
			}
			mSectionMapper.changeData(sectionPositionMap, positionSectionMap);
		}

	}

}
