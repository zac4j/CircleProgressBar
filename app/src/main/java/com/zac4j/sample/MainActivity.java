package com.zac4j.sample;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.zac4j.widget.DonutProgressBar;
import com.zac4j.widget.HuaProgressBar;

public class MainActivity extends AppCompatActivity {

  private DonutProgressBar mDonutProgressBar;
  private HuaProgressBar mHuaProgressBar;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mDonutProgressBar = (DonutProgressBar) findViewById(R.id.donut_progressbar);
    mHuaProgressBar = (HuaProgressBar) findViewById(R.id.hua_progressbar);
  }

  @Override protected void onResume() {
    super.onResume();
    setProgress();
  }

  private void setProgress() {
    ValueAnimator animator = ValueAnimator.ofInt(0, 100);
    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override public void onAnimationUpdate(ValueAnimator valueAnimator) {
        int progress = (int) valueAnimator.getAnimatedValue();

        mDonutProgressBar.setProgress(progress);
        mHuaProgressBar.setProgress(progress);
      }
    });

    animator.setRepeatCount(2);
    animator.setDuration(8000);
    animator.start();
  }
}
