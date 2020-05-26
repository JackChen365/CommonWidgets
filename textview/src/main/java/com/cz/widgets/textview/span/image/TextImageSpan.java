package com.cz.widgets.textview.span.image;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.FloatRange;
import androidx.annotation.IntDef;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;

/**
 * @author Created by cz
 * @date 2019-05-14 09:25
 * @email bingo110@126.com
 * The image span object support drawing text.
 */
public class TextImageSpan extends ClickableDrawableSpan {
    private final Context context;
    private final TextPaint textPaint=new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private String text;

    public TextImageSpan(Builder builder){
        super(builder.context, builder.drawableRes,builder.alignment);
        this.context=builder.context;
        if(null!=builder.drawable){
            setDrawable(builder.drawable);
        } else if(0!=builder.drawableRes){
            setResourceId(builder.drawableRes);
        }
        setText(builder.text);
        setTextStyle(builder.textStyle);
        setTextSize(builder.textSize);
        setTextColor(builder.textColor);
        setDrawableSize(builder.drawableWidth,builder.drawableHeight);
        setPadding(builder.padding[0],builder.padding[1],builder.padding[2],builder.padding[3]);
        setOnClickListener(builder.listener);
    }


    public TextImageSpan(@NonNull Context context, int resourceId) {
        this(context, resourceId,ALIGN_BOTTOM);
    }

    public TextImageSpan(@NonNull Context context, int resourceId, int verticalAlignment) {
        super(context, resourceId, verticalAlignment);
        this.context=context;
        Drawable drawable = getDrawable();
        setBounds(drawable.getBounds());
        TypedArray typedArray = context.obtainStyledAttributes(new int[]{android.R.attr.colorAccent});
        textPaint.setColor(typedArray.getColor(0, Color.WHITE));
        typedArray.recycle();
    }

    /**
     * Setting the text by resource id
     * @param stringRes
     */
    public void setText(@StringRes int stringRes){
        this.text=context.getString(stringRes);
    }

    /**
     * Setting the drawable dimension.
     * @param width 图片宽
     * @param height 图片高
     */
    public void setDrawableSize(int width,int height){
        setBounds(0,0,width,height);
    }

    /**
     * Setting the text.
     * @param text
     */
    public void setText(String text){
        this.text=text;
    }

