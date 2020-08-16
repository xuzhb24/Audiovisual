package com.android.audio.core;

import android.content.Context;
import android.media.AudioManager;

/**
 * Created by xuzhb on 2020/7/4
 * Desc:音频焦点管理
 */
public class AudioFocusManager implements AudioManager.OnAudioFocusChangeListener {

    private OnAudioFocusListener mOnAudioFocusListener;
    private AudioManager mAudioManager;

    public AudioFocusManager(Context context, OnAudioFocusListener listener) {
        mAudioManager = (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        mOnAudioFocusListener = listener;
    }

    public boolean requestAudioFocus() {
        //AUDIOFOCUS_REQUEST_GRANTED：申请成功
        //AUDIOFOCUS_REQUEST_FAILED：申请失败
        return mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    public void abandonAudioFocus() {
        mAudioManager.abandonAudioFocus(this);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        if (mOnAudioFocusListener == null) {
            return;
        }
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:  //获取焦点，一般会长期占有
                mOnAudioFocusListener.audioFocusGain();
                break;
            case AudioManager.AUDIOFOCUS_LOSS:  //失去焦点并将持续很长时间，如被其他播放器抢占
                mOnAudioFocusListener.audioFocusLoss();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:  //暂时失去焦点，并会很快再次获得，必须停止播放，可以不释放资源，如来电
                mOnAudioFocusListener.audioFocusLossTransient();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:  //暂时失去焦点，但是可以继续播放，不过要降低音量
                mOnAudioFocusListener.audioFocusLossTransientCanDuck();
                break;
        }
    }

    //音频焦点改变回调接口
    public interface OnAudioFocusListener {

        //获得焦点
        void audioFocusGain();

        //永久失去焦点
        void audioFocusLoss();

        //短暂失去焦点
        void audioFocusLossTransient();

        //瞬间失去焦点
        void audioFocusLossTransientCanDuck();

    }

}
