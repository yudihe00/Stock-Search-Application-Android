package com.yudihe.hw9;

import android.content.Context;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

import java.util.HashMap;

/**
 * Created by heyudi on 11/19/17.
 */
/** Customizing AutoCompleteTextView to return Country Name
 *  corresponding to the selected item
 */
public class CustomAutoCompleteTextView extends android.support.v7.widget.AppCompatAutoCompleteTextView {
    public CustomAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

//    /** Returns the symbol name corresponding to the selected item */
//    @Override
//    protected CharSequence convertSelectionToString(Object selectedItem) {
//        /** Each item in the autocompetetextview suggestion list is a hashmap object */
//        String str=selectedItem.toString();
////        String[] tmp = str.split(" ");
////        str=tmp[0];
//        return str;
//    }

    @Override
    protected void replaceText(CharSequence text) {
        super.replaceText(text);
    }
}
