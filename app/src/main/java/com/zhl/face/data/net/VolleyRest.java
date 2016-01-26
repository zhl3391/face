package com.zhl.face.data.net;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.zhl.face.app.Constant;
import com.zhl.face.data.net.response.FaceResponse;
import com.zhl.face.data.net.response.SeriesResponse;
import com.zhl.face.model.FaceModel;
import com.zhl.face.utils.FileUtils;
import com.zhl.face.utils.NetUtils;
import com.zhl.face.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import rx.Observable;
import rx.Subscriber;

public class VolleyRest extends BaseRest implements RestApi {

    private static final String TAG = "VolleyRest";

    @Override
    public Observable<SeriesResponse> getSeriesList(final int type,final int page) {
        return Observable.create(new Observable.OnSubscribe<SeriesResponse>() {
            @Override
            public void call(Subscriber<? super SeriesResponse> subscriber) {
                if (subscriber.isUnsubscribed()){
                    return;
                }
                if (NetUtils.isNetConnect()) {
                    String url = null;
                    switch (type) {
                        case Constant.HOT:
                            url = SERIES_LIST_HOT + page;
                            break;
                        case Constant.NEW:
                            url = SERIES_LIST_NEW + page;
                            break;
                    }
                    RequestFuture<String> future = RequestFuture.newFuture();
                    StringRequest request = new StringRequest(url, future, future);
                    RequestManager.addRequest(request, this);
                    try {
                        String result = future.get();
                        Logger.t(TAG).json(result);
                        subscriber.onNext(new Gson().fromJson(result, SeriesResponse.class));
                        subscriber.onCompleted();
                    } catch (InterruptedException | ExecutionException e) {
                        subscriber.onError(e);
                    }
                } else {
                    subscriber.onError(new NetworkConnectionException());
                }
            }
        });
    }

    @Override
    public Observable<SeriesResponse> searchSeriesList(final String key, final int page) {
        return Observable.create(new Observable.OnSubscribe<SeriesResponse>() {
            @Override public void call(Subscriber<? super SeriesResponse> subscriber) {
                if (NetUtils.isNetConnect()){
                    try {
                        String key2 = URLEncoder.encode(key, "utf-8");
                        String url = SEARCH_SERIES + key2 + PAGE + page;
                        RequestFuture<String> future = RequestFuture.newFuture();
                        StringRequest request = new StringRequest(url,future,future);
                        RequestManager.addRequest(request,this);
                        String result = future.get();
                        Logger.t(TAG).json(result);
                        subscriber.onNext(new Gson().fromJson(result, SeriesResponse.class));
                        subscriber.onCompleted();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        subscriber.onError(e);
                    }catch (InterruptedException | ExecutionException e) {
                        subscriber.onError(e);
                    }

                }else {
                    subscriber.onError(new NetworkConnectionException());
                }
            }
        });
    }

    @Override
    public Observable<FaceResponse> getFaceList(final int type,final int page) {
        return Observable.create(new Observable.OnSubscribe<FaceResponse>() {
            @Override public void call(Subscriber<? super FaceResponse> subscriber) {
                if (NetUtils.isNetConnect()){
                    String url = null;
                    switch (type){
                        case Constant.HOT:
                            url = SERIES_LIST_HOT + page;
                            break;
                        case Constant.NEW:
                            url = FACE_LIST_NEW + page;
                            break;
                    }
                    RequestFuture<String> future = RequestFuture.newFuture();
                    StringRequest request = new StringRequest(url,future,future);
                    RequestManager.addRequest(request,this);
                    try {
                        String result = future.get();
                        Logger.t(TAG).json(result);
                        subscriber.onNext(new Gson().fromJson(result, FaceResponse.class));
                        subscriber.onCompleted();
                    } catch (InterruptedException | ExecutionException e) {
                        subscriber.onError(e);
                    }
                }else {
                    subscriber.onError(new NetworkConnectionException());
                }
            }
        });
    }

    @Override
    public Observable<FaceResponse> searchFaceList(final String key, final int page) {
        return Observable.create(new Observable.OnSubscribe<FaceResponse>() {
            @Override public void call(Subscriber<? super FaceResponse> subscriber) {
                if (NetUtils.isNetConnect()){
                    try {
                        String key2 = URLEncoder.encode(key, "utf-8");
                        String url = SEARCH_FACE + key2 + PAGE + page;
                        RequestFuture<String> future = RequestFuture.newFuture();
                        StringRequest request = new StringRequest(url,future,future);
                        RequestManager.addRequest(request,this);
                        String result = future.get();
                        Logger.t(TAG).json(result);
                        subscriber.onNext(new Gson().fromJson(result, FaceResponse.class));
                        subscriber.onCompleted();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }catch (InterruptedException | ExecutionException e) {
                        subscriber.onError(e);
                    }

                }else {
                    subscriber.onError(new NetworkConnectionException());
                }
            }
        });
    }

