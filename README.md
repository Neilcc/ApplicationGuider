# ApplicationGuider
#### A applicationGuider guide line (guide overlay /guide view) lib for Android applications

## Example

![img](https://github.com/Neilcc/ApplicationGuider/blob/master/photos/result.png)

## How to use

```
   GuideGenerator.init((Activity) getContext())
                .setHollowImageRes(R.drawable.u_biz_guide_home_page_hollow)
                .setTipViewRes(R.drawable.u_biz_guide_home_page_tip, GuideView.Position.TOP, 10, 0, 0, 0)
                .setTargetVersion(MGInfo.getVersionName(), NewsFragment.class.getSimpleName())
                .setTargetView(view)
                .show();

```

## Compile gradle :

1. add repository

```
  maven{
           url "https://dl.bintray.com/zccneil/BinaryStar/"
       }
```

```

  compile 'com.zcc.applicationguider:guidelinelib:1.0.0'

```


