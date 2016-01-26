package com.zhl.face.presenter;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.zhl.face.app.Constant;
import com.zhl.face.app.PreSetting;
import com.zhl.face.data.net.VolleyRest;
import com.zhl.face.data.net.response.Data;
import com.zhl.face.data.net.response.SeriesResponse;
import com.zhl.face.interactor.DefaultSubscriber;
import com.zhl.face.interactor.GetNetSeriesCase;
import com.zhl.face.model.SeriesModel;
import com.zhl.face.service.DownloadService;
import com.zhl.face.utils.Utils;
import com.zhl.face.view.iview.IDownSeriesListView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeriesDownPresenter implements IPresenter,DownloadService.DownloadListener{

    private IDownSeriesListView iDownSeriesListView;
    private GetNetSeriesCase getNetSeriesCase;

    public List<SeriesModel> seriesModelList = new ArrayList<>();
    private Map<String,Integer> positionMap = new HashMap<>();
    private Data data;

    public SeriesModel selectSeries;
    private PreSetting preSetting;
    private DownloadService downloadService;
    private Activity activity;

    public boolean isHasMore;
    private boolean isFirstLoad;
    public boolean isLoadMore;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DownloadService.DownloadBinder binder = (DownloadService.DownloadBinder) service;
            if (binder != null){
                downloadService = binder.getService();
                downloadService.addDownloadListener(SeriesDownPresenter.this);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public SeriesDownPresenter(IDownSeriesListView iDownSeriesListView,GetNetSeriesCase getNetSeriesCase){
        this.iDownSeriesListView = iDownSeriesListView;
        this.getNetSeriesCase = getNetSeriesCase;
        preSetting = PreSetting.getInstance();
    }

    public void init(){
        isFirstLoad = true;
        loadSeries();
    }

    public void loadSeries(){
        iDownSeriesListView.showLoading(true);
        getNetSeriesCase.execute(new SeriesDownSubscriber());
    }

    public void loadMore(){
        isLoadMore = true;
        if (data != null){
            getNetSeriesCase.setPage(++data.page);
        }
        loadSeries();
    }

    public void bindDownloadService(Activity activity){
        this.activity = activity;
        Intent service = new Intent(activity, DownloadService.class);
        activity.bindService(service, connection, Context.BIND_AUTO_CREATE);
    }

    public void downloadSeries(int position,SeriesModel seriesModel){
        if (downloadService != null){
            downloadService.download(seriesModel);
        }
    }

    private void setSeriesDowned(List<SeriesModel> seriesModels){
        for (int i=0; i<seriesModels.size(); i++){
            SeriesModel seriesModel = seriesModels.get(i);
            if (SeriesModel.isExist(seriesModel.serId)){
                seriesModel.isDowned = true;
            }
        }
    }

    private void setPositionMap(List<SeriesModel> seriesModels){
        for (int i=0; i<seriesModels.size(); i++){
            SeriesModel seriesModel = seriesModels.get(i);
            positionMap.put(seriesModel.serId,seriesModelList.size() + i);
        }
    }

    @Override
    public void resume() {
        if (!isFirstLoad){
            setSeriesDowned(seriesModelList);
            iDownSeriesListView.showSeriesList();
        }else {
            isFirstLoad = false;
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {
        getNetSeriesCase.unsubscribe();
        if(downloadService != null){
            downloadService.removeDownloadListener(this);
        }
        if (activity != null){
            activity.unbindService(connection);
        }

    }

    @Override
    public void onProgress(String seriesId, int progress) {
        int position = getPosition(seriesId);
        if (position < 0 || seriesModelList.isEmpty() || position > seriesModelList.size()){
            return;
        }
        seriesModelList.get(position).progress = progress;
        iDownSeriesListView.showProgress(position,progress);
    }

    @Override
    public void onFinish(String seriesId, File file) {
        int position = getPosition(seriesId);
        if (position < 0 || seriesModelList.isEmpty() || position > seriesModelList.size()) {
            return;
        }
        seriesModelList.get(position).progress = 0;
        seriesModelList.get(position).isDowned = true;
        iDownSeriesListView.showDownloaded(position);
        Utils.notifyMediaUpdate();
    }


    @Override
    public void onError(String seriesId) {

    }

    private int getPosition(String seriesId){
        if (positionMap.containsKey(seriesId)){
            return positionMap.get(seriesId);
        }else {
            return -1;
        }
    }

    private final class SeriesDownSubscriber extends DefaultSubscriber<SeriesResponse>{
        @Override
        public void onNext(SeriesResponse seriesResponse) {
            super.onNext(seriesResponse);
            data = seriesResponse.data;
            if (data != null){
                isHasMore = data.page < data.totalPage;
                int rc = data.rc;
                switch (rc){
                    case Constant.RC_SUCCESS:
                        if (data.info != null){
                            setSeriesDowned(data.info);
                            setPositionMap(data.info);
                            seriesModelList.addAll(data.info);
                            iDownSeriesListView.showLoading(false);
                            iDownSeriesListView.showSeriesList();
                        }else {
                            iDownSeriesListView.showError(true);
                        }
                        break;
                    case Constant.RC_SUCCESS_NULL:
                        iDownSeriesListView.showEmpty(true);
                        break;
                    case Constant.RC_SUCCESS_FAIL:
                        iDownSeriesListView.showError(true);
                        break;
                }
                if (!isHasMore){
                    iDownSeriesListView.showNoMore();
                }
            }else {
                iDownSeriesListView.showError(true);
            }
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            iDownSeriesListView.showError(true);
        }
    }
}
