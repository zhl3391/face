package com.zhl.face.model;

import java.io.Serializable;

/**
 * Created by zhl on 15/6/10.
 */
public class PhotoModel implements Serializable{
    public String originalPath;
    public boolean isChecked;

    public PhotoModel(String originalPath, boolean isChecked) {
        super();
        this.originalPath = originalPath;
        this.isChecked = isChecked;
    }

    public PhotoModel(String originalPath) {
        this.originalPath = originalPath;
    }

    public PhotoModel() {
    }
}
