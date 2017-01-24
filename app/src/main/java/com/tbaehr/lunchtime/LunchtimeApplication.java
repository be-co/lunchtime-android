package com.tbaehr.lunchtime;

import android.app.Application;
import android.content.Context;

/**
 * Created by timo.baehr@gmail.com on 07.01.17.
 */
public class LunchtimeApplication extends Application {

    private static LunchtimeApplication instance;

    public LunchtimeApplication() {
        instance = this;
    }

    public static Context getContext() {
        return instance.getApplicationContext();
    }

}
