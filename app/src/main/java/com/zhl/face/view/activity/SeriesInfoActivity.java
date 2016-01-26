package com.zhl.face.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.zhl.face.R;
import com.zhl.face.app.Constant;
import com.zhl.face.data.net.VolleyRest;
import com.zhl.face.interactor.GetNetFaceCase;
import com.zhl.face.model.FaceModel;
import com.zhl.face.model.IFaceGridShow;
import com.zhl.face.model.SeriesModel;
import com.zhl.face.presenter.SeriesInfoPresenter;
import com.zhl.face.utils.Utils;
import com.zhl.face.view.fragment.FmDownFaceList;
import com.zhl.face.view.fragment.FmFace;
import com.zhl.face.view.iview.ISeriesInfoView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SeriesInfoActivity extends ActivityFragment implements ISeriesInfoView{

    public static final String KEY_SERIES = "series";

    private SeriesModel seriesModel;

    private SeriesInfoPresenter seriesInfoPresenter;

    private FmDownFaceList fmDownFaceList;

    private MenuItem menuItemDownload;

    public static Intent buildIntent(Context context,SeriesModel seriesModel){
        Intent intent = new Intent(context,SeriesInfoActivity.class);
        intent.putExtra(KEY_SERIES, seriesModel);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        seriesModel = (SeriesModel) intent.getSerializableExtra(KEY_SERIES);
        String seriesName = seriesModel.serName;
        String seriesSize = Utils.getSeriesSize(seriesModel.size);
        super.onCreate(savedInstanceState);
        if (seriesModel.isDowned){
            actionBar.setTitle(seriesName+"(已下载)");
        }else {
            actionBar.setTitle(seriesName+seriesSize);
        }

        seriesInfoPresenter = new SeriesInfoPresenter(this);
        seriesInfoPresenter.bindDownloadService(this);

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setMax(100);
    }

    @Override
    protected Fragment getMainFragment() {
        if (seriesModel.isDowned){
            return FmFace.newInstance(seriesModel.serId,false);
        }else {
            fmDownFaceList = FmDownFaceList.newInstance(seriesModel.serId);
            return fmDownFaceList;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (seriesModel.isDowned){
            return super.onCreateOptionsMenu(menu);
        }
        getMenuInflater().inflate(R.menu.menu_download,menu);
        menuItemDownload = menu.getItem(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_download:
                if (progressBar.getProgress() == 0){
                    List<FaceModel> faceModels = fmDownFaceList.getFaces();
                    if (!faceModels.isEmpty()){
                        seriesInfoPresenter.downloadSeries(seriesModel, faceModels);
                    }
                }
                menuItemDownload.setVisible(false);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        seriesInfoPresenter.destroy();
    }

    @Override
    public void showDownloaded(String seriesId) {
        if (seriesId.equals(seriesModel.serId)){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(SeriesInfoActivity.this, R.string.download_suc, Toast.LENGTH_SHORT).show();
                }
            });
            finish();
        }
    }

    @Override
    public void showProgress(String seriesId,int progress) {
        if (seriesId.equals(seriesModel.serId)){
            progressBar.setProgress(progress);
        }
    }
}
