package com.android.audio.event;

import com.android.audio.core.AudioController;

/**
 * Created by xuzhb on 2020/7/5
 * Desc:切换播放模式
 */
public class AudioPlayModeEvent {

    private AudioController.PlayMode playMode;

    public AudioPlayModeEvent(AudioController.PlayMode playMode) {
        this.playMode = playMode;
    }

    public AudioController.PlayMode getPlayMode() {
        return playMode;
    }

    public void setPlayMode(AudioController.PlayMode playMode) {
        this.playMode = playMode;
    }

}
