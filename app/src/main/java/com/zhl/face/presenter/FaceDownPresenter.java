package com.zhl.face.presenter;

import com.zhl.face.app.AppConfig;
import com.zhl.face.app.Constant;
import com.zhl.face.app.PreSetting;
import com.zhl.face.data.net.VolleyRest;
import com.zhl.face.data.net.response.Data;
import com.zhl.face.data.net.response.FaceResponse;
import com.zhl.face.helper.SendHelper;
import com.zhl.face.interactor.DefaultSubscriber;
import com.zhl.face.interactor.DownloadFaceCase;
import com.zhl.face.interactor.GetNetFaceCase;
import com.zhl.face.interactor.GetNetSeriesCase;
import com.zhl.face.model.EnumSendWay;
import com.zhl.face.model.FaceModel;
import com.zhl.face.model.FavoriteModel;
import com.zhl.face.model.IFaceGridShow;
import com.zhl.face.utils.FileUtils;
import com.zhl.face.utils.Utils;
import com.zhl.face.view.iview.IFaceGridView;

import java.util.ArrayList;
import java.util.List;

public class FaceDownPresenter implements IPresenter{

    private IFaceGridView iFaceGridView;
    private GetNetFaceCase getNetFaceCase;

    public List<IFaceGridShow> faceModelList = new ArrayList<>();
    public List<FaceModel> selectFaceList = new ArrayList<>();

    private Data<FaceModel> data;

    private SendHelper sendHelper;
    private PreSetting preSetting;

    public boolean isHasMore;
    public boolean isLoadMore;

    public FaceDownPresenter(IFaceGridView iFaceGridView,GetNetFaceCase getNetFaceCase){
        this.iFaceGridView = iFaceGridView;
        this.getNetFaceCase = getNetFaceCase;
        sendHelper = SendHelper.getInstance();
        preSetting = PreSetting.getInstance();
    }

    public void init(){
        loadFace();
    }

    public void loadFace(){
        iFaceGridView.showLoading(true);
        getNetFaceCase.execute(new FaceDownSubscriber());
    }

    public void loadMore(){
        isLoadMore = true;
        if (data != null){
            getNetFaceCase.setPage(++data.page);
        }
        loadFace();
    }

    private void setFaceDowned(List<FaceModel> faceModelList){
        for (int i=0; i<faceModelList.size(); i++){
            FaceModel faceModel = faceModelList.get(i);
            if (FaceModel.isExist(faceModel.expressionId)){
                faceModel.isDowned = true;
            }
            String path = FileUtils.TEMP_DIR
                    + Utils.toMD5(faceModel.downloadUrl);
            faceModel.expressionPath = path;
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
        getNetFaceCase.unsubscribe();
    }

    public void downloadFace(final FaceModel faceModel, final boolean isShow){
        if (faceModel != null){
            if (FileUtils.getInstance().isExist(faceModel.expressionPath)){
                String[] temp = faceModel.downloadUrl.split("/");
                String faceName = temp[temp.length - 1];
                if (faceName != null) {
                    String oldPath = faceModel.expressionPath;
                    String newPath = FileUtils.FACE_DIR + faceName;
                    String thumbPath = FileUtils.FACE_DIR + Utils.toMD5(faceModel.downloadUrl) + "thumb";
                    FileUtils.getInstance().copyFile(oldPath, newPath);
                    FileUtils.getInstance().createThumb(newPath, thumbPath);
                    faceModel.thumbPath = thumbPath;
                    faceModel.expressionPath = newPath;
                    faceModel.parentId = AppConfig.DEFAULT_SERIES_ID;
                    FaceModel.save(faceModel);
                    faceModel.isDowned = true;
//                    iFaceGridView.showFaceList();

                    preSetting.setFaceDown(preSetting.getFaceDown() + faceModel.expressionId + ",");
                    Utils.notifyMediaUpdate();
                }else {
                }
            }else {
                DownloadFaceCase downloadFaceCase = new DownloadFaceCase(faceModel,new VolleyRest());
                downloadFaceCase.execute(new DefaultSubscriber<FaceModel>(){
                    @Override
                    public void onNext(FaceModel faceModel1) {
                        if (isShow){
                            iFaceGridView.showDownloadFace(faceModel1);
                        }else {
                            downloadFace(faceModel1,false);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        downloadFace(faceModel,false);
                    }
                });
            }
        }
    }

    public void downloadSelectFaces(){
        for (FaceModel faceModel: selectFaceList) {
            downloadFace(faceModel,false);
        }
    }

    public void sendToQq(int position){
        FaceModel faceModel = (FaceModel) faceModelList.get(position);
        if (faceModel.isDowned){
            faceModel = FaceModel.findFace(faceModel.expressionId);
            FavoriteModel favoriteModel = new FavoriteModel(faceModel);
            FavoriteModel.save(favoriteModel);
        }
        sendHelper.sendFace(faceModel, iFaceGridView.getMainActivity(), EnumSendWay.Qq);
    }

    public void sendToWeiXin(int position){
        FaceModel faceModel = (FaceModel) faceModelList.get(position);
        if (faceModel.isDowned){
            faceModel = FaceModel.findFace(faceModel.expressionId);
            FavoriteModel favoriteModel = new FavoriteModel(faceModel);
            FavoriteModel.save(favoriteModel);
        }
        sendHelper.sendFace(faceModel,iFaceGridView.getMainActivity(), EnumSendWay.WeiXin);
    }

    private final class FaceDownSubscriber extends DefaultSubscriber<FaceResponse>{

        @Override
        public void onNext(FaceResponse faceResponse) {
            super.onNext(faceResponse);
            data = faceResponse.data;
            if (data != null){
                isHasMore = data.page < data.totalPage;
                int rc = data.rc;
                switch (rc){
                    case Constant.RC_SUCCESS:
                        if (data.info != null){
                            setFaceDowned(data.info);
                            faceModelList.addAll(data.info);
                            iFaceGridView.showLoading(false);
                            iFaceGridView.showFaceList();
                        }else {
                            iFaceGridView.showError(true);
                        }
                        break;
                    case Constant.RC_SUCCESS_NULL:
                        iFaceGridView.showEmpty(true);
                        break;
                    case Constant.RC_SUCCESS_FAIL:
                        iFaceGridView.showError(true);
                        break;
                }
                if (!isHasMore){
                    iFaceGridView.showNoMore();
                }
            }else {
                iFaceGridView.showError(true);
            }
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            iFaceGridView.showError(true);
        }
    }
}
