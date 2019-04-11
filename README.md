# ColorPicker

使用kotlin实现图片颜色的获取

### 效果图

<img src="./screenshot/SVID_20190411_163454_1.gif" width="360px"/>

### 实现

1. 导入 Palette 库
```
    implementation 'com.android.support:palette-v7:28.0.0'
```

2. 获取颜色
```
    /**
     * 获取图片颜色
     */
    private fun getImageColor(bitmap: Bitmap) {
        colorList.clear()
        Palette.from(bitmap)
            .generate { palette ->
                if (palette != null) {
                    //最活跃的颜色
                    val vibrant = palette.vibrantSwatch
                    //活跃的亮色
                    val lightVibrant = palette.lightVibrantSwatch
                    //活跃的深色
                    val darkVibrant = palette.darkVibrantSwatch
                    //最柔和的颜色
                    val muted = palette.mutedSwatch
                    //柔和的亮色
                    val lightMuted = palette.lightMutedSwatch
                    //柔和的深色
                    val darkMuted = palette.darkMutedSwatch

                    if (lightMuted != null) {
                        colorList.add(ColorBean(lightMuted.rgb))
                    }
                    if (lightVibrant != null) {
                        colorList.add(ColorBean(lightVibrant.rgb))
                    }
                    if (vibrant != null) {
                        colorList.add(ColorBean(vibrant.rgb))
                    }
                    if (muted != null) {
                        colorList.add(ColorBean(muted.rgb))
                    }
                    if (darkVibrant != null) {
                        colorList.add(ColorBean(darkVibrant.rgb))
                    }
                    if (darkMuted != null) {
                        colorList.add(ColorBean(darkMuted.rgb))
                    }
                    gvList.numColumns = colorList.size
                    gvColorAdapter.notifyDataSetChanged()
                }
            }

    }
```