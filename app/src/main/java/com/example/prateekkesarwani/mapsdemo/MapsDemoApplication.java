package com.example.prateekkesarwani.mapsdemo;

import android.app.Application;
import android.content.Context;

/**
 * Created by prateek.kesarwani on 14/06/17.
 */

public class MapsDemoApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    public static Context getAppContext() {
        return context;
    }

}
