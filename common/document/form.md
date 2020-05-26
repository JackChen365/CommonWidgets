## DragLayout

### Picture

* ![Form](https://github.com/momodae/LibraryResources/blob/master/CommonWidgets/image/view_form.gif?raw=true)

### Style prototype

```
<attr name="editLayout" format="reference"/>
<declare-styleable name="EditLayout">
    //The edit layout resource id.
    <attr name="edit_layout" format="reference"/>

    //The hint drawable resource id.
    <attr name="edit_hintDrawable" format="reference"/>

    //The hint text resource id.
    <attr name="edit_hint" format="string"/>

    //The text will put in the editor.
    <attr name="edit_text" format="string"/>

    //The hint text color.
    <attr name="edit_hintTextColor" format="color"/>

    //The text color.
    <attr name="edit_textColor" format="color"/>

    //The text size.
    <attr name="edit_textSize" format="dimension"/>

    //The error text color.
    <attr name="edit_errorTextColor" format="color"/>

    //The delete drawable resource id.
    <attr name="edit_deleteDrawable" format="reference"/>

    //The padding of the editor.
    <attr name="edit_padding" format="dimension" />

    //The error hint information.
    <attr name="edit_errorText" format="string"/>

    //The empty hint information
    <attr name="edit_emptyErrorText" format="string"/>

    //The background of the editor. Not for the layout.
    <attr name="edit_background" format="reference"/>

    <attr name="edit_patternValidator" format="string"/>

    //The text range of the validator. Not the limitation of the editor.
    <attr name="edit_patterMinTextLength" format="integer"/>
    <attr name="edit_patterMaxTextLength" format="integer"/>

    //The pre-defined validator.
    <attr name="edit_validator" format="enum">
        <enum name="any" value="0x00"/>
        <enum name="number" value="0x01"/>
        <enum name="character" value="0x02"/>
        <enum name="email" value="0x04"/>
        <enum name="phone" value="0x08"/>
        <enum name="web_url" value="0x10"/>
        <enum name="id" value="0x20"/>
    </attr>

    //The text input type.
    <attr name="edit_inputType">
        <enum name="number" value="0x01"/>
        <enum name="text" value="0x02"/>
        <enum name="number_password" value="0x03"/>
        <enum name="text_password" value="0x04"/>
    </attr>

    //The text length limitation.
    <attr name="edit_maxLength" format="integer"/>

    //The text line limitation.
    <attr name="edit_maxLine" format="integer"/>
</declare-styleable>
```
For an editor widget in the form. We do not have to consider the ellipsize end


### Usage

* Use the EditLayout in XML file.
```
<com.cz.widgets.common.form.layout.EditLayout
        android:id="@+id/editLayout1"
        android:layout_width="match_parent"
        android:layout_height="60dp"
        android:paddingLeft="12dp"
        android:paddingRight="12dp"
        //Here we could change the editor(The defualt one is EditText)
        //But we could use TextInputLayout too.
        app:edit_layout="@layout/view_input_edit_view"

        app:edit_textColor="@android:color/black"
        app:edit_textSize="16sp"
        app:edit_hintTextColor="@android:color/darker_gray"
        app:edit_errorTextColor="@color/colorAccent"
        app:edit_hint="@string/view_input_hint1"
        app:edit_emptyErrorText="@string/view_input_empty_info"
        app:edit_errorText="@string/view_input_not_in_range"
        app:edit_validator="phone"
        app:edit_maxLength="11"
        app:edit_patterMinTextLength="11"
        app:edit_inputType="number"
        app:edit_background="@drawable/view_edit_background"
        app:edit_deleteDrawable="@mipmap/view_login_closed"
        android:background="@color/md_grey_200"/>
```

* Customize the layout

```
<com.cz.widgets.common.form.layout.EditLayout
        android:id="@+id/editLayout2"
        android:layout_width="match_parent"
        android:layout_height="60dp"
        android:paddingLeft="12dp"
        android:paddingRight="12dp"
        app:edit_textColor="@android:color/black"
        app:edit_hintTextColor="@android:color/darker_gray"
        app:edit_errorTextColor="@color/colorAccent"
        app:edit_textSize="16sp"
        app:edit_hint="@string/view_input_hint2"
        app:edit_emptyErrorText="@string/view_input_empty_info"
        app:edit_errorText="@string/view_input_sms_error"
        app:edit_inputType="number"
        app:edit_patterMinTextLength="6"
        app:edit_maxLength="6"
        app:edit_patternValidator="\\d+"
        app:edit_background="@drawable/view_edit_background"
        app:edit_deleteDrawable="@mipmap/view_login_closed"
        android:background="@color/md_grey_200">

        //You are able to add child views inside the layout.
        <TextView
            android:id="@+id/smsCodeText"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textSize="14sp"
            android:text="Receive code"
            android:textColor="#FF0082FF"/>

    </com.cz.widgets.common.form.layout.EditLayout>
```

### Cooperate with the ValidatorObservable

```
//Initialize the subscribe
val subscribe = ValidatorObservable.create(editLayout1,editLayout2,editLayout3)
                .subscribe { editLayout, changed -> messageText.append("ValidatorObserver:$changed\n") }

//Verity the form.
checkButton.setOnClickListener {
    if (!subscribe.isValid()) {
        //Everytime when you verity the form by calling the method.
        //I will update the error message if is not available.
        messageText.append(subscribe.getErrorMessage() + "\n")
    } else {
        messageText.append("The form is available!\n")
    }
}

```


### Implement your own editor

```
public class MyTextInputLayout extends TextInputLayout implements IEditText {
    ...

    public MyTextInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //The text input layout don't have an EditText view. We add our custom edit text here.
        inflate(context, R.layout.view_default_edit_layout_view,this);
    }

    @Override
    public void setOnFocusChangeListener(View.OnFocusChangeListener l) {
        super.setOnFocusChangeListener(l);
        EditText editText = getEditText();
        editText.setOnFocusChangeListener(l);
    }

    @Override
    public void addTextChangedListener(TextWatcher watcher) {
        EditText editText = getEditText();
        editText.addTextChangedListener(watcher);
    }

    @Override
    public void removeTextChangedListener(TextWatcher watcher) {
        EditText editText = getEditText();
        editText.removeTextChangedListener(watcher);
    }

    @Override
    public void setTextColor(int color) {
        EditText editText = getEditText();
        editText.setTextColor(color);
    }

    @Override
    public Editable getText() {
        EditText editText = getEditText();
        return editText.getText();
    }

    @Override
    public void setText(CharSequence text) {
        EditText editText = getEditText();
        editText.setText(text);
    }

    @Override
    public void setSelection(int selection) {
        EditText editText = getEditText();
        editText.setSelection(selection);
    }

    @Override
    public void setHintTextColor(int color) {
        EditText editText = getEditText();
        editText.setHintTextColor(color);
    }

    @Override
    public void setTextSize(int type, float textSize) {
        EditText editText = getEditText();
        editText.setTextSize(type,textSize);
    }

    @Override
    public void setInputType(int inputType) {
        EditText editText = getEditText();
        editText.setInputType(inputType);
    }

    @Override
    public void setTransformationMethod(TransformationMethod method) {
        EditText editText = getEditText();
        editText.setTransformationMethod(method);
    }

    @Override
    public TransformationMethod getTransformationMethod() {
        EditText editText = getEditText();
        return editText.getTransformationMethod();
    }

    @Override
    public void setFilters(InputFilter[] filters) {
        EditText editText = getEditText();
        editText.setFilters(filters);
    }

    @Override
    public void setMaxLines(int maxLines) {
        EditText editText = getEditText();
        editText.setMaxLines(maxLines);
    }
}

```

* Write your widget in the XML

```
//view_input_edit_view.xml

<com.cz.widgets.sample.view.form.MyTextInputLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/input_editor"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:textCursorDrawable="@null"
    android:layout_gravity="center_vertical"
    android:gravity="left|center_vertical"
    android:background="@android:color/transparent"
    android:inputType="text"
    android:lines="1"/>

//Use the customize layout.
<com.cz.widgets.common.form.layout.EditLayout
    android:id="@+id/editLayout1"
    android:layout_width="match_parent"
    android:layout_height="60dp"
    android:paddingLeft="12dp"
    android:paddingRight="12dp"

    //Here we could change the editor(The defualt one is EditText)
    //But we could use TextInputLayout too.
    app:edit_layout="@layout/view_input_edit_view"/>
```