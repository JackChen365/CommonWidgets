package com.cz.widgets.sample.textview.other;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ReplacementSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cz.widgets.textview.span.animation.controller.AbsTextController;
import com.cz.widgets.textview.span.animation.AnimationTextSpan;
import com.cz.widgets.sample.R;

import java.text.DecimalFormat;
import java.util.List;

/**
 * @author Created by cz
 * @date 2020-04-20 23:20
 * @email bingo110@126.com
 */
public class CountDownTextController extends AbsTextController {
    private static final String DEFAULT_VALUE ="00";
    public final int MINUTES = 60 * 1000;
    public final int HOUR = 60 * MINUTES;
    public final int DAY = 24 * HOUR;
    private DecimalFormat decimalFormatter;
    private OnCountDownListener listener;
    private CountDownTimer countDownTimer;
    private Drawable numberBackground;
    private Drawable separateBackground;

    private int timeMillis;

    public CountDownTextController(Context context) {
        this(context,null, R.attr.countDownTextView);
    }

    public CountDownTextController(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.countDownTextView);
    }

    public CountDownTextController(Context context, AttributeSet attrs, int defStyleAttr) {
        this.decimalFormatter=new DecimalFormat("00");
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CountDownTextView,defStyleAttr,R.style.CountDownTextView);
        setNumberBackground(a.getDrawable(R.styleable.CountDownTextView_countdown_numberBackground));
        setSeparateBackground(a.getDrawable(R.styleable.CountDownTextView_countdown_separateBackground));
        setTimeMillis(a.getInteger(R.styleable.CountDownTextView_countdown_time,0));
        a.recycle();
    }

    private void setNumberBackground(Drawable drawable) {
        this.numberBackground=drawable;
    }

    private void setSeparateBackground(Drawable drawable) {
        this.separateBackground =drawable;
    }

    public void setTimeMillis(int timeMillis) {
        this.timeMillis = timeMillis;
    }

    public void start(){
        if(null!=countDownTimer){
            throw new IllegalStateException("Count down timer is running!");
        }
        countDownTimer = new CountDownTimer(timeMillis, 1 * 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int hour = (int) (millisUntilFinished / HOUR);
                int minute = (int) ((millisUntilFinished - hour * HOUR) / MINUTES);
                int second = (int) (millisUntilFinished / 1000 % 60);
                TextView textView = getTextView();

                String hourText = decimalFormatter.format(hour);
                String minuteText = decimalFormatter.format(minute);
                String secondText = decimalFormatter.format(second);

                SpannableStringBuilder spannableString=new SpannableStringBuilder();
                int index=0;
                spannableString.append(hourText);
                NumberTextSpan numberTextSpan = new NumberTextSpan(textView, spannableString, index, index + hourText.length());
                spannableString.setSpan(numberTextSpan,index,index+hourText.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                index+=hourText.length();

                ColonImageSpan drawableSpan = new ColonImageSpan(separateBackground);
                spannableString.append(":");
                spannableString.setSpan(drawableSpan,index,index+1,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                index+=1;

                spannableString.append(minuteText);
                numberTextSpan = new NumberTextSpan(textView, spannableString, index, index + minuteText.length());
                spannableString.setSpan(numberTextSpan,index,index+minuteText.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                index+=minuteText.length();

                spannableString.append(":");
                drawableSpan = new ColonImageSpan(separateBackground);
                spannableString.setSpan(drawableSpan,index,index+1,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                index+=1;

                spannableString.append(secondText);
                numberTextSpan=new NumberTextSpan(textView,spannableString,index,index+secondText.length());
                spannableString.setSpan(numberTextSpan,index,index+secondText.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                textView.setText(spannableString);
                if(null!=listener){
                    listener.onTick(timeMillis,millisUntilFinished);
                }
                invalidate();
            }

            @Override
            public void onFinish() {
                if(null!=listener){
                    listener.onFinish(timeMillis);
                }
            }
        };
        countDownTimer.start();
    }

    @Override
    protected void onAttachToView(TextView textView) {
        super.onAttachToView(textView);
        CharSequence text = textView.getText();
        if(!TextUtils.isEmpty(text)){
            throw new IllegalArgumentException("When you use count down text controller, You can not allow set you own text information!");
        }
    }

    @Override
    protected void startAnimator(List<AnimationTextSpan> animationTextSpanList) {
    }

    @Override
    public void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
    }

    public void setOnCountDownListener(OnCountDownListener listener){
        this.listener=listener;
    }

    public interface OnCountDownListener{
        void onTick(long total, long current);
        void onFinish(long total);
    }

    private class NumberTextSpan extends AnimationTextSpan {
        public NumberTextSpan(TextView hostView, CharSequence text, int start, int end) {
            super(hostView, text, start, end);
        }

        @Override
        public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
            if(null!=fm){
                int intrinsicHeight = numberBackground.getIntrinsicHeight();
                fm.descent = fm.top+intrinsicHeight;
                fm.bottom = fm.top+intrinsicHeight;
            }
            return numberBackground.getIntrinsicWidth();
        }

        @Override
        public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float offsetX, int top, int offsetY, int bottom, @NonNull Paint paint) {
            float textOffsetLeft=0;
            int textOffsetTop=0;
            if(null!=numberBackground){
                Rect textBounds = getTextBounds();
                paint.getTextBounds(text.toString(),start,end,textBounds);
                float textWidth=paint.measureText(text,start,end);
                int textHeight=textBounds.height();
                int intrinsicWidth = numberBackground.getIntrinsicWidth();
                int intrinsicHeight = numberBackground.getIntrinsicHeight();

                textOffsetLeft=(intrinsicWidth-textWidth)/2;
                textOffsetTop=(intrinsicHeight+textHeight)/2;

                int offsetLeft= (int) offsetX;
                int offsetTop= offsetY;
                numberBackground.setBounds(offsetLeft, offsetTop, offsetLeft+intrinsicWidth, offsetTop+intrinsicHeight);
                numberBackground.draw(canvas);
            }
            super.draw(canvas,text,start,end,offsetX+textOffsetLeft,top,offsetY+textOffsetTop,bottom,paint);
        }
    }

    private class ColonImageSpan extends ReplacementSpan{
        private Drawable drawable;

        public ColonImageSpan(Drawable drawable) {
            this.drawable = drawable;
        }

        @Override
        public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
            return this.drawable.getIntrinsicWidth();
        }

        @Override
        public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
            if(null!=drawable){
                TextView textView = getTextView();
                int height = textView.getHeight();
                int totalPaddingTop = textView.getTotalPaddingTop();
                int intrinsicHeight = drawable.getIntrinsicHeight();
                drawable.setBounds((int)x,(height-intrinsicHeight)/2-totalPaddingTop, (int)(x+drawable.getIntrinsicWidth()), (height+intrinsicHeight)/2-totalPaddingTop);
                drawable.draw(canvas);
            }
        }
    }
}
