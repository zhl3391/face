package com.zhl.face.view.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.zhl.face.R;
import com.zhl.face.view.fragment.FmSetting;

public class SettingActivity extends ActivityFragment {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle(getString(R.string.nav_setting));
    }

    @Override
    protected Fragment getMainFragment() {
        return new FmSetting();
    }

}
