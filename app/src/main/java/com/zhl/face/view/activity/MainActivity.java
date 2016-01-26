package com.zhl.face.view.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.orhanobut.logger.Logger;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.onlineconfig.OnlineConfigAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;
import com.zhl.face.R;
import com.zhl.face.app.AppConfig;
import com.zhl.face.app.Constant;
import com.zhl.face.app.PreSetting;
import com.zhl.face.data.net.RestApi;
import com.zhl.face.data.net.VolleyRest;
import com.zhl.face.interactor.DefaultSubscriber;
import com.zhl.face.interactor.PostDownload;
import com.zhl.face.interactor.PostSearch;
import com.zhl.face.model.SeriesModel;
import com.zhl.face.service.DownloadService;
import com.zhl.face.service.SerFloat;
import com.zhl.face.utils.FileUtils;
import com.zhl.face.utils.Utils;
import com.zhl.face.view.adapter.MyFacePagerAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener{

    @InjectView(R.id.navigation_left)
    NavigationView navigationLeft;
//    @InjectView(R.id.navigation_right)
//    NavigationView navigationRight;
    @InjectView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.tabLayout)
    TabLayout tabLayout;
    @InjectView(R.id.viewPager)
    ViewPager viewPager;
    @InjectView(R.id.rootView)
    View rootView;

    private ImageView imgMark;
    private ViewGroup decorView;
    private Bitmap bitmapMark;

    private ActionBar actionBar;
    private ActionBarDrawerToggle drawerToggle;

    private PreSetting preSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        preSetting = PreSetting.getInstance();

        Utils.setColorStatusBar(this, R.color.colorPrimaryDark, rootView);

        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar
                ,R.string.drawer_open,R.string.drawer_close);
        drawerToggle.syncState();
        drawerLayout.setDrawerListener(drawerToggle);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
        drawerLayout.setDrawerShadow(me.imid.swipebacklayout.lib.R.drawable.shadow_right, Gravity.LEFT);

        SeriesModel seriesModel = new SeriesModel();
        seriesModel.serId = AppConfig.DEFAULT_SERIES_ID;
        seriesModel.serName = AppConfig.DEFAULT_SERIES_NAME;
        SeriesModel.save(seriesModel);

        setupViewPager();
        setupNavigation();

        decorView = (ViewGroup) getWindow().getDecorView();
        imgMark = new ImageView(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        imgMark.setLayoutParams(params);
        decorView.addView(imgMark);

        initUmeng();
        initConfig();

        postDownload();
        postSearch();

        Intent intent = new Intent(this, DownloadService.class);
        startService(intent);

        if (preSetting.getFloatView()) {
            Intent floatView = new Intent(this, SerFloat.class);
            startService(floatView);
        }

        FileUtils.getInstance().assetsToSd(this, AppConfig.IMG_SHARE_NAME,
                FileUtils.DOWNLOAD_DIR + AppConfig.IMG_SHARE_NAME);

        if (preSetting.isFirstOpen()){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    drawerLayout.openDrawer(Gravity.LEFT);
                    preSetting.setIsFirstOpen(false);
                }
            },500);

        }

    }

    /**
     * 提交下载量
     */
    private void postDownload() {
        String faceIds = preSetting.getFaceDown();
        String seriesIds = preSetting.getSeriesDown();
        RestApi restApi = new VolleyRest();
        if (!TextUtils.isEmpty(faceIds)){
            PostDownload face = new PostDownload(PostDownload.TYPE_FACE,faceIds,restApi);
            face.execute(new DefaultSubscriber<Integer>(){
                @Override
                public void onNext(Integer rc) {
                    if (rc == Constant.RC_SUCCESS){
                        preSetting.setFaceDown("");
                    }
                }
            });
        }
        if (!TextUtils.isEmpty(seriesIds)){
            PostDownload series = new PostDownload(PostDownload.TYPE_SERIES,seriesIds,restApi);
            series.execute(new DefaultSubscriber<Integer>(){
                @Override
                public void onNext(Integer rc) {
                    if (rc == Constant.RC_SUCCESS){
                        preSetting.setSeriesDown("");
                    }
                }
            });
        }
    }

    /**
     * 提交搜索词
     */
    private void postSearch(){
        if (preSetting.getIsPostSearch() == 1){
            String search = preSetting.getSearch();
            RestApi restApi = new VolleyRest();
            if (!TextUtils.isEmpty(search)){
                PostSearch postSearch = new PostSearch(search,restApi);
                postSearch.execute(new DefaultSubscriber<Integer>(){
                    @Override
                    public void onNext(Integer rc) {
                        if (rc == Constant.RC_SUCCESS){
                            preSetting.setSearch("");
                        }
                    }
                });
            }
        }
    }

    private void initUmeng(){
//        MobclickAgent.updateOnlineConfig(this);
        OnlineConfigAgent.getInstance().updateOnlineConfig(this);
        AnalyticsConfig.enableEncrypt(true);
        UmengUpdateAgent.setUpdateOnlyWifi(false);
        UmengUpdateAgent.update(this);
        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
            @Override
            public void onUpdateReturned(int i, UpdateResponse updateResponse) {
                preSetting.setIsUpdate(i == UpdateStatus.Yes);
            }
        });
    }

    private void initConfig(){
        String shareText = OnlineConfigAgent.getInstance().getConfigParams(
                this, AppConfig.PARAM_SHARE_TEXT);
        preSetting.setShareText(shareText);
        Logger.i(shareText);

        String isIntroduce = OnlineConfigAgent.getInstance().getConfigParams(
                this, AppConfig.PARAM_IS_INTRODUCE);
        if (!TextUtils.isEmpty(isIntroduce)){
            preSetting.setIsIntroduce(Integer.valueOf(isIntroduce));
        }

        String isPostSearch = OnlineConfigAgent.getInstance().getConfigParams(
                this,AppConfig.PARAM_IS_POST_SEARCH);
        if (!TextUtils.isEmpty(isPostSearch)){
            preSetting.setIsPostSearch(Integer.valueOf(isPostSearch));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (int i = 0;i < decorView.getChildCount();i++){
            decorView.getChildAt(i).setVisibility(View.VISIBLE);
        }
        imgMark.setVisibility(View.GONE);
        MobclickAgent.onResume(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        decorView.destroyDrawingCache();
        decorView.buildDrawingCache();
        decorView.setDrawingCacheEnabled(true);
        bitmapMark = decorView.getDrawingCache();
        if (bitmapMark != null && !bitmapMark.isRecycled()){
            imgMark.setImageBitmap(bitmapMark);
            for (int i = 0;i < decorView.getChildCount();i++){
                decorView.getChildAt(i).setVisibility(View.GONE);
            }
            imgMark.setVisibility(View.VISIBLE);
            imgMark.bringToFront();
        }
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FileUtils.getInstance().clearTemp();
        if (bitmapMark != null){
            bitmapMark.recycle();
            bitmapMark = null;
            decorView.destroyDrawingCache();
        }
    }

    private void setupViewPager(){
        MyFacePagerAdapter adapter = new MyFacePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabsFromPagerAdapter(adapter);
    }

    private void setupNavigation(){
        navigationLeft.setNavigationItemSelectedListener(this);
//        navigationRight.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem menuItem) {
        drawerLayout.closeDrawers();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (menuItem.getItemId()){
                    case R.id.nav_feedback:
                        FeedbackAgent agent = new FeedbackAgent(MainActivity.this);
                        agent.startFeedbackActivity();
//                        startActivity(new Intent(MainActivity.this,FeedbackActivity.class));
                        MobclickAgent.onEvent(MainActivity.this, AppConfig.EVENT_NAV_FEEDBACK);
                        break;
                    case R.id.nav_about:
                        startActivity(new Intent(MainActivity.this,AboutActivity.class));
                        MobclickAgent.onEvent(MainActivity.this, AppConfig.EVENT_NAV_ABOUT);
                        break;
                    case R.id.nav_setting:
                        startActivity(new Intent(MainActivity.this,SettingActivity.class));
                        MobclickAgent.onEvent(MainActivity.this, AppConfig.EVENT_NAV_SETTING);
                        break;
                    case R.id.nav_download:
                        startActivity(new Intent(MainActivity.this,DownLoadActivity.class));
                        MobclickAgent.onEvent(MainActivity.this, AppConfig.EVENT_NAV_DOWNLOAD);
                    default:
                        break;
                }
            }
        },300);
        return true;
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
            startActivity(new Intent(MainActivity.this,SearchActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
