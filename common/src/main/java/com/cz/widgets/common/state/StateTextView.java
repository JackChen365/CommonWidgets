package com.cz.widgets.common.state;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.IntDef;
import androidx.appcompat.widget.AppCompatTextView;

import com.cz.widgets.common.ContextHelper;
import com.cz.widgets.common.R;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by cz on 12/9/16.
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
 */
public class StateTextView extends AppCompatTextView {
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

    static{
        for(int i=0;i<STATUS.length;i++){
            STATE_LIST.add(STATUS[i][0]);
        }
    }

    @IntDef({STATE_NONE,STATE_FLAG1,STATE_FLAG2,STATE_FLAG3,STATE_FLAG4,STATE_FLAG5,STATE_FLAG6,STATE_FLAG7,STATE_FLAG8,STATE_FLAG9})
    public @interface State{
    }


    public StateTextView(Context context) {
        this(context,null,0);
    }

    public StateTextView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public StateTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Context wrapperContext = ContextHelper.getWrapperContext(context);
        TypedArray a = wrapperContext.obtainStyledAttributes(attrs, R.styleable.StateTextView);
        setStateEnabledInner(a.getInt(R.styleable.StateTextView_stateEnabled,STATE_NONE),true);
        a.recycle();
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
        refreshDrawableState();
    }

    /**
     * Disable the customize state.
     */
    public void clearStatus(){
        state=STATE_NONE;
        refreshDrawableState();
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (STATE_NONE!=state) {
            mergeDrawableStates(drawableState, STATUS[state-1]);
        }
        return drawableState;
    }
}
