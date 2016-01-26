package com.zhl.face.data.net;

import com.zhl.face.data.net.response.FaceResponse;
import com.zhl.face.data.net.response.SeriesResponse;
import com.zhl.face.model.FaceModel;

import org.json.JSONObject;

import rx.Observable;

public interface RestApi {
    String HOST = "http://api.superface.net/?service=";

    String PAGE = "&page=";

    String SORT_LIST = HOST + "Sort.GetSorts";
    String SORT_INFO = HOST + "Series.GetSortSeries&sort_id=";

    String SERIES_LIST_NEW  = HOST + "Series.GetNewSeries" + PAGE;
    String SERIES_FACE_LIST = HOST + "Face.GetFaces&series_id=";
    String SERIES_LIST_HOT  = HOST + "Series.GetHotSeries" + PAGE;

    String SEARCH_FACE   = HOST + "Face.SearchFace&key=";
    String SEARCH_SERIES = HOST + "Series.SearchSeries&key=";
    String SEARCH_KEYS   = HOST + "Search.GetKeys";
    String SEARCH_COUNT = HOST + "Search.SetSearch";

    String FACE_LIST_NEW = HOST + "Face.GetNewFaces" + PAGE;
    String PAGE_20  = "&size=20&page=";
    String PAGE_10  = "&size=10&page=";

    String PAGE_30  = "&size=30&page=";

    String PAGE_ALL = "&size=100&page=";
    String DOWNLOAD_COUNT_FACE   = HOST + "Face.SetDownCount";
    String DOWNLOAD_COUNT_SERIES = HOST + "Series.SetDownCount";


    String WEB_INTRODUCE = "";

    Observable<SeriesResponse> getSeriesList(int type,int page);

    Observable<SeriesResponse> searchSeriesList(String key,int page);

    Observable<FaceResponse> getFaceList(int type,int page);

    Observable<FaceResponse> searchFaceList(String key,int page);

    Observable<FaceResponse> getSeriesFace(String seriesId);

    Observable<FaceModel> downloadFace(FaceModel faceModel);

    Observable<String> getKeys();

    Observable<Integer> postFaceCount(String faceIds);

    Observable<Integer> postSeriesCount(String seriesIds);

    Observable<Integer> postSearch(String search);
}
