package com.android.audio.core;

import android.widget.Toast;

import com.android.audio.entity.AudioBean;
import com.android.audio.event.AudioCompleteEvent;
import com.android.audio.event.AudioErrorEvent;
import com.android.audio.event.EventBusUtil;
import com.android.audio.notice.AudioService;
import com.android.audio.util.AudioUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by xuzhb on 2020/7/5
 * Desc:音频播放管理器
 */
public class AudioController {

    //播放模式
    public enum PlayMode {
        LOOP,    //循环播放
        RANDOM,  //随机播放
        REPEAT   //单曲循环
    }

    private AudioPlayer mAudioPlayer;
    private PlayMode mPlayMode = PlayMode.LOOP;
    private ArrayList<AudioBean> mQueue;  //播放列表
    private int mIndex = 0;               //当前播放音频对应索引

    private AudioController() {
        EventBus.getDefault().register(this);
        mAudioPlayer = new AudioPlayer();
        mQueue = new ArrayList<>();
    }

    private static class SingletonHolder {
        private static AudioController instance = new AudioController();
    }

    public static AudioController getInstance() {
        return SingletonHolder.instance;
    }

    //释放资源
    public void release() {
        mAudioPlayer.release();
        EventBus.getDefault().unregister(this);
    }

    //列表头添加音频并播放
    public void addAudio(AudioBean bean) {
        addAudio(bean, 0);
    }

    public void addAudio(AudioBean bean, int index) {
        if (bean == null) {
            return;
        }
        if (isQueueEmpty()) {
            return;
        }
        int queryIndex = queryAudioIndex(bean);
        if (queryIndex < 0) {  //播放的音频没在播放列表中
            mQueue.add(index, bean);
            mIndex = index;
            play();
        } else {
            AudioBean nowAudio = getNowAudio();
            //切歌或者当前未播放
            if (!bean.equals(nowAudio) || !isStart()) {
                mIndex = queryIndex;
                play();
            }
        }
    }

    //移除音频
    public void removeAudio(AudioBean bean) {
        if (bean == null) {
            return;
        }
        if (isQueueEmpty()) {
            return;
        }
        AudioBean nowAudio = getNowAudio();
        if (bean.equals(nowAudio)) {  //移除的是当前正在播放的音频
            AudioBean nextAudio = getNextAudio();
            mQueue.remove(bean);
            mIndex = (nextAudio != null) ? queryAudioIndex(nextAudio) : 0;
        } else {
            int index = queryAudioIndex(bean);
            if (index > -1) {  //移除的音频在播放列表中
                mQueue.remove(bean);
                mIndex = (nowAudio != null) ? queryAudioIndex(nowAudio) : 0;
            }
        }
    }

    //播放/暂停
    public void playOrPause() {
        if (isStart()) {
            pause();
        } else if (isPause()) {
            resume();
        } else {
            play();
        }
    }

    //恢复播放
    public void resume() {
        AudioService.start();
        mAudioPlayer.resume();
    }

    //暂停播放
    public void pause() {
        mAudioPlayer.pause();
    }

    //重新加载并播放音频
    public void play() {
        AudioBean bean = getNowAudio();
        load(bean);
    }

    //播放下一曲
    public void next() {
        AudioBean bean = getNextAudio();
        load(bean);
    }

    //播放上一曲
    public void previous() {
        AudioBean bean = getPreAudio();
        load(bean);
    }

    //获取当前音频
    public AudioBean getNowAudio() {
        return getAudioByIndex(mIndex);
    }

    //获取播放状态
    public CustomMediaPlayer.Status getStatus() {
        return mAudioPlayer.getStatus();
    }

    //是否正在缓冲
    public boolean isPrepare() {
        return mAudioPlayer.isPrepare();
    }

    //是否正在播放
    public boolean isStart() {
        return mAudioPlayer.isStart();
    }

    //是否暂停播放
    public boolean isPause() {
        return mAudioPlayer.isPause();
    }

