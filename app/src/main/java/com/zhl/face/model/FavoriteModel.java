package com.zhl.face.model;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;

import java.util.List;

@Table(name = FavoriteModel.TABLE_NAME)
public class FavoriteModel extends FaceModel {
    public static final String TABLE_NAME = "favorite";

    public FavoriteModel(){
        super();
    }

    public FavoriteModel(FaceModel faceModel){
        this.expressionPath = faceModel.expressionPath;
        this.expressionId = faceModel.expressionId;
        this.expressionName = faceModel.expressionName;
        this.parentId = faceModel.parentId;
        this.thumbPath = faceModel.thumbPath;
        this.useCount = faceModel.useCount;
    }

    public static List<FavoriteModel> getFavorites(){
        return new Select()
                .from(FavoriteModel.class)
                .orderBy(C_USE_COUNT + " desc")
                .execute();
    }

    public static FavoriteModel getFavorite(String favoriteId){
        return new Select().from(FavoriteModel.class)
                .where(C_FACE_ID + " = ?", favoriteId)
                .executeSingle();
    }


    public static void save(FavoriteModel favoriteModel){
        FavoriteModel temp = getFavorite(favoriteModel.expressionId);
        if (temp != null){
            temp.useCount++;
            temp.save();
        }else {
            favoriteModel.save();
        }
    }

    public static boolean isExist(String favoriteId){
        return new Select().from(FavoriteModel.class)
                .where(C_FACE_ID + " = ?",favoriteId)
                .exists();
    }

    public static void deleteFavorite(String favoriteId){
        new Delete().from(FavoriteModel.class)
                .where(C_FACE_ID + " = ?",favoriteId)
                .execute();
    }

    public static void deleteAllFavorite(List<FaceModel> faceModels){
        ActiveAndroid.beginTransaction();
        try{
            for (FaceModel faceModel : faceModels) {
                deleteFavorite(faceModel.expressionId);
            }
            ActiveAndroid.setTransactionSuccessful();
        }finally {
            ActiveAndroid.endTransaction();
        }
    }

}
