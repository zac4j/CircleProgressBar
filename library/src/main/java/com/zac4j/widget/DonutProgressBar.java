package com.zac4j.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;

/**
 * Progress Bar in donut style.
 * Created by zac on 12/19/2016.
 */

public class DonutProgressBar extends ProgressBar {

  private static final int SMALL = 1;
  private static final int MEDIUM = 2;
  private static final int LARGER = 3;

  private static final float DEFAULT_START_DEGREE = -90.0f;
  private static final String PROGRESS_TEXT_PATTERN = "%d%%";

  private float mRadius;
  private float mCenterX;
  private float mCenterY;

  private Paint mFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private Paint mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private Paint mStrokeBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

  private RectF mProgressRect = new RectF();
  private Rect mProgressTextRect = new Rect();

  private int mSize;
  private final float mProgressBarSizeSmall;
  private final float mProgressBarSizeMedium;
  private final float mProgressBarSizeLarger;
  private boolean mIsShowText;

  @Retention(RetentionPolicy.SOURCE) @IntDef({ SMALL, MEDIUM, LARGER }) private @interface Size {
  }

  public DonutProgressBar(Context context) {
    this(context, null);
  }

  public DonutProgressBar(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public DonutProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    // Load defaults from resources
    final Resources res = getResources();
    final float defaultStrokeWidth =
        res.getDimension(R.dimen.default_donut_progressbar_stroke_width);
    final int defaultFillColor =
        ContextCompat.getColor(context, R.color.default_donut_progressbar_fill_color);
    final int defaultStrokeColor =
        ContextCompat.getColor(context, R.color.default_donut_progressbar_stroke_color);
    final int defaultStrokeBgColor =
        ContextCompat.getColor(context, R.color.default_donut_progressbar_stroke_bg_color);
    final int defaultTextColor =
        ContextCompat.getColor(context, R.color.default_donut_progressbar_text_color);
    mProgressBarSizeSmall = res.getDimension(R.dimen.progress_bar_size_small);
    mProgressBarSizeMedium = res.getDimension(R.dimen.progress_bar_size_medium);
    mProgressBarSizeLarger = res.getDimension(R.dimen.progress_bar_size_large);

    // Retrieve styles attributes
    TypedArray a =
        context.obtainStyledAttributes(attrs, R.styleable.DonutProgressBar, defStyleAttr, 0);

    mSize = a.getInt(R.styleable.DonutProgressBar_size, MEDIUM);

    mFillPaint.setStyle(Paint.Style.FILL);
    mFillPaint.setColor(a.getColor(R.styleable.DonutProgressBar_fillColor, defaultFillColor));

    mStrokePaint.setStyle(Paint.Style.STROKE);
    mStrokePaint.setStrokeWidth(
        a.getDimension(R.styleable.DonutProgressBar_strokeWidth, defaultStrokeWidth));
    mStrokePaint.setColor(a.getColor(R.styleable.DonutProgressBar_strokeColor, defaultStrokeColor));

    mStrokeBgPaint.setStyle(Paint.Style.STROKE);
    mStrokeBgPaint.setStrokeWidth(
        a.getDimension(R.styleable.DonutProgressBar_strokeWidth, defaultStrokeWidth));
    mStrokeBgPaint.setColor(
        a.getColor(R.styleable.DonutProgressBar_strokeBgColor, defaultStrokeBgColor));

    mIsShowText = a.getBoolean(R.styleable.DonutProgressBar_isShowText, true);
    if (mIsShowText) {
      mTextPaint.setTextAlign(Paint.Align.CENTER);
      mTextPaint.setTextSize(a.getDimensionPixelSize(R.styleable.DonutProgressBar_android_textSize,
          Utils.sp2Pixel(context, 14)));
      mTextPaint.setColor(
          a.getColor(R.styleable.DonutProgressBar_android_textColor, defaultTextColor));
    }

    a.recycle();
  }

  public int getFillColor() {
    return mFillPaint.getColor();
  }

  public void setFillColor(int fillColor) {
    mFillPaint.setColor(fillColor);
    invalidate();
  }

  public int getStrokeColor() {
    return mStrokePaint.getColor();
  }

  public void setStrokeColor(int strokeColor) {
    mStrokePaint.setColor(strokeColor);
    invalidate();
  }

  public int getStrokeBackgroundColor() {
    return mStrokeBgPaint.getColor();
  }

  public void setStrokeBackgroundColor(int strokeBgColor) {
    mStrokeBgPaint.setColor(strokeBgColor);
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
  }

  public int getSize() {
    return mSize;
  }

  public void setSize(@Size int size) {
    mSize = size;
    requestLayout();
  }

  @Override protected synchronized void onDraw(Canvas canvas) {
    drawCircle(canvas);
    drawStroke(canvas);
    drawProgressText(canvas);
  }

  private void drawCircle(Canvas canvas) {
    canvas.drawCircle(mCenterX, mCenterY, mRadius, mFillPaint);
  }

  private void drawProgressText(Canvas canvas) {
    if (!mIsShowText) {
      return;
    }
    String progressText = String.format(Locale.getDefault(), PROGRESS_TEXT_PATTERN, getProgress());

    mTextPaint.getTextBounds(progressText, 0, progressText.length(), mProgressTextRect);
    canvas.drawText(progressText, mCenterX, (mCenterY + mProgressRect.height()) / 2, mTextPaint);
  }

  private void drawStroke(Canvas canvas) {
    canvas.drawArc(mProgressRect, DEFAULT_START_DEGREE, 360.0f, false, mStrokeBgPaint);
    canvas.drawArc(mProgressRect, DEFAULT_START_DEGREE, 360.0f * getProgress() / getMax(), false,
        mStrokePaint);
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

  @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    initProgressRect(w, h);
  }

  private void initProgressRect(int w, int h) {
    mCenterX = w / 2;
    mCenterY = h / 2;

    mRadius = Math.min(mCenterX, mCenterY);
    mProgressRect.top = mCenterY - mRadius;
    mProgressRect.bottom = mCenterY + mRadius;
    mProgressRect.left = mCenterX - mRadius;
    mProgressRect.right = mCenterX + mRadius;

    mProgressRect.inset(mStrokePaint.getStrokeWidth() / 2, mStrokePaint.getStrokeWidth() / 2);
  }
}
