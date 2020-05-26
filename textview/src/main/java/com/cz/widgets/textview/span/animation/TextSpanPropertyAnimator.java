package com.cz.widgets.textview.span.animation;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * This class is changing from the {@link android.view.ViewPropertyAnimator}
 * I just change a little code to support text animation.
 *
 * For a text animation here we support the animation repeat mode and repeat count.
 * Because we are not a render node. So we removed a few animation properties such like translationZ
 *
 * All the properties:
 * @see #rotation(float)
 * @see #scaleX(float)
 * @see #scaleXBy(float)
 * @see #scaleY(float)
 * @see #scaleYBy(float)
 * @see #alpha(float)
 * @see #alphaBy(float)
 * @see #translationX(float)
 * @see #translationXBy(float)
 * @see #translationY(float)
 * @see #translationYBy(float)
 *
 * All the animation operations.
 * @see #start()
 * @see #pause()
 * @see #resume()
 * @see #cancel()
 *
 * @see AnimationTextSpan#propertyAnimator()
 */
public class TextSpanPropertyAnimator implements TextAnimator{
    final View view;
    /**
     * The animation text span whose properties are being animated by this class. This is set at
     * construction time.
     */
    final AnimationTextSpan animationTextSpan;

    private ValueAnimator animator;

    /**
     * The duration of the underlying Animator object. By default, we don't set the duration
     * on the Animator and just use its default duration. If the duration is ever set on this
     * Animator, then we use the duration that it was set to.
     */
    private long mDuration;

    /**
     * A flag indicating whether the duration has been set on this object. If not, we don't set
     * the duration on the underlying Animator, but instead just use its default duration.
     */
    private boolean mDurationSet = false;

    /**
     * The startDelay of the underlying Animator object. By default, we don't set the startDelay
     * on the Animator and just use its default startDelay. If the startDelay is ever set on this
     * Animator, then we use the startDelay that it was set to.
     */
    private long mStartDelay = 0;

    /**
     * A flag indicating whether the startDelay has been set on this object. If not, we don't set
     * the startDelay on the underlying Animator, but instead just use its default startDelay.
     */
    private boolean mStartDelaySet = false;

    // The number of times the animation will repeat. The default is 0, which means the animation
    // will play only once
    private int mRepeatCount = 0;

    /**
     * The type of repetition that will occur when repeatMode is nonzero. RESTART means the
     * animation will start from the beginning on every new cycle. REVERSE means the animation
     * will reverse directions on each iteration.
     */
    private int mRepeatMode = ValueAnimator.RESTART;

    /**
     * The interpolator of the underlying Animator object. By default, we don't set the interpolator
     * on the Animator and just use its default interpolator. If the interpolator is ever set on
     * this Animator, then we use the interpolator that it was set to.
     */
    private TimeInterpolator mInterpolator;

    /**
     * A flag indicating whether the interpolator has been set on this object. If not, we don't set
     * the interpolator on the underlying Animator, but instead just use its default interpolator.
     */
    private boolean mInterpolatorSet = false;

    /**
     * Listener for the lifecycle events of the underlying ValueAnimator object.
     */
    private Animator.AnimatorListener mListener = null;

    /**
     * Listener for the update events of the underlying ValueAnimator object.
     */
    private ValueAnimator.AnimatorUpdateListener mUpdateListener = null;

    /**
     * A lazily-created ValueAnimator used in order to get some default animator properties
     * (duration, start delay, interpolator, etc.).
     */
    private ValueAnimator mTempValueAnimator;

    /**
     * This listener is the mechanism by which the underlying Animator causes changes to the
     * properties currently being animated, as well as the cleanup after an animation is
     * complete.
     */
    private AnimatorEventListener mAnimatorEventListener = new AnimatorEventListener();

    /**
     * This list holds the properties that have been asked to propertyAnimator. We allow the caller to
     * request several animations prior to actually starting the underlying animator. This
     * enables us to run one single animator to handle several properties in parallel. Each
     * property is tossed onto the pending list until the animation actually starts (which is
     * done by posting it onto mView), at which time the pending list is cleared and the properties
     * on that list are added to the list of properties associated with that animator.
     */
    ArrayList<NameValuesHolder> mPendingAnimations = new ArrayList<NameValuesHolder>();
    private Runnable mPendingSetupAction;
    private Runnable mPendingCleanupAction;
    private Runnable mPendingOnStartAction;
    private Runnable mPendingOnEndAction;

