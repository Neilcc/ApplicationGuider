package com.zcc.hyguideline;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.zcc.guideline.lib.GuideGenerator;
import com.zcc.guideline.lib.GuideView;

public class MainActivity extends AppCompatActivity {
    private View guideTarget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        guideTarget = findViewById(R.id.guide_target);
        GuideGenerator.init(this)
                .setHollowImageRes(R.drawable.u_biz_guide_home_page_hollow)
                .setTipViewRes(R.drawable.u_biz_guide_home_page_tip, GuideView.Position.TOP, 15, 0, 0, 0)
                .setTargetView(guideTarget)
                .show();

    }
}
