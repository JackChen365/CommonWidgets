## Preview

### Picture

![zoom_hierarchy](https://github.com/momodae/LibraryResources/blob/master/CommonWidgets/image/zoom_hierarchy.gif?raw=true)

Here is an interface called:Previewable

```
public interface Previewable {
    View newPreview();
    void onChildChange(Canvas canvas, View child);
}
```

The HierarchyLayout is an implementation of the interface

```
@Override
public View newPreview() {
    Context context = getContext();
    //We lightly change the class to draw the preview efficiently.
    HierarchyLayout layout=new HierarchyLayout(context){
        private View childView;
        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            //Measure all the tree nodes completely. This is generally for the preview layout.
            if(null!=adapter&&null!=adapter.root){
              ...
            }
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
        }

        @Override
        public void addItemDecoration(@NonNull ItemDecoration decor, int index) {
        }

        @Override
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            Adapter adapter = getAdapter();
            if (null != adapter && null != adapter.root) {
                detachViewFromParent(childView);
                TreeNode hierarchyNode = adapter.root;
                drawHierarchyTree(canvas, adapter, hierarchyNode, childView);
            }
        }

        /**
         * Draw all the tree nodes all at once.
         * @param hierarchyNode
         * @param <E>
         */
        private<E> void drawHierarchyTree(Canvas canvas,Adapter adapter,TreeNode<E> hierarchyNode, View child) {
            //This will add additional extra drawing time from 197 to 691
//                Context context = getContext();
//                adapter.onBindView(context,childView,hierarchyNode,hierarchyNode.value);
            ...
            //Continue loop;
            for(TreeNode<E> childNode:hierarchyNode.children){
                drawHierarchyTree(canvas,adapter,childNode,child);
            }
        }
    };
    layout.setAdapter(adapter);
    return layout;
}

@Override
public void onChildChange(Canvas canvas, View child) {
    LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
    TreeNode treeNode = layoutParams.treeNode;
    if(null!=treeNode){
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int decoratedMeasuredWidth = getDecoratedMeasuredWidth(child);
        int decoratedMeasuredHeight = getDecoratedMeasuredHeight(child);

        int left=paddingLeft+treeNode.centerBreadth*decoratedMeasuredWidth;
        int top=paddingTop+treeNode.depth * decoratedMeasuredHeight;
        canvas.save();
        canvas.translate(left,top);
        child.draw(canvas);
        canvas.restore();
    }
}

```

The method: new preview is when I draw a preview I ask for a totally independent view from the current layout.
I can not just use current layout to draw the preview. Because we are loading all the child views lazily.

What's more, we can not initialize all the child views all at once. It just too slow.

```
I/HierarchyLayout: onMeasure:2239
I/PreViewLayout: time1:2240
I/PreViewLayout: time2:2240
I/HierarchyLayout: draw:105
I/PreViewLayout: time3:2345
I/PreViewLayout: time4:2348
I/PreViewLayout: time5:2349
```

This the performance consumption. It is terrible. And it is not a big tree.

So here I have to slightly change the layout by add only one child and draw the whole tree.

```
// The new version of preview.
I/HierarchyLayoutImpl: onMeasure:14
I/PreViewLayout: time1:14
I/PreViewLayout: time2:14
I/HierarchyLayoutImpl: draw:197
I/PreViewLayout: time3:211
I/PreViewLayout: time4:213
I/PreViewLayout: time5:214
```

Perfect!

#### Changing update to the preview.

We have an another method called:onChildChange

```
@Override
public void onChildChange(Canvas canvas, View child) {
    LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
    TreeNode treeNode = layoutParams.treeNode;
    if(null!=treeNode){
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int decoratedMeasuredWidth = getDecoratedMeasuredWidth(child);
        int decoratedMeasuredHeight = getDecoratedMeasuredHeight(child);

        int left=paddingLeft+treeNode.centerBreadth*decoratedMeasuredWidth;
        int top=paddingTop+treeNode.depth * decoratedMeasuredHeight;
        canvas.save();
        canvas.translate(left,top);
        child.draw(canvas);
        canvas.restore();
    }
}
```

Here we could just draw the child view on preview. Because of the recycler strategy of the layout.
The view shown on the layout is not actually the right location. Only the layout knows where is should be.

So Here we dispatch the change to the layout. and the Layout will be drawing the child by tree node.

But where do we know the child view is changing?

```
@Override
public void childDrawableStateChanged(final View child) {
    super.childDrawableStateChanged(child);
    if(null!=layoutChildDrawableStateChangeListener&&hasWindowFocus()){
        int currentViewStateFlag = 0;
        if(child.isSelected()) currentViewStateFlag|=LayoutParams.VIEW_STATE_SELECTED;
        if(child.isEnabled()) currentViewStateFlag|=LayoutParams.VIEW_STATE_ENABLED;
        if(child.isActivated()) currentViewStateFlag|=LayoutParams.VIEW_STATE_ACTIVATED;
        if(child.isFocused()) currentViewStateFlag|=LayoutParams.VIEW_STATE_FOCUSED;
        if(child.isHovered()) currentViewStateFlag|=LayoutParams.VIEW_STATE_HOVERED;
        if(child.isPressed()) currentViewStateFlag|=LayoutParams.VIEW_STATE_PRESSED;
        final LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
        //To avoid dispatch to many times, We use our custom view state flag to check if the view state is changed.
        if(currentViewStateFlag!=layoutParams.viewStateFlag){
            layoutParams.viewStateFlag=currentViewStateFlag;
            checkForState.setStateChangedView(child);
            post(checkForState);
        }
    }
}
```

Here we have to do a little change. This method just called to many times.
I hold a special state for all the child views to avoid dispatch to many times.

```
public static class LayoutParams extends android.view.ViewGroup.MarginLayoutParams {
        public static final int VIEW_STATE_SELECTED = 1 << 1;
        public static final int VIEW_STATE_FOCUSED = 1 << 2;
        public static final int VIEW_STATE_ENABLED = 1 << 3;
        public static final int VIEW_STATE_PRESSED = 1 << 4;
        public static final int VIEW_STATE_ACTIVATED = 1 << 5;
        public static final int VIEW_STATE_HOVERED = 1 << 6;
        public int viewStateFlag =VIEW_STATE_ENABLED;
        ...
}
```

### The PreViewLayout

```
public class PreViewLayout extends SurfaceView implements SurfaceHolder.Callback{
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private RenderThread renderThread;
    private Bitmap previewBitmap;


}
```

The preview render thread

```
 public class RenderThread extends Thread{
    ...
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
                ...
                //Draw the preview.
                preview.draw(canvas);
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
}
```

We use a thread to render the view as a cache bitmap. Then draw the changed view to the bitmap.

```
zoomLayout.setOnLayoutChildDrawableStateChanged(new ZoomLayout.OnLayoutChildDrawableStateChanged() {
    @Override
    public void childDrawableStateChanged(View child,float scaleX,float scaleY) {
        if(null!=previewBitmap){
            SurfaceHolder holder = getHolder();
            if(null!=holder){
                //Update the bitmap
                ...
                Canvas canvas = new Canvas(previewBitmap);
                previewable.onChildChange(canvas,child);
                ...

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
```