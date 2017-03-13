package com.zac4j.widget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;

/**
 * 菊花(JuHua) ProgressBar
 * Created by zac on 1/10/2017.
 */

public class HuaProgressBar extends ProgressBar {

  private static final int SMALL = 1;
  private static final int MEDIUM = 2;
  private static final int LARGER = 3;

  private static final int DEFAULT_PETAL_COUNT = 12;
  private static final String PROGRESS_TEXT_PATTERN = "%d%%";

  private int mProgressStartColor;
  private int mProgressEndColor;

  private float mRadius;
  private float mCenterX;
  private float mCenterY;

  private Paint mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

  private RectF mProgressRect = new RectF();
  private Rect mProgressTextRect = new Rect();

  private ObjectAnimator mAnimator;
  private boolean mShowProgressText;
  private int mSize;
  private float mRotateDegree;

  private long mDuration;
  private Interpolator mInterpolator;
  private int mAnimatorRepeatCount;
  private int mAnimatorRepeatMode;

  private float mProgressBarSizeSmall;
  private float mProgressBarSizeMedium;
  private float mProgressBarSizeLarger;

  @Retention(RetentionPolicy.SOURCE) @IntDef({ SMALL, MEDIUM, LARGER }) private @interface Size {
  }

  public HuaProgressBar(Context context) {
    this(context, null);
  }

  public HuaProgressBar(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public HuaProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    initProgressBar();

    // Load defaults from resources
    final Resources res = getResources();
    final float defaultProgressWidthSmall =
        res.getDimension(R.dimen.default_hua_progressbar_line_width_small);
    final float defaultProgressWidthMedium =
        res.getDimension(R.dimen.default_hua_progressbar_line_width_medium);
    final float defaultProgressWidthLarge =
        res.getDimension(R.dimen.default_hua_progressbar_line_width_large);
    final int defaultProgressStartColor =
        ContextCompat.getColor(context, R.color.default_hua_progressbar_start_color);
    final int defaultProgressEndColor =
        ContextCompat.getColor(context, R.color.default_hua_progressbar_end_color);
    final int defaultTextColor =
        ContextCompat.getColor(context, R.color.default_hua_progressbar_text_color);
    mProgressBarSizeSmall = res.getDimension(R.dimen.progress_bar_size_small);
    mProgressBarSizeMedium = res.getDimension(R.dimen.progress_bar_size_medium);
    mProgressBarSizeLarger = res.getDimension(R.dimen.progress_bar_size_large);

    // Retrieve styles attributes
    final TypedArray a =
        context.obtainStyledAttributes(attrs, R.styleable.HuaProgressBar, defStyleAttr, 0);

    mSize = a.getInt(R.styleable.HuaProgressBar_huaSize, MEDIUM);

    float progressWidth = defaultProgressWidthMedium;

    if (mSize == SMALL) {
      progressWidth = defaultProgressWidthSmall;
    } else if (mSize == LARGER) {
      progressWidth = defaultProgressWidthLarge;
    }

    mProgressStartColor =
        a.getColor(R.styleable.HuaProgressBar_progressStartColor, defaultProgressStartColor);
    mProgressEndColor =
        a.getColor(R.styleable.HuaProgressBar_progressEndColor, defaultProgressEndColor);

    mProgressPaint.setStyle(Paint.Style.STROKE);
    mProgressPaint.setStrokeWidth(
        a.getDimension(R.styleable.HuaProgressBar_progressWidth, progressWidth));
    mProgressPaint.setColor(mProgressStartColor);
    mProgressPaint.setStrokeCap(Paint.Cap.ROUND);

    mShowProgressText = a.getBoolean(R.styleable.HuaProgressBar_showText, mSize == LARGER);
    if (mShowProgressText) {
      mTextPaint.setTextAlign(Paint.Align.CENTER);
      mTextPaint.setTextSize(a.getDimensionPixelSize(R.styleable.HuaProgressBar_android_textSize,
          Utils.sp2Pixel(context, 14)));
      mTextPaint.setColor(
          a.getColor(R.styleable.HuaProgressBar_android_textColor, defaultTextColor));
    }

    a.recycle();
  }

  /**
   * Initialize default animator value
   */
  private void initProgressBar() {
    mDuration = 1000L;
    mInterpolator = new LinearInterpolator();
    mAnimatorRepeatMode = ValueAnimator.RESTART;
    mAnimatorRepeatCount = ValueAnimator.INFINITE;
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

  private void createProgressShader() {
    Shader shader =
        new SweepGradient(mCenterX, mCenterY, new int[] { mProgressEndColor, mProgressStartColor },
            new float[] { 0.0f, 1.0f });
    Matrix matrix = new Matrix();
    matrix.postRotate(mRotateDegree, mCenterX, mCenterY);
    shader.setLocalMatrix(matrix);
    mProgressPaint.setShader(shader);
  }

  public int getProgressStartColor() {
    return mProgressStartColor;
  }

  public void setProgressStartColor(@ColorInt int progressStartColor) {
    mProgressStartColor = progressStartColor;
    postInvalidate();
  }

  public int getProgressEndColor() {
    return mProgressEndColor;
  }

  public void setProgressEndColor(@ColorInt int progressEndColor) {
    mProgressEndColor = progressEndColor;
    postInvalidate();
  }

  public float getRotateDegree() {
    return mRotateDegree;
  }

  public void setRotateDegree(float rotateDegree) {
    mRotateDegree = rotateDegree;
    postInvalidate();
  }

  public int getSize() {
    return mSize;
  }

  public void setSize(@Size int size) {
    mSize = size;
    requestLayout();
  }

  @Override protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    startAnimator();
  }

