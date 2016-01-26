package com.zhl.face.interactor;

import com.zhl.face.data.net.RestApi;
import com.zhl.face.model.FaceModel;

import rx.Observable;

public class DownloadFaceCase extends UseCase{

    private FaceModel faceModel;
    private RestApi restApi;

    public DownloadFaceCase(FaceModel faceModel,RestApi restApi){
        this.faceModel = faceModel;
        this.restApi = restApi;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return restApi.downloadFace(faceModel);
    }
}
