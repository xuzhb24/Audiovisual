package com.android.audio.notice;

import com.android.audio.util.AudioUtil;

/**
 * Created by xuzhb on 2020/7/11
 * Desc:
 */
public class NoticeAction {

    public static final String NOTIFICATION_ACTION = AudioUtil.getContext().getPackageName() + ".NOTIFICATION_BAR";
    public static final String EXTRA_NAME = "EXTRA_NAME";
    public static final String ACTION_PLAY = "ACTION_PLAY";
    public static final String ACTION_PRE = "ACTION_PRE";
    public static final String ACTION_NEXT = "ACTION_NEXT";
    public static final String ACTION_CANCEL = "ACTION_CANCEL";

}
