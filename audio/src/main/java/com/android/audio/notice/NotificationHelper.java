package com.android.audio.notice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.android.audio.R;
import com.android.audio.core.AudioController;
import com.android.audio.entity.AudioBean;
import com.android.audio.util.AudioUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

/**
 * Created by xuzhb on 2020/7/11
 * Desc:音频通知
 */
public class NotificationHelper {

    public static final String CHANNEL_ID = "channel_id_audio";
    public static final String CHANNEL_NAME = "channel_name_audio";
    public static final int NOTIFICATION_ID = 0x111;

    private Notification mNotification;
    private RemoteViews mRemoteViews;
    private NotificationManager mManager;
    private AudioBean mAudioBean;

    private NotificationHelper() {

    }

    public static NotificationHelper getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static NotificationHelper instance = new NotificationHelper();
    }

    public void init() {
        mManager = (NotificationManager) AudioUtil.getContext().getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mAudioBean = AudioController.getInstance().getNowAudio();
        createNotification();
    }

    //创建Notification
    private void createNotification() {
        if (mNotification == null) {
            createRemoteViews();  //创建布局
            Intent intent = new Intent();
            intent.setAction(AudioUtil.ACTION_AUDIO_DETAIL);
            PendingIntent pendingIntent = PendingIntent.getActivity(AudioUtil.getContext(),
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            //适配安卓8.0的消息渠道
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                channel.enableLights(false);
                channel.enableVibration(false);
                mManager.createNotificationChannel(channel);
            }
            NotificationCompat.Builder builder = new NotificationCompat.Builder(AudioUtil.getContext(), CHANNEL_ID)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_logo)
                    .setCustomContentView(mRemoteViews);
            mNotification = builder.build();
            showLoadStatus(mAudioBean);
        }
    }

    //创建Notification的布局
    private void createRemoteViews() {
        mRemoteViews = new RemoteViews(AudioUtil.getContext().getPackageName(), R.layout.notification_layout);
        mRemoteViews.setTextViewText(R.id.title_tv, mAudioBean.getTitle());
        //播放
        mRemoteViews.setOnClickPendingIntent(R.id.play_fl, createAudioIntent(NoticeAction.ACTION_PLAY, 1));
        //上一曲
        mRemoteViews.setOnClickPendingIntent(R.id.previous_fl, createAudioIntent(NoticeAction.ACTION_PRE, 2));
        //下一曲
        mRemoteViews.setOnClickPendingIntent(R.id.next_fl, createAudioIntent(NoticeAction.ACTION_NEXT, 3));
        //关闭
        mRemoteViews.setOnClickPendingIntent(R.id.close_fl, createAudioIntent(NoticeAction.ACTION_CANCEL, 4));
    }

    private PendingIntent createAudioIntent(String action, int requestCode) {
        Intent intent = new Intent(NoticeAction.NOTIFICATION_ACTION);
        intent.putExtra(NoticeAction.EXTRA_NAME, action);
        return PendingIntent.getBroadcast(AudioUtil.getContext(), requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public Notification getNotification() {
        return mNotification;
    }

    //加载音频
    public void showLoadStatus(AudioBean bean) {
        mAudioBean = bean;
        if (mRemoteViews != null) {
            mRemoteViews.setImageViewResource(R.id.play_iv, R.drawable.ic_notice_play);
            mRemoteViews.setTextViewText(R.id.title_tv, bean.getTitle());
            loadPic(AudioUtil.getContext(), bean.getPic(), R.id.image_iv, mRemoteViews, mNotification, NOTIFICATION_ID);
            mManager.notify(NOTIFICATION_ID, mNotification);
        }
    }

    private void loadPic(Context context, String url, int viewId, RemoteViews remoteViews, Notification notification, int notificationId) {
        NotificationTarget target = new NotificationTarget(context, viewId, remoteViews, notification, notificationId);
        Glide.with(context)
                .asBitmap()
                .load(url)
                .placeholder(R.drawable.ic_logo)
                .error(R.drawable.ic_logo)
                .transition(withCrossFade())
                .fitCenter()
                .into(target);
    }

    //播放中
    public void showPlayStatus() {
        if (mRemoteViews != null) {
            mRemoteViews.setImageViewResource(R.id.play_iv, R.drawable.ic_notice_play);
            mManager.notify(NOTIFICATION_ID, mNotification);
        }
    }

    //暂停播放
    public void showPauseStatus() {
        if (mRemoteViews != null) {
            mRemoteViews.setImageViewResource(R.id.play_iv, R.drawable.ic_notice_pause);
            mManager.notify(NOTIFICATION_ID, mNotification);
        }
    }

    //移除通知
    public void cancel() {
        mManager.cancel(NOTIFICATION_ID);
    }

}
