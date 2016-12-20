package com.zac4j.library;

import android.content.Context;
import android.util.TypedValue;

/**
 * Utilities
 * Created by zac on 12/20/2016.
 */

class Utils {

  static int dp2Pixel(Context context, float dp) {
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
        context.getResources().getDisplayMetrics());
  }

  static int spToPixel(Context context, float sp) {
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
        context.getResources().getDisplayMetrics());
  }
}
