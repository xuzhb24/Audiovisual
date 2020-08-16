package com.android.audio.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.android.audio.R;
import com.android.audio.core.AudioController;
import com.android.audio.entity.AudioBean;
import com.android.audio.event.AudioErrorEvent;
import com.android.audio.event.AudioLoadEvent;
import com.android.audio.event.AudioPauseEvent;
import com.android.audio.event.AudioProgressEvent;
import com.android.audio.event.AudioQueueEmptyEvent;
import com.android.audio.event.AudioStartEvent;
import com.android.audio.event.EventBusUtil;
import com.android.audio.util.AudioUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by xuzhb on 2020/7/9
 * Desc:底部音频播放条
 */
public class BottomPlayBar extends FrameLayout {

    private ImageView mAlbumIv;
    private TextView mTitleTv;
    private TextView mDurationTv;
    private ImageView mPlayIv;
    private ArcView mLoadingAv;
    private ProgressBar mProgressPb;

    private AudioBean mAudioBean;
    private long mAnimCurrentPlayTime;       //当前动画播放进度
    private ObjectAnimator mRotateAnimator;  //封面旋转动画

    public BottomPlayBar(@NonNull Context context) {
        this(context, null);
    }

    public BottomPlayBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomPlayBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        View layout = LayoutInflater.from(context).inflate(R.layout.bottom_play_bar, this);
        mAlbumIv = layout.findViewById(R.id.album_iv);
        mTitleTv = layout.findViewById(R.id.title_tv);
        mDurationTv = layout.findViewById(R.id.duration_tv);
        mPlayIv = layout.findViewById(R.id.play_iv);
        mLoadingAv = layout.findViewById(R.id.loading_av);
        mProgressPb = layout.findViewById(R.id.progress_pb);
        initAnim();
        //打开音频详情页
        layout.findViewById(R.id.center_ll).setOnClickListener(v -> AudioUtil.openAudioDetail((Activity) context));
        //播放/暂停
        layout.findViewById(R.id.play_fl).setOnClickListener(v -> AudioController.getInstance().playOrPause());
        //关闭播放条
        layout.findViewById(R.id.close_iv).setOnClickListener(v -> EventBusUtil.postBarClose());
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBus.getDefault().register(this);
        showLoadView();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnim();
        EventBus.getDefault().unregister(this);
    }

    //加载音频
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioLoadEvent(AudioLoadEvent event) {
        showLoadView();
        mLoadingAv.startRotate();
        stopAnim();
    }

    //播放音频
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioStartEvent(AudioStartEvent event) {
        showPlayView();
        startAnim();
    }

    //暂停播放
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioPauseEvent(AudioPauseEvent event) {
        showPauseView();
        stopAnim();
    }

    //更新播放进度
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioProgressEvent(AudioProgressEvent event) {
        int duration = event.getDuration();
        int position = event.getPosition();
        mDurationTv.setText(getProgress(position, duration));
        mProgressPb.setProgress(position);
        mProgressPb.setMax(duration);
        mAudioBean = AudioController.getInstance().getNowAudio();
        if (mAudioBean != null) {
            loadAlbum(mAlbumIv, mAudioBean.getPic());
            mTitleTv.setText(mAudioBean.getTitle());
            if (AudioController.getInstance().isStart()) {
                showPlayView();
            } else {
                showPauseView();
            }
        }
    }

    //播放出错
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void on(AudioErrorEvent event) {
        EventBusUtil.postBarClose();
    }

    //播放队列被清空
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioQueueEmptyEvent(AudioQueueEmptyEvent event) {
        EventBusUtil.postBarClose();
    }

    private void showLoadView() {
        mAudioBean = AudioController.getInstance().getNowAudio();
        mAlbumIv.setRotation(0);
        mDurationTv.setText(getProgress(0, 0));
        mPlayIv.setImageResource(R.drawable.ic_bar_pause);
        mProgressPb.setProgress(0);
        if (mAudioBean != null) {
            loadAlbum(mAlbumIv, mAudioBean.getPic());
            mTitleTv.setText(mAudioBean.getTitle());
        } else {
            mAlbumIv.setImageResource(R.drawable.ic_bar_default);
            mTitleTv.setText("让生活充满音乐");
        }
    }

    //播放
    private void showPlayView() {
        mPlayIv.setImageResource(R.drawable.ic_bar_play);
        mLoadingAv.stopRotate();
    }

    //暂停
    private void showPauseView() {
        mPlayIv.setImageResource(R.drawable.ic_bar_pause);
        mLoadingAv.stopRotate();
    }

    //设置封面
    private void loadAlbum(ImageView imageView, String url) {
        Glide.with(this)
                .asBitmap()
                .placeholder(R.drawable.ic_bar_default)
                .error(R.drawable.ic_bar_default)
                .load(url)
                .into(new BitmapImageViewTarget(imageView) {
                    @Override
                    protected void setResource(final Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(imageView.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        imageView.setImageDrawable(circularBitmapDrawable);
                    }
                });
    }

    //获取当前播放进度
    private String getProgress(long currentProgress, long totalProgress) {
        return AudioUtil.formatTime(currentProgress) + " / " + AudioUtil.formatTime(totalProgress);
    }

    //初始化封面旋转动画
    private void initAnim() {
        mRotateAnimator = ObjectAnimator.ofFloat(mAlbumIv, View.ROTATION.getName(), 0f, 360);
        mRotateAnimator.setDuration(10000);
        mRotateAnimator.setInterpolator(new LinearInterpolator());
        mRotateAnimator.setRepeatCount(ValueAnimator.INFINITE);
    }

    //继续动画
    private void startAnim() {
        mRotateAnimator.start();
        mRotateAnimator.setCurrentPlayTime(mAnimCurrentPlayTime);
    }

    //停止动画
    private void stopAnim() {
        mAnimCurrentPlayTime = mRotateAnimator.getCurrentPlayTime();
        mRotateAnimator.cancel();
    }

}
