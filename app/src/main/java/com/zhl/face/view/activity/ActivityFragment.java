package com.zhl.face.view.activity;

import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.umeng.analytics.MobclickAgent;
import com.zhl.face.R;
import com.zhl.face.utils.SystemBarTintManager;
import com.zhl.face.utils.Utils;

import butterknife.ButterKnife;
import butterknife.InjectView;

public abstract class ActivityFragment extends BaseSwipeBack {

	@InjectView(R.id.rootView)
	protected View rootView;
	@InjectView(R.id.toolbar)
    protected Toolbar toolbar;
	@InjectView(R.id.progressBar)
	protected ProgressBar progressBar;

	protected ActionBar actionBar;
    private SystemBarTintManager mTintManager;
    private ColorDrawable mColorDrawable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragment);
		ButterKnife.inject(this);

        setSupportActionBar(toolbar);
		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ab_back);

		mTintManager = Utils.setColorStatusBar(this, R.color.colorPrimaryDark, rootView);
		
		mColorDrawable = new ColorDrawable(getResources().getColor(R.color.colorPrimary));
        toolbar.setBackground(mColorDrawable);

		Fragment fragment = getMainFragment();
		if (fragment != null) {
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction()
			.replace(R.id.mainFragment, fragment).commit();
		}
	}
	
	protected abstract Fragment getMainFragment();

	public void changeFragment(Fragment fragment){
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.mainFragment, fragment).commit();
	}

	public void setStatusBarColor(int color){
		if (mTintManager != null) {
			mTintManager.setStatusBarTintColor(color);
		}
	}
	
	public void setActionBarColor(int color){
		mColorDrawable.setColor(color);
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
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
}