    /**
     * Constants used to associate a property being requested and the mechanism used to set
     * the property (this class calls directly into View to set the properties in question).
     */
    static final int NONE           = 0x0000;
    static final int TRANSLATION_X  = 0x0001;
    static final int TRANSLATION_Y  = 0x0002;
    static final int SCALE_X        = 0x0004;
    static final int SCALE_Y        = 0x0008;
    static final int ROTATION       = 0x0010;
    static final int X              = 0x0080;
    static final int Y              = 0x0100;
    static final int ALPHA          = 0x0200;

    private static final int TRANSFORM_MASK = TRANSLATION_X | TRANSLATION_Y  |
            SCALE_X | SCALE_Y | ROTATION | X | Y;

    /**
     * The mechanism by which the user can request several properties that are then animated
     * together works by posting this Runnable to start the underlying Animator. Every time
     * a property animation is requested, we cancel any previous postings of the Runnable
     * and re-post it. This means that we will only ever run the Runnable (and thus start the
     * underlying animator) after the caller is done setting the properties that should be
     * animated together.
     */
    private Runnable mAnimationStarter = new Runnable() {
        @Override
        public void run() {
            startAnimation();
        }
    };

    /**
     * This class holds information about the overall animation being run on the set of
     * properties. The mask describes which properties are being animated and the
     * values holder is the list of all property/value objects.
     */
    private static class PropertyBundle {
        int mPropertyMask;
        ArrayList<NameValuesHolder> mNameValuesHolder;

        PropertyBundle(int propertyMask, ArrayList<NameValuesHolder> nameValuesHolder) {
            mPropertyMask = propertyMask;
            mNameValuesHolder = nameValuesHolder;
        }

        /**
         * Removes the given property from being animated as a part of this
         * PropertyBundle. If the property was a part of this bundle, it returns
         * true to indicate that it was, in fact, canceled. This is an indication
         * to the caller that a cancellation actually occurred.
         *
         * @param propertyConstant The property whose cancellation is requested.
         * @return true if the given property is a part of this bundle and if it
         * has therefore been canceled.
         */
        boolean cancel(int propertyConstant) {
            if ((mPropertyMask & propertyConstant) != 0 && mNameValuesHolder != null) {
                int count = mNameValuesHolder.size();
                for (int i = 0; i < count; ++i) {
                    NameValuesHolder nameValuesHolder = mNameValuesHolder.get(i);
                    if (nameValuesHolder.mNameConstant == propertyConstant) {
                        mNameValuesHolder.remove(i);
                        mPropertyMask &= ~propertyConstant;
                        return true;
                    }
                }
            }
            return false;
        }
    }

    /**
     * This list tracks the list of properties being animated by any particular animator.
     * In most situations, there would only ever be one animator running at a time. But it is
     * possible to request some properties to propertyAnimator together, then while those properties
     * are animating, to request some other properties to propertyAnimator together. The way that
     * works is by having this map associate the group of properties being animated with the
     * animator handling the animation. On every update event for an Animator, we ask the
     * map for the associated properties and set them accordingly.
     */
    private HashMap<ValueAnimator, PropertyBundle> mAnimatorMap =
            new HashMap<>();
    private HashMap<Animator, Runnable> mAnimatorSetupMap;
    private HashMap<Animator, Runnable> mAnimatorCleanupMap;
    private HashMap<Animator, Runnable> mAnimatorOnStartMap;
    private HashMap<Animator, Runnable> mAnimatorOnEndMap;

    /**
     * This is the information we need to set each property during the animation.
     * mNameConstant is used to set the appropriate field in View, and the from/delta
     * values are used to calculate the animated value for a given animation fraction
     * during the animation.
     */
    static class NameValuesHolder {
        int mNameConstant;
        float mFromValue;
        float mDeltaValue;
        NameValuesHolder(int nameConstant, float fromValue, float deltaValue) {
            mNameConstant = nameConstant;
            mFromValue = fromValue;
            mDeltaValue = deltaValue;
        }
    }

