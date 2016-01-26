package com.zhl.face.app;

import com.zhl.face.R;
import com.zhl.face.data.net.RestApi;
import com.zhl.face.model.EnumSendWay;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class PreSetting {

    private PreSetting(){
        mContext 			    = FaceApp.getContext();
        preferencesDefault	 	= PreferenceManager.getDefaultSharedPreferences(mContext);
        preferences				= mContext.getSharedPreferences(AppConfig.PACKAGE_NAME, Activity.MODE_PRIVATE);
        editor					= preferences.edit();
//		editorDefault			= preferencesDefault.edit();
    };

    private static PreSetting single;

    private Context mContext;

    public static PreSetting getInstance(){
        if (single == null) {
            single = new PreSetting();
        }
        return single;
    }

    private SharedPreferences preferencesDefault;
    private SharedPreferences preferences;
    //	private Editor editorDefault;
    private Editor editor;

    public boolean isSlideLeftOn() {
        return preferencesDefault.getBoolean(
                mContext.getString(R.string.key_setting_slide_left), true);
    }
    public boolean isSlideRightOn() {
        return preferencesDefault.getBoolean(
                mContext.getString(R.string.key_setting_slide_right), false);
    }
    public int getFloatViewX() {
        return preferences.getInt(PreConstant.KEY_FLOATVIEW_X, FaceApp.winWidth / 2);
    }
    public void setFloatViewX(int floatViewX) {
        editor.putInt(PreConstant.KEY_FLOATVIEW_X, floatViewX);
        editor.commit();
    }
    public int getFloatViewY() {
        return preferences.getInt(PreConstant.KEY_FLOATVIEW_Y, 0);
    }
    public void setFloatViewY(int floatViewY) {
        editor.putInt(PreConstant.KEY_FLOATVIEW_Y, floatViewY);
        editor.commit();
    }
    public void setSendWay(int way){
        editor.putInt(PreConstant.KEY_SEND_WAY, way);
        editor.commit();
    }
    public int getSendWay(){
        return preferences.getInt(PreConstant.KEY_SEND_WAY, EnumSendWay.WeiXin.ordinal());
    }
    public boolean getFloatView(){
        return preferencesDefault.getBoolean(
                mContext.getString(R.string.key_setting_floatview), false);
    }
    public boolean getIsTipDownFaceShow(){
        return preferences.getBoolean(PreConstant.KEY_TIP_DOWN_FACE, false);
    }
    public void setIsTipDownFaceShow(boolean isShow){
        editor.putBoolean(PreConstant.KEY_TIP_DOWN_FACE, isShow);
        editor.commit();
    }
    public int getCustomMaxId(){
        return preferences.getInt(PreConstant.KEY_CUSTOM_MAX_ID, 1);
    }
    public void setCustomMaxId(int customId){
        if (customId > 1) {
            editor.putInt(PreConstant.KEY_CUSTOM_MAX_ID, customId);
            editor.commit();
        }
    }
    public boolean isFirstOpen(){
        return preferences.getBoolean(PreConstant.KEY_IS_FIRST_OPEN, true);
    }
    public void setIsFirstOpen(boolean isFirstOpen){
        editor.putBoolean(PreConstant.KEY_IS_FIRST_OPEN, isFirstOpen);
        editor.commit();
    }
    public void setShareText(String text){
        if (text != null) {
            editor.putString(PreConstant.KEY_SHARE_TEXT, text);
            editor.commit();
        }
    }
    public String getShareText(){
        return preferences.getString(PreConstant.KEY_SHARE_TEXT, "");
    }

    public void setIsIntroduce(int isShow){
        editor.putInt(PreConstant.KEY_IS_INTRODUCE,isShow);
        editor.commit();
    }

    public int getIsIntroduce(){
        return preferences.getInt(PreConstant.KEY_IS_INTRODUCE,0);
    }

    public void setIsUpdate(boolean isUpdate){
        editor.putBoolean(PreConstant.KEY_IS_UPDATE,isUpdate);
        editor.commit();
    }
    public boolean isUpdate(){
        return preferences.getBoolean(PreConstant.KEY_IS_UPDATE,false);
    }


    public String getHost(){
        return preferences.getString(PreConstant.KEY_HOST, RestApi.HOST);
    }

    public void setHost(String host){
        editor.putString(PreConstant.KEY_HOST,host);
        editor.commit();
    }

    public String getFaceDown(){
        return preferences.getString(PreConstant.KEY_FACE_DOWN,"");
    }

    public void setFaceDown(String faceId){
        editor.putString(PreConstant.KEY_FACE_DOWN,faceId);
        editor.commit();
    }

    public String getSeriesDown(){
        return preferences.getString(PreConstant.KEY_SERIES_DOWN,"");
    }

    public void setSeriesDown(String seriesId){
        editor.putString(PreConstant.KEY_SERIES_DOWN,seriesId);
        editor.commit();
    }

    public String getSearch(){
        return preferences.getString(PreConstant.KEY_SEARCH,"");
    }

    public void setSearch(String search){
        editor.putString(PreConstant.KEY_SEARCH,search);
        editor.commit();
    }

    public void setIsPostSearch(int isPostSearch){
        editor.putInt(PreConstant.KEY_IS_POST_SEARCH, isPostSearch);
        editor.commit();
    }

    public int getIsPostSearch(){
        return preferences.getInt(PreConstant.KEY_IS_POST_SEARCH,0);
    }
}
