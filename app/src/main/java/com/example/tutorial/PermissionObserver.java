package com.example.tutorial;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;


public class PermissionObserver extends BroadcastReceiver {
    Context mContext;
    private PermissionChangedListener mPermissionChangedListener;
    public static final String ACTION_PERMISSION_CHANGED = "com.example.tutorial.intent.action.permission.changed";
    
    
    PermissionObserver(Context context, PermissionChangedListener listener) {
        mContext = context;
        mPermissionChangedListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (mPermissionChangedListener != null) {
            mPermissionChangedListener.onPermissionChanged();
        }
    }

    public void register() {
        mContext.registerReceiver(this, new IntentFilter(ACTION_PERMISSION_CHANGED));
    }

    public void unregister() {
        mContext.unregisterReceiver(this);
    }

}
