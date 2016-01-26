package com.zhl.face.interactor;

import com.zhl.face.data.net.RestApi;

import rx.Observable;

public class PostDownload extends UseCase{
    public static final int TYPE_FACE   = 0;
    public static final int TYPE_SERIES = 1;

    private int type;
    private String ids;
    private RestApi restApi;

    public PostDownload(int type,String ids,RestApi restApi){
        this.type = type;
        this.ids = ids;
        this.restApi = restApi;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        if (type == TYPE_FACE){
            return restApi.postFaceCount(ids);
        }else{
            return restApi.postSeriesCount(ids);
        }
    }
}
