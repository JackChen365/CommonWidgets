package com.cz.widgets.common.frame;

import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;

import androidx.annotation.CallSuper;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.appcompat.view.ContextThemeWrapper;
import com.cz.widgets.common.frame.listener.OnLayoutFrameChangeListener;
import com.cz.widgets.common.frame.transition.DefaultFrameTransition;
import com.cz.widgets.common.frame.transition.FrameTransition;
import com.cz.widgets.common.frame.triiger.FrameTrigger;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

/**
 * @author Created by cz
 * @date 2020-05-22 23:03
 * @email bingo110@126.com
 * The abstract frame wrapper. It's not a View. But a tool instead.
 *
 * The function list:
 * 1. The Frame transition change the frame with animation.
 * 2. The Frame trigger. change the frame by different situation automatically
 * 3. The Frame configuration. Including the global frame and custom frame.
 *
 * The {@link FrameTransition} is to support changing animation.
 * @see DefaultFrameTransition We use this customize the different change animation.
 * Also notice we could use this to change the rule when you switch from the Frame-A to the Frame-B
 * If you don't want to hide the last frame like the content.
 *
 * The {@link FrameTrigger} It is actually an controller to change the frame.
 * Like when you enter a page. The network just not available and you want to show the user the network goes wrong.
 * But after you turn on the Network. You want to load something from the network and display the content immediately.
 *
 * The Frame configuration. We use {{@link #registerFrame(int, int)}} to configure the global template frame.
 * For your custom frame.
 * <pre>
 * <com.cz.widgets.common.frame.FrameViewLayout
 *         android:id="@+id/frameView"
 *         android:layout_width="match_parent"
 *         android:layout_height="wrap_content">
 *
 *         <FrameLayout
 *             android:layout_width="match_parent"
 *             android:layout_height="wrap_content"
 *             android:gravity="center"
 *             //Mark as a content view.
 *             app:layout_content="true">
 *         </FrameLayout>
 *
 *         <LinearLayout
 *             android:id="@+id/customEmptyLayout"
 *             android:layout_width="wrap_content"
 *             android:layout_height="wrap_content"
 *             android:orientation="vertical"
 *             android:background="@color/colorPrimaryDark"
 *             android:layout_gravity="center"
 *             android:gravity="center">
 *             ...
 *        </LinearLayout>
 *        ...
 * </com.cz.widgets.common.frame.FrameViewLayout>
 * <pre/>
 * @see FrameContainer implement from this interface will make your View as a frame container.
 *
 */
public class AbsFrameWrapper {
    private static final String TAG="AbsFrameWrapper";
    /**
     * The default content layout id. It not real, just represent the content view.
     */
    private static final int FRAME_CONTENT = 0;
    /**
     * The global template frame array. Each FrameWrapper will just use all the Frames from this array.
     */
    private static final SparseArray<FrameItem> frameArray =new SparseArray<>();

    /**
     * Register a new template frame globally.
     * @param id
     * @param layout
     */
    public static void registerFrame(int id,@LayoutRes int layout){
        frameArray.put(id,new FrameItem(id,layout));
    }

    /**
     * The wrapped context LayoutInflater.
     * That's how we support the style.
     * @see AbsFrameWrapper#AbsFrameWrapper(android.view.View, int)
     */
    private final LayoutInflater layoutInflater;
    /**
     * The frame change listener list.
     */
    private List<OnLayoutFrameChangeListener> listenerList= new ArrayList<>();
    /**
     * The frame transition.
     */
    private FrameTransition frameTransition=new DefaultFrameTransition();
    /**
     * The blocking queue for frame changing. Because of the FrameTransition we never know when to change to next frame of layout.
     * So here we have a blocking queue.
     */
    private Queue<Message> blockingFrameQueue = new ArrayDeque<>();
    /**
     * The frame trigger list.
     */
    private List<FrameTrigger> triggerList=null;
    /**
     * The changing runnable.
     */
    private Runnable frameChangeAction =new Runnable(){
        @Override
        public void run() {
            if(!blockingFrameQueue.isEmpty()){
                Message message = blockingFrameQueue.poll();
                currentFrameId=message.arg1;
                message.recycle();
            }
            if(!blockingFrameQueue.isEmpty()){
                Message message = blockingFrameQueue.peek();
                setFrameInternal(message.arg1,message.arg2);
            }
        }
    };

