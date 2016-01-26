package com.zhl.face.presenter;

import com.zhl.face.app.AppConfig;
import com.zhl.face.helper.SendHelper;
import com.zhl.face.interactor.DefaultSubscriber;
import com.zhl.face.interactor.GetDbFaceCase;
import com.zhl.face.model.EnumSendWay;
import com.zhl.face.model.FaceModel;
import com.zhl.face.model.FavoriteModel;
import com.zhl.face.model.IFaceGridShow;
import com.zhl.face.model.SeriesModel;
import com.zhl.face.utils.FileUtils;
import com.zhl.face.view.iview.IFaceGridView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FacePresenter implements IPresenter {

    private IFaceGridView iFaceGridView;
    private GetDbFaceCase getDbFaceCase;

    public List<IFaceGridShow> faceModelList = new ArrayList<>();
    public List<FaceModel> selectFaceList = new ArrayList<>();

    private SendHelper sendHelper;
    private FileUtils fileUtils;

    private boolean isFavorite;
    private boolean isFirstLoad;
    private boolean isRefresh;

    public FacePresenter(IFaceGridView iFaceGridView,GetDbFaceCase getDbFaceCase,boolean isFavorite){
        this.iFaceGridView = iFaceGridView;
        this.getDbFaceCase = getDbFaceCase;
        this.isFavorite = isFavorite;
        sendHelper = SendHelper.getInstance();
        fileUtils = FileUtils.getInstance();
    }

    public void init(){
        isFirstLoad = true;
        loadFace();
    }

    public void loadFace(){
        getDbFaceCase.execute(new FaceSubscriber());
    }

    @Override
    public void resume() {
        if (!isFirstLoad){
            isRefresh = true;
            loadFace();
        }else {
            isFirstLoad = false;
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {
        getDbFaceCase.unsubscribe();
    }


    public void deleteSeries(String seriesId){
        for (FaceModel faceModel : FaceModel.getFaces(seriesId)){
            FavoriteModel.deleteFavorite(faceModel.expressionId);
        }
        if (!seriesId.equals(AppConfig.DEFAULT_SERIES_ID)){
            SeriesModel seriesModel = SeriesModel.findSeries(seriesId);
            if (seriesModel != null){
                fileUtils.deleteFile(new File(FileUtils.SERIES_DIR+seriesModel.serName));
            }
            SeriesModel.delete(seriesId);
        }else {
            fileUtils.deleteFile(new File(FileUtils.FACE_DIR));
            fileUtils.createFloder(FileUtils.FACE_DIR);
        }
        FaceModel.deleteSeriesFace(seriesId);

    }

    public void deleteSelectFaces(){
        if (isFavorite){
            FavoriteModel.deleteAllFavorite(selectFaceList);
        }else {
            FaceModel.deleteAll(selectFaceList);
            for (FaceModel faceModel : selectFaceList) {
                deleteFaceFile(faceModel);
            }
        }
        faceModelList.removeAll(selectFaceList);
        selectFaceList.clear();
        if (faceModelList.isEmpty()){
            iFaceGridView.showEmpty(true);
        }
    }

    public void deleteFace(int position){
        FaceModel faceModel = (FaceModel) faceModelList.get(position);
        if (isFavorite){
            FavoriteModel.deleteFavorite(faceModel.expressionId);
        }else {
            FavoriteModel.deleteFavorite(faceModel.expressionId);
            FaceModel.delete(faceModel.expressionId);
            deleteFaceFile(faceModel);
        }
        faceModelList.remove(faceModel);
        if (faceModelList.isEmpty()){
            iFaceGridView.showEmpty(true);
        }
    }

    public void sendToQq(int position){
        FaceModel faceModel = (FaceModel) faceModelList.get(position);
        FavoriteModel favoriteModel = new FavoriteModel(faceModel);
        FavoriteModel.save(favoriteModel);
        sendHelper.sendFace(faceModel, iFaceGridView.getMainActivity(), EnumSendWay.Qq);
    }

    public void sendToWeiXin(int position){
        FaceModel faceModel = (FaceModel) faceModelList.get(position);
        FavoriteModel favoriteModel = new FavoriteModel(faceModel);
        FavoriteModel.save(favoriteModel);
        sendHelper.sendFace(faceModel,iFaceGridView.getMainActivity(), EnumSendWay.WeiXin);
    }

    public void deleteFaceFile(FaceModel faceModel){
        fileUtils.deleteFile(new File(faceModel.expressionPath));
        fileUtils.deleteFile(new File(faceModel.thumbPath));
    }

    private final class FaceSubscriber extends DefaultSubscriber<List<FaceModel>>{
        @Override
        public void onNext(List<FaceModel> faceModels) {
            super.onNext(faceModels);
            if (isRefresh){
                faceModelList.clear();
                isRefresh = false;
            }
            faceModelList.addAll(faceModels);
            if (faceModelList.isEmpty()){
                iFaceGridView.showEmpty(true);
            }else {
                iFaceGridView.showEmpty(false);
                iFaceGridView.showFaceList();
            }
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            iFaceGridView.showError(true);
        }
    }
}
