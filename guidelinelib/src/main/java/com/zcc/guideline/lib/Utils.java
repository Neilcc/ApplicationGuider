package com.zcc.guideline.lib;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.text.TextUtils;
import android.util.TypedValue;

/**
 * Created by Hengyun on 16/5/30.
 */
public class Utils {

    /**
     * 获取版本名
     *
     * @return 当前App 版本名
     */
    public static String getVersionName(Context context) {
        if (!TextUtils.isEmpty(DataHolder.VERSION_NAME)) {
            return DataHolder.VERSION_NAME;
        } else {
            try {
                PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                if (!TextUtils.isEmpty(info.versionName)) {
                    DataHolder.VERSION_NAME = info.versionName;
                }
            } catch (Exception e) {
            }
            return DataHolder.VERSION_NAME;
        }
    }

    public static int dip2px(float dip, Context context) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics()
        );
    }

    private static class DataHolder {
        public static String VERSION_NAME = "";
    }


}
