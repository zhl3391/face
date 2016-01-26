package com.zhl.face.model;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = FaceModel.TABLE_NAME)
public class FaceModel extends Model implements IFaceGridShow{
    public static final String TABLE_NAME     = "face";
    public static final String C_FACE_ID      = "faceId";
    public static final String C_FACE_NAME    = "faceName";
    public static final String C_PARENT_ID    = "parentId";
    public static final String C_FACE_PATH    = "facePath";
    public static final String C_THUMB_PATH   = "thumbPath";
    public static final String C_USE_COUNT    = "useCount";

    @Column(name = C_USE_COUNT)
    public int useCount;               //使用次数
    @Column(name = C_FACE_ID)
    public String expressionId;        //表情id
    @Column(name = C_FACE_NAME)
    public String expressionName;      //表情名称
    @Column(name = C_PARENT_ID)
    public String parentId;            //所属系列id
    @Column(name = C_FACE_PATH)
    public String expressionPath;      //本地路径
    @Column(name = C_THUMB_PATH)
    public String thumbPath;           //本地缩略图路径

    public String downloadUrl;         //表情url
    public String shortCutUrl;         //表情缩略图url
    public boolean isDowned  = false;  //是否已经下载
    public boolean isChecked = false;

    public FaceModel(){
        super();
    }

    @Override
    public String getCoverPath() {
        return thumbPath;
    }
    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
    @Override
    public String getCoverUrl() {
        return shortCutUrl;
    }

    @Override
    public String getImageUrl() {
        return downloadUrl;
    }

    @Override
    public String getImagePath() {
        return expressionPath;
    }

    @Override
    public String getName() {
        return expressionName;
    }

    public static FaceModel findFace(String faceId){
        return new Select()
                .from(FaceModel.class)
                .where(C_FACE_ID + " = ?",faceId)
                .executeSingle();
    }

    public static void save(FaceModel faceModel){
        if (faceModel != null){
            if (!isExist(faceModel.expressionId)){
                faceModel.save();
            }
        }
    }

    public static void saveAll(List<FaceModel> faceModels){
        ActiveAndroid.beginTransaction();
        try {
            for (FaceModel faceModel : faceModels){
                save(faceModel);
            }
            ActiveAndroid.setTransactionSuccessful();
        }finally {
            ActiveAndroid.endTransaction();
        }


    }

    public static List<FaceModel> getFaces(String seriesId){
        return new Select()
                .from(FaceModel.class)
                .where(C_PARENT_ID + " = ?",seriesId)
                .execute();
    }

    public static boolean isExist(String faceId){
        return new Select().from(FaceModel.class)
                .where(C_FACE_ID + " = ?",faceId)
                .exists();
    }

    public static void deleteSeriesFace(String seriesId){
        new Delete().from(FaceModel.class)
                .where(C_PARENT_ID + " = ?",seriesId)
                .execute();
    }

    public static void delete(String faceId){
        new Delete().from(FaceModel.class)
                .where(C_FACE_ID + " = ?",faceId)
                .execute();
    }

    public static void deleteAll(List<FaceModel> faceModels){
        ActiveAndroid.beginTransaction();
        try{
            for (FaceModel faceModel : faceModels) {
                delete(faceModel.expressionId);
            }
            ActiveAndroid.setTransactionSuccessful();
        }finally {
            ActiveAndroid.endTransaction();
        }
    }

}
