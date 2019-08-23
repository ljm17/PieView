package com.ljm.pieviewdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ljm.pieviewdemo.pieview.PieEntry;
import com.ljm.pieviewdemo.pieview.PieView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SeekBar mAlphaRadiusSb;
    private SeekBar mHoleRadiusSb;
    private SeekBar mAlphaSb;
    private TextView mPercentage;
    private TextView mBlockUpTv;
    private TextView mDefaultTv;
    private TextView mCenterTextTv;
    private PieView mPieView;

    private boolean disPlayPercentage  = true;
    private boolean disPlayCenterText  = false;

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
                .setShowAnimator(true)
                .refresh();
    }

    private void initWidget() {
        mAlphaRadiusSb = findViewById(R.id.sb_alpha_circle_radius);
        mAlphaSb = findViewById(R.id.sb_alpha_circle_alpha);
        mHoleRadiusSb = findViewById(R.id.sb_hole_circle_radius);
        mPercentage = findViewById(R.id.tv_show_percentage);
        mBlockUpTv = findViewById(R.id.tv_block_up);
        mDefaultTv = findViewById(R.id.tv_default_data);
        mCenterTextTv = findViewById(R.id.tv_center_text);
        mPieView = findViewById(R.id.pie_view);

        mAlphaRadiusSb.setProgress(50);
        mHoleRadiusSb.setProgress(60);
        mAlphaSb.setProgress(40);
    }

    private void initListener() {
        mPercentage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disPlayPercentage = !disPlayPercentage;
                mPieView.setDisPlayPercent(disPlayPercentage).refresh();
            }
        });

        mBlockUpTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<PieEntry> list = new ArrayList<>();
                for (int i = 1; i < 7; i ++) {
                    list.add(new PieEntry(i * 20, String.format("第%s区", i), i == 4));
                }
                mPieView.setData(list)
                        .setShowAnimator(true)
                        .refresh();
            }
        });

        mDefaultTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<PieEntry> list = new ArrayList<>();
                for (int i = 1; i < 7; i ++) {
                    list.add(new PieEntry(i * 20, String.format("第%s区", i)));
                }
                mPieView.setData(list)
                        .setShowAnimator(true)
                        .refresh();
            }
        });

        mCenterTextTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disPlayCenterText = !disPlayCenterText;
                mPieView.setShowCenterText(disPlayCenterText).refresh();
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
