package com.cz.widgets.common.form.validator.pattern.impl;

import com.cz.widgets.common.form.validator.pattern.PatternValidator;

import java.util.regex.Pattern;

/**
 * Created by cz on 2016/9/23.
 */
public class NumberValidator extends PatternValidator {
    private final Pattern pattern;
    public NumberValidator() {
        pattern= Pattern.compile("[0-9]+");
    }

    @Override
    public Pattern getPattern() {
        return pattern;
    }
}
