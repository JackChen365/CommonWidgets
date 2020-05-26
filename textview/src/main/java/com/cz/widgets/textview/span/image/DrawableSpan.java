package com.cz.widgets.textview.span.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.annotation.NonNull;

/**
 * @author Created by cz
 * @date 2020-04-20 21:04
 * @email bingo110@126.com
 *
 * The drawable span object.
 */
public class DrawableSpan extends ClickableDrawableSpan {
    public DrawableSpan(@NonNull Context context, @NonNull Bitmap bitmap) {
        super(context, bitmap);
    }

    public DrawableSpan(@NonNull Context context, @NonNull Bitmap bitmap, int verticalAlignment) {
        super(context, bitmap, verticalAlignment);
    }

    public DrawableSpan(Context context,@NonNull Drawable drawable) {
        super(context,drawable);
    }

    public DrawableSpan(Context context,@NonNull Drawable drawable, int verticalAlignment) {
        super(context,drawable, verticalAlignment);
    }

    public DrawableSpan(@NonNull Context context, @NonNull Uri uri) {
        super(context, uri);
    }

    public DrawableSpan(@NonNull Context context, @NonNull Uri uri, int verticalAlignment) {
        super(context, uri, verticalAlignment);
    }

    public DrawableSpan(@NonNull Context context, int resourceId) {
        super(context, resourceId);
    }

    public DrawableSpan(@NonNull Context context, int resourceId, int verticalAlignment) {
        super(context, resourceId, verticalAlignment);
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        super.draw(canvas, text, start, end, x, top, y, bottom, paint);
        drawDrawable(canvas,x,y,paint);
    }

    private void drawDrawable(Canvas canvas, float x, int y, Paint paint) {
        Rect intrinsicRect = getIntrinsicRect(paint);
        Drawable drawable = getDrawable();
        Rect bounds = getBounds();
        drawable.setBounds(bounds);
        canvas.save();
        canvas.translate(x+intrinsicRect.left+getLeftPadding(),y+intrinsicRect.top+getTopPadding());
        drawable.draw(canvas);
        canvas.restore();
    }
}
