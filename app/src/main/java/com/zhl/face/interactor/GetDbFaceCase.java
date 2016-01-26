package com.zhl.face.interactor;

import android.text.TextUtils;

import com.zhl.face.data.db.DbFace;
import com.zhl.face.model.FaceModel;
import com.zhl.face.model.FavoriteModel;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class GetDbFaceCase extends UseCase{

    private String seriesId;
    private boolean isFavorite;

    public GetDbFaceCase(String seriesId,boolean isFavorite){
        this.seriesId = seriesId;
        this.isFavorite = isFavorite;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return Observable.create(new Observable.OnSubscribe<List<FaceModel>>() {
            @Override
            public void call(Subscriber<? super List<FaceModel>> subscriber) {
                List<FaceModel> faceModelList = new ArrayList<>();
                if (isFavorite){
                    faceModelList.addAll(FavoriteModel.getFavorites());
                }else {
                    if (TextUtils.isEmpty(seriesId)){
                        subscriber.onError(new NullPointerException("seriesId is empty"));
                    }
                    faceModelList.addAll(FaceModel.getFaces(seriesId));
                }
                subscriber.onNext(faceModelList);
            }
        });
    }
}
