package com.cz.widgets.textview.span.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextPaint;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.cz.widgets.textview.span.click.ClickableReplacementSpan;

import java.io.InputStream;


/**
 * @author Created by cz
 * @date 2019-05-14 16:14
 * @email bingo110@126.com
 *
 * The clickable drawable span object.
 * We support a few drawable states.
 *
 * @see #setPressed(boolean)
 * @see #setEnabled(boolean)
 * @see #setChecked(boolean)
 * @see #setActivated(boolean)
 * @see #setSelected(boolean)
 *
 */
public class ClickableDrawableSpan extends ClickableReplacementSpan {
    //All the different drawable state status.
    private static final int FLAG_PRESSED = 0x01;
    private static final int FLAG_SELECTED = 0x02;
    private static final int FLAG_ENABLED = 0x04;
    private static final int FLAG_CHECKED = 0x08;
    private static final int FLAG_ACTIVATED = 0x10;
    /**
     * A constant indicating that the bottom of this span should be aligned
     * with the bottom of the surrounding text, i.e., at the same level as the
     * lowest descender in the text.
     */
    public static final int ALIGN_BOTTOM = 0;
    /**
     * A constant indicating that the bottom of this span should be aligned
     * with the baseline of the surrounding text.
     */
    public static final int ALIGN_BASELINE = 1;
    public static final int ALIGN_CENTER=2;
    public static final int ALIGN_TOP=3;

    private final ClickableDrawableGestureListener gestureListener=new ClickableDrawableGestureListener();
    private GestureDetector gestureDetector;

    @IntDef({ALIGN_TOP,ALIGN_CENTER,ALIGN_BASELINE,ALIGN_BOTTOM})
    public @interface DrawableAlign{
    }
    @Nullable
    private Drawable drawable;
    @Nullable
    private Uri contentUri;
    @DrawableRes
    private int resourceId;
    @Nullable
    private Context context;

    public int privateFlags=FLAG_ENABLED;

    private ThreadLocal<Rect> tmpRect=new ThreadLocal();
    /**
     * 当前绘制尺寸
     */
    private final Rect bounds=new Rect();
    /**
     * drawable边距数组
     */
    private final int[] padding=new int[4];

    protected final int verticalAlignment;

    /**
     * Creates a {@link DynamicDrawableSpan}. The default vertical alignment is
     * {@link #ALIGN_BOTTOM}
     */
    public ClickableDrawableSpan() {
        verticalAlignment = ALIGN_BOTTOM;
    }


    /**
     * @deprecated Use {@link #ClickableDrawableSpan(Context, Bitmap)} instead.
     */
    @Deprecated
    public ClickableDrawableSpan(@NonNull Bitmap b) {
        this(null, b, ALIGN_BOTTOM);
    }

    /**
     * @deprecated Use {@link #ClickableDrawableSpan(Context, Bitmap, int)} instead.
     */
    @Deprecated
    public ClickableDrawableSpan(@NonNull Bitmap b, int verticalAlignment) {
        this(null, b, verticalAlignment);
    }

    /**
     * Constructs an {@link ImageSpan} from a {@link Context} and a {@link Bitmap} with the default
     * alignment {@link DynamicDrawableSpan#ALIGN_BOTTOM}
     *
     * @param context context used to create a drawable from {@param bitmap} based on the display
     *                metrics of the resources
     * @param bitmap  bitmap to be rendered
     */
    public ClickableDrawableSpan(@NonNull Context context, @NonNull Bitmap bitmap) {
        this(context, bitmap, ALIGN_BOTTOM);
    }

    /**
     * Constructs an {@link ImageSpan} from a {@link Context}, a {@link Bitmap} and a vertical
     * alignment.
     *
     * @param context           context used to create a drawable from {@param bitmap} based on
     *                          the display metrics of the resources
     * @param bitmap            bitmap to be rendered
     * @param verticalAlignment one of {@link DynamicDrawableSpan#ALIGN_BOTTOM} or
     *                          {@link DynamicDrawableSpan#ALIGN_BASELINE}
     */
    public ClickableDrawableSpan(@NonNull Context context, @NonNull Bitmap bitmap, int verticalAlignment) {
        this.context = context;
        this.drawable =  new BitmapDrawable(context.getResources(), bitmap);
        this.verticalAlignment =verticalAlignment;
        this.gestureDetector=new GestureDetector(context,gestureListener);
    }

    /**
     * Constructs an {@link ImageSpan} from a drawable with the default
     * alignment {@link DynamicDrawableSpan#ALIGN_BOTTOM}.
     *
     * @param drawable drawable to be rendered
     */
    public ClickableDrawableSpan(Context context, @NonNull Drawable drawable) {
        this(context,drawable, ALIGN_BOTTOM);
    }