    @Override
    public Observable<FaceResponse> getSeriesFace(final String seriesId) {
        return Observable.create(new Observable.OnSubscribe<FaceResponse>() {
            @Override public void call(Subscriber<? super FaceResponse> subscriber) {
                if (NetUtils.isNetConnect()){
                    String url = SERIES_FACE_LIST + seriesId;
                    RequestFuture<String> future = RequestFuture.newFuture();
                    StringRequest request = new StringRequest(url,future,future);
                    RequestManager.addRequest(request,this);
                    try {
                        String result = future.get();
                        Logger.t(TAG).json(result);
                        subscriber.onNext(new Gson().fromJson(result, FaceResponse.class));
                        subscriber.onCompleted();
                    } catch (InterruptedException | ExecutionException e) {
                        subscriber.onError(e);
                    }
                }else {
                    subscriber.onError(new NetworkConnectionException());
                }
            }
        });
    }

    @Override
    public Observable<FaceModel> downloadFace(final FaceModel faceModel) {
        return Observable.create(new Observable.OnSubscribe<FaceModel>() {
            @Override public void call(Subscriber<? super FaceModel> subscriber) {
                if (NetUtils.isNetConnect()){
                    String url = faceModel.downloadUrl;
                    RequestFuture<byte[]> future = RequestFuture.newFuture();
                    GifRequest request = new GifRequest(url,future,future);
                    RequestManager.addRequest(request, this);
                    String path = FileUtils.TEMP_DIR
                            + Utils.toMD5(faceModel.downloadUrl);
                    try {
                        byte[] data = future.get();
                        FileUtils.getInstance().saveFile(path, data);
                        faceModel.expressionPath = path;
                        subscriber.onNext(faceModel);
                        subscriber.onCompleted();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                        subscriber.onError(e);
                    }

                }else {
                    subscriber.onError(new NetworkConnectionException());
                }
            }
        });
    }

    @Override
    public Observable<String> getKeys() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override public void call(Subscriber<? super String> subscriber) {
                if (NetUtils.isNetConnect()){
                    String url = SEARCH_KEYS;
                    RequestFuture<String> future = RequestFuture.newFuture();
                    StringRequest request = new StringRequest(url,future,future);
                    RequestManager.addRequest(request, this);
                    try {
                        String keys = future.get();
                        Logger.t(TAG).json(keys);
                        JSONObject object = new JSONObject(keys);
                        subscriber.onNext(object.getString("data"));
                        subscriber.onCompleted();
                    } catch (InterruptedException | ExecutionException | JSONException e) {
                        e.printStackTrace();
                        subscriber.onError(e);
                    }
                }else {
                    subscriber.onError(new NetworkConnectionException());
                }
            }
        });
    }

    @Override
    public Observable<Integer> postFaceCount(final String faceIds) {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override public void call(Subscriber<? super Integer> subscriber) {
                if (NetUtils.isNetConnect()){
                    String url = DOWNLOAD_COUNT_FACE;
                    RequestFuture<String> future = RequestFuture.newFuture();
                    StringRequest request = new StringRequest(Request.Method.POST,url,future,future){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> params = new HashMap<>();
                            params.put("faceIds",faceIds);
                            return params;
                        }
                    };
                    RequestManager.addRequest(request, this);
                    try {
                        JSONObject data = new JSONObject(future.get());
                        Logger.t(TAG).json(data.toString());
                        subscriber.onNext(data.getJSONObject("data").getInt("rc"));
                        subscriber.onCompleted();
                    } catch (InterruptedException | ExecutionException | JSONException e) {
                        e.printStackTrace();
                        subscriber.onError(e);
                    }
                }else {
                    subscriber.onError(new NetworkConnectionException());
                }
            }
        });
    }

    @Override
    public Observable<Integer> postSeriesCount(final String seriesIds) {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override public void call(Subscriber<? super Integer> subscriber) {
                if (NetUtils.isNetConnect()){
                    String url = DOWNLOAD_COUNT_SERIES;
                    RequestFuture<String> future = RequestFuture.newFuture();
                    StringRequest request = new StringRequest(Request.Method.POST,url,future,future){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> params = new HashMap<>();
                            params.put("seriesIds",seriesIds);
                            return params;
                        }
                    };
                    RequestManager.addRequest(request, this);
                    try {
                        JSONObject data = new JSONObject(future.get());
                        Logger.t(TAG).json(data.toString());
                        subscriber.onNext(data.getJSONObject("data").getInt("rc"));
                        subscriber.onCompleted();
                    } catch (InterruptedException | ExecutionException | JSONException e) {
                        e.printStackTrace();
                        subscriber.onError(e);
                    }
                }else {
                    subscriber.onError(new NetworkConnectionException());
                }
            }
        });
    }

    @Override
    public Observable<Integer> postSearch(final String search) {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override public void call(Subscriber<? super Integer> subscriber) {
                if (NetUtils.isNetConnect()){
                    String url = SEARCH_COUNT;
                    RequestFuture<String> future = RequestFuture.newFuture();
                    StringRequest request = new StringRequest(Request.Method.POST,url,future,future){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> params = new HashMap<>();
                            params.put("search",search);
                            return params;
                        }
                    };
                    RequestManager.addRequest(request, this);
                    try {
                        JSONObject data = new JSONObject(future.get());
                        Logger.t(TAG).json(data.toString());
                        subscriber.onNext(data.getJSONObject("data").getInt("rc"));
                        subscriber.onCompleted();
                    } catch (InterruptedException | ExecutionException | JSONException e) {
                        e.printStackTrace();
                        subscriber.onError(e);
                    }
                }else {
                    subscriber.onError(new NetworkConnectionException());
                }
            }
        });
    }

}
