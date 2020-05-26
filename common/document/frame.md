## FrameLayout

### Picture

* ![](https://github.com/momodae/LibraryResources/blob/master/CommonWidgets/image/view_frame.gif?raw=true)

* ![](https://github.com/momodae/LibraryResources/blob/master/CommonWidgets/image/view_frame_trigger.gif?raw=true)

### Functions

* Global frame configuration and style.
* Custom frame layout
* Frame trigger
* FrameTransition


### Usage

```
val viewFrameWrapper1 = FrameWrapper(frameView1,R.style.FrameSmallStyle)
val viewFrameWrapper2 = FrameWrapper(frameView2,R.style.FrameFullScreenStyle)

//Change the layoutTransition
viewFrameWrapper1.setFrameTransition(ContentFrameTransition())
viewFrameWrapper2.setFrameTransition(FrameTranslationX())

//Change the frame.
viewFrameWrapper1.setFrame(FrameWrapper.FRAME_CONTAINER)
viewFrameWrapper1.setFrame(FrameWrapper.FRAME_PROGRESS)
viewFrameWrapper1.setFrame(FrameWrapper.FRAME_EMPTY)
viewFrameWrapper1.setFrame(FrameWrapper.FRAME_ERROR)
```

### Global frame configuration

> You register your global frame template.


```
public class FrameWrapper extends AbsFrameWrapper {
    public static final int FRAME_CONTAINER=0;
    public static final int FRAME_PROGRESS=R.id.frameProgress;
    public static final int FRAME_EMPTY=R.id.frameEmpty;
    public static final int FRAME_ERROR=R.id.frameError;

    static{
        registerFrame(R.id.frameProgress, R.layout.view_frame_progress);
        registerFrame(R.id.frameEmpty, R.layout.view_frame_load_empty);
        registerFrame(R.id.frameError, R.layout.view_frame_load_error);
    }
}
```

For custom frame template we use a specific ViewGroup:FrameViewLayout

```
<com.cz.widgets.common.frame.FrameViewLayout
        android:id="@+id/frameView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content">
        <FrameLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:gravity="center"
            //Most imprtant! mark this child view as the content view.
            app:layout_content="true">
            ...
        </FrameLayout>

        <LinearLayout
            android:id="@+id/customEmptyLayout"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:background="@color/colorPrimaryDark"
            android:layout_gravity="center"
            android:gravity="center">
            ...
        </LinearLayout>
</com.cz.widgets.common.frame.FrameViewLayout>
```

Once you want to have your own frame container
Take a look at the interface: FrameContainer and the implementation: FrameViewLayout.

### Template style

```
//app/values/view_frame_style.xml

<!--    The error style-->
<attr name="frameErrorLayout" format="reference"/>
<attr name="frameErrorImage" format="reference"/>
<attr name="frameErrorText" format="reference"/>

<style name="FrameSmallStyle">
    <!--        The error layout-->
    <item name="frameErrorLayout">@style/FrameErrorLayoutStyle</item>
    <item name="frameErrorImage">@style/FrameErrorSmallImageStyle</item>
    <item name="frameErrorText">@style/FrameErrorSmallTextStyle</item>
</style>

<style name="FrameErrorLayoutStyle">
    <item name="android:layout_width">wrap_content</item>
    <item name="android:layout_height">wrap_content</item>
    <item name="android:layout_gravity">center</item>
    <item name="android:gravity">center</item>
    <item name="android:background">@drawable/view_accent_border_drawable</item>
    <item name="android:orientation">vertical</item>
</style>

//app/layout/view_frame_style.
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout android:id="@+id/frameError"
        xmlns:android="http://schemas.android.com/apk/res/android"
        android:layout_width="match_parent"
        android:layout_height="match_parent">

    <LinearLayout
            android:id="@+id/errorLayout"
            style="?frameErrorLayout">

        <ImageView
                android:id="@+id/errorImage"
                style="?frameErrorImage" />

        <TextView
                android:id="@+id/errorText"
                style="?frameErrorText" />

    </LinearLayout>
</FrameLayout>


//Finally:
val viewFrameWrapper1 = FrameWrapper(frameView1,R.style.FrameSmallStyle)
val viewFrameWrapper2 = FrameWrapper(frameView2,R.style.FrameFullScreenStyle)
```

### FrameTransition
We support frame transition. But here you only allow to use ViewPropertyAnimator.

```
public class DefaultFrameTransition extends FrameTransition{
    @Override
    public void appearingAnimator(ViewGroup parent, View child,int frameId) {
        child.setAlpha(0f);
        child.animate().alpha(1f);
    }
    @Override
    public void disappearingAnimator(ViewGroup parent, View child,int frameId) {
        child.animate().alpha(0f);
    }
}
```

We handle the animator outside as the same animator.

```
//We do something like this.
ViewPropertyAnimator currentAnimate = currentFrameView.animate();
currentAnimate.setStartDelay(delayTime);
currentFrameView.setVisibility(View.VISIBLE);
frameTransition.disappearingAnimator(hostView, currentFrameView, currentFrameId);
currentAnimate.start();
```


If you want keep one frame while the frame disappear. Such as the content view.

```
public class ContentFrameTransition extends DefaultFrameTransition {
    @Override
    public void appearingAnimator(ViewGroup parent, View child, int frameId) {
        if(FrameWrapper.FRAME_CONTAINER!=frameId){
            super.appearingAnimator(parent, child, frameId);
        }
    }
    @Override
    public void disappearingAnimator(ViewGroup parent, View child, int frameId) {
        if(FrameWrapper.FRAME_CONTAINER!=frameId){
            super.disappearingAnimator(parent, child, frameId);
        }
    }
    @Override
    public void onFrameDisappear(View child, int frameId) {
        //If the frame view is the content view. do nothing.
        if(FrameWrapper.FRAME_CONTAINER!=frameId){
            super.onFrameDisappear(child, frameId);
        }
    }
}
```

### Frame trigger

There are a lot of needs to manage the frame layout. The typical sample was Network change event.
For each list. if the network not work when we enter the page. After turn on the network. We should load the list data automatically.

```
if(isNetworkConnected(this)){
    ...
    frameWrapper.addFrameTrigger(RecyclerViewFrameTrigger(adapter))
} else {
    //Add network change frame trigger.
    val trigger= NetworkFrameTrigger(this,recyclerView)
    trigger.setCallback {
        recyclerView.layoutManager= LinearLayoutManager(this@FrameTriggerActivity)
        val adapter = SimpleAdapter(dataProvider.wordList.toList())
        recyclerView.adapter=adapter
        frameWrapper.setFrame(FrameWrapper.FRAME_CONTAINER)
        frameWrapper.addFrameTrigger(RecyclerViewFrameTrigger(adapter))
    }
    frameWrapper.addFrameTrigger(trigger)
}
```

After the network turn on. We will load the list data automatically.