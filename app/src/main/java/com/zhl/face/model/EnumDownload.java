package com.zhl.face.model;

public enum EnumDownload {
//    Face("精选表情"),New("新增系列"),Hot("热门系列");
    Face("精选表情"),New("新增系列");
    private String mDisplayName;

    EnumDownload(String displayName) {
        mDisplayName = displayName;
    }

    public String getDisplayName() {
        return mDisplayName;
    }
}