    /**
     * Constructor, called by View. This is private by design, as the user should only
     * get a ViewPropertyAnimator by calling View.propertyAnimator().
     *
     * @param textSpan The View associated with this ViewPropertyAnimator
     */
    public TextSpanPropertyAnimator(View view, AnimationTextSpan textSpan) {
        this.view=view;
        this.animationTextSpan=textSpan;
    }

    /**
     * Sets the duration for the underlying animator that animates the requested properties.
     * By default, the animator uses the default value for ValueAnimator. Calling this method
     * will cause the declared value to be used instead.
     * @param duration The length of ensuing property animations, in milliseconds. The value
     * cannot be negative.
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public TextSpanPropertyAnimator setDuration(long duration) {
        if (duration < 0) {
            throw new IllegalArgumentException("Animators cannot have negative duration: " +
                    duration);
        }
        mDurationSet = true;
        mDuration = duration;
        return this;
    }

    /**
     * Defines what this animation should do when it reaches the end. This
     * setting is applied only when the repeat count is either greater than
     * 0 or {@link ValueAnimator#INFINITE}. Defaults to {@link ValueAnimator#RESTART}.
     *
     * @param value {@link ValueAnimator#RESTART} or {@link ValueAnimator#REVERSE}
     */
    public TextSpanPropertyAnimator setRepeatMode(int value){
        this.mRepeatMode=value;
        return this;
    }

    /**
     * Sets how many times the animation should be repeated. If the repeat
     * count is 0, the animation is never repeated. If the repeat count is
     * greater than 0 or {@link ValueAnimator#INFINITE}, the repeat mode will be taken
     * into account. The repeat count is 0 by default.
     *
     * @param value the number of times the animation should be repeated
     */
    public TextSpanPropertyAnimator setRepeatCount(int value){
        this.mRepeatCount=value;
        return this;
    }

    /**
     * Returns the current duration of property animations. If the duration was set on this
     * object, that value is returned. Otherwise, the default value of the underlying Animator
     * is returned.
     *
     * @see #setDuration(long)
     * @return The duration of animations, in milliseconds.
     */
    public long getDuration() {
        if (mDurationSet) {
            return mDuration;
        } else {
            // Just return the default from ValueAnimator, since that's what we'd get if
            // the value has not been set otherwise
            if (mTempValueAnimator == null) {
                mTempValueAnimator = new ValueAnimator();
            }
            return mTempValueAnimator.getDuration();
        }
    }

    /**
     * Returns the current startDelay of property animations. If the startDelay was set on this
     * object, that value is returned. Otherwise, the default value of the underlying Animator
     * is returned.
     *
     * @see #setStartDelay(long)
     * @return The startDelay of animations, in milliseconds.
     */
    public long getStartDelay() {
        if (mStartDelaySet) {
            return mStartDelay;
        } else {
            // Just return the default from ValueAnimator (0), since that's what we'd get if
            // the value has not been set otherwise
            return 0;
        }
    }

    /**
     * Defines how many times the animation should repeat. The default value
     * is 0.
     *
     * @return the number of times the animation should repeat, or {@link ValueAnimator#INFINITE}
     */
    public int getRepeatCount() {
        return mRepeatCount;
    }

    /**
     * Defines what this animation should do when it reaches the end.
     *
     * @return either one of {@link ValueAnimator#REVERSE} or {@link ValueAnimator#RESTART}
     */
    public int getRepeatMode() {
        return mRepeatMode;
    }


