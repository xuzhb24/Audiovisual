package com.android.audiovisual.base;

import android.app.Application;

import com.android.audio.util.AudioUtil;

/**
 * Created by xuzhb on 2020/7/13
 * Desc:
 */
public class BaseApplication extends Application {

    private static BaseApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        AudioUtil.init(this);
    }

    //获取Application单例
    public static BaseApplication getInstance() {
        return mInstance;
    }

}
