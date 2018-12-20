package com.pic.optimize.export;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class ShareActivity extends Activity {

    public static final String SCHEME_FILE = "file";
    public static final String SCHEME_CONTENT = "content";
    String FILE_EXT;
    private Uri mImportingUri;
    private String fromFileName = "";
    private long fromFileSize = 0;
    private String tmpPath = "/tmp";
    private File toFile = null;

    public static void startActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context,ShareActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = this.getIntent();
        int flags = intent.getFlags();
        if ((flags & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) == 0) {
            if (intent.getAction() != null && Intent.ACTION_VIEW.equals(intent.getAction())) {
                if (SCHEME_FILE.equals(intent.getScheme()) || SCHEME_CONTENT.equals(intent.getScheme())) {

                    String i_type = getIntent().getType();
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    mImportingUri = intent.getData();
                    System.out.println("====mImportingUri=" + mImportingUri);

                    InputStream is = null;
                    try {
                        is = getContentResolver().openInputStream(mImportingUri);
                    } catch (Exception e) {
                        System.out.println("====e=" + e);
                    }

                    if (mImportingUri != null && SCHEME_FILE.equalsIgnoreCase(mImportingUri.getScheme())) {
                        //Is file
                        startToCopyFile(is);
                    } else if (mImportingUri != null && SCHEME_CONTENT.equalsIgnoreCase(mImportingUri.getScheme())) {
                        startCopyMedia(is);
                    }

                }
            }
        }

    }

    public static void makesureFileExist(String path) {
        String separator = File.separator;
        int index = path.lastIndexOf(separator);
        path = path.substring(0, index);
        File f = new File(path);
        f.mkdirs();

        File f1 = new File(path);
        try {
            f1.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
        }
        if (sdDir != null) {
            return sdDir.toString();
        } else
            return null;

    }


    private String getName(Uri uri) {
        String str = uri.toString();
        int index = str.lastIndexOf(File.separator);
        String name = str.substring(index, str.length());
        return name;
    }




    private boolean startCopyMedia(InputStream is) {
        Cursor c = null;
        try {
            c = getContentResolver().query(mImportingUri,
                    new String[]{MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns.SIZE},
                    null, null, null);
            if (c != null && c.moveToFirst()) {
                int dn_index = c.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);
                fromFileName = c.getString(dn_index);
                int s_index = c.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE);
                fromFileSize = c.getLong(s_index);
            }
        } catch (Exception e) {
            System.out.println("===query e=" + e);
        }
        return true;
    }

    private boolean startToCopyFile(InputStream is) {

        String fileName = getSDPath() + tmpPath + File.separator + getName(mImportingUri);
        makesureFileExist(fileName);
        File toFile = new File(fileName);
        CopyThread mCopyThread = new CopyThread(is, toFile);
        new Thread(mCopyThread).start();
        return true;
    }


    private class CopyThread implements Runnable {

        private File toFile;
        private InputStream fosfrom = null;


        public CopyThread(InputStream fosfrom, File toFile) {
            this.fosfrom = fosfrom;
            this.toFile = toFile;
        }

        @Override
        public void run() {
            try {
                TimeUnit.MILLISECONDS.sleep(800);

                FileInputStream fosfrom = null;
                if (this.fosfrom != null) {
                    fosfrom = (FileInputStream) this.fosfrom;
                }
                FileOutputStream fosto = new FileOutputStream(toFile);
                byte bt[] = new byte[1024];
                int c;
                int time = 0;
                while ((c = fosfrom.read(bt)) > 0) {
                    fosto.write(bt, 0, c);
                }
                if (fosfrom != null) {
                    fosfrom.close();
                }
                fosto.close();

            } catch (Exception e) {
                return;
            } finally {
                try {
                    if (this.fosfrom != null) {
                        this.fosfrom.close();
                    }
                } catch (IOException e) {
                }
            }
        }
    }

}
