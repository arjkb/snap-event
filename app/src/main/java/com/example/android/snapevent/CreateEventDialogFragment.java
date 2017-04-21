package com.example.android.snapevent;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.DialogFragment;

/**
 * Created by akbfedora on 4/21/17.
 */

public class CreateEventDialogFragment extends DialogFragment {
    String dialogMessage = null;

    public interface CreateEventDialogListener  {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }
    CreateEventDialogListener mListener;

    public CreateEventDialogFragment()  {
        this.dialogMessage = "Hello, world!";
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (CreateEventDialogListener) activity;
        } catch (ClassCastException e)  {
            throw new ClassCastException(activity.toString()
                            + " must implement CreateEventDialogListener");
        }
    }
}
