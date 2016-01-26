package com.zhl.face.view.activity;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.zhl.face.R;
import com.zhl.face.app.AppConfig;
import com.zhl.face.app.PreSetting;
import com.zhl.face.data.net.VolleyRest;
import com.zhl.face.interactor.GetKeyCase;
import com.zhl.face.presenter.SearchPresenter;
import com.zhl.face.utils.Utils;
import com.zhl.face.view.fragment.FmFaceSeries;
import com.zhl.face.view.iview.ISearchView;
import com.zhl.face.view.widget.FlowLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SearchActivity extends BaseSwipeBack implements ISearchView,View.OnClickListener{

    private static final int ANIM_DURATION   = 500;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.rootView)
    View rootView;
    @InjectView(R.id.mainFragment)
    View mainFragment;
    @InjectView(R.id.layoutSearch)
    View layoutSearch;
    @InjectView(R.id.etSearch)
    EditText etSearch;
    @InjectView(R.id.imgSearch)
    ImageView imgSearch;
    @InjectView(R.id.flowLayout)
    FlowLayout flowLayout;

    private List<TextView> tvLables = new ArrayList<>();

    private ActionBar actionBar;
    private MenuItem searchItem;

    private FragmentManager fragmentManager;
    private SearchPresenter presenter;
    private FmFaceSeries fmFaceSeries;
    private Activity activity;
    private PreSetting preSetting;

    private InputMethodManager imm;

    private boolean isSearch = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.inject(this);

        activity = this;

        setupActionBar();

        imgSearch.setOnClickListener(this);
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    search(etSearch.getText().toString());
                }
                return true;
            }
        });

        fragmentManager = getSupportFragmentManager();

        presenter = new SearchPresenter(this,new GetKeyCase(new VolleyRest()));
        presenter.getKeys();

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        preSetting = PreSetting.getInstance();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        searchItem = menu.getItem(0);
        searchItem.setVisible(!isSearch);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            showSearch(true);
            searchItem.setVisible(false);
            actionBar.setTitle(getString(R.string.title_search));
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupActionBar(){
        Utils.setColorStatusBar(this, R.color.colorPrimaryDark, rootView);

        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.title_search));
    }


    @Override
    public void showKeys() {
        for (int i = 0; i < presenter.searchKeys.length; i++) {
            addTvKey(presenter.searchKeys[i]);
        }
    }

    private void addTvKey(String key){
        TextView textView = new TextView(activity);
        FlowLayout.LayoutParams layoutParams = new FlowLayout.LayoutParams(
                FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin   = 5;
        layoutParams.rightMargin  = 5;
        layoutParams.topMargin    = 5;
        layoutParams.bottomMargin = 5;
        textView.setText(key);
        textView.setLayoutParams(layoutParams);
        textView.setBackgroundResource(R.drawable.shape_bg_btn);
        textView.setOnClickListener((View.OnClickListener) activity);
        textView.setId(R.id.key);
        flowLayout.addView(textView);
        tvLables.add(textView);
    }

    /**
     * 开始搜索，显示表情和系列的列表各自发送请求
     * @param text 搜索词
     */
    private void search(String text){
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(this, R.string.toast_search_null, Toast.LENGTH_SHORT).show();
        }else {
            MobclickAgent.onEvent(this, AppConfig.EVENT_SEARCH);
            preSetting.setSearch(preSetting.getSearch()+text+",");
            actionBar.setTitle(text);
            fmFaceSeries = FmFaceSeries.newInstance(text);
            showSearch(false);
            imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
//            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void showSearch(boolean isShow){
        if (isShow){
            etSearch.setText("");
            isSearch = true;
            layoutSearch.setVisibility(View.VISIBLE);
            layoutSearch.animate().scaleY(1)
                    .setInterpolator(new OvershootInterpolator())
                    .setDuration(ANIM_DURATION).withEndAction(new Runnable() {

                @Override
                public void run() {
                    flowLayout.setVisibility(View.VISIBLE);
                    flowLayout.animate().alpha(1).setDuration(ANIM_DURATION).start();
                }
            }).start();
            mainFragment.animate().alpha(0)
                    .setDuration(ANIM_DURATION).withEndAction(new Runnable() {

                @Override
                public void run() {
                    fragmentManager.beginTransaction().remove(fmFaceSeries).commit();
                    fmFaceSeries = null;
                }
            }).start();
            searchItem.setVisible(false);
        }else {
            isSearch = false;
            flowLayout.animate().alpha(0).setDuration(ANIM_DURATION)
                    .withEndAction(new Runnable() {

                        @Override
                        public void run() {
                            flowLayout.setVisibility(View.GONE);
                        }
                    }).start();
            layoutSearch.animate().scaleY(0).withEndAction(new Runnable() {

                @Override
                public void run() {
                    layoutSearch.setVisibility(View.GONE);
                    fragmentManager.beginTransaction()
                            .replace(R.id.mainFragment, fmFaceSeries).commit();
                    mainFragment.animate().alpha(1)
                            .setDuration(ANIM_DURATION).start();
                }
            })
                    .setDuration(ANIM_DURATION).start();
            searchItem.setVisible(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgSearch:
                search(etSearch.getText().toString());
                break;
            case R.id.key:
                search(((TextView)v).getText().toString());
                break;
            default:
                break;
        }
    }
}
