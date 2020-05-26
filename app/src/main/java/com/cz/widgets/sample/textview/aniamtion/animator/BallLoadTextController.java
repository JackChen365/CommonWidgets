package com.cz.widgets.sample.textview.aniamtion.animator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;

import com.cz.widgets.sample.textview.aniamtion.animator.drawable.BallDrawable;
import com.cz.widgets.textview.span.animation.AnimationTextSpan;
import com.cz.widgets.textview.span.animation.controller.AbsTextController;

import java.util.List;


/**
 * Created by cz
 * @date 2020-04-27 22:39
 * @email bingo110@126.com
 * A ball loading text controller
 *
 * @see BallDrawable
 */
public class BallLoadTextController extends AbsTextController {
    private AnimatorSet animatorSet;
    private final BallDrawable ballDrawable=new BallDrawable();

    public BallLoadTextController() {
        ballDrawable.setColor(Color.WHITE);
        ballDrawable.setBounds(0,0,12,12);
    }

    @Override
    protected void startAnimator(List<AnimationTextSpan> animationTextSpanList) {
        animatorSet=new AnimatorSet();
        AnimationTextSpan lastElement=null;
        Animator lastAnimator=null;
        for(AnimationTextSpan animationTextSpan:animationTextSpanList){
            RectF bounds = animationTextSpan.getBounds();
            float offsetX=null==lastElement?bounds.left-bounds.width()/2:lastElement.getBounds().centerX();
            RectF rectF = new RectF(offsetX, bounds.top - bounds.height()/2, bounds.centerX(), bounds.top + bounds.height()/2);
            ArcAnimator arcAnimator = getArcAnimator(rectF);
            ValueAnimator animator1= ObjectAnimator.ofFloat(animationTextSpan,"y",bounds.top,bounds.top+10);
            ValueAnimator animator2= ObjectAnimator.ofFloat(animationTextSpan,"y",bounds.top+10, bounds.top);
            animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    invalidate();
                }
            });
            animatorSet.playSequentially(arcAnimator,animator1,animator2);
            if(null!=lastAnimator){
                animatorSet.playSequentially(lastAnimator,arcAnimator);
            }
            lastElement=animationTextSpan;
            lastAnimator=arcAnimator;
        }
        if(!animationTextSpanList.isEmpty()){
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    animatorSet.start();
                }
            });
            animatorSet.start();
        }
    }

    @Override
    public void cancel() {
        super.cancel();
        if(null!=animatorSet){
            animatorSet.cancel();
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        ballDrawable.draw(canvas);
    }

    private ArcAnimator getArcAnimator(final RectF rectF) {
        final ArcAnimator arcAnimator = new ArcAnimator(rectF, 180f, 360f);
        arcAnimator.setDuration(600);
        arcAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ballDrawable.setX((int) (arcAnimator.getX() - ballDrawable.getBounds().width() / 2));
                ballDrawable.setY((int) arcAnimator.getY());
                invalidate();
            }
        });
        return arcAnimator;
    }

    public class ArcAnimator extends ValueAnimator {
        private final float start,end;
        private Path path= new Path();
        private PathMeasure pathMeasure=new PathMeasure();
        private float[] pos = new float[2];
        private float[] tan = new float[2];

        public ArcAnimator(RectF rect, float start, float end) {
            this.start = start;
            this.end = end;
            setFloatValues(1f);
            addUpdateListener(new AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    setFraction(animation.getAnimatedFraction());
                }
            });
            path.addOval(rect,Path.Direction.CW);
            pathMeasure.setPath(path,false);
        }

        public float getX() {
            return pos[0];
        }

        public float getY() {
            return pos[1];
        }

        public void setFraction(float fraction) {
            float startFraction=start/360f;
            float endFraction=end/360f;
            float fractionValue=startFraction+(endFraction-startFraction)*fraction;
            float distance=pathMeasure.getLength() * fractionValue;
            pathMeasure.getPosTan(distance,pos,tan);
        }
    }
}
