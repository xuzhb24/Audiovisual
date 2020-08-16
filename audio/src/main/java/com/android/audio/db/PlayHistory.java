package com.android.audio.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.android.audio.entity.AudioBean;

import java.io.Serializable;

/**
 * Created by xuzhb on 2020/7/21
 * Desc:播放记录
 */
@Entity(tableName = "play_history")
public class PlayHistory implements Serializable {

    @PrimaryKey(autoGenerate = false)
    @NonNull
    public String key;

    @Embedded
    public AudioBean audioBean;

    @ColumnInfo(name = "last_position")
    public long lastPosition;  //上次播放的位置

    @ColumnInfo(name = "total_duration")
    public long totalDuration;  //总时长

}
