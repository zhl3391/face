package com.zhl.face.model;

import java.util.ArrayList;

public class PageModel<T> {
    public int size;            //每页总数
    public int pageNo;          //当前页码
    public int totalPage;       //总页数
    public int totalNum;        //总记录数
    public ArrayList<T> items;  //数据列表
}