    //获取当前播放模式
    public PlayMode getPlayMode() {
        return mPlayMode;
    }

    //设置播放模式
    public void setPlayMode(PlayMode playMode) {
        mPlayMode = playMode;
        EventBusUtil.postPlayMode(playMode);
    }

    //获取播放列表
    public ArrayList<AudioBean> getQueue() {
        return mQueue == null ? new ArrayList<AudioBean>() : mQueue;
    }

    //设置播放列表和播放位置为指定音频对应的位置
    public void setQueue(ArrayList<AudioBean> queue, AudioBean bean) {
        if (queue == null || queue.isEmpty() || bean == null) {
            return;
        }
        int index = queue.indexOf(bean);
        if (index < 0) {  //如果没有找到，默认从头开始播放
            queue.add(bean);
            index = 0;
        }
        setQueue(queue, index);
    }

    //设置播放列表，默认播放位置为第一首
    public void setQueue(ArrayList<AudioBean> queue) {
        setQueue(queue, 0);
    }

    //设置播放列表和播放位置
    public void setQueue(ArrayList<AudioBean> queue, int index) {
        if (mQueue == null) {
            mQueue = new ArrayList<>();
        } else {
            mQueue.clear();
        }
        mQueue.addAll(queue);
        mIndex = (index < 0) ? 0 : Math.min(index, mQueue.size() - 1);
    }

    //添加播放列表，默认播放位置为第一首
    public void addQueue(ArrayList<AudioBean> queue) {
        addQueue(queue, 0);
    }

    //添加播放列表，并设置播放位置
    public void addQueue(ArrayList<AudioBean> queue, int index) {
        mQueue.addAll(queue);
        mIndex = (index < 0) ? 0 : Math.min(index, mQueue.size() - 1);
    }

    //获取当前播放时间位置
    public int getCurrentPosition() {
        return mAudioPlayer.getCurrentPosition();
    }

    //获取总时长
    public int getDuration() {
        return mAudioPlayer.getDuration();
    }

    private int queryAudioIndex(AudioBean bean) {
        return mQueue.indexOf(bean);
    }

    private void load(AudioBean bean) {
        if (bean != null) {
            AudioService.start();
            mAudioPlayer.load(bean);
        }
    }

    //播放列表是否为空
    private boolean isQueueEmpty() {
        if (mQueue == null || mQueue.size() <= 0) {
            EventBusUtil.postQueueEmpty();
            return true;
        }
        return false;
    }

    //上一曲
    private AudioBean getPreAudio() {
        switch (mPlayMode) {
            case LOOP:
                mIndex = (mIndex + mQueue.size() - 1) % mQueue.size();
                return getAudioByIndex(mIndex);
            case RANDOM:
                mIndex = new Random().nextInt(mQueue.size()) % mQueue.size();
                return getAudioByIndex(mIndex);
            case REPEAT:
                return getAudioByIndex(mIndex);
        }
        return null;
    }

    //下一曲
    private AudioBean getNextAudio() {
        switch (mPlayMode) {
            case LOOP:
                mIndex = (mIndex + 1) % mQueue.size();
                return getAudioByIndex(mIndex);
            case RANDOM:
                mIndex = new Random().nextInt(mQueue.size()) % mQueue.size();
                return getAudioByIndex(mIndex);
            case REPEAT:
                return getAudioByIndex(mIndex);
        }
        return null;
    }

    private AudioBean getAudioByIndex(int index) {
        if (isQueueEmpty()) {
            EventBusUtil.postQueueEmpty();
            return null;
        }
        if (index >= 0 && index < mQueue.size()) {
            return mQueue.get(index);
        }
        return null;
    }

    //播放完毕
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioCompleteEvent(AudioCompleteEvent event) {
        next();
    }

    //播放出错
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioErrorEvent(AudioErrorEvent event) {
        Toast.makeText(AudioUtil.getContext(), "播放出错啦！", Toast.LENGTH_SHORT).show();
        pause();
    }

}
