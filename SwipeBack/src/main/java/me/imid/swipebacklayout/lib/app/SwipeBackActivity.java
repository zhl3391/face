
package me.imid.swipebacklayout.lib.app;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.Utils;

public class SwipeBackActivity extends ActionBarActivity implements SwipeBackActivityBase {
    private SwipeBackActivityHelper mHelper;
//    private ImageView mImgMark;
//    private ViewGroup mDecorView;
//    private Bitmap mBitmapMark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHelper = new SwipeBackActivityHelper(this);
        mHelper.onActivityCreate();

//        mDecorView = (ViewGroup) getWindow().getDecorView();
//        mImgMark = new ImageView(this);
//        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
//                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
//        mImgMark.setLayoutParams(params);
//        mDecorView.addView(mImgMark);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mHelper.onPostCreate();
    }

    @Override
    public View findViewById(int id) {
        View v = super.findViewById(id);
        if (v == null && mHelper != null)
            return mHelper.findViewById(id);
        return v;
    }

    @Override
    public SwipeBackLayout getSwipeBackLayout() {
        return mHelper.getSwipeBackLayout();
    }

    @Override
    public void setSwipeBackEnable(boolean enable) {
        getSwipeBackLayout().setEnableGesture(enable);
    }

    @Override
    public void scrollToFinishActivity() {
        Utils.convertActivityToTranslucent(this);
        getSwipeBackLayout().scrollToFinishActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        for (int i = 0;i < mDecorView.getChildCount();i++){
//            mDecorView.getChildAt(i).setVisibility(View.VISIBLE);
//        }
//        mImgMark.setVisibility(View.GONE);
    }


    @Override
    protected void onPause() {
        super.onPause();
//        mDecorView.destroyDrawingCache();
//        mDecorView.buildDrawingCache();
//        mDecorView.setDrawingCacheEnabled(true);
//        mBitmapMark = mDecorView.getDrawingCache();
//        if (mBitmapMark != null && !mBitmapMark.isRecycled()){
//            mImgMark.setImageBitmap(mBitmapMark);
//            for (int i = 0;i < mDecorView.getChildCount();i++){
//                mDecorView.getChildAt(i).setVisibility(View.GONE);
//            }
//            mImgMark.setVisibility(View.VISIBLE);
//            mImgMark.bringToFront();
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (mBitmapMark != null){
//            mBitmapMark.recycle();
//            mBitmapMark = null;
//            mDecorView.destroyDrawingCache();
//        }
    }
}
