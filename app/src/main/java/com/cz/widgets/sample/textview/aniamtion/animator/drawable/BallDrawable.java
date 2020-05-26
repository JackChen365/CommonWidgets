package com.cz.widgets.sample.textview.aniamtion.animator.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by cz
 * @date 2020-04-24 23:39
 * @email bingo110@126.com
 * A ball drawable.
 */
public class BallDrawable extends Drawable {
    private final Paint paint= new Paint(Paint.ANTI_ALIAS_FLAG);

    public void setColor(int color){
        paint.setColor(color);
    }

    public void setX(int x) {
        Rect bounds = getBounds();
        bounds.set(x,bounds.top,x+bounds.width(),bounds.top+bounds.height());
    }

    public void setY(int y) {
        Rect bounds = getBounds();
        bounds.set(bounds.left,y,bounds.left+bounds.width(),y+bounds.height());
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.save();
        Rect bounds = getBounds();
        canvas.translate(bounds.left,bounds.top);
        canvas.drawCircle(bounds.width()/2f,bounds.height()/2f,Math.min(bounds.width(),bounds.height())/2f,paint);
        canvas.restore();
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }

    @Override
    public void invalidateSelf() {
        //nothing
    }
}
