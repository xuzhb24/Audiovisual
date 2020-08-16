package com.android.audio.event;

import com.android.audio.core.AudioController;
import com.android.audio.core.CustomMediaPlayer;
import com.android.audio.entity.AudioBean;

import org.greenrobot.eventbus.EventBus;

public class EventBusUtil {

    //加载音频
    public static void postLoad(AudioBean audioBean) {
        EventBus.getDefault().post(new AudioLoadEvent(audioBean));
    }

    //开始播放
    public static void postStart() {
        EventBus.getDefault().post(new AudioStartEvent());
    }

    //暂停播放
    public static void postPause() {
        EventBus.getDefault().post(new AudioPauseEvent());
    }

    //更新播放进度
    public static void postProgress(CustomMediaPlayer.Status status, int position, int duration) {
        EventBus.getDefault().post(new AudioProgressEvent(status, position, duration));
    }

    //播放出错
    public static void postError(String msg) {
        postError(msg, -1, -1);
    }

    //播放出错
    public static void postError(String msg, int what, int extra) {
        EventBus.getDefault().post(new AudioErrorEvent(msg, what, extra));
    }

    //播放完毕
    public static void postComplete() {
        EventBus.getDefault().post(new AudioCompleteEvent());
    }

    //释放资源
    public static void postRelease() {
        EventBus.getDefault().post(new AudioReleaseEvent());
    }

    //关闭底部音频播放条
    public static void postBarClose() {
        AudioController.getInstance().pause();
        EventBus.getDefault().post(new AudioBarCloseEvent());
    }

    //切换播放模式
    public static void postPlayMode(AudioController.PlayMode playMode) {
        EventBus.getDefault().post(new AudioPlayModeEvent(playMode));
    }

    //播放队列为空
    public static void postQueueEmpty() {
        EventBus.getDefault().post(new AudioQueueEmptyEvent());
    }

}
