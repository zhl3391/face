package com.zhl.face.data.db;

import com.activeandroid.query.Select;
import com.zhl.face.model.FaceModel;

import java.util.List;

public class DbFace {

    public static List<FaceModel> getFavorites(){
        return new Select()
                .from(FaceModel.class)
                .execute();
    }
}
