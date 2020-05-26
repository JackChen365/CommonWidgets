package com.cz.widgets.common.form.validator.pattern.impl;

import com.cz.widgets.common.form.validator.pattern.PatternValidator;

import java.util.regex.Pattern;

/**
 * Created by cz on 2016/9/23.
 */
public class PhoneValidator extends PatternValidator {
    private final Pattern pattern;
    public PhoneValidator() {
        pattern= Pattern.compile("[1][3,4,5,7,8][0-9]{9}");
    }
    @Override
    public Pattern getPattern() {
        return pattern;
    }
}
