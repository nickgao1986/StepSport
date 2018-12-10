package com.example.shoplistdownload;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Window;
import android.widget.ImageView;

import com.pic.optimize.R;

import java.io.IOException;
import java.io.InputStream;

public class ContactDetail extends Activity {

    private ImageView contact_photo;

    public static void startActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context,ContactDetail.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.contact_detail);

        int contactId = getIntent().getIntExtra("contactId", 0);

        contact_photo = (ImageView)findViewById(R.id.contact_photo);

        loadTask task = new loadTask(contactId);
        task.execute();

    }


    private class loadTask extends AsyncTask<Void, Void, Bitmap> {

        public loadTask(int id) {
            contactId = id;
        }
        private int contactId;

        @Override
        protected Bitmap doInBackground(Void... params) {
            InputStream inputStream = openDisplayPhoto(contactId);

            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inSampleSize = 1;
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, opt);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {

            if(result != null) {
                contact_photo.setImageBitmap(result);
            }
            super.onPostExecute(result);
        }

    }

    /**
     * 这个是取到清晰图的inputStream的代码
     * @param contactId
     * @return
     */
    public InputStream openDisplayPhoto(long contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri displayPhotoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.DISPLAY_PHOTO);
        try {
            AssetFileDescriptor fd =
                    this.getContentResolver().openAssetFileDescriptor(displayPhotoUri, "r");
            return fd.createInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
