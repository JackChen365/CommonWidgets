package com.cz.widgets.common.frame.triiger;

import androidx.annotation.Nullable;

import com.cz.widgets.common.frame.AbsFrameWrapper;

/**
 * @author Created by cz
 * @date 2020-05-22 20:44
 * @email bingo110@126.com
 * The frame changing trigger.
 */
public abstract class FrameTrigger<T> {

    /**
     * The attached frame wrapper object.
     */
    private AbsFrameWrapper frameWrapper;
    /**
     * The frame callback.
     */
    private FrameCallback callback=null;
    /**
     * Attach to the frame wrapper
     */
    public void attachToFrameWrapper(AbsFrameWrapper frameWrapper){
        this.frameWrapper = frameWrapper;
    }
    /**
     * Something changed. We will trigger the frame changed.
     */
    protected abstract void trigger(AbsFrameWrapper frameWrapper,@Nullable T state);

    /**
     * The state value has changed. We trigger the frame.
     */
    public void trigger(@Nullable T state){
        if(isActive()){
            trigger(frameWrapper,state);
        }
    }

    public void setCallback(FrameCallback callback){
        this.callback=callback;
    }

    protected void call(){
        if(null!=callback){
            callback.call();
        }
    }

    /**
     * Check the trigger is still alive. If you want to disable the trigger. return true after you has done something.
     * @return
     */
    public boolean isActive(){
        return true;
    }

    /**
     * Detach from the frame wrapper.
     * You could release something.
     */
    public void onDetached(){

    }
}
