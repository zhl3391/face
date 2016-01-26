package com.zhl.face.model;

public class AlbumModel {
    private String name;

    private int count;

    private String recent;

    private boolean isCheck;

    public AlbumModel() {
        super();
    }

    public AlbumModel(String name) {
        this.name = name;
    }

    public AlbumModel(String name, int count, String recent) {
        super();
        this.name = name;
        this.count = count;
        this.recent = recent;
    }

    public AlbumModel(String name, int count, String recent, boolean isCheck) {
        super();
        this.name = name;
        this.count = count;
        this.recent = recent;
        this.isCheck = isCheck;
    }
}
