package com.cz.widgets.common.form.validator.pattern.impl;

import com.cz.widgets.common.form.validator.pattern.PatternValidator;

import java.util.regex.Pattern;

/**
 * Created by cz on 2016/9/23.
 */
public class CharacterValidator extends PatternValidator {
    private final Pattern pattern;
    public CharacterValidator() {
        pattern= Pattern.compile("[a-zA-Z]+");
    }

    @Override
    public Pattern getPattern() {
        return pattern;
    }
}
