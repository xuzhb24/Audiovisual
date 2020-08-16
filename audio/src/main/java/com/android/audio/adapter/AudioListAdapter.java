package com.android.audio.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.audio.R;
import com.android.audio.core.AudioController;
import com.android.audio.entity.AudioBean;
import com.android.audio.view.PlayStateView;

import java.util.List;

/**
 * Created by xuzhb on 2020/7/11
 */
public class AudioListAdapter extends RecyclerView.Adapter {

    private List<AudioBean> mList;

    public AudioListAdapter(List<AudioBean> list) {
        this.mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_audio_list, null));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AudioBean bean = mList.get(position);
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.psv.bind(bean.getId());
        viewHolder.psv.setDescText(bean.getTitle());
        viewHolder.psv.setOnRootClickListener(v -> AudioController.getInstance().addAudio(bean));
        viewHolder.iv.setOnClickListener(v -> AudioController.getInstance().removeAudio(bean));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        private PlayStateView psv;
        private ImageView iv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            psv = itemView.findViewById(R.id.psv);
            iv = itemView.findViewById(R.id.iv);
        }
    }

}