    /**
     * Setting the text size by default unit {@link TypedValue#COMPLEX_UNIT_SP}
     * @param textSize
     */
    public void setTextSize(float textSize){
        float size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSize, context.getResources().getDisplayMetrics());
        this.textPaint.setTextSize(size);
    }

    /**
     * Setting the text size
     * @param textSize
     */
    public void setTextSize(int typedValue,float textSize){
        float size = TypedValue.applyDimension(typedValue, textSize, context.getResources().getDisplayMetrics());
        this.textPaint.setTextSize(size);
    }

    /**
     * Setting the text color
     * @param color
     */
    public void setTextColor(@ColorInt int color){
        this.textPaint.setColor(color);
    }

    /**
     * Setting the text style.
     * @see Typeface#NORMAL
     * @see Typeface#BOLD
     * @see Typeface#ITALIC
     * @see Typeface#BOLD_ITALIC
     */
    public void setTextStyle(int typeface){
        Typeface font= Typeface.DEFAULT;
        if(typeface== Typeface.BOLD){
            font = Typeface.create(Typeface.SANS_SERIF, typeface);
        } else if(typeface== Typeface.ITALIC){
            font = Typeface.create(Typeface.SANS_SERIF, typeface);
        } else if(typeface== Typeface.BOLD_ITALIC){
            font = Typeface.create(Typeface.SANS_SERIF, typeface);
        }
        this.textPaint.setTypeface(font);
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        super.draw(canvas,text,start,end,x,top,y,bottom,paint);
        final TextPaint textPaint= (TextPaint) paint;
        //Drawing the drawable.
        drawDrawable(canvas, x, top,y, bottom,textPaint);
        //Drawing the text.
        drawText(canvas, x, top,y, bottom,textPaint);
    }

    /**
     * Drawing the drawable.
     * @param canvas
     * @param x
     * @param top
     * @param bottom
     */
    private void drawDrawable(Canvas canvas, float x, int top, int y, int bottom, Paint paint) {
        Rect intrinsicRect = getIntrinsicRect(paint);
        Drawable drawable = getDrawable();
        Rect bounds = getBounds();
        drawable.setBounds(0,0,bounds.width(),bounds.height());
        canvas.save();
        canvas.translate(x+intrinsicRect.left+getLeftPadding(),y+intrinsicRect.top+getTopPadding());
        drawable.draw(canvas);
        canvas.restore();
    }

    /**
     * Drawing the text.
     * @param canvas
     */
    private void drawText(Canvas canvas, float x, int top, int y, int bottom, Paint paint) {
        Rect intrinsicRect = getIntrinsicRect(paint);
        final String text=this.text;
        if(!TextUtils.isEmpty(text)){
            Rect bounds = getBounds();
            int leftPadding = getLeftPadding();
            int topPadding = getTopPadding();
            final float textWidth = textPaint.measureText(text);//文字宽
            final float centerY = (bounds.height() - (textPaint.descent() + textPaint.ascent())) / 2;
            canvas.drawText(text , x+leftPadding+(bounds.width() - textWidth) / 2, y+topPadding+intrinsicRect.top+centerY, textPaint);
        }
    }

    public static class Builder{
        final Context context;
        @DrawableRes
        int drawableRes;
        Drawable drawable;
        int alignment=ALIGN_CENTER;
        int textAppearance;
        int textColor;
        float textSize;
        String text;
        int textStyle;
        int[] padding=new int[4];
        int drawableWidth;
        int drawableHeight;
        View.OnClickListener listener;

        @IntDef({Typeface.NORMAL, Typeface.BOLD, Typeface.ITALIC, Typeface.BOLD_ITALIC})
        public @interface TextStyle{
        }

        public Builder(@NonNull Context context) {
            this.context = context;
        }

        /**
         * Setting the drawable by resource id.
         * @param drawableRes
         * @return
         */
        public Builder drawable(@DrawableRes int drawableRes) {
            this.drawableRes = drawableRes;
            return this;
        }

        /**
         * Setting the drawable.
         * @param drawable
         * @return
         */
        public Builder drawable(Drawable drawable) {
            this.drawable = drawable;
            return this;
        }

        /**
         * Setting the text alignment.
         * @see ClickableDrawableSpan#ALIGN_TOP
         * @see ClickableDrawableSpan#ALIGN_CENTER
         * @see ClickableDrawableSpan#ALIGN_BASELINE
         * @see ClickableDrawableSpan#ALIGN_BOTTOM
         * @param alignment
         * @return
         */
        public Builder alignment(@DrawableAlign int alignment) {
            this.alignment = alignment;
            return this;
        }

        /**
         * Setting the text dimension.
         * @param textSize
         */
        public Builder textSize(@FloatRange(from = 0) float textSize) {
            this.textSize = textSize;
            return this;
        }

        /**
         * Setting the text.
         * @param color
         */
        public Builder textColor(@ColorInt int color) {
            this.textColor = textColor;
            return this;
        }

        /**
         * Setting the text resources.
         * @param stringRes
         * @return
         */
        public Builder text(@StringRes int stringRes) {
            this.text = context.getString(stringRes);
            return this;
        }

        /**
         * Setting the text
         * @param text
         */
        public Builder text(@NonNull String text) {
            this.text = text;
            return this;
        }

        /**
         * Setting the text appearance
         * @param textAppearance
         * @return
         */
        public Builder textAppearance(@StyleRes int textAppearance){
            this.textAppearance =textAppearance;
            return this;
        }

        /**
         * Setting the text style.
         * @see Typeface#NORMAL
         * @see Typeface#BOLD
         * @see Typeface#ITALIC
         * @see Typeface#BOLD_ITALIC
         */
        public Builder textStyle(@TextStyle int textStyle) {
            this.textStyle = textStyle;
            return this;
        }

        /**
         * Setting the padding and apply in all the directions.
         * @param padding
         */
        public Builder padding(@IntRange(from=0,to=300) int padding){
            padding(padding,padding,padding,padding);
            return this;
        }

        /**
         * Setting the padding.
         * @param left 左边距
         * @param top 上边距
         * @param right 右边距
         * @param bottom 底部边距
         */
        public Builder padding(@IntRange(from=0,to=300)int left, @IntRange(from=0,to=300)int top, @IntRange(from=0,to=300)int right, @IntRange(from=0,to=300)int bottom){
            this.padding[0]=left;
            this.padding[1]=top;
            this.padding[2]=right;
            this.padding[3]=bottom;
            return this;
        }

        /**
         * Setting the drawable size.
         * @param drawableWidth
         * @param drawableHeight
         * @return
         */
        public Builder drawableSize(@IntRange(from = 0) int drawableWidth, @IntRange(from = 0)int drawableHeight) {
            this.drawableWidth = drawableWidth;
            this.drawableHeight = drawableHeight;
            return this;
        }

        /**
         * Setting the click listener.
         * @param listener
         */
        public Builder click(@NonNull View.OnClickListener listener) {
            this.listener = listener;
            return this;
        }

        public TextImageSpan build(){
            return new TextImageSpan(this);
        }
    }
}
