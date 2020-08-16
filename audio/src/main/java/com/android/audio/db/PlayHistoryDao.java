package com.android.audio.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Created by xuzhb on 2020/7/21
 */
@Dao
public interface PlayHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insert(PlayHistory... histories);

    @Delete
    int delete(PlayHistory... histories);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int update(PlayHistory... histories);

    @Query("select * from play_history")
    List<PlayHistory> queryAll();

    @Query("select * from play_history where `key` == :id")
    PlayHistory queryById(String id);

    @Query("select * from play_history limit 20 offset :page *20")
    List<PlayHistory> queryByPage(int page);

    @Query("select last_position from play_history where `key` == :id")
    long queryPosition(String id);

    @Query("select total_duration from play_history where `key` == :id")
    long queryDuration(String id);

}
