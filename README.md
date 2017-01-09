# CircleProgressBar
Circle Progress Bar for Android applications. DonutProgressBar inspired by MIUI.

## Screenshots


## Getting Started
**CircleProgressBar** (min API 16):
#### 1.This library is available on Maven Central, you can find it with Gradle:
```groovy
dependencies {
  compile 'com.zac4j.library:circleprogressbar:0.1.0'
}
```
#### 2.Add the DonutProgressBar widget in your xml file:
```xml
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
      android:layout_centerInParent="true"
      android:textSize="16sp"
      app:size="large"
      app:strokeBgColor="@color/gray"
      app:strokeColor="@color/purple"
      app:strokeWidth="4dp"
      />

</RelativeLayout>
```
#### 3.On activity onResume() method set progress animator:
```java
private void setProgress() {
  ValueAnimator animator = ValueAnimator.ofInt(0, 100);
  animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
    @Override public void onAnimationUpdate(ValueAnimator valueAnimator) {

      int progress = (int) valueAnimator.getAnimatedValue();

      mDonutProgressBar.setProgress(progress);
    }
  });

  animator.setRepeatCount(10);
  animator.setDuration(6000);
  animator.start();
}
```

#### 4.Options and Settings of DonutProgressBar
- `fillColor :` Color to fill center circle.
- `strokeWidth :` Width of the stroke used to draw outer circles.
- `strokeColor :` Color of the stroke used to draw outer circles.
- `strokeBgColor :` Background color of the outer circle.
- `showText :` If show center progress text.
- `android:text :` Text of center progress text.
- `android:textSize :` Size of center progress text.
- `android:textColor :` Color of center progress text.
- `size :` Size of outer circle in dp.
  - `small : 16dp`
  - `medium : 48dp`
  - `large : 76dp`

## Experimental:
- `WaveProgressBar :` Pending..inspired by AliPay Face Recognition UI

## License
The code is available under the [Apache License][license]

[license]:https://github.com/zac4j/CircleProgressBar/blob/master/LICENSE
