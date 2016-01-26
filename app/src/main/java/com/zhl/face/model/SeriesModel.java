package com.zhl.face.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;

import java.io.Serializable;

@Table(name = SeriesModel.TABLE_NAME)
public class SeriesModel extends Model implements IFaceGridShow,Serializable{

    public static final String TABLE_NAME    = "series";
    public static final String C_SERIES_ID   = "seriesId";
    public static final String C_SERIES_NAME = "seriesName";
    public static final String C_COVER_PATH  = "coverPath";

    @Column(name = C_SERIES_ID, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String serId;        //系列id
    @Column(name = C_SERIES_NAME)
    public String serName;      //系列名称
    @Column(name = C_COVER_PATH)
    public String coverPath;    //封面本地路径
    public String downloadUrl;  //系列包下载url
    public String coverUrl;     //封面url
    public String description;  //描述
    public long size;            //表情包大小
    public int downloadNum;     //下载量
    public boolean isDowned;    //是否已经下载
    public boolean isChecked;

    public int progress;

    public SeriesModel(){
        super();
    }


    @Override
    public String getCoverPath() {
        return coverPath;
    }

    @Override
    public String getCoverUrl() {
        return coverUrl;
    }

    @Override
    public String getImageUrl() {
        return coverUrl;
    }

    @Override
    public String getImagePath() {
        return coverPath;
    }

    @Override
    public String getName() {
        return serName;
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public static SeriesModel findSeries(String seriesId){
        return new Select()
                .from(SeriesModel.class)
                .where(C_SERIES_ID + " = ?",seriesId)
                .executeSingle();
    }

    public static void save(SeriesModel seriesModule){
        if (seriesModule != null){
            if (!isExist(seriesModule.serId)){
                seriesModule.save();
            }
        }
    }

    public static void delete(String seriesId){
        new Delete().from(SeriesModel.class)
                .where(C_SERIES_ID + " = ?",seriesId)
                .execute();
    }

    public static boolean isExist(String seriesId){
        return new Select()
                .from(SeriesModel.class)
                .where(C_SERIES_ID + " = ?",seriesId)
                .exists();
    }
}
