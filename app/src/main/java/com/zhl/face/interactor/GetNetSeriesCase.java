package com.zhl.face.interactor;

import android.text.TextUtils;

import com.zhl.face.data.net.RestApi;


import rx.Observable;

public class GetNetSeriesCase extends UseCase{

    private int type;
    private int page;
    private String key;
    private RestApi restApi;

    public GetNetSeriesCase(int type,int page,RestApi restApi){
        this.type = type;
        this.page = page;
        this.restApi = restApi;
    }

    public GetNetSeriesCase(String key,int page,RestApi restApi){
        this.key = key;
        this.page = page;
        this.restApi = restApi;
    }

    public void setPage(int page){
        this.page = page;
    }

    public int getPage(){
        return page;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        if (TextUtils.isEmpty(key)){
            return restApi.getSeriesList(type,page);
        }else {
            return restApi.searchSeriesList(key,page);
        }
    }
}
