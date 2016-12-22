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
import android.support.annotation.Size;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;

/**
 * Circle ProgressBar
 * Created by zac on 12/19/2016.
 */

public class CircleProgressBar extends ProgressBar {

  private static final int SMALL = 1;
  private static final int MEDIUM = 2;
  private static final int LARGER = 3;

  private static final float DEFAULT_START_DEGREE = -90.0f;
  private static final String PROGRESS_TEXT_PATTERN = "%d%%";

  private float mRadius;
  private float mCenterX;
  private float mCenterY;

  private Paint mPaintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
  private Paint mPaintStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
  private Paint mPaintStrokeBg = new Paint(Paint.ANTI_ALIAS_FLAG);
  private Paint mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);

  private RectF mProgressRect = new RectF();
  private Rect mProgressTextRect = new Rect();

  private int mSize;
  private final float mProgressBarSizeSmall;
  private final float mProgressBarSizeMedium;
  private final float mProgressBarSizeLarger;
  private boolean mIsShowText;

  @Retention(RetentionPolicy.SOURCE) @IntDef({ SMALL, MEDIUM, LARGER }) private @interface Size {
  }

  public CircleProgressBar(Context context) {
    this(context, null);
  }

  public CircleProgressBar(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public CircleProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    // Load defaults from resources
    final Resources res = getResources();
    final float defaultStrokeWidth =
        res.getDimension(R.dimen.default_circle_progressbar_stroke_width);
    final int defaultFillColor =
        ContextCompat.getColor(context, R.color.default_circle_progressbar_fill_color);
    final int defaultStrokeColor =
        ContextCompat.getColor(context, R.color.default_circle_progressbar_stroke_color);
    final int defaultStrokeBgColor =
        ContextCompat.getColor(context, R.color.default_circle_progressbar_stroke_bg_color);
    final int defaultTextColor =
        ContextCompat.getColor(context, R.color.default_circle_progressbar_text_color);
    mProgressBarSizeSmall = res.getDimension(R.dimen.progress_bar_size_small);
    mProgressBarSizeMedium = res.getDimension(R.dimen.progress_bar_size_medium);
    mProgressBarSizeLarger = res.getDimension(R.dimen.progress_bar_size_large);

    // Retrieve styles attributes
    TypedArray a =
        context.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar, defStyleAttr, 0);

    mSize = a.getInt(R.styleable.CircleProgressBar_size, MEDIUM);

    mPaintFill.setStyle(Paint.Style.FILL);
    mPaintFill.setColor(a.getColor(R.styleable.CircleProgressBar_fillColor, defaultFillColor));

    mPaintStroke.setStyle(Paint.Style.STROKE);
    mPaintStroke.setStrokeWidth(
        a.getDimension(R.styleable.CircleProgressBar_strokeWidth, defaultStrokeWidth));
    mPaintStroke.setColor(
        a.getColor(R.styleable.CircleProgressBar_strokeColor, defaultStrokeColor));

    mPaintStrokeBg.setStyle(Paint.Style.STROKE);
    mPaintStrokeBg.setStrokeWidth(
        a.getDimension(R.styleable.CircleProgressBar_strokeWidth, defaultStrokeWidth));
    mPaintStrokeBg.setColor(
        a.getColor(R.styleable.CircleProgressBar_strokeBgColor, defaultStrokeBgColor));

    mIsShowText = a.getBoolean(R.styleable.CircleProgressBar_isShowText, true);
    if (mIsShowText) {
      mPaintText.setTextAlign(Paint.Align.CENTER);
      mPaintText.setTextSize(a.getDimensionPixelSize(R.styleable.CircleProgressBar_android_textSize,
          Utils.sp2Pixel(context, 14)));
      mPaintText.setColor(
          a.getColor(R.styleable.CircleProgressBar_android_textColor, defaultTextColor));
    }

    a.recycle();
  }

  public int getFillColor() {
    return mPaintFill.getColor();
  }

  public void setFillColor(int fillColor) {
    mPaintFill.setColor(fillColor);
    invalidate();
  }

  public int getStrokeColor() {
    return mPaintStroke.getColor();
  }

  public void setStrokeColor(int strokeColor) {
    mPaintStroke.setColor(strokeColor);
    invalidate();
  }

  public int getStrokeBackgroundColor() {
    return mPaintStrokeBg.getColor();
  }

  public void setStrokeBackgroundColor(int strokeBgColor) {
    mPaintStrokeBg.setColor(strokeBgColor);
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
    mPaintText.setTextSize(size);
    requestLayout();
  }

  public void setTextColor(@ColorInt int color) {
    mPaintText.setColor(color);
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
    canvas.drawCircle(mCenterX, mCenterY, mRadius, mPaintFill);
  }

  private void drawProgressText(Canvas canvas) {
    if (!mIsShowText) {
      return;
    }
    String progressText = String.format(Locale.getDefault(), PROGRESS_TEXT_PATTERN, getProgress());

    mPaintText.getTextBounds(progressText, 0, progressText.length(), mProgressTextRect);
    canvas.drawText(progressText, mCenterX, (mCenterY + mProgressRect.height()) / 2, mPaintText);
  }

  private void drawStroke(Canvas canvas) {
    canvas.drawArc(mProgressRect, DEFAULT_START_DEGREE, 360.0f, false, mPaintStrokeBg);
    canvas.drawArc(mProgressRect, DEFAULT_START_DEGREE, 360.0f * getProgress() / getMax(), false,
        mPaintStroke);
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

    mProgressRect.inset(mPaintStroke.getStrokeWidth() / 2, mPaintStroke.getStrokeWidth() / 2);
  }
}
