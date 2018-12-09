package com.pic.optimize;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

public class DialogTest extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Dialog dialog = getEditCustomDialog();
//        dialog.show();
//
//        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
//        lp.width = DialogTest.this.getResources().getDimensionPixelSize(R.dimen.dialog_width);
//        lp.height = DialogTest.this.getResources().getDimensionPixelSize(R.dimen.dialog_height);
//        dialog.getWindow().setAttributes(lp);


        Dialog dialog = getEditCustomDialog("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("TAG","<<<<left onclick");
            }
        }, "Upgrade Now", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("TAG","<<<<right onclick");
            }
        });
        //是否可以点击硬件的back让弹出框消失
        dialog.setCancelable(true);
        //是否点击弹出框的空白部分可以让弹出框消失
        dialog.setCanceledOnTouchOutside(true);

    }

    public AlertDialog getEditCustomDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.test_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(DialogTest.this);
        builder.setView(view);
        builder.setTitle("A New Version is Available");
        return builder.create();
    }

    public AlertDialog getEditCustomDialog(String leftText,android.content.DialogInterface.OnClickListener leftlistener, String rightText, android.content.DialogInterface.OnClickListener rightlistener) {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.test_dialog1, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(DialogTest.this);
        if (!TextUtils.isEmpty(leftText)) {
            builder.setNegativeButton(leftText, leftlistener);
        }

        if (!TextUtils.isEmpty(rightText)) {
            builder.setPositiveButton(rightText, rightlistener);
        }

        builder.setView(view);
        builder.setTitle("A New Version is Available");
        return builder.create();
    }


    public static void startActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context,DialogTest.class);
        context.startActivity(intent);
    }
}
