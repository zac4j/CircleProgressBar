package com.zac4j.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

/**
 * Progress Bar in wave style.
 * Created by zac on 12/28/2016.
 */

public class WaveProgressBar extends ProgressBar {

  private BitmapShader mWaveShader;

  private Matrix mShaderMatrix;

  private Paint mWavePaint;
  private Paint mTextPaint;

  private int mWaveForeground;
  private int mWaveBackground;

  private float mWaveAmplitude;
  private float mWavelength;
  private float mWaveShift;
  private float mWaterline;

  private boolean mIsShowText;
  private int mTextColor;
  private float mTextSize;

  public WaveProgressBar(Context context) {
    this(context, null);
  }

  public WaveProgressBar(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public WaveProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    // Load defaults from resources
    final Resources res = getResources();
    final TypedValue val = new TypedValue();

    res.getValue(R.dimen.default_wave_progressbar_amplitude, val, true);
    final float defaultAmplitude = val.getFloat();
    res.getValue(R.dimen.default_wave_progressbar_wavelength, val, true);
    final float defaultWavelength = val.getFloat();
    res.getValue(R.dimen.default_wave_progressbar_waterline, val, true);
    final float defaultWaterline = val.getFloat();
    res.getValue(R.dimen.default_wave_progressbar_shift, val, true);
    final float defaultShift = val.getFloat();

    final int defaultWaveForeground =
        ContextCompat.getColor(context, R.color.default_wave_progressbar_foreground);
    final int defaultWaveBackground =
        ContextCompat.getColor(context, R.color.default_wave_progressbar_background);
    final int defaultTextColor =
        ContextCompat.getColor(context, R.color.default_wave_progressbar_text_color);

    TypedArray a =
        context.obtainStyledAttributes(attrs, R.styleable.WaveProgressBar, defStyleAttr, 0);

    mWaveBackground =
        a.getColor(R.styleable.WaveProgressBar_android_background, defaultWaveBackground);
    mWaveForeground =
        a.getColor(R.styleable.WaveProgressBar_android_foreground, defaultWaveForeground);
    mWaveAmplitude = a.getFloat(R.styleable.WaveProgressBar_amplitude, defaultAmplitude);
    mWavelength = a.getFloat(R.styleable.WaveProgressBar_wavelength, defaultWavelength);
    mWaterline = a.getFloat(R.styleable.WaveProgressBar_waterline, defaultWaterline);
    mWaveShift = a.getFloat(R.styleable.WaveProgressBar_shift, defaultShift);

    mIsShowText = a.getBoolean(R.styleable.WaveProgressBar_showText, true);
    if (mIsShowText) {
      mTextColor = a.getColor(R.styleable.WaveProgressBar_android_textColor, defaultTextColor);
      mTextSize = a.getDimensionPixelSize(R.styleable.DonutProgressBar_android_textSize,
          Utils.sp2Pixel(context, 14));
    }

    a.recycle();
  }

  private void createShader() {
    double angularFrequency = 2.0f * Math.PI / mWavelength / getWidth(); // angular frequency
    float amplitude = getHeight() * mWaveAmplitude;
    float waterline = getHeight() * mWaterline;
    float wavelength = getWidth();

    Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);

    Paint wavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    wavePaint.setStrokeWidth(2);

    final int endX = getWidth() + 1;
    final int endY = getHeight() + 1;

    float[] waveY = new float[endX];

    wavePaint.setColor(mWaveBackground);
    // y=Asin(ωx+φ)+h
    for (int beginX = 0; beginX < endX; beginX++) {
      double ωx = beginX * angularFrequency;
      float beginY = (float) (waterline + amplitude * Math.sin(ωx));
      canvas.drawLine(beginX, beginY, beginX, endY, wavePaint);

      waveY[beginX] = beginY;
    }

    wavePaint.setColor(mWaveForeground);
    final int foreWaveShift = (int) (wavelength / 4);
    for (int beginX = 0; beginX < endX; beginX++) {
      canvas.drawLine(beginX, waveY[(beginX + foreWaveShift) % endX], beginX, endY, wavePaint);
    }

    // Repeat in the horizontal orientation, clamp in the vertical orientation.
    mWaveShader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
    mWavePaint.setShader(mWaveShader);
  }

  @Override protected synchronized void onDraw(Canvas canvas) {

  }
}