    /**
     * Sets the startDelay for the underlying animator that animates the requested properties.
     * By default, the animator uses the default value for ValueAnimator. Calling this method
     * will cause the declared value to be used instead.
     * @param startDelay The delay of ensuing property animations, in milliseconds. The value
     * cannot be negative.
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public TextSpanPropertyAnimator setStartDelay(long startDelay) {
        if (startDelay < 0) {
            throw new IllegalArgumentException("Animators cannot have negative start " +
                "delay: " + startDelay);
        }
        mStartDelaySet = true;
        mStartDelay = startDelay;
        return this;
    }

    /**
     * Sets the interpolator for the underlying animator that animates the requested properties.
     * By default, the animator uses the default interpolator for ValueAnimator. Calling this method
     * will cause the declared object to be used instead.
     *
     * @param interpolator The TimeInterpolator to be used for ensuing property animations. A value
     * of <code>null</code> will result in linear interpolation.
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public TextSpanPropertyAnimator setInterpolator(TimeInterpolator interpolator) {
        mInterpolatorSet = true;
        mInterpolator = interpolator;
        return this;
    }

    /**
     * Returns the timing interpolator that this animation uses.
     *
     * @return The timing interpolator for this animation.
     */
    public TimeInterpolator getInterpolator() {
        if (mInterpolatorSet) {
            return mInterpolator;
        } else {
            // Just return the default from ValueAnimator, since that's what we'd get if
            // the value has not been set otherwise
            if (mTempValueAnimator == null) {
                mTempValueAnimator = new ValueAnimator();
            }
            return mTempValueAnimator.getInterpolator();
        }
    }

    /**
     * Sets a listener for events in the underlying Animators that run the property
     * animations.
     *
     * @see Animator.AnimatorListener
     *
     * @param listener The listener to be called with AnimatorListener events. A value of
     * <code>null</code> removes any existing listener.
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public TextSpanPropertyAnimator setListener(Animator.AnimatorListener listener) {
        mListener = listener;
        return this;
    }

    Animator.AnimatorListener getListener() {
        return mListener;
    }

    /**
     * Sets a listener for update events in the underlying ValueAnimator that runs
     * the property animations. Note that the underlying animator is animating between
     * 0 and 1 (these values are then turned into the actual property values internally
     * by ViewPropertyAnimator). So the animator cannot give information on the current
     * values of the properties being animated by this ViewPropertyAnimator, although
     * the view object itself can be queried to get the current values.
     *
     * @see ValueAnimator.AnimatorUpdateListener
     *
     * @param listener The listener to be called with update events. A value of
     * <code>null</code> removes any existing listener.
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public TextSpanPropertyAnimator setUpdateListener(ValueAnimator.AnimatorUpdateListener listener) {
        mUpdateListener = listener;
        return this;
    }

    ValueAnimator.AnimatorUpdateListener getUpdateListener() {
        return mUpdateListener;
    }

    /**
     * Starts the currently pending property animations immediately. Calling <code>start()</code>
     * is optional because all animations start automatically at the next opportunity. However,
     * if the animations are needed to start immediately and synchronously (not at the time when
     * the next event is processed by the hierarchy, which is when the animations would begin
     * otherwise), then this method can be used.
     */
    public void start() {
        view.removeCallbacks(mAnimationStarter);
        startAnimation();
    }

    /**
     * Cancels all property animations that are currently running or pending.
     */
    public void cancel() {
        if (mAnimatorMap.size() > 0) {
            HashMap<Animator, PropertyBundle> mAnimatorMapCopy =
                    (HashMap<Animator, PropertyBundle>)mAnimatorMap.clone();
            Set<Animator> animatorSet = mAnimatorMapCopy.keySet();
            for (Animator runningAnim : animatorSet) {
                runningAnim.cancel();
            }
        }
        mPendingAnimations.clear();
        mPendingSetupAction = null;
        mPendingCleanupAction = null;
        mPendingOnStartAction = null;
        mPendingOnEndAction = null;
        view.removeCallbacks(mAnimationStarter);
    }

    public void pause(){
        if (mAnimatorMap.size() > 0) {
            HashMap<ValueAnimator, PropertyBundle> mAnimatorMapCopy =
                    (HashMap<ValueAnimator, PropertyBundle>)mAnimatorMap.clone();
            Set<ValueAnimator> animatorSet = mAnimatorMapCopy.keySet();
            for (ValueAnimator runningAnim : animatorSet) {
                runningAnim.pause();
            }
        }
    }

    public void resume(){
        if (mAnimatorMap.size() > 0) {
            HashMap<ValueAnimator, PropertyBundle> mAnimatorMapCopy =
                    (HashMap<ValueAnimator, PropertyBundle>)mAnimatorMap.clone();
            Set<ValueAnimator> animatorSet = mAnimatorMapCopy.keySet();
            for (ValueAnimator runningAnim : animatorSet) {
                runningAnim.resume();
            }
        }
    }

