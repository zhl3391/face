package com.zhl.face.interactor;

import android.text.TextUtils;

import com.zhl.face.data.net.RestApi;

import rx.Observable;

public class GetNetFaceCase extends UseCase{

    private int type;
    private int page;
    private String seriesId;
    private String key;
    private RestApi restApi;

    public GetNetFaceCase(int type,int page,String seriesId,RestApi restApi){
        this.type = type;
        this.page = page;
        this.restApi = restApi;
        this.seriesId = seriesId;
    }

    public GetNetFaceCase(String key,int page,RestApi restApi){
        this.key = key;
        this.page = page;
        this.restApi = restApi;
    }

    public void setPage(int page) {
        this.page = page;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        if (TextUtils.isEmpty(key)){
            if (TextUtils.isEmpty(seriesId)){
                return restApi.getFaceList(type,page);
            }else {
                return restApi.getSeriesFace(seriesId);
            }
        }else {
            return restApi.searchFaceList(key,page);
        }

    }
}