  @Override protected void onDetachedFromWindow() {
    stopAnimators();
    super.onDetachedFromWindow();
  }

  @Override public void setVisibility(int visibility) {
    super.setVisibility(visibility);
    if (getVisibility() == visibility) {
      return;
    }

    if (visibility == GONE || visibility == INVISIBLE) {
      stopAnimators();
    } else {
      startAnimator();
    }
  }

  @Override protected void onVisibilityChanged(View changedView, int visibility) {
    super.onVisibilityChanged(changedView, visibility);

    if (visibility == GONE || visibility == INVISIBLE) {
      stopAnimators();
    } else {
      startAnimator();
    }
  }

  private void startAnimator() {
    if (isInEditMode()) return;

    if (getVisibility() != VISIBLE) {
      return;
    }

    if (mInterpolator == null) {
      mInterpolator = new LinearInterpolator();
    }

    if (mAnimator == null) {
      mAnimator = ObjectAnimator.ofFloat(this, "rotateDegree", 0.0f, 360.0f);
      mAnimator.setRepeatMode(mAnimatorRepeatMode);
      mAnimator.setRepeatCount(mAnimatorRepeatCount);
      mAnimator.setDuration(mDuration);
      mAnimator.setInterpolator(mInterpolator);
      mAnimator.start();
    }

    postInvalidate();
  }

  private void stopAnimators() {
    if (mAnimator != null) {
      mAnimator.cancel();
      mAnimator = null;
    }
    postInvalidate();
  }

  @Override protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int width, height;

    final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
    final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
    final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
    final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

    float progressBarWidth;
    float progressBarHeight;

    if (mSize == SMALL) {
      progressBarWidth = progressBarHeight = mProgressBarSizeSmall;
    } else if (mSize == LARGER) {
      progressBarWidth = progressBarHeight = mProgressBarSizeLarger;
    } else { // default size is medium
      progressBarWidth = progressBarHeight = mProgressBarSizeMedium;
    }

    switch (widthMode) {
      case MeasureSpec.EXACTLY:
        width = widthSize;
        break;
      case MeasureSpec.AT_MOST:
        width = Math.min((int) progressBarWidth, widthSize);
        break;
      case MeasureSpec.UNSPECIFIED:
      default:
        width = (int) progressBarWidth;
    }

    switch (heightMode) {
      case MeasureSpec.EXACTLY:
        height = heightSize;
        break;
      case MeasureSpec.AT_MOST:
        height = Math.min((int) progressBarHeight, heightSize);
        break;
      case MeasureSpec.UNSPECIFIED:
      default:
        height = (int) progressBarHeight;
    }

    setMeasuredDimension(width, height);
  }

  @Override protected synchronized void onDraw(Canvas canvas) {
    if (getProgress() == getMax()) {
      stopAnimators();
    }
    createProgressShader();
    drawProgress(canvas);
    drawProgressText(canvas);
  }

  private void drawProgress(Canvas canvas) {
    float unitDegrees = (float) (2.0f * Math.PI / DEFAULT_PETAL_COUNT);
    float outerCircleRadius = mRadius;
    float interCircleRadius = mRadius * 0.5f;

    for (int i = 0; i < DEFAULT_PETAL_COUNT; i++) {
      float rotateDegrees = i * unitDegrees;

      float startX = mCenterX + (float) Math.sin(rotateDegrees) * interCircleRadius;
      float startY = mCenterX - (float) Math.cos(rotateDegrees) * interCircleRadius;

      float stopX = mCenterX + (float) Math.sin(rotateDegrees) * outerCircleRadius;
      float stopY = mCenterX - (float) Math.cos(rotateDegrees) * outerCircleRadius;

      canvas.drawLine(startX, startY, stopX, stopY, mProgressPaint);
    }
  }

  private void drawProgressText(Canvas canvas) {
    if (mShowProgressText) {
      String progressText =
          String.format(Locale.getDefault(), PROGRESS_TEXT_PATTERN, getProgress());

      mTextPaint.getTextBounds(progressText, 0, progressText.length(), mProgressTextRect);
      canvas.drawText(progressText, mCenterX, mCenterY + mProgressTextRect.height() / 2,
          mTextPaint);
    }
  }
}
