package com.android.audio.db;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.android.audio.util.AudioUtil;

/**
 * Created by xuzhb on 2020/7/21
 * Desc:音频数据库
 */
@Database(entities = {PlayHistory.class}, version = 1, exportSchema = false)
public abstract class AudioDataBase extends RoomDatabase {

    private static class SingleTongHolder {
        private static final AudioDataBase instance = Room.databaseBuilder(AudioUtil.getContext(), AudioDataBase.class, "audio_db")
                .allowMainThreadQueries()
                .build();
    }

    public static AudioDataBase getInstance() {
        return SingleTongHolder.instance;
    }

    public abstract PlayHistoryDao getPlayHistoryDao();

}
