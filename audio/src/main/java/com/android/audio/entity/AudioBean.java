package com.android.audio.entity;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.io.Serializable;

/**
 * Created by xuzhb on 2020/6/30
 * Desc:音频实体类
 */
public class AudioBean implements Serializable {

    private static final long serialVersionUID = -8849228294348905620L;

    private String id;      //音频id
    private String url;     //音频播放链接
    private String pic;     //音频缩略图
    private String title;   //音频标题
    private long duration;  //音频时长

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof AudioBean)) {
            return false;
        }
        return TextUtils.equals(((AudioBean) obj).id, this.id);
    }
}
