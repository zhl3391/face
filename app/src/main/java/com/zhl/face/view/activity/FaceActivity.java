package com.zhl.face.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.zhl.face.R;
import com.zhl.face.app.AppConfig;
import com.zhl.face.view.fragment.FmFace;

public class FaceActivity extends ActivityFragment{

    public static final String KEY_SERIES_ID = "series_id";
    public static final String KEY_SERIES_NAME = "series_name";

    private String seriesId;
    private String seriesName;

    private MaterialDialog delDlg;
    private FmFace fmFace;

    public static Intent buildIntent(Context context,String seriesId,String seriesName){
        Intent intent = new Intent(context,FaceActivity.class);
        intent.putExtra(KEY_SERIES_ID,seriesId);
        intent.putExtra(KEY_SERIES_NAME,seriesName);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        seriesId = intent.getStringExtra(KEY_SERIES_ID);
        seriesName = intent.getStringExtra(KEY_SERIES_NAME);
        super.onCreate(savedInstanceState);
        actionBar.setTitle(seriesName);
    }

    @Override
    protected Fragment getMainFragment() {
        fmFace = FmFace.newInstance(seriesId,false);
        return fmFace;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_delete) {
            showDeleteDlg();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDeleteDlg(){
        if (delDlg == null){
            String content;
            if (AppConfig.DEFAULT_SERIES_ID.equals(seriesId)){
                content = getString(R.string.dlg_clear);
            }else{
                content = getString(R.string.dlg_del_series);
            }
            delDlg = new MaterialDialog.Builder(this)
                    .content(content)
                    .theme(Theme.LIGHT)
                    .positiveText(R.string.text_ok)
                    .positiveColorRes(R.color.red)
                    .negativeText(R.string.text_dont_del)
                    .negativeColor(R.color.black)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(com.afollestad.materialdialogs.MaterialDialog dialog) {
                            super.onPositive(dialog);
                            fmFace.deleteSeries();
                        }
                    }).build();
        }
        delDlg.show();
    }
}
