package com.zhl.face.model;

import com.zhl.face.R;

public enum EnumSendWay {

	WeiXin("微信"),Qq("QQ");
	private String mDisplayName;
	private int mIndex;
	private static int iconRes[] = {R.mipmap.ic_weixin,R.mipmap.ic_qq};

	EnumSendWay(String displayName) {
        mDisplayName = displayName;
        mIndex = this.ordinal();;
    }

    public String getDisplayName() {
        return mDisplayName;
    }
    
    public int getIconResId(){
    	return iconRes[mIndex];
    }
}
