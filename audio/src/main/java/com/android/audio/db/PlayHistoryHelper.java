package com.android.audio.db;

import com.android.audio.entity.AudioBean;
import com.android.audio.util.AudioUtil;

import java.util.List;

/**
 * Created by xuzhb on 2020/7/21
 */
public class PlayHistoryHelper {

    /**
     * 插入数据库
     *
     * @param bean          音频实体
     * @param lastPosition  上次播放的位置
     * @param totalDuration 音频总时长
     */
    public static long insert(AudioBean bean, long lastPosition, long totalDuration) {
        PlayHistory cache = new PlayHistory();
        cache.key = bean.getId();
        cache.audioBean = bean;
        cache.lastPosition = lastPosition;
        cache.totalDuration = totalDuration;
        return getPlayHistoryDao().insert(cache)[0];
    }

    //删除
    public static int delete(PlayHistory... histories) {
        return getPlayHistoryDao().delete(histories);
    }

    //更新
    public static int update(PlayHistory... histories) {
        return getPlayHistoryDao().update(histories);
    }

    //查询
    public static List<PlayHistory> queryAll() {
        return getPlayHistoryDao().queryAll();
    }

    //根据id查询
    public static PlayHistory queryById(String id) {
        return getPlayHistoryDao().queryById(id);
    }

    //根据音频查询
    public static PlayHistory queryByAudio(AudioBean bean) {
        if (bean != null) {
            return queryById(bean.getId());
        }
        return null;
    }

    //分页查询
    public static List<PlayHistory> queryByPage(int page) {
        return getPlayHistoryDao().queryByPage(page);
    }

    //根据id查询指定音频上次播放位置
    public static long queryPosition(String id) {
        return getPlayHistoryDao().queryPosition(id);
    }

    //根据音频查询上次播放位置
    public static long queryPosition(AudioBean bean) {
        if (bean != null) {
            return queryPosition(bean.getId());
        }
        return 0;
    }

    //根据id查询指定音频总时长
    public static long queryDuration(String id) {
        return getPlayHistoryDao().queryDuration(id);
    }

    //根据音频查询总时长
    public static long queryDuration(AudioBean bean) {
        if (bean != null) {
            return queryDuration(bean.getId());
        }
        return 0;
    }

    //获取指定音频上次播放位置，分秒形式
    public static String getPositionText(AudioBean bean) {
        return AudioUtil.formatTime(queryPosition(bean));
    }

    //获取指定音频总时长，分秒形式
    public static String getDurationText(AudioBean bean) {
        return AudioUtil.formatTime(queryDuration(bean));
    }

    //获取指定音频播放进度，百分比形式
    public static String getPercentText(AudioBean bean) {
        PlayHistory history = queryByAudio(bean);
        if (history != null) {
            long position = history.lastPosition;
            long duration = history.totalDuration;
            if (position >= duration) {
                return "100";
            } else {
                double percent = position / (float) duration * 100;
                return String.valueOf((int) percent);
            }
        }
        return "0";
    }

    private static PlayHistoryDao getPlayHistoryDao() {
        return AudioDataBase.getInstance().getPlayHistoryDao();
    }

}
