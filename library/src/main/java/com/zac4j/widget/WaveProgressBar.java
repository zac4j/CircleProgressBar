package com.zac4j.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
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

  private Paint mBackWavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private Paint mForeWavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private Paint mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

  private float mWaveAmplitude;
  private float mWavelength;
  private float mWaveShift;
  private float mWaterline;

  private float mCenterX;
  private float mCenterY;
  private float mRadius;

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

    mBackWavePaint.setStrokeWidth(2);
    mBackWavePaint.setStyle(Paint.Style.FILL);
    mBackWavePaint.setColor(
        a.getColor(R.styleable.WaveProgressBar_android_background, defaultWaveBackground));

    mForeWavePaint.setStrokeWidth(2);
    mForeWavePaint.setStyle(Paint.Style.FILL);
    mForeWavePaint.setColor(
        a.getColor(R.styleable.WaveProgressBar_android_foreground, defaultWaveForeground));

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

  @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);

    mCenterX = w / 2;
    mCenterY = h / 2;

    mRadius = Math.min(mCenterX, mCenterY);
  }

  @Override protected synchronized void onDraw(Canvas canvas) {
    drawWave();
    drawBackground(canvas);
  }

  private void drawBackground(Canvas canvas) {
    canvas.drawCircle(mCenterX, mCenterY, mRadius, mBackgroundPaint);
  }

  /**
   * y=Asin(ωx+φ)+h
   */
  private void drawWave() {

    Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);

    double ω = 2.0f * Math.PI / mWavelength / getWidth();
    float A = mWaveAmplitude * getHeight();
    float h = (1 - mWaterline) * getHeight();
    float foreOffset = getWidth() / 4.0f;

    int endX = getWidth() + 1;
    int endY = getHeight() + 1;

    float[] startYs = new float[endX];

    for (int startX = 0; startX < endX; startX++) {
      float startY = (float) (A * Math.sin(ω * startX) + h);
      canvas.drawLine(startX, startY, startX, endY, mBackWavePaint);
      startYs[startX] = startY;
    }

    for (int startX = 0; startX < endX; startX++) {
      float startY = startYs[(int) ((startX + foreOffset) % endX)];
      canvas.drawLine(startX, startY, startX, endY, mForeWavePaint);
    }

    BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
    mBackgroundPaint.setShader(shader);
  }
}
