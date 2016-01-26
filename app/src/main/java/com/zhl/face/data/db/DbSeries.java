package com.zhl.face.data.db;

import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.zhl.face.model.SeriesModel;

import java.util.List;

public class DbSeries {

    public static List<SeriesModel> getSeriess(){
        return new Select()
                .from(SeriesModel.class)
                .execute();
    }
}