    /**
     * Constructs an {@link ImageSpan} from a drawable and a vertical alignment.
     *
     * @param drawable          drawable to be rendered
     * @param verticalAlignment one of {@link DynamicDrawableSpan#ALIGN_BOTTOM} or
     *                          {@link DynamicDrawableSpan#ALIGN_BASELINE}
     */
    public ClickableDrawableSpan(Context context,@NonNull Drawable drawable, int verticalAlignment) {
        this.drawable = drawable;
        this.verticalAlignment =verticalAlignment;
        this.gestureDetector=new GestureDetector(context,gestureListener);
    }

    /**
     * Constructs an {@link ImageSpan} from a {@link Context} and a {@link Uri} with the default
     * alignment {@link DynamicDrawableSpan#ALIGN_BOTTOM}. The Uri source can be retrieved via
     *
     * @param context context used to create a drawable from {@param bitmap} based on the display
     *                metrics of the resources
     * @param uri     {@link Uri} used to construct the drawable that will be rendered
     */
    public ClickableDrawableSpan(@NonNull Context context, @NonNull Uri uri) {
        this(context, uri, ALIGN_BOTTOM);
    }

    /**
     * Constructs an {@link ImageSpan} from a {@link Context}, a {@link Uri} and a vertical
     * alignment. The Uri source can be retrieved via
     *
     * @param context           context used to create a drawable from {@param bitmap} based on
     *                          the display
     *                          metrics of the resources
     * @param uri               {@link Uri} used to construct the drawable that will be rendered.
     * @param verticalAlignment one of {@link DynamicDrawableSpan#ALIGN_BOTTOM} or
     *                          {@link DynamicDrawableSpan#ALIGN_BASELINE}
     */
    public ClickableDrawableSpan(@NonNull Context context, @NonNull Uri uri, int verticalAlignment) {
        this.context = context;
        this.contentUri = uri;
        this.verticalAlignment =verticalAlignment;
        this.gestureDetector=new GestureDetector(context,gestureListener);
    }

    /**
     * Constructs an {@link ImageSpan} from a {@link Context} and a resource id with the default
     * alignment {@link DynamicDrawableSpan#ALIGN_BOTTOM}
     *
     * @param context    context used to retrieve the drawable from resources
     * @param resourceId drawable resource id based on which the drawable is retrieved
     */
    public ClickableDrawableSpan(@NonNull Context context, @DrawableRes int resourceId) {
        this(context, resourceId, ALIGN_BOTTOM);
    }

    /**
     * Constructs an {@link ImageSpan} from a {@link Context}, a resource id and a vertical
     * alignment.
     *
     * @param context           context used to retrieve the drawable from resources
     * @param resourceId        drawable resource id based on which the drawable is retrieved.
     * @param verticalAlignment one of {@link DynamicDrawableSpan#ALIGN_BOTTOM} or
     *                          {@link DynamicDrawableSpan#ALIGN_BASELINE}
     */
    public ClickableDrawableSpan(@NonNull Context context, @DrawableRes int resourceId,
                              int verticalAlignment) {
        this.context = context;
        this.resourceId = resourceId;
        this.verticalAlignment =verticalAlignment;
        this.gestureDetector=new GestureDetector(context,gestureListener);
    }
    /**
     * Returns the vertical alignment of this span, one of {@link #ALIGN_BOTTOM} or
     * {@link #ALIGN_BASELINE}.
     */
    public int getVerticalAlignment() {
        return verticalAlignment;
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

    protected void setDrawable(@Nullable Drawable mDrawable) {
        this.drawable = mDrawable;
    }

    protected void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);
        Drawable drawable = getDrawable();
        if(pressed){
            privateFlags|= FLAG_PRESSED;
            drawable.setState(new int[]{android.R.attr.state_pressed});
        } else {
            privateFlags^= FLAG_PRESSED;
            drawable.setState(new int[]{-android.R.attr.state_pressed});
        }
        if(drawable.isStateful()){
            drawable.jumpToCurrentState();
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        Drawable drawable = getDrawable();
        if(enabled){
            privateFlags|= FLAG_ENABLED;
            drawable.setState(new int[]{android.R.attr.state_enabled});
        } else {
            privateFlags^= FLAG_ENABLED;
            drawable.setState(new int[]{-android.R.attr.state_enabled});
        }
        if(drawable.isStateful()){
            drawable.jumpToCurrentState();
        }
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        Drawable drawable = getDrawable();
        if(checked){
            privateFlags|= FLAG_CHECKED;
            drawable.setState(new int[]{android.R.attr.state_checked});
        } else {
            privateFlags^= FLAG_CHECKED;
            drawable.setState(new int[]{-android.R.attr.state_checked});
        }
        if(drawable.isStateful()){
            drawable.jumpToCurrentState();
        }
    }

