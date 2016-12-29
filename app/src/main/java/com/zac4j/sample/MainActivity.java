package com.zac4j.sample;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.zac4j.widget.DonutProgressBar;
import com.zac4j.widget.WaveProgressBar;

public class MainActivity extends AppCompatActivity {

  private DonutProgressBar mDonutProgressBar;
  private WaveProgressBar mWaveProgressBar;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mDonutProgressBar = (DonutProgressBar) findViewById(R.id.donut_progressbar);
    mWaveProgressBar = (WaveProgressBar) findViewById(R.id.wave_progressbar);
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
        mWaveProgressBar.setProgress(progress);
      }
    });
    animator.setRepeatCount(10);
    animator.setDuration(6000);
    animator.start();
  }
}