    /**
     * This method will cause the View's <code>x</code> property to be animated to the
     * specified value. Animations already running on the property will be canceled.
     *
     * @param value The value to be animated to.
     * @see View#setX(float)
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public TextSpanPropertyAnimator x(float value) {
        animateProperty(X, value);
        return this;
    }

    /**
     * This method will cause the View's <code>x</code> property to be animated by the
     * specified value. Animations already running on the property will be canceled.
     *
     * @param value The amount to be animated by, as an offset from the current value.
     * @see View#setX(float)
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public TextSpanPropertyAnimator xBy(float value) {
        animatePropertyBy(X, value);
        return this;
    }

    /**
     * This method will cause the View's <code>y</code> property to be animated to the
     * specified value. Animations already running on the property will be canceled.
     *
     * @param value The value to be animated to.
     * @see View#setY(float)
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public TextSpanPropertyAnimator y(float value) {
        animateProperty(Y, value);
        return this;
    }

    /**
     * This method will cause the View's <code>y</code> property to be animated by the
     * specified value. Animations already running on the property will be canceled.
     *
     * @param value The amount to be animated by, as an offset from the current value.
     * @see View#setY(float)
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public TextSpanPropertyAnimator yBy(float value) {
        animatePropertyBy(Y, value);
        return this;
    }


    /**
     * This method will cause the View's <code>rotation</code> property to be animated to the
     * specified value. Animations already running on the property will be canceled.
     *
     * @param value The value to be animated to.
     * @see View#setRotation(float)
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public TextSpanPropertyAnimator rotation(float value) {
        animateProperty(ROTATION, value);
        return this;
    }

    /**
     * This method will cause the View's <code>rotation</code> property to be animated by the
     * specified value. Animations already running on the property will be canceled.
     *
     * @param value The amount to be animated by, as an offset from the current value.
     * @see View#setRotation(float)
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public TextSpanPropertyAnimator rotationBy(float value) {
        animatePropertyBy(ROTATION, value);
        return this;
    }


    /**
     * This method will cause the View's <code>translationX</code> property to be animated to the
     * specified value. Animations already running on the property will be canceled.
     *
     * @param value The value to be animated to.
     * @see View#setTranslationX(float)
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public TextSpanPropertyAnimator translationX(float value) {
        animateProperty(TRANSLATION_X, value);
        return this;
    }

    /**
     * This method will cause the View's <code>translationX</code> property to be animated by the
     * specified value. Animations already running on the property will be canceled.
     *
     * @param value The amount to be animated by, as an offset from the current value.
     * @see View#setTranslationX(float)
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public TextSpanPropertyAnimator translationXBy(float value) {
        animatePropertyBy(TRANSLATION_X, value);
        return this;
    }

    /**
     * This method will cause the View's <code>translationY</code> property to be animated to the
     * specified value. Animations already running on the property will be canceled.
     *
     * @param value The value to be animated to.
     * @see View#setTranslationY(float)
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public TextSpanPropertyAnimator translationY(float value) {
        animateProperty(TRANSLATION_Y, value);
        return this;
    }

    /**
     * This method will cause the View's <code>translationY</code> property to be animated by the
     * specified value. Animations already running on the property will be canceled.
     *
     * @param value The amount to be animated by, as an offset from the current value.
     * @see View#setTranslationY(float)
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public TextSpanPropertyAnimator translationYBy(float value) {
        animatePropertyBy(TRANSLATION_Y, value);
        return this;
    }

    /**
     * This method will cause the View's <code>scaleX</code> property to be animated to the
     * specified value. Animations already running on the property will be canceled.
     *
     * @param value The value to be animated to.
     * @see View#setScaleX(float)
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public TextSpanPropertyAnimator scaleX(float value) {
        animateProperty(SCALE_X, value);
        return this;
    }

    /**
     * This method will cause the View's <code>scaleX</code> property to be animated by the
     * specified value. Animations already running on the property will be canceled.
     *
     * @param value The amount to be animated by, as an offset from the current value.
     * @see View#setScaleX(float)
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public TextSpanPropertyAnimator scaleXBy(float value) {
        animatePropertyBy(SCALE_X, value);
        return this;
    }

    /**
     * This method will cause the View's <code>scaleY</code> property to be animated to the
     * specified value. Animations already running on the property will be canceled.
     *
     * @param value The value to be animated to.
     * @see View#setScaleY(float)
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public TextSpanPropertyAnimator scaleY(float value) {
        animateProperty(SCALE_Y, value);
        return this;
    }

    /**
     * This method will cause the View's <code>scaleY</code> property to be animated by the
     * specified value. Animations already running on the property will be canceled.
     *
     * @param value The amount to be animated by, as an offset from the current value.
     * @see View#setScaleY(float)
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public TextSpanPropertyAnimator scaleYBy(float value) {
        animatePropertyBy(SCALE_Y, value);
        return this;
    }

    /**
     * This method will cause the View's <code>alpha</code> property to be animated to the
     * specified value. Animations already running on the property will be canceled.
     *
     * @param value The value to be animated to.
     * @see View#setAlpha(float)
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public TextSpanPropertyAnimator alpha(float value) {
        animateProperty(ALPHA, value);
        return this;
    }

    /**
     * This method will cause the View's <code>alpha</code> property to be animated by the
     * specified value. Animations already running on the property will be canceled.
     *
     * @param value The amount to be animated by, as an offset from the current value.
     * @see View#setAlpha(float)
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public TextSpanPropertyAnimator alphaBy(float value) {
        animatePropertyBy(ALPHA, value);
        return this;
    }


    /**
     * Specifies an action to take place when the next animation runs. If there is a
     * {@link #setStartDelay(long) startDelay} set on this ViewPropertyAnimator, then the
     * action will run after that startDelay expires, when the actual animation begins.
     * This method, along with {@link #withEndAction(Runnable)}, is intended to help facilitate
     * choreographing ViewPropertyAnimator animations with other animations or actions
     * in the application.
     *
     * @param runnable The action to run when the next animation starts.
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public TextSpanPropertyAnimator withStartAction(Runnable runnable) {
        mPendingOnStartAction = runnable;
        if (runnable != null && mAnimatorOnStartMap == null) {
            mAnimatorOnStartMap = new HashMap<>();
        }
        return this;
    }

    /**
     * Specifies an action to take place when the next animation ends. The action is only
     * run if the animation ends normally; if the ViewPropertyAnimator is canceled during
     * that animation, the runnable will not run.
     * This method, along with {@link #withStartAction(Runnable)}, is intended to help facilitate
     * choreographing ViewPropertyAnimator animations with other animations or actions
     * in the application.
     *
     * <p>For example, the following code animates a view to x=200 and then back to 0:</p>
     * <pre>
     *     Runnable endAction = new Runnable() {
     *         public void run() {
     *             view.propertyAnimator().x(0);
     *         }
     *     };
     *     view.propertyAnimator().x(200).withEndAction(endAction);
     * </pre>
     *
     * @param runnable The action to run when the next animation ends.
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public TextSpanPropertyAnimator withEndAction(Runnable runnable) {
        mPendingOnEndAction = runnable;
        if (runnable != null && mAnimatorOnEndMap == null) {
            mAnimatorOnEndMap = new HashMap<Animator, Runnable>();
        }
        return this;
    }

    /**
     * Starts the underlying Animator for a set of properties. We use a single animator that
     * simply runs from 0 to 1, and then use that fractional value to set each property
     * value accordingly.
     */
    private void startAnimation() {
        animator = ValueAnimator.ofFloat(1.0f);
        ArrayList<NameValuesHolder> nameValueList =
                (ArrayList<NameValuesHolder>) mPendingAnimations.clone();
        mPendingAnimations.clear();
        int propertyMask = 0;
        int propertyCount = nameValueList.size();
        for (int i = 0; i < propertyCount; ++i) {
            NameValuesHolder nameValuesHolder = nameValueList.get(i);
            propertyMask |= nameValuesHolder.mNameConstant;
        }
        mAnimatorMap.put(animator, new PropertyBundle(propertyMask, nameValueList));
        if (mPendingSetupAction != null) {
            mAnimatorSetupMap.put(animator, mPendingSetupAction);
            mPendingSetupAction = null;
        }
        if (mPendingCleanupAction != null) {
            mAnimatorCleanupMap.put(animator, mPendingCleanupAction);
            mPendingCleanupAction = null;
        }
        if (mPendingOnStartAction != null) {
            mAnimatorOnStartMap.put(animator, mPendingOnStartAction);
            mPendingOnStartAction = null;
        }
        if (mPendingOnEndAction != null) {
            mAnimatorOnEndMap.put(animator, mPendingOnEndAction);
            mPendingOnEndAction = null;
        }
        animator.addUpdateListener(mAnimatorEventListener);
        animator.addListener(mAnimatorEventListener);
        animator.setRepeatCount(mRepeatCount);
        animator.setRepeatMode(mRepeatMode);
        if (mStartDelaySet) {
            animator.setStartDelay(mStartDelay);
        }
        if (mDurationSet) {
            animator.setDuration(mDuration);
        }
        if (mInterpolatorSet) {
            animator.setInterpolator(mInterpolator);
        }
        animator.start();
    }

