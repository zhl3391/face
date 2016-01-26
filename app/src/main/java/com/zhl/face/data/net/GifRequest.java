package com.zhl.face.data.net;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

public class GifRequest extends Request<byte[]> {

	private Listener<byte[]> mListener;
	
	public GifRequest(String url, Listener<byte[]> listener, ErrorListener errorListener) {
		super(Method.GET, url, errorListener);
		mListener = listener;
	}

	@Override
	protected void deliverResponse(byte[] arg0) {
		mListener.onResponse(arg0);
		
	}

	@Override
	protected Response<byte[]> parseNetworkResponse(NetworkResponse arg0) {
		byte[] data = arg0.data;
		if (data == null) {
			return Response.error(new VolleyError("data = null"));
		}else {
			return Response.success(data, HttpHeaderParser.parseCacheHeaders(arg0));
		}
	}


}
