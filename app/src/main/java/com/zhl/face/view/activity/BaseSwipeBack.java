package com.zhl.face.view.activity;

import android.os.Bundle;
import android.view.MenuItem;

import com.umeng.analytics.MobclickAgent;
import com.zhl.face.app.PreSetting;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class BaseSwipeBack extends SwipeBackActivity {

	protected SwipeBackLayout mSwipeBackLayout;

	protected PreSetting mPreSetting;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mPreSetting = PreSetting.getInstance();

		mSwipeBackLayout = getSwipeBackLayout();
		mSwipeBackLayout.setEdgeSize(40);
		resetSlide();
	}
	
	public void resetSlide(){
		if (mPreSetting.isSlideLeftOn()) {
			mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
		}
		if (mPreSetting.isSlideRightOn()) {
			mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_RIGHT);
		}
		if (mPreSetting.isSlideLeftOn() && mPreSetting.isSlideRightOn()) {
			mSwipeBackLayout.setEdgeTrackingEnabled(
					SwipeBackLayout.EDGE_LEFT|SwipeBackLayout.EDGE_RIGHT);
		}
		if (!mPreSetting.isSlideLeftOn() && !mPreSetting.isSlideRightOn()) {
			mSwipeBackLayout.setEnableGesture(false);
		}else {
			mSwipeBackLayout.setEnableGesture(true);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
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
}
