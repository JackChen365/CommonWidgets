package com.cz.widgets.common.form.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.DrawableRes;

import com.cz.widgets.common.ContextHelper;
import com.cz.widgets.common.R;
import com.cz.widgets.common.form.validator.Editable;
import com.cz.widgets.common.form.validator.Validator;
import com.cz.widgets.common.form.validator.ValidatorCondition;
import com.cz.widgets.common.form.validator.pattern.PatternValueValidator;
import com.cz.widgets.common.form.validator.pattern.RangeValidator;
import com.cz.widgets.common.form.validator.pattern.impl.CharacterValidator;
import com.cz.widgets.common.form.validator.pattern.impl.EmailValidator;
import com.cz.widgets.common.form.validator.pattern.impl.IdValidator;
import com.cz.widgets.common.form.validator.pattern.impl.NumberValidator;
import com.cz.widgets.common.form.validator.pattern.impl.PhoneValidator;
import com.cz.widgets.common.form.validator.pattern.impl.WebUrlValidator;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by cz on 24/9/16.
 * This is an layout to simplify the forms.
 * We support those mainly functions:
 * 1. Customize the layout. You could include your custom layout inside the end of the layout.
 * 2. Customize the attributes. such as the hint icon and delete icon.
 * 3. Abstract the editor. you could be able to put EditText or InputTextLayout.
 * 4. Cooperate with the {@link com.cz.widgets.common.form.validator.ValidatorObservable}. We could easily observer all the layout inside the form.
 * 5. Support the regex to verify the information.
 *
 * Also take a look at {@link com.cz.widgets.common.form.validator.ValidatorObservable}
 */
public class EditLayout<V extends IEditText> extends LinearLayout implements ValidatorCondition, Editable {
    public static final int NO_LIMIT=-1;
    /**
     * All the pre-defined regex-patterns.
     */
    public static final int ANY=0x00;
    public static final int PATTERN_NUMBER=0x01<<0;
    public static final int PATTERN_CHARACTER=0x01<<1;
    public static final int PATTERN_EMAIL=0x01<<2;
    public static final int PATTERN_PHONE=0x01<<3;
    public static final int PATTERN_WEB_URL=0x01<<4;
    public static final int PATTERN_ID=0x01<<5;

    /**
     * The common input type.
     */
    public static final int INPUT_NUMBER=0x01;
    public static final int INPUT_TEXT=0x02;
    public static final int INPUT_NUMBER_PASSWORD=0x03;
    public static final int INPUT_TEXT_PASSWORD=0x04;

    /**
     * All the custom validator.
     */
    private final List<Validator> validatorList;
    /**
     * The focus change listener.
     */
    private View.OnFocusChangeListener onFocusChangeListener;
    /**
     * The text change listener.
     */
    private OnTextChangeListener listener;
    /**
     * The under-line background.
     */
    private Drawable editorBackground;
    /**
     * The editor. We use generic to abstract the View.
     */
    private V editor;
    /**
     * The hint image view.
     */
    private ImageView hintView;
    /**
     * The delete image view.
     */
    private ImageView deleteView;
    /**
     * The error hint error information that not match the regex-pattern.
     */
    private String editErrorText;
    /**
     * The empty hint error information.
     */
    private String emptyErrorText;
    /**
     * The error information that will record the error hint text orderly.
     * If the text is empty. we record the empty error information otherwise record the error information.
     */
    private String errorInformation;
    /**
     * The error hint text color.
     */
    private int errorTextColor;
    /**
     * The normal text color.
     */
    private int textColor;

    public EditLayout(Context context) {
        this(context,null, R.attr.editLayout);
    }

    public EditLayout(Context context, AttributeSet attrs) {
        this(context, attrs,R.attr.editLayout);
    }

    public EditLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //Inflate the template layout.
        inflate(context, R.layout.view_edit_layout, this);
        setWillNotDraw(false);
        setGravity(Gravity.CENTER_VERTICAL);
        validatorList =new ArrayList<>();

        Context wrapperContext = ContextHelper.getWrapperContext(context);
        TypedArray a = wrapperContext.obtainStyledAttributes(attrs, R.styleable.EditLayout,defStyleAttr,R.style.EditLayout);
        //Initialize the default layout from the ViewStub
        int editLayout=a.getResourceId(R.styleable.EditLayout_edit_layout,NO_ID);
        ViewStub viewStub= findViewById(R.id.editLayoutViewStub);
        viewStub.setLayoutResource(editLayout);
        viewStub.inflate();
        initEditLayout();

