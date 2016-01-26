package com.zhl.face.interactor;

import com.zhl.face.data.db.DbSeries;
import com.zhl.face.model.SeriesModel;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class GetDbSeriesCase extends UseCase {
    @Override
    protected Observable buildUseCaseObservable() {
        return Observable.create(new Observable.OnSubscribe<List<SeriesModel>>() {
            @Override
            public void call(Subscriber<? super List<SeriesModel>> subscriber) {
                List<SeriesModel> seriesModules = DbSeries.getSeriess();
                subscriber.onNext(seriesModules);
            }
        });
    }

}
