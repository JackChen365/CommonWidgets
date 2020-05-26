package com.cz.widgets.common;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;

import androidx.appcompat.view.ContextThemeWrapper;

/**
 * @author Created by cz
 * @date 2020-05-17 08:39
 * @email bingo110@126.com
 * This class shouldn't call outside.
 * We use the specific theme for all the widgets in this library.
 * If in your application you don't want to override the theme we will apply for our default style.
 */
public class ContextHelper {
    public static Context applyTheme(Context context){
        Resources.Theme theme = context.getTheme();
        theme.applyStyle(R.style.CommonWidgetStyle,false);
        return context;
    }

    /**
     * Return a wrapper context that wrapped by the theme:{@link R.attr#commonWidgets}
     * @return the wrapped context.
     */
    public static Context getWrapperContext(Context context) {
        TypedArray ta = context.obtainStyledAttributes(null, new int[]{R.attr.commonWidgets});
        int themeResId = ta.getResourceId(0, 0);
        if (themeResId != 0) {
            context = new ContextThemeWrapper(context, themeResId);
        }
        return context;
    }
}