        //Initialize all the custom style attributes.
        setHintDrawable(a.getDrawable(R.styleable.EditLayout_edit_hintDrawable));
        setEditHint(a.getString(R.styleable.EditLayout_edit_hint));
        setEditText(a.getString(R.styleable.EditLayout_edit_text));
        setEditHintTextColor(a.getColor(R.styleable.EditLayout_edit_hintTextColor, Color.DKGRAY));
        setEditTextColor(a.getColor(R.styleable.EditLayout_edit_textColor, Color.GRAY));
        setEditTextSize(a.getDimension(R.styleable.EditLayout_edit_textSize,0));
        setSearchDeleteDrawable(a.getDrawable(R.styleable.EditLayout_edit_deleteDrawable));
        setEditPadding((int) a.getDimension(R.styleable.EditLayout_edit_padding,0));
        setEditErrorTextColor(a.getColor(R.styleable.EditLayout_edit_errorTextColor,NO_ID));
        setEditError(a.getString(R.styleable.EditLayout_edit_errorText));
        setEditEmptyError(a.getString(R.styleable.EditLayout_edit_emptyErrorText));
        setEditValidator(a.getInt(R.styleable.EditLayout_edit_validator,ANY));
        setPatternValidator(a.getString(R.styleable.EditLayout_edit_patternValidator));
        setEditorBackground(a.getDrawable(R.styleable.EditLayout_edit_background));
        setEditInputType(a.getInt(R.styleable.EditLayout_edit_inputType, INPUT_TEXT));
        setEditMaxLength(a.getInteger(R.styleable.EditLayout_edit_maxLength, Short.MAX_VALUE));
        setEditMaxLine(a.getInteger(R.styleable.EditLayout_edit_maxLine,1));

