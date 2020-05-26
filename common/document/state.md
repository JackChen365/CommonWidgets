## StateView

### Picture

* ![](https://github.com/momodae/LibraryResources/blob/master/CommonWidgets/image/view_state_image.gif?raw=true)

### Styles

```
<attr name="state1" format="boolean"/>
<attr name="state2" format="boolean"/>
<attr name="state3" format="boolean"/>
<attr name="state4" format="boolean"/>
<attr name="state5" format="boolean"/>
<attr name="state6" format="boolean"/>
<attr name="state7" format="boolean"/>
<attr name="state8" format="boolean"/>
<attr name="state9" format="boolean"/>

<declare-styleable name="StateTextView">
    <attr name="stateEnabled" format="enum">
        <enum name="state1" value="0x01"/>
        <enum name="state2" value="0x02"/>
        <enum name="state3" value="0x03"/>
        <enum name="state4" value="0x04"/>
        <enum name="state5" value="0x05"/>
        <enum name="state6" value="0x06"/>
        <enum name="state7" value="0x07"/>
        <enum name="state8" value="0x08"/>
        <enum name="state9" value="0x09"/>
    </attr>
</declare-styleable>

<attr name="stateImageView" format="reference"/>
<declare-styleable name="StateImageView">
    <attr name="image_text" format="string"/>
    <attr name="image_textColor" format="color"/>
    <attr name="image_textSize" format="dimension"/>
    <attr name="image_textAppearance" format="reference"/>
    <attr name="image_stateEnabled" format="enum">
        <enum name="state1" value="0x01"/>
        <enum name="state2" value="0x02"/>
        <enum name="state3" value="0x03"/>
        <enum name="state4" value="0x04"/>
        <enum name="state5" value="0x05"/>
        <enum name="state6" value="0x06"/>
        <enum name="state7" value="0x07"/>
        <enum name="state8" value="0x08"/>
        <enum name="state9" value="0x09"/>
    </attr>
</declare-styleable>
```
### Usage

```
//The ColorStateList
//app/color/view_custom_text_state_selector.xml
<selector xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:auto="http://schemas.android.com/apk/res-auto">
    <item android:color="@color/md_white_1000" android:state_pressed="true"/>
    <item android:color="@color/md_orange_900" auto:state1="true"/>
    <item android:color="@color/md_red_900" auto:state2="true"/>
    <item android:color="@color/md_blue_900" auto:state3="true"/>
    <item android:color="@color/md_brown_900" auto:state4="true"/>
    <item android:color="@color/md_green_900" auto:state5="true"/>
    <item android:color="@color/md_yellow_900" auto:state6="true"/>
    <item android:color="@color/md_grey_900" auto:state7="true"/>
    <item android:color="@color/md_pink_900" auto:state8="true"/>
    <item android:color="@color/colorPrimaryDark" auto:state9="true"/>
</selector>

//The StateListDrawabble
//app/drawable/view_custom_state_selector.xml
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">
    <item android:drawable="@color/md_grey_900" android:state_pressed="true"/>
    <item android:drawable="@color/md_green_900" app:state1="true"/>
    <item android:drawable="@color/md_yellow_900" app:state2="true"/>
    <item android:drawable="@color/md_pink_900" app:state3="true"/>
    <item android:drawable="@color/md_red_900" app:state4="true"/>
    <item android:drawable="@color/colorPrimaryDark" app:state5="true"/>
    <item android:drawable="@color/md_purple_900" app:state6="true"/>
    <item android:drawable="@color/md_blue_900" app:state7="true"/>
    <item android:drawable="@color/md_orange_900" app:state8="true"/>
    <item android:drawable="@color/md_teal_900" app:state9="true"/>
</selector>

```

We use all the widgets like the TextView or ImageView

```
<com.cz.widgets.common.state.StateTextView
    android:id="@+id/stateTextView1"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_gravity="center_horizontal"
    android:padding="12dp"
    android:gravity="center"
    android:textSize="20dp"
    android:text="Hello world!"
    app:stateEnabled="state1"
    android:textColor="@color/view_custom_text_state_selector"
    android:background="@drawable/view_custom_state_selector"/>
```


That's it. You may curious about why We should have the extras state.
For the basic view. We have a few pre-defined states

```
 static final int[] VIEW_STATE_IDS = new int[] {
        R.attr.state_window_focused,    VIEW_STATE_WINDOW_FOCUSED,
        R.attr.state_selected,          VIEW_STATE_SELECTED,
        R.attr.state_focused,           VIEW_STATE_FOCUSED,
        R.attr.state_enabled,           VIEW_STATE_ENABLED,
        R.attr.state_pressed,           VIEW_STATE_PRESSED,
        R.attr.state_activated,         VIEW_STATE_ACTIVATED,
        R.attr.state_accelerated,       VIEW_STATE_ACCELERATED,
        R.attr.state_hovered,           VIEW_STATE_HOVERED,
        R.attr.state_drag_can_accept,   VIEW_STATE_DRAG_CAN_ACCEPT,
        R.attr.state_drag_hovered,      VIEW_STATE_DRAG_HOVERED
};
```

For organized drawing layer. You could use LevelDrawable.
But here we are not talking about one of them. We want more drawing states for the View.