package com.zhl.face.view.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.zhl.face.R;
import com.zhl.face.utils.AnimHelper;
import com.zhl.face.utils.Utils;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class WebActivity extends BaseSwipeBack {

	public static final String KEY_URL = "url";

	@InjectView(R.id.toolbar)
	Toolbar toolbar;
	@InjectView(R.id.rootView)
	View mRootView;
	@InjectView(R.id.markLayout)
	View mMarkLayout;
	@InjectView(R.id.webView)
	WebView mWebView;

	private String mUrl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web);

		ButterKnife.inject(this);

		Intent intent = getIntent();
		mUrl = intent.getStringExtra(KEY_URL);
		
		initUi();
		
		mWebView.setWebViewClient(new WebViewClient(){
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				AnimHelper.fadeIn(mMarkLayout, null);
			}
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				getSupportActionBar().setTitle(view.getTitle());
				AnimHelper.fadeOut(mMarkLayout, null);
			}
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
				Toast.makeText(WebActivity.this, description, Toast.LENGTH_SHORT).show();
			}
		});
		mWebView.loadUrl(mUrl);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	@Override
	public void onBackPressed() {
		if (!back()) {
			super.onBackPressed();
		}
	}
	
	private void initUi(){

        setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		mMarkLayout.setAlpha(0);
		Utils.setColorStatusBar(this, R.color.colorPrimary, mRootView);
	}
	
	/**
     * 返回处理
     * @return
     */
    protected boolean back(){
        if(mWebView.canGoBack()){
            mWebView.goBack();
            return true;
        }
        return false;
    }
}
