package com.android.audiovisual;

import com.android.audio.entity.AudioBean;

import java.util.ArrayList;

/**
 * Created by xuzhb on 2020/7/13
 * Desc:
 */
public class AACreateData {

    public static ArrayList<AudioBean> createAudioList() {
        ArrayList<AudioBean> list = new ArrayList<>();
        list.add(createAudio("1", "http://sr-sycdn.kuwo.cn/resource/n2/33/25/2629654819.mp3",
                "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1559698289780&di=5146d48002250bf38acfb4c9b4bb6e4e&imgtype=0&src=http%3A%2F%2Fpic.baike.soso.com%2Fp%2F20131220%2Fbki-20131220170401-1254350944.jpg",
                "111111电影《不能说的秘密》主题曲,尤其以最美的不是下雨天,是与你一起躲过雨的屋檐最为经典"));
        list.add(createAudio("2", "http://sq-sycdn.kuwo.cn/resource/n1/98/51/3777061809.mp3",
                "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1559698289780&di=5146d48002250bf38acfb4c9b4bb6e4e&imgtype=0&src=http%3A%2F%2Fpic.baike.soso.com%2Fp%2F20131220%2Fbki-20131220170401-1254350944.jpg",
                "222222电影《不能说的秘密》主题曲,尤其以最美的不是下雨天,是与你一起躲过雨的屋檐最为经典"));
        return list;
    }

    private static AudioBean createAudio(String id, String url, String pic, String title) {
        AudioBean bean = new AudioBean();
        bean.setId(id);
        bean.setUrl(url);
        bean.setPic(pic);
        bean.setTitle(title);
        return bean;
    }

}
