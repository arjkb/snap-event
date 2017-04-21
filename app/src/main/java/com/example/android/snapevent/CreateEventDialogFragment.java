package com.example.android.snapevent;

import android.support.v4.app.DialogFragment;

/**
 * Created by akbfedora on 4/21/17.
 */

public class CreateEventDialogFragment extends DialogFragment {
    public interface CreateEventDialogListener  {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }
}
