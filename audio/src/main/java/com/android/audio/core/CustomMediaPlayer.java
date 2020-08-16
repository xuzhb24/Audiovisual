package com.android.audio.core;

import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by xuzhb on 2020/6/30
 * Desc:自定义播放器，加了状态的标志
 */
public class CustomMediaPlayer extends MediaPlayer implements MediaPlayer.OnCompletionListener {

    public enum Status {
        IDLE,     //空闲
        PREPARE,  //缓冲中
        START,    //开始播放
        PAUSE,    //暂停播放
        STOP,     //停止播放
        COMPLETE  //完成播放
    }

    private Status mStatus;

    public CustomMediaPlayer() {
        super();
        mStatus = Status.IDLE;
    }

    @Override
    public void setDataSource(String path) throws IOException, IllegalArgumentException, IllegalStateException, SecurityException {
        super.setDataSource(path);
        mStatus = Status.PREPARE;
    }

    @Override
    public void start() throws IllegalStateException {
        super.start();
        mStatus = Status.START;
    }

    @Override
    public void pause() throws IllegalStateException {
        super.pause();
        mStatus = Status.PAUSE;
    }

    @Override
    public void stop() throws IllegalStateException {
        super.stop();
        mStatus = Status.STOP;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mStatus = Status.COMPLETE;
    }

    public Status getStatus() {
        return mStatus;
    }

}
