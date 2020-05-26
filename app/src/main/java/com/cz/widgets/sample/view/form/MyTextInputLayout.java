package com.cz.widgets.sample.view.form;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import com.cz.widgets.common.form.layout.IEditText;
import com.cz.widgets.sample.R;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Created by cz on 2016/11/3.
 */
public class MyTextInputLayout extends TextInputLayout implements IEditText {
    public MyTextInputLayout(Context context) {
        this(context,null,0);
    }

    public MyTextInputLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyTextInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //The text input layout don't have an EditText view. We add our custom edit text here.
//        TextInputLayout#addView 238
//        public void addView(View child, int index, LayoutParams params) {
//            if (child instanceof EditText) {
//                android.widget.FrameLayout.LayoutParams flp = new android.widget.FrameLayout.LayoutParams(params);
//                flp.gravity = 16 | flp.gravity & -113;
//                this.inputFrame.addView(child, flp);
//                this.inputFrame.setLayoutParams(params);
//                this.updateInputLayoutMargins();
//                this.setEditText((EditText)child);
//            } else {
//                super.addView(child, index, params);
//            }
//
//        }
        inflate(context, R.layout.view_default_edit_layout_view,this);
    }

    @Override
    public void setOnFocusChangeListener(View.OnFocusChangeListener l) {
        super.setOnFocusChangeListener(l);
        EditText editText = getEditText();
        editText.setOnFocusChangeListener(l);
    }

    @Override
    public void addTextChangedListener(TextWatcher watcher) {
        EditText editText = getEditText();
        editText.addTextChangedListener(watcher);
    }

    @Override
    public void removeTextChangedListener(TextWatcher watcher) {
        EditText editText = getEditText();
        editText.removeTextChangedListener(watcher);
    }

    @Override
    public void setTextColor(int color) {
        EditText editText = getEditText();
        editText.setTextColor(color);
    }

    @Override
    public Editable getText() {
        EditText editText = getEditText();
        return editText.getText();
    }

    @Override
    public void setText(CharSequence text) {
        EditText editText = getEditText();
        editText.setText(text);
    }

    @Override
    public void setSelection(int selection) {
        EditText editText = getEditText();
        editText.setSelection(selection);
    }

    @Override
    public void setHintTextColor(int color) {
        EditText editText = getEditText();
        editText.setHintTextColor(color);
    }

    @Override
    public void setTextSize(int type, float textSize) {
        EditText editText = getEditText();
        editText.setTextSize(type,textSize);
    }

    @Override
    public void setInputType(int inputType) {
        EditText editText = getEditText();
        editText.setInputType(inputType);
    }

    @Override
    public void setTransformationMethod(TransformationMethod method) {
        EditText editText = getEditText();
        editText.setTransformationMethod(method);
    }

    @Override
    public TransformationMethod getTransformationMethod() {
        EditText editText = getEditText();
        return editText.getTransformationMethod();
    }

    @Override
    public void setFilters(InputFilter[] filters) {
        EditText editText = getEditText();
        editText.setFilters(filters);
    }

    @Override
    public void setMaxLines(int maxLines) {
        EditText editText = getEditText();
        editText.setMaxLines(maxLines);
    }
}
