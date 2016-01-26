package com.zhl.face.app;

import android.app.Application;
import android.content.Context;
import android.view.WindowManager;

import com.activeandroid.ActiveAndroid;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.zhl.face.utils.FileUtils;

public class FaceApp extends Application {

    private static Context context;

    public static int winHeight;
    public static int winWidth;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        ActiveAndroid.initialize(this);
        if (AppConfig.DEBUG){
            Logger.init("face").hideThreadInfo().setMethodCount(0);
        }else {
            Logger.init("face").hideThreadInfo().setMethodCount(0).setLogLevel(LogLevel.NONE);
        }
        FileUtils.getInstance().createAppDir();

        WindowManager wm = (WindowManager)context.getSystemService(
                Context.WINDOW_SERVICE);
        winWidth  = wm.getDefaultDisplay().getWidth();
        winHeight = wm.getDefaultDisplay().getHeight();
        CrashHandler.getInstance().init(context);

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ActiveAndroid.dispose();
    }

    public static Context getContext(){
        return context;
    }
}
