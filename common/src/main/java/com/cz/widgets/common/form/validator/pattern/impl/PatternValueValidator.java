package com.cz.widgets.common.form.validator.pattern.impl;

import com.cz.widgets.common.form.validator.pattern.PatternValidator;

import java.util.regex.Pattern;

/**
 * Created by cz
 * @date 2020-02-29 14:27
 * @email bingo110@126.com
 *
 */
public  class PatternValueValidator extends PatternValidator {
    private final Pattern pattern;

    public PatternValueValidator(String patternValue) {
        this.pattern = Pattern.compile(patternValue);
    }

    @Override
    public Pattern getPattern() {
        return pattern;
    }
}
