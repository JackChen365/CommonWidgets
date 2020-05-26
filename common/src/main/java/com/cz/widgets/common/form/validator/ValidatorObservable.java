package com.cz.widgets.common.form.validator;

import android.animation.AnimatorSet;
import android.text.TextWatcher;


import com.cz.widgets.common.form.layout.EditLayout;
import com.cz.widgets.common.form.layout.IEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;


/**
 * Created by cz
 * @date 2016/9/23
 * @email bingo110@126.com
 *
 * This is an abstract validator manager object.
 * This is an example for the object
 * <pre>
 * final ValidatorObserver subscribe = ValidatorObserver.create(layout1, layout2).
 *                 addValidatorCondition(this).
 *                 addEditor(layout3).
 *                 subscribe(new ValidatorObserver.ValidatorAction<EditLayout>() {
 *             @Override
 *             public void onChanged(EditLayout editText, boolean changed) {
 *                 textView.append("ValidatorObserver:"+changed+"\n");
 *             }
 *         });
 * </pre>
 *
 * It combine all the EditLayout together when the condition changed. You will receive the changed object.
 *
 * Here are the mainly functions.
 * 1. {@link ValidatorObservable#isValid()} Check if all the condition is valid manually
 * 2. {@link ValidatorObservable#subscribe(ValidatorAction)} Receive the event when there are one object changed.
 *
 * @see EditLayout usually cooperate with this class.
 */
public class ValidatorObservable<V extends ValidatorCondition,E extends ValidatorCondition & Editable> {
    private final List<V> layouts;
    private final HashMap<ValidatorCondition,ValidatorTextWatcher> itemTextWatchers;
    private String errorMessage;
    private ValidatorAction validatorAction;

    private ValidatorObservable(E[] layouts) {
        this.layouts = new ArrayList<>();
        this.itemTextWatchers=new HashMap<>();
        if(null!=layouts){
            this.layouts.addAll((Collection<V>) Arrays.asList(layouts));
            for(int i=0;i<layouts.length;i++){
                E layout = layouts[i];
                IEditText editor = layout.getEditor();
                ValidatorTextWatcher textWatcher = new ValidatorTextWatcher(layout);
                itemTextWatchers.put(layout,textWatcher);
                editor.addTextChangedListener(textWatcher);
            }
        }
    }
    public static ValidatorObservable create(EditLayout... layouts){
        return new ValidatorObservable(layouts);
    }

    public ValidatorObservable addValidatorCondition(V condition){
        layouts.add(condition);
        return this;
    }

    public ValidatorObservable subscribe(ValidatorAction action){
        this.validatorAction=action;
        return this;
    }

    /**
     * Return a layout from the observer collection.
     * @param layout
     * @return
     */
    public ValidatorObservable addEditor(E layout){
        if(null!=layout){
            IEditText editor = layout.getEditor();
            ValidatorTextWatcher textWatcher = new ValidatorTextWatcher(layout);
            editor.addTextChangedListener(textWatcher);
            itemTextWatchers.put(layout, textWatcher);
            layouts.add((V) layout);
        }
        return this;
    }

    public ValidatorObservable removeEditor(E layout){
        if(null!=layout){
            layouts.remove(layout);
            ValidatorTextWatcher textWatcher = itemTextWatchers.remove(layout);
            if(null!=textWatcher){
                IEditText editor = layout.getEditor();
                editor.removeTextChangedListener(textWatcher);
            }
        }
        return this;
    }

    /**
     * Check if all the condition is valid manually
     * @return
     */
    public boolean isValid(){
        boolean result=true;
        for(int i=0;i<layouts.size();i++){
            V layout = layouts.get(i);
            if(!(result&=layout.isValid())){
                errorMessage=layout.getErrorText();
                break;
            }
        }
        return result;
    }

    /**
     * Return the error text.
     * @return
     */
    public String getErrorMessage(){
        return errorMessage;
    }

    public interface ValidatorAction<E extends ValidatorCondition & Editable>{
        void onChanged(E editLayout, boolean changed);
    }

    class ValidatorTextWatcher implements TextWatcher {
        private boolean isValid;
        private final E layout;

        public ValidatorTextWatcher(E layout) {
            this.layout = layout;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(isValid^layout.isValid()){
                isValid=!isValid;
                if(null!=validatorAction){
                    validatorAction.onChanged(layout,isValid);
                }
            }
        }

        @Override
        public void afterTextChanged(android.text.Editable editable) {
        }

    }
}
