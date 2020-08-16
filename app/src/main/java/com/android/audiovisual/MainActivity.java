package com.android.audiovisual;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.android.audio.core.AudioController;
import com.android.audio.entity.AudioBean;
import com.android.audiovisual.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        AudioController.getInstance().setQueue(AACreateData.createAudioList());
        AudioBean bean1 = AACreateData.createAudioList().get(0);
        mBinding.psv1.bind(bean1.getId());
        mBinding.psv1.setDescText(bean1.getTitle());
        mBinding.psv1.setOnRootClickListener(v -> {
            AudioController.getInstance().addAudio(bean1);
        });
        AudioBean bean2 = AACreateData.createAudioList().get(1);
        mBinding.psv2.bind(bean2.getId());
        mBinding.psv2.setDescText(bean2.getTitle());
        mBinding.psv2.setOnRootClickListener(v -> {
            AudioController.getInstance().addAudio(bean2);
        });
    }

}
