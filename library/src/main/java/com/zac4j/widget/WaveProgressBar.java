package com.zac4j.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;
import java.util.Locale;

/**
 * Progress Bar in wave style.
 * Created by zac on 12/28/2016.
 */

public class WaveProgressBar extends ProgressBar {

  private static final String PROGRESS_TEXT_PATTERN = "%d%%";

  private Paint mBackWavePaint;
  private Paint mForeWavePaint;
  private Paint mBackgroundPaint;
  private Paint mTextPaint;

  private RectF mProgressRect = new RectF();
  private Rect mProgressTextRect = new Rect();

  private float mWaveAmplitude;
  private float mWavelength;
  private float mWaveShift;
  private float mWaterline;

  private BitmapShader mBitmapShader;
  private Matrix mShaderMatrix;

  private float mCenterX;
  private float mCenterY;
  private float mRadius;

  private int mMaxProgress;
  private long mWaveDuration;

  private boolean mIsShowText;
  private int mTextColor;
  private float mTextSize;
  private AnimatorSet mAnimatorSet;

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

    mBackWavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mBackWavePaint.setStrokeWidth(2);
    mBackWavePaint.setStyle(Paint.Style.FILL);
    mBackWavePaint.setColor(
        a.getColor(R.styleable.WaveProgressBar_backWaveColor, defaultWaveBackground));

    mForeWavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mForeWavePaint.setStrokeWidth(2);
    mForeWavePaint.setStyle(Paint.Style.FILL);
    mForeWavePaint.setColor(
        a.getColor(R.styleable.WaveProgressBar_foreWaveColor, defaultWaveForeground));

    mWaveAmplitude = a.getFloat(R.styleable.WaveProgressBar_amplitude, defaultAmplitude);
    mWavelength = a.getFloat(R.styleable.WaveProgressBar_wavelength, defaultWavelength);
    mWaterline = a.getFloat(R.styleable.WaveProgressBar_waterline, defaultWaterline);
    mWaveShift = a.getFloat(R.styleable.WaveProgressBar_shift, defaultShift);

    mIsShowText = a.getBoolean(R.styleable.WaveProgressBar_showText, true);
    if (mIsShowText) {
      mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
      mTextPaint.setTextAlign(Paint.Align.CENTER);
      mTextPaint.setTextSize(a.getDimensionPixelSize(R.styleable.DonutProgressBar_android_textSize,
          Utils.sp2Pixel(context, 14)));
      mTextPaint.setColor(
          a.getColor(R.styleable.WaveProgressBar_android_textColor, defaultTextColor));
    }

    mShaderMatrix = new Matrix();
    mAnimatorSet = new AnimatorSet();

