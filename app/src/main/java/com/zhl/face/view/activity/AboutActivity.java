package com.zhl.face.view.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.zhl.face.R;
import com.zhl.face.view.fragment.FmAbout;

public class AboutActivity extends ActivityFragment {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(getString(R.string.nav_about));
    }

    @Override
	protected Fragment getMainFragment() {
		return new FmAbout();
	}

}
