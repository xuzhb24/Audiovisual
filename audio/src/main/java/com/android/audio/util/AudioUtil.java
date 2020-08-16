package com.android.audio.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.android.audio.R;

/**
 * Created by xuzhb on 2020/7/5
 */
public class AudioUtil {

    private static Context mContext;

    public static void init(Context context) {
        mContext = context.getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }

    public static float dp2px(float dpValue) {
        float density = mContext.getResources().getDisplayMetrics().density;
        return dpValue * density + 0.5f;
    }

    public static float sp2px(float spValue) {
        float scaledDensity = mContext.getResources().getDisplayMetrics().scaledDensity;
        return spValue * scaledDensity + 0.5f;
    }


    //毫秒转分秒
    public static String formatTime(long time) {
        String minute = (time / (1000 * 60)) + "";
        String second = (time % (1000 * 60) / 1000) + "";
        if (minute.length() < 2) {
            minute = 0 + minute;
        }
        if (second.length() < 2) {
            second = 0 + second;
        }
        return minute + ":" + second;
    }

    //隐式启动音频详情页的Action
    public static final String ACTION_AUDIO_DETAIL = "android.intent.action.AudioDetail";

    //打开音频详情页
    public static void openAudioDetail(Activity activity) {
        Intent intent = new Intent(ACTION_AUDIO_DETAIL);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.translate_bottom_in, R.anim.alpha_stay);
    }

}
