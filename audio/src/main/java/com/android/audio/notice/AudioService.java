package com.android.audio.notice;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.android.audio.core.AudioController;
import com.android.audio.event.AudioBarCloseEvent;
import com.android.audio.event.AudioLoadEvent;
import com.android.audio.event.AudioPauseEvent;
import com.android.audio.event.AudioReleaseEvent;
import com.android.audio.event.AudioStartEvent;
import com.android.audio.event.EventBusUtil;
import com.android.audio.util.AudioUtil;
import com.android.audio.util.ServiceUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by xuzhb on 2020/7/11
 * Desc:音频后台服务
 */
public class AudioService extends Service {

    private NotificationReceiver mReceiver;

    public static void start() {
        if (!ServiceUtil.isServiceRunning(AudioUtil.getContext(), AudioService.class)) {
            ServiceUtil.startService(AudioUtil.getContext(), AudioService.class);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        registerReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationHelper.getInstance().init();
        startForeground(NotificationHelper.NOTIFICATION_ID, NotificationHelper.getInstance().getNotification());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterReceiver();
        EventBus.getDefault().unregister(this);
        stopForeground(true);
        NotificationHelper.getInstance().cancel();
    }

    //加载音频
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioLoadEvent(AudioLoadEvent event) {
        NotificationHelper.getInstance().showLoadStatus(event.getAudioBean());
    }

    //播放音频
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioStartEvent(AudioStartEvent event) {
        NotificationHelper.getInstance().showPlayStatus();
    }

    //暂停播放
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioPauseEvent(AudioPauseEvent event) {
        NotificationHelper.getInstance().showPauseStatus();
    }

    //关闭音频播放条
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioBarCloseEvent(AudioBarCloseEvent event) {
        ServiceUtil.stopService(getApplicationContext(), AudioService.class);
    }

    //释放资源
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioReleaseEvent(AudioReleaseEvent event) {
        ServiceUtil.stopService(getApplicationContext(), AudioService.class);
    }

    private void registerReceiver() {
        if (mReceiver == null) {
            mReceiver = new NotificationReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(NoticeAction.NOTIFICATION_ACTION);
            registerReceiver(mReceiver, filter);
        }
    }

    private void unRegisterReceiver() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    //接收Notification发送的广播
    public static class NotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && NoticeAction.NOTIFICATION_ACTION.equals(intent.getAction())) {
                String extra = intent.getStringExtra(NoticeAction.EXTRA_NAME);
                if (!TextUtils.isEmpty(extra)) {
                    switch (extra) {
                        case NoticeAction.ACTION_PLAY:
                            AudioController.getInstance().playOrPause();
                            break;
                        case NoticeAction.ACTION_PRE:
                            AudioController.getInstance().previous();
                            break;
                        case NoticeAction.ACTION_NEXT:
                            AudioController.getInstance().next();
                            break;
                        case NoticeAction.ACTION_CANCEL:
                            EventBusUtil.postBarClose();
                            break;
                    }
                }
            }
        }
    }

}
