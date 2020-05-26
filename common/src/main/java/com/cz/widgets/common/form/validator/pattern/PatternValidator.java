package com.cz.widgets.common.form.validator.pattern;

import android.text.Editable;
import android.text.TextUtils;


import com.cz.widgets.common.form.validator.Validator;

import java.util.regex.Pattern;

/**
 * Created by cz on 2016/9/23.
 */
public abstract class PatternValidator implements Validator {

    public abstract Pattern getPattern();

    @Override
    public boolean validator(Editable value) {
        Pattern pattern = getPattern();
        return !TextUtils.isEmpty(value)&&pattern.matcher(value).matches();
    }
}
