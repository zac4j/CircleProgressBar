# CircleProgressBar
- Circle Progress Bar for Android applications.
  - DonutProgressBar inspired by MIUI.
  - HuaProgressBar inspired by UIActivityIndicatorView on iOS, also named 菊花(JuHua) in Chinese.
  - WaveProgressBar inspired by AliPay android app face recognition login UI.

## Screenshots
![donut][donut]

## Getting Started
**CircleProgressBar** (min API 16):
#### 1.This library is available on Maven Central, you can find it with Gradle:

```groovy
dependencies {
  compile 'com.zac4j.library:circleprogressbar:0.1.0'
}
```
#### 2.Add the ProgressBar widget in your xml file:
```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="16dp"
    tools:context="com.zac4j.sample.MainActivity"
    >

  <com.zac4j.widget.DonutProgressBar
      android:id="@id/donut_progressbar"
      android:layout_width="wrap_content"
      android:layout_height="wrap_content"
      android:layout_centerHorizontal="true"
      app:donutSize="medium"
      app:progressBgColor="@color/gray"
      app:progressColor="@color/purple"
      app:progressWidth="4dp"
      />

  <com.zac4j.widget.HuaProgressBar
      android:id="@id/hua_progressbar"
      android:layout_width="wrap_content"
      android:layout_height="wrap_content"
      android:layout_centerInParent="true"
      android:textColor="@color/deep_orange"
      android:textSize="12sp"
      app:huaSize="medium"
      app:progressEndColor="@color/gray_light"
      app:progressStartColor="@color/orange"
      app:showText="true"
      />

</RelativeLayout>
```
#### 3.On activity onResume() method set progress animator:
```
private void setProgress() {
  ValueAnimator animator = ValueAnimator.ofInt(0, 100);
  animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
    @Override public void onAnimationUpdate(ValueAnimator valueAnimator) {
      int progress = (int) valueAnimator.getAnimatedValue();

      mDonutProgressBar.setProgress(progress);
      mHuaProgressBar.setProgress(progress);
    }
  });

  animator.setRepeatCount(6);
  animator.setDuration(6000);
  animator.start();
}
```

#### 4.Options and Settings
**Common ProgressBar attributes:**
- `progressWidth :`Width of the stroke used to draw progress.
- `progressBgColor:`Background color of the progress bar.
- `showText :` If show inter progress text.
- `android:text :` Text of inter progress text.
- `android:textSize :` Size of inter progress text.
- `android:textColor :` Color of inter progress text.

**DonutProgressBar attributes:**
- `fillColor :` Color to fill inter circle.
- `progressColor :` Color of the stroke used to draw outer circles.
- `donutSize :` Size of progressbar in dp.
  - `small : 16dp`
  - `medium : 48dp`
  - `large : 76dp`

**HuaProgressBar attributes:**
- `progressStartColor :`Start color of the hua progress.
- `progressEndColor :`End color of the hua progress.
- `huaSize :` Size of progressbar in dp.
  - `small : 16dp`
  - `medium : 48dp`
  - `large : 76dp`

## Experimental:
- `WaveProgressBar :` Pending...

## License
The code is available under the [Apache License][license]

[donut]:http://7xom3t.com1.z0.glb.clouddn.com/donutprogressbar.gif
[license]:https://github.com/zac4j/CircleProgressBar/blob/master/LICENSE
