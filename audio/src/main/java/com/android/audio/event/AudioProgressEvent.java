package com.android.audio.event;

import com.android.audio.core.CustomMediaPlayer;

/**
 * Created by xuzhb on 2020/7/5
 * Desc:进度更新
 */
public class AudioProgressEvent {

    private CustomMediaPlayer.Status status;  //播放器状态
    private int position;  //当前播放的位置
    private int duration;  //音频时长

    public AudioProgressEvent(CustomMediaPlayer.Status status, int position, int duration) {
        this.status = status;
        this.position = position;
        this.duration = duration;
    }

    public CustomMediaPlayer.Status getStatus() {
        return status;
    }

    public void setStatus(CustomMediaPlayer.Status status) {
        this.status = status;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
