package com.zhl.face.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.orhanobut.logger.Logger;
import com.zhl.face.app.Constant;
import com.zhl.face.app.FaceApp;
import com.zhl.face.app.PreSetting;
import com.zhl.face.data.net.VolleyRest;
import com.zhl.face.data.net.response.Data;
import com.zhl.face.data.net.response.FaceResponse;
import com.zhl.face.interactor.DefaultSubscriber;
import com.zhl.face.interactor.GetNetFaceCase;
import com.zhl.face.model.FaceModel;
import com.zhl.face.model.SeriesModel;
import com.zhl.face.utils.FileUtils;
import com.zhl.face.utils.Utils;
import com.zhl.face.utils.ZipUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import cn.aigestudio.downloader.bizs.DLManager;
import cn.aigestudio.downloader.interfaces.DLTaskListener;

public class DownloadService extends Service{

    public static final int MAX_COUNT = 1;

    private PreSetting preSetting;

    private List<DownloadListener> downloadListeners = new ArrayList<>();
    private Queue<SeriesModel> seriesDownModels = new LinkedList<>();
    private List<String> seriesIds = new ArrayList<>();
    private List<FaceModel> faceModels;

    private DownloadBinder binder = new DownloadBinder();

    private int downloadingCount;

    public void addDownloadListener(DownloadListener downloadListener){
        downloadListeners.add(downloadListener);
    }

    public void removeDownloadListener(DownloadListener downloadListener){
        downloadListeners.remove(downloadListener);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        preSetting = PreSetting.getInstance();

    }

    public class DownloadBinder extends Binder{
        public DownloadService getService(){
            return DownloadService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void download(SeriesModel seriesModel){
        if (!seriesIds.contains(seriesModel.serId)){
            seriesDownModels.offer(seriesModel);
            seriesIds.add(seriesModel.serId);
        }
        execute();
    }

    public void download(SeriesModel seriesModel,List<FaceModel> faceModels){
        this.faceModels = faceModels;
        download(seriesModel);
    }

    public void execute(){
        if (downloadingCount < MAX_COUNT){
            if (!seriesDownModels.isEmpty()){
                final SeriesModel seriesModel = seriesDownModels.poll();
                if (seriesModel != null){
                    clearFile(seriesModel);
                    final String seriesId = seriesModel.serId;
                    seriesIds.remove(seriesId);
                    downloadingCount++;
                    DLManager.getInstance(FaceApp.getContext()).dlStart(seriesModel.downloadUrl,
                            FileUtils.DOWNLOAD_DIR,
                            new DLTaskListener() {
                                @Override
                                public void onProgress(int progress) {
                                    notifyProgress(seriesId, progress);
                                }

                                @Override
                                public void onFinish(final File file) {
                                    super.onFinish(file);
                                    Logger.i("download finish");
                                    onDownFinish(seriesModel, file);
                                }

                                @Override
                                public void onError(String error) {
                                    super.onError(error);
                                    Logger.e(error);
                                    notifyError(seriesId);
                                }
                            });
                }
            }
        }
    }

    private void onDownFinish(final SeriesModel seriesModel, final File file){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FileUtils.getInstance().createFloder(
                            FileUtils.SERIES_DIR + seriesModel.serName);
                    ZipUtils.UnZipFolder(file.getPath()
                            , FileUtils.SERIES_DIR + seriesModel.serName);
                    FileUtils.getInstance().deleteFile(file);
                    loadSeriesFaces(seriesModel);
                    preSetting.setSeriesDown(preSetting.getSeriesDown() + seriesModel.serId + ",");
                    downloadingCount--;
                    notifyFinish(seriesModel.serId, file);
                    execute();
                } catch (Exception e) {
                    e.printStackTrace();
                    notifyError(seriesModel.serId);
                }
            }
        }).start();
    }

    private void loadSeriesFaces(final SeriesModel seriesModel){
        if (faceModels == null){
            new GetNetFaceCase(0,0,seriesModel.serId,new VolleyRest())
                    .execute(new DefaultSubscriber<FaceResponse>(){
                        @Override
                        public void onNext(FaceResponse faceResponse) {
                            Data<FaceModel> data = faceResponse.data;
                            if (data != null){
                                int rc = data.rc;
                                if (rc == Constant.RC_SUCCESS && data.info != null){
                                    saveData(data.info,seriesModel);
                                }else {
                                }
                            }
                        }
                    });
        }else {
            saveData(faceModels,seriesModel);
            faceModels = null;
        }
    }

    private void saveData(final List<FaceModel> faceModels, final SeriesModel seriesModel){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String seriesFloder = FileUtils.SERIES_DIR+ seriesModel.serName;
                String coverPath = FileUtils.SERIES_DIR + seriesModel.serName + Constant.SERIES_COVER_NAME;
                seriesModel.coverPath = coverPath;
                SeriesModel.save(seriesModel);
                for (int i = 0; i < faceModels.size(); i++) {
                    FaceModel faceModel = faceModels.get(i);
                    String[] temp = faceModel.downloadUrl.split("/");
                    String name = temp[temp.length - 1];
                    String path = seriesFloder + "/" + name;
                    String thumbPath = FileUtils.SERIES_DIR + seriesModel.serName
                            + "/" + Utils.toMD5(faceModel.shortCutUrl);
                    faceModel.thumbPath = thumbPath;
                    faceModel.expressionPath = path;
                    faceModel.parentId = seriesModel.serId;
                    FileUtils.getInstance().createThumb(path, thumbPath);
                }
                FaceModel.saveAll(faceModels);
            }
        }).start();
    }

    private void notifyProgress(String seriesId,int progress){
        for (DownloadListener downloadListener : downloadListeners) {
            if (downloadListener != null){
                downloadListener.onProgress(seriesId,progress);
            }
        }
    }

    private void notifyFinish(String seriesId,File file){
        for (DownloadListener downloadListener : downloadListeners) {
            if (downloadListener != null){
                downloadListener.onFinish(seriesId, file);
            }
        }
    }

    private void notifyError(String seriesId){
        for (DownloadListener downloadListener : downloadListeners) {
            if (downloadListener != null){
                downloadListener.onError(seriesId);
            }
        }
    }

    private void clearFile(SeriesModel seriesModel){
        System.out.println("-------------------"+seriesModel.serId);
        FileUtils.getInstance().deleteFile(
                new File(FileUtils.DOWNLOAD_DIR + seriesModel.serId + ".zip"));
    }

    public interface DownloadListener{
        void onProgress(String seriesId,int progress);
        void onFinish(String seriesId,File file);
        void onError(String seriesId);
    }
}
