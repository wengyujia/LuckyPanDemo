package com.jia.luckypan;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends Activity {

    private LuckyPan mLuckyPan;
    private ImageView mStartBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLuckyPan= (LuckyPan) findViewById(R.id.id_luckyPan);
        mStartBtn=(ImageView)findViewById(R.id.id_start_btn);
        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mLuckyPan.isStart()){
                    mLuckyPan.PanStart();    //如果想自己控制转盘停留区域可以输入数值0~5
                    mStartBtn.setImageResource(R.drawable.stop);
                }else {
                    if (!mLuckyPan.isShowEnd()){
                        mLuckyPan.PanStop();
                        mStartBtn.setImageResource(R.drawable.start);
                    }
                }
            }
        });
    }
}
