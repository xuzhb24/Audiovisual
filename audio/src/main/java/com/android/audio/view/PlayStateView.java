package com.android.audio.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.audio.R;
import com.android.audio.core.AudioController;
import com.android.audio.entity.AudioBean;
import com.android.audio.event.AudioBarCloseEvent;
import com.android.audio.event.AudioLoadEvent;
import com.android.audio.event.AudioPauseEvent;
import com.android.audio.event.AudioStartEvent;
import com.android.audio.util.AudioUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by xuzhb on 2020/7/7
 * Desc:音频播放状态
 */
public class PlayStateView extends LinearLayout {

    private float iconWidth;           //左侧图标宽度
    private float iconHeight;          //左侧图标高度
    private Drawable defaultIcon;      //未播放的图标
    private Drawable playingIcon;      //播放中的图标
    private float iconMarginTop;       //图标上边距
    private float iconMarginRight;     //图标右边距
    private String descText;           //右侧文本
    private float descTextSize;        //文本字体大小
    private int descDefaultTextColor;  //未播放的文本字体颜色
    private int descPlayingTextColor;  //播放中的文本字体颜色
    private int descMinLines;          //文本最小行数，默认为2
    private int descMaxLines;          //文本最多行数，默认为2
    private float descTextMarginTop;   //文本的上边距
    private float descTextLineSpacingExtra;  //文本字体间距

    private String mId;  //绑定的音频id

    public String getDescText() {
        return descText;
    }

    public void setDescText(String descText) {
        this.descText = descText;
        mDescTv.setText(descText);
    }

    //和指定的音频绑定
    public void bind(String id) {
        this.mId = id;
    }

    private LinearLayout mRootLl;
    private FrameLayout mStateFl;
    private ImageView mStateIv;
    private TextView mDescTv;

    public PlayStateView(Context context) {
        this(context, null);
    }

    public PlayStateView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayStateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            parseAttributes(context, attrs);
        }
        initView(context);
    }

    //获取自定义属性集
    private void parseAttributes(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PlayStateView);
        iconWidth = ta.getDimension(R.styleable.PlayStateView_iconWidth, AudioUtil.dp2px(13));
        iconHeight = ta.getDimension(R.styleable.PlayStateView_iconHeight, AudioUtil.dp2px(13));
        defaultIcon = ta.getDrawable(R.styleable.PlayStateView_defaultIcon);
        if (defaultIcon == null) {
            defaultIcon = context.getResources().getDrawable(R.drawable.ic_state_default);
        }
        playingIcon = ta.getDrawable(R.styleable.PlayStateView_playingIcon);
        if (playingIcon == null) {
            playingIcon = context.getResources().getDrawable(R.drawable.ic_state_playing);
        }
        iconMarginTop = ta.getDimension(R.styleable.PlayStateView_iconMarginTop, AudioUtil.dp2px(3));
        iconMarginRight = ta.getDimension(R.styleable.PlayStateView_iconMarginRight, AudioUtil.dp2px(8f));
        descText = ta.getString(R.styleable.PlayStateView_descText);
        descTextSize = ta.getDimension(R.styleable.PlayStateView_descTextSize, AudioUtil.sp2px(14));
        descDefaultTextColor = ta.getColor(R.styleable.PlayStateView_descDefaultTextColor, Color.parseColor("#BBBBBB"));
        descPlayingTextColor = ta.getColor(R.styleable.PlayStateView_descPlayingTextColor, Color.parseColor("#26D59E"));
        descMinLines = ta.getInt(R.styleable.PlayStateView_descMinLines, 1);
        descMaxLines = ta.getInt(R.styleable.PlayStateView_descMaxLines, Integer.MAX_VALUE);
        descTextMarginTop = ta.getDimension(R.styleable.PlayStateView_descTextMarginTop, 0);
        descTextLineSpacingExtra = ta.getDimension(R.styleable.PlayStateView_descTextLineSpacingExtra, AudioUtil.dp2px(3));
        ta.recycle();
    }

    private void initView(Context context) {
        View layout = LayoutInflater.from(context).inflate(R.layout.play_state_view, this);
        mRootLl = layout.findViewById(R.id.root_ll);
        mStateFl = layout.findViewById(R.id.state_fl);
        mStateIv = layout.findViewById(R.id.state_iv);
        mDescTv = layout.findViewById(R.id.desc_tv);
        FrameLayout.LayoutParams ivParams = (FrameLayout.LayoutParams) mStateIv.getLayoutParams();
        ivParams.width = (int) iconWidth;
        ivParams.height = (int) iconHeight;
        ivParams.topMargin = (int) iconMarginTop;
        ivParams.rightMargin = (int) iconMarginRight;
        mStateIv.setLayoutParams(ivParams);
        mStateIv.setImageDrawable(defaultIcon);
        mDescTv.setText(descText);
        mDescTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, descTextSize);
        mDescTv.setTextColor(descDefaultTextColor);
        mDescTv.setMinLines(descMinLines);
        mDescTv.setMaxLines(descMaxLines);
        mDescTv.setEllipsize(TextUtils.TruncateAt.END);
        LinearLayout.LayoutParams tvParams = (LinearLayout.LayoutParams) mDescTv.getLayoutParams();
        tvParams.topMargin = (int) descTextMarginTop;
        mDescTv.setLayoutParams(tvParams);
        mDescTv.setLineSpacing(descTextLineSpacingExtra, 1);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBus.getDefault().register(this);
        updatePlayState(isNowAudio());
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioLoadEvent(AudioLoadEvent event) {
        updatePlayState(isNowAudio());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioPauseEvent(AudioPauseEvent event) {
        updatePlayState(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioStartEvent(AudioStartEvent event) {
        updatePlayState(isNowAudio());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioStopEvent(AudioBarCloseEvent event) {
        updatePlayState(false);
    }

    private boolean isNowAudio() {
        AudioBean nowAudio = AudioController.getInstance().getNowAudio();
        if (nowAudio != null && TextUtils.equals(nowAudio.getId(), mId) &&
                (AudioController.getInstance().isPrepare() || AudioController.getInstance().isStart())) {
            return true;
        }
        return false;
    }

    private void updatePlayState(boolean isPlaying) {
        if (isPlaying) {
            mStateIv.setImageDrawable(playingIcon);
            mDescTv.setTextColor(descPlayingTextColor);
        } else {
            mStateIv.setImageDrawable(defaultIcon);
            mDescTv.setTextColor(descDefaultTextColor);
        }
    }

    //图标点击事件
    public void setOnIconClickListener(OnClickListener listener) {
        mStateFl.setOnClickListener(listener);
    }

    //文本点击事件
    public void setOnTextClickListener(OnClickListener listener) {
        mDescTv.setOnClickListener(listener);
    }

    public void setOnRootClickListener(OnClickListener listener) {
        mRootLl.setOnClickListener(listener);
    }

}
