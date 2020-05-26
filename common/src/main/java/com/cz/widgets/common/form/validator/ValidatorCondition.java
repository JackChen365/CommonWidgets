package com.cz.widgets.common.form.validator;

/**
 * Created by cz
 * @date 2020-02-29 15:00
 * @email bingo110@126.com
 * 
 */
public interface ValidatorCondition {
    /**
     * Check if the condition is valid
     * @return
     */
    boolean isValid();

    /**
     * Return if the condition if invalid. What kind of text we should display.
     * @return
     */
    String getErrorText();
}