    /**
     * Utility function, called by the various x(), y(), etc. methods. This stores the
     * constant name for the property along with the from/delta values that will be used to
     * calculate and set the property during the animation. This structure is added to the
     * pending animations, awaiting the eventual start() of the underlying animator. A
     * Runnable is posted to start the animation, and any pending such Runnable is canceled
     * (which enables us to end up starting just one animator for all of the properties
     * specified at one time).
     *
     * @param constantName The specifier for the property being animated
     * @param toValue The value to which the property will propertyAnimator
     */
    private void animateProperty(int constantName, float toValue) {
        float fromValue = getValue(constantName);
        float deltaValue = toValue - fromValue;
        animatePropertyBy(constantName, fromValue, deltaValue);
    }

    /**
     * Utility function, called by the various xBy(), yBy(), etc. methods. This method is
     * just like animateProperty(), except the value is an offset from the property's
     * current value, instead of an absolute "to" value.
     *
     * @param constantName The specifier for the property being animated
     * @param byValue The amount by which the property will change
     */
    private void animatePropertyBy(int constantName, float byValue) {
        float fromValue = getValue(constantName);
        animatePropertyBy(constantName, fromValue, byValue);
    }

    /**
     * Utility function, called by animateProperty() and animatePropertyBy(), which handles the
     * details of adding a pending animation and posting the request to start the animation.
     *
     * @param constantName The specifier for the property being animated
     * @param startValue The starting value of the property
     * @param byValue The amount by which the property will change
     */
    private void animatePropertyBy(int constantName, float startValue, float byValue) {
        // First, cancel any existing animations on this property
        if (mAnimatorMap.size() > 0) {
            Animator animatorToCancel = null;
            Set<ValueAnimator> animatorSet = mAnimatorMap.keySet();
            for (Animator runningAnim : animatorSet) {
                PropertyBundle bundle = mAnimatorMap.get(runningAnim);
                if (bundle.cancel(constantName)) {
                    // property was canceled - cancel the animation if it's now empty
                    // Note that it's safe to break out here because every new animation
                    // on a property will cancel a previous animation on that property, so
                    // there can only ever be one such animation running.
                    if (bundle.mPropertyMask == NONE) {
                        // the animation is no longer changing anything - cancel it
                        animatorToCancel = runningAnim;
                        break;
                    }
                }
            }
            if (animatorToCancel != null) {
                animatorToCancel.cancel();
            }
        }

        NameValuesHolder nameValuePair = new NameValuesHolder(constantName, startValue, byValue);
        mPendingAnimations.add(nameValuePair);
        view.removeCallbacks(mAnimationStarter);
        view.postOnAnimation(mAnimationStarter);
    }