        //Add text range validator.
        int patterMinTextLength = a.getInteger(R.styleable.EditLayout_edit_patterMinTextLength, NO_LIMIT);
        int patterMaxTextLength = a.getInteger(R.styleable.EditLayout_edit_patterMaxTextLength, Integer.MAX_VALUE);
        validatorList.add(new RangeValidator(patterMinTextLength,patterMaxTextLength));
        a.recycle();
    }

    /**
     * Initialize all the views inside this layout.
     */
    private void initEditLayout() {
        hintView= findViewById(R.id.hintIcon);
        editor= (V)findViewById(R.id.et_editor);
        deleteView= findViewById(R.id.deleteIcon);
        deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(editor.getText())) {
                    editor.setText(null);
                    v.setVisibility(View.GONE);
                }
            }
        });
        editor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(null!=onFocusChangeListener){
                    onFocusChangeListener.onFocusChange(view,hasFocus);
                }
                if(TextUtils.isEmpty(editor.getText())){
                    deleteView.setVisibility(View.GONE);
                } else {
                    deleteView.setVisibility(hasFocus? View.VISIBLE: View.GONE);
                }
                setActivated(hasFocus);
                hintView.setSelected(hasFocus);
            }
        });
        editor.addTextChangedListener(new TextWatcher() {
            private CharSequence lastItem = null;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(NO_ID!=errorTextColor){
                    editor.setTextColor(isValid()?textColor:errorTextColor);
                }
                deleteView.setVisibility(TextUtils.isEmpty(editor.getText()) ? View.GONE : View.VISIBLE);
                if (null != listener) {
                    listener.onTextChanged(s, lastItem, count);
                }
                lastItem = s;
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
            }
        });
    }


    /**
     * Customize the regex pattern if the pre-defined regex padding not available for you.
     * @param patternValue
     */
    private void setPatternValidator(String patternValue) {
        if(!TextUtils.isEmpty(patternValue)){
            String[] patternArray = patternValue.split("\\s+");
            if(null!=patternArray){
                for(int i=0;i<patternArray.length;i++){
                    this.validatorList.add(new PatternValueValidator(patternArray[i]));
                }
            }
        }
    }

    public void setOnFocusChangeListener(View.OnFocusChangeListener listener){
        this.onFocusChangeListener=listener;
    }

    public void setHintDrawableResource(@DrawableRes int res) {
        setHintDrawable(getResources().getDrawable(res));
    }

    public void setHintDrawable(Drawable drawable) {
        if(null!=drawable){
            hintView.setVisibility(View.VISIBLE);
            hintView.setImageDrawable(drawable);
        } else {
            hintView.setVisibility(View.GONE);
        }
    }

    public void setEditHint(String text) {
        editor.setHint(text);
    }

    public void setEditText(String text) {
        editor.setText(text);
        editor.setSelection(TextUtils.isEmpty(text)?0:text.length());
    }

    public void setEditHintTextColor(int color) {
        editor.setHintTextColor(color);
    }

    public void setEditTextColor(int color) {
        this.textColor=color;
        editor.setTextColor(color);
    }

    public void setEditErrorTextColor(int color) {
        this.errorTextColor=color;
    }

    public void setEditTextSize(float textSize){
        editor.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }

    public void setEditEnabled(boolean enabled){
        editor.setEnabled(enabled);
    }

    public void setEditInputType(int inputType){
        switch (inputType){
            case INPUT_NUMBER:
            case INPUT_NUMBER_PASSWORD:
                editor.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                break;
            case INPUT_TEXT:
            case INPUT_TEXT_PASSWORD:
                default:
                editor.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
        }
        setEditPasswordTransformation(INPUT_NUMBER == inputType || INPUT_TEXT == inputType);
    }

    private void setEditorBackground(Drawable drawable) {
        this.editorBackground =drawable;
    }

    public void setEditPasswordTransformation(boolean show){
        if (show) {
            editor.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            editor.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }

    public TransformationMethod getTransformationMethod(){
        return editor.getTransformationMethod();
    }

    public void toggleEditPasswordTransformation(){
        android.text.Editable text = editor.getText();
        TransformationMethod transformationMethod = getTransformationMethod();
        if(transformationMethod== HideReturnsTransformationMethod.getInstance()){
            editor.setTransformationMethod(PasswordTransformationMethod.getInstance());
        } else {
            editor.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
        if(!TextUtils.isEmpty(text)){
            editor.setSelection(text.length());
        }
    }

    public void setEditPadding(int padding) {
        editor.setPadding(padding, 0, padding, 0);
    }

    public void setEditError(String text) {
        this.editErrorText =text;
    }

    public void setEditEmptyError(String emptyError) {
        this.emptyErrorText =emptyError;
    }

    public void setEditMaxLength(int maxLength) {
        editor.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
    }

    public void setEditMaxLine(int maxLine) {
        editor.setMaxLines(maxLine);
    }


    private void setEditValidator(int flag) {
        validatorList.clear();
        if(0!=(PATTERN_EMAIL&flag)){
            validatorList.add(new EmailValidator());
        }
        if(0!=(PATTERN_NUMBER&flag)){
            validatorList.add(new NumberValidator());
        }
        if(0!=(PATTERN_PHONE&flag)){
            validatorList.add(new PhoneValidator());
        }
        if(0!=(PATTERN_WEB_URL&flag)){
            validatorList.add(new WebUrlValidator());
        }
        if(0!=(PATTERN_CHARACTER&flag)){
            validatorList.add(new CharacterValidator());
        }
        if(0!=(PATTERN_ID&flag)){
            validatorList.add(new IdValidator());
        }
    }

    @Override
    public String getErrorText(){
        return errorInformation;
    }

    @Override
    public boolean isValid(){
        boolean validatorResult=true;
        android.text.Editable text = getText();
        for(int i = 0; i< validatorList.size(); i++){
            Validator validator = validatorList.get(i);
            if(!(validatorResult&=validator.validator(text))){
                break;
            }
        }
        if(!validatorResult){
            updateErrorInformation();
        }
        return validatorResult;
    }

    private void updateErrorInformation(){
        //Record the error message.
        android.text.Editable text = getText();
        if(TextUtils.isEmpty(text)&&!TextUtils.isEmpty(emptyErrorText)){
            errorInformation = emptyErrorText;
        } else if(!TextUtils.isEmpty(editErrorText)){
            errorInformation = editErrorText;
        }
    }

    public V getEditor(){
        return editor;
    }

    public android.text.Editable getText(){
        return editor.getText();
    }

    public void setSearchDeleteDrawable(Drawable drawable) {
        if(null!=drawable){
            deleteView.setImageDrawable(drawable);
        } else {
            deleteView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (null!= editorBackground) {
            editorBackground.setState(getDrawableState());
        }
    }

    @Override
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        if (null!= editorBackground) {
            editorBackground.jumpToCurrentState();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if(null!=editorBackground){
            int width = getWidth();
            int height = getHeight();
            int intrinsicHeight = editorBackground.getIntrinsicHeight();
            editorBackground.setBounds(0,height-intrinsicHeight,width,height);
            editorBackground.draw(canvas);
        }
    }

    @Override
    public void setOnTextChangeListener(OnTextChangeListener listener){
        this.listener=listener;
    }

    public interface OnTextChangeListener{
        void onTextChanged(CharSequence newItem, CharSequence oldItem, int count);
    }


}
