package com.logic.nibble.myzoonline.events;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.logic.nibble.myzoonline.R;

public class OTPTextWatcher implements TextWatcher {

    private final EditText[] editText;
    private View view;

    public OTPTextWatcher(EditText[] editText, View view) {
        this.editText = editText;
        this.view = view;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String text = editable.toString();
        switch (view.getId()) {

            case R.id.otp_1:
                if (text.length() == 1)
                    editText[1].requestFocus();
                break;
            case R.id.otp_2:

                if (text.length() == 1)
                    editText[2].requestFocus();
                else if (text.length() == 0)
                    editText[0].requestFocus();
                break;
            case R.id.otp_3:
                if (text.length() == 1)
                    editText[3].requestFocus();
                else if (text.length() == 0)
                    editText[1].requestFocus();
                break;
            case R.id.otp_4:
                if (text.length() == 0)
                    editText[2].requestFocus();
                break;
        }
    }
}
