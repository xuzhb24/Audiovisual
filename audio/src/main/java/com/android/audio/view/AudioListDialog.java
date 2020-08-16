package com.android.audio.view;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.audio.R;
import com.android.audio.adapter.AudioListAdapter;
import com.android.audio.core.AudioController;
import com.android.audio.entity.AudioBean;
import com.android.audio.event.AudioLoadEvent;
import com.android.audio.event.AudioPlayModeEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by xuzhb on 2020/7/11
 * Desc:音频列表弹窗
 */
public class AudioListDialog extends DialogFragment {

    private ImageView mModeIv;
    private TextView mModeTv;
    private AudioListAdapter mAdapter;

    public static AudioListDialog newInstance() {
        return new AudioListDialog();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_audio_list, container, false);
        initView(view);
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initParams();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    //初始化参数
    private void initParams() {
        Window window = getDialog().getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.dimAmount = 0.2f;
            params.gravity = Gravity.BOTTOM;
            //设置dialog宽度
            params.width = getContext().getResources().getDisplayMetrics().widthPixels;
            //设置dialog高度
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            //设置dialog动画
            window.setWindowAnimations(R.style.AnimTranslateBottom);
            window.setAttributes(params);
        }
        setCancelable(true);
    }

    private void initView(View view) {
        AudioController.PlayMode playMode = AudioController.getInstance().getPlayMode();
        List<AudioBean> list = AudioController.getInstance().getQueue();  //播放列表
        mModeTv = view.findViewById(R.id.mode_tv);
        mModeIv = view.findViewById(R.id.mode_iv);
        mModeTv.setOnClickListener(v -> {
            //调用切换播放模式事件
            switch (playMode) {
                case LOOP:
                    AudioController.getInstance().setPlayMode(AudioController.PlayMode.RANDOM);
                    break;
                case RANDOM:
                    AudioController.getInstance().setPlayMode(AudioController.PlayMode.REPEAT);
                    break;
                case REPEAT:
                    AudioController.getInstance().setPlayMode(AudioController.PlayMode.LOOP);
                    break;
            }
        });
        updatePlayMode(playMode);
        RecyclerView recyclerView = view.findViewById(R.id.recycler);
        mAdapter = new AudioListAdapter(list);
        recyclerView.setAdapter(mAdapter);
        view.findViewById(R.id.close_tv).setOnClickListener(v -> dismiss());
    }

    //加载音频
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioLoadEvent(AudioLoadEvent event) {
        mAdapter.notifyDataSetChanged();
    }

    //切换播放模式
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioPlayModeEvent(AudioPlayModeEvent event) {
        updatePlayMode(event.getPlayMode());
    }

    //切换播放模式
    private void updatePlayMode(AudioController.PlayMode playMode) {
        switch (playMode) {
            case LOOP:
                mModeIv.setImageResource(R.drawable.ic_dialog_loop);
                mModeTv.setText("循环播放");
                break;
            case RANDOM:
                mModeIv.setImageResource(R.drawable.ic_dialog_random);
                mModeTv.setText("随机播放");
                break;
            case REPEAT:
                mModeIv.setImageResource(R.drawable.ic_dialog_once);
                mModeTv.setText("单曲循环");
                break;
        }
    }

    public void show(FragmentManager manager) {
        super.show(manager, AudioListDialog.class.getName());
    }

}
