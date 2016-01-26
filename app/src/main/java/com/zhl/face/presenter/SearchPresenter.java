package com.zhl.face.presenter;

import com.zhl.face.interactor.DefaultSubscriber;
import com.zhl.face.interactor.GetKeyCase;
import com.zhl.face.view.iview.ISearchView;

public class SearchPresenter implements IPresenter {


    private ISearchView iSearchView;
    private GetKeyCase getKeyCase;

    public String[] searchKeys;

    public SearchPresenter(ISearchView iSearchView,GetKeyCase getKeyCase){
        this.iSearchView = iSearchView;
        this.getKeyCase = getKeyCase;
    }

    public void getKeys(){
        getKeyCase.execute(new SearchSubscriber());
    }

    public void search(String searchText){

    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {
        getKeyCase.unsubscribe();
    }

    private final class SearchSubscriber extends DefaultSubscriber<String>{
        @Override
        public void onNext(String s) {
            super.onNext(s);
            searchKeys = s.split("＃");
            iSearchView.showKeys();
        }
    }


}
