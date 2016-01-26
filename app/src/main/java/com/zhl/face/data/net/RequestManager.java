package com.zhl.face.data.net;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.orhanobut.logger.Logger;
import com.zhl.face.app.FaceApp;
import com.zhl.face.utils.CacheUtils;

public class RequestManager {
	public static RequestQueue mRequestQueue = newRequestQueue();

	private RequestManager(){

	}

	private static Cache openCache() {
		return new DiskBasedCache(CacheUtils.getExternalCacheDir(FaceApp.getContext()),
				10 * 1024 * 1024);
	}

	private static RequestQueue newRequestQueue() {
		RequestQueue requestQueue = new RequestQueue(openCache(), new BasicNetwork(new HurlStack()));
		requestQueue.start();
		return requestQueue;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void addRequest(Request request, Object tag) {
		if (tag != null) {
			request.setTag(tag);
		}
        Logger.i(request.getUrl());
		mRequestQueue.add(request);
	}

	public static void cancelAll(Object tag) {
		mRequestQueue.cancelAll(tag);
	}

}
