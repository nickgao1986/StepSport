package com.pic.optimize;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.model.MessageFaceModel;

public class InputFaceActivity extends Activity {

    private MessageFaceModel mMessageFaceModel = MessageFaceModel.getInstance();
    public static final int SELECT_STATE_FACE_ICON = 209;
    public static final int SELECT_MESSAGE_FACE_ICON = 109;
    private int mWidth = 0;

    public static void startActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context,InputFaceActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        MessageFaceModel.getInstance().init(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mWidth = this.getResources().getDimensionPixelSize(R.dimen.image_width);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        setContentView(R.layout.input_face_activity);
        GridView gridView = (GridView) findViewById(R.id.input_face_gridview);
        gridView.setAdapter(new FaceListAdapter());
        gridView.setOnItemClickListener(new FaceListOnItemClickListener());

        Button cancelButton = (Button)findViewById(R.id.input_face_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                finish();
            }

        });

    }

    private class FaceListAdapter extends BaseAdapter {

        public int getCount() {
            if(mMessageFaceModel.getFaceIcons() != null){
                return mMessageFaceModel.getFaceIcons().size();
            }else{
                return 0;
            }
        }

        public Object getItem(int arg0) {
            return arg0;
        }

        public long getItemId(int arg0) {
            return arg0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            ImageView view = new ImageView(InputFaceActivity.this);
            view.setImageBitmap(mMessageFaceModel.getFaceIcons().get(position));

            view.setLayoutParams(new GridView.LayoutParams(mWidth, mWidth));
            view.setScaleType(ImageView.ScaleType.CENTER);
            return view;
        }

    }

    private class FaceListOnItemClickListener implements AdapterView.OnItemClickListener {

        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {

            InputFaceActivity.this.finish();
        }

    }


}
