package com.cz.widgets.common.state;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cz.widgets.common.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by cz
 * @date 12/25/20 3:17 PM
 * @email bingo110@126.com
 */
public class StateMarkLayout extends FrameLayout {
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
    public static final int STATE_FLAG3=0x04;
    public static final int STATE_FLAG4=0x08;
    public static final int STATE_FLAG5=0x10;
    public static final int STATE_FLAG6=0x20;
    public static final int STATE_FLAG7=0x40;
    public static final int STATE_FLAG8=0x80;
    public static final int STATE_FLAG9=0x100;
    private static final int STATE_SIZE=0x200;

    public static final int[][] EXTRA_STATUS ={STATE_1,STATE_2,STATE_3, STATE_4,STATE_5,STATE_6, STATE_7,STATE_8,STATE_9};
    private static final List<Integer> STATE_LIST=new ArrayList<>();
    private int currentStateFlag;

    @IntDef({STATE_NONE,STATE_FLAG1,STATE_FLAG2,STATE_FLAG3,STATE_FLAG4,STATE_FLAG5,STATE_FLAG6,STATE_FLAG7,STATE_FLAG8,STATE_FLAG9})
    public @interface State{
    }

    static{
        //Add all the extra status.
        for(int i = 0; i< EXTRA_STATUS.length; i++){
            STATE_LIST.add(EXTRA_STATUS[i][0]);
        }
    }

    public StateMarkLayout(@NonNull Context context) {
        super(context);
    }

    public StateMarkLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StateMarkLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    /**
     * Enable one specific drawable status.
     * @param flag
     * @param enabled
     */
    public void setStateEnabled(@State int flag,boolean enabled) {
        for(int state=1;state<STATE_SIZE;state*=2){
            if(0 != (flag & state)){
                if(enabled){
                    currentStateFlag |=  state;
                } else {
                    currentStateFlag ^= state;
                }
            }
        }
        refreshDrawableState();
    }

    /**
     * Clean all the state.
     */
    public void clearStatus(){
        currentStateFlag =STATE_NONE;
        refreshDrawableState();
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        final int[] defaultDrawableState = super.onCreateDrawableState(extraSpace + 1);
        if (STATE_NONE!= currentStateFlag) {
            int activeStateSize=0;
            int[] activeStatus=new int[STATE_LIST.size()];
            for(int state=1;state<STATE_SIZE;state*=2){
                if(0 != (currentStateFlag & state)){
                    activeStatus[activeStateSize]=EXTRA_STATUS[state][0];
                    activeStateSize ++;
                }
            }
            int[] extraStates=new int[activeStateSize];
            System.arraycopy(activeStatus,0,extraStates,0,activeStateSize);
            return mergeDrawableStates(defaultDrawableState, extraStates);
        }
        return defaultDrawableState;
    }

    /**
     * Merge your own state values in <var>additionalState</var> into the base
     * state values <var>baseState</var> that were returned by
     * {@link #onCreateDrawableState(int)}.
     *
     * @param baseState The base state values returned by
     * {@link #onCreateDrawableState(int)}, which will be modified to also hold your
     * own additional state values.
     *
     * @param additionalState The additional state values you would like
     * added to <var>baseState</var>; this array is not modified.
     *
     * @return As a convenience, the <var>baseState</var> array you originally
     * passed into the function is returned.
     *
     * @see #onCreateDrawableState(int)
     */
    protected static int[] mergeDrawableStates(int[] baseState, int[] additionalState) {
        final int N = baseState.length;
        int i = N - 1;
        while (i >= 0 && baseState[i] == 0) {
            i--;
        }
        if(baseState.length <= i+additionalState.length){
            int[] newBaseState=new int[i+additionalState.length];
            System.arraycopy(additionalState,0,newBaseState,0,i);
            baseState = newBaseState;
        }
        System.arraycopy(additionalState, 0, baseState, i + 1, additionalState.length);
        return baseState;
    }
}
