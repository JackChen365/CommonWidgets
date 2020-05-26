package com.cz.widgets.textview.span.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cz.widgets.textview.ViewSpanLayout;
import com.cz.widgets.textview.span.callback.SpannableComponent;
import com.cz.widgets.textview.span.click.ClickableReplacementSpan;
import com.cz.widgets.textview.span.click.TouchableMovementMethod;

/**
 * Created by cz
 * @date 2020-04-01 22:02
 * @email bingo110@126.com
 * Replacement view span.
 * This class allows us draw a view inside the ClickableReplacementSpan
 * So we could literally use any view like the {@link android.widget.TableLayout} to support more functions
 *
 * We use {@link com.cz.widgets.textview.span.click.TouchableMovementMethod} to receive touch event.
 * So you could be setting the click listener just like a view.
 *
 */
public class ClickableViewSpan extends ClickableReplacementSpan {
    private final Rect bounds=new Rect();
    /**
     * The host view.
     */
    private TextView textView;
    /**
     * The wrapped view.
     */
    private ViewGroup spanLayout;
    private View wrappedView;

    public ClickableViewSpan(final TextView textView, final View view){
        Context context = textView.getContext();
        this.textView = textView;
        this.textView.setMovementMethod(TouchableMovementMethod.getInstance());
        this.wrappedView = view;
        this.spanLayout = new ViewSpanLayout(context, textView);
        this.spanLayout.setLayoutParams(view.getLayoutParams());
        attachSpannableCallback(textView,view);
        ViewParent parent = view.getParent();
        if(null!=parent&&parent instanceof ViewGroup){
            ViewGroup viewGroup = (ViewGroup) parent;
            viewGroup.removeView(view);
        }
        this.spanLayout.addView(view,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        textView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                spanLayout =null;
            }
        });
    }

    /**
     * Attach to the view.
     * @param view
     */
    private void attachSpannableCallback(TextView hostView,View view){
        if(view instanceof SpannableComponent){
            SpannableComponent spannableComponent = (SpannableComponent) view;
            spannableComponent.attachToView(hostView);
        }
        if(view instanceof ViewGroup){
            ViewGroup viewGroup=(ViewGroup)view;
            for(int i=0;i<viewGroup.getChildCount();i++){
                final View childView=viewGroup.getChildAt(i);
                attachSpannableCallback(hostView,childView);
            }
        }
    }

    public void setBounds(int left,int top,int right,int bottom){
        this.bounds.set(left,top,right,bottom);
    }

    public void setBounds(Rect bounds){
        this.bounds.set(bounds);
    }

    public Rect getBounds(){
        return bounds;
    }

    public int getMeasuredWidth(){
        return spanLayout.getMeasuredWidth();
    }

    public int getMeasuredHeight(){
        return spanLayout.getMeasuredHeight();
    }

    @Override
    @SuppressLint("Range")
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
        int measuredWidth = textView.getMeasuredWidth();
        int measuredHeight = textView.getMeasuredHeight();
        if(0 == measuredWidth && 0 == measuredHeight){
            //Before we measure the view by the measure mode.
            //We should let the text view measure the wrapped view by the mode:View.MeasureSpec.AT_MOST
            spanLayout.measure(View.MeasureSpec.makeMeasureSpec(ViewGroup.LayoutParams.WRAP_CONTENT,View.MeasureSpec.AT_MOST),
                    View.MeasureSpec.makeMeasureSpec(ViewGroup.LayoutParams.WRAP_CONTENT,View.MeasureSpec.AT_MOST));
            int wrappedViewMeasuredWidth = spanLayout.getMeasuredWidth();
            int wrappedViewMeasuredHeight = spanLayout.getMeasuredHeight();
            spanLayout.layout(0,0,wrappedViewMeasuredWidth,wrappedViewMeasuredHeight);
            if (null!=fm) {
                fm.ascent= fm.top;
                fm.descent=fm.top+wrappedViewMeasuredHeight;
                fm.bottom=fm.top+wrappedViewMeasuredHeight;
            }
            return wrappedViewMeasuredWidth;
        } else {
            //We imitate the measure spec. Cause we know exactly how big the text view already.
            int parentWidthMeasureSpec= View.MeasureSpec.makeMeasureSpec(measuredWidth, View.MeasureSpec.EXACTLY);
            int parentHeightMeasureSpec= View.MeasureSpec.makeMeasureSpec(measuredWidth, View.MeasureSpec.EXACTLY);
            ViewGroup.LayoutParams layoutParams = spanLayout.getLayoutParams();
            //Create a new measure spec for the child view.
            int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec, 0, layoutParams.width);
            int childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec, 0, layoutParams.height);

            spanLayout.measure(childWidthMeasureSpec,childHeightMeasureSpec);
            int wrappedViewMeasuredWidth = spanLayout.getMeasuredWidth();
            int wrappedViewMeasuredHeight = spanLayout.getMeasuredHeight();
            spanLayout.layout(0,0,wrappedViewMeasuredWidth,wrappedViewMeasuredHeight);
            if (null!=fm) {
                fm.ascent= fm.top;
                fm.descent=fm.top+wrappedViewMeasuredHeight;
                fm.bottom=fm.top+wrappedViewMeasuredHeight;
            }
            return wrappedViewMeasuredWidth;
        }
    }

    /**
     * Does the hard part of measureChildren: figuring out the MeasureSpec to
     * pass to a particular child. This method figures out the right MeasureSpec
     * for one dimension (height or width) of one child view.
     *
     * The goal is to combine information from our MeasureSpec with the
     * LayoutParams of the child to get the best possible results. For example,
     * if the this view knows its size (because its MeasureSpec has a mode of
     * EXACTLY), and the child has indicated in its LayoutParams that it wants
     * to be the same size as the parent, the parent should ask the child to
     * layout given an exact size.
     *
     * @param spec The requirements for this view
     * @param padding The padding of this view for the current dimension and
     *        margins, if applicable
     * @param childDimension How big the child wants to be in the current
     *        dimension
     * @return a MeasureSpec integer for the child
     */
    public static int getChildMeasureSpec(int spec, int padding, int childDimension) {
        int specMode = View.MeasureSpec.getMode(spec);
        int specSize = View.MeasureSpec.getSize(spec);


        int size = Math.max(0, specSize - padding);

        int resultSize = 0;
        int resultMode = 0;

        switch (specMode) {
            // Parent has imposed an exact size on us
            case View.MeasureSpec.EXACTLY:
                if (childDimension >= 0) {
                    resultSize = childDimension;
                    resultMode = View.MeasureSpec.EXACTLY;
                } else if (childDimension == ViewGroup.LayoutParams.MATCH_PARENT) {
                    // Child wants to be our size. So be it.
                    resultSize = size;
                    resultMode = View.MeasureSpec.EXACTLY;
                } else if (childDimension == ViewGroup.LayoutParams.WRAP_CONTENT) {
                    // Child wants to determine its own size. It can't be
                    // bigger than us.
                    resultSize = size;
                    resultMode = View.MeasureSpec.AT_MOST;
                }
                break;

            // Parent has imposed a maximum size on us
            case View.MeasureSpec.AT_MOST:
                if (childDimension >= 0) {
                    // Child wants a specific size... so be it
                    resultSize = childDimension;
                    resultMode = View.MeasureSpec.EXACTLY;
                } else if (childDimension == ViewGroup.LayoutParams.MATCH_PARENT) {
                    // Child wants to be our size, but our size is not fixed.
                    // Constrain child to not be bigger than us.
                    resultSize = size;
                    resultMode = View.MeasureSpec.AT_MOST;
                } else if (childDimension == ViewGroup.LayoutParams.WRAP_CONTENT) {
                    // Child wants to determine its own size. It can't be
                    // bigger than us.
                    resultSize = size;
                    resultMode = View.MeasureSpec.AT_MOST;
                }
                break;

            // Parent asked to see how big we want to be
            case View.MeasureSpec.UNSPECIFIED:
                if (childDimension >= 0) {
                    // Child wants a specific size... let him have it
                    resultSize = childDimension;
                    resultMode = View.MeasureSpec.EXACTLY;
                } else if (childDimension == ViewGroup.LayoutParams.MATCH_PARENT) {
                    // Child wants to be our size... find out how big it should
                    // be
                    resultSize = size;
                    resultMode = View.MeasureSpec.UNSPECIFIED;
                } else if (childDimension == ViewGroup.LayoutParams.WRAP_CONTENT) {
                    // Child wants to determine its own size.... find out how
                    // big it should be
                    resultSize = size;
                    resultMode = View.MeasureSpec.UNSPECIFIED;
                }
                break;
        }
        //noinspection ResourceType
        return View.MeasureSpec.makeMeasureSpec(resultSize, resultMode);
    }

    public View getView(){
        return spanLayout;
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        y+=fontMetrics.top;
        setBounds((int)x, y, (int) (x+measuredWidth), y+measuredHeight);
        canvas.save();
        //Translate the canvas.
        canvas.translate(x,y);
        spanLayout.draw(canvas);
        canvas.restore();
    }

    public void invalidate() {
        textView.invalidate();
    }

    public void refreshDrawableState(){
        wrappedView.refreshDrawableState();
    }

    @Override
    public void setPressed(boolean pressed) {
        wrappedView.setPressed(pressed);
    }

    @Override
    public boolean isPressed() {
        return wrappedView.isPressed();
    }

    @Override
    public void setEnabled(boolean enabled) {
        wrappedView.setEnabled(enabled);
    }

    @Override
    public boolean isEnabled() {
        return wrappedView.isPressed();
    }

    @Override
    public boolean isChecked() {
        return false;
    }

    @Override
    public void setActivated(boolean activated) {
        wrappedView.setActivated(activated);
    }

    @Override
    public boolean isActivated() {
        return wrappedView.isActivated();
    }

    @Override
    public void setSelected(boolean selected) {
        wrappedView.setSelected(selected);
    }

    @Override
    public boolean isSelected() {
        return wrappedView.isSelected();
    }

    @Override
    public void setOnClickListener(View.OnClickListener listener) {
        wrappedView.setOnClickListener(listener);
    }

    @Override
    public void setOnLongClickListener(View.OnLongClickListener listener) {
        wrappedView.setOnLongClickListener(listener);
    }

    @Override
    public void performClick(@NonNull View widget) {
        super.performClick(widget);
    }

    @Override
    public void performLongClick(TextView textView) {
        super.performLongClick(textView);
    }

    @Override
    public void onTouchEvent(TextView textView, MotionEvent event) {
        int left = textView.getPaddingLeft()+bounds.left;
        int top = textView.getPaddingTop()+bounds.top;
        event.offsetLocation(-left,-top);
        spanLayout.dispatchTouchEvent(event);
    }
}
