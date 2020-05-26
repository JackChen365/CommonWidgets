package com.cz.widgets.common.form.layout;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;


/**
 * Created by cz
 * @date 2020-02-29 14:53
 * @email bingo110@126.com
 *
 * The default form edit text.
 * We use this interface {@link IEditText} in order to abstract the Editor.
 * For example If you want to use the TextInputLayout. You could customize the Editor by implementing the interface.
 *
 * @see EditLayout
 * @see com.cz.widgets.common.R.attr#edit_layout
 */
public class DefaultFormEditText extends AppCompatEditText implements IEditText {

    public DefaultFormEditText(Context context) {
        super(context);
    }

    public DefaultFormEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DefaultFormEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
