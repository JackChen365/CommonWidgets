package com.cz.widgets.zoomlayout.tree;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;

import com.cz.widgets.zoomlayout.R;
import com.cz.widgets.zoomlayout.ZoomLayout;

/**
 * @author Created by cz
 * @date 2020-05-11 23:13
 * @email bingo110@126.com
 */
public class PreViewLayout extends SurfaceView implements SurfaceHolder.Callback{
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private RenderThread renderThread;
    private Bitmap previewBitmap;
    private Drawable background;
    private int scrollX;
    private int scrollY;

    public PreViewLayout(Context context) {
        this(context,null,R.attr.previewLayout);
    }

    public PreViewLayout(Context context, AttributeSet attrs) {
        this(context,attrs, R.attr.previewLayout);
    }

    public PreViewLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.paint.setStyle(Paint.Style.STROKE);

        setWillNotDraw(false);
        SurfaceHolder holder = getHolder();
        setZOrderOnTop(true);
        holder.setFormat(PixelFormat.TRANSLUCENT);
        holder.addCallback(this);


        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PreViewLayout, defStyleAttr, R.style.PreviewLayout);
        setColor(a.getColor(R.styleable.PreViewLayout_preview_color, Color.TRANSPARENT));
        setStrokeWidth(a.getDimensionPixelOffset(R.styleable.PreViewLayout_preview_strokeWidth,0));
        a.recycle();
    }

    private void setColor(int color) {
        paint.setColor(color);
    }

    private void setStrokeWidth(int strokeWidth) {
        paint.setStrokeWidth(strokeWidth);
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
        this.background = background;
    }

    @Override
    public void setBackground(Drawable background) {
        this.background = background;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (background != null)
            background.setBounds(left, top, right, bottom);
    }

    public void attachToHostView(final ZoomLayout zoomLayout){
        if(null!=renderThread){
            throw new IllegalArgumentException("The render thread is already started!");
        }
        if(!(zoomLayout instanceof Previewable)){
            throw new NullPointerException("The layout is not a preview sub-class, Please use the interface:Previewable!");
        }
        final Previewable previewable = (Previewable) zoomLayout;
        final View preview = previewable.newPreview();
        if(null==preview){
            throw new NullPointerException("The preview is null. Please override the method:newPreView in your layout.");
        }
        zoomLayout.setOnLayoutChildDrawableStateChanged(new ZoomLayout.OnLayoutChildDrawableStateChanged() {
            @Override
            public void childDrawableStateChanged(View child,float scaleX,float scaleY) {
                if(null!=previewBitmap){
                    SurfaceHolder holder = getHolder();
                    if(null!=holder){
                        //Update the bitmap
                        Canvas canvas = new Canvas(previewBitmap);
                        canvas.save();
                        int measuredWidth = preview.getMeasuredWidth();
                        final int width = getWidth();
                        float scale=width*1f/measuredWidth;
                        canvas.scale(scale,scale);
                        previewable.onChildChange(canvas,child);
                        canvas.restore();

                        Canvas lockCanvas=null;
                        try {
                            //Draw the cached bitmap.
                            lockCanvas = holder.lockCanvas();
                            if(null!=lockCanvas){
                                lockCanvas.drawBitmap(previewBitmap,0,0,null);
                                drawScrollRect(lockCanvas,zoomLayout,preview,scaleX, scaleY);
                            }
                        } finally {
                            if (null!=lockCanvas) {
                                holder.unlockCanvasAndPost(lockCanvas);
                            }
                        }
                    }
                }
            }
        });
        zoomLayout.setOnLayoutScrollChangeListener(new ZoomLayout.OnLayoutScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int dx, int dy,float scaleX,float scaleY) {
                scrollX += dx;
                scrollY += dy;
                int measuredWidth = preview.getMeasuredWidth();
                int measuredHeight = preview.getMeasuredHeight();
                if(0!=measuredWidth&&0!=measuredHeight){
                    SurfaceHolder holder = getHolder();
                    Canvas canvas = null;
                    try {
                        //Draw the cached bitmap.
                        canvas = holder.lockCanvas();
                        if(null!=canvas){
                            canvas.drawBitmap(previewBitmap,0,0,null);
                            drawScrollRect(canvas,zoomLayout,preview,scaleX, scaleY);
                        }
                    } finally {
                        if (null!=canvas) {
                            holder.unlockCanvasAndPost(canvas);
                        }
                    }
                }
            }
        });
        this.renderThread=new RenderThread(zoomLayout,preview);
    }

    private void drawScrollRect(Canvas canvas, ZoomLayout zoomLayout,View preview,float scaleX, float scaleY) {
        //Draw the scroll rectangle.
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        float measuredWidth = preview.getMeasuredWidth();
        float measuredHeight = preview.getMeasuredHeight();
        float matrixScaleX = (width / measuredWidth)/scaleX;
        float matrixScaleY = (height /measuredHeight)/scaleY;
        int layoutWidth = zoomLayout.getWidth();
        int layoutHeight = zoomLayout.getHeight();
        float left = scrollX*scaleX;
        float top = scrollY*scaleY;
        float right = left+layoutWidth;
        float bottom = top+layoutHeight;
        canvas.drawRect(left*matrixScaleX,top*matrixScaleY, right*matrixScaleX, bottom*matrixScaleY,paint);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if(null!=renderThread&&!renderThread.isAlive()){
            renderThread.setPriority(Thread.MIN_PRIORITY);
            renderThread.start();
        }
//        renderThread.updatePreView();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        renderThread.quit();
    }

    @Override
    protected void onDetachedFromWindow() {
        renderThread.quit();
        if(null!=previewBitmap&&!previewBitmap.isRecycled()){
            previewBitmap.recycle();
            previewBitmap=null;
        }
        super.onDetachedFromWindow();
    }

    public class RenderThread extends Thread{
        private final Object LOCK=new Object();
        private volatile boolean isRunning=false;
        private ZoomLayout zoomLayout;
        private View preview;
        public RenderThread(@NonNull ZoomLayout zoomLayout,@NonNull View preview) {
            super("RenderThread");
            this.zoomLayout=zoomLayout;
            this.preview = preview;
        }

        @Override
        public synchronized void start() {
            isRunning=true;
            super.start();
        }

        @Override
        public void run() {
            super.run();
            while(isRunning){
                //Drawing the preview on this canvas.
                synchronized (LOCK) {
                    preview.measure(View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED),
                            View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED));
                    int measuredWidth = preview.getMeasuredWidth();
                    int measuredHeight = preview.getMeasuredHeight();
                    preview.layout(0,0,measuredWidth,measuredHeight);

                    final int width = getWidth();
                    float scale=width*1f/measuredWidth;
                    final int height = (int) (scale*measuredHeight);
                    setMeasuredDimension(width,height);
                    post(new Runnable() {
                        @Override
                        public void run() {
                            SurfaceHolder holder = getHolder();
                            holder.setFixedSize(width,height);
                        }
                    });
                    previewBitmap=Bitmap.createBitmap(width,height, Bitmap.Config.RGB_565);
                    Canvas canvas = new Canvas(previewBitmap);
                    //Draw the background
                    if (null!=background){
                        background.draw(canvas);
                    }
                    canvas.save();
                    canvas.scale(scale,scale);
                    //Draw the preview.
                    preview.draw(canvas);
                    canvas.restore();
                }
                SurfaceHolder holder = getHolder();
                Canvas canvas = null;
                try {
                    canvas = holder.lockCanvas();
                    if(null!=canvas&&null!=previewBitmap){
                        canvas.drawBitmap(previewBitmap, 0, 0, null);
                        drawScrollRect(canvas, zoomLayout,preview,1f, 1f);
                    }
                } finally {
                    if(null!=canvas){
                        holder.unlockCanvasAndPost(canvas);
                    }
                }
                synchronized (LOCK) {
                    try {
                        LOCK.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        /**
         * notify the thread to update the preview again.
         */
        void updatePreView(){
            synchronized (LOCK){
                LOCK.notify();
            }
        }

        public void quit() {
            synchronized (LOCK){
                isRunning=false;
                LOCK.notify();
            }
        }
    }
}