    /**
     * Current frame id
     */
    private int currentFrameId= FRAME_CONTENT;
    private final ViewGroup hostView;
    private final View wrappedView;

    public AbsFrameWrapper(@NonNull final View wrappedView, @StyleRes int style) {
        ViewParent parent = wrappedView.getParent();
        if(null==parent){
            throw new NullPointerException("The wrapped view is null!");
        }
        //Wrap the target view.
        ViewGroup parentView= (ViewGroup) parent;
        Context context = wrappedView.getContext();
        ViewGroup.LayoutParams layoutParams = wrappedView.getLayoutParams();
        int index = parentView.indexOfChild(wrappedView);
        parentView.removeView(wrappedView);

        this.wrappedView=wrappedView;
        this.hostView = new FrameLayout(context);
        hostView.addView(wrappedView,FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
        parentView.addView(hostView,index,layoutParams);

        if (style == 0) {
            Log.w(TAG,"There is not style for the template!");
        } else {
            context = new ContextThemeWrapper(context, style);
        }
        layoutInflater=LayoutInflater.from(context);
        hostView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                onDetachedFromWindow();
            }
        });
    }

    /**
     * Changes the frame transition. This is for the frame when you are changing the frame from one to another.
     * It changes with animation.
     * @see DefaultFrameTransition This is the default layout transition.
     * @param frameTransition
     */
    public void setFrameTransition(FrameTransition frameTransition) {
        this.frameTransition = frameTransition;
    }

    /**
     * Check the current frame.
     * @param frameId
     * @return
     */
    public boolean isFrame(int frameId){
        return currentFrameId==frameId;
    }

    /**
     * Add a frame trigger.
     * @see FrameTrigger
     * @param trigger
     */
    public void addFrameTrigger(FrameTrigger trigger){
        trigger.attachToFrameWrapper(this);
        if(null==triggerList){
            triggerList=new ArrayList<>();
        }
        triggerList.add(trigger);
    }

    /**
     * Remove a frame trigger from the list.
     * @param trigger
     */
    public void removeFrameTrigger(FrameTrigger trigger){
        if(null!=triggerList){
            triggerList.remove(trigger);
        }
    }

    /**
     * Return the host view. We use this view wrap your view.
     * @return
     */
    @NonNull
    public ViewGroup getHostView() {
        return hostView;
    }

    /**
     * Return your wrapped view.
     * @return
     */
    @NonNull
    public View getWrappedView() {
        return wrappedView;
    }

    public void setFrame(int frameId){
        setFrame(frameId,0);
    }

    public void setFrame(int frameId,int delayTime) {
        Message message = Message.obtain();
        message.arg1=frameId;
        message.arg2=delayTime;
        boolean emptyQueue = blockingFrameQueue.isEmpty();
        blockingFrameQueue.offer(message);
        if(emptyQueue){
            //Change the frame immediately
            setFrameInternal(frameId,delayTime);
        }
    }

    public View findFrameView(int frameId,@IdRes int id){
        View findView=null;
        if(hostView instanceof FrameContainer){
            //Trying to find frame view from host view.
            FrameContainer frameContainer = (FrameContainer) this.hostView;
            if(frameContainer.applyFrame(id)){
                findView=hostView.findViewById(id);
            }
        }
        if(null==findView){
            View frameView = getFrameView(frameId);
            findView=frameView.findViewById(id);
        }
        return findView;
    }

    private View inflateFrameView(int frameId) {
        FrameItem frameItem = frameArray.get(frameId);
        final View frameView = layoutInflater.inflate(frameItem.layout, hostView, false);
        frameView.setVisibility(View.INVISIBLE);
        hostView.addView(frameView);
        return frameView;
    }

    /**
     * Return the frame view by the given frame id.
     * Because the view is supposed load lazily. If we can not find the view.
     * We will try to load the view from the configuration.
     * For the content view. We assume it is a frame container we will ask for the content view.
     * Otherwise I will return the wrapped view.
     *
     * @param frameId
     * @return
     */
    @NonNull
    public View getFrameView(int frameId){
        //Only search from the frame template array.
        if(FRAME_CONTENT==frameId){
            if(wrappedView instanceof FrameContainer){
                FrameContainer frameContainer = (FrameContainer) this.wrappedView;
                return frameContainer.getContentView();
            } else {
                return wrappedView;
            }
        } else {
            View frameView = hostView.findViewById(frameId);
            if(null==frameView){
                frameView=inflateFrameView(frameId);
            }
            return frameView;
        }
    }

    private void setFrameInternal(final int frameId, int delayTime) {
        if(frameId==currentFrameId){
            if(!blockingFrameQueue.isEmpty()){
                Message message = blockingFrameQueue.poll();
                message.recycle();
            }
            if(!blockingFrameQueue.isEmpty()){
                Message message = blockingFrameQueue.peek();
                if(null!=message){
                    setFrameInternal(message.arg1,message.arg2);
                }
            }
        } else {
            final View currentFrameView = getFrameView(currentFrameId);
            final View newFrameView = getFrameView(frameId);
            if(!frameTransition.playSequentially()){
                //playTogether.
                ViewPropertyAnimator currentAnimate = currentFrameView.animate();
                currentAnimate.setStartDelay(delayTime);
                currentFrameView.setVisibility(View.VISIBLE);
                frameTransition.disappearingAnimator(hostView, currentFrameView, currentFrameId);
                currentAnimate.start();
                //When the last frame view hidden. We invoke this method.
                onFrameHidden(currentFrameId,currentFrameView);

                newFrameView.setVisibility(View.VISIBLE);
                frameTransition.appearingAnimator(hostView, newFrameView, frameId);
                ViewPropertyAnimator newAnimate = newFrameView.animate();
                newAnimate.withEndAction(frameChangeAction);
                newAnimate.start();
                //When the current frame view starts the shown animation. We invoke this method.
                onFrameShown(frameId,newFrameView);
            } else {
                //playSequentially
                ViewPropertyAnimator currentAnimate = currentFrameView.animate();
                currentAnimate.setStartDelay(delayTime);
                currentAnimate.withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        //To avoid the layout size change we just change the view visibility to INVISIBLE
                        frameTransition.onFrameDisappear(currentFrameView,currentFrameId);
                        newFrameView.setVisibility(View.VISIBLE);
                        frameTransition.appearingAnimator(hostView, newFrameView, frameId);
                        ViewPropertyAnimator newAnimate = newFrameView.animate();
                        newAnimate.withEndAction(frameChangeAction);
                        newAnimate.start();
                        //When the current frame view starts the shown animation. We invoke this method.
                        onFrameShown(frameId,newFrameView);
                    }
                });
                currentFrameView.setVisibility(View.VISIBLE);
                frameTransition.disappearingAnimator(hostView, currentFrameView, currentFrameId);
                currentAnimate.start();
                //When the last frame view hidden. We invoke this method.
                onFrameHidden(currentFrameId,currentFrameView);
            }
        }
    }

    @CallSuper
    protected void onFrameShown(int id,View view){
        for(OnLayoutFrameChangeListener listener:listenerList){
            listener.onFrameShown(this,id,view);
        }
    }

    @CallSuper
    public void onFrameHidden(int id,View view){
        for(OnLayoutFrameChangeListener listener:listenerList){
            listener.onFrameHidden(this,id,view);
        }
    }

    protected void onDetachedFromWindow() {
        listenerList.clear();
        if(null!=triggerList){
            for(Iterator<FrameTrigger> iterator = triggerList.iterator();iterator.hasNext();){
                FrameTrigger frameTrigger = iterator.next();
                frameTrigger.onDetached();
                iterator.remove();
            }
        }
    }

    static class FrameItem{
        final int id;
        @LayoutRes
        final int layout;

        public FrameItem(int id,@LayoutRes int layout) {
            this.id = id;
            this.layout = layout;
        }
    }
}
