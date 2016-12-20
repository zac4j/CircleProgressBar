package com.zac4j.library;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.ProgressBar;

/**
 * Circle ProgressBar
 * Created by zac on 12/19/2016.
 */

public class CircleProgressBar extends ProgressBar {

  private static final float DEFAULT_START_DEGREE = -90.0f;

  private float mRadius;
  private float mCenterX;
  private float mCenterY;

  private Paint mPaintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
  private Paint mPaintStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
  private Paint mPaintStrokeBg = new Paint(Paint.ANTI_ALIAS_FLAG);
  private Paint mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);

  private RectF mProgressRect = new RectF();

  public CircleProgressBar(Context context) {
    this(context, null);
  }

  public CircleProgressBar(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public CircleProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    if (isInEditMode()) return;

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

    // Retrieve styles attributes
    TypedArray a =
        context.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar, defStyleAttr, 0);

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

    mPaintText.setTextAlign(Paint.Align.CENTER);
    mPaintText.setTextSize(a.getDimensionPixelSize(R.styleable.CircleProgressBar_android_textSize,
        Utils.spToPixel(context, 14)));

    a.recycle();
  }

  public Paint getProgressText() {
    return mPaintText;
  }

  public void setProgressText(CharSequence progressText) {

    requestLayout();
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

  @Override protected synchronized void onDraw(Canvas canvas) {
    drawCircle(canvas);
    drawProgressText(canvas);
    drawStroke(canvas);
  }

  private void drawCircle(Canvas canvas) {
    canvas.drawCircle(mCenterX, mCenterY, mRadius, mPaintFill);
  }

  private void drawProgressText(Canvas canvas) {
  }

  private void drawStroke(Canvas canvas) {
    canvas.drawArc(mProgressRect, DEFAULT_START_DEGREE, 360.0f, false, mPaintStrokeBg);
    canvas.drawArc(mProgressRect, DEFAULT_START_DEGREE, 360.0f * getProgress() / getMax(), false,
        mPaintStroke);
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

    mProgressRect.inset(mPaintStroke.getStrokeWidth() / 2, mPaintStroke.getStrokeWidth() / 2);
  }
}
