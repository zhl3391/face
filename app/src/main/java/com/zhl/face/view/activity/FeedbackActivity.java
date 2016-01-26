package com.zhl.face.view.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.umeng.fb.fragment.FeedbackFragment;
import com.zhl.face.R;

public class FeedbackActivity extends ActivityFragment{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle(getString(R.string.nav_feedback));
    }

    @Override
    protected Fragment getMainFragment() {
        String conversation_id = getIntent().getStringExtra(
                FeedbackFragment.BUNDLE_KEY_CONVERSATION_ID);
        return FeedbackFragment.newInstance(conversation_id);
    }
}
