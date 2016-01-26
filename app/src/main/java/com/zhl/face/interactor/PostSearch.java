package com.zhl.face.interactor;

import com.zhl.face.data.net.RestApi;

import rx.Observable;

/**
 * Created by zhl on 15/10/11.
 * 提交搜索词
 */
public class PostSearch extends UseCase{

    private String search;
    private RestApi restApi;

    public PostSearch(String search,RestApi restApi){
        this.search = search;
        this.restApi = restApi;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return restApi.postSearch(search);
    }
}
