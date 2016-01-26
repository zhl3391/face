package com.zhl.face.presenter;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.zhl.face.model.FaceModel;
import com.zhl.face.model.SeriesModel;
import com.zhl.face.service.DownloadService;
import com.zhl.face.view.iview.ISeriesInfoView;

import java.io.File;
import java.util.List;

public class SeriesInfoPresenter implements IPresenter,DownloadService.DownloadListener{


    private ISeriesInfoView iSeriesInfoView;

    private DownloadService downloadService;
    private Activity activity;


    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DownloadService.DownloadBinder binder = (DownloadService.DownloadBinder) service;
            if (binder != null){
                downloadService = binder.getService();
                downloadService.addDownloadListener(SeriesInfoPresenter.this);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public SeriesInfoPresenter(ISeriesInfoView iSeriesInfoView){
        this.iSeriesInfoView = iSeriesInfoView;
    }

    public void bindDownloadService(Activity activity){
        this.activity = activity;
        Intent service = new Intent(activity, DownloadService.class);
        activity.bindService(service, connection, Context.BIND_AUTO_CREATE);
    }

    public void downloadSeries(SeriesModel seriesModel,List<FaceModel> faces){
        if (downloadService != null){
            downloadService.download(seriesModel,faces);
        }
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {
        if(downloadService != null){
            downloadService.removeDownloadListener(this);
        }
        if (activity != null){
            activity.unbindService(connection);
        }
    }

    @Override
    public void onProgress(String seriesId, int progress) {
        iSeriesInfoView.showProgress(seriesId,progress);
    }

    @Override
    public void onFinish(String seriesId, File file) {
        iSeriesInfoView.showDownloaded(seriesId);
    }

    @Override
    public void onError(String seriesId) {

    }
}
