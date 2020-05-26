package com.cz.widgets.common.form.validator;


import com.cz.widgets.common.form.layout.EditLayout;
import com.cz.widgets.common.form.layout.IEditText;

/**
 * Created by cz
 * @date 2020-02-29 14:29
 * @email bingo110@126.com
 */
public interface Editable {
    /**
     * Return the abstract editor
     * @return
     */
    IEditText getEditor();

    /**
     * This interface responsible for editor text changed event.
     * @param textWatcher
     */
    void setOnTextChangeListener(EditLayout.OnTextChangeListener textWatcher);
}
