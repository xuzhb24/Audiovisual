package com.android.audio.event;

import com.android.audio.entity.AudioBean;

/**
 * Created by xuzhb on 2020/7/5
 * Desc:加载音频
 */
public class AudioLoadEvent {

    private AudioBean audioBean;

    public AudioLoadEvent(AudioBean audioBean) {
        this.audioBean = audioBean;
    }

    public AudioBean getAudioBean() {
        return audioBean;
    }

    public void setAudioBean(AudioBean audioBean) {
        this.audioBean = audioBean;
    }

}
