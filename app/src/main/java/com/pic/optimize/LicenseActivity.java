/**
 * Copyright (C) 2010-2012, RingCentral, Inc. 
 * All Rights Reserved.
 */

package com.pic.optimize;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LicenseActivity extends Activity {

    private TextView contextText;
    private static final String LOG = "[RC]LicenseActivity";
    //UI member variable
    private WebView mWebView;
    private ProgressBar mProgressBar;
    private TextView mPrompt;
    private RelativeLayout mLoadingLayout;
    private ImageView mNoInternetImageView;
    private String mLoadUrl;
    private boolean mSendingRequest = false;
    private boolean isLoadingError = false;

    public static void startActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context,LicenseActivity.class);
        context.startActivity(intent);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.licence_layout);

        initView();

    }

    private void initView() {
        mWebView = (WebView) findViewById(R.id.load_webView);
        mProgressBar = (ProgressBar) findViewById(R.id.load_progressBar);
        mLoadingLayout = (RelativeLayout) findViewById(R.id.loading_layout);
        mPrompt = (TextView) findViewById(R.id.prompt);
        mNoInternetImageView = (ImageView) findViewById(R.id.no_internet_img);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setUseWideViewPort(true);
        //   webSettings.setBuiltInZoomControls(true);
        mWebView.requestFocus();
        mWebView.loadUrl("file:///android_asset/software_licenses.html");
        initWebViewListener();


    }

    private void initWebViewListener() {
//            this.mWebView.setWebViewClient(new WebViewClient() {
//        });

        this.mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress >= 80) {
                    mWebView.setVisibility(View.VISIBLE);
                }
            }
        });

    }

}

