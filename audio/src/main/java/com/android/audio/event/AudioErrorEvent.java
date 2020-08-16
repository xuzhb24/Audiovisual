package com.android.audio.event;

/**
 * Created by xuzhb on 2020/7/5
 * Desc:播放出错
 */
public class AudioErrorEvent {

    private String msg;

    public AudioErrorEvent(String msg, int what, int extra) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
