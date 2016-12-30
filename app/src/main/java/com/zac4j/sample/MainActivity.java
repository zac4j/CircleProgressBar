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

    // required
    mWaveProgressBar.setMax(60);
    // required
    mWaveProgressBar.setWaveDuration(5000);

    ValueAnimator animator = ValueAnimator.ofInt(0, 60);
    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override public void onAnimationUpdate(ValueAnimator valueAnimator) {

        int progress = (int) valueAnimator.getAnimatedValue();

        mDonutProgressBar.setProgress(progress);
        mWaveProgressBar.setProgress(progress);
      }
    });

    animator.setRepeatCount(10);
    animator.setDuration(5000);
    animator.start();
  }
}