    @Override
    public void setActivated(boolean activated) {
        super.setActivated(activated);
        Drawable drawable = getDrawable();
        if(activated){
            privateFlags|= FLAG_ACTIVATED;
            drawable.setState(new int[]{android.R.attr.state_activated});
        } else {
            privateFlags^= FLAG_ACTIVATED;
            drawable.setState(new int[]{-android.R.attr.state_activated});
        }
        if(drawable.isStateful()){
            drawable.jumpToCurrentState();
        }
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        Drawable drawable = getDrawable();
        if(selected){
            privateFlags|= FLAG_SELECTED;
            drawable.setState(new int[]{android.R.attr.state_selected});
        } else {
            privateFlags^= FLAG_SELECTED;
            drawable.setState(new int[]{-android.R.attr.state_selected});
        }
        if(drawable.isStateful()){
            drawable.jumpToCurrentState();
        }
    }

    @Override
    public boolean isPressed() {
        return FLAG_PRESSED == (privateFlags & FLAG_PRESSED);
    }

    @Override
    public boolean isEnabled() {
        return FLAG_ENABLED == (privateFlags & FLAG_ENABLED);
    }

    @Override
    public boolean isChecked() {
        return FLAG_CHECKED == (privateFlags & FLAG_CHECKED);
    }

    @Override
    public boolean isActivated() {
        return FLAG_ACTIVATED == (privateFlags & FLAG_ACTIVATED);
    }

    @Override
    public boolean isSelected() {
        return FLAG_SELECTED == (privateFlags & FLAG_SELECTED);
    }

