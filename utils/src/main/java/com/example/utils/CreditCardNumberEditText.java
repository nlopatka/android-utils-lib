package com.example.utils;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

public class CreditCardNumberEditText extends EditText {
    public static final int DIGIT_GROUP_COUNT = 4;
    public static final int MAX_LENGTH = 19;
    private boolean mIsModified = true;
    private TextWatcher mTextWatcher = new CardTextWatcher();
    private String mLast4Digits;

    public CreditCardNumberEditText(Context context) {
        super(context);
        init();
    }

    public CreditCardNumberEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CreditCardNumberEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        InputFilter[] inputFilters = new InputFilter[1];
        inputFilters[0] = new InputFilter.LengthFilter(MAX_LENGTH);
        setFilters(inputFilters);
        setInputType(InputType.TYPE_CLASS_PHONE);
        addTextChangedListener(mTextWatcher);
        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus && !isModified()) {
                    setText("");
                }
                if(!hasFocus && !isModified()) {
                    setLast4Digits(mLast4Digits);
                }
            }
        });
    }

    public String getCardNumber() {
        if(isModified()) {
            return super.getText().toString().replace(" ", "");
        } else {
            return "";
        }
    }

    public void setLast4Digits(String last4Digits) {
        if(last4Digits == null || last4Digits.equals("")) {
            return;
        }
        mLast4Digits = last4Digits;
        mIsModified = false;
        removeTextChangedListener(mTextWatcher);
        setText("**** **** **** " + last4Digits);
        addTextChangedListener(mTextWatcher);
    }

    public boolean isModified() {
        return mIsModified;
    }

    private class CardTextWatcher implements TextWatcher {
        private int mPrevLength = 0;
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            int digitInGroup = 0;
            int i = 0;
            //Log.e("TMP before", editable.toString());
            while(i < editable.length()) {
                char c = editable.charAt(i);
                if(digitInGroup == DIGIT_GROUP_COUNT) {
                    digitInGroup=0;
                    if (c!=' ') {
                        editable.insert(i, " ");
                    } else {
                        //if deleting remove last space
                        if(mPrevLength>editable.length()
                                && i == editable.length()-1) {
                            editable.delete(i, i+1);
                        }
                    }
                } else {
                    if (Character.isDigit(c)) {
                        digitInGroup++;
                    } else {
                        editable.delete(i, i+1);
                        continue;
                    }
                }
                i++;
            }
            if(digitInGroup == DIGIT_GROUP_COUNT && mPrevLength < editable.length()
                    && editable.length() < MAX_LENGTH) {
                //if inserting insert last space
                editable.insert(i, " ");
            }
            //Log.e("TMP after ", editable.toString());
            if(editable.length() > 0) {
                mIsModified = true;
            }
            mPrevLength = editable.length();
        }
    }
}
