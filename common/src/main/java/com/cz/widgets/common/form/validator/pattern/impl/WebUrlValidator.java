package com.cz.widgets.common.form.validator.pattern.impl;

import android.util.Patterns;

import com.cz.widgets.common.form.validator.pattern.PatternValidator;

import java.util.regex.Pattern;

/**
 * Created by cz on 2016/9/23.
 */
public class WebUrlValidator extends PatternValidator {
    @Override
    public Pattern getPattern() {
        return Patterns.WEB_URL;
    }
}
