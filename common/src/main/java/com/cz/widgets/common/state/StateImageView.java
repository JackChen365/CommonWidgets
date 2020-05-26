package com.cz.widgets.common.state;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.IntDef;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatImageView;

import com.cz.widgets.common.ContextHelper;
import com.cz.widgets.common.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by cz
 * @date 2020-5-14 16:44
 * @email bingo110@126.com
 *
 * Migrate from an old programming library - Created by cz on 12/9/16.
 *
 * This class allows us to draw a short text. You are not supposed to break the line.
 * But we actually support text appearance and text color state.
 * So even you just want to have an image view that supports text and multi extra drawable state.
 * This is the best thing you want.
 *
 * We support more extra drawable state.
 * @see R.attr#state1
 * @see R.attr#state2
 * @see R.attr#state3
 * @see R.attr#state4
 * @see R.attr#state5
 * @see R.attr#state6
 * @see R.attr#state7
 * @see R.attr#state8
 * @see R.attr#state9
 *
 */
public class StateImageView extends AppCompatImageView {
    private static final int[] STATE_1 = {R.attr.state1};
    private static final int[] STATE_2 = {R.attr.state2};
    private static final int[] STATE_3 = {R.attr.state3};
    private static final int[] STATE_4 = {R.attr.state4};
    private static final int[] STATE_5 = {R.attr.state5};
    private static final int[] STATE_6 = {R.attr.state6};
    private static final int[] STATE_7 = {R.attr.state7};
    private static final int[] STATE_8 = {R.attr.state8};
    private static final int[] STATE_9 = {R.attr.state9};

    public static final int STATE_NONE=0x00;
    public static final int STATE_FLAG1=0x01;
    public static final int STATE_FLAG2=0x02;
    public static final int STATE_FLAG3=0x03;
    public static final int STATE_FLAG4=0x04;
    public static final int STATE_FLAG5=0x05;
    public static final int STATE_FLAG6=0x06;
    public static final int STATE_FLAG7=0x07;
    public static final int STATE_FLAG8=0x08;
    public static final int STATE_FLAG9=0x09;

    public static final int[][] STATUS={STATE_1,STATE_2,STATE_3, STATE_4,STATE_5,STATE_6, STATE_7,STATE_8,STATE_9};
    private static final List<Integer> STATE_LIST=new ArrayList<>();
    private int state;
    /**
     * The current text color.
     */
    private int curTextColor;
    /**
     * The color state list.
     */
    private ColorStateList textColorStateList;
    /**
     * Text paint instance.
     */
    private final TextPaint textPaint=new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
    /**
     * The char sequence.
     */
    private CharSequence text;

    @IntDef({STATE_NONE,STATE_FLAG1,STATE_FLAG2,STATE_FLAG3,STATE_FLAG4,STATE_FLAG5,STATE_FLAG6,STATE_FLAG7,STATE_FLAG8,STATE_FLAG9})
    public @interface State{
    }

    static{
        for(int i=0;i<STATUS.length;i++){
            STATE_LIST.add(STATUS[i][0]);
        }
    }


    public StateImageView(Context context) {
        this(context,null,R.attr.stateImageView);
    }

    public StateImageView(Context context, AttributeSet attrs) {
        this(context, attrs,R.attr.stateImageView);
    }

    public StateImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Context wrapperContext = ContextHelper.getWrapperContext(context);
        TypedArray a = wrapperContext.obtainStyledAttributes(attrs, R.styleable.StateImageView,defStyleAttr,R.style.StateImageView);
        setStateEnabledInner(a.getInt(R.styleable.StateImageView_image_stateEnabled,STATE_NONE),true);
        setTextColorState(a.getColorStateList(R.styleable.StateImageView_image_textColor));
        setTextSize(a.getDimension(R.styleable.StateImageView_image_textSize,0f));
        setText(a.getString(R.styleable.StateImageView_image_text));
        a.recycle();
    }

    /**
     * Change the text color.
     * @param color
     */
    public void setTextColor(int color) {
        this.textColorStateList= ColorStateList.valueOf(color);
        updateTextColors();
    }

    /**
     * Change the text color state list.
     * @param colorStateList
     */
    public void setTextColorState(ColorStateList colorStateList) {
        this.textColorStateList= colorStateList;
        updateTextColors();
    }

    public void setTextSize(float textSize) {
        this.textPaint.setTextSize(textSize);
        invalidate();
    }

    public void setText(@StringRes int stringRes){
        this.text=getContext().getString(stringRes);
        invalidate();
    }

    public void setText(CharSequence text){
        this.text=text;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int heightMode=MeasureSpec.getMode(heightMeasureSpec);
        //If the text size more than the image. we will change the size.
        if(MeasureSpec.AT_MOST==heightMode|| MeasureSpec.UNSPECIFIED==heightMode){
            int measureWidth=getMeasuredWidth();
            int measureHeight=getMeasuredHeight();
            if(!TextUtils.isEmpty(text)){
                int textWidth= (int) textPaint.measureText(text,0,text.length());
                int textHeight = (int)(textPaint.descent() - textPaint.ascent());
                final int textMeasureWidth=getPaddingLeft()+textWidth+getPaddingRight();
                final int textMeasureHeight=getPaddingTop()+textHeight+getPaddingBottom();
                measureWidth= Math.max(measureWidth,textMeasureWidth);
                measureHeight= Math.max(measureHeight,textMeasureHeight);
            }
            setMeasuredDimension(measureWidth,measureHeight);
        }
    }

    /**
     * Update the text color by different drawable state.
     */
    private void updateTextColors() {
        boolean needInvalidate = false;
        int color = textColorStateList.getColorForState(getDrawableState(), 0);
        if (color != curTextColor) {
            curTextColor = color;
            needInvalidate = true;
        }
        if(needInvalidate){
            invalidate();
        }
    }

    /**
     * Use a specific color state.
     * @param flag
     * @param enabled
     */
    public void setStateEnabled(@State int flag,boolean enabled) {
        setStateEnabledInner(flag,enabled);
    }

    private void setStateEnabledInner(int flag,boolean enabled){
        state=enabled?flag:STATE_NONE;
        if(isShown()){
            refreshDrawableState();
        }
    }

    /**
     * Disable the customize state.
     */
    public void clearStatus(){
        state=STATE_NONE;
        refreshDrawableState();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        // If the drawable state is changing. we will change the text color.
        if (textColorStateList != null && textColorStateList.isStateful()) {
            updateTextColors();
        }
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (STATE_NONE!=state) {
            mergeDrawableStates(drawableState, STATUS[state-1]);
        }
        return drawableState;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if(TextUtils.isEmpty(text)){
            int width = getWidth();
            int height = getHeight();
            int color = textPaint.getColor();
            if(color!=curTextColor){
                textPaint.setColor(curTextColor);
            }
            //Measure the text width.
            final float textWidth = textPaint.measureText(text,0,text.length());
            canvas.drawText(text ,0,text.length(),(width - textWidth) / 2, (height-(textPaint.descent() + textPaint.ascent())) / 2, textPaint);
        }

    }
}