    /**
     * This method handles setting the property values directly in the View object's fields.
     * propertyConstant tells it which property should be set, value is the value to set
     * the property to.
     *
     * @param propertyConstant The property to be set
     * @param value The value to set the property to
     */
    private void setValue(int propertyConstant, float value) {
        switch (propertyConstant) {
            case TRANSLATION_X:
                animationTextSpan.setTranslationX(value);
                break;
            case TRANSLATION_Y:
                animationTextSpan.setTranslationY(value);
                break;
            case ROTATION:
                animationTextSpan.setRotate(value);
                break;
            case SCALE_X:
                animationTextSpan.setScaleX(value);
                break;
            case SCALE_Y:
                animationTextSpan.setScaleY(value);
                break;
            case X:
                animationTextSpan.setX(value);
                break;
            case Y:
                animationTextSpan.setY(value);
                break;
            case ALPHA:
                animationTextSpan.setAlpha(value);
                break;
        }
    }

    /**
     * This method gets the value of the named property from the View object.
     *
     * @param propertyConstant The property whose value should be returned
     * @return float The value of the named property
     */
    private float getValue(int propertyConstant) {
        switch (propertyConstant) {
            case TRANSLATION_X:
                return animationTextSpan.getTranslationX();
            case TRANSLATION_Y:
                return animationTextSpan.getTranslationY();
            case ROTATION:
                return animationTextSpan.getRotation();
            case SCALE_X:
                return animationTextSpan.getScaleX();
            case SCALE_Y:
                return animationTextSpan.getScaleY();
            case X:
                return animationTextSpan.getX();
            case Y:
                return animationTextSpan.getY();
            case ALPHA:
                return animationTextSpan.getAlpha();
        }
        return 0;
    }

