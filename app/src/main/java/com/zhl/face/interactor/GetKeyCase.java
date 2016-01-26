package com.zhl.face.interactor;

import com.zhl.face.data.net.RestApi;

import rx.Observable;

/**
 * Created by zhl on 15/7/15.
 */
public class GetKeyCase extends UseCase{

    private RestApi restApi;

    public GetKeyCase(RestApi restApi){
        this.restApi = restApi;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return restApi.getKeys();
    }
}
