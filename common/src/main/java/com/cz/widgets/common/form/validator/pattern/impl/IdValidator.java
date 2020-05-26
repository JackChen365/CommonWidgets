package com.cz.widgets.common.form.validator.pattern.impl;

import com.cz.widgets.common.form.validator.pattern.PatternValidator;

import java.util.regex.Pattern;

/**
 * Created by cz on 2016/9/23.
 */
public class IdValidator extends PatternValidator {
    private final Pattern pattern;

    public IdValidator() {
        this.pattern = Pattern.compile("^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|(X|x))$");
    }

    @Override
    public Pattern getPattern() {
        return pattern;
    }
}