    /**
     * Your subclass must implement this method to provide the bitmap
     * to be drawn.  The dimensions of the bitmap must be the same
     * from each call to the next.
     */
    public Drawable getDrawable(){
        Drawable drawable = null;
        if (this.drawable != null) {
            drawable = this.drawable;
        } else if (contentUri != null) {
            Bitmap bitmap;
            try {
                InputStream is = context.getContentResolver().openInputStream(
                        contentUri);
                bitmap = BitmapFactory.decodeStream(is);
                drawable = new BitmapDrawable(context.getResources(), bitmap);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight());
                this.drawable =drawable;
                is.close();
            } catch (Exception e) {
                Log.e("ImageSpan", "Failed to loaded content " + contentUri, e);
            }
        } else {
            try {
                drawable = context.getResources().getDrawable(resourceId);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight());
                this.drawable =drawable;
            } catch (Exception e) {
                Log.e("ImageSpan", "Unable to find resource: " + resourceId);
            }
        }

        return drawable;
    }

    /**
     * Setting the drawable padding dimension.
     * @param padding
     */
    public void setPadding(int padding){
        setPadding(padding,padding,padding,padding);
    }

    /**
     * Setting the drawable padding dimension.
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    public void setPadding(int left,int top,int right,int bottom){
        this.padding[0]=left;
        this.padding[1]=top;
        this.padding[2]=right;
        this.padding[3]=bottom;
    }
    /**
     * The left padding of the ReplacementSpan
     * @return
     */
    public int getLeftPadding(){
        return padding[0];
    }

    /**
     * The top padding of the ReplacementSpan
     * @return
     */
    public int getTopPadding(){
        return padding[1];
    }

    /**
     * The right padding of the ReplacementSpan
     * @return
     */
    public int getRightPadding(){
        return padding[2];
    }

    /**
     * The bottom padding of the ReplacementSpan
     * @return
     */
    public int getBottomPadding(){
        return padding[3];
    }

    /**
     * The intrinsic width of the ReplacementSpan
     * @return
     */
    public int getIntrinsicWidth(){
        return getLeftPadding()+bounds.width()+getRightPadding();
    }

    /**
     * The intrinsic height of the ReplacementSpan
     * @return
     */
    public int getIntrinsicHeight(){
        return getTopPadding()+bounds.height()+getBottomPadding();
    }

    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
        Drawable drawable = getDrawable();
        if(null!=drawable){
            Rect bounds = getBounds();
            Rect drawableBounds = drawable.getBounds();
            if(bounds.width()!=drawableBounds.width()&&bounds.height()!=drawableBounds.height()){
                bounds.set(drawableBounds);
            }
            if (null!=fm) {
                Rect intrinsicRect = getIntrinsicRect(paint);
                fm.top=intrinsicRect.top;
                fm.ascent=intrinsicRect.top;
                fm.descent=intrinsicRect.bottom;
                fm.bottom=intrinsicRect.bottom;
            }
        }
        return getIntrinsicWidth();
    }

    /**
     * Return the intrinsic rect of the drawable.
     * The rect align by the different alignment.
     * @see #ALIGN_TOP
     * @see #ALIGN_CENTER
     * @see #ALIGN_BASELINE
     * @see #ALIGN_BOTTOM
     *
     * @param paint
     */
    protected Rect getIntrinsicRect(@Nullable Paint paint) {
        Rect rect = tmpRect.get();
        if(null==rect){
            rect = new Rect();
            tmpRect.set(rect);
        }
        Rect bounds = getBounds();
        int drawableHeight = bounds.height();
        int intrinsicWidth = getIntrinsicWidth();
        int intrinsicHeight = getIntrinsicHeight();
        int topPadding=getTopPadding();
        int bottomPadding=getBottomPadding();
        Paint.FontMetricsInt fontMetricsInt = paint.getFontMetricsInt();
        //The vertical alignment.
        int verticalAlignment = getVerticalAlignment();
        //We layout the drawable by the alignment.
        if(verticalAlignment==ALIGN_TOP){
            rect.set(0,fontMetricsInt.ascent,intrinsicWidth,fontMetricsInt.ascent+intrinsicHeight);
        } else if(verticalAlignment==ALIGN_BOTTOM){
            rect.set(0,-intrinsicHeight+fontMetricsInt.descent,intrinsicWidth,fontMetricsInt.descent);
        } else if(verticalAlignment==ALIGN_BASELINE){
            rect.set(0,-intrinsicHeight,intrinsicWidth,0);
        } else if(verticalAlignment==ALIGN_CENTER){
            int fontHeight = fontMetricsInt.bottom - fontMetricsInt.top;
            int top = fontMetricsInt.top+(fontHeight-drawableHeight)/2;
            int bottom = top+drawableHeight;
            rect.set(0,top-topPadding,intrinsicWidth,bottom+bottomPadding);
        }
        return rect;
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        Rect intrinsicRect = getIntrinsicRect(paint);
        int offsetLeft = (int) (x + intrinsicRect.left + getLeftPadding());
        int offsetTop = y + intrinsicRect.top + getTopPadding();
        bounds.offsetTo(offsetLeft,offsetTop);
    }

    /**
     * Makes the text underlined and in the link color.
     */
    @Override
    public void updateDrawState(@NonNull TextPaint ds) {
        ds.setColor(ds.linkColor);
        ds.setUnderlineText(true);
    }

    @Override
    public void onTouchEvent(TextView textView, MotionEvent event) {
        int paddingLeft = textView.getPaddingLeft();
        int paddingTop = textView.getPaddingTop();
        event.offsetLocation(-paddingLeft,-paddingTop);
        this.gestureListener.setTextView(textView);
        this.gestureDetector.onTouchEvent(event);
        int action = event.getActionMasked();
        if(action==MotionEvent.ACTION_DOWN){
            int x= (int) event.getX();
            int y= (int) event.getY();
            Rect bounds = getBounds();
            //Here we are setting the pressed state.
            if(isEnabled()&&bounds.contains(x,y)){
                setPressed(true);
                textView.invalidate();
            }
        } else if(action==MotionEvent.ACTION_UP||
                action==MotionEvent.ACTION_CANCEL||
                action==MotionEvent.ACTION_POINTER_UP){
            //Here we release the pressed state.
            if(isPressed()){
                setPressed(false);
                textView.invalidate();
            }
        }
    }

    /**
     * The clickable gesture listener.
     * We use this this listener capture the click event.
     * @see ClickableDrawableGestureListener#onSingleTapUp(MotionEvent)
     * @see ClickableDrawableGestureListener#onLongPress(MotionEvent)
     */
    private class ClickableDrawableGestureListener extends GestureDetector.SimpleOnGestureListener{
        private TextView textView;

        public void setTextView(TextView textView) {
            this.textView = textView;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            int x= (int) e.getX();
            int y= (int) e.getY();
            Rect bounds = getBounds();
            if(bounds.contains(x,y)){
                performClick(textView);
                return true;
            }
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            int x= (int) e.getX();
            int y= (int) e.getY();
            Rect bounds = getBounds();
            if(bounds.contains(x,y)){
                performLongClick(textView);
            }
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }
}
