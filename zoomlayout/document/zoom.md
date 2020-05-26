## How to zoom the layout

I have tried a few ways to zoom the view.


### The first version
I was just to draw the view but not add this view to the ViewGroup.

```
canvas.scale(matrixScaleX, matrixScaleY)
canvas.translate(childView.left.toFloat(), childView.top.toFloat())
childView.draw(canvas)
canvas.restore()
```

But it is hard to handle all the touch events and the view gesture.

I have tried to dispatch the touch event manually, and change the view drawable status.

```
override fun onSingleTapUp(e: MotionEvent): Boolean {
    //Perform the click event here.
    views.find { it.isPressed }?.let { it.performClick() }
    releasePressView()
    return true
}


override fun onDown(e: MotionEvent): Boolean {
    viewFlinger.abortAnimation()
    val x=scrollX+e.x.toInt()
    val y=scrollY+e.y.toInt()
    val matrixScaleX = getMatrixScaleX()
    val matrixScaleY = getMatrixScaleY()
    run {
        forEachChild { view->
            tmpRect.set((view.left*matrixScaleX).toInt(),
                    (view.top*matrixScaleY).toInt(),
                    (view.right*matrixScaleX).toInt(),
                    (view.bottom*matrixScaleY).toInt())
            if(tmpRect.contains(x,y)){
                view.isPressed=true
            }
        }
    }
    return false
}
```
It just makes me feel disgusting. This time I would not do this again.


### The second one.

I scaled the layer like this. By zoom the canvas in the dispatchDraw method. I was able to scale all the child views inside this view.

```
@Override
protected void dispatchDraw(Canvas canvas) {
    canvas.save();
    float matrixScaleX = getLayoutScaleX();
    float matrixScaleY = getLayoutScaleY();
    canvas.scale(matrixScaleX,matrixScaleY);
    super.dispatchDraw(canvas);
    canvas.restore();
}
```

But after that. I got a seriously problem.
I scaled the child view's drawing layer but I did not change the touch event's location.

It is like you are getting bigger and bigger but you never change your clothes.
Then all the things happened in the wrong location.


After I did a bunch of research. I actually did not found the solution.
Then I did something crazy. I was trying to copy the dispatchTouchEvent from ViewGroup to my ZoomLayout.
It is hard work. Because a lot of fields in the class are hidden or missing.
Afterward, I figure it out by moving the method from the ViewGroup of System version 4.2 to my ZoomLayout.

```
@Override
public boolean dispatchTouchEvent(MotionEvent ev) {
    if (!onFilterTouchEventForSecurity(ev)) {
        return false;
    }
    ...

    boolean disallowIntercept = (groupFlags & FLAG_DISALLOW_INTERCEPT) != 0;

    if (action == MotionEvent.ACTION_DOWN) {
        if (motionTarget != null) {
            // this is weird, we got a pen down, but we thought it was
            // already down!
            // XXX: We should probably send an ACTION_UP to the current
            // target.
            motionTarget = null;
        }
        // If we're disallowing intercept or if we're allowing and we didn't
        // intercept
        if (disallowIntercept || !onInterceptTouchEvent(ev)) {
            // reset this event's action (just to protect ourselves)
            ...
            for (int i = count - 1; i >= 0; i--) {
                final View child = getChildAt(i);
                if (child.getVisibility() == VISIBLE || child.getAnimation() != null) {
                    child.getHitRect(frame);
                    frame.set((int)(frame.left*matrixScaleX),
                            (int)(frame.top*matrixScaleY),
                            (int)(frame.right*matrixScaleX),
                            (int)(frame.bottom*matrixScaleY));

                    //-----------------------------------------
                    //Change everything here.
                    if (frame.contains(scrolledXInt, scrolledYInt)) {
                        // offset the event to the view's coordinate system
                        final float xc = scrolledXFloat - frame.left;
                        final float yc = scrolledYFloat - frame.top;
                        ev.setLocation(xc/matrixScaleX, yc/matrixScaleY);
                        if (child.dispatchTouchEvent(ev)){
                            // Event handled, we have a target now.
                            motionTarget = child;
                            return true;
                        }
                        // The event didn't get handled, try the next view.
                        // Don't reset the event's location, it's not
                        // necessary here.
                    }
                }
            }
        }
    }

    ...

    View target = motionTarget;
    if (target == null) {
        if ((groupFlags & FLAG_CANCEL_NEXT_UP_EVENT) != 0) {
            ev.setAction(MotionEvent.ACTION_CANCEL);
            groupFlags &= ~FLAG_CANCEL_NEXT_UP_EVENT;
        }
        ev.setLocation(xf, yf);

        //-----------------------------------------
        //We handle this event by dispatching this event to this method.
        return onTouchEvent(ev);
    }

    ...
    return target.dispatchTouchEvent(ev);
}
```


It is not as complicated as the latest version of Android. But we dispatch all the event like the ViewGroup.
And I would never need to dispatch the touch events to the child views.

But we still not mention how we scale the View. We use the ScaleGestureDetector to detect the scale gesture.

```
@Override
public boolean onScale(ScaleGestureDetector detector) {
    float scaleFactor=detector.getScaleFactor();
    float oldMatrixScaleX = getLayoutScaleX();
    float oldMatrixScaleY = getLayoutScaleY();
    if(zoomMinimum>scaleFactor*oldMatrixScaleX){
        scaleFactor=zoomMinimum/oldMatrixScaleX;
    } else if(zoomMaximum<scaleFactor*oldMatrixScaleX){
        scaleFactor=zoomMaximum/oldMatrixScaleX;
    }
    matrix.postScale(scaleFactor, scaleFactor);
    setViewScaleInternal(oldMatrixScaleX,oldMatrixScaleY,scaleFocusX,scaleFocusY);
    return true;
}

public void setViewScaleInternal(float oldMatrixScaleX, float oldMatrixScaleY,float focusX,float focusY) {
    //Calculate the focus center location.
    float matrixScaleX = getLayoutScaleX();
    float matrixScaleY = getLayoutScaleY();

    float scrolledX = focusX+scaleScrollX;
    float scrolledY = focusY+scaleScrollY;
    int dx = Math.round(((matrixScaleX-oldMatrixScaleX) * scrolledX)/matrixScaleX);
    int dy = Math.round(((matrixScaleY-oldMatrixScaleY) * scrolledY)/matrixScaleY);
    scrollBy(dx,dy);
    ViewCompat.postInvalidateOnAnimation(this);

    if(null!=scaleChangeListener){
        scaleChangeListener.onScaleChange(scaleFocusX,scaleFocusY);
    }
}
```

We calculate the focus center of the scale gesture while the layout is zooming.
Just like that. I do want to calculate this again. Almost drive me crazy.


After that. Everything works properly.

### References.

* [The zoom strategy](./zoom.md).
* [The recycle strategy](./zoom_recycler.md).
* [The preview layout](./preview.md).