    /**
     * Utility class that handles the various Animator events. The only ones we care
     * about are the end event (which we use to clean up the animator map when an animator
     * finishes) and the update event (which we use to calculate the current value of each
     * property and then set it on the view object).
     */
    private class AnimatorEventListener
            implements Animator.AnimatorListener, ValueAnimator.AnimatorUpdateListener {
        @Override
        public void onAnimationStart(Animator animation) {
            if (mAnimatorSetupMap != null) {
                Runnable r = mAnimatorSetupMap.get(animation);
                if (r != null) {
                    r.run();
                }
                mAnimatorSetupMap.remove(animation);
            }
            if (mAnimatorOnStartMap != null) {
                Runnable r = mAnimatorOnStartMap.get(animation);
                if (r != null) {
                    r.run();
                }
                mAnimatorOnStartMap.remove(animation);
            }
            if (mListener != null) {
                mListener.onAnimationStart(animation);
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            if (mListener != null) {
                mListener.onAnimationCancel(animation);
            }
            if (mAnimatorOnEndMap != null) {
                mAnimatorOnEndMap.remove(animation);
            }
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
            if (mListener != null) {
                mListener.onAnimationRepeat(animation);
            }
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (mAnimatorCleanupMap != null) {
                Runnable r = mAnimatorCleanupMap.get(animation);
                if (r != null) {
                    r.run();
                }
                mAnimatorCleanupMap.remove(animation);
            }
            if (mListener != null) {
                mListener.onAnimationEnd(animation);
            }
            if (mAnimatorOnEndMap != null) {
                Runnable r = mAnimatorOnEndMap.get(animation);
                if (r != null) {
                    r.run();
                }
                mAnimatorOnEndMap.remove(animation);
            }
            mAnimatorMap.remove(animation);
        }

        /**
         * Calculate the current value for each property and set it on the view. Invalidate
         * the view object appropriately, depending on which properties are being animated.
         *
         * @param animation The animator associated with the properties that need to be
         * set. This animator holds the animation fraction which we will use to calculate
         * the current value of each property.
         */
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            PropertyBundle propertyBundle = mAnimatorMap.get(animation);
            if (propertyBundle == null) {
                // Shouldn't happen, but just to play it safe
                return;
            }

            float fraction = animation.getAnimatedFraction();
            int propertyMask = propertyBundle.mPropertyMask;
            if ((propertyMask & TRANSFORM_MASK) != 0) {
                animationTextSpan.invalidate();
            }
            ArrayList<NameValuesHolder> valueList = propertyBundle.mNameValuesHolder;
            if (valueList != null) {
                int count = valueList.size();
                for (int i = 0; i < count; ++i) {
                    NameValuesHolder values = valueList.get(i);
                    float value = values.mFromValue + fraction * values.mDeltaValue;
                    setValue(values.mNameConstant, value);
                }
            }
            animationTextSpan.invalidate();
            if (mUpdateListener != null) {
                mUpdateListener.onAnimationUpdate(animation);
            }
        }
    }
}
