package com.zhl.face.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.zhl.face.app.FaceApp;

public class NetUtils {

    /**
     * 网络是否连接
     * @return
     */
    public static boolean isNetConnect() {
        boolean isConnected;

        ConnectivityManager connectivityManager =
                (ConnectivityManager) FaceApp.getContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        isConnected = (networkInfo != null && networkInfo.isConnectedOrConnecting());

        return isConnected;
    }
}
