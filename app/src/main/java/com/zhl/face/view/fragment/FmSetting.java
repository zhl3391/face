package com.zhl.face.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.support.v4.preference.PreferenceFragment;

import com.zhl.face.R;
import com.zhl.face.app.PreSetting;
import com.zhl.face.service.SerFloat;
import com.zhl.face.view.activity.SettingActivity;

public class FmSetting extends PreferenceFragment implements OnPreferenceChangeListener,
	OnPreferenceClickListener{

	private CheckBoxPreference mCbSlideLeft;
	private CheckBoxPreference mCbSlideRight;
//	private CheckBoxPreference mCbFloatView;
//    private Preference         mFloatTip;

	private PreSetting mPreSetting;

	private SettingActivity mActivity;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPreSetting = PreSetting.getInstance();
		mActivity   =  (SettingActivity) getActivity();

		initUi();
	}

	private void initUi() {
		addPreferencesFromResource(R.xml.setting);

		mCbSlideLeft = (CheckBoxPreference) findPreference(
				getString(R.string.key_setting_slide_left));
		mCbSlideRight = (CheckBoxPreference) findPreference(
				getString(R.string.key_setting_slide_right));
//		mCbFloatView = (CheckBoxPreference) findPreference(
//				getString(R.string.key_setting_floatview));
//        mFloatTip = findPreference(
//                getString(R.string.key_setting_float_tip));

		mCbSlideLeft.setChecked(mPreSetting.isSlideLeftOn());
		mCbSlideRight.setChecked(mPreSetting.isSlideRightOn());
//		mCbFloatView.setChecked(mPreSetting.getFloatView());
		mCbSlideLeft.setOnPreferenceChangeListener(this);
		mCbSlideRight.setOnPreferenceChangeListener(this);
//		mCbFloatView.setOnPreferenceChangeListener(this);
//        mFloatTip.setOnPreferenceClickListener(this);
	}

	@Override
	public boolean onPreferenceChange(Preference arg0, Object arg1) {
		if (arg0 == mCbSlideLeft || arg0 == mCbSlideRight) {
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					mActivity.resetSlide();
				}
			}, 200);
		}
//		if (arg0 == mCbFloatView) {
//			if ((Boolean)arg1) {
//				mActivity.startService(new Intent(mActivity, SerFloat.class));
//			}else {
//				mActivity.stopService(new Intent(mActivity, SerFloat.class));
//			}
//		}
		return true;
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
//		if (preference == mFloatTip){
//            showFloatTip();
//        }
        return false;
	}

    private void showFloatTip(){
//        new MaterialDialog.Builder(mActivity)
//                .title("就像这样")
//                .customView(R.layout.layout_float_introduce,false)
//                .show();
    }

}
