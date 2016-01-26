package com.zhl.face.model;

public enum EnumMyFace {
	Favorite("常用表情"),Series("表情系列");
	private String mDisplayName;

	EnumMyFace(String displayName) {
        mDisplayName = displayName;
    }

    public String getDisplayName() {
        return mDisplayName;
    }
}