    a.recycle();
  }

  public float getWaveShift() {
    return mWaveShift;
  }

  public void setWaveShift(float waveShift) {
    mWaveShift = waveShift;
    invalidate();
  }

  public float getWaterline() {
    return mWaterline;
  }

  public void setWaterline(float waterline) {
    mWaterline = waterline;
    invalidate();
  }

  public float getWaveAmplitude() {
    return mWaveAmplitude;
  }

  public void setWaveAmplitude(float waveAmplitude) {
    mWaveAmplitude = waveAmplitude;
    invalidate();
  }

  public void setForeWaveColor(int color) {
    mForeWavePaint.setColor(color);
    invalidate();
  }

  public void setBackWavePaint(int color) {
    mBackWavePaint.setColor(color);
    invalidate();
  }

  public boolean isShowText() {
    return mIsShowText;
  }

  public void setShowText(boolean showText) {
    mIsShowText = showText;
    invalidate();
  }

  public void setTextSize(float size) {
    mTextPaint.setTextSize(size);
    requestLayout();
  }

  public void setTextColor(@ColorInt int color) {
    mTextPaint.setColor(color);
    invalidate();
  }

  @Override public synchronized void setMax(int max) {
    mMaxProgress = max;
    super.setMax(max);
  }

  public synchronized void setWaveDuration(int duration) {
    mWaveDuration = duration;
  }

  @Override protected void onAttachedToWindow() {
    startAnimators();
    super.onAttachedToWindow();
  }

  @Override protected void onDetachedFromWindow() {
    stopAnimators();
    super.onDetachedFromWindow();
  }

  private void startAnimators() {
    ObjectAnimator shiftAnimator = ObjectAnimator.ofFloat(this, "waveShift", 0f, 1f);
    shiftAnimator.setRepeatCount(ValueAnimator.INFINITE);
    shiftAnimator.setDuration(800);
    shiftAnimator.setInterpolator(new LinearInterpolator());

    float endWaterline = 0.5f * mMaxProgress / 100;

    ObjectAnimator waterlineAnimator = ObjectAnimator.ofFloat(this, "waterline", 0f, endWaterline);
    waterlineAnimator.setDuration(mWaveDuration);
    waterlineAnimator.setInterpolator(new DecelerateInterpolator());
    waterlineAnimator.setRepeatCount(ValueAnimator.INFINITE);

    ObjectAnimator amplitudeAnimator = ObjectAnimator.ofFloat(this, "waveAmplitude", 0f, 0.05f);
    amplitudeAnimator.setRepeatCount(ValueAnimator.INFINITE);
    amplitudeAnimator.setRepeatMode(ValueAnimator.REVERSE);
    amplitudeAnimator.setDuration(mWaveDuration / 2);
    amplitudeAnimator.setInterpolator(new LinearInterpolator());

    mAnimatorSet.playTogether(shiftAnimator, waterlineAnimator, amplitudeAnimator);
    mAnimatorSet.start();
  }

  private void stopAnimators() {
    mAnimatorSet.cancel();
  }

  @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    mCenterX = w / 2;
    mCenterY = h / 2;

    mRadius = Math.min(mCenterX, mCenterY);
    mProgressRect.top = mCenterY - mRadius;
    mProgressRect.bottom = mCenterY + mRadius;
    mProgressRect.left = mCenterX - mRadius;
    mProgressRect.right = mCenterX + mRadius;
  }

  @Override protected synchronized void onDraw(Canvas canvas) {
    createWaveShader();
    drawBackground(canvas);
    drawProgressText(canvas);
  }

  private void drawProgressText(Canvas canvas) {
    if (!mIsShowText) {
      return;
    }
    String progressText = String.format(Locale.getDefault(), PROGRESS_TEXT_PATTERN, getProgress());

    mTextPaint.getTextBounds(progressText, 0, progressText.length(), mProgressTextRect);
    canvas.drawText(progressText, mCenterX, (mCenterY + mProgressRect.height()) / 2, mTextPaint);
  }

  /**
   * y=Asin(ωx+φ)+k
   */
  private void createWaveShader() {

    Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);

    double ω = 2.0f * Math.PI / mWavelength / getWidth();
    float A = mWaveAmplitude * getHeight();
    float k = (1 - mWaterline) * getHeight();
    float foreOffset = getWidth() / 4.0f;

    int endX = getWidth() + 1;
    int endY = getHeight() + 1;

    float[] startYs = new float[endX];

    for (int startX = 0; startX < endX; startX++) {
      float startY = (float) (A * Math.sin(ω * startX) + k);
      canvas.drawLine(startX, startY, startX, endY, mBackWavePaint);
      startYs[startX] = startY;
    }

    for (int startX = 0; startX < endX; startX++) {
      float startY = startYs[(int) ((startX + foreOffset) % endX)];
      canvas.drawLine(startX, startY, startX, endY, mForeWavePaint);
    }

    mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
    mBackgroundPaint.setShader(mBitmapShader);
  }

  private void drawBackground(Canvas canvas) {

    mShaderMatrix.setTranslate(mWaveShift * getWidth(), -mWaterline * getHeight());

    mBitmapShader.setLocalMatrix(mShaderMatrix);

    canvas.drawCircle(mCenterX, mCenterY, mRadius, mBackgroundPaint);
  }
}
