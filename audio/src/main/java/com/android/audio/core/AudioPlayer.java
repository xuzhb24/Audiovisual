package com.android.audio.core;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.audio.db.PlayHistoryHelper;
import com.android.audio.entity.AudioBean;
import com.android.audio.event.EventBusUtil;
import com.android.audio.util.AudioUtil;

import java.io.IOException;

/**
 * Created by xuzhb on 2020/7/4
 * Desc:音频播放器，加入了焦点和播放状态等的管理
 */
public class AudioPlayer implements MediaPlayer.OnPreparedListener, MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, AudioFocusManager.OnAudioFocusListener {

    private static final String TAG = "AudioPlayer";
    private static final int UPDATE_MSG = 0x01;
    private static final int TIME_INTERVAL = 100;

    private AudioBean mCurrentAudio;         //当前播放的音频
    private CustomMediaPlayer mMediaPlayer;  //负责播放的MediaPlayer
    private WifiManager.WifiLock mWifiLock;  //阻止wifi进入睡眠状态，使wifi一直处于活跃状态
    private AudioFocusManager mAudioFocusManager;  //音频焦点
    private boolean isPausedByFocusLossTransient;  //是否由于短暂失去焦点停止播放，如接听电话

    //播放进度更新
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case UPDATE_MSG:
                    if (isStart() || isPause()) {  //暂停也更新进度，防止UI不同步
                        EventBusUtil.postProgress(getStatus(), getCurrentPosition(), getDuration());
                        sendEmptyMessageDelayed(UPDATE_MSG, TIME_INTERVAL);
                    }
                    break;
            }
        }
    };

    public AudioPlayer() {
        init();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        log("onPrepared");
        start();  //开始播放
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {  //percent是缓冲的百分比，取值0到100
        log("onBufferingUpdate,percent:" + percent);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        log("onError:" + what + " " + extra);
        EventBusUtil.postError("onError", what, extra);
        return true;  //返回false会接着调用onCompletion，返回true则不会
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        log("onCompletion");
        EventBusUtil.postComplete();
    }

    //重新获得焦点
    @Override
    public void audioFocusGain() {
        log("audioFocusGain");
        setVolume(1.0f, 1.0f);
        if (isPausedByFocusLossTransient) {
            resume();
        }
        isPausedByFocusLossTransient = false;
    }

    //永久失去焦点
    @Override
    public void audioFocusLoss() {
        log("audioFocusLoss");
        pause();
    }

    //短暂失去焦点，如接听电话
    @Override
    public void audioFocusLossTransient() {
        log("audioFocusLossTransient");
        pause();
        isPausedByFocusLossTransient = true;
    }

    //短暂失去焦点，但可以继续播放
    @Override
    public void audioFocusLossTransientCanDuck() {
        log("audioFocusLossTransientCanDuck");
        setVolume(0.5f, 0.5f);
    }

    //初始化播放器，和release配对调用
    private void init() {
        //初始化MediaPlayer
        mMediaPlayer = new CustomMediaPlayer();
        mMediaPlayer.setWakeMode(AudioUtil.getContext(), PowerManager.PARTIAL_WAKE_LOCK);  //使用唤醒锁
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        //初始化WiFi锁
        mWifiLock = ((WifiManager) AudioUtil.getContext().getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, TAG);
        //初始化音频焦点
        mAudioFocusManager = new AudioFocusManager(AudioUtil.getContext(), this);
    }

    //加载音频，播放调用顺序：load->prepareAsync->回调onPrepared->start
    public void load(AudioBean audioBean) {
        try {
            if (mCurrentAudio != null && !mCurrentAudio.equals(audioBean)) {  //切歌或者播放完毕
                savePlayHistory();
            }
            mCurrentAudio = audioBean;
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(audioBean.getUrl());
            mMediaPlayer.prepareAsync();  //异步缓冲，到真正开始播放根据网络情况需要一段时间
            EventBusUtil.postLoad(audioBean);
        } catch (IOException e) {
            e.printStackTrace();
            EventBusUtil.postError(e.getMessage());
        }
    }

    //播放音频，外部调用
    public void resume() {
        if (isPause()) {
            start();
        }
    }

    //开始播放
    private void start() {
        //获取音频焦点
        if (!mAudioFocusManager.requestAudioFocus()) {
            Log.e(TAG, "获取音频焦点失败");
        }
        mMediaPlayer.start();
        //启用WiFi锁
        mWifiLock.acquire();
        //更新进度
        mHandler.sendEmptyMessage(UPDATE_MSG);
        EventBusUtil.postStart();
    }

    //跳转指定位置播放
    public void seekTo(int position) {
        mMediaPlayer.seekTo(position);
    }

    //暂停播放
    public void pause() {
        if (isStart()) {
            mMediaPlayer.pause();
        } else if (isPrepare()) {
            mMediaPlayer.reset();  //处理prepareAsync中音频pause()方法无法起效问题
        }
        savePlayHistory();  //暂停时保存播放记录
        //关闭WiFi锁
        if (mWifiLock.isHeld()) {
            mWifiLock.release();
        }
        //取消音频焦点
        mAudioFocusManager.abandonAudioFocus();
        EventBusUtil.postPause();
    }

    //释放资源
    public void release() {
        if (mWifiLock != null && mWifiLock.isHeld()) {
            mWifiLock.release();
        }
        if (mAudioFocusManager != null) {
            mAudioFocusManager.abandonAudioFocus();
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
        mWifiLock = null;
        mAudioFocusManager = null;
        mMediaPlayer = null;
        mHandler.removeCallbacksAndMessages(null);
        EventBusUtil.postRelease();
    }

    //获取播放器的状态
    public CustomMediaPlayer.Status getStatus() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getStatus();
        } else {
            return CustomMediaPlayer.Status.IDLE;
        }
    }

    //是否正在缓冲
    public boolean isPrepare() {
        return getStatus() == CustomMediaPlayer.Status.PREPARE;
    }

    //是否正在播放
    public boolean isStart() {
        return getStatus() == CustomMediaPlayer.Status.START;
    }

    //是否暂停播放
    public boolean isPause() {
        return getStatus() == CustomMediaPlayer.Status.PAUSE;
    }

    //获取当前音频播放位置
    public int getCurrentPosition() {
        if (isStart() || isPause()) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    //获取当前音频总时长
    public int getDuration() {
        if (isStart() || isPause()) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    //设置音量
    public void setVolume(float left, float right) {
        mMediaPlayer.setVolume(left, right);
    }

    //保存播放记录
    public void savePlayHistory() {
        if (mCurrentAudio != null) {
            log("play history：" + mCurrentAudio.getId() + "  " + getCurrentPosition() + "  "
                    + getDuration() + "  " + (getCurrentPosition() / (float) getDuration()));
            PlayHistoryHelper.insert(mCurrentAudio, getCurrentPosition(), getDuration());
        }
    }

    private void log(String msg) {
        Log.w(TAG, msg);
    }

}
