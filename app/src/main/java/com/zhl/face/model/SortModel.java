package com.zhl.face.model;

import java.io.Serializable;

public class SortModel implements Serializable{
    
    public String sortId;         //分类id
    public String patSortId;      //父分类id
    public String sortName;       //分类名称
    public String coverUrl;       //封面url
    public int count;             //分类个数
}
