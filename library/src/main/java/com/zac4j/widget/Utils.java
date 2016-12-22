package com.zac4j.widget;

import android.content.Context;
import android.util.TypedValue;

/**
 * Utilities
 * Created by zac on 12/20/2016.
 */

public class Utils {

  public static int dp2Pixel(Context context, float dp) {
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
        context.getResources().getDisplayMetrics());
  }

  public static int sp2Pixel(Context context, float sp) {
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
        context.getResources().getDisplayMetrics());
  }
}
