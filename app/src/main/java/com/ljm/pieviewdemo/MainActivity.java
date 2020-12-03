package com.ljm.pieviewdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ljm.pieview.PieEntry;
import com.ljm.pieview.PieView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SeekBar mAlphaRadiusSb;
    private SeekBar mHoleRadiusSb;
    private SeekBar mAlphaSb;
    private TextView mPercentage;
    private TextView mBlockUpTv;
    private TextView mStartAnimTv;
    private TextView mCenterTextTv;
    private PieView mPieView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWidget();
        initListener();
        processLogic();
    }

    private void processLogic() {
        List<PieEntry> list = new ArrayList<>();
        for (int i = 1; i < 7; i ++) {
            list.add(new PieEntry(i * 20, String.format("第%s区", i)));
        }
        mPieView.setData(list)
                .refresh();
    }

    private void initWidget() {
        mAlphaRadiusSb = findViewById(R.id.sb_alpha_circle_radius);
        mAlphaSb = findViewById(R.id.sb_alpha_circle_alpha);
        mHoleRadiusSb = findViewById(R.id.sb_hole_circle_radius);
        mPercentage = findViewById(R.id.tv_show_percentage);
        mBlockUpTv = findViewById(R.id.tv_block_up);
        mCenterTextTv = findViewById(R.id.tv_center_text);
        mStartAnimTv = findViewById(R.id.tv_start_anim);
        mPieView = findViewById(R.id.pie_view);

        mAlphaRadiusSb.setProgress(50);
        mHoleRadiusSb.setProgress(60);
        mAlphaSb.setProgress(40);

        mPercentage.setSelected(true);
    }

    private void initListener() {
        mPercentage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(!v.isSelected());
                mPieView.setDisPlayPercent(v.isSelected()).refresh();
            }
        });

        mBlockUpTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(!v.isSelected());
                mPieView.getData().get(3).setBlockRaised(v.isSelected());
                mPieView.refresh();
            }
        });

        mStartAnimTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPieView.startAnimator();
            }
        });

        mCenterTextTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(!v.isSelected());
                mPieView.setShowCenterText(v.isSelected()).refresh();
            }
        });

        mAlphaSb.setOnSeekBarChangeListener(new SimpleSeekBarListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mPieView.setCenterAlpha((float) progress / 100).refresh();
            }
        });

        mAlphaRadiusSb.setOnSeekBarChangeListener(new SimpleSeekBarListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mPieView.setAlphaRadiusPercent((float) progress / 100).refresh();
            }
        });

        mHoleRadiusSb.setOnSeekBarChangeListener(new SimpleSeekBarListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mPieView.setHoleRadiusPercent((float) progress / 100).refresh();
            }
        });
    }


}
