package com.cz.widgets.common.frame;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cz.widgets.common.R;

/**
 * @author Created by cz
 * @date 2020-03-08 20:03
 * @email bingo110@126.com
 */
public class FrameViewLayout extends FrameLayout implements FrameContainer {

    public FrameViewLayout(@NonNull Context context) {
        super(context);
    }

    public FrameViewLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FrameViewLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = getChildCount();
        for(int i=0;i<childCount;i++){
            View childView = getChildAt(i);
            LayoutParams layoutParams = (LayoutParams) childView.getLayoutParams();
            if(!layoutParams.contentView){
                //We hide all the other child views.
                childView.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    /**
     * Generate a default layout params.
     * When you call {@link ViewGroup#addView(View)}.
     * It will ask for a default LayoutParams
     * @return
     */
    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /**
     * Create a layout params from a giving one.
     * @param p
     * @return
     */
    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        Context context = getContext();
        return new LayoutParams(context,attrs);
    }

    @Override
    public boolean applyFrame(int frameId) {
        boolean applyFrame=false;
        int childCount = getChildCount();
        for(int i=0;i<childCount;i++){
            View childView = getChildAt(i);
            int id = childView.getId();
            if(frameId==id){
                applyFrame=true;
                break;
            }
        }
        return applyFrame;
    }

    @NonNull
    @Override
    public View getContentView() {
        View contentView=null;
        int childCount = getChildCount();
        for(int i=0;i<childCount;i++){
            View childView = getChildAt(i);
            LayoutParams layoutParams = (LayoutParams) childView.getLayoutParams();
            if(layoutParams.contentView) {
                contentView=childView;
                break;
            }
        }
        return contentView;
    }

    /**
     * Our custom LayoutParams object to support layout transition.
     * @attr R.attr.layout_content
     */
    public class LayoutParams extends FrameLayout.LayoutParams{
        public boolean contentView;

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.FrameViewLayout);
            contentView = a.getBoolean(R.styleable.FrameViewLayout_layout_content, false);
            a.recycle();
        }
    }

}
