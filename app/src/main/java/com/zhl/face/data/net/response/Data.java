package com.zhl.face.data.net.response;

import java.util.ArrayList;

public class Data<T> {
    public int rc;
    public int page;
    public int totalPage;
    public int size;
    public String msg;
    public ArrayList<T> info;
}
