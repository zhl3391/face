package com.zhl.face.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.zhl.face.R;
import com.zhl.face.utils.Utils;
import com.zhl.face.view.adapter.DownloadPagerAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DownLoadActivity extends BaseSwipeBack{

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.tabLayout)
    TabLayout tabLayout;
    @InjectView(R.id.viewPager)
    ViewPager viewPager;
    @InjectView(R.id.rootView)
    View rootView;

    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        ButterKnife.inject(this);

        setupActionBar();
        setupViewPager();
    }

    private void setupViewPager(){
        DownloadPagerAdapter adapter = new DownloadPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabsFromPagerAdapter(adapter);
    }

    private void setupActionBar(){
        Utils.setColorStatusBar(this, R.color.colorPrimaryDark, rootView);

        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.nav_download));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            startActivity(new Intent(DownLoadActivity.this,SearchActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
