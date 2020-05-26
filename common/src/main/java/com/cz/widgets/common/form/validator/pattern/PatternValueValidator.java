package com.cz.widgets.common.form.validator.pattern;

import java.util.regex.Pattern;

/**
 * Created by cz on 2016/9/23.
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